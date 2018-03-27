package com.example.bsproperty.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;

import com.example.bsproperty.bean.ImagePiece;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2018/3/9.
 */

public class ImageSplitter {

    public static List<ImagePiece> split(Bitmap bitmap, int piece) {

        List<ImagePiece> pieces = new ArrayList<ImagePiece>(piece * piece);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int loop = width / piece;

        int pieceWidth = piece;
        int pieceHeight = piece;
        for (int i = 0; i < loop; i++) {
            for (int j = 0; j < loop; j++) {
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.index = j + i * piece;
                int xValue = j * pieceWidth;
                int yValue = i * pieceHeight;
                imagePiece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
                        pieceWidth, pieceHeight);
                pieces.add(imagePiece);
            }
        }

        return pieces;
    }

    public static Bitmap scaleImg(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int newWidth = 256;
        int newHeight = 256;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    public static String saveFile(Context context,Bitmap bitmap,String name) {

        String PHOTO_DIR = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
        File file = new File(PHOTO_DIR, name+".png");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), name, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
