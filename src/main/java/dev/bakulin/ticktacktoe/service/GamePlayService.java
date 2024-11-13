package dev.bakulin.ticktacktoe.service;

import dev.bakulin.ticktacktoe.dto.GameState;
import dev.bakulin.ticktacktoe.dto.MoveRequest;
import dev.bakulin.ticktacktoe.model.Actor;
import dev.bakulin.ticktacktoe.model.Game;
import dev.bakulin.ticktacktoe.model.GameSession;
import dev.bakulin.ticktacktoe.model.GameStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
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

    public Game acceptMove(String sessionId, MoveRequest moveRequest) {
        Game game = getGame(sessionId);

        // TODO check required move is valid

        GameState state = game.getState();
        if (!state.getStatus().isActive()) {
            // TODO intro own error model
            throw new RuntimeException("Game " + sessionId + " is completed. Move can't be applied");
        }

        if (GameStatus.INIT.equals(state.getStatus())) {
            state.setStatus(GameStatus.PLAYING);
        }

        // TODO: apply move

        if (Actor.CROSS.equals(moveRequest.moveBy)) {
            // write down moves
            StringBuilder stateAfterMove = new StringBuilder(state.getCrossesMoves());
            stateAfterMove.replace(0, 1, moveRequest.moveTo.toString());
            state.setCrossesMoves(stateAfterMove.toString());

            // update field state
            StringBuilder fieldAfterMove = new StringBuilder(state.getField());
            fieldAfterMove.replace(moveRequest.moveTo - 1, moveRequest.moveTo, "X");
            state.setField(fieldAfterMove.toString());
        }else if (Actor.ZERO.equals(moveRequest.moveBy)) {
            // write down moves
            StringBuilder stateAfterMove = new StringBuilder(state.getZeroesMoves());
            stateAfterMove.replace(0, 1, moveRequest.moveTo.toString());
            state.setZeroesMoves(stateAfterMove.toString());

            // update field state
            StringBuilder fieldAfterMove = new StringBuilder(state.getField());
            fieldAfterMove.replace(moveRequest.moveTo - 1, moveRequest.moveTo, "O");
            state.setField(fieldAfterMove.toString());
        }

        game.setUpdatedAt(ZonedDateTime.now());
        save(game);

        return game;
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
