package dev.bakulin.ticktacktoe.model;

public enum GameStatus {
    INIT,    // just init session, no moves yet
    PLAYING, // running game
    FINISHED,// completed with final
    CLOSED   // stop before final
}
