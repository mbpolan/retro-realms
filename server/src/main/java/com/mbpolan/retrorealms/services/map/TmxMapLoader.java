package com.mbpolan.retrorealms.services.map;

import com.mbpolan.retrorealms.services.ServiceUtils;
import com.mbpolan.retrorealms.services.beans.Rectangle;
import com.mbpolan.retrorealms.settings.AssetSettings;
import com.mbpolan.retrorealms.tmx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private Path dataPath = Paths.get(".", "data");
    private AssetLoader assetLoader;

    /**
     * Creates a new loader for TMX map files.
     *
     * @param assetLoader A loader to use for reading tileset and sprite metadata.
     */
    public TmxMapLoader(AssetLoader assetLoader) {
        this.assetLoader = assetLoader;
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

            // parse the areas on the map
            List<Area> areas = parseAreas(mapType);

            // parse the tilesets associated with this map and extract the tileset metadata path
            TilesetData tilesetData = parseTileset(mapType);

            // process each layer of the map
            List<Layer> layers = parseLayers(mapType, tilesetData.metadata);

            // parse all doors on the map
            List<Door> doors = parseDoors(mapType);

            return GameMap.builder()
                    .areas(areas)
                    .doors(doors)
                    .height(mapType.getHeight())
                    .layers(layers)
                    .tileMetadata(tilesetData.metadata)
                    .tilesetSettings(tilesetData.settings)
                    .tileSize(mapType.getTilewidth())
                    .width(mapType.getWidth())
                    .build();
        }

        catch (JAXBException ex) {
            LOG.error("Failed to load TMX map", ex);
            throw new IOException("Unable to load game map", ex);
        }
    }

    private static class TilesetData {

        AssetSettings settings;
        TilesetMetadata metadata;

        public TilesetData(AssetSettings settings, TilesetMetadata metadata) {
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

                        LOG.debug("Extracted area {} from ({},{} -> {}, {})", id, x1, y1, x2, y2);
                        areas.add(new Area(id, new Rectangle(x1, y1, x2 - x1, y2 - y1)));
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
     * @return A {@link TilesetData} describing tilesets in use.
     * @throws IOException If an error occurs while parsing.
     */
    private TilesetData parseTileset(com.mbpolan.retrorealms.tmx.Map mapType) throws IOException {
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

        // get the basename of the tileset image and form the path to the resource file
        String baseName = ServiceUtils.getBasename(image.getSource());
        String resourcePath = String.format("%s.json", baseName);

        // the path to the tileset metadata must be in the same directory as the image
        Path tilesetMetadata = dataPath.resolve(Paths.get(resourcePath));
        if (!Files.exists(tilesetMetadata)) {
            throw new IOException(String.format("Cannot find tileset metadata: %s", tilesetMetadata.toAbsolutePath()));
        }

        // make all paths relative to the data directory in the form of URL path components
        String relImagePath = String.format("/%s", dataPath.relativize(tilesetSource).toString().replace("\\", "/"));
        String relResourcePath = String.format("/%s", dataPath.relativize(tilesetMetadata).toString().replace("\\", "/"));

        TilesetMetadata tilesMetadata = assetLoader.loadTilesetMetadata(new FileInputStream(tilesetMetadata.toFile()));
        return new TilesetData(new AssetSettings(tileset.getName(), relResourcePath, relImagePath), tilesMetadata);
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
     * @return A list of {@link Door}s on the map.
     * @throws IOException If an error occurs while parsing.
     */
    private List<Door> parseDoors(com.mbpolan.retrorealms.tmx.Map mapType) throws IOException {
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

                    // required properties are "id" and "connectsToId"
                    Integer id = null, connectsToId = null;
                    for (PropertyType prop : props.getProperty()) {
                        switch (prop.getName()) {
                            case "doorId":
                                id = Integer.parseInt(prop.getValue());
                                break;
                            case "connectsTo":
                                connectsToId = Integer.parseInt(prop.getValue());
                                break;
                            default:
                                LOG.warn("Skipping unknown door property: {}", prop.getName());
                        }
                    }

                    if (id == null) {
                        throw new IOException(String.format("Invalid door at (%d,%d): missing 'doorId'", obj.getX(), obj.getY()));
                    }

                    if (connectsToId == null) {
                        throw new IOException(String.format("Invalid door at (%d,%d): missing 'connectsTo'", obj.getX(), obj.getY()));
                    }

                    // if all's good, add the door to the collection and define its area
                    doors.add(new Door(id, connectsToId, new Rectangle(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight())));
                }
            }
        }

        LOG.info("Parsed {} doors", doors.size());
        return doors;
    }
}
