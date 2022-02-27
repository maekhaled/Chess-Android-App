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

import java.io.IOException;


public class AddGameDialogFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState){
        Bundle bundle = getArguments();
        String message = bundle.getString("message");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Ok",(dialog,i)->{ });
        return builder.create();
    }
}