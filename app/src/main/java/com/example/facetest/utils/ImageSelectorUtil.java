package com.example.facetest.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.facetest.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin on 15/05/16.
 */
public class ImageSelectorUtil {

    public static final String BASE_DIR = "FaceScanPro";

    /**
     * Detect the available intent and open a new dialog.
     * @param context
     */
    public static void openMediaSelector(Activity context, String imageName){

        Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        Intent gallIntent=new Intent(Intent.ACTION_GET_CONTENT);
        gallIntent.setType("image/*");

        // look for available intents
        List<ResolveInfo> info=new ArrayList<>();
        List<Intent> yourIntentsList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(camIntent, 0);
        for (ResolveInfo res : listCam) {
            final Intent finalIntent = new Intent(camIntent);
            finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            yourIntentsList.add(finalIntent);
            info.add(res);
        }
        List<ResolveInfo> listGall = packageManager.queryIntentActivities(gallIntent, 0);
        for (ResolveInfo res : listGall) {
            final Intent finalIntent = new Intent(gallIntent);
            finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            yourIntentsList.add(finalIntent);
            info.add(res);
        }

        // show available intents
        openDialog(context,yourIntentsList,info, imageName);
    }

    public static void openCameraSelector(Activity context, String imageName){
        Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        // look for available intents
        List<ResolveInfo> info=new ArrayList<>();
        List<Intent> yourIntentsList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(camIntent, 0);
        for (ResolveInfo res : listCam) {
            final Intent finalIntent = new Intent(camIntent);
            finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            yourIntentsList.add(finalIntent);
            info.add(res);
        }
        // show available intents
        openDialog(context,yourIntentsList,info, imageName);

    }

    public static void openDefaultCamera(Activity context, String imageName){
        Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        // look for available intents
        List<ResolveInfo> info=new ArrayList<>();
        List<Intent> yourIntentsList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(camIntent, 0);
        for (ResolveInfo res : listCam) {
            final Intent finalIntent = new Intent(camIntent);
            finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            yourIntentsList.add(finalIntent);
            info.add(res);
        }
        File baseDir = new File(Environment.getExternalStorageDirectory()+File.separator +
                BASE_DIR  );
        if(!baseDir.exists()) {
            baseDir.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory()+File.separator +
                BASE_DIR + File.separator + imageName + ".jpg");
        yourIntentsList.get(0).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        context.startActivityForResult(yourIntentsList.get(0),1);
    }

    public static void openFileSelector(Activity context, String imageName){
        Intent gallIntent=new Intent(Intent.ACTION_GET_CONTENT);
        gallIntent.setType("image/*");

        // look for available intents
        List<ResolveInfo> info=new ArrayList<>();
        List<Intent> yourIntentsList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> listGall = packageManager.queryIntentActivities(gallIntent, 0);
        for (ResolveInfo res : listGall) {
            final Intent finalIntent = new Intent(gallIntent);
            finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            yourIntentsList.add(finalIntent);
            info.add(res);
        }

        // show available intents
        openDialog(context,yourIntentsList,info, imageName);
    }

    /**
     * Open a new dialog with the detected items.
     *
     * @param context
     * @param intents
     * @param activitiesInfo
     */
    private static void openDialog(final Activity context, final List<Intent> intents,
                                   List<ResolveInfo> activitiesInfo, final String imageName) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Select");
        dialog.setAdapter(buildAdapter(context, activitiesInfo),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = intents.get(id);
                        File baseDir = new File(Environment.getExternalStorageDirectory()+File.separator +
                                BASE_DIR  );
                        if(!baseDir.exists()) {
                            baseDir.mkdirs();
                        }
                        File file = new File(Environment.getExternalStorageDirectory()+File.separator +
                                BASE_DIR + File.separator + imageName + ".jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        context.startActivityForResult(intent,1);

                    }
                });

        dialog.setNeutralButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }


    /**
     * Build the list of items to show using the intent_listview_row layout.
     * @param context
     * @param activitiesInfo
     * @return
     */
    private static ArrayAdapter<ResolveInfo> buildAdapter(final Context context,final List<ResolveInfo> activitiesInfo) {
        return new ArrayAdapter<ResolveInfo>(context, R.layout.intent_listview_rowitem,R.id.title,activitiesInfo){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ResolveInfo res=activitiesInfo.get(position);
                ImageView image=(ImageView) view.findViewById(R.id.icon);
                image.setImageDrawable(res.loadIcon(context.getPackageManager()));
                TextView textview=(TextView)view.findViewById(R.id.title);
                textview.setText(res.loadLabel(context.getPackageManager()).toString());
                return view;
            }
        };
    }

    public static String getRealPathFromURI(Uri contentURI, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }


    }

    public static void deleteFile(Context context, String fileName ){

        File file = new File(fileName);
        boolean deleted = file.delete();
        Log.d("DeleteFile", "deleted: " + deleted);

    }

}
