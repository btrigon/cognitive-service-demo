package com.example.facetest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Benjamin on 15/05/16.
 */
public class ImageUtils {


    public static synchronized Bitmap getCroppedBitmap(Bitmap image, int startX, int startY, int width, int height, int paddingPercent)
        throws IllegalArgumentException
    {
        // check parameters are valid bounds
        if(image == null || image.getWidth()<=0 || image.getHeight()<=0
                ||  (startX+width) > image.getWidth() || (startY+height) > image.getHeight()
                || paddingPercent < 0 || paddingPercent > 100){
            throw new IllegalArgumentException("bad arguments");
        }

  //      int paddingPercentY = paddingPercent * 2;

        int newStartX = startX - (width * paddingPercent / 200 );  // half of padding on left
        if(newStartX<0) newStartX = 0;

        int newStartY = startY - (height * paddingPercent / 150); // 75% of padding on top
        if(newStartY<0) newStartY = 0;

        int newWidth = width + (width * paddingPercent / 100);
        if(startX + newWidth > image.getWidth()){
            // outside bounds
            newWidth = image.getWidth() - startX; // from start to right edge
        }

        int newHeight = height + (height * paddingPercent / 100);
        if(startY + newHeight > image.getHeight()){
            // outside bounds
            newHeight = image.getHeight() - startY; // from start to bottom edge
        }

        return Bitmap.createBitmap(image, newStartX, newStartY, newWidth, newHeight);
    }

    public static synchronized void resizeStoredCameraImage(File file){
        Log.d("ImageUtils", "resizeStoredCameraImage: " + file.toString());

        ExifInterface ei = null;
        int orientation = 0;
        try {
            ei = new ExifInterface(file.getAbsolutePath());
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                default: orientation = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        if(bitmap==null){
            Log.e("ImageUtils", "bitmap was null for: " + file.getAbsolutePath());
            return;
        }

        ImageDimens imageDimens = getScaledDimens(bitmap);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, imageDimens.getWidth(), imageDimens.getHeight(), false);
        Log.d("ImageUtils", "resizeStoredCameraImage orientation: " + orientation);
        Bitmap rotatedBitmap =  rotateImageIfRequired(scaledBitmap, orientation);

        try {
            file.delete(); // remove original file
            file.createNewFile(); //added
            OutputStream fileStream = new BufferedOutputStream(
                    new FileOutputStream(file));
            fileStream.write(bitmapToBytes(rotatedBitmap));
            fileStream.close();

            bitmap.recycle();
            scaledBitmap.recycle();
            rotatedBitmap.recycle();
            Log.d("ImageUtils", "storeScaledImage, written:  " + file.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // convert Bitmap to bytes
    //  synchronized - allow one running instance to reduce memory usage
    private static synchronized  byte[] bitmapToBytes(Bitmap photo) {
        //     Log.d(DEBUG_TAG, "bitmapToBytes start " + photo.toString() );
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        //     Log.d(DEBUG_TAG, "bitmapToBytes finish " + photo.toString() );
        return byteArray;
    }

    /**
     * Rotate an image if required.
     * @param img
     * @param rotation
     * @return
     */
    private static Bitmap rotateImageIfRequired(Bitmap img, int rotation) {

        if(rotation!=0){
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        }else{
            return img;
        }
    }

    private static class ImageDimens {

        private int width;
        private int height;


        public ImageDimens(int width, int height) {
            this.height = height;
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }

    /**
     *
     * @param bitmap
     * @return dimensions with long side set to 1440
     */
    private static ImageDimens getScaledDimens(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d("ImageUtils", "before scaled w: " + width + "   height: " + height);

        if(width>1440 || height>1440) {
            // need to scale down
            if (width > height) {
                // landscape
                float ratio = (float)width / 1440f;
                Log.d("ImageUtils", "ratio: " + ratio);
                width = 1440;
                height = (int) (height / ratio);
            } else if (height >= width) {
                // portrait
                float ratio = (float)height / 1440f;
                Log.d("ImageUtils", "ratio: " + ratio);
                height = 1440;
                width = (int) (width / ratio);
            }
        }
        Log.d("ImageUtils", "scaled w: " + width + "   height: " + height);
        return new ImageDimens(width, height);
    }

}
