package dev.bakulin.ticktacktoe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameState {
    String status; // playing

    // active game
    String field;   // init: 123|456|789|, in-action: XX3|4O6|O89|, finish: XXX|4O6|O89|
    String crossesMoves; // 12300
    String zeroesMoves;  // 57000

    // last game status: null, crosses, zeroes, tie
    String lastGameWinner; // crosses
    String winCombo;       // 111000000
}
