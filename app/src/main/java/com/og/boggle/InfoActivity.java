package com.og.boggle;

// A class for the info activity

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class InfoActivity extends AppCompatActivity {

    Button gitHubCodeButton;
    Button websiteButton;
    Button backToMainMenuButton;

    private void goToUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gitHubCodeButton = (Button) findViewById(R.id.githubCodeButton);
        websiteButton = (Button) findViewById(R.id.websiteButton);
        backToMainMenuButton = (Button) findViewById(R.id.backToMainMenuButton);

        gitHubCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String s = "https://github.com/ogiladi";
                goToUrl(getString(R.string.githubUrl));
            }
        });

        websiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUrl(getString(R.string.myWebsiteUrl));
            }
        });

        backToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
