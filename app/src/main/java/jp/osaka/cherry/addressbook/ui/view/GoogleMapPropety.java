package jp.osaka.cherry.addressbook.ui.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * グーグルマップのプロパティ
 */
public class GoogleMapPropety implements Parcelable {
    /**
     * @serial 生成
     */
    public static final Creator<GoogleMapPropety> CREATOR =
            new Creator<GoogleMapPropety>() {

                /**
                 * Parcelableクラス作成
                 * @see Creator#createFromParcel(Parcel)
                 */
                @Override
                public GoogleMapPropety createFromParcel(Parcel source) {
                    return new GoogleMapPropety(source);
                }

                /**
                 * 配列生成
                 * @see Creator#newArray(int)
                 */
                @Override
                public GoogleMapPropety[] newArray(int size) {
                    return new GoogleMapPropety[size];
                }
            };

    /**
     * @serial 3D buildings layer is enabled.
     */
    public boolean isBuildingEnabled;

    /**
     * @serial indoor maps are currently enabled.
     */
    public boolean isIndoorEnabled;

    /**
     * @serial type of map tiles
     */
    public int mapType;

    /**
     * @serial the map is drawing traffic data. This is subject to the availability of traffic data.
     */
    public boolean trafficEnabled;

    /**
     * @serial the compass is enabled/disabled.
     */
    public boolean compassEnabled;

    /**
     * @serial the indoor level picker is enabled/disabled.
     */
    public boolean indoorLevePickerEnabled;

    /**
     * @serial the Map Toolbar is enabled/disabled.
     */
    public boolean mapToolbarEnabled;

    /**
     * @serial the my-location button is enabled/disabled.
     */
    public boolean myLocationButtonEnabled;

    /**
     * @serial rotate gestures are enabled/disabled.
     */
    public boolean rorateGesturesEnabled;

    /**
     * @serial scroll gestures are enabled/disabled.
     */
    public boolean scrollGesturesEnabled;

    /**
     * @serial tilt gestures are enabled/disabled.
     */
    public boolean tiltGesturesEnabled;

    /**
     * @serial the zoom controls are enabled/disabled.
     */
    public boolean zoomControlsEnabled;

    /**
     * @serial zoom gestures are enabled/disabled.
     */
    public boolean zoomGesturesEnabled;


    /**
     * コンストラクタ
     */
    public GoogleMapPropety() {

    }

    /**
     * コンストラクタ
     *
     * @param parcel パーシャル
     */
    public GoogleMapPropety(Parcel parcel) {
        isBuildingEnabled = parcel.readByte() != 0;
        isIndoorEnabled = parcel.readByte() != 0;
        mapType = parcel.readInt();
        trafficEnabled = parcel.readByte() != 0;
        compassEnabled = parcel.readByte() != 0;
        indoorLevePickerEnabled = parcel.readByte() != 0;
        mapToolbarEnabled = parcel.readByte() != 0;
        myLocationButtonEnabled = parcel.readByte() != 0;
        rorateGesturesEnabled = parcel.readByte() != 0;
        scrollGesturesEnabled = parcel.readByte() != 0;
        tiltGesturesEnabled = parcel.readByte() != 0;
        zoomControlsEnabled = parcel.readByte() != 0;
        zoomGesturesEnabled = parcel.readByte() != 0;
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
        dest.writeByte((byte) (isBuildingEnabled ? 1 : 0));
        dest.writeByte((byte) (isIndoorEnabled ? 1 : 0));
        dest.writeInt(mapType);
        dest.writeByte((byte) (trafficEnabled ? 1 : 0));
        dest.writeByte((byte) (compassEnabled ? 1 : 0));
        dest.writeByte((byte) (indoorLevePickerEnabled ? 1 : 0));
        dest.writeByte((byte) (mapToolbarEnabled ? 1 : 0));
        dest.writeByte((byte) (myLocationButtonEnabled ? 1 : 0));
        dest.writeByte((byte) (rorateGesturesEnabled ? 1 : 0));
        dest.writeByte((byte) (scrollGesturesEnabled ? 1 : 0));
        dest.writeByte((byte) (tiltGesturesEnabled ? 1 : 0));
        dest.writeByte((byte) (zoomControlsEnabled ? 1 : 0));
        dest.writeByte((byte) (zoomGesturesEnabled ? 1 : 0));
    }
}
