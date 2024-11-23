package dev.bakulin.tictactoe.exception;

public abstract class GameplayError extends RuntimeException {
    public abstract String getError();
    public abstract String getDetails();
}
