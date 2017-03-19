package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.services.beans.MapArea;
import com.mbpolan.retrorealms.services.beans.Tile;
import com.mbpolan.retrorealms.settings.MapSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that manages various areas of the game map.
 *
 * @author Mike Polan
 */
@Service
public class MapService {

    private static final Logger LOG = LoggerFactory.getLogger(MapService.class);

    private MapArea area;

    @Autowired
    private SettingsService settings;

    @PostConstruct
    public void init() throws IOException {
        MapSettings mapSettings = settings.getMapSettings();

        // make sure the map file exists
        File mapFile = new File(mapSettings.getFile());
        if (!mapFile.exists()) {
            throw new IllegalStateException(String.format("Cannot find map file: %s",
                    mapFile.getAbsolutePath()));
        }

        // load the map data itself
        List<List<Tile>> tiles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(mapFile))) {
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
}
