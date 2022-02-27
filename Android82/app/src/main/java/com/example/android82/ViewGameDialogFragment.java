package com.example.android82;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ViewGameDialogFragment extends DialogFragment {
    private int position;
    private String dat_filePath;
    private ChessRecordGroup crg;

    public Dialog onCreateDialog(Bundle savedInstanceState){
        Bundle bundle = getArguments();
        this.position = bundle.getInt("position");
        this.dat_filePath = bundle.getString("filePath");

        try {
            this.crg = ChessRecordGroup.readApp(dat_filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this Game Record?")
                .setPositiveButton("Yes",(dialog,i)->{
                    this.crg.chessRecords.remove(this.position);
                    try {
                        ChessRecordGroup.writeApp(this.crg,this.dat_filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(this.getContext(),"Successfully deleted Game Record",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this.getContext(),ViewGames.class));})
                .setNegativeButton("No",(dialog,i)->{/* do nothing */});
        return builder.create();
    }
}