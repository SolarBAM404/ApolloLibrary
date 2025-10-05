package me.solar.apolloLibrary.games.managers;

import java.util.ArrayList;
import java.util.List;

public class GamesPlayerManager {

    private GamesPlayerManager() {
        throw new IllegalStateException("Utility class");
    }

    private final List<GamesPlayer> gamesPlayers = new ArrayList<>();

}
