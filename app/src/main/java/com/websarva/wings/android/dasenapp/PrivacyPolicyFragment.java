package com.websarva.wings.android.dasenapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class PrivacyPolicyFragment extends DialogFragment {

    private Button agreeButton;

    public static PrivacyPolicyFragment newInstance(String buttonType) {
        PrivacyPolicyFragment fragment = new PrivacyPolicyFragment();
        Bundle args = new Bundle();
        args.putString(FixedWords.BUTTON_TYPE, buttonType);
        fragment.setArguments(args);
        return fragment;
    }
    // TODO
//    public static Boolean isPolicyAgreed(Context context) {
//
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        addDialogLayout(dialog);
        return dialog;
    }

    private void addDialogLayout(Dialog dialog) {
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.fragment_privacy_policy);

        addButtonSetting(dialog);
        addLinkTextSetting(dialog);
    }

    private void addButtonSetting(Dialog dialog) {
        agreeButton = dialog.findViewById(R.id.agree_button);
        String buttonType = FixedWords.EMPTY;
        if (getArguments() != null) {
            buttonType = getArguments().getString(FixedWords.BUTTON_TYPE);
        }

        assert buttonType != null;
        changeButtonText(buttonType);
        addButtonListener(dialog, buttonType);
    }

    private void addLinkTextSetting(Dialog dialog) {
        TextView linkText = dialog.findViewById(R.id.link);
        linkText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void changeButtonText(String buttonType) {
        switch (buttonType) {
            case FixedWords.AGREE:
                agreeButton.setText(getResources().getString(R.string.agree));
                break;
            case FixedWords.CLOSE:
                agreeButton.setText(getResources().getString(R.string.close));
                break;
            default:
        }
    }
    private void addButtonListener(Dialog dialog, String buttonType) {
        agreeButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (buttonType.equals(FixedWords.AGREE)) agreePolicy();
                dialog.dismiss();
            }
        });
    }

    // TODO
    private void agreePolicy() {

    }
}