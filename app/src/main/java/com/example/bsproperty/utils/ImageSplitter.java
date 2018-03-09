package com.example.bsproperty.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.bsproperty.bean.ImagePiece;

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

        int pieceWidth = width / piece;
        int pieceHeight = height / piece;
        for (int i = 0; i < piece; i++) {
            for (int j = 0; j < piece; j++) {
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
}
