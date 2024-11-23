package dev.bakulin.tictactoe.exception;

public class GameInactiveError extends GameplayError {

    final String session;

    public GameInactiveError(String session) {
        this.session = session;
    }

    @Override
    public String getError() {
        return "Move rejected";
    }

    @Override
    public String getDetails() {
        return "Game " + session + " is completed. Move can't be applied";
    }
}
