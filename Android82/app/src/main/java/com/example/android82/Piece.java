package com.example.android82;

import com.example.android82.piece.*;
import java.util.ArrayList;

/**
 * Piece is an abstract class that is part of a Board (for chess). A Piece has a current location on 
 * the board of a specific size, and it has its location set as {-1,- 1} when it is killed
 * (moved out of the board). A Piece is an abstract superclass for all the actual chess 
 * Pieces, such as Pawn, Knight, etc. 
 * 
 * @author Seok Yim, Mae Khaled
 */
public abstract class Piece {
    /**
     * This is the current location for the Piece. CurrentLocation[0] is the row index, and
     * CurrentLocation[1] is the column index.
     */
    public int[] currentLocation;
    /**
     * This is the color of the Piece. It has a value of either 'w' or 'b', which stand for 
     * white and black, respectively.
     */
    public char color;

    /**
     * The one and only constructor for Piece. Initializes currentLocation and color.
     * 
     * @param cr    current row index
     * @param cc    current column index
     * @param color color of the Piece
     */
    public Piece(int cr, int cc, char color){
        this.currentLocation = new int[]{cr,cc};
        this.color = color;
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
    public abstract boolean isValidMove(int er, int ec, Piece[][] chessboard, int turn_number);

    /**
     * Moves Piece to a specific location. Assumes that the move to be made is valid (
     * does no condition checking).
     * 
     * @param er    ending/final row index after move
     * @param ec    ending/final column index after move
     */
    public void move(int er, int ec){
        setCurrentLocation(er, ec);
    }

    /**
     * Promotes Piece to Pawn to specified Piece type. (Only applicable to Pawns)
     * 
     * @param promoType Piece to promote Pawn to
     * @return          Piece that Pawn was promoted to
     */
    public Piece promote(String promoType){
        switch(promoType){
            case "B":
                return new Bishop(this.currentLocation[0], this.currentLocation[1], this.color);
            case "N":
                return new Knight(this.currentLocation[0], this.currentLocation[1], this.color);
            case "R":
                return new Rook(this.currentLocation[0], this.currentLocation[1], this.color);
            case "Q":
                return new Queen(this.currentLocation[0], this.currentLocation[1], this.color);
            default:
                return new Queen(this.currentLocation[0], this.currentLocation[1], this.color);
        }
    }

    /**
     *  Sets the current location for Piece. 
     * 
     * @param cr    current row index
     * @param cc    current column index
     */
    public void setCurrentLocation(int cr, int cc){
        this.currentLocation = new int[]{cr,cc};
    }

    /**
     * Kills the Piece. Here, kill means removing the Piece from the board by settings 
     * its current location to {-1,-1}.
     */
    public void kill(){
        this.currentLocation = new int[]{-1,-1};
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
    public abstract boolean canReach(int er, int ec, Piece[][] chessboard);

    /**
     * Checks whether a specific move leaves its own team's king checked. This method should
     * only be called when the move is deemed to be valid in any other way(satisfying a piece specific requirement,
     * not killing its own teammate, a Pawn not jumping over other piece, etc.). All
     * subclasses of Piece should call this method inside their isValidMove() method.
     * 
     * @param color         either 'w' or 'b', denoting whose king is being observed for their safety
     * @param chessboard    Piece[][] chessboard
     * @return				true if a king of the specified color is checked, false otherwise
     */
    public static boolean leavesKingChecked(Piece[][] chessboard, char color){
        Piece king = findKing(chessboard, color);
        int dest_row = king.currentLocation[0];
        int dest_col = king.currentLocation[1];

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(chessboard[i][j] != null && chessboard[i][j].color != color 
                && chessboard[i][j].canReach(dest_row, dest_col, chessboard)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds the King for the specified color.
     * 
     * @param chessboard    Piece[][] chessboard
     * @param color         color of King
     * @return				King of the specified color
     */
    public static King findKing(Piece[][] chessboard, char color){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(chessboard[i][j] instanceof King && chessboard[i][j].color == color){
                    return (King)chessboard[i][j];
                }
            }
        }
        //shouldn't reach here the King is alive
        return null;
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
    public abstract ArrayList<Integer[]> getAllPossibleMoves(Piece[][] chessboard, int turn_number);

    /**
     * Returns ACTUALLY POSSIBLE MOVES (Takes into consideration being blocked by pieces)
     *
     * @param chessboard    Piece[][] chessboard
     * @param turn_number   the number of turns passed since the start of the game
     * @return              the ArrayList of Integer[] of length 2 containing the row and column indices of the final location for the possible move
     */
    public ArrayList<Integer[]> getActuallyPossibleMoves(Piece[][] chessboard, int turn_number, char turn){
        ArrayList<Integer[]> possible_moves = new ArrayList<>();
        if(turn != this.color){
            return possible_moves;
        }
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(i == this.currentLocation[0] && j == this.currentLocation[1]){
                    continue;//a piece cannot not move
                }
                if(this.isValidMove(i,j,chessboard,turn_number)){
                    if(!(chessboard[i][j] != null && chessboard[i][j].color == this.color)){
                        possible_moves.add(new Integer[]{i,j});
                    }
                }
            }
        }
        return possible_moves;
    }
}
