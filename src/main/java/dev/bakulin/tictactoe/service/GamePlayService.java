package dev.bakulin.tictactoe.service;

import dev.bakulin.tictactoe.dto.GameInitRequest;
import dev.bakulin.tictactoe.dto.MoveRequest;
import dev.bakulin.tictactoe.engine.TicTacToe;
import dev.bakulin.tictactoe.exception.GameInactiveError;
import dev.bakulin.tictactoe.exception.UnexpectedMoveError;
import dev.bakulin.tictactoe.model.Actor;
import dev.bakulin.tictactoe.model.Game;
import dev.bakulin.tictactoe.model.GameSession;
import dev.bakulin.tictactoe.model.GameState;
import dev.bakulin.tictactoe.model.GameStatus;
import dev.bakulin.tictactoe.model.Sides;
import dev.bakulin.tictactoe.repository.GameSessionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.bakulin.tictactoe.engine.TicTacToe.FINAL_ROUND;

/**
 * Service to accept game actions from players
 */
@Slf4j
@Service
public class GamePlayService {

    private final GameSessionsRepository repository;
    private final TicTacToe engine;

    public GamePlayService(GameSessionsRepository repository, TicTacToe engine) {
        this.repository = repository;
        this.engine = engine;
    }

    public Game getGame(String sessionId) {
        return repository.get(sessionId);
    }

    public Game init(GameInitRequest request) {
        Actor guestSide = Optional.ofNullable(request)
                .map(GameInitRequest::getSide)
                .orElse(Actor.ZERO);

        Sides sides = Sides.initByGuest(guestSide);

        String session = UUID.randomUUID().toString();
        Game game = Game.init(session, new GameState(), sides);

        repository.save(game);

        return game;
    }

    public Game acceptMove(String sessionId, MoveRequest move) {
        Game game = repository.get(sessionId);

        GameState state = game.getState();
        if (!state.getStatus().isActive()) {
            throw new GameInactiveError("Game " + sessionId + " is completed. Move can't be applied");
        }

        if (!state.getNext().equals(move.getMoveBy())) {
            throw new UnexpectedMoveError(state.getNext());
        }

        if (state.getStatus().isActive()) {
            takeTurn(move, state);

            String field = engine.evalField(state.getField(), state.getCrossesMoves(), state.getZeroesMoves());
            state.setField(field);
            game.setUpdatedAt(ZonedDateTime.now());
            repository.save(game);
        } else {
            log.info("Skip move, game is over");
        }

        return game;
    }

    private void takeTurn(MoveRequest move, GameState state) {
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

    public List<GameSession> list() {
        return repository.all()
                .stream()
                .map(gp -> new GameSession(gp.getSession(), gp.getCreatedAt()))
                .sorted(Comparator.comparing(GameSession::getCreatedAt).reversed())
                .toList();
    }

    public String getGameText(String sessionId) {
        return Optional.ofNullable(repository.get(sessionId))
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
