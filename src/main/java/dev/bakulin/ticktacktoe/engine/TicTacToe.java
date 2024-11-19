package dev.bakulin.ticktacktoe.engine;

import dev.bakulin.ticktacktoe.model.Actor;
import org.springframework.stereotype.Service;

@Service
public class TicTacToe {

    public static final String EMPTY_FIELD = "123456789"; // field 3x3
    public static final String EMPTY_MOVES = "000000000"; // no any move
    public static final String TIE = "tie";

    private String[] winCombos = new String[]{
            // rows
            "111000000",
            "000111000",
            "000000111",
            // cols
            "100100100",
            "010010010",
            "001001001",
            // diagonals
            "100010001",
            "001010100",
    };

    public String applyMove(int place, Actor moveBy, String crosses, String zeroes) {
        int index = (place - 1);
        if (occupied(index, crosses) || occupied(index, zeroes)) {
            throw new RuntimeException("Place " + place + " already occupied");
        }

        if (Actor.CROSS.equals(moveBy)) {
            return afterMove(index, crosses);
        } else if (Actor.ZERO.equals(moveBy)) {
            return afterMove(index, zeroes);
        }

        throw new RuntimeException("Unknown actor: " + moveBy);
    }

    public String evalField(String field, String crosses, String zeroes) {
        StringBuilder out = new StringBuilder();
        for(int cell = 0, nl = 1; cell < field.length(); cell++, nl++) {
            if (occupied(cell, crosses)) {
                out.append('X');
            } else if (occupied(cell, zeroes)) {
                out.append('O');
            } else {
                out.append(field.charAt(cell));
            }
        }

        return out.toString();
    }

    //  определяет победителя по вариантам выигрышных комбинаций
    public boolean hasWin(String log) {
        for (String combo : winCombos) {
            int comboBit = toBit(combo);
            int logBit = toBit(log);

            if ((logBit & comboBit) == comboBit) {
                return true;
            }
        }
        return false;
    }

    private String afterMove(int occupy, String log) {
        char[] chars = log.toCharArray();
        chars[occupy] = '1';
        return String.valueOf(chars);
    }

    public boolean occupied(int cell, String places) {
        return '1' == places.charAt(cell);
    }

    private static int toBit(String log) {
        return Integer.parseUnsignedInt(log, 2);
    }
}
