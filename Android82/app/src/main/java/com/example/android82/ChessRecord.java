package com.example.android82;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChessRecord implements Serializable {
    public String game_title;
    public LocalDateTime game_date_time;
    public String[] moves;

    public ChessRecord(String game_title,String[] moves){
        this.game_title = game_title;
        this.game_date_time = LocalDateTime.now();
        this.moves = moves;
    }
    public String toString(){
        return "Title: " + this.game_title + "\nDate: " + this.game_date_time.toString().split("T")[0] + "  " + this.game_date_time.toString().split("T")[1];
    }
}
