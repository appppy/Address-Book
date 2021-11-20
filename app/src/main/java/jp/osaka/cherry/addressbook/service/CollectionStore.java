/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.osaka.cherry.addressbook.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;


/**
 * 一覧の保存
 */
class CollectionStore<T> {

    /**
     * @serial 目印
     */
    static final String SHARED_PREFERENCE_NAME = "CollectionStore";

    /**
     * @serial プリファレンス
     */
    private final SharedPreferences mPrefs;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    CollectionStore(Context context) {
        // プリファレンスの取得
        mPrefs =
                context.getSharedPreferences(
                        SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
    }

    /**
     * 一覧の取得
     *
     * @return 一覧
     */
    public Collection<JSONObject> get(String key) {
        Collection<JSONObject> collection = new ArrayList<>();
        String json = mPrefs.getString(key, "");
        if(!TextUtils.isEmpty(json)) {
            // JSON -> List<Object>に変換
            try {
                JSONArray arr = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    collection.add((JSONObject) arr.get(i));
                }
            } catch (JSONException ignored) {
            }
        }
        return collection;
    }

    /**
     * 一覧の設定
     *
     * @param collection 設定
     */
    public void set(String key, Collection<T> collection) {
        String json = new JSONArray(collection).toString();
        mPrefs.edit().putString(key, json).apply();
    }
}
