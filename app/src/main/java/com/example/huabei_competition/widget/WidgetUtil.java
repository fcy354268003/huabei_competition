package com.example.huabei_competition.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.huabei_competition.R;
import com.example.huabei_competition.util.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class WidgetUtil {

    private static HashMap<String, Typeface> typefaceHashMap = new HashMap<>();
    public static final String CUSTOMER_HUAKANGSHAONV = "fonts/DFPShaoNvW5-GB.ttf";

    public static void setCustomerText(TextView textView, String customerStyle) {
        if (!typefaceHashMap.containsKey(customerStyle)) {
            Typeface typeface = Typeface.createFromAsset(MyApplication.getApplicationByReflect().getAssets(), customerStyle);
            typefaceHashMap.put(customerStyle, typeface);
        }
        textView.setTypeface(typefaceHashMap.get(customerStyle));
    }

    /**
     * @param uri 图片uri
     */
    public static void picToByteStream(ContentResolver contentResolver, Uri uri) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ParcelFileDescriptor r = contentResolver.openFileDescriptor(uri, "r");
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(r.getFileDescriptor());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();
            String base64Stream = Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File picToFile(Context context, @NonNull Uri uri) {
        String scheme = uri.getScheme();
        if (!scheme.equals(ContentResolver.SCHEME_CONTENT))
            return null;
        Cursor cursor = context.getContentResolver()
                .query(uri, new String[]{MediaStore.Images.ImageColumns.DATA},
                        null,
                        null,
                        null);
        if (cursor == null)
            return null;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (columnIndex > -1) {
                String path = cursor.getString(columnIndex);
                System.out.println(path);
                return new File(path);
            }
        }
        return null;
    }

    public static String getImgMimeType(File imgFile) {
        if (imgFile == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getPath(), options);
        return options.outMimeType;
    }

    public static Bitmap fileToBitmap(Resources resources, File file) {
        if (file != null)
            return BitmapFactory.decodeFile(file.getPath());
        return BitmapFactory.decodeResource(resources, R.drawable.head3);
    }

    private static final String TAG = "WidgetUtil";

}
