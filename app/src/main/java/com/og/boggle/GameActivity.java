package com.og.boggle;

// A class for the game activity

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.TreeSet;

public class GameActivity extends AppCompatActivity {

    protected BoggleGlobalClass global;
    private BoardLanguage.Language language;
    private BoardLanguage boardLanguage;
    private final int boardSize = 4;

    TextView[][] boxes;
    TextView currentWordView;
    TextView scoreView;

    RelativeLayout boardLayout;
    RelativeLayout gameOverLayout;

    Button optionsButton;
    Button startNewGameButton;
    Button backToMainMenuButton;


    int offBackColor = Color.parseColor("#F2eaedf2");
    int onBackColor = Color.parseColor("#F2073e99");
    int offTextColor = onBackColor;
    int onTextColor = offBackColor;

    BoggleBoard board;
    int totalBoardScore;
    int currentScore;
    BoggleWord currentBoggleWord;

    int minBoardScore;

    TreeSet<BoggleWord> boardWords;
    TreeSet<BoggleWord> remainingWords;
    TreeSet<BoggleWord> foundWords;
    String boardWordsString;

    Animation fadeIn = new AlphaAnimation(0f, 1f);
    Animation fadeOut = new AlphaAnimation(1f, 0f);

    private void goToUrl(String s) {
        Uri uri = Uri.parse(s);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(launchBrowser);
    }

    private String showLanguage() {
        switch (language) {
            case EN_CA: return getString(R.string.chooseEnglish);
            case FR: return getString(R.string.chooseFrench);
            case ES: return getString(R.string.chooseSpanish);
        }
        return "";
    }

//    destroy object when going back to main menu to avoid using too much memory
    @Override
    public void finish() {
        super.finish();
        global.deleteSolver();
    }

    private String presentList(TreeSet<BoggleWord> listWords) {
        StringBuilder result = new StringBuilder();
        for (BoggleWord word : listWords) {
            result.append(word.showWord()).append("\u00A0").append("| ");
        }
        String resultString = result.toString().trim();
        if (resultString.length() >= 2) resultString = resultString.substring(0, resultString.length()-2);
        return resultString;
    }

    private BoggleWord containsWord(BoggleWord word, TreeSet<BoggleWord> listWords, BoardLanguage boardLanguage) {
        for (BoggleWord listWord : listWords) {
            if (boardLanguage.compareWords(word, listWord)) {
                Log.i("word", listWord.showWord());
                return listWord;
            }
        }
        return null;
    }

    public void startGame(int minTotalScore) {
        boxes = new TextView[boardSize][boardSize];
        board = new BoggleBoard(language);

        totalBoardScore = global.getSolver().totalScore(board);
        currentScore = 0;

        while (totalBoardScore < minTotalScore) {
            board = new BoggleBoard(language);
            totalBoardScore = global.getSolver().totalScore(board);
        }

        boardWords = global.getSolver().getValidWords(board);
        remainingWords = global.getSolver().getValidWords(board);
        foundWords = new TreeSet<>();

        global.setGlobalListAllWords(boardWordsString);

        currentBoggleWord = new BoggleWord("");

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                int ind = boardSize * row + col;
                String btnID = "box" + Integer.toString(ind);
                int resID = getResources().getIdentifier(btnID, "id", "com.og.boggle");
                boxes[row][col] = (TextView) findViewById(resID);
                boxes[row][col].setText(board.showLetter(row, col));
                boxes[row][col].setBackgroundColor(offBackColor);
                boxes[row][col].setTextColor(offTextColor);
            }
        }

        scoreView.setText(String.format(getString(R.string.currentScore), 0, totalBoardScore));
        currentWordView.setText(String.format(getString(R.string.currentWord), currentBoggleWord.showWord()));
    }

    public void chooseLetter(View view) {
        fadeIn.setDuration(300);
        fadeOut.setDuration(300);

        int index = Integer.parseInt(view.getTag().toString());
        int col = index % boardSize;
        int row = (index - col) / boardSize;

        // choosing an unchosen box
        if (!board.isPressed(row, col)) {
            if (board.isEmpty() || board.areNbrs(index, currentBoggleWord.getLastIndex())) {
                board.boxPressed(row, col);
                view.setBackgroundColor(onBackColor);
                ((TextView) view).setTextColor(onTextColor);
                view.startAnimation(fadeOut);
                String s = board.showLetter(row, col);
                currentBoggleWord.addLetter(s, index);
                BoggleWord listWord = containsWord(currentBoggleWord, boardWords, boardLanguage);
                BoggleWord remainingWord = containsWord(currentBoggleWord, remainingWords, boardLanguage);
                if (listWord != null && remainingWord == null) {
                    foundOldWord();
                }
                if (remainingWord != null) {
                    foundNewWord(remainingWord);
                }
            } else {
                showMsg(getString(R.string.mustAdjacent), 2000);
                return;
            }
        }
        // unchoosing a chosen box
        else {
            if (index == currentBoggleWord.getLastIndex()) {
                board.boxPressed(row, col);
                currentBoggleWord.removeLast();
                view.setBackgroundColor(offBackColor);
                ((TextView) view).setTextColor(offTextColor);
                view.startAnimation(fadeOut);
            }
            else {
                showMsg(getString(R.string.removeLast), 2000);
                return;
            }
        }
        view.startAnimation(fadeIn);

        scoreView.setText(String.format(getString(R.string.currentScore), currentScore, totalBoardScore));
        currentWordView.setText(String.format(getString(R.string.currentWord), currentBoggleWord.showWord()));

        if (currentScore == totalBoardScore) {
            gameOver();
        }
    }

    public void cleanBoard() {
        fadeIn.setDuration(300);
        fadeOut.setDuration(300);
        currentBoggleWord = new BoggleWord("");
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board.isPressed(row, col)) {
                    board.boxPressed(row, col);
                    int ind = boardSize * row + col;
                    String btnID = "box" + Integer.toString(ind);
                    int resID = getResources().getIdentifier(btnID, "id", "com.og.boggle");
                    TextView currentButton = (TextView) findViewById(resID);
                    currentButton.startAnimation(fadeOut);
                    currentButton.setBackgroundColor(offBackColor);
                    currentButton.setTextColor(offTextColor);
                    currentButton.startAnimation(fadeIn);
                }
            }
        }
    }

    private void gameOver() {
        AlertDialog.Builder builderGameOver = new AlertDialog.Builder(GameActivity.this);
        View viewGameOver = getLayoutInflater().inflate(R.layout.game_over, null);
        builderGameOver.setView(viewGameOver);
        final AlertDialog dialogGameOver = builderGameOver.create();

        Button newGameButton = viewGameOver.findViewById(R.id.newGameButton);
        Button backToMainMenuButton = viewGameOver.findViewById(R.id.backToMainMenuButton);

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(minBoardScore);
                dialogGameOver.dismiss();
            }
        });

        backToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialogGameOver.show();
    }

    private void showMsg(String s, int time) {
        AlertDialog.Builder builderShowMsg= new AlertDialog.Builder(GameActivity.this);
        View viewShowMsg = getLayoutInflater().inflate(R.layout.show_msg, null);
        builderShowMsg.setView(viewShowMsg);
        TextView msgContent = viewShowMsg.findViewById(R.id.msgContent);
        msgContent.setText(s);

        final AlertDialog dialogShowMsg = builderShowMsg.create();
        dialogShowMsg.show();

        new CountDownTimer(time,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                dialogShowMsg.dismiss();
            }
        }.start();
    }

    private void foundNewWord(BoggleWord w) {
        String newWordMsg = String.format(getString(R.string.foundNewWord), w.showWord());
        String scoreMsg;
        if (w.wordScore() == 1) {
            scoreMsg = getString(R.string.gainOnePt);
        } else {
            scoreMsg = String.format(getString(R.string.gainMultiPts), w.wordScore());
        }
        showMsg(newWordMsg+scoreMsg, 1500);
        currentScore += w.wordScore();
        remainingWords.remove(w);
        foundWords.add(w);
        cleanBoard();
    }

    private void foundOldWord() {
        showMsg(getString(R.string.foundOldWord), 1200);
    }

    private void showOptions() {
        AlertDialog.Builder builderOptions = new AlertDialog.Builder(GameActivity.this);
        View viewOptions = getLayoutInflater().inflate(R.layout.options_menu, null);
        builderOptions.setView(viewOptions);
        final AlertDialog dialogOptions = builderOptions.create();

        Button recallRulesButton = viewOptions.findViewById(R.id.recallRulesButton);
        Button revealBoardWordsButton = viewOptions.findViewById(R.id.revealBoardWordsButton);
        Button revealUnfoundWordsButton = viewOptions.findViewById(R.id.revealUnfoundWordsButton);
        Button newGameButton = viewOptions.findViewById(R.id.newGameButton);
        Button backToMainMenuButton = viewOptions.findViewById(R.id.backToMainMenuButton);
        Button backToGameButton = viewOptions.findViewById(R.id.backToGameButton);

        recallRulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOptions.dismiss();
                AlertDialog.Builder builderRules = new AlertDialog.Builder(GameActivity.this);
                View viewRules = getLayoutInflater().inflate(R.layout.boggle_rules, null);
                builderRules.setView(viewRules);
                final AlertDialog dialogRules = builderRules.create();

                Button backToGameButton = viewRules.findViewById(R.id.backToGameButton);
                backToGameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogRules.dismiss();
                    }
                });

                dialogRules.show();
            }
        });

        revealBoardWordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOptions.dismiss();
                AlertDialog.Builder builderRevealWords = new AlertDialog.Builder(GameActivity.this);
                View viewRevealWords = getLayoutInflater().inflate(R.layout.reveal_words, null);
                builderRevealWords.setView(viewRevealWords);
                final AlertDialog dialogRevealWords = builderRevealWords.create();

                TextView revealWordsContent = viewRevealWords.findViewById(R.id.revealWordsContent);
                Button backToGameButton = viewRevealWords.findViewById(R.id.backToGameButton);

                revealWordsContent.setText(presentList(boardWords));
                backToGameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogRevealWords.dismiss();
                    }
                });
                dialogRevealWords.show();
            }
        });

        revealUnfoundWordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOptions.dismiss();
                AlertDialog.Builder builderRevealWords = new AlertDialog.Builder(GameActivity.this);
                View viewRevealWords = getLayoutInflater().inflate(R.layout.reveal_words, null);
                builderRevealWords.setView(viewRevealWords);
                final AlertDialog dialogRevealWords = builderRevealWords.create();

                TextView revealWordsContent = viewRevealWords.findViewById(R.id.revealWordsContent);
                Button backToGameButton = viewRevealWords.findViewById(R.id.backToGameButton);

                revealWordsContent.setText(presentList(remainingWords));
                backToGameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogRevealWords.dismiss();
                    }
                });
                dialogRevealWords.show();
            }
        });

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame(minBoardScore);
                dialogOptions.dismiss();
            }
        });

        backToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        backToGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOptions.dismiss();
            }
        });

        dialogOptions.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        minBoardScore = 30;

        boardLayout = (RelativeLayout) findViewById(R.id.boardLayout);
        gameOverLayout = (RelativeLayout) findViewById(R.id.gameOverLayout);

        optionsButton = (Button) findViewById(R.id.optionsButton);
        startNewGameButton = (Button) findViewById(R.id.startNewGameButton);
        backToMainMenuButton = (Button) findViewById(R.id.backToMainMenuButton);

        currentWordView = (TextView) findViewById(R.id.currentWordView);
        scoreView = (TextView) findViewById(R.id.scoreView);

        global = (BoggleGlobalClass) getApplication();

        if (global == null) finish();

        language = global.getLanguage();
        boardLanguage = new BoardLanguage(language);

        getSupportActionBar().setTitle(String.format(getString(R.string.gameActivityTitle), showLanguage()));

        startGame(minBoardScore);

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.aboutApp) {
            startActivity(new Intent(this, InfoActivity.class));
            return true;
        }

        if (id == R.id.aboutBackground) {
            goToUrl(getString(R.string.aboutBackgroundUrl));
            return true;
        }

        if (id == R.id.aboutBoggle) {
            goToUrl(getString(R.string.moreAboutBoggleUrl));
        }

        return super.onOptionsItemSelected(item);
    }

}
