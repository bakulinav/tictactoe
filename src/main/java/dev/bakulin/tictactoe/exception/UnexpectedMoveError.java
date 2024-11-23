package dev.bakulin.tictactoe.exception;

import dev.bakulin.tictactoe.model.Actor;

public class UnexpectedMoveError extends GameplayError {

    final Actor expectedFrom;

    public UnexpectedMoveError(Actor expectedFrom) {
        this.expectedFrom = expectedFrom;
    }

    @Override
    public String getError() {
        return "Move rejected";
    }

    @Override
    public String getDetails() {
        return "Next move expected by " + expectedFrom;
    }
}
