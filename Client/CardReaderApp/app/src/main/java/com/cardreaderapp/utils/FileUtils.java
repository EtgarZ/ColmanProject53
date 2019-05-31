package com.cardreaderapp.utils;

import android.net.Uri;
import android.webkit.MimeTypeMap;

public class FileUtils {
    public static String getFileExtension(Uri uri){
        return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
    }
}
