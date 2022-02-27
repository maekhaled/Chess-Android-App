package com.example.android82;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.android82.databinding.AddGameBinding;

import java.io.File;
import java.io.IOException;

public class AddGame extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private AddGameBinding binding;

    private EditText game_title;
    private Button add_button;

    private String[] moves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_game);

        //tool bar stuff
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting up moves array
        Bundle bundle = getIntent().getExtras();
        String temp  = bundle.getString("moves");
        this.moves = temp.split("\n");

        //setting up buttons and stuff
        this.game_title = findViewById(R.id.game_title);
        this.add_button = findViewById(R.id.add_game);
        this.add_button.setOnClickListener(e-> {
            try {
                add_button_click();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        });
    }
    private void add_button_click() throws IOException, ClassNotFoundException {
        String title = this.game_title.getText().toString();
        if(title.equals("")){//empty title not allowed
            Bundle bundle = new Bundle();
            bundle.putString("message","Title cannot be empty.");
            DialogFragment df = new AddGameDialogFragment();
            df.setArguments(bundle);
            df.show(getSupportFragmentManager(),"badfeilds");
            return;
        }

        ChessRecordGroup crg = ChessRecordGroup.readApp(this.getFilesDir()+ File.separator+"ChessRecord.dat");

        //check whether there is a duplicate title
        boolean duplicateFound = false;
        for(int i = 0; i < crg.chessRecords.size(); i++){
            if(crg.chessRecords.get(i).game_title.equals(title)){
                duplicateFound = true;
                break;
            }
        }
        if(duplicateFound){
            Bundle bundle = new Bundle();
            bundle.putString("message","Duplicate title exists. Please provide a unique title.");
            DialogFragment df = new AddGameDialogFragment();
            df.setArguments(bundle);
            df.show(getSupportFragmentManager(),"badfeilds");
            return;
        }

        crg.chessRecords.add(new ChessRecord(this.game_title.getText().toString(),this.moves));
        ChessRecordGroup.writeApp(crg,this.getFilesDir()+File.separator+"ChessRecord.dat");

        Toast toast = Toast.makeText(this,"Successfully Added Game Record!",Toast.LENGTH_SHORT);
        toast.show();

        startActivity(new Intent(this,ChessApp.class));
    }
}