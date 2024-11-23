package dev.bakulin.tictactoe.repository;

import dev.bakulin.tictactoe.exception.GameNotFoundError;
import dev.bakulin.tictactoe.model.Game;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GameSessionsRepository {
    // TODO move to persistent storage
    private Map<String, Game> games = new HashMap<>();

    public void save(Game game) {
        games.put(game.getSession(), game);
    }

    public Game get(String sessionId) {
        return Optional.ofNullable(games.get(sessionId))
                .orElseThrow(() -> new GameNotFoundError(sessionId));
    }

    public Collection<Game> all() {
        return games.values();
    }
}
