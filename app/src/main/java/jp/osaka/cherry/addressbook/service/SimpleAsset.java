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
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.UUID;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.constants.COLOR;

import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_LAT;
import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_LNG;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_LONG_VALUE;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;
import static jp.osaka.cherry.addressbook.service.SimpleAsset.CONTENT.CONTACT;


/**
 * 項目
 */
public class SimpleAsset implements Parcelable {

    /**
     * @serial 識別子
     */
    public String id;

    /**
     * @serial ユニーク識別子
     */
    public String uuid;

    /**
     * @serial 作成日
     */
    public long creationDate;

    /**
     * @serial 変更日
     */
    public long modifiedDate;

    /**
     * @serial 表示名
     */
    public String displayName;

    /**
     * @serial ホームページ
     */
    public String url;

    /**
     * @serial 電話番号
     */
    public String call;

    /**
     * @serial 画像のパス
     */
    public String imagePath = INVALID_STRING_VALUE;

    /**
     * @serial ノート
     */
    public String note;

    /**
     * @serial タイムスタンプ
     */
    public long timestamp;

    /**
     * @serial タイトル
     */
    public String title;

    /**
     * @serial サブタイトル
     */
    public String subtitle;

    /**
     * @serial 選択
     */
    public boolean isSelected = false;

    /**
     * コンテンツ
     */
    public enum CONTENT {
        CONTACT,
        ARCHIVE,
        TRASH
    }

    /**
     * @serial コンテンツ
     */
    public CONTENT content;

    /**
     * @serial 住所
     */
    public String place;

    /**
     * @serial 生年月日
     */
    public long date;

    /**
     * @serial 色
     */
    public COLOR color;

    /**
     * @serial 緯度
     */
    public double latitude;

    /**
     * @serial 経度
     */
    public double longitude;

    /**
     * @serial 半径
     */
    public float radius;


    /**
     * @serial 生成
     */
    public static final Creator<SimpleAsset> CREATOR =
            new Creator<SimpleAsset>() {

                /**
                 * Parcelableクラス作成
                 * @see Creator#createFromParcel(Parcel)
                 */
                @Override
                public SimpleAsset createFromParcel(Parcel source) {
                    return new SimpleAsset(source);
                }

                /**
                 * 配列生成
                 * @see Creator#newArray(int)
                 */
                @Override
                public SimpleAsset[] newArray(int size) {
                    return new SimpleAsset[size];
                }
            };


    /**
     * 人の生成
     *
     * @param id 識別子
     * @param uuid ユニーク識別子
     * @param creationDate 作成日
     * @param modifiedDate 変更日
     * @param displayName 表示名
     * @param tel 電話番号
     * @param imagePath 画像パス
     * @param note ノート
     * @param timestamp タイムスタンプ
     * @param content コンテンツ
     * @param address アドレス
     * @param birthday 誕生日
     * @param color 色
     * @param latitude 緯度
     * @param longitude 経度
     * @param radius 半径
     */
    public SimpleAsset(
            String id,
            String uuid,
            long creationDate,
            long modifiedDate,
            String displayName,
            String tel,
            String url,
            //String email,
            String imagePath,
            String note,
            long timestamp,
            CONTENT content,
            String address,
            long birthday,
            COLOR color,
            double latitude,
            double longitude,
            float  radius
    ) {
        this.id = id;
        this.uuid = uuid;
        this.creationDate = creationDate;
        this.modifiedDate = modifiedDate;
        this.displayName = displayName;
        this.call = tel;
        this.url = url;
        //this.send = email;
        this.imagePath = imagePath;
        this.note = note;
        this.timestamp = timestamp;
        this.content = content;
        this.place = address;
        this.date = birthday;
        this.color = color;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    /**
     * コンストラクタ
     *
     * @param parcel パーシャル
     */
    public SimpleAsset(Parcel parcel) {
        id = parcel.readString();
        uuid = parcel.readString();
        creationDate = parcel.readLong();
        modifiedDate = parcel.readLong();
        displayName = parcel.readString();
        call = parcel.readString();
        url = parcel.readString();
        //send = parcel.readString();
        imagePath = parcel.readString();
        note = parcel.readString();
        timestamp = parcel.readLong();
        content = SimpleAsset.CONTENT.valueOf(parcel.readString());
        place = parcel.readString();
        date = parcel.readLong();
        color = COLOR.valueOf(parcel.readString());
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        radius = parcel.readFloat();
    }

    /**
     * インスタンス生成
     *
     * @return インスタンス
     */
    public static SimpleAsset createInstance() {
        return new SimpleAsset(
                String.valueOf(UUID.randomUUID()),
                String.valueOf(UUID.randomUUID()),
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                INVALID_STRING_VALUE,
                INVALID_STRING_VALUE,
                INVALID_STRING_VALUE,
                INVALID_STRING_VALUE,
                INVALID_STRING_VALUE,
                INVALID_LONG_VALUE,
                CONTACT,
                INVALID_STRING_VALUE,
                INVALID_LONG_VALUE,
                COLOR.WHITE,
                DEFAULT_LAT,
                DEFAULT_LNG,
                0
        );
    }

    /***
     * コピー
     *
     * @param item 項目
     */
    public void copy(SimpleAsset item) {
        id = item.id;
        uuid = item.uuid;
        setParams(item);
    }

    /***
     * パラメータの設定
     *
     * @param item 項目
     */
    public void setParams(SimpleAsset item) {
        creationDate = item.creationDate;
        modifiedDate = item.modifiedDate;
        displayName = item.displayName;
        call = item.call;
        url = item.url;
        imagePath = item.imagePath;
        note = item.note;
        timestamp = item.timestamp;
        content = item.content;
        place = item.place;
        date = item.date;
        color = item.color;
        latitude = item.latitude;
        longitude = item.longitude;
        radius = item.radius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uuid);
        dest.writeLong(creationDate);
        dest.writeLong(modifiedDate);
        dest.writeString(displayName);
        dest.writeString(call);
        dest.writeString(url);
        dest.writeString(imagePath);
        dest.writeString(note);
        dest.writeLong(timestamp);
        dest.writeString(content.name());
        dest.writeString(place);
        dest.writeLong(date);
        dest.writeString(color.name());
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(radius);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equal(Object o) {
        boolean result = false;
        if (o instanceof SimpleAsset) {
            SimpleAsset item = (SimpleAsset) o;
            if (item.uuid.equals(uuid)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 文字に変換する
     *
     * @param context コンテキスト
     * @return 文字
     */
    public String toString(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.name)).append(":").append(displayName);
        if ((call != null) && call.equals(INVALID_STRING_VALUE)) {
            sb.append(", ").append(context.getString(R.string.call)).append(":").append(call);
        }
        if ((url != null) && url.equals(INVALID_STRING_VALUE)) {
            sb.append(", ").append(context.getString(R.string.url)).append(":").append(url);
        }
        if (note != null && note.equals(INVALID_STRING_VALUE)) {
            sb.append(", ").append(context.getString(R.string.note)).append(":").append(note);
        }
        sb.append(", ").append("latitude").append(":").append(latitude);
        sb.append(", ").append("longitude").append(":").append(longitude);
        sb.append(", ").append("radius").append(":").append(radius);
        String s = sb.toString();
        sb.delete(0, sb.length());
        return s;
    }

    /**
     * CSVに変換する
     *
     * @return CSV
     */
    public String toCSV() {
        String result;
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(id).append("\"").append(","); //id
        sb.append("\"").append(uuid).append("\"").append(","); //uuid
        sb.append("\"").append(creationDate).append("\"").append(","); //createDate
        sb.append("\"").append(modifiedDate).append("\"").append(","); //modifiedDate
        sb.append("\"").append(displayName).append("\"").append(","); //displayName
        sb.append("\"").append(call).append("\"").append(","); //tel
        sb.append("\"").append(url).append("\"").append(","); //url
        sb.append("\"").append(imagePath).append("\"").append(","); //imagePath
        sb.append("\"").append(note).append("\"").append(","); //note
        sb.append("\"").append(timestamp).append("\"").append(","); //recent
        sb.append("\"").append(content.name()).append("\"").append(","); //isArchive
        sb.append("\"").append(place).append("\"").append(","); //isTrash
        sb.append("\"").append(date).append("\"").append(","); //date
        sb.append("\"").append(color.name()).append("\"").append(","); //color
        sb.append("\"").append(latitude).append("\"").append(",");
        sb.append("\"").append(longitude).append("\"").append(",");
        sb.append("\"").append(radius).append("\"").append("\n");
        result = sb.toString();
        sb.delete(0, sb.length());
        return result;
    }

    /**
     * JSONオブジェクトに変換する
     *
     * @return jsonオブジェクト
     */
    public JSONObject toJSONObject() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("uuid",uuid);
            object.put("creationDate",creationDate);
            object.put("modifiedDate",modifiedDate);
            object.put("displayName",displayName);
            object.put("call", call);
            object.put("url",url);
            object.put("imagePath", imagePath);
            object.put("note", note);
            object.put("timestamp", timestamp);
            object.put("content", content.name());
            object.put("place", place);
            object.put("date", date);
            object.put("color", color.name());
            object.put("latitude", latitude);
            object.put("longitude",longitude);
            object.put("radius",radius);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * コンストラクタ
     *
     * @param object jsonオブジェクト
     */
    public SimpleAsset(JSONObject object) {
        try {
            id = object.getString("id");
            uuid = object.getString("uuid");
            creationDate = object.getLong("creationDate");
            modifiedDate = object.getLong("modifiedDate");
            displayName = object.getString("displayName");
            call = object.getString("call");
            url = object.getString("url");
            imagePath = object.getString("imagePath");
            note = object.getString("note");
            timestamp = object.getLong("timestamp");
            content = SimpleAsset.CONTENT.valueOf(object.getString("content"));
            place = object.getString("place");
            date = object.getLong("date");
            color = COLOR.valueOf(object.getString("color"));
            latitude = object.getDouble("latitude");
            longitude = object.getDouble("longitude");
            radius = BigDecimal.valueOf(object.getDouble("radius")).floatValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
