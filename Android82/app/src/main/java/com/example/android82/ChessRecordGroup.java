package com.example.android82;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ChessRecordGroup implements Serializable {

    static final long serialVersionUID = 1L;

    public ArrayList<ChessRecord> chessRecords;

    public ChessRecordGroup(){
        this.chessRecords = new ArrayList<>();
    }

    public static void writeApp(ChessRecordGroup crg, String filePath) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
        oos.writeObject(crg);
    }
    public static ChessRecordGroup readApp(String filePath) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
        ChessRecordGroup crg = (ChessRecordGroup)ois.readObject();
        return crg;
    }
}
