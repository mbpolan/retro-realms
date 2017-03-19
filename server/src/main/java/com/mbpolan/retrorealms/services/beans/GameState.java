package com.mbpolan.retrorealms.services.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * Bean that contains a snapshot of the state of the game.
 *
 * @author Mike Polan
 */
public class GameState {

    private Set<Player> players;

    public GameState() {
        this.players = new HashSet<>();
    }

    public GameState(Set<Player> players) {
        this.players = players;
    }

    public boolean isDirty() {
        return !players.isEmpty();
    }

    public void addChangedPlayer(Player player) {
        this.players.add(player);
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public GameState reset() {
        // create a copy of the changed players in this state
        Set<Player> copiedPlayers = new HashSet<>();
        copiedPlayers.addAll(players);

        // reset the players
        this.players.clear();

        // and return an identical copy of the previous state
        return new GameState(copiedPlayers);
    }
}
