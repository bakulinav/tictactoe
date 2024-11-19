package dev.bakulin.ticktacktoe.client.model;

import lombok.Getter;

@Getter
public enum Status {
    INIT(true),    // just init session, no moves yet
    PLAYING(true), // running game
    FINISHED(false),// completed with final
    CLOSED(false)   // stop before final
    ;

    final boolean active;

    Status(boolean active) {
        this.active = active;
    }
}
