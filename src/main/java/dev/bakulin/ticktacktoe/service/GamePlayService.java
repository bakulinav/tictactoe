package dev.bakulin.ticktacktoe.service;

import dev.bakulin.ticktacktoe.dto.GameState;
import dev.bakulin.ticktacktoe.dto.MoveRequest;
import dev.bakulin.ticktacktoe.dto.MoveState;
import dev.bakulin.ticktacktoe.model.Game;
import dev.bakulin.ticktacktoe.model.GameSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class GamePlayService {

    private Map<String, Game> games = new HashMap<>();

    public Game init() {
        String session = UUID.randomUUID().toString();
        Game game = Game.init(session, GameState.init());
        save(game);

        return game;
    }

    private void save(Game game) {
        games.put(game.getSession(), game);
    }

    public MoveState acceptMove(String sessionId, MoveRequest moveRequest) {
        Game game = getGame(sessionId);

        // TODO implement

        return new MoveState().setAccepted(true);
    }

    public Game getGame(String sessionId) {
        return Optional.ofNullable(games.get(sessionId))
                .orElseThrow(() -> new RuntimeException("Game not found: " + sessionId));
    }

    public List<GameSession> list() {
        return games.values()
                .stream()
                .map(gp -> new GameSession(gp.getSession(), gp.getCreatedAt()))
                .sorted(Comparator.comparing(GameSession::getCreatedAt).reversed())
                .toList();
    }
}
