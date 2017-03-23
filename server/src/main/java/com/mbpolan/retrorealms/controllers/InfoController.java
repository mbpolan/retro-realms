package com.mbpolan.retrorealms.controllers;

import com.mbpolan.retrorealms.beans.info.ServerInfo;
import com.mbpolan.retrorealms.beans.info.SpritesMetadataInfo;
import com.mbpolan.retrorealms.beans.info.TilesetMetadataInfo;
import com.mbpolan.retrorealms.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    private ServerInfo getServerInfo() {
        return new ServerInfo(
                new TilesetMetadataInfo(settings.getTilesetPath(), settings.getTilesetResource()),
                new SpritesMetadataInfo(settings.getSpritesPath(), settings.getSpritesResource()));
    }
}
