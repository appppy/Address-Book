package jp.osaka.cherry.addressbook.service;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_ASSETS;


/**
 * データベース
 */
public class SimpleDatabase {

    /**
     * @serial 目印
     */
    private static final String TAG = "SimpleDatabase";

    /**
     * @serial プリファレンス
     */
    private final CollectionStore<JSONObject> mPrefs;

    /**
     * @serial 一覧
     */
    private final Collection<SimpleAsset> mCollection = new ArrayList<>();

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public SimpleDatabase(Context context) {
        mPrefs = new CollectionStore<>(context);
    }

    /**
     * リストア
     */
    public void restore() {
        if (LOG_I) {
            Log.i(TAG, "restore#enter");
        }

        // クリア
        mCollection.clear();

        // リストア
        try {
            List<JSONObject> objects = (List<JSONObject>) mPrefs.get(EXTRA_ASSETS);
            for(JSONObject object : objects) {
                mCollection.add(new SimpleAsset(object));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (LOG_I) {
            Log.i(TAG, "restore#leave");
        }
    }

    /**
     * バックアップ
     */
    void backup(Collection<SimpleAsset> collection) {
        if (LOG_I) {
            Log.i(TAG, "backup#enter");
        }
        // 内部データの保存
        mCollection.clear();
        mCollection.addAll(collection);

        // バックアップ
        List<JSONObject> objects = new ArrayList<>();
        for(SimpleAsset item : collection) {
            objects.add(item.toJSONObject());
        }
        mPrefs.set(EXTRA_ASSETS, objects);

        if (LOG_I) {
            Log.i(TAG, "backup#leave");
        }
    }

    /**
     * 一覧取得
     *
     * @return 一覧
     */
    public Collection<SimpleAsset> getAssets() {
        return mCollection;
    }
}
