package com.websarva.wings.android.dasenapp;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class RecommendAppFragment extends DialogFragment {

    private Button closeButton;

    public static RecommendAppFragment newInstance() {
        return new RecommendAppFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        addDialogLayout(dialog);
        return dialog;
    }

    private void addDialogLayout(Dialog dialog) {
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.fragment_recommend_app); //

        addButtonSetting(dialog);
        addLinkTextSetting(dialog);
    }

    private void addButtonSetting(Dialog dialog) {
        closeButton = dialog.findViewById(R.id.recommendation_close_button);
        addButtonListener(dialog);
    }

    private void addLinkTextSetting(Dialog dialog) {
        TextView linkText = dialog.findViewById(R.id.recommendation_app_link);

        linkText.setLinkTextColor(Color.parseColor(FixedWords.COLOR_BLUE));
        linkText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void addButtonListener(Dialog dialog) {
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
