package com.cardreaderapp.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.io.FileUtils;

import static android.content.Context.MODE_APPEND;

final class ImgTarget implements Target {

    private Uri uri;

    public Context ctx;

    public String Name;
    @Override
    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            FileOutputStream fos = null;
            try {
                fos = ctx.openFileOutput(Name, MODE_APPEND);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void onBitmapFailed(Drawable drawable) {
        Log.v("IMG Downloader", "Bitmap Failed...");
    }

    @Override
    public void onPrepareLoad(Drawable drawable) {
        Log.v("IMG Downloader", "Bitmap Preparing Load...");
    }

}


public class ImageStorageHandler {


    public static void insertInPrivateStorage(final String name, String  path,final Context ctx) throws IOException {
        ImgTarget target=new ImgTarget();
        target.ctx=ctx;
        target.Name=name;
        Picasso.with(ctx).load(path).into(target);

    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(file);
        return data;

    }

    public static String getFileName(Uri uri,Context ctx)
    {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static String getRealPathFromURI(Context context, Uri uri)
    {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null,
                null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }

}
