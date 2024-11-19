package dev.bakulin.ticktacktoe.client.model;

import lombok.Data;

@Data
public class GameState {
        Status status; // playing

        // active game
        String field;   // init: 123456789, in-action: XX34O6O89, finish: XXX4O6O89
        String crossesMoves; // 12300
        String zeroesMoves;  // 57000
        Side next;

        // last game status: null, crosses, zeroes, tie
        String lastGameWinner; // crosses
}
