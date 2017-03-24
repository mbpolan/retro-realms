package com.mbpolan.retrorealms.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbpolan.retrorealms.services.assets.TileAsset;
import com.mbpolan.retrorealms.services.assets.TilesetAsset;
import com.mbpolan.retrorealms.services.beans.MapArea;
import com.mbpolan.retrorealms.services.beans.Tile;
import com.mbpolan.retrorealms.settings.MapSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service that manages various areas of the game map.
 *
 * @author mbpolan
 */
@Service
public class MapService {

    private static final Logger LOG = LoggerFactory.getLogger(MapService.class);

    @Autowired
    private SettingsService settings;

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private MapArea area;
    private Map<Integer, TileAsset> tileMetadata;

    @PostConstruct
    public void init() throws IOException {
        MapSettings mapSettings = settings.getMapSettings();

        // process tileset metadata before loading the map
        Path assetPath = Paths.get(".", "data", mapSettings.getTilesetSettings().getPath());
        if (Files.notExists(assetPath)) {
            throw new IllegalStateException(String.format("Cannot find tileset metadata: %s",
                    assetPath.toAbsolutePath()));
        }

        readTilesetMetadata(new FileInputStream(assetPath.toFile()));

        // make sure the map file exists
        Path mapPath = Paths.get(mapSettings.getFile());
        if (Files.notExists(mapPath)) {
            throw new IllegalStateException(String.format("Cannot find map file: %s",
                    mapPath.toAbsolutePath()));
        }

        // load the map data itself
        List<List<Tile>> tiles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(mapPath.toFile()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                List<Tile> row = Arrays.stream(line.split(","))
                        .map(s -> new Tile(Integer.parseInt(s.trim())))
                        .collect(Collectors.toList());

                tiles.add(row);
            }
        }

        // add the area to the map
        this.area = new MapArea(tiles, mapSettings.getWidth(), mapSettings.getHeight(), mapSettings.getTileSize());

        LOG.info("Loaded map of dimensions {}x{} tiles, with tile size {}",
                mapSettings.getWidth(), mapSettings.getHeight(), mapSettings.getTileSize());
    }

    /**
     * Returns descriptors for each area of the map.
     *
     * @return An immutable list of {@link MapArea} beans.
     */
    public List<MapArea> getMapAreas() {
        return Collections.singletonList(area);
    }

    /**
     * Returns a description of an area of the map.
     *
     * @param area The ID number of the area.
     * @return A {@link MapArea} bean.
     */
    public MapArea getMapArea(int area) {
        return this.area;
    }

    /**
     * Processes metadata associated with the tileset into internal data structures.
     *
     * @param in The stream to read JSON metata from.
     * @throws IOException If the metadata cannot be completely processed.
     */
    private void readTilesetMetadata(InputStream in) throws IOException {
        TilesetAsset asset = jsonMapper.readValue(in, TilesetAsset.class);

        // create a look-up for tiles based on their ID numbers
        this.tileMetadata = asset.getTiles().stream()
                .collect(Collectors.toMap(TileAsset::getId, Function.identity()));

        LOG.info("Processed {} tiles in tileset '{}'", tileMetadata.size(), asset.getName());
    }
}
