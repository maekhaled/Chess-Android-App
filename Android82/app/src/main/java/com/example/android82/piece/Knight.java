package com.example.android82.piece;

import java.util.ArrayList;

import com.example.android82.Piece;


/**
 * Knight is a subclass of chess.Piece. It represents Knight pieces in a chess game.
 * 
 * @author Seok Yim, Mae Khaled
 *
 */
public class Knight extends Piece{
        /**
     * Constructor for Knight.
     * 
     * @param cr    current row index
     * @param cc    current column index
     * @param color color of the Knight
     */
    public Knight(int cr, int cc, char color){
        super(cr,cc,color);
    }

    /**
     * Checks whether a specific move for a Piece
     * is valid. Assumes the move is not repeating the current Piece location.
     * Assumes that er and ec are between 0 and 7, inclusive on both sides.
     * 
     * @param er    		ending/final row index for this move
     * @param ec    		ending/final column index for this move
     * @param chessboard	Piece[][] chessboard
     * @param turn_number	the number of turns passed since the start of game
     * @return      		true if is a valid move, false otherwise
     */
    public boolean isValidMove(int er, int ec, Piece[][] chessboard, int turn_number) {
        if(!this.canReach(er, ec, chessboard)){
            return false;
        }
        //checking whether the ending position has a piece and if so, whether that piece is killable
        Piece ending_position = chessboard[er][ec];
        if(ending_position != null){
            if(this.color == ending_position.color){
                return false;
            }
        }

        //assume we made the move, and check whether its King is safe
        //temporarily make the move and revert back later
        int sr = this.currentLocation[0];
        int sc = this.currentLocation[1];
        Piece temp = chessboard[er][ec];
        chessboard[er][ec] = this;
        this.setCurrentLocation(er, ec);
        chessboard[sr][sc] = null;

        if(leavesKingChecked(chessboard, this.color)){
            chessboard[sr][sc] = this;
            chessboard[er][ec] = temp;
            this.setCurrentLocation(sr, sc);
            return false;
        }
        else{
            chessboard[sr][sc] = this;
            chessboard[er][ec] = temp;
            this.setCurrentLocation(sr, sc);
            return true;
        }
    }
    /**
     * Determines whether a Piece can reach a specific location to kill an enemy
     * if the enemy were to be there.
     * 
     * @param er            ending row index
     * @param ec            ending column index
     * @param chessboard    Piece[][] chessboard
     * @return				true if the piece can reach the specified location to possibly kill an enemy, false otherwise
     */
    public boolean canReach(int er, int ec, Piece[][] chessboard){
        //check if move is within the piece's capability
        int row_diff = Math.abs(this.currentLocation[0] - er);
        int col_diff = Math.abs(this.currentLocation[1] - ec);
        if(!((row_diff == 1 && col_diff == 2) || (row_diff == 2 && col_diff == 1))){
            return false;
        }

        return true;
    }
    /**
     * Returns all possible moves that a specific piece is capable of making.
     * The returned possible moves might be blocked by other pieces (so they could be
     * considered invalid), but they are ensured to be moves within the board.
     * 
     * @param chessboard	Piece[][] chessboard
     * @param turn_number	the number of turns passed since the start of the game
     * @return				the ArrayList of Integer[] of length 2 containing the row and column indices of the final location for the possible move
     */
    public ArrayList<Integer[]> getAllPossibleMoves(Piece[][] chessboard, int turn_number){
        int sr, sc;
        ArrayList<Integer[]> rl = new ArrayList<>();
        
        int[][] adders = new int[][]{{1,2},{1,-2},{2,1},{2,-1},{-1,2},{-1,-2},{-2,1},{-2,1}};
        for(int i = 0; i < adders.length; i++){
            sr = this.currentLocation[0] + adders[i][0];
            sc = this.currentLocation[1] + adders[i][1];
            if(sr >= 0 && sr <= 7 && sc >= 0 && sc <= 7){
                rl.add(new Integer[]{sr,sc});
            }
        }
        
        return rl;
    }
    /**
     * Overrides the toString method from its superclass.
     * 
     * @return  a string representing piece information (color and type)
     */
    public String toString(){
        return this.color + "N";
    }
}
