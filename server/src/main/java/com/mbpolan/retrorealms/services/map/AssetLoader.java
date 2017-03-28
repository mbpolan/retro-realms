package com.mbpolan.retrorealms.services.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbpolan.retrorealms.services.map.assets.TilesetAsset;
import com.mbpolan.retrorealms.services.beans.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Parser that loads tileset metadata in JSON format.
 *
 * @author mbpolan
 */
public class AssetLoader {

    private static final Logger LOG = LoggerFactory.getLogger(AssetLoader.class);

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    /**
     * Processes metadata associated with the tileset into standard data structures.
     *
     * @param in The stream to read JSON metata from.
     * @throws IOException If the metadata cannot be completely processed.
     */
    public TilesetMetadata loadTilesetMetadata(InputStream in) throws IOException {
        TilesetAsset asset = JSON_MAPPER.readValue(in, TilesetAsset.class);

        // create a look-up for tiles based on their ID numbers
        Map<Integer, Tile> tiles = asset.getTiles().stream()
                .map(a -> new Tile(a.getId(), Optional.ofNullable(a.getFrame())
                        .map(bb -> new Rectangle(bb.getX(), bb.getY(), bb.getX() + bb.getW(), bb.getY() + bb.getH()))
                        .orElse(null)))
                .collect(Collectors.toMap(Tile::getId, Function.identity()));

        LOG.info("Processed {} tiles in tileset '{}'", tiles.size(), asset.getName());
        return new TilesetMetadata(tiles);
    }
}
