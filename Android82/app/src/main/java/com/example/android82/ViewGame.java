package com.example.android82;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.android82.databinding.ViewGameBinding;

import java.io.File;
import java.io.IOException;

public class ViewGame extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ViewGameBinding binding;

    private ChessRecord record;
    private int position;

    private TextView game_record_description;
    private ListView game_record_moves;
    private Button replay_button;
    private Button delete_button;

    public ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_game);

        //tool bar stuff
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.game_record_description = findViewById(R.id.game_record_description);
        this.game_record_moves = findViewById(R.id.game_record_moves);
        this.replay_button = findViewById(R.id.replay_button);
        this.delete_button = findViewById(R.id.delete_button);

        Bundle bundle = getIntent().getExtras();
        this.position = bundle.getInt("position");

        try {
            ChessRecordGroup crg = ChessRecordGroup.readApp(this.getFilesDir()+ File.separator+"ChessRecord.dat");
            this.record = crg.chessRecords.get(this.position);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.game_record_description.setText(this.record.toString());
        this.game_record_moves.setAdapter(new ArrayAdapter<>(this,R.layout.item,this.record.moves));

        this.replay_button.setOnClickListener(e->replay_button_click());
        this.delete_button.setOnClickListener(e->delete_button_click());

        this.activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            //don't need to do anything actually
                        }
                    }
                });
    }
    private void replay_button_click(){
        Bundle bundle = new Bundle();
        bundle.putInt("position",this.position);
        Intent intent = new Intent(this,ReplayGame.class);
        intent.putExtras(bundle);
        this.activityResultLauncher.launch(intent);
    }
    private void delete_button_click(){
        Bundle bundle = new Bundle();
        bundle.putInt("position",this.position);
        bundle.putString("filePath",this.getFilesDir()+ File.separator+"ChessRecord.dat");

        DialogFragment df = new ViewGameDialogFragment();
        df.setArguments(bundle);
        df.show(getSupportFragmentManager(),"badfields");
        return;
    }
}