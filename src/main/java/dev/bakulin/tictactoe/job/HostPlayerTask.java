package dev.bakulin.tictactoe.job;


import dev.bakulin.tictactoe.engine.TicTacToe;
import dev.bakulin.tictactoe.model.Actor;
import dev.bakulin.tictactoe.model.Game;
import dev.bakulin.tictactoe.model.GameState;
import dev.bakulin.tictactoe.model.GameStatus;
import dev.bakulin.tictactoe.repository.GameSessionsRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static dev.bakulin.tictactoe.engine.TicTacToe.FINAL_ROUND;

/**
 * Job to process active game sessions
 */
@Slf4j
@Component
@Profile("default")
public class HostPlayerTask {
    private final GameSessionsRepository repository;
    private final TicTacToe engine;
    private final Random brain;

    public HostPlayerTask(GameSessionsRepository repository, TicTacToe engine) {
        this.repository = repository;
        this.engine = engine;
        this.brain = new Random();
    }

    @PostConstruct
    void afterConstruct() {
        log.info("Host player is running");
    }

    @Scheduled(fixedDelayString = "10", initialDelay = 5L, timeUnit= TimeUnit.SECONDS)
    public void execute() {
        log.debug("Run host player task");
        this.process();
    }

    private void process() {
        repository.all()
                .stream()
                .filter(game -> game.getState().getStatus().isActive())
                .forEach(game -> takeTurn(game, game.getSides().getHost()));
    }

    void takeTurn(Game game, Actor asSide) {
        GameState state = game.getState();

        if (!state.getNext().equals(asSide)) {
            return;
        }

        makeMyMove(state, asSide);

        String field = engine.evalField(state.getField(), state.getCrossesMoves(), state.getZeroesMoves());
        state.setField(field);
        game.setUpdatedAt(ZonedDateTime.now());
        repository.save(game);
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

    // TODO deduplicate in GamePlayService
    private void recordAndSwitch(Actor moveBy, GameState state, String movesLog) {
        if (Actor.CROSS.equals(moveBy)) {
            state.setCrossesMoves(movesLog);
            state.setNext(Actor.ZERO);
        } else if (Actor.ZERO.equals(moveBy)) {
            state.setZeroesMoves(movesLog);
            state.setNext(Actor.CROSS);
        }
    }

    // TODO deduplicate in GamePlayService
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

}
