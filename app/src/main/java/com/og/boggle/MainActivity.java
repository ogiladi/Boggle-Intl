package com.og.boggle;

// A class for the main activity (main menu)

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    protected static BoggleGlobalClass global;

    Button chooseEnglish;
    Button chooseFrench;
    Button chooseSpanish;

//    @Override
//    public void finish() {
//        super.finish();
//        global.deleteSolver();
//    }

    private static class BuildSolverTask extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog dialog;
        private WeakReference<MainActivity> weakRef;
        private BoardLanguage.Language language;

        private BuildSolverTask(MainActivity context, BoardLanguage.Language language) {
            weakRef = new WeakReference<>(context);
            this.language = language;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            final MainActivity activity = weakRef.get();
            dialog = ProgressDialog.show(
                    activity,
                    activity.getString(R.string.boardLoadingTitle),
                    activity.getString(R.string.boardLoadingSubTitle),
                    true,
                    true,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            BuildSolverTask.this.cancel(true);
                            activity.finish();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            global.buildSolver(language);
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            global.deleteSolver();
        }

        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (dialog != null) dialog.dismiss();
            MainActivity activity = weakRef.get();
            activity.startActivity(new Intent(activity, GameActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        global = (BoggleGlobalClass) getApplication();

        chooseEnglish = (Button) findViewById(R.id.chooseEnglish);
        chooseFrench = (Button) findViewById(R.id.chooseFrench);
        chooseSpanish = (Button) findViewById(R.id.chooseSpanish);

        chooseEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BuildSolverTask(MainActivity.this, BoardLanguage.Language.EN_CA).execute();
            }
        });

        chooseFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BuildSolverTask(MainActivity.this, BoardLanguage.Language.FR).execute();
            }
        });

        chooseSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BuildSolverTask(MainActivity.this, BoardLanguage.Language.ES).execute();
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

        return super.onOptionsItemSelected(item);
    }
}
