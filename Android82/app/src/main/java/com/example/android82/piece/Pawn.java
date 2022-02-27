package com.example.android82.piece;

import com.example.android82.Piece;
import java.util.ArrayList;

/**
 * Pawn is a subclass extending chess.Piece. It represents the Pawn pieces in a chess game.
 * 
 * @author Seok Yim, Mae Khaled
 *
 */
public class Pawn extends Piece {
    /**
     * Boolean variable that tells if Pawn has had its first move (starts as false)
     */
    public boolean hadFirstMove = false;

    /**
     * Indicates the turn on which first move was made
     */
    public int firstMoveTurn = -1;

    /**
     * int variable indicating on which turn(number) the Pawn made its two-step
     * move. Default value is -1, which means it is yet to make a two-step move.
     */
    public int twoStepTurnNumber = -1;

    /**
     * Constructor for Pawn.
     * 
     * @param cr    current row index
     * @param cc    current column index
     * @param color color of the Pawn
     */
    public Pawn(int cr, int cc, char color) {
        super(cr, cc, color);
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
        ArrayList<Integer[]> al = this.getAllPossibleMoves(chessboard, turn_number);
        for (Integer[] I : al) {
            if (I[0] == er && I[1] == ec) {
                qualifies = true;
                break;
            }
        }
        if (!qualifies) {
            return false;
        }

        boolean is_EnPassant = false;
        if (!this.canReach(er, ec, chessboard) && Math.abs(this.currentLocation[0] - er) == 1
        && Math.abs(this.currentLocation[1] - ec) == 1) {
            is_EnPassant = true;
        }

        if (!is_EnPassant) {
            if(Math.abs(this.currentLocation[1] - ec) == 0){
                //PAWNS CANNOT KILL THROUGH A STRAIGHT MOVE!!
                if(chessboard[er][ec] != null){
                    return false;
                }
            } 
            else {// <-- diagonal move!
                // checking whether the ending position has a piece and if so, whether that
                // piece is killable
                Piece ending_position = chessboard[er][ec];
                if (ending_position != null) {
                    if (this.color == ending_position.color) {
                        return false;
                    }
                }
            }


            // assume we made the move, and check whether its King is safe
            // temporarily make the move and revert back later
            int sr = this.currentLocation[0];
            int sc = this.currentLocation[1];
            Piece temp = chessboard[er][ec];
            chessboard[er][ec] = this;
            this.setCurrentLocation(er, ec);
            chessboard[sr][sc] = null;

            if (leavesKingChecked(chessboard, this.color)) {
                chessboard[sr][sc] = this;
                chessboard[er][ec] = temp;
                this.setCurrentLocation(sr, sc);
                return false;
            } else {
                chessboard[sr][sc] = this;
                chessboard[er][ec] = temp;
                this.setCurrentLocation(sr, sc);
                return true;
            }
        } else {// is En Passant case
            int sr = this.currentLocation[0];
            int sc = this.currentLocation[1];
            Pawn captured_pawn = (Pawn)chessboard[sr][ec];
            chessboard[sr][ec] = null;
            chessboard[er][ec] = this;
            chessboard[sr][sc] = null;

            if(leavesKingChecked(chessboard, this.color)){
                chessboard[sr][sc] = this;
                chessboard[sr][ec] = captured_pawn;
                chessboard[er][ec] = null;
                return false;
            }
            else{
                chessboard[sr][sc] = this;
                chessboard[sr][ec] = captured_pawn;
                chessboard[er][ec] = null;
                return true;
            }
        }
    }

    /**
     * Moves Piece to a specific location. Assumes that the move to be made is valid (
     * does no condition checking).
     * 
     * @param er    ending/final row index after move
     * @param ec    ending/final column index after move
     */
    public void move(int er, int ec) {
        super.move(er, ec);
        this.hadFirstMove = true;
    }

    /**
     * Checks whether the specified enemy Piece is in a diagonal position from the
     * Pawn. Here, the diagonal position means "in the front row of the pawn from
     * the player's perspective" and also having exactly 1 row and column diffeence.
     * 
     * @param chessboard Piece[][] array
     * @param er         enemy row index
     * @param ec         enemy column index
     * @return true if enemy exists and is at the specified diagonal position from
     *         Piece, false otherwise
     */
    private boolean hasSpecificEnemyInDiag(Piece[][] chessboard, int er, int ec) {
        int row_adder = 1;
        if (this.color == 'w') {
            row_adder = -1;
        }

        if (this.currentLocation[0] + row_adder < 0 || this.currentLocation[0] + row_adder > 7) {
            // on first or last row
            return false;
        }

        // check whether the location of the given coordinates is in diag position
        if (this.currentLocation[0] + row_adder != er) {
            return false;
        }
        if (this.currentLocation[1] + 1 != ec && this.currentLocation[1] - 1 != ec) {
            return false;
        }

        // check whether enemy Piece exists at the specified location
        Piece ep = chessboard[er][ec];
        if (ep == null) {
            return false;
        } else {
            if (ep.color == this.color) {
                return false;
            } else {
                return true;
            }
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
    public boolean canReach(int er, int ec, Piece[][] chessboard) {
        // check if move is within the piece's capability
        int row_diff = Math.abs(this.currentLocation[0] - er);
        int col_diff = Math.abs(this.currentLocation[1] - ec);
        if (this.color == 'w') {
            if (!((this.currentLocation[0] - er == 1 && col_diff == 0)
                    || (hasSpecificEnemyInDiag(chessboard, er, ec) && col_diff == 1
                            && (this.currentLocation[0] - er == 1))
                    || (!this.hadFirstMove && this.currentLocation[0] - er == 2 && col_diff == 0))) {
                return false;
            }
        } else {
            if (!((er - this.currentLocation[0] == 1 && col_diff == 0)
                    || (hasSpecificEnemyInDiag(chessboard, er, ec) && col_diff == 1
                            && (er - this.currentLocation[0] == 1))
                    || (!this.hadFirstMove && er - this.currentLocation[0] == 2 && col_diff == 0))) {
                return false;
            }
        }

        // check if the movement gets blocked
        int sr = this.currentLocation[0];
        int sc = this.currentLocation[1];

        int r_adder;
        if (col_diff == 0) {// moving straight; cannot kill through this move
            if (row_diff == 2) {
                if (sr < er) {
                    r_adder = 1;
                } else {
                    r_adder = -1;
                }
                sr += r_adder;

                while (sr != er) {
                    if (chessboard[sr][sc] != null) {
                        return false;
                    }
                    sr += r_adder;
                }
            }
            //PAWNS CAN'T KILL MOVING STRAIGHT
            if(chessboard[er][ec] != null){
                return false;
            }
        } else {// diagonal move
            return true;
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
    public ArrayList<Integer[]> getAllPossibleMoves(Piece[][] chessboard, int turn_number) {
        int sr = this.currentLocation[0];
        int sc = this.currentLocation[1];
        ArrayList<Integer[]> rl = new ArrayList<>();

        // check if move is within the piece's capability
        // 1. Regular Move (one step or two steps forward)
        int r_adder;
        if (this.color == 'w') {
            r_adder = -1;
        } else {
            r_adder = 1;
        }
        if (sr + r_adder >= 0 && sr + r_adder <= 7) {
            rl.add(new Integer[] { sr + r_adder, sc });
        }

        if (!this.hadFirstMove) {
            if (sr + r_adder * 2 >= 0 && sr + r_adder * 2 <= 7) {
                rl.add(new Integer[] { sr + r_adder * 2, sc });
            }
        }

        // 2. Dianoal move to kill enemey
        if (sr + r_adder >= 0 && sr + r_adder <= 7 && sc - 1 >= 0) {
            Piece temp_piece = chessboard[sr + r_adder][sc - 1];
            if (temp_piece != null && temp_piece.color != this.color) {
                rl.add(new Integer[] { sr + r_adder, sc - 1 });
            }
        }
        if (sr + r_adder >= 0 && sr + r_adder <= 7 && sc + 1 <= 7) {
            Piece temp_piece = chessboard[sr + r_adder][sc + 1];
            if (temp_piece != null && temp_piece.color != this.color) {
                rl.add(new Integer[] { sr + r_adder, sc + 1 });
            }
        }

        // 3. En Passant
        int En_Passant_row = 4;
        int[] c_adders = { 1, -1 };
        if (this.color == 'w') {
            En_Passant_row = 3;
        }
        if (sr == En_Passant_row) {
            for (int i = 0; i < c_adders.length; i++) {
                if (sc + c_adders[i] >= 0 && sc + c_adders[i] <= 7) {
                    Piece neighbor_piece = chessboard[sr][sc + c_adders[i]];
                    if (neighbor_piece != null && neighbor_piece.color != this.color
                            && neighbor_piece instanceof Pawn) {
                        Pawn neighbor_pawn = (Pawn) neighbor_piece;
                        if (neighbor_pawn.twoStepTurnNumber == turn_number - 1) {
                            if (chessboard[sr + r_adder][sc + c_adders[i]] == null) {// can't kill two pieces at once in chess
                                rl.add(new Integer[] { sr + r_adder, sc + c_adders[i] });
                            }
                        }
                    }
                }
            }
        }

        return rl;
    }

    /**
     * Overrides the toString method from its superclass.
     * 
     * @return a string representing piece information (color and type)
     */
    public String toString() {
        return this.color + "p";
    }
}
