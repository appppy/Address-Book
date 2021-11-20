package jp.osaka.cherry.addressbook.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.constants.COLOR;

import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;


/**
 * イメージヘルパ
 */
public class ImageHelper {

    /**
     * Uriから指定されたサイズを下回らない最小のサイズのBitmapを生成します。
     * inSampleSizeが整数でしか倍率を指定できないのでぴったりにはなりません。
     *
     * @param uri   画像のUri
     * @param width 縮小後のサイズ
     * @return Bitmap画像
     */
    public static Bitmap decodeUri(Context context, Uri uri, int width) {
        try {
            // 縮小する倍率を計算する
            int sampleSize = calcSampleSize(context, uri, width);

            BitmapFactory.Options options = new BitmapFactory.Options();
            // 縮小する倍率をセット
            options.inSampleSize = sampleSize;

            InputStream is = context.getContentResolver().openInputStream(uri);
            // Bitmapを生成
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
            if (is != null) {
                is.close();
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 縮小する倍率を計算します。
     * 具体的には、指定されたサイズを下回らない最小のサイズに
     * なるような倍率を計算します。
     *
     * @param context コンテキスト
     * @param uri     画像のuri
     * @param size    縮小後のサイズ
     * @return 縮小する倍率
     */
    public static int calcSampleSize(Context context, Uri uri, int size) {
        int sampleSize = 1;
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Bitmapは生成せずに画像のサイズを測るだけの設定
            options.inJustDecodeBounds = true;
            // 測定
            BitmapFactory.decodeStream(is, null, options);
            if (is != null) {
                is.close();
            }

            // 画像サイズを指定されたサイズで割る
            // int同士の除算なので自動的に小数点以下は切り捨てられる
            if(options.outWidth != 0) {
                sampleSize = options.outWidth / size;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sampleSize;
    }

    /**
     * Uriから指定されたサイズを下回らない最小のサイズのBitmapを生成します。
     * inSampleSizeが整数でしか倍率を指定できないのでぴったりにはなりません。
     *
     * @param pathName 画像のファイルパス
     * @return Bitmap画像
     */
    public static Bitmap decodeFile(String pathName) {

        // 縮小する倍率を計算する
        int sampleSize = calcSampleSize(pathName);

        BitmapFactory.Options options = new BitmapFactory.Options();
        // 縮小する倍率をセット
        options.inSampleSize = sampleSize;

        // Bitmapを生成
        return BitmapFactory.decodeFile(pathName, options);

    }

    /**
     * 縮小する倍率を計算します。
     * 具体的には、指定されたサイズを下回らない最小のサイズに
     * なるような倍率を計算します。
     *
     * @param pathName 画像のファイルパス
     * @return 縮小する倍率
     */
    public static int calcSampleSize(String pathName) {
        int sampleSize = 1;

        BitmapFactory.Options options = new BitmapFactory.Options();
        // Bitmapは生成せずに画像のサイズを測るだけの設定
        options.inJustDecodeBounds = true;
        // 測定
        BitmapFactory.decodeFile(pathName, options);

        // 画像サイズを指定されたサイズで割る
        // int同士の除算なので自動的に小数点以下は切り捨てられる
        //       sampleSize = options.outWidth / size;

        return sampleSize;
    }


    /**
     * UriからPathへの変換処理
     *
     * @param uri URI
     * @return パス
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String toPath(Context context, Uri uri) {
        String path = INVALID_STRING_VALUE;
        ContentResolver contentResolver = context.getContentResolver();
        //String[] columns = {MediaStore.Images.Media.DATA};
        String[] columns = {};
        String selection = MediaStore.Images.Media._ID + "=?";
        String wholeId;
        String id;
        Cursor cursor;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            wholeId = DocumentsContract.getDocumentId(uri);
            id = wholeId.split(":")[1];
            cursor = contentResolver.query(uri, columns, selection, new String[]{id}, null);
        } else {
            cursor = contentResolver.query(uri, columns, null, null, null);
        }
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(0);
            cursor.close();
        }
        if (path != null) {
            return path;
        }

        path = getImageUrlWithAuthority(context, uri);
        if (path != null) {
            return path;
        }
        return uri.getPath();
    }


    /**
     * 画面URL
     *
     * @param context コンテキスト
     * @param uri URI
     * @return 画面URL
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        String path = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                Uri tempUri = writeToTempImageAndGetPathUri(context, bmp);
                ContentResolver contentResolver = context.getContentResolver();
                String[] columns = {MediaStore.Images.Media._ID, MediaStore.Files.FileColumns._ID};
                Cursor cursor = contentResolver.query(tempUri, columns, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    path = cursor.getString(0);
                    cursor.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    Objects.requireNonNull(is).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return path;
    }

    /**
     * 画像保存
     *
     * @param inContext コンテキスト
     * @param inImage 画像
     * @return 画像Uri
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        //String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.TITLE, "Title");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        return inContext.getContentResolver().insert(collection, values);
        //return Uri.parse(path);
    }

    /**
     * 画像色取得
     *
     * @param context コンテキスト
     * @param color 色
     * @return 画像色
     */
    public static int getImageColor(Context context, COLOR color) {
        if (context == null || color == null) {
            return 0;
        }
        int result = ContextCompat.getColor(context, R.color.grey_300);
        try {
            switch (color) {
                case RED: {
                    result = ContextCompat.getColor(context, R.color.red_600);
                    break;
                }
                case PINK: {
                    result = ContextCompat.getColor(context, R.color.pink_600);
                    break;
                }
                case PURPLE: {
                    result = ContextCompat.getColor(context, R.color.purple_600);
                    break;
                }
                case DEEP_PURPLE: {
                    result = ContextCompat.getColor(context, R.color.deep_purple_600);
                    break;
                }
                case INDIGO: {
                    result = ContextCompat.getColor(context, R.color.indigo_600);
                    break;
                }
                case BLUE: {
                    result = ContextCompat.getColor(context, R.color.blue_600);
                    break;
                }
                case LIGHT_BLUE: {
                    result = ContextCompat.getColor(context, R.color.light_blue_600);
                    break;
                }
                case CYAN: {
                    result = ContextCompat.getColor(context, R.color.cyan_600);
                    break;
                }
                case TEAL: {
                    result = ContextCompat.getColor(context, R.color.teal_600);
                    break;
                }
                case GREEN: {
                    result = ContextCompat.getColor(context, R.color.green_600);
                    break;
                }
                case LIGHT_GREEN: {
                    result = ContextCompat.getColor(context, R.color.light_green_600);
                    break;
                }
                case LIME: {
                    result = ContextCompat.getColor(context, R.color.lime_600);
                    break;
                }
                case YELLOW: {
                    result = ContextCompat.getColor(context, R.color.yellow_600);
                    break;
                }
                case AMBER: {
                    result = ContextCompat.getColor(context, R.color.amber_600);
                    break;
                }
                case ORANGE: {
                    result = ContextCompat.getColor(context, R.color.orange_600);
                    break;
                }
                case DEEP_ORANGE: {
                    result = ContextCompat.getColor(context, R.color.deep_orange_600);
                    break;
                }
                case BROWN: {
                    result = ContextCompat.getColor(context, R.color.brown_600);
                    break;
                }
                case BLUE_GREY:
                case GREY: {
                    result = ContextCompat.getColor(context, R.color.blue_grey_600);
                    break;
                }
                default: {
                    result = ContextCompat.getColor(context, R.color.grey_300);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
