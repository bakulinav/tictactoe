package dev.bakulin.ticktacktoe.service;

import dev.bakulin.ticktacktoe.dto.GameInitRequest;
import dev.bakulin.ticktacktoe.dto.MoveRequest;
import dev.bakulin.ticktacktoe.engine.TicTacToe;
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
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class GamePlayService {

    private static final int FINAL_ROUND = 9;
    private Map<String, Game> games = new HashMap<>();

    private final TicTacToe engine;
    private final Random brain;

    public GamePlayService(TicTacToe engine) {
        this.engine = engine;
        this.brain = new Random();
    }

    public Game init(GameInitRequest request) {
        Actor guestSide = Optional.ofNullable(request)
                .map(GameInitRequest::getSide)
                .orElse(Actor.ZERO);

        Sides sides = Sides.initByGuest(guestSide);

        String session = UUID.randomUUID().toString();
        Game game = Game.init(session, new GameState(), sides);

        makeMyMove(game.getState(), game.getSides().getHost());

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
            throw new GameInactiveError("Game " + sessionId + " is completed. Move can't be applied");
        }

        if (!state.getNext().equals(move.getMoveBy())) {
            // move check to engine
            throw new UnexpectedMoveError(state.getNext());
        }

        acceptMove(move, state);
        // TODO move makeMyMove to a separate job
        makeMyMove(state, game.getSides().getHost());

        String field = engine.evalField(state.getField(), state.getCrossesMoves(), state.getZeroesMoves());
        state.setField(field);
        game.setUpdatedAt(ZonedDateTime.now());
        save(game);

        return game;
    }

    private void makeMyMove(GameState gameState, Actor hostSide) {
        if (hostSide.equals(gameState.getNext())) {
            if (!gameState.getStatus().isActive()) {
                log.info("Skip move, game is over");
                return;
            }

            int place = findMove(gameState);
            log.info("Make host move by {} to {}", hostSide, place);
            String stateAfterMove = engine.applyMove(place, hostSide, gameState.getCrossesMoves(), gameState.getZeroesMoves());
            recordAndSwitch(hostSide, gameState, stateAfterMove);

            checkForComplete(hostSide, gameState, stateAfterMove);
        }
    }

    private int findMove(GameState gameState) {
        int guess;
        do {
            guess = brain.nextInt(0, 9);
        } while (engine.occupied(guess, gameState.getCrossesMoves())
                || engine.occupied(guess, gameState.getZeroesMoves()));
        return guess + 1; // index to places
    }

    private void acceptMove(MoveRequest move, GameState state) {
        if (!state.getStatus().isActive()) {
            log.info("Skip move, game is over");
            return;
        }

        log.info("Accept guest move by {} to {}", move.moveBy, move.moveTo);

        String stateAfterMove = engine.applyMove(move.moveTo, move.moveBy, state.getCrossesMoves(), state.getZeroesMoves());
        recordAndSwitch(move.moveBy, state, stateAfterMove);

        checkForComplete(move.moveBy, state, stateAfterMove);
    }

    private void checkForComplete(Actor moveMadeBy, GameState state, String stateAfterMove) {

        boolean winoCombo = engine.hasWin(stateAfterMove);
        if (winoCombo) {
            log.info("Winner is {}", moveMadeBy);
            state.setStatus(GameStatus.FINISHED);
            state.setWinner(moveMadeBy.toString());
        } else if (state.getRounds() == FINAL_ROUND) {
            state.setStatus(GameStatus.FINISHED);
            state.setWinner(TicTacToe.TIE);
        }
    }

    private void recordAndSwitch(Actor moveBy, GameState state, String movesLog) {
        if (Actor.CROSS.equals(moveBy)) {
            state.setCrossesMoves(movesLog);
            state.setNext(Actor.ZERO);
        } else if (Actor.ZERO.equals(moveBy)) {
            state.setZeroesMoves(movesLog);
            state.setNext(Actor.CROSS);
        }
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
