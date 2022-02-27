package com.example.android82;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.android82.piece.*;

import java.util.ArrayList;

public class PlayGame extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ImageView[][] pieces;
    private boolean[][] highlighted;
    private boolean clicked_state;
    private ImageView clicked_piece;
    private RelativeLayout[][] tiles;
    private Chess chess_game;
    private boolean undo_allowed;

    private Spinner white_promotion_type_spinner;
    private Spinner black_promotion_type_spinner;
    private String[] promotion_types;

    private TextView result_text;
    private Button undo_button;
    private Button AI_button;
    private Button restart_button;
    private Button draw_button;
    private Button resign_button;
    private Button record_button;
    private ListView recorded_moves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //basic setup
        setup();
    }
    private void setup(){
        pieces = new ImageView[8][8];
        highlighted = new boolean[8][8];
        tiles = new RelativeLayout[8][8];
        clicked_state = false;
        clicked_piece = null;
        undo_allowed = true;

        ConstraintLayout chessboard = findViewById(R.id.chessboard);

        //setting up the fields for PlayGame
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                tiles[i][j] = (RelativeLayout)chessboard.getChildAt(8*i + j);
                tiles[i][j].setOnClickListener(e->tile_click((RelativeLayout)e));
                pieces[i][j] = (ImageView)((ViewGroup)chessboard.getChildAt(8*i + j)).getChildAt(1);
                pieces[i][j].setOnClickListener(e->piece_click((ImageView)e));
                highlighted[i][j] = false;
            }
        }
        this.chess_game = new Chess();
        this.chess_game.initiateGame();
        this.undo_button = findViewById(R.id.undo_button);
        this.AI_button = findViewById(R.id.AI);
        this.draw_button = findViewById(R.id.draw_button);
        this.resign_button = findViewById(R.id.resign_button);
        this.restart_button = findViewById(R.id.restart_button);
        this.record_button = findViewById(R.id.record_button);
        this.result_text = findViewById(R.id.result_textview);

        this.result_text.setText("");
        this.undo_button.setOnClickListener(e->undo_click());
        this.AI_button.setOnClickListener(e->AI_click());
        this.draw_button.setOnClickListener(e->draw_click());
        this.resign_button.setOnClickListener(e->resign_click());
        this.restart_button.setOnClickListener(e->restart_click());
        this.record_button.setOnClickListener(e->record_click());

        this.restart_button.setVisibility(View.GONE);
        this.record_button.setVisibility(View.GONE);

        //setting up spinners(dropdowns) for promotion type selection
        this.promotion_types = getResources().getStringArray(R.array.promotion_types);
        this.white_promotion_type_spinner = findViewById(R.id.white_promotion_type_spinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,R.layout.spinner,this.promotion_types);
        this.white_promotion_type_spinner.setAdapter(adapter1);
        this.black_promotion_type_spinner = findViewById(R.id.black_promotion_type_spinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,R.layout.spinner,this.promotion_types);
        this.black_promotion_type_spinner.setAdapter(adapter2);
        this.white_promotion_type_spinner.setSelection(0);
        this.black_promotion_type_spinner.setSelection(0);

        //setting up result text (showing turn)
        this.result_text.setText("White's Turn");
        applyChessBoard();

        this.recorded_moves = findViewById(R.id.recorded_moves);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,R.layout.recorded_move,this.chess_game.moves.split("\n"));
        this.recorded_moves.setAdapter(adapter3);
    }
    private void piece_click(ImageView piece){
        if(chess_game.isOver){
            return;
        }

        String tile_number = ((TextView)((RelativeLayout)piece.getParent()).getChildAt(0)).getText().toString();
        int[] indices = convertTileNumberToIndices(tile_number);
        int col = indices[0], row = indices[1];

        if(!clicked_state){
            clicked_state = true;
            clicked_piece = piece;
            //highlight the possible moves
            ArrayList<Integer[]> possible_moves = this.chess_game.chessboard[row][col].getActuallyPossibleMoves(this.chess_game.chessboard,this.chess_game.turns_passed,this.chess_game.turn);
            for(int i = 0; i < possible_moves.size(); i++) {
                this.highlighted[possible_moves.get(i)[0]][possible_moves.get(i)[1]] = true;
                if((possible_moves.get(i)[0]+possible_moves.get(i)[1]) % 2 == 0){//white tile
                    this.tiles[possible_moves.get(i)[0]][possible_moves.get(i)[1]].setBackground(getResources().getDrawable(R.drawable.highlighted_white_tile,this.getTheme()));
                }
                else{//black tile
                    this.tiles[possible_moves.get(i)[0]][possible_moves.get(i)[1]].setBackground(getResources().getDrawable(R.drawable.highlighted_black_tile,this.getTheme()));
                }
            }
            //highlight the clicked piece's tile
            this.highlighted[row][col] = true;
            if((col+row) % 2 == 0){//white tile
                this.tiles[row][col].setBackground(getResources().getDrawable(R.drawable.highlighted_white_tile,this.getTheme()));
            }
            else{//black tile
                this.tiles[row][col].setBackground(getResources().getDrawable(R.drawable.highlighted_black_tile,this.getTheme()));
            }
        }
        else{
            if(highlighted[row][col] == false){//unhighlight everything
                unhighlightTiles();
                clicked_piece = null;
                clicked_state = false;
            }
            else{
                if(piece == this.clicked_piece){//clicking the same piece again ==> unhighlight everything
                    unhighlightTiles();
                    clicked_piece = null;
                    clicked_state = false;
                }
                else{//making a move (killing a piece)
                    String beforeMoveTileNumber = ((TextView)((RelativeLayout)this.clicked_piece.getParent()).getChildAt(0)).getText().toString();
                    String afterMoveTileNumber = tile_number;
                    String input_string = beforeMoveTileNumber + " " + afterMoveTileNumber;

                    int[] indices2 = convertTileNumberToIndices(beforeMoveTileNumber);
                    if(chess_game.isValidPromotion(indices2[1],indices2[0],row,col)){
                        if(this.chess_game.turn == 'w'){
                            input_string += " " + processPromotionType((String)this.white_promotion_type_spinner.getSelectedItem());
                        }
                        else{
                            input_string += " " + processPromotionType((String)this.black_promotion_type_spinner.getSelectedItem());
                        }
                    }
                    chess_game.applyAndroidMove(input_string);
                    applyTurnOver();
                    if(chess_game.isOver){
                        applyGameOver();
                    }

                    unhighlightTiles();
                    clicked_piece = null;
                    clicked_state = false;
                    applyChessBoard();
                    undo_allowed = true;
                }
            }
        }
    }
    private void tile_click(RelativeLayout tile){
        if(chess_game.isOver){
            return;
        }
        int row, col;
        String tile_number = ((TextView)tile.getChildAt(0)).getText().toString();
        int[] indices = convertTileNumberToIndices(tile_number);
        col = indices[0];
        row = indices[1];

        if(clicked_state){
          //if clicked tile is highlighted
            if(highlighted[row][col]){
                String beforeMoveTileNumber = ((TextView)((RelativeLayout)this.clicked_piece.getParent()).getChildAt(0)).getText().toString();
                String afterMoveTileNumber = tile_number;
                String input_string = beforeMoveTileNumber + " " + afterMoveTileNumber;

                int[] indices2 = convertTileNumberToIndices(beforeMoveTileNumber);
                if(chess_game.isValidPromotion(indices2[1],indices2[0],row,col)){
                    if(this.chess_game.turn == 'w'){
                        input_string += " " + processPromotionType((String)this.white_promotion_type_spinner.getSelectedItem());
                    }
                    else{
                        input_string += " " + processPromotionType((String)this.black_promotion_type_spinner.getSelectedItem());
                    }
                }
                chess_game.applyAndroidMove(input_string);
                applyTurnOver();
                if(chess_game.isOver){
                    applyGameOver();
                }

                unhighlightTiles();
                clicked_piece = null;
                clicked_state = false;
                applyChessBoard();
                undo_allowed = true;
            }
            else{
                unhighlightTiles();
                clicked_state = false;
                clicked_piece = null;
            }
        }
    }
    private void undo_click(){
        if(this.chess_game.turns_passed == 0 || !undo_allowed){
           return;
        }
        this.chess_game.applyAndroidMove("undo");
        applyTurnOver();
        unhighlightTiles();
        clicked_piece = null;
        clicked_state = false;
        applyChessBoard();
        undo_allowed = false;
    }

    private class PossibleMoves{
        Integer[] starting_position;
        ArrayList<Integer[]> possible_moves;
    }
    private void AI_click(){
        if(chess_game.isOver){
            return;
        }
        ArrayList<PossibleMoves> all_possible_moves = new ArrayList<>();

        for(int i = 0; i < this.chess_game.chessboard.length; i++){
            for(int j = 0; j < this.chess_game.chessboard[i].length; j++){
                if(this.chess_game.chessboard[i][j] != null && this.chess_game.turn == this.chess_game.chessboard[i][j].color){
                    Integer[] starting_position = new Integer[]{i,j};
                    ArrayList<Integer[]> possible_moves = this.chess_game.chessboard[i][j].getActuallyPossibleMoves(this.chess_game.chessboard,this.chess_game.turns_passed,this.chess_game.turn);
                    if(possible_moves.size() != 0){
                        PossibleMoves pm = new PossibleMoves();
                        pm.starting_position = starting_position;
                        pm.possible_moves = possible_moves;
                        all_possible_moves.add(pm);
                    }
                }
            }
        }
        int random_number1 = (int)(all_possible_moves.size() * Math.random());
        all_possible_moves.get(random_number1);

        int random_number2 = (int)(all_possible_moves.get(random_number1).possible_moves.size() * Math.random());
        String input_string = (char)('a' + all_possible_moves.get(random_number1).starting_position[1]) + "" + (char)('8' - all_possible_moves.get(random_number1).starting_position[0]);
        input_string += " " + (char)('a' + all_possible_moves.get(random_number1).possible_moves.get(random_number2)[1]) + "" + (char)('8' - all_possible_moves.get(random_number1).possible_moves.get(random_number2)[0]);

        if(chess_game.isValidPromotion(all_possible_moves.get(random_number1).starting_position[0],all_possible_moves.get(random_number1).starting_position[1],
                all_possible_moves.get(random_number1).possible_moves.get(random_number2)[0],all_possible_moves.get(random_number1).possible_moves.get(random_number2)[1])){
            if(this.chess_game.turn == 'w'){
                input_string += " " + processPromotionType((String)this.white_promotion_type_spinner.getSelectedItem());
            }
            else{
                input_string += " " + processPromotionType((String)this.black_promotion_type_spinner.getSelectedItem());
            }
        }
        chess_game.applyAndroidMove(input_string);
        applyTurnOver();
        if(chess_game.isOver){
            applyGameOver();
        }

        unhighlightTiles();
        clicked_piece = null;
        clicked_state = false;
        applyChessBoard();
        undo_allowed = true;
    }
    private void draw_click(){
        chess_game.applyAndroidMove("draw?");
        applyTurnOver();
        if(chess_game.isOver){
            applyGameOver();
        }

        unhighlightTiles();
        clicked_piece = null;
        clicked_state = false;
        applyChessBoard();
    }
    private void resign_click(){
        chess_game.applyAndroidMove("resign");
        applyTurnOver();
        if(chess_game.isOver){
            applyGameOver();
        }

        unhighlightTiles();
        clicked_piece = null;
        clicked_state = false;
        applyChessBoard();
    }
    private void restart_click(){
        if(!this.chess_game.isOver){
            return;
        }
        //enable all buttons again
        this.AI_button.setVisibility(View.VISIBLE);
        this.undo_button.setVisibility(View.VISIBLE);
        this.draw_button.setVisibility(View.VISIBLE);
        this.resign_button.setVisibility(View.VISIBLE);
        this.restart_button.setVisibility(View.GONE);
        this.record_button.setVisibility(View.GONE);

        undo_allowed = true;
        unhighlightTiles();

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                this.pieces[i][j].setEnabled(true);
                this.tiles[i][j].setEnabled(true);
            }
        }
        this.chess_game = new Chess();
        this.chess_game.initiateGame();
        this.white_promotion_type_spinner.setSelection(0);
        this.black_promotion_type_spinner.setSelection(0);
        this.applyChessBoard();
        this.result_text.setText("White's turn.");
        this.recorded_moves.setAdapter(new ArrayAdapter<>(this,R.layout.recorded_move,this.chess_game.moves.split("\n")));
    }
    private void record_click(){
        Bundle bundle = new Bundle();
        bundle.putString("moves",this.chess_game.moves);
        Intent intent = new Intent(this,AddGame.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private int[] convertTileNumberToIndices(String tile_number){
        int[] indices = new int[2];
        indices[0] = tile_number.charAt(0) - 'a';
        indices[1] = 7 - (tile_number.charAt(1) - '1');
        return indices;
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
    private void unhighlightTiles(){
        for(int i =0; i < this.highlighted.length; i++){
            for(int j = 0; j < this.highlighted[i].length; j++){
                this.highlighted[i][j] = false;
                if((i+j) % 2 == 0){//white tile
                    this.tiles[i][j].setBackground(getResources().getDrawable(R.drawable.white_tile,this.getTheme()));
                }
                else{//black tile
                    this.tiles[i][j].setBackground(getResources().getDrawable(R.drawable.black_tile,this.getTheme()));
                }
            }
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

        this.undo_button.setVisibility(View.GONE);
        this.AI_button.setVisibility(View.GONE);
        this.draw_button.setVisibility(View.GONE);
        this.resign_button.setVisibility(View.GONE);
        this.restart_button.setVisibility(View.VISIBLE);
        this.record_button.setVisibility(View.VISIBLE);
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                this.pieces[i][j].setEnabled(false);
                this.tiles[i][j].setEnabled(false);
            }
        }
    }
    private String processPromotionType(String promotion_type){
        if(promotion_type == null){
            return null;
        }
        if(promotion_type.equals("Queen")){
            return "Q";
        }
        else if(promotion_type.equals("Knight")){
            return "N";
        }
        else if(promotion_type.equals("Bishop")){
            return "B";
        }
        else{//Rook
            return "R";
        }
    }
}