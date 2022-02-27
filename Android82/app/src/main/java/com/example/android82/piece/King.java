package com.example.android82.piece;

import com.example.android82.Piece;
import java.util.ArrayList;

/**
 * King is a subclass extending chess.Piece. It represents King pieces in a chess game.
 * 
 * @author Seok Yim, Mae Khaled
 *
 */
public class King extends Piece{

	/**
	 * Denotes whether this King piece has already made its first move or not
	 */
    public boolean hadFirstMove = false;
    /**
     * Denotes whether this King piece is currently checked or not
     */
    public boolean isInChcek = false;

    public int firstMoveTurn = -1;

    /**
     * Constructor for King.
     * 
     * @param cr    current row index
     * @param cc    current column index
     * @param color color of the King
     */
    public King(int cr, int cc, char color){
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
        boolean qualifies = false;
        ArrayList<Integer[]> al = this.getAllPossibleMoves(chessboard,turn_number);
        for(Integer[] I : al){
            if(I[0] == er && I[1] == ec){
                qualifies = true;
                break;
            }
        }
        if(!qualifies){
            return false;
        }
        
        boolean is_castling = false;
        if(!this.canReach(er, ec, chessboard)){
            is_castling = true;
        }

        if(!is_castling){
            //checking whether the ending position has a piece and if so, whether that piece is killable
            Piece ending_position = chessboard[er][ec];
            if(ending_position != null){
                if(this.color == ending_position.color){
                    return false;
                }
            }
            //assume we made the move and check whether its King is safe
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
        else{
            return true;
        }
    }

    /**
     * Moves Piece to a specific location. Assumes that the move to be made is valid (
     * does no condition checking).
     * 
     * @param er    ending/final row index after move
     * @param ec    ending/final column index after move
     */
    public void move(int er, int ec){
        super.move(er,ec);
        this.hadFirstMove = true; 
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
        if(!(row_diff <= 1 && col_diff <= 1)){
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
        
        int[][] adders = new int[][]{{1,1},{1,0},{1,-1},{0,1},{0,-1},{-1,1},{-1,0},{-1,-1}};
        for(int i = 0; i < adders.length; i++){
            sr = this.currentLocation[0] + adders[i][0];
            sc = this.currentLocation[1] + adders[i][1];
            if(sr >= 0 && sr <= 7 && sc >= 0 && sc <= 7){
                rl.add(new Integer[]{sr,sc});
            }
        }
        
        if(!this.hadFirstMove && !this.isInChcek){//takes into consideration CASTLING
            int[][] temp_arr = {{7,7},{7,0}};
            if(this.color == 'b'){
                temp_arr = new int[][]{{0,0},{0,7}};
            }
            for(int i = 0; i < temp_arr.length; i++){
                Piece temp_rook = chessboard[temp_arr[i][0]][temp_arr[i][1]];
                if(temp_rook != null  && temp_rook instanceof Rook){
                    Rook real_rook = (Rook)temp_rook;
                    if(!real_rook.hadFirstMove && this.blank_inbetween(this, real_rook, chessboard)){
                        //when attempting to castle, you cannot pass through check
                        int add_amount = 1;
                        if(this.currentLocation[1] > real_rook.currentLocation[1]){
                            add_amount = -1;
                        }
                        int or = this.currentLocation[0];
                        int oc = this.currentLocation[1];
                        sr = or;
                        sc = oc;
                        int counter = 0;
                        
                        boolean passes_through_check = false;
                        while(counter < 2){
                            chessboard[sr][sc] = null;
                            sc += add_amount;
                            this.setCurrentLocation(sr, sc);
                            chessboard[sr][sc] = this;
                            if(leavesKingChecked(chessboard, this.color)){
                                passes_through_check = true;
                                break;
                            }
                            counter++;
                        }

                        if(!passes_through_check){
                            rl.add(new Integer[]{sr,sc});
                        }
                        this.setCurrentLocation(or, oc);
                        chessboard[sr][sc] = null;
                        chessboard[or][oc] = this;
                    }
                }
            }
        }
        return rl;
    }
    
    /**
     * Checks whether there are no Pieces in between two Pieces.
     * Assumes that the two Pieces given are in the same row.
     * 
     * @param a				Piece 1
     * @param b				Piece 2
     * @param chessboard	Piece[][] chessboard
     * @return				true if no Pieces are in between Piece 1 and 2, false otherwise
     */
    private boolean blank_inbetween(Piece a, Piece b, Piece[][] chessboard){//Assumes a and b share the same row index
        int row = a.currentLocation[0];

        int lower, higher;
        if(a.currentLocation[1] < b.currentLocation[1]){
            lower = a.currentLocation[1];
            higher = b.currentLocation[1];
        }
        else{
            lower = b.currentLocation[1];
            higher = a.currentLocation[1];
        }
        
        for(int i = lower + 1; i < higher; i++){
            if(chessboard[row][i] != null){
                return false;
            }
        }
        return true;
    }
    /**
     * Overrides the toString method from its superclass.
     * 
     * @return  a string representing piece information (color and type)
     */
    public String toString(){
        return this.color + "K";
    }
}
