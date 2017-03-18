package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.services.beans.MapArea;
import com.mbpolan.retrorealms.services.beans.Tile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private MapArea area;

    @PostConstruct
    public void init() throws IOException {
        List<List<Tile>> tiles = new ArrayList<>();

        // load the map
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(GameService.class.getResourceAsStream("/map.csv")))) {
            String line;

            while ((line = reader.readLine()) != null) {
                List<Tile> row = Arrays.stream(line.split(","))
                        .map(s -> new Tile(Integer.parseInt(s.trim())))
                        .collect(Collectors.toList());

                tiles.add(row);
            }
        }

        // compute dimensions of the map
        int height = tiles.size();
        int width = height > 0 ? tiles.get(0).size() : 0;

        // add the area to the map
        this.area = new MapArea(tiles, width, height);
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
