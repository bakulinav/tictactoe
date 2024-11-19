package dev.bakulin.ticktacktoe.service;

import dev.bakulin.ticktacktoe.client.GameServerClient;
import dev.bakulin.ticktacktoe.client.model.GameResponse;
import dev.bakulin.ticktacktoe.client.model.GameState;
import dev.bakulin.ticktacktoe.client.model.MoveRequest;
import dev.bakulin.ticktacktoe.client.model.Side;
import dev.bakulin.ticktacktoe.engine.TicTacToe;
import lombok.extern.slf4j.Slf4j;

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

    public String initGameplay(Side side) {
        GameResponse gameResponse = gameServerClient.initGameAs(side);

        return gameResponse.getSession();
    }

    public boolean makeMove(String s) {
        boolean complete = false;

        log.info("Check for session: {}", s);
        GameResponse serverState = gameServerClient.getState(s);

        if(isGameFinished(serverState)) {
            log.info("Game session {} is inactive status:{}", s, serverState.getState().getStatus());
            complete = true;
        } else {
            Side me = serverState.getSides().getGuest();

            if (me.equals(serverState.getState().getNext())) {
                Integer place = findMove(serverState.getState());
                log.info("Make a move at {} for session: {}", place, s);
                GameResponse afterMoveState = gameServerClient.makeMove(s, new MoveRequest().setMoveBy(me).setMoveTo(place));

                if (isGameFinished(afterMoveState)) {
                    complete = true;
                }
            } else {
                log.info("Pass for session: {}", s);
            }
        }

        return complete;
    }

    boolean isGameFinished(GameResponse game) {
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
