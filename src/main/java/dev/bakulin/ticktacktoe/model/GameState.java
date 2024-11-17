package dev.bakulin.ticktacktoe.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import static dev.bakulin.ticktacktoe.engine.TicTacToeEngine.EMPTY_FIELD;
import static dev.bakulin.ticktacktoe.engine.TicTacToeEngine.EMPTY_MOVES;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameState {
    GameStatus status; // playing

    // active game
    String field;   // init: 123456789, in-action: XX34O6O89, finish: XXX4O6O89
    String crossesMoves; // 12300
    String zeroesMoves;  // 57000
    Actor next;

    // last game status: null, crosses, zeroes, tie
    String lastGameWinner; // crosses

    public static GameState init() {
        return new GameState()
                .setStatus(GameStatus.INIT)
                .setField(EMPTY_FIELD)
                .setCrossesMoves(EMPTY_MOVES)
                .setZeroesMoves(EMPTY_MOVES)
                .setNext(Actor.CROSS)
                .setLastGameWinner(null)
                ;
    }
}
