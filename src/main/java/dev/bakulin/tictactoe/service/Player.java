package dev.bakulin.tictactoe.service;

import dev.bakulin.tictactoe.client.GameServerClient;
import dev.bakulin.tictactoe.dto.MoveRequest;
import dev.bakulin.tictactoe.engine.TicTacToe;
import dev.bakulin.tictactoe.model.Actor;
import dev.bakulin.tictactoe.model.Game;
import dev.bakulin.tictactoe.model.GameState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Random;

@Slf4j
public class Player {
    private final GameServerClient gameServerClient;
    private final TicTacToe engine;
    private final Random brain;

    public Player(GameServerClient gameServerClient, TicTacToe engine) {
        this.gameServerClient = gameServerClient;
        this.engine = engine;
        this.brain = new Random();
    }

    public String initGameplay(Actor side) {
        try {
            Game gameResponse = gameServerClient.initGameAs(side);
            return gameResponse.getSession();
        } catch (ResourceAccessException ex) {
            log.error("Fail to connect to game host", ex);
            throw new RuntimeException("Fail to connect to game host");
        }
    }

    public boolean takeTurn(String session) {
        boolean complete = false;

        log.info("Check for session: {}", session);
        Game serverState;
        try {
            serverState = gameServerClient.getState(session);
        } catch (ResourceAccessException ex) {
            log.error("Fail to connect to game host to", ex);
            throw new RuntimeException("Fail to connect to game host");
        } catch (HttpClientErrorException ex) {
            log.error("Fail get remote session state: {}", ex.getMessage());
            if (ex.contains(HttpClientErrorException.NotFound.class)) {
                log.warn("Game {} not found. Complete it.", session);
                return true;
            }
            throw new RuntimeException("Fail get state", ex);
        }

        if(isGameFinished(serverState)) {
            log.info("Game session {} has inactive status:{}", session, serverState.getState().getStatus());
            complete = true;
        } else {
            Actor me = serverState.getSides().getGuest();

            if (me.equals(serverState.getState().getNext())) {
                Integer place = findMove(serverState.getState());
                log.info("Make a move at {} for session: {}", place, session);

                Game afterMoveState = gameServerClient.makeMove(session, new MoveRequest().setMoveBy(me).setMoveTo(place));

                if (isGameFinished(afterMoveState)) {
                    complete = true;
                }
            } else {
                log.info("Pass for session: {}", session);
            }
        }

        return complete;
    }

    boolean isGameFinished(Game game) {
        return !game.getState().getStatus().isActive();
    }

    private int findMove(GameState gameState) {
        int guess;
        do {
            guess = brain.nextInt(0, 9);
        } while (engine.occupied(guess, gameState.getCrossesMoves())
                || engine.occupied(guess, gameState.getZeroesMoves()));
        return guess + 1; // index to place
    }
}
