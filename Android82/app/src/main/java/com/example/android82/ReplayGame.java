package com.example.android82;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android82.piece.Bishop;
import com.example.android82.piece.King;
import com.example.android82.piece.Knight;
import com.example.android82.piece.Pawn;
import com.example.android82.piece.Queen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ReplayGame extends AppCompatActivity {

    private int position;

    private Chess chess_game;
    private ImageView[][] pieces;

    private TextView result_text;
    private Button back_button;
    private Button next_move_button;
    private ListView recorded_moves;

    private String[] moves;
    private int moves_index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay_game);

        Bundle bundle = getIntent().getExtras();
        this.position = bundle.getInt("position");

        setup();
    }
    public void back_button_click(){
        Bundle bundle = new Bundle();
        bundle.putInt("position",this.position);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }
    private void next_move_button_click(){
        String input_string = this.moves[moves_index];
        chess_game.applyAndroidMove(input_string);
        applyTurnOver();
        if(chess_game.isOver){
            applyGameOver();
        }
        else{
            this.moves_index++;
        }
        applyChessBoard();
    }
    private void setup(){
        pieces = new ImageView[8][8];

        ConstraintLayout chessboard = findViewById(R.id.chessboard);

        //setting up the fields for PlayGame
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                pieces[i][j] = (ImageView)((ViewGroup)chessboard.getChildAt(8*i + j)).getChildAt(1);
            }
        }
        this.chess_game = new Chess();
        this.chess_game.initiateGame();

        //setting up result text (showing turn)
        this.result_text = findViewById(R.id.result_textview);
        this.result_text.setText("White's Turn");
        applyChessBoard();

        //setting up buttons
        this.back_button = findViewById(R.id.back_button);
        this.back_button.setOnClickListener(e->back_button_click());
        this.next_move_button = findViewById(R.id.next_move_button);
        this.next_move_button.setOnClickListener(e->next_move_button_click());

        //set up recorded_moves listview
        this.recorded_moves = findViewById(R.id.recorded_moves);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,R.layout.recorded_move,this.chess_game.moves.split("\n"));
        this.recorded_moves.setAdapter(adapter3);

        //set up moves array and moves index
        this.moves_index = 0;
        try {
            ChessRecordGroup crg = ChessRecordGroup.readApp(this.getFilesDir()+ File.separator+"ChessRecord.dat");
            this.moves = crg.chessRecords.get(this.position).moves;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void applyTurnOver(){
        String result_text_content = "";
        if(this.chess_game.turn == 'w'){
            result_text_content += "White's Turn. ";
        }
        else{
            result_text_content += "Black's Turn. ";
        }
        switch(this.chess_game.checkStatus){
            case 0:
                break;
            case 1:
                result_text_content += "Checked. ";
                break;
            case 2:
                result_text_content += "Check Mate. ";//is this meaningless?
                break;
            default:
        }
        this.result_text.setText(result_text_content);
        this.recorded_moves.setAdapter(new ArrayAdapter<>(this,R.layout.recorded_move,this.chess_game.moves.split("\n")));
    }
    private void applyGameOver(){
        String result_text_content = "";
        if(this.chess_game.checkStatus == 2){
            result_text_content += "Check Mate. ";
        }

        if(this.chess_game.is_stalemate){
            result_text_content += "Stalemate. ";
        }

        if(this.chess_game.winner == 'd'){
            result_text_content += "Draw. ";
        }
        else if(this.chess_game.winner == 'w'){
            result_text_content += "White wins.";
        }
        else{
            result_text_content += "Black wins.";
        }

        this.result_text.setText(result_text_content);
        this.next_move_button.setVisibility(View.GONE);
    }
    private void applyChessBoard(){//transfers the chessboard data to the GUI
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                Piece temp = this.chess_game.chessboard[i][j];
                if(temp!= null){//need to change the drawables here
                    this.pieces[i][j].setVisibility(View.VISIBLE);
                    if(temp instanceof Pawn){
                        if(temp.color == 'w'){
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.white_pawn,this.getTheme()));
                        }
                        else{
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.black_pawn,this.getTheme()));
                        }
                    }
                    else if(temp instanceof Bishop){
                        if(temp.color == 'w'){
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.white_bishop,this.getTheme()));
                        }
                        else{
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.black_bishop,this.getTheme()));
                        }
                    }
                    else if(temp instanceof King){
                        if(temp.color == 'w'){
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.white_king,this.getTheme()));
                        }
                        else{
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.black_king,this.getTheme()));
                        }
                    }
                    else if(temp instanceof Knight){
                        if(temp.color == 'w'){
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.white_knight,this.getTheme()));
                        }
                        else{
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.black_knight,this.getTheme()));
                        }
                    }
                    else if(temp instanceof Queen){
                        if(temp.color == 'w'){
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.white_queen,this.getTheme()));
                        }
                        else{
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.black_queen,this.getTheme()));
                        }
                    }
                    else{//Rook
                        if(temp.color == 'w'){
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.white_rook,this.getTheme()));
                        }
                        else{
                            this.pieces[i][j].setImageDrawable(getResources().getDrawable(R.drawable.black_rook,this.getTheme()));
                        }
                    }
                }
                else{//no piece here
                    this.pieces[i][j].setImageDrawable(null);
                    this.pieces[i][j].setVisibility(View.GONE);
                }
            }
        }
    }
}