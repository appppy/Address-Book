package jp.osaka.cherry.addressbook.utils;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.osaka.cherry.addressbook.constants.COLOR;
import jp.osaka.cherry.addressbook.service.SimpleAsset;

import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_LAT;
import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_LNG;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_LONG_VALUE;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;
import static jp.osaka.cherry.addressbook.utils.GeofenceHelper.getAddress;


/**
 * ヘルパ
 */
public class AssetHelper {

    /**
     * 項目の取得
     *
     * @param uuid 識別子
     * @param list 一覧
     * @return 項目
     */
    public static SimpleAsset getAsset(String uuid, List<SimpleAsset> list) {
        SimpleAsset result = null;
        for (SimpleAsset dest : list) {
            if (uuid.equals(dest.uuid)) {
                result = dest;
                break;
            }
        }
        return result;
    }

    /**
     * 更新日でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 更新日でソートした一覧
     */
    public static Collection<SimpleAsset> toSortByDateModifiedCollection(Collection<SimpleAsset> collection) {
        Collections.sort((List<SimpleAsset>) collection, (lhs, rhs) -> (int) (lhs.modifiedDate - rhs.modifiedDate));
        return collection;
    }

    /**
     * 作成日でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 作成日でソートした一覧
     */
    public static Collection<SimpleAsset> toSortByDateCreatedCollection(Collection<SimpleAsset> collection) {
        Collections.sort((List<SimpleAsset>) collection, (lhs, rhs) -> (int) (lhs.creationDate - rhs.creationDate));
        return collection;
    }

    /**
     * 名前でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 名前でソートした一覧
     */
    public static Collection<SimpleAsset> toSortByNameCollection(Collection<SimpleAsset> collection) {
        Collections.sort((List<SimpleAsset>) collection, (lhs, rhs) -> lhs.displayName.compareTo(rhs.displayName));
        return collection;
    }

    /**
     * 一覧のコピー
     *
     * @param dest コピー先
     * @param src  コピー元
     */
    public static void copy(ArrayList<SimpleAsset> dest, ArrayList<SimpleAsset> src) {
        dest.clear();
        for (SimpleAsset s : src) {
            SimpleAsset d = SimpleAsset.createInstance();
            d.copy(s);
            dest.add(d);
        }
    }

    /**
     * Uri取得
     *
     * @param src アセット
     * @return Uri
     */
    public static Uri getUri(SimpleAsset src) {
        Uri result;
        result = Uri.parse("geo:" + src.latitude + "," + src.longitude);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public static boolean isModified(SimpleAsset dest, SimpleAsset src) {
        boolean result = false;
        if (dest.uuid.equals(src.uuid)) {
            if ((dest.creationDate != src.creationDate)
                    || (dest.modifiedDate != src.modifiedDate)
                    || (!dest.displayName.equals(src.displayName))
                    || (!dest.call.equals(src.call))
                    || (!dest.url.equals(src.url))
                    || (!dest.imagePath.equals(src.imagePath))
                    || (!dest.note.equals(src.note))
                    || (dest.content != src.content)
                    || (!dest.place.equals(src.place)
                    || (dest.date != src.date)
                    || (dest.latitude != src.latitude)
                    || (dest.longitude != src.longitude)))
            {
                result = true;
            }
        }
        return result;
    }

    /**
     * CSVに変換
     *
     * @param list 一覧
     * @return CSV
     */
    public static String toCSV(ArrayList<SimpleAsset> list) {
        StringBuilder sb = new StringBuilder();
        for (SimpleAsset item : list) {
            sb.append(item.toCSV());
        }
        String data = sb.toString();
        sb.delete(0, sb.length());
        return data;
    }

    /**
     * ArrayListに変換
     * @param context コンテキスト
     * @param records レコード
     * @return 一覧
     */
    public static ArrayList<SimpleAsset> toAssets(Context context, List<String[]> records) {
        ArrayList<SimpleAsset> result = new ArrayList<>();
        for (String[] record : records) {
             if (record.length > 16) {
                try {
                    String path;
                    File out = new File(record[7]);
                    if (out.exists()) {
                        path = record[7];
                    } else {
                        path = INVALID_STRING_VALUE;
                    }
                    SimpleAsset asset = new SimpleAsset(
                            record[0],
                            record[1],
                            toLongValue(record[2], System.currentTimeMillis()),
                            toLongValue(record[3], System.currentTimeMillis()),
                            record[4],
                            record[5],
                            record[6],
                            path,
                            record[8],
                            toLongValue(record[9], INVALID_LONG_VALUE),
                            toContentValue(record[10]),
                            record[11],
                            toLongValue(record[12], INVALID_LONG_VALUE),
                            toColorValue(record[13]),
                            toDoubleValue(record[14], DEFAULT_LAT),
                            toDoubleValue(record[15], DEFAULT_LNG),
                            toFloatValue(record[16])
                    );
                    LatLng latLng = new LatLng(asset.latitude, asset.longitude);
                    asset.place = getAddress(context, latLng);
                    result.add(asset);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    SimpleAsset asset = SimpleAsset.createInstance();
                    int i = 0;
                    for (String r : record) {
                        switch (i) {
                            case 0: {
                                asset.displayName = r;
                                break;
                            }
                            case 1: {
                                asset.note = r;
                                break;
                            }
                            case 2: {
                                asset.latitude = toDoubleValue(r, DEFAULT_LAT);
                                break;
                            }
                            case 3: {
                                asset.longitude = toDoubleValue(r, DEFAULT_LNG);
                                break;
                            }
                            case 4: {
                                asset.radius = toFloatValue(r);
                                break;
                            }
                            case 5: {
                                asset.call = r;
                                break;
                            }
                            case 6: {
                                asset.url = r;
                                break;
                            }
                            case 7: {
                                asset.color = toColorValue(r);
                                break;
                            }
                            case 8: {
                                String path;
                                File out = new File(r);
                                if (out.exists()) {
                                    path = r;
                                } else {
                                    path = INVALID_STRING_VALUE;
                                }
                                asset.imagePath = path;
                                break;
                            }
                            case 9: {
                                asset.creationDate = toLongValue(r, System.currentTimeMillis());
                                break;
                            }
                            case 10: {
                                asset.modifiedDate = toLongValue(r, System.currentTimeMillis());
                                break;
                            }
                            case 11: {
                                asset.content = toContentValue(r);
                                break;
                            }
                            case 12: {
                                asset.place = r;
                                break;
                            }
                            case 13: {
                                asset.date = toLongValue(r, INVALID_LONG_VALUE);
                                break;
                            }
                            case 14: {
                                asset.uuid = r;
                                break;
                            }
                            case 15: {
                                asset.timestamp = toLongValue(r, INVALID_LONG_VALUE);
                                break;
                            }
                            default:
                                break;
                        }
                        i++;
                    }
                    LatLng latLng = new LatLng(asset.latitude, asset.longitude);
                    asset.place = getAddress(context, latLng);
                    result.add(asset);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * long変換
     *
     * @param value 値
     * @param defaultValue デフォルト値
     * @return Long変換値
     */
    private static long toLongValue(String value, long defaultValue) {
        long result = defaultValue;
        if(!value.isEmpty()) {
            result = Long.parseLong(value);
        }
        return result;
    }

    /**
     * dobule変換
     *
     * @param value 値
     * @param defaultValue デフォルト値
     * @return double変換値
     */
    private static double toDoubleValue(String value, double defaultValue) {
        double result = defaultValue;
        if(!value.isEmpty()) {
            result = Double.parseDouble(value);
        }
        return result;
    }

    /**
     * float変換
     *
     * @param value 値
     * @return float変換値
     */
    private static float toFloatValue(String value) {
        float result = (float) 0;
        if(!value.isEmpty()) {
            result = Float.parseFloat(value);
        }
        return result;
    }

    /**
     * コンテント値変換
     *
     * @param value 値
     * @return コンテント値
     */
    private static SimpleAsset.CONTENT toContentValue(String value) {
        SimpleAsset.CONTENT result = SimpleAsset.CONTENT.CONTACT;
        if(!value.isEmpty()) {
            result = SimpleAsset.CONTENT.valueOf(value);
        }
        return result;
    }

    /**
     * 色値変換
     *
     * @param value 値
     * @return 色値
     */
    private static COLOR toColorValue(String value) {
        COLOR result = COLOR.WHITE;
        if(!value.isEmpty()) {
            result = COLOR.valueOf(value);
        }
        return result;
    }

    /**
     * 選択状態の確認
     *
     * @return 選択状態
     */
    public static boolean isSelected(List<SimpleAsset> collection) {
        boolean result = false;
        // 製品の選択状態を確認する
        for (SimpleAsset item : collection) {
            // 選択状態を確認した
            if (item.isSelected) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 複数選択状態の確認
     *
     * @return 複数選択状態
     */
    public static boolean isMultiSelected(List<SimpleAsset> collection) {
        int count = 0;
        // 製品の選択状態を確認する
        for (SimpleAsset item : collection) {
            // 選択状態を確認した
            if (item.isSelected) {
                count++;
            }
        }
        return (count > 1);
    }

    /**
     * 選択状態の確認
     *
     * @return 選択状態
     */
    public static ArrayList<SimpleAsset> getSelectedCollection(Collection<? extends SimpleAsset> collection) {
        ArrayList<SimpleAsset> result = new ArrayList<>();
        // 製品の選択状態を確認する
        for (SimpleAsset item : collection) {
            // 選択状態を確認した
            if (item.isSelected) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * JSON文字変換
     *
     * @param collection 一覧
     * @return JSON文字列
     */
    public static String toJSONString(ArrayList<SimpleAsset> collection) {
        JSONArray array = new JSONArray();
        for (SimpleAsset item : collection) {
            array.put(item.toJSONObject());
        }
        return array.toString();
    }

    /**
     * 一覧値変換
     *
     * @param JSONString JSON文字列
     * @return 一覧
     */
    public static ArrayList<SimpleAsset> toAssets(String JSONString) {
        ArrayList<SimpleAsset> results = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(JSONString);
            int count = array.length();
            for (int i=0; i<count; i++){
                results.add(new SimpleAsset(array.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
