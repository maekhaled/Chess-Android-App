package com.example.android82;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class ChessApp extends AppCompatActivity {
    Button play_button;
    Button view_recorded_games_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play_button = findViewById(R.id.play_button);
        view_recorded_games_button = findViewById(R.id.view_recorded_games_button);

        play_button.setOnClickListener(v->playGame());
        view_recorded_games_button.setOnClickListener(v->viewRecordedGames());

        //create data file if not found
        File file = new File(this.getFilesDir(), "ChessRecord.dat");
        if(!file.exists()){
            try {
                file.createNewFile();
                ChessRecordGroup crg = new ChessRecordGroup();
                ChessRecordGroup.writeApp(crg,this.getFilesDir()+File.separator+"ChessRecord.dat");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void playGame(){
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this,PlayGame.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void viewRecordedGames(){
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this,ViewGames.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}