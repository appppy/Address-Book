
package jp.osaka.cherry.addressbook.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import jp.osaka.cherry.addressbook.ui.view.GoogleMapPropety;


/**
 * 設定ヘルパ
 */
public class SettingsHelper {

    /**
     * @serial コンテキスト
     */
    private Context sContext = null;

    /**
     * @serial BUILDINGSキー
     */
    public static final String KEY_BUILDINGS = "KEY_BUILDINGS";

    /**
     * @serial INDOORキー
     */
    public static final String KEY_INDOOR = "KEY_INDOOR";

    /**
     * @serial MAP TYPEキー
     */
    public static final String KEY_MAP_TYPE = "KEY_MAP_TYPE";

    /**
     * @serial TRAFFICキー
     */
    public static final String KEY_TRAFFIC = "KEY_TRAFFIC";

    /**
     * @serial UI COMPASSキー
     */
    public static final String KEY_UI_COMPASS = "KEY_UI_COMPASS";

    /**
     * @serial UI INDOOR LEVEL PICKERキー
     */
    public static final String KEY_UI_INDOOR_LEVEL_PICKER = "KEY_UI_INDOOR_LEVEL_PICKER";

    /**
     * @serial UI MAP TOOLBARキー
     */
    public static final String KEY_UI_MAP_TOOLBAR = "KEY_UI_MAP_TOOLBAR";

    /**
     * @serial UI MY LOCATION BUTTONキー
     */
    public static final String KEY_UI_MY_LOCATION_BUTTON = "KEY_UI_MY_LOCATION_BUTTON";

    /**
     * @serial UI ROTATE GESTGURESキー
     */
    public static final String KEY_UI_ROTATE_GESTURES = "KEY_UI_ROTATE_GESTURES";

    /**
     * @serial UI SCROLL GESTURESキー
     */
    public static final String KEY_UI_SCROLL_GESTURES = "KEY_UI_SCROLL_GESTURES";

    /**
     * @serial UI TILT GESTURESキー
     */
    public static final String KEY_UI_TILT_GESTURES = "KEY_UI_TILT_GESTURES";

    /**
     * @serial UI ZOOM CONTROLSキー
     */
    public static final String KEY_UI_ZOOM_CONTROLS = "KEY_UI_ZOOM_CONTROLS";

    /**
     * @serial UI ZOOM GESTURESキー
     */
    public static final String KEY_UI_ZOOM_GESTURES = "KEY_UI_ZOOM_GESTURES";

    /**
     * @serial Googleマップ設定
     */
    private final GoogleMapPropety mGoogleMapPropety = new GoogleMapPropety();

    /**
     * 親クラスの設定
     *
     * @param context コンテキスト
     */
    public void setParent(Context context) {
        sContext = context;
    }

    /**
     * 地図のプロパティの取得
     * @return 地図のプロパティ
     */
    public GoogleMapPropety getGoogleMapPropety() {
        mGoogleMapPropety.isBuildingEnabled = readBoolean(KEY_BUILDINGS);
        mGoogleMapPropety.isIndoorEnabled = readBoolean(KEY_INDOOR);
        mGoogleMapPropety.mapType = Integer.parseInt(readString());
        mGoogleMapPropety.trafficEnabled = readBoolean(KEY_TRAFFIC);

        mGoogleMapPropety.compassEnabled = readBoolean(KEY_UI_COMPASS);
        mGoogleMapPropety.indoorLevePickerEnabled = readBoolean(KEY_UI_INDOOR_LEVEL_PICKER);
        mGoogleMapPropety.mapToolbarEnabled = readBoolean(KEY_UI_MAP_TOOLBAR);
        mGoogleMapPropety.myLocationButtonEnabled = readBoolean(KEY_UI_MY_LOCATION_BUTTON);

        mGoogleMapPropety.rorateGesturesEnabled = readBoolean(KEY_UI_ROTATE_GESTURES);
        mGoogleMapPropety.scrollGesturesEnabled = readBoolean(KEY_UI_SCROLL_GESTURES);
        mGoogleMapPropety.tiltGesturesEnabled = readBoolean(KEY_UI_TILT_GESTURES);
        mGoogleMapPropety.zoomControlsEnabled = readBoolean(KEY_UI_ZOOM_CONTROLS);
        mGoogleMapPropety.zoomGesturesEnabled = readBoolean(KEY_UI_ZOOM_GESTURES);

        return mGoogleMapPropety;
    }

    /**
     * 設定の取得
     * @param key キー
     * @return 設定値
     */
    private boolean readBoolean(String key) {
        boolean result = true;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(sContext);
        switch (key) {
            case KEY_UI_COMPASS:
            case KEY_UI_INDOOR_LEVEL_PICKER:
            case KEY_UI_MAP_TOOLBAR:
            case KEY_UI_MY_LOCATION_BUTTON: {
                result = false;
                break;
            }
            default: {
                break;
            }
        }
        return sharedPreferences.getBoolean(key, result);
    }

    /**
     * 設定の取得
     * @return 設定値
     */
    private String readString() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(sContext);
        return sharedPreferences.getString(SettingsHelper.KEY_MAP_TYPE, "1");
    }

}
