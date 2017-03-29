package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.services.beans.MapArea;
import com.mbpolan.retrorealms.services.map.AssetLoader;
import com.mbpolan.retrorealms.services.map.GameMap;
import com.mbpolan.retrorealms.services.map.Layer;
import com.mbpolan.retrorealms.services.map.TmxMapLoader;
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
import java.util.Collections;
import java.util.List;

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

    private MapArea area;
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

        // create the initial map area
        this.area = new MapArea(map.getLayers(), this.map.getWidth(),
                this.map.getHeight(), this.map.getTileSize());
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
     * Returns information about the tileset for the map.
     *
     * @return Settings information about the tileset.
     */
    public AssetSettings getTilesetSettings() {
        return this.map.getTilesetSettings();
    }
}
