package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.services.beans.MapArea;
import com.mbpolan.retrorealms.services.beans.Rectangle;
import com.mbpolan.retrorealms.services.map.*;
import com.mbpolan.retrorealms.settings.AssetSettings;
import com.mbpolan.retrorealms.settings.MapSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    private Map<Integer, MapArea> areas;
    private GameMap map;

    @PostConstruct
    public void init() throws IOException {
        MapSettings mapSettings = settings.getMapSettings();

        // make sure the map file exists
        Path mapPath = Paths.get(mapSettings.getFile());
        if (Files.notExists(mapPath)) {
            throw new IllegalStateException(String.format("Cannot find map file: %s",
                    mapPath.toAbsolutePath()));
        }

        // we only support maps saved in the TMX file format
        if (!ServiceUtils.getExtension(mapPath.toString()).equalsIgnoreCase("tmx")) {
            throw new IllegalStateException("Only TMX map file formats are supported");
        }

        // load the map data
        TmxMapLoader loader = new TmxMapLoader(new AssetLoader());
        this.map = loader.load(new FileInputStream(mapPath.toFile()));

        LOG.info("Successfully parsed map data");

        // generate the world based on the data we loaded from the map
        generateWorld();
    }

    /**
     * Returns descriptors for each area of the map.
     *
     * @return An immutable list of {@link MapArea} beans.
     */
    public Collection<MapArea> getMapAreas() {
        return this.areas.values();
    }

    /**
     * Returns a description of an area of the map.
     *
     * @param area The ID number of the area.
     * @return A {@link MapArea} bean.
     */
    public MapArea getMapArea(int area) {
        return this.areas.get(area);
    }

    /**
     * Returns information about the tileset for the map.
     *
     * @return Settings information about the tileset.
     */
    public AssetSettings getTilesetSettings() {
        return this.map.getTilesetSettings();
    }

    /**
     * Returns the square size of a tile.
     *
     * @return The size of a single tile, in pixels.
     */
    public int getTileSize() {
        return this.map.getTileSize();
    }

    /**
     * Generates the various map areas and other constructs for the game world.
     */
    private void generateWorld() {
        this.areas = new HashMap<>();

        LOG.info("Generating world...");
        long start = System.currentTimeMillis();

        // based on the areas that are defined, we need to partition the entire rectangle of tiles that make up the
        // map into individual areas
        map.getAreas().forEach(a -> {
            Rectangle bounds = a.getBounds();
            List<Layer> areaLayers = new ArrayList<>();

            // compute the tiles that belong to this map area
            map.getLayers().forEach(l -> {
                List<List<Tile>> tiles = new ArrayList<>();

                for (int y = 0; y < map.getHeight(); y++) {

                    // does this row fall into the area occupied by this area?
                    if (y >= bounds.getY1() && y <= bounds.getY2()) {
                        List<Tile> row = new ArrayList<>();

                        for (int x = 0; x < map.getWidth(); x++) {
                            // does this column fall into the area occupied by this area?
                            if (x >= bounds.getX1() && x <= bounds.getX2()) {
                                row.add(l.getTiles().get(y).get(x));
                            }
                        }

                        tiles.add(row);
                    }
                }

                areaLayers.add(new Layer(tiles));
            });

            // compute the dimensions of the area and add it to the world
            int areaWidth = bounds.getX2() - bounds.getX1() + 1;
            int areaHeight = bounds.getY2() - bounds.getY1() + 1;

            LOG.debug("Added map area {} over ({},{} -> {},{}) {}x{}",
                    a.getId(), bounds.getX1(), bounds.getY1(), bounds.getX2(), bounds.getY2(),
                    areaWidth, areaHeight);

            areas.put(a.getId(), new MapArea(areaLayers, areaWidth, areaHeight, map.getTileSize()));
        });

        LOG.info("World generated in {} ms", System.currentTimeMillis() - start);
    }
}
