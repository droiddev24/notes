package example.com.notes.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import example.com.notes.R;

/**
 * Created by VISHU on 06-Aug-2016.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent notesIntent = new Intent(SplashActivity.this, ListNotesActivity.class);
                startActivity(notesIntent);
                finish();
            }
        }, 3000);//3 seconds
    }
}
