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

package jp.osaka.cherry.addressbook.ui;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.UUID;

import jp.osaka.cherry.addressbook.constants.GEOFENCE;

import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_CREATION_DATE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_DWELL_DATE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_ENTER_DATE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_EXIT_DATE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_EXPIRATION_DURATION;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_IMAGE_PATH;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_IS_ARCHIVE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_IS_TRASH;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_KEY;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LATITUDE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LOITERING_DELAY;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LONGITUDE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_MODIFIED_DATE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_NOTE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_NOTIFICATION_RESPONSIVENSS;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_RADIUS;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_TRANSITION;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_TRANSITION_TYPE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_UUID;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_INT_VALUE;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_LONG_VALUE;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;

/**
 * シンプルジオフェンス
 */
public class GeofenceAsset implements Parcelable {
    /**
     * @serial 選択状態
     */
    public boolean isSelected = false;

    /**
     * @serial 識別子
     */
    public String id;

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
     * @serial 期限
     */
    public long expirationDuration;

    /**
     * @serial 型
     */
    public int transitionType;

    /**
     * @serial 遅延時間
     */
    public int loiteringDelay;

    /**
     * @serial 通知応答
     */
    public int notificationResponsiveness;

    /*
     * カスタム設定
     */

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
     * @serial ENTER日
     */
    public long enterDate;

    /**
     * @serial DWELL日
     */
    public long dwellDate;

    /**
     * @serial EXIT日
     */
    public long exitDate;

    /**
     * @serial 状態
     */
    public int transition;

    /**
     * @serial ノート
     */
    public String note;


    /**
     * @serial アーカイブの有無
     */
    public boolean isArchive;

    /**
     * @serial ゴミ箱の有無
     */
    public boolean isTrash;


    /**
     * @serial 画像のパス
     */
    public String imagePath;


    /**
     * @serial 表示名
     */
    public String displayName;

    /**
     * インスタンス生成
     *
     * @return インスタンス
     */
    public static GeofenceAsset createInstance() {
        return new GeofenceAsset(
                INVALID_STRING_VALUE,
                GEOFENCE.DEFAULT_LAT,
                GEOFENCE.DEFAULT_LNG,
                GEOFENCE.MIN_RADIUS,
                GEOFENCE.DEFAULT_EXPIRATION_DURATION_VAUE,
                GEOFENCE.DEFAULT_TRANSITION_TYPE_VALUE,
                GEOFENCE.DEFAULT_LOITERING_DELAY_VALUE,
                GEOFENCE.DEFAULT_NOTIFICATION_RESPONSIVENESS_VALUE,
                String.valueOf(UUID.randomUUID()),
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                INVALID_LONG_VALUE,
                INVALID_LONG_VALUE,
                INVALID_LONG_VALUE,
                INVALID_INT_VALUE,
                INVALID_STRING_VALUE,
                false,
                false,
                INVALID_STRING_VALUE);
    }

    /**
     * @serial 生成
     */
    public static final Creator<GeofenceAsset> CREATOR =
            new Creator<GeofenceAsset>() {

                /**
                 * Parcelableクラス作成
                 * @see Creator#createFromParcel(Parcel)
                 */
                @Override
                public GeofenceAsset createFromParcel(Parcel source) {
                    return new GeofenceAsset(source);
                }

                /**
                 * 配列生成
                 * @see Creator#newArray(int)
                 */
                @Override
                public GeofenceAsset[] newArray(int size) {
                    return new GeofenceAsset[size];
                }
            };

    /**
     * シンプルジオフェンスの生成
     *
     * @param id 識別子
     * @param latitude 緯度
     * @param longitude 経度
     * @param radius 半径
     * @param expirationDuration 期限
     * @param transitionType 型
     * @param loiteringDelay 遅延
     * @param notificationResponsiveness 通知応答
     * @param uuid ユニーク識別子
     * @param creationDate 作成日
     * @param modifiedDate 変更日
     * @param enterDate ENTER日
     * @param dwellDate DWELL日
     * @param exitDate EXIT日
     * @param transition 状態
     * @param note ノート
     * @param isArchive アーカイブの有無
     * @param isTrash ゴミ箱の有無
     * @param imagePath 画像のパス
     */
    public GeofenceAsset(
            String id,
            double latitude,
            double longitude,
            float radius,
            long expirationDuration,
            int transitionType,
            int loiteringDelay,
            int notificationResponsiveness,
            String uuid,
            long creationDate,
            long modifiedDate,
            long enterDate,
            long dwellDate,
            long exitDate,
            int transition,
            String note,
            boolean isArchive,
            boolean isTrash,
            String imagePath
    ) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.expirationDuration = expirationDuration;
        this.transitionType = transitionType;
        this.loiteringDelay = loiteringDelay;
        this.notificationResponsiveness = notificationResponsiveness;

        this.uuid = uuid;
        this.creationDate = creationDate;
        this.modifiedDate = modifiedDate;
        this.enterDate = enterDate;
        this.dwellDate = dwellDate;
        this.exitDate = exitDate;
        this.transition = transition;
        this.note = note;

        this.isArchive = isArchive;
        this.isTrash = isTrash;

        this.imagePath = imagePath;
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
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(radius);
        dest.writeLong(expirationDuration);
        dest.writeInt(transitionType);
        dest.writeInt(loiteringDelay);
        dest.writeInt(notificationResponsiveness);

        dest.writeString(uuid);
        dest.writeLong(creationDate);
        dest.writeLong(modifiedDate);
        dest.writeLong(enterDate);
        dest.writeLong(dwellDate);
        dest.writeLong(exitDate);
        dest.writeInt(transition);
        dest.writeString(note);

        dest.writeByte((byte) (isArchive ? 1 : 0));
        dest.writeByte((byte) (isTrash ? 1 : 0));

        dest.writeString(imagePath);
    }

    /**
     * コンストラクタ
     *
     * @param parcel パーシャル
     */
    public GeofenceAsset(Parcel parcel) {
        id = parcel.readString();
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        radius = parcel.readFloat();
        expirationDuration = parcel.readLong();
        transitionType = parcel.readInt();
        loiteringDelay = parcel.readInt();
        notificationResponsiveness = parcel.readInt();

        uuid = parcel.readString();
        creationDate = parcel.readLong();
        modifiedDate = parcel.readLong();
        enterDate = parcel.readLong();
        dwellDate = parcel.readLong();
        exitDate = parcel.readLong();
        transition = parcel.readInt();
        note = parcel.readString();

        isArchive = parcel.readByte() != 0;
        isTrash = parcel.readByte() != 0;

        imagePath = parcel.readString();
    }

    /**
     * コンストラクタ
     *
     * @param bundle 引数
     */
    public GeofenceAsset(Bundle bundle) {
        setBundle(bundle);
    }

    /**
     * バンドル設定
     *
     * @param bundle バンドル
     */
    public void setBundle(Bundle bundle) {
        id = bundle.getString(EXTRA_KEY);
        latitude = bundle.getDouble(EXTRA_LATITUDE);
        longitude = bundle.getDouble(EXTRA_LONGITUDE);
        radius = bundle.getFloat(EXTRA_RADIUS);
        expirationDuration = bundle.getLong(EXTRA_EXPIRATION_DURATION);
        transitionType = bundle.getInt(EXTRA_TRANSITION_TYPE);
        loiteringDelay = bundle.getInt(EXTRA_LOITERING_DELAY);
        notificationResponsiveness = bundle.getInt(EXTRA_NOTIFICATION_RESPONSIVENSS);

        uuid = bundle.getString(EXTRA_UUID);
        creationDate = bundle.getLong(EXTRA_CREATION_DATE);
        modifiedDate = bundle.getLong(EXTRA_MODIFIED_DATE);
        enterDate = bundle.getLong(EXTRA_ENTER_DATE);
        dwellDate = bundle.getLong(EXTRA_DWELL_DATE);
        exitDate = bundle.getLong(EXTRA_EXIT_DATE);
        transition = bundle.getInt(EXTRA_TRANSITION);
        note = bundle.getString(EXTRA_NOTE);

        isArchive = bundle.getBoolean(EXTRA_IS_ARCHIVE);
        isTrash = bundle.getBoolean(EXTRA_IS_TRASH);

        imagePath = bundle.getString(EXTRA_IMAGE_PATH);
    }

    /**
     * コンストラクタ
     *
     * @return 引数
     */
    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putString(EXTRA_KEY, id);
        bundle.putDouble(EXTRA_LATITUDE, latitude);
        bundle.putDouble(EXTRA_LONGITUDE, longitude);
        bundle.putFloat(EXTRA_RADIUS, radius);
        bundle.putLong(EXTRA_EXPIRATION_DURATION, expirationDuration);
        bundle.putInt(EXTRA_TRANSITION_TYPE, transitionType);
        bundle.putInt(EXTRA_LOITERING_DELAY, loiteringDelay);
        bundle.putInt(EXTRA_NOTIFICATION_RESPONSIVENSS, notificationResponsiveness);

        bundle.putString(EXTRA_UUID, uuid);
        bundle.putLong(EXTRA_CREATION_DATE, creationDate);
        bundle.putLong(EXTRA_MODIFIED_DATE, modifiedDate);
        bundle.putLong(EXTRA_ENTER_DATE, enterDate);
        bundle.putLong(EXTRA_DWELL_DATE, dwellDate);
        bundle.putLong(EXTRA_EXIT_DATE, exitDate);
        bundle.putInt(EXTRA_TRANSITION, transition);
        bundle.putString(EXTRA_NOTE, note);

        bundle.putBoolean(EXTRA_IS_ARCHIVE, isArchive);
        bundle.putBoolean(EXTRA_IS_TRASH, isTrash);

        bundle.putString(EXTRA_IMAGE_PATH, imagePath);
        return bundle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof GeofenceAsset) {
            GeofenceAsset geofence = (GeofenceAsset) o;
            if (geofence.uuid.equals(uuid)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    public String toString() {
        String result;
        StringBuilder sb = new StringBuilder();
        sb.append("GEOFENCE:").append("(").append(uuid).append(")").append(imagePath);
        result = sb.toString();
        sb.delete(0, sb.length());
        return result;
    }
}
