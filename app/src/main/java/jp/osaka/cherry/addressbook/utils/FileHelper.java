package jp.osaka.cherry.addressbook.utils;

import android.content.Context;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.ui.files.SimpleFile;

import static jp.osaka.cherry.addressbook.utils.AssetHelper.toAssets;


/**
 * ファイルヘルパ
 */
public class FileHelper {
    /**
     * ファイル書き込み
     *
     * @param context  コンテキスト
     * @param filename ファイル名
     * @param list     一覧
     */
    public static void writeFile(Context context, String filename, ArrayList<SimpleAsset> list) {
        try {
            StringBuilder sb = new StringBuilder();
            FileOutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            for (SimpleAsset item : list) {
                sb.append(item.toCSV());
            }
            String data = sb.toString();
            out.write(data.getBytes());
            out.flush();
            out.close();
            sb.delete(0, sb.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ファイル読み込み
     *
     * @param context コンテキスト
     * @param file    ファイル
     * @return 一覧
     */
    public static ArrayList<SimpleAsset> readFile(Context context, String file) {
        ArrayList<SimpleAsset> result = new ArrayList<>();
        try {
            try (FileInputStream in = context.openFileInput(file)) {
                InputStreamReader ireader;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    ireader = new InputStreamReader(in, StandardCharsets.UTF_8);
                } else {
                    ireader = new InputStreamReader(in);
                }
                CSVReader reader = new CSVReader(ireader, ',', '"', 0);
                List<String[]> records = reader.readAll();
                result = toAssets(context, records);
                reader.close();
                ireader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ファイル読み込み
     *
     * @param file ファイル
     * @return リスト
     */
    public static ArrayList<SimpleAsset> readFile(Context context, File file) {
        ArrayList<SimpleAsset> result = new ArrayList<>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            CSVReader reader = new CSVReader(br, ',', '"', 0);
            List<String[]> records = reader.readAll();
            result = toAssets(context, records);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * ファイルの削除
     *
     * @param context コンテキスト
     * @param name    ファイル名
     */
    public static void deleteFile(Context context, String name) {
        context.deleteFile(name + ".csv");
    }

    /**
     * 検索したファイルの取得
     *
     * @param collection 一覧
     * @param src        検索文字
     * @return 検索したファイル一覧
     */
    public static ArrayList<SimpleFile> toListOf(ArrayList<SimpleFile> collection, String src) {
        ArrayList<SimpleFile> result = new ArrayList<>();
        if (src.isEmpty()) {
            return result;
        }
        for (SimpleFile item : collection) {
            if (item != null && (item.name.contains(src))) {
                result.add(item);
            }
        }
        return result;
    }


    /**
     * 名前でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 名前でソートした一覧
     */
    public static Collection<SimpleFile> toSortByNameFileCollection(Collection<SimpleFile> collection) {
        Collections.sort((List<SimpleFile>) collection, (lhs, rhs) -> lhs.name.compareTo(rhs.name));
        return collection;
    }

    /**
     * 更新日でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 更新日でソートした一覧
     */
    public static Collection<SimpleFile> toSortByDateModifiedCollection(Collection<SimpleFile> collection) {
        Collections.sort((List<SimpleFile>) collection, (lhs, rhs) -> (int) (lhs.date - rhs.date));
        return collection;
    }
}
