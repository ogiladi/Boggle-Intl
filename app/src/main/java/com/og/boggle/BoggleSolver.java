package com.og.boggle;

// A class that creates a solver object. This object enables solving a given board in a fraction
// of a second (solving means finding all the words hiding in the board).

// Using Hunspell dictionaries: https://github.com/wooorm/dictionaries


import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;

class BoggleSolver {
    private final TrieST allWords;
    private final int boardSize = 4;
    private BoardLanguage boardLanguage;

    BoggleSolver(Context context, BoardLanguage.Language language) {

        boardLanguage = new BoardLanguage(language);

        allWords = new TrieST();
        try {
            InputStream is = context.getAssets().open(boardLanguage.getFileName());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String word;
            int wordNum = 0;
            while ((word=reader.readLine()) != null) {
                allWords.put(word, wordNum++);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//  The solver object is implemented as a prefix search tree, also known as Trie
    private class TrieST {
        private final int R = boardLanguage.getNumLetters();
        private Node root;
        private int n;

        class Node {
            private int val;
            private Node[] next;
            private ArrayList<Integer> pathToNode;
            private String word;

            Node() {
                val = -1;
                next = new Node[R];
                pathToNode = new ArrayList<>();
            }

            boolean isWord() {
                return val != -1;
            }

            void setPath(ArrayList<Integer> path) {
                pathToNode = path;
            }

            void addToPath(int ind) {
                pathToNode.add(ind);
            }

            ArrayList<Integer> getPath() {
                return pathToNode;
            }

            int getLast() {
                return pathToNode.get(pathToNode.size()-1);
            }

            void removeLast() {
                pathToNode.remove(pathToNode.get(pathToNode.size()-1));
            }

            String getWord() {
                return word;
            }
        }

        TrieST() {
            n = 0;
        }

        private int numWordsTrie() {
            return n;
        }

        Node root() {
            return root;
        }

        Node getNext(Node x, char c) {
            int indc = boardLanguage.getIndex(c);
            return x.next[indc];
        }

        void delete(String key) {
            if (key == null) throw new IllegalArgumentException("argument to delete() is null");
            root = delete(root, key, 0);
        }

        private Node delete(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) {
                if (x.val != -1) n--;
            } else {
                char c = key.charAt(d);
                int indc = boardLanguage.getIndex(c);
                x.next[indc] = delete(x.next[indc], key, d+1);
            }

            if (x.val != -1) return x;
            for (int i = 0; i < R; i++) {
                if (x.next[i] != null) return x;
            }
            return null;
        }

        void put(String key, int val) {
            if (key == null) throw new IllegalArgumentException("first argument to put() is null");
            if (val == -1) delete(key);
            else root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, int val, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) {
                if (x.val == -1) n++;
                x.val = val;
                x.word = key;
                return x;
            }
            char c = key.charAt(d);
            int indc = boardLanguage.getIndex(c);
            x.next[indc] = put(x.next[indc], key, val, d+1);
            return x;
        }
    }

//  A recurse depth-first-search method to find words hiding in a given board
    private void dfs(TreeSet<String> setWords, TrieST.Node lastNode, BoggleBoard board) {
        if (lastNode.isWord()) {
            String word = lastNode.getWord();
            if (word.length() >= 3) setWords.add(word);
        }

        int lastInd = lastNode.getLast();
        int y = lastInd % boardSize;
        int x = (lastInd - y) / boardSize;

        for (int s = -1; s <= 1; s++) {
            for (int t = -1; t <= 1; t++) {
                if (s != 0 || t != 0) {
                    int newx = x+s;
                    int newy = y+t;
                    if (newx >= 0 && newx < boardSize && newy >= 0 && newy < boardSize) {
                        int newLastInd = boardSize * newx + newy;
                        if(!lastNode.getPath().contains(newLastInd)) {
                            char newLastChar = board.getLetter(newx, newy);
                            TrieST.Node newLastNode = allWords.getNext(lastNode, newLastChar);
                            if (newLastNode != null && newLastChar == 'q') {
                                newLastNode = allWords.getNext(newLastNode, 'u');
                            }
                            if (newLastNode != null) {
                                newLastNode.setPath(lastNode.getPath());
                                newLastNode.addToPath(newLastInd);
                                dfs(setWords, newLastNode, board);
                            }
                            if (newLastNode != null && boardLanguage.letterHasAccents(newLastChar)) {
                                for (char accent : boardLanguage.letterAccents(newLastChar)) {
                                    newLastNode = allWords.getNext(lastNode, accent);
                                    if (newLastNode != null) {
                                        newLastNode.setPath(lastNode.getPath());
                                        newLastNode.addToPath(newLastInd);
                                        dfs(setWords, newLastNode, board);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        lastNode.removeLast();
    }

//    A method to return all words hiding in a board
    TreeSet<BoggleWord> getValidWords(BoggleBoard board) {
        TreeSet<String> validWordsAsString = new TreeSet<>();
        TreeSet<BoggleWord> validWords = new TreeSet<>();

        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                int startInd = boardSize * x + y;
                char startChar = board.getLetter(x, y);
                TrieST.Node startNode = allWords.getNext(allWords.root(), startChar);
                if (startNode != null && startChar == 'q') startNode = allWords.getNext(startNode, 'u');
                if (startNode != null) {
                    startNode.addToPath(startInd);
                    dfs(validWordsAsString, startNode, board);
                }
                if (boardLanguage.letterHasAccents(startChar)) {
                    for (char accent : boardLanguage.letterAccents(startChar)) {
                        startNode = allWords.getNext(allWords.root(), accent);
                        if (startNode != null) {
                            startNode.addToPath(startInd);
                            dfs(validWordsAsString, startNode, board);
                        }
                    }
                }
            }
        }

        for (String wordString : validWordsAsString) {
            validWords.add(new BoggleWord(wordString));
        }

        return validWords;
    }

//    Compute the total score of a board
    int totalScore(BoggleBoard board) {
        TreeSet<BoggleWord> boardWords = getValidWords(board);
        int score = 0;
        for (BoggleWord boggleWord : boardWords) {
            score += boggleWord.wordScore();
        }
        return score;
    }
}
