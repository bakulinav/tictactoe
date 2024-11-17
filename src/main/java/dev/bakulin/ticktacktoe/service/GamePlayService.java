package dev.bakulin.ticktacktoe.service;

import dev.bakulin.ticktacktoe.dto.GameInitRequest;
import dev.bakulin.ticktacktoe.dto.MoveRequest;
import dev.bakulin.ticktacktoe.engine.TicTacToeEngine;
import dev.bakulin.ticktacktoe.exception.GameInactiveError;
import dev.bakulin.ticktacktoe.exception.UnexpectedMoveError;
import dev.bakulin.ticktacktoe.model.Actor;
import dev.bakulin.ticktacktoe.model.Game;
import dev.bakulin.ticktacktoe.model.GameSession;
import dev.bakulin.ticktacktoe.model.GameState;
import dev.bakulin.ticktacktoe.model.GameStatus;
import dev.bakulin.ticktacktoe.model.Sides;
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

    final TicTacToeEngine engine;

    public GamePlayService(TicTacToeEngine engine) {
        this.engine = engine;
    }

    public Game init(GameInitRequest request) {
        Actor guestSide = Optional.ofNullable(request)
                .map(GameInitRequest::getSide)
                .orElse(Actor.ZERO);

        Sides sides = Sides.initByGuest(guestSide);

        String session = UUID.randomUUID().toString();
        Game game = Game.init(session, GameState.init(), sides);
        save(game);

        return game;
    }

    private void save(Game game) {
        games.put(game.getSession(), game);
    }

    public Game acceptMove(String sessionId, MoveRequest move) {
        Game game = getGame(sessionId);

        // TODO check required move is valid

        GameState state = game.getState();
        if (!state.getStatus().isActive()) {
            // TODO intro own error model
            throw new GameInactiveError("Game " + sessionId + " is completed. Move can't be applied");
        }

        if (!state.getNext().equals(move.getMoveBy())) {
            // move check to engine
            throw new UnexpectedMoveError(state.getNext());
        }

        if (GameStatus.INIT.equals(state.getStatus())) {
            state.setStatus(GameStatus.PLAYING);
        }

        String stateAfterMove = engine.applyMove(move.moveTo, move.moveBy, state.getCrossesMoves(), state.getZeroesMoves());
        if (Actor.CROSS.equals(move.moveBy)) {
            state.setCrossesMoves(stateAfterMove);
            state.setNext(Actor.ZERO);
        }else if (Actor.ZERO.equals(move.moveBy)) {
            state.setZeroesMoves(stateAfterMove);
            state.setNext(Actor.CROSS);
        }

        boolean winoCombo = engine.hasWin(stateAfterMove);
        if (winoCombo) {
            state.setStatus(GameStatus.FINISHED);
            state.setLastGameWinner(move.moveBy.toString());
        }

        String field = engine.evalField(state.getField(), state.getCrossesMoves(), state.getZeroesMoves());
        state.setField(field);
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

    public String getGameText(String sessionId) {
        return Optional.ofNullable(games.get(sessionId))
                .map(this::renderField)
                .orElseThrow(() -> new RuntimeException("Game not found: " + sessionId));
    }

    private String renderField(Game game) {
        StringBuilder out = new StringBuilder();
        char[] field = game.getState().getField().toCharArray();
        for(int i = 0, cnt = 1; i < field.length; i++, cnt++) {
            out.append(field[i]);
            out.append(cnt % 3 == 0 ? '\n' : ' ');
        }
         return out.toString();
    }
}
