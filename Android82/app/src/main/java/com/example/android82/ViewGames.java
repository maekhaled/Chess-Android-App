package com.example.android82;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.android82.databinding.ActivityViewGamesBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class ViewGames extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityViewGamesBinding binding;

    private ArrayList<ChessRecord> records;
    private ListView listview;
    private Button sort_button;
    private Spinner sort_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_games);

        //tool bar stuff
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            ChessRecordGroup crg = ChessRecordGroup.readApp(this.getFilesDir()+File.separator+"ChessRecord.dat");
            this.records = new ArrayList<>();
            for(ChessRecord cr: crg.chessRecords){
                this.records.add(cr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.listview = findViewById(R.id.games_list);
        this.listview.setAdapter(new ArrayAdapter(this,R.layout.item,this.records.toArray()));
        this.listview.setOnItemClickListener((a,b,pos,d)->itemClick(pos));

        this.sort_button = findViewById(R.id.sort_button);
        this.sort_spinner = findViewById(R.id.sort_spinner);

        this.sort_spinner.setAdapter(new ArrayAdapter(this,R.layout.spinner, getResources().getStringArray(R.array.sort_types)));
        this.sort_spinner.setSelection(0);

        this.sort_button.setOnClickListener(e->sort_button_click());
    }
    private void itemClick(int pos){
        Bundle bundle = new Bundle();
        bundle.putInt("position",pos);
        Intent intent = new Intent(this,ViewGame.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void sort_button_click(){
        if(((String)this.sort_spinner.getSelectedItem()).equals("By Title")){
            this.records.sort(Comparator.comparing(r->r.game_title.toUpperCase()));
        }
        else{//By Date
            this.records.sort(Comparator.comparing(r->r.game_date_time));
        }
        this.listview.setAdapter(new ArrayAdapter(this,R.layout.item,this.records.toArray()));
    }
}