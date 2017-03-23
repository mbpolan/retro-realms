package com.mbpolan.retrorealms.services;

import com.mbpolan.retrorealms.settings.AssetSettings;
import com.mbpolan.retrorealms.settings.GameSettings;
import com.mbpolan.retrorealms.settings.MapSettings;
import com.mbpolan.retrorealms.settings.PlayerSettings;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Service that manages various administrator-configurable settings.
 *
 * @author mbpolan
 */
@Service
public class SettingsService {

    private GameSettings gameSettings;
    private MapSettings mapSettings;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() throws FileNotFoundException {
        File mapSettingsFile = new File("./data/map.yml");
        if (!mapSettingsFile.exists()) {
            throw new IllegalStateException(String.format("Cannot find map settings file: %s",
                    mapSettingsFile.getAbsolutePath()));
        }

        // load the map settings file and create a bean from it
        Map<String, Object> mapSettingsRoot = (Map<String, Object>) new Yaml().load(new FileInputStream(mapSettingsFile));
        Map<String, Object> map = (Map<String, Object>) mapSettingsRoot.get("map");
        Map<String, Object> tileset = (Map<String, Object>) map.get("tileset");
        Map<String, Object> sprites = (Map<String, Object>) map.get("sprites");

        this.mapSettings = new MapSettings(
                Integer.parseInt(map.get("width").toString()),
                Integer.parseInt(map.get("height").toString()),
                Integer.parseInt(map.get("tileSize").toString()),
                map.get("file").toString(),
                new AssetSettings(tileset.get("path").toString(), tileset.get("resource").toString()),
                new AssetSettings(sprites.get("path").toString(), sprites.get("resource").toString()));

        File gameSettingsFile = new File("./data/server.yml");
        if (!gameSettingsFile.exists()) {
            throw new IllegalStateException(String.format("Cannot find game settings file: %s",
                    gameSettingsFile.getAbsolutePath()));
        }

        // load the game settings file and create a bean from it
        Map<String, Object> gameSettingsRoot = (Map<String, Object>) new Yaml().load(new FileInputStream(gameSettingsFile));
        Map<String, Object> game = (Map<String, Object>) gameSettingsRoot.get("game");
        Map<String, Object> players = (Map<String, Object>) game.get("players");

        this.gameSettings = new GameSettings(new PlayerSettings(
                Integer.parseInt(players.get("walkDelay").toString()),
                Integer.parseInt(players.get("speedMultiplier").toString())));
    }

    /**
     * Returns settings related to the game server itself.
     *
     * @return Game server settings.
     */
    public GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     * Returns settings related to the map and world.
     *
     * @return Map settings.
     */
    public MapSettings getMapSettings() {
        return mapSettings;
    }

    /**
     * Returns the path to the tileset metadata resource.
     *
     * @return The path to the tileset metadata, relative to the server root.
     */
    public String getTilesetPath() {
        return this.mapSettings.getTilesetSettings().getPath();
    }

    /**
     * Returns the path to the actual tileset resource.
     *
     * @return The path to the tileset resource, relative to the server root.
     */
    public String getTilesetResource() {
        return this.mapSettings.getTilesetSettings().getResource();
    }

    /**
     * Returns the path to the sprites metadata resource.
     *
     * @return The resource path.
     */
    public String getSpritesPath() {
        return this.mapSettings.getSpritesSettings().getPath();
    }

    /**
     * Returns the path to the actual sprites resource.
     *
     * @return The path to the sprites resource, relative to the server root.
     */
    public String getSpritesResource() {
        return this.mapSettings.getSpritesSettings().getResource();
    }

    /**
     * Returns the speed multiplier for player movement.
     *
     * @return The player speed multiplier.
     */
    public int getPlayerSpeedMultiplier() {
        return this.gameSettings.getPlayers().getSpeedMultiplier();
    }

    /**
     * Returns the delay between player walk movements.
     *
     * @return The player walk delay, in milliseconds.
     */
    public int getPlayerWalkDelay() {
        return this.gameSettings.getPlayers().getWalkDelay();
    }
}
