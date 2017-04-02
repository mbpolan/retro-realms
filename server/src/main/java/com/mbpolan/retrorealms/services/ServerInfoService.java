package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.beans.info.RectangleInfo;
import com.mbpolan.retrorealms.beans.info.TileMetadataInfo;
import com.mbpolan.retrorealms.services.beans.Rectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that provides metadata about the game and server.
 *
 * @author mbpolan
 */
@Service
public class ServerInfoService {

    @Autowired
    private MapService map;

    private String tilesetName;
    private String tilesetSource;
    private List<TileMetadataInfo> tiles;

    @PostConstruct
    public void init() {
        this.tilesetName = map.getTilesetMetadata().getName();
        this.tilesetSource = map.getTilesetMetadata().getImageSourcePath();

        // store the metadata for tiles in a form external clients will understand
        this.tiles = map.getTilesetMetadata().getTiles().stream()
                .map(t -> new TileMetadataInfo(t.getId(),
                        normalizeRectangle(t.getFrame()),
                        t.getBoundingBoxes().stream()
                                .map(ServerInfoService::normalizeRectangle)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public String getTilesetName() {
        return tilesetName;
    }

    public String getTilesetSource() {
        return tilesetSource;
    }

    public List<TileMetadataInfo> getTiles() {
        return tiles;
    }

    private static RectangleInfo normalizeRectangle(Rectangle r) {
        return new RectangleInfo(r.getX1(), r.getY1(), r.getWidth(), r.getHeight());
    }
}
