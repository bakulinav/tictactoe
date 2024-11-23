package dev.bakulin.tictactoe.exception;

public class GameNotFoundError extends GameplayError {

    final String session;

    public GameNotFoundError(String session) {
        this.session = session;
    }

    @Override
    public String getError() {
        return "Game not found";
    }

    @Override
    public String getDetails() {
        return "Game " + session + " is not found";
    }
}
