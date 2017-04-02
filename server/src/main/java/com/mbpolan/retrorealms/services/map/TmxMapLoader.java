package com.mbpolan.retrorealms.services.map;

import com.mbpolan.retrorealms.services.beans.Rectangle;
import com.mbpolan.retrorealms.settings.TilesetSettings;
import com.mbpolan.retrorealms.tmx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parser that loads a TMX map file and transforms it into standard data structures.
 *
 * @author mbpolan
 */
public class TmxMapLoader {

    private static final Logger LOG = LoggerFactory.getLogger(TmxMapLoader.class);

    private static final String MAP_AREA_PROP_REGEX = "^area_([0-9]+)$";
    private static final String MAP_AREA_VALUE_REGEX = "^([0-9]+),([0-9]+);([0-9]+),([0-9]+)$";

    private Path dataPath;

    /**
     * Creates a new loader for TMX map files.
     *
     * @param dataPath The path on the filesystem where server map data exists.
     */
    public TmxMapLoader(Path dataPath) {
        this.dataPath = dataPath;
    }

    /**
     * Loads data for a TMX map.
     *
     * @param in The input stream to read.
     * @return A processed {@link GameMap}.
     * @throws IOException If an error occurs while loading map data.
     */
    public GameMap load(InputStream in) throws IOException {
        try {
            JAXBContext ctx = JAXBContext.newInstance(com.mbpolan.retrorealms.tmx.Map.class);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();

            com.mbpolan.retrorealms.tmx.Map mapType = (com.mbpolan.retrorealms.tmx.Map) unmarshaller.unmarshal(in);
            LOG.debug("Successfully read TMX map data");

            // we only support square tiles
            if (mapType.getTileheight() != mapType.getTilewidth()) {
                throw new IOException(String.format("Only square tile sizes are supported (found: %dx%d", mapType.getTilewidth(), mapType.getTileheight()));
            }

            final int tileSize = mapType.getTilewidth();

            // parse the areas on the map
            List<Area> areas = parseAreas(mapType);

            // parse the tilesets associated with this map and extract the tileset metadata path
            TilesetMetadata metadata = parseTileset(mapType, tileSize);

            // process each layer of the map
            List<Layer> layers = parseLayers(mapType, metadata);

            // parse all doors on the map
            List<Door> doors = parseDoors(mapType, areas, tileSize);

            return GameMap.builder()
                    .areas(areas)
                    .doors(doors)
                    .height(mapType.getHeight())
                    .layers(layers)
                    .tileMetadata(metadata)
                    .tileSize(tileSize)
                    .width(mapType.getWidth())
                    .build();
        }

        catch (JAXBException ex) {
            LOG.error("Failed to load TMX map", ex);
            throw new IOException("Unable to load game map", ex);
        }
    }

    private static class TilesetData {

        TilesetSettings settings;
        TilesetMetadata metadata;

        TilesetData(TilesetSettings settings, TilesetMetadata metadata) {
            this.settings = settings;
            this.metadata = metadata;
        }
    }

    /**
     * Parses metadata that describes distinct areas of the map.
     *
     * @param mapType The TMX map to parse.
     * @return A list of {@link Area} subsections in the map.
     * @throws IOException If an error occurs while parsing.
     */
    private List<Area> parseAreas(com.mbpolan.retrorealms.tmx.Map mapType) throws IOException {
        final Pattern areaPattern = Pattern.compile(MAP_AREA_PROP_REGEX);
        final Pattern areaValuePattern = Pattern.compile(MAP_AREA_VALUE_REGEX);

        List<Area> areas = new ArrayList<>();

        PropertiesType props = mapType.getProperties();
        if (props != null) {
            for (PropertyType prop : props.getProperty()) {
                LOG.debug("Parsing map property with name {}", prop.getName());

                // property defining a map area
                Matcher areaMatcher = areaPattern.matcher(prop.getName());
                if (areaMatcher.find()) {
                    LOG.debug("Processing map area property");
                    int id = Integer.parseInt(areaMatcher.group(1));

                    // match the value of the property
                    Matcher valueMatcher = areaValuePattern.matcher(prop.getValue());
                    if (valueMatcher.find()) {
                        int x1 = Integer.parseInt(valueMatcher.group(1));
                        int y1 = Integer.parseInt(valueMatcher.group(2));
                        int x2 = Integer.parseInt(valueMatcher.group(3));
                        int y2 = Integer.parseInt(valueMatcher.group(4));

                        LOG.debug("Extracted area {} from ({},{} -> {},{})", id, x1, y1, x2, y2);
                        areas.add(new Area(id, new Rectangle(x1, y1, x2, y2)));
                    }

                    else {
                        throw new IOException(String.format("Map area value doesn't match pattern: %s", prop.getValue()));
                    }
                }

                else {
                    LOG.warn("Skipping unknown map property: {}", prop.getName());
                }
            }
        }

        LOG.info("Parsed {} map areas", areas.size());
        return areas;
    }

    /**
     * Parses the list of tilesets that are used on the map.
     *
     * @param mapType The TMX map to parse.
     * @param tileSize The square size of a tile.
     * @return A {@link TilesetData} describing tilesets in use.
     * @throws IOException If an error occurs while parsing.
     */
    private TilesetMetadata parseTileset(com.mbpolan.retrorealms.tmx.Map mapType, int tileSize) throws IOException {
        // parse and valiadate map tilesets
        TilesetType tileset = mapType.getTileset();

        // we only support tilesets with tiles that match those of the map
        if (mapType.getTileheight() != mapType.getTilewidth()) {
            throw new IOException(String.format("Only square tile sizes are supported (found: %dx%d", mapType.getTilewidth(), mapType.getTileheight()));
        }

        // parse and validate the tileset source image
        ImageType image = tileset.getImage();
        Path tilesetSource = dataPath.resolve(Paths.get(image.getSource()));

        // the image must be located under the data/assets directory, relative to the server
        // TODO

        // parse the tiles that belong to this tileset
        Map<Integer, Tile> tiles = parseTiles(tileset, tileSize);

        // make all paths relative to the data directory in the form of URL path components
        String relImagePath = String.format("/%s", dataPath.relativize(tilesetSource).toString().replace("\\", "/"));

        return new TilesetMetadata(tileset.getName(), relImagePath, tileset.getFirstgid(), tiles);
    }

    /**
     * Parses the collection of tiles that are defined for a tileset.
     *
     * @param tileset The tileset to parse.
     * @param tileSize The square size of a tile.
     * @return A map of tile ID numbers to their descriptors.
     */
    private Map<Integer, Tile> parseTiles(TilesetType tileset, int tileSize) {
        List<TileType> tileTypes = tileset.getTile();
        ListIterator<TileType> nextTile = tileTypes.listIterator();

        // start with the first tile ID in the tileset
        int gid = tileset.getFirstgid();

        Map<Integer, Tile> tiles = new HashMap<>();

        // iterate based on the count of tiles that should be in this tileset
        // reason being that there might not be metadata for every single tile
        for (int i = 0; i < tileset.getTilecount(); i++, gid++) {
            Tile tile;

            // compute the frame that encloses the tile on the base image
            int x = (i % tileset.getColumns()) * tileSize;
            int y = (i / tileset.getColumns()) * tileSize;
            Rectangle frame = new Rectangle(x, y, x + tileset.getTilewidth(), y + tileset.getTileheight());

            // does the local tile ID match the global tile ID?
            if (nextTile.hasNext() && tileTypes.get(nextTile.nextIndex()).getId() + 1 == gid) {
                TileType tileType = nextTile.next();

                // compute bounding boxes for the tile based on the metadata
                tile = new Tile(gid, frame, tileType.getObjectgroup().stream()
                        .flatMap(g -> g.getObject().stream())
                        .map(o -> new Rectangle(o.getX(), o.getY(), o.getX() + o.getWidth(), o.getY() + o.getHeight()))
                        .collect(Collectors.toList()));
            }

            // if not then generate a tile without any metadata information
            else {
                tile = new Tile(gid, frame);
            }

            tiles.put(gid, tile);
        }

        LOG.info("Parsed {} tiles in tileset {}", tiles.size(), tileset.getName());
        return tiles;
    }

    /**
     * Parses the various layers of the map.
     *
     * @param mapType The TMX map to parse.
     * @param tileMetadata Metadata for the tilesets used in the map.
     * @return A list of {@link Layer}s describing each map layer.
     * @throws IOException If an error occurs while parsing.
     */
    private List<Layer> parseLayers(com.mbpolan.retrorealms.tmx.Map mapType, TilesetMetadata tileMetadata) throws IOException {
        List<Layer> layers = new ArrayList<>();

        for (LayerType layer : mapType.getLayer()) {
            DataType data = layer.getData();

            // we only support CSV layer encoding
            if (data.getEncoding() != EncodingType.CSV) {
                throw new IOException(String.format("Unsupported data encoding type: %s", data.getEncoding()));
            }

            // parse the raw layer data
            List<List<Tile>> tiles = new ArrayList<>();
            for (String row : data.getValue().split("\n")) {
                if (!row.trim().isEmpty()) {
                    tiles.add(Arrays.stream(row.split(","))
                            .map(id -> tileMetadata.get(Integer.parseInt(id)))
                            .collect(Collectors.toList()));
                }
            }

            layers.add(new Layer(tiles));
        }

        LOG.info("Parsed {} layers", layers.size());
        return layers;
    }

    /**
     * Parses the collection of doors that are defined on the map.
     *
     * @param mapType The TMX map to parse.
     * @param areas The list of areas on the map.
     * @param tileSize The square size of a single tile, in pixels.
     * @return A list of {@link Door}s on the map.
     * @throws IOException If an error occurs while parsing.
     */
    private List<Door> parseDoors(com.mbpolan.retrorealms.tmx.Map mapType, List<Area> areas, int tileSize) throws IOException {
        List<Door> doors = new ArrayList<>();

        for (ObjectGroupType group : mapType.getObjectgroup()) {
            LOG.debug("Parsing object group with name {}", group.getName());

            // doors should be placed under a "doors" group
            if (group.getName().equals("doors")) {
                for (ObjectType obj : group.getObject()) {
                    // doors are expected to have properties defined
                    PropertiesType props = obj.getProperties();
                    if (props == null) {
                        throw new IOException(String.format("Invalid door at (%d,%d): no properties", obj.getX(), obj.getY()));
                    }

                    // define the pixel region and tile bounds of this door
                    // the origin needs to be adjusted from its bottom-left position to the top-left instead
                    int originY = obj.getY() - obj.getHeight();
                    Rectangle region = new Rectangle(obj.getX(), originY, obj.getX() + obj.getWidth(), originY + obj.getHeight());
                    Rectangle bounds = new Rectangle(
                            region.getX1() / tileSize,
                            region.getY1() / tileSize,
                            (region.getX2() / tileSize) - 1,
                            (region.getY2() / tileSize) - 1);

                    // required properties are "id", "toTileX" and "toTileY"
                    Integer id = null, toTileX = null, toTileY = null;
                    for (PropertyType prop : props.getProperty()) {
                        switch (prop.getName()) {
                            case "doorId":
                                id = Integer.parseInt(prop.getValue());
                                break;
                            case "toTileX":
                                toTileX = Integer.parseInt(prop.getValue());
                                break;
                            case "toTileY":
                                toTileY = Integer.parseInt(prop.getValue());
                                break;
                            default:
                                LOG.warn("Skipping unknown door property: {}", prop.getName());
                        }
                    }

                    if (id == null) {
                        throw new IOException(String.format("Invalid door at (%d,%d): missing 'doorId'", obj.getX(), obj.getY()));
                    }

                    if (toTileX == null) {
                        throw new IOException(String.format("Invalid door at (%d,%d): missing 'toTileX'", obj.getX(), obj.getY()));
                    }


                    if (toTileY == null) {
                        throw new IOException(String.format("Invalid door at (%d,%d): missing 'toTileY'", obj.getX(), obj.getY()));
                    }

                    // find the map areas where this door is placed and where leads to
                    Area source = null, target = null;
                    for (Area area : areas) {
                        if (area.getBounds().contains(bounds)) {
                            source = area;
                        }

                        if (area.getBounds().contains(toTileX, toTileY)) {
                            target = area;
                        }
                    }

                    // make sure this door actually leads somewhere meaningful
                    if (source == null) {
                        throw new IOException(String.format("Invalid door at (%d,%d): source is out of bounds", obj.getX(), obj.getY()));
                    }

                    if (target == null) {
                        throw new IOException(String.format("Invalid door at (%d,%d): target is out of bounds", obj.getX(), obj.getY()));
                    }

                    // transform the target tile coordinates to pixel coordinates relative to the origin of the target area
                    int dstX = (toTileX - target.getBounds().getX1()) * tileSize;
                    int dstY = (toTileY - target.getBounds().getY1()) * tileSize;

                    // transform the door's pixel region to be relative to the origin of the source area
                    Rectangle relBounds = region.relativeTo(source.getBounds().multiply(tileSize, tileSize));

                    LOG.debug("Extracted door in area {} to area {} ({},{})", source.getId(), target.getId(), dstX, dstY);

                    // if all's good, add the door to the collection and define its area
                    doors.add(new Door(id, source.getId(), target.getId(), dstX, dstY, relBounds));
                }
            }
        }

        LOG.info("Parsed {} doors", doors.size());
        return doors;
    }
}
