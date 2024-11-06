package dev.bakulin;

import java.io.PrintStream;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int[] field = new int[]{
                1, 2, 3,
                4, 5, 6,
                7, 8, 9
        };

        // TODO переделать в битовую маску
        int[][] wins = new int[][]{
                // rows
                new int[]{1, 2, 3},
                new int[]{4, 5, 6},
                new int[]{7, 8, 9},
                // cols
                new int[]{1, 4, 7},
                new int[]{2, 5, 8},
                new int[]{3, 6, 9},
                // crosses
                new int[]{1, 5, 9},
                new int[]{3, 5, 7},
        };

        // init
        int[] crosses = new int[5];
        int[] zeroes = new int[4];

        // gameplay
        int[] turnOf = crosses; // crosses begins
        for(int round = 1; round <= 9; round++) {
            System.out.println("Round " + round);
            renderLine();
            int move = generateExcept(crosses, zeroes);
            recordMove(move, turnOf);
            // TODO определять победителя по маскам выигрышных комбинаций
            renderState(field, crosses, zeroes);
            renderLine();
            Thread.sleep(500);

            // switch turn
            if (turnOf == crosses) turnOf = zeroes;
            else turnOf = crosses;
        }
    }

    private static void recordMove(int move, int[] log) {
        for (int pos = 0; pos < log.length; pos++) {
            if (log[pos] == 0) {
                log[pos] = move;
                return;
            }
        }
    }

    private static int generateExcept(int[] crosses, int[] zeroes) {
        Random random = new Random();
        int guess;
        do {
            guess = random.nextInt(1, 10);
        } while (occupied(guess, crosses) || occupied(guess, zeroes));
        return guess;
    }

    private static void renderLine() {
        System.out.println("-----");
    }

    private static void renderState(int[] field, int[] crosses, int[] zeroes) {
        PrintStream out = System.out;
        for(int cell : field) {
            if (occupied(cell, crosses)) {
                out.print('Х');
            } else if (occupied(cell, zeroes)) {
                out.print('O');
            } else {
                out.print(cell);
            }

            if (cell % 3 == 0) {
                out.print('\n');
            } else {
                out.print(' ');
            }
        }
    }

    private static boolean occupied(int cell, int[] places) {
        for (int place : places) {
            if (place == cell) {
                return true;
            }
        }

        return false;
    }
}
