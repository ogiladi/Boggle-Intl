package com.og.boggle;


import android.support.annotation.NonNull;
import java.util.Stack;

// A class to represent a Boggle board word. Uses a lower case string to represent the word

public class BoggleWord implements Comparable<BoggleWord> {
    private String word;
    private Stack<Integer> listInd;

    BoggleWord(String s) {
        word = s.toLowerCase();
        listInd = new Stack<>();
    }

    void addLetter(String s, int i) {
        word += s.toLowerCase();
        listInd.push(i);
    }

    void removeLast() {
        if (word.length() == 0) return;
        word = word.substring(0, word.length()-1);
        if (word.length() > 0 && word.charAt(word.length()-1) == 'q') {
            word = word.substring(0, word.length()-1);
        }
        listInd.pop();
    }

    public String getWord() {
        return word;
    }

//    public boolean isEmpty() {
//        return listInd.empty();
//    }

    int getLastIndex() {
        if (!listInd.empty()) return listInd.peek();
        else return -1;
    }

    String showWord() {
        if (word.length() == 0) return "";
        else if (word.length() == 1) return word.toUpperCase();
        else return word.substring(0,1).toUpperCase() + word.substring(1, word.length()).toLowerCase();
    }

    int wordScore() {
        int len = word.length();
        if (len < 3) return 0;
        if (len <= 4) return 1;
        if (len == 5) return 2;
        if (len == 6) return 3;
        if (len == 7) return 4;
        return 11;
    }

    @Override
    public int compareTo(@NonNull BoggleWord w) {
        return word.compareTo(w.word);
    }
}
