package dev.bakulin.tictactoe.model;

import lombok.Getter;

@Getter
public enum GameStatus {
    INIT(true),    // just init session, no moves yet
    PLAYING(true), // running game
    FINISHED(false),// completed with final
    CLOSED(false)   // stop before final
    ;

    final boolean active;

    GameStatus(boolean active) {
        this.active = active;
    }
}
