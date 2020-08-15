package com.websarva.wings.android.dasenapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ShareCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class Sharing {

    private Context mContext;
    private Activity mActivity;
    private View mView;

    public Sharing(Context context, Activity activity, View view) {
        mContext = context;
        mActivity = activity;
        mView = view;
    }

    private Bitmap takeScreenShot(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();

        if (bgDrawable != null) bgDrawable.draw(canvas);
        else canvas.drawColor(Color.WHITE);

        view.draw(canvas);
        return returnedBitmap;
    }

    private String saveImage(Bitmap bitmap) {
        String fileName = "order.jpg";
        String errorMassage = "エラー発生";
        try {
            String directory = Objects.requireNonNull(mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).toString();
            File folder = new File(directory);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File imageFile = new File(folder, fileName);
            imageFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();

            return directory + "/" + fileName;
        } catch (Exception e) {
            Toast.makeText(mContext, errorMassage, Toast.LENGTH_SHORT).show();
        }
        return FixedWords.EMPTY;
    }

    private void shareCompat(Uri imagePath) {
        String dataType = "image/jpeg";
        int meaningLessNum = 1;
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(mActivity);
        builder.setChooserTitle(FixedWords.EMPTY)
                .setText(getTweetMessage())
                .setStream(imagePath)
                .setType(dataType);

        Intent intent = builder.createChooserIntent().addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mActivity.startActivityForResult(intent, meaningLessNum);
    }

    private String getTweetMessage() {
        String message1 = "スタメンを作成しました！";
        String androidTab = "Android: ";
        String googlePlayUrl = "bit.ly/2Dqbg6M";
        String hashTagMessage = "#野球スタメン作成アプリ";
        String nextLine = "\n";
        return message1 + nextLine
                + androidTab + googlePlayUrl + nextLine + nextLine
                + hashTagMessage;
    }

    public void share() {
        Bitmap bitmap = takeScreenShot(mView);
        String uriString = saveImage(bitmap);
        if (!uriString.equals(FixedWords.EMPTY)) shareCompat(Uri.parse(uriString));
    }
}
