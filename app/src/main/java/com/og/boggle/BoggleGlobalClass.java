package com.og.boggle;

// A global class that contains the solver object -- the object that is used to solve a
// given board in a fraction of a second. This object is deleted each time the player goes back to
// the main menu. This is because the solver object uses a large amount of memory

import android.app.Application;

public class BoggleGlobalClass extends Application {

    private BoardLanguage.Language language;
    private BoggleSolver solver;
    private String globalListAllWords;

    public BoggleGlobalClass() {}

    public void onCreate() {
        super.onCreate();
    }

    public void buildSolver(BoardLanguage.Language language) {
        this.language = language;
        solver = new BoggleSolver(this, language);
    }

    public BoggleSolver getSolver() {
        return solver;
    }

    public BoardLanguage.Language getLanguage() {
        return this.language;
    }

    void deleteSolver() {
        solver = null;
    }

    public void setGlobalListAllWords(String s) { globalListAllWords = s; }
}
