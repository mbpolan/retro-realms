package com.mbpolan.retrorealms.services.beans;

import java.util.List;

/**
 * Bean that contains a snapshot of the state of the game.
 *
 * @author Mike Polan
 */
public class GameState {

    private List<Player> players;

    public GameState(List<Player> players) {
        this.players = players;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameState gameState = (GameState) o;

        return players.equals(gameState.players);

    }

    @Override
    public int hashCode() {
        return players.hashCode();
    }
}
