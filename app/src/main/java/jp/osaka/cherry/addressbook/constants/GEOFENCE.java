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

package jp.osaka.cherry.addressbook.constants;

import com.google.android.gms.location.Geofence;

/**
 * ジオフェンスの定義
 */
public final class GEOFENCE {
    /*
     * デフォルト値
     */

    /**
     * @serial 期限
     */
    public static final long DEFAULT_EXPIRATION_DURATION_VAUE = Geofence.NEVER_EXPIRE;

    /**
     * @serial 型
     */
    public static final int DEFAULT_TRANSITION_TYPE_VALUE = (Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);

    /**
     * @serial 遅延時間
     */
    public static final int DEFAULT_LOITERING_DELAY_VALUE = 300000;

    /**
     * @serial 通知レスポンス時間
     */
    public static final int DEFAULT_NOTIFICATION_RESPONSIVENESS_VALUE = 300000;

    /**
     * @serial 緯度
     */
    public static final double DEFAULT_LAT = 51.477812;

    /**
     * @serial 経度
     */
    public static final double DEFAULT_LNG = -0.001475;

    /**
     * @serial 最小半径
     */
    public static final float MIN_RADIUS = 1f;
}
