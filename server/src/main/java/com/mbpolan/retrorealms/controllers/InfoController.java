package com.mbpolan.retrorealms.controllers;

import com.mbpolan.retrorealms.beans.info.ServerInfo;
import com.mbpolan.retrorealms.beans.info.SpritesMetadataInfo;
import com.mbpolan.retrorealms.beans.info.TilesetMetadataInfo;
import com.mbpolan.retrorealms.services.SettingsService;
import com.mbpolan.retrorealms.settings.AssetSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * Controller that provides server metadata.
 *
 * @author mbpolan
 */
@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private SettingsService settings;
    private AssetSettings tileset;
    private AssetSettings sprites;

    @PostConstruct
    public void init() {
        tileset = settings.getMapSettings().getTilesetSettings();
        sprites = settings.getMapSettings().getSpritesSettings();
    }

    @GetMapping
    private ServerInfo getServerInfo() {
        return new ServerInfo(
                new TilesetMetadataInfo(tileset.getName(), tileset.getPath(), tileset.getResource()),
                new SpritesMetadataInfo(sprites.getName(), sprites.getPath(), sprites.getResource()));
    }
}
