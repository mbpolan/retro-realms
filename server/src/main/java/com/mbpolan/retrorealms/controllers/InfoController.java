package com.mbpolan.retrorealms.controllers;

import com.mbpolan.retrorealms.beans.info.ServerInfo;
import com.mbpolan.retrorealms.beans.info.SpritesMetadataInfo;
import com.mbpolan.retrorealms.beans.info.TilesetMetadataInfo;
import com.mbpolan.retrorealms.services.MapService;
import com.mbpolan.retrorealms.services.ServerInfoService;
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
    private MapService map;

    @Autowired
    private SettingsService settings;

    @Autowired
    private ServerInfoService infoService;

    private AssetSettings sprites;

    @PostConstruct
    public void init() {
        this.sprites = settings.getMapSettings().getSpritesSettings();
    }

    @GetMapping
    private ServerInfo getServerInfo() {
        return new ServerInfo(map.getTileSize(),
                new TilesetMetadataInfo(infoService.getTilesetName(), infoService.getTilesetSource(), infoService.getTiles()),
                new SpritesMetadataInfo(sprites.getName(), sprites.getPath(), sprites.getResource()));
    }
}
