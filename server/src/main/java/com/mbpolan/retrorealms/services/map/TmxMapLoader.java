package com.mbpolan.retrorealms.services.map;

import com.mbpolan.retrorealms.services.ServiceUtils;
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
import java.util.stream.Collectors;

/**
 * Parser that loads a TMX map file and transforms it into standard data structures.
 *
 * @author mbpolan
 */
public class TmxMapLoader {

    private static final Logger LOG = LoggerFactory.getLogger(TmxMapLoader.class);

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

            // parse the tilesets associated with this map and extract the tileset metadata path
            TilesetData tilesetData = parseTileset(mapType);

            // process each layer of the map
            List<Layer> layers = parseLayers(mapType, tilesetData.metadata);

            return new GameMap(mapType.getWidth(), mapType.getHeight(), mapType.getTilewidth(),
                    tilesetData.settings, tilesetData.metadata, layers);
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

        LOG.debug("Parsed {} layers", layers.size());
        return layers;
    }
}
