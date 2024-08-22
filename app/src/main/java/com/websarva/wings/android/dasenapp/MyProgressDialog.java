package com.websarva.wings.android.dasenapp;

import static android.app.PendingIntent.getActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyProgressDialog extends DialogFragment {
    // Static method to create a new instance of ProgressDialog with a message
    public static MyProgressDialog newInstance(String message) {
        MyProgressDialog instance = new MyProgressDialog();
        Bundle arguments = new Bundle();
        arguments.putString("message", message);
        instance.setArguments(arguments);
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Retrieve the message from arguments
        String mMessage = getArguments().getString("message");

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.my_progress_dialog, null);

        // Set the message to the TextView
        TextView mMessageTextView = view.findViewById(R.id.my_progress_message);
        mMessageTextView.setText(mMessage);

        // Set the view to the AlertDialog and return the created dialog
        builder.setView(view);
        return builder.create();
    }
}
