package dev.bakulin.ticktacktoe.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import static dev.bakulin.ticktacktoe.engine.TicTacToe.EMPTY_FIELD;
import static dev.bakulin.ticktacktoe.engine.TicTacToe.EMPTY_MOVES;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameState {

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.NONE)
    private int rounds = 0;

    GameStatus status = GameStatus.PLAYING;

    // active game
    String field = EMPTY_FIELD; // init: 123456789, in-action: XX34O6O89, finish: XXX4O6O89
    String crossesMoves = EMPTY_MOVES; // 12300
    String zeroesMoves = EMPTY_MOVES;  // 57000
    Actor next = Actor.CROSS; // crosses always first

    // last game status: null, crosses, zeroes, tie
    String winner = null; // crosses

    public void setCrossesMoves(String crossesMoves) {
        this.crossesMoves = crossesMoves;
        this.rounds++;
    }

    public void setZeroesMoves(String zeroesMoves) {
        this.zeroesMoves = zeroesMoves;
        this.rounds++;
    }
}
