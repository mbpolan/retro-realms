package com.mbpolan.retrorealms.services;

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

        this.mapSettings = new MapSettings(
                Integer.parseInt(map.get("width").toString()),
                Integer.parseInt(map.get("height").toString()),
                Integer.parseInt(map.get("tileSize").toString()),
                map.get("file").toString());

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
