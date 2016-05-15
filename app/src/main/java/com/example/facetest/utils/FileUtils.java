package com.example.facetest.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by Benjamin on 15/05/16.
 */
public class FileUtils {

    /**
     * Convert bitmap to byte array
     * @param bitmap
     * @return byte[] of bitmap
     */
    public static byte[] toBinary(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }


}
