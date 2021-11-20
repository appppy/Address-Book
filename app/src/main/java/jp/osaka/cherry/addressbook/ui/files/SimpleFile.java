package jp.osaka.cherry.addressbook.ui.files;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ファイル
 */
public class SimpleFile implements Parcelable {

    /**
     * @serial 日付
     */
    public long date;

    /**
     * @serial 名
     */
    public String name;

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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(date);
        parcel.writeString(name);
    }

    /**
     * コンストラクタ
     *
     * @param parcel パーシャル
     */
    private SimpleFile(Parcel parcel) {
        date = parcel.readLong();
        name = parcel.readString();
    }

    /**
     * コンストラクタ
     */
    SimpleFile() {
    }

    /**
     * @serial 生成
     */
    public static final Creator<SimpleFile> CREATOR =
            new Creator<SimpleFile>() {

                /**
                 * Parcelableクラス作成
                 * @see Creator#createFromParcel(Parcel)
                 */
                @Override
                public SimpleFile createFromParcel(Parcel source) {
                    return new SimpleFile(source);
                }

                /**
                 * 配列生成
                 * @see Creator#newArray(int)
                 */
                @Override
                public SimpleFile[] newArray(int size) {
                    return new SimpleFile[size];
                }
            };
}
