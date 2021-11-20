package jp.osaka.cherry.addressbook.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.ui.GeofenceAsset;
import jp.osaka.cherry.addressbook.ui.view.GoogleMapPropety;


/**
 * ジオフェンスヘルパ
 */
public class GeofenceHelper {

    /**
     * 色取得
     *
     * @param geofence ジオフェンス
     * @return 色
     */
    public static int getColor(GeofenceAsset geofence) {
        int color = 0x80007b50;
        if (geofence.isArchive) {
            color = 0x80607d8b;
        } else if (geofence.isTrash) {
            color = 0x80616161;
        } else {
            switch (geofence.transition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER: {
                    color = 0x805677ff;
                    break;
                }
                case Geofence.GEOFENCE_TRANSITION_DWELL: {
                    color = 0x803f51b5;
                    break;
                }
                case Geofence.GEOFENCE_TRANSITION_EXIT: {
                    color = 0x80e91e63;
                }
                default: {
                    break;
                }
            }
        }
        return color;
    }

    /**
     * 設定
     *
     * @param map マップ
     * @param propety プロパティ
     */
    public static void setup(GoogleMap map, GoogleMapPropety propety) {
        try {
            map.setBuildingsEnabled(propety.isBuildingEnabled);
            map.setIndoorEnabled(propety.isIndoorEnabled);
            map.setMapType(propety.mapType);
            map.setTrafficEnabled(propety.trafficEnabled);

            map.getUiSettings().setCompassEnabled(propety.compassEnabled);
            map.getUiSettings().setIndoorLevelPickerEnabled(propety.indoorLevePickerEnabled);
            map.getUiSettings().setMapToolbarEnabled(propety.mapToolbarEnabled);
            map.getUiSettings().setMyLocationButtonEnabled(propety.myLocationButtonEnabled);

            map.getUiSettings().setRotateGesturesEnabled(propety.rorateGesturesEnabled);
            map.getUiSettings().setScrollGesturesEnabled(propety.scrollGesturesEnabled);
            map.getUiSettings().setTiltGesturesEnabled(propety.tiltGesturesEnabled);
            map.getUiSettings().setZoomControlsEnabled(propety.zoomControlsEnabled);
            map.getUiSettings().setZoomGesturesEnabled(propety.zoomGesturesEnabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ズームレベルに変換
     *
     * @param radius 半径
     * @return ズームレベル
     */
    public static float toZoomLevel(float radius) {
        int r = (int) radius;
        float zoom;
        if (r > 10000000) {
            zoom = 0.0f;
        } else if (r > 2000000) {
            zoom = 1.0f;
        } else if (r > 1000000) {
            zoom = 2.0f;
        } else if (r > 500000) {
            zoom = 3.0f;
        } else if (r > 200000) {
            zoom = 4.0f;
        } else if (r > 100000) {
            zoom = 5.0f;
        } else if (r > 50000) {
            zoom = 6.0f;
        } else if (r > 20000) {
            zoom = 7.0f;
        } else if (r > 10000) {
            zoom = 8.0f;
        } else if (r > 5000) {
            zoom = 10.0f;
        } else if (r > 2000) {
            zoom = 11.0f;
        } else if (r > 1000) {
            zoom = 12.0f;
        } else if (r > 500) {
            zoom = 13.0f;
        } else if (r > 200) {
            zoom = 14.0f;
        } else if (r > 100) {
            zoom = 15.0f;
        } else if (r > 50) {
            zoom = 16.0f;
        } else if (r > 15) {
            zoom = 17.0f;
        } else if (r > 10) {
            zoom = 19.0f;
        } else if (r > 5) {
            zoom = 20.0f;
        } else {
            zoom = 21.0f;
        }
        return zoom;
    }

    /**
     * 半径に変換
     *
     * @param zoom ズームレベル
     * @return 半径
     */
    public static float toRadius(float zoom) {
        int radius;
        if (zoom == 0.0f) {
            radius = 2000000;
        } else if (zoom <= 1.0f) {
            radius = 2000000;
        } else if (zoom <= 2.0f) {
            radius = 2000000;
        } else if (zoom <= 3.0f) {
            radius = 1000000;
        } else if (zoom <= 4.0f) {
            radius = 500000;
        } else if (zoom <= 5.0f) {
            radius = 200000;
        } else if (zoom <= 7.0f) {
            radius = 100000;
        } else if (zoom <= 8.0f) {
            radius = 50000;
        } else if (zoom <= 9.0f) {
            radius = 20000;
        } else if (zoom <= 10.0f) {
            radius = 10000;
        } else if (zoom <= 11.0f) {
            radius = 5000;
        } else if (zoom <= 12.0f) {
            radius = 2000;
        } else if (zoom <= 13.0f) {
            radius = 1000;
        } else if (zoom <= 14.0f) {
            radius = 500;
        } else if (zoom <= 15.0f) {
            radius = 200;
        } else if (zoom <= 17.0f) {
            radius = 100;
        } else if (zoom <= 18.0f) {
            radius = 50;
        } else if (zoom <= 19.0f) {
            radius = 15;
        } else if (zoom <= 20.0f) {
            radius = 10;
        } else {
            radius = 5;
        }
        return radius;
    }

    /**
     * ラベル取得
     *
     * @param context コンテキスト
     * @param item 項目
     * @return ラベル
     */
    public static String getLabel(Context context, GeofenceAsset item) {
        String result = getAddress(context, new LatLng(item.latitude, item.longitude));
        if (result.equals("-")) {
            StringBuilder sb = new StringBuilder();
            sb.append(context.getString(R.string.no_description));
            result = sb.toString();
            sb.delete(0, sb.length());
        }
        return result;
    }

    /**
     * 住所取得
     *
     * @param context コンテキスト
     * @param latlng 緯度・経度
     * @return 住所
     */
    public static String getAddress(Context context, LatLng latlng) {
        String addressString = "-";
        try {
            Geocoder geoCorder = new Geocoder(context, Locale.getDefault());
            List<Address> addressList = geoCorder.getFromLocation(latlng.latitude,
                    latlng.longitude, 1);
            if (addressList != null && addressList.size() != 0) {
                Address addr = addressList.get(0);
                StringBuilder address = new StringBuilder().
                        append(STRING(addr.getSubLocality())).
                        append(STRING(addr.getThoroughfare())).
                        append(STRING(addr.getSubThoroughfare()));
                addressString = address.toString();
                address.delete(0, address.length());
                if (addressString.isEmpty()) {
                    addressString = "-";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addressString;
    }

    /**
     * ラベル取得
     *
     * @param addr 住所
     * @return ラベル
     */
    public static String getLabel(Address addr) {
        String addressString;
        StringBuilder address = new StringBuilder().
                append(STRING(addr.getSubLocality())).
                append(STRING(addr.getThoroughfare())).
                append(STRING(addr.getSubThoroughfare()));
        addressString = address.toString();
        address.delete(0, address.length());
        if (addressString.isEmpty()) {
            addressString = addr.getLatitude() + ", " + addr.getLongitude();
        }
        return addressString;
    }

    /**
     * NULLの場合は、文字なしとする文字
     *
     * @param string 文字
     * @return 文字
     */
    private static String STRING(String string) {
        if (string == null) {
            string = "";
        }
        if (string.equals("null")) {
            string = "";
        }
        return string;
    }

    /**
     * 選択状態の確認
     *
     * @return 選択状態
     */
    public static boolean isSelected(List<GeofenceAsset> collection) {
        boolean result = false;
        // 製品の選択状態を確認する
        for (GeofenceAsset geofence : collection) {
            // 選択状態を確認した
            if (geofence.isSelected) {
                result = true;
                break;
            }
        }
        return result;
    }

}
