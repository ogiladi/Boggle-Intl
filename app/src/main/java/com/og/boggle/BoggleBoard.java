package com.og.boggle;

// A class that created a random board of letter. The letters are chosen according to their
// frequency in the given language (see the BoardLanguage class).

import java.util.Set;
import java.util.TreeSet;

public class BoggleBoard {

    private final int boardSize = 4;
    private BoggleBox[][] board;
    private int numPressed;

    BoggleBoard(BoardLanguage.Language language) {
        numPressed = 0;
        BoardLanguage boardLanguage = new BoardLanguage(language);
        board = new BoggleBox[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                char letter = boardLanguage.getRandomLetter();
                board[i][j] = new BoggleBox(letter);
            }
        }
    }

    private class BoggleBox {
        private char letter;
        private boolean isPressed;

        BoggleBox(char letterInput) {
            letter = letterInput;
            isPressed = false;
        }

        String showLetter() {
            if (letter == 'q') return "Qu";
            return String.valueOf(letter).toUpperCase();
        }

        void changePressed() {
            isPressed = !isPressed;
        }

        boolean isPressed() {
            return isPressed;
        }

        char getLetter() {
            return letter;
        }
    }

    String showLetter(int i, int j) { return board[i][j].showLetter(); }

    boolean isPressed(int i, int j) { return board[i][j].isPressed(); }

//  Return a list of neighbouring boxes
    private Set<Integer> nbrs(int i, int j) {
        Set<Integer> nbrs = new TreeSet<>();
        for (int s = -1; s <= 1; s++) {
            for (int t = -1; t <= 1; t++) {
                int new_i = i+s;
                int new_j = j+t;
                if (new_i >= 0 && new_i < boardSize && new_j >= 0 && new_j < boardSize) {
                    if (s != 0 || t != 0) {
                        int newInd = boardSize * new_i + new_j;
                        nbrs.add(newInd);
                    }
                }
            }
        }
        return nbrs;
    }

//  Return a string created by a sequence of boxes in the board
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                result.append(board[i][j].showLetter()).append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

    char getLetter(int i, int j) {
        return board[i][j].getLetter();
    }

    boolean areNbrs(int n1, int n2) {
        int j1 = n1 % boardSize;
        int i1 = (n1-j1) / boardSize;
        return nbrs(i1, j1).contains(n2);
    }

    void boxPressed(int i, int j) {
        BoggleBox box = board[i][j];
        if (box.isPressed()) numPressed--;
        else numPressed++;
        box.changePressed();
    }

    boolean isEmpty() {
        return numPressed == 0;
    }

}
