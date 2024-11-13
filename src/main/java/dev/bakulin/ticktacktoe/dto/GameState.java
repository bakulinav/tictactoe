package dev.bakulin.ticktacktoe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.bakulin.ticktacktoe.model.GameStatus;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameState {
    private static final String EMPTY_FIELD_STATE = "123456789"; // field 3x3
    private static final String EMPTY_MOVES = "00000"; // no any move

    GameStatus status; // playing

    // active game
    String field;   // init: 123456789, in-action: XX34O6O89, finish: XXX4O6O89
    String crossesMoves; // 12300
    String zeroesMoves;  // 57000

    // last game status: null, crosses, zeroes, tie
    String lastGameWinner; // crosses
    String winCombo;       // 111000000

    public static GameState init() {
        return new GameState()
                .setStatus(GameStatus.INIT)
                .setField(EMPTY_FIELD_STATE)
                .setCrossesMoves(EMPTY_MOVES)
                .setZeroesMoves(EMPTY_MOVES)
                .setLastGameWinner(null)
                .setWinCombo(null)
                ;
    }
}
