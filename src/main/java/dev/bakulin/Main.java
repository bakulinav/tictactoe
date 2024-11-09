package dev.bakulin;

import java.io.PrintStream;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String fieldMask = "123456789";

        String[] winCombos = new String[]{
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

        // init
        String crossesMask = "000000000";
        String zeroesMask = "000000000";

        // gameplay
        String actor = crossesMask; // crosses begins
        String whoTurn = "crosses";
        for(int round = 1; round <= 9; round++) {
            System.out.println("Round " + round);
            renderState(fieldMask, crossesMask, zeroesMask);

            int move = generateExcept(crossesMask, zeroesMask);
            actor = afterMove(move, actor);
            if (hasWin(actor, winCombos)) {
                if ("crosses".equals(whoTurn)) { crossesMask = actor; } else { zeroesMask = actor;}
                renderState(fieldMask, crossesMask, zeroesMask);
                System.out.println(whoTurn + " wins in " + round + " round");
                break;
            }
            Thread.sleep(500);

            // switch turn
            if ("crosses".equals(whoTurn)) {crossesMask = actor; actor = zeroesMask; whoTurn = "zeroes";}
            else { zeroesMask = actor; actor = crossesMask; whoTurn = "crosses";}
        }

        renderState(fieldMask, crossesMask, zeroesMask);
    }

    //  определяет победителя по вариантам выигрышных комбинаций
    private static boolean hasWin(String log, String[] wins) {
        for (String combo : wins) {
            int comboBit = toBit(combo);
            int logBit = toBit(log);

            if ((logBit & comboBit) == comboBit) {
                return true;
            }
        }
        return false;
    }

    private static String afterMove(int occupy, String log) {
        char[] chars = log.toCharArray();
        chars[occupy] = '1';
        return String.valueOf(chars);
    }

    private static int toBit(String log) {
        return Integer.parseUnsignedInt(log, 2);
    }

    private static int generateExcept(String crosses, String zeroes) {
        Random random = new Random();
        int guess;
        do {
            guess = random.nextInt(0, 9);
        } while (occupied(guess, crosses) || occupied(guess, zeroes));
        return guess;
    }

    private static void renderLine() {
        System.out.println("-----");
    }

    private static void renderState(String field, String crosses, String zeroes) {
        PrintStream out = System.out;

        renderLine();
        for(int cell = 0, nl = 1; cell < field.length(); cell++, nl++) {
            if (occupied(cell, crosses)) {
                out.print('Х');
            } else if (occupied(cell, zeroes)) {
                out.print('O');
            } else {
                out.print(field.charAt(cell));
            }

            if (nl % 3 == 0) {
                out.print('\n');
            } else {
                out.print(' ');
            }
        }
        renderLine();
    }

    private static boolean occupied(int cell, String places) {
        return '1' == places.charAt(cell);
    }
}
