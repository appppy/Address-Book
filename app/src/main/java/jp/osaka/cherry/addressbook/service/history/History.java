package jp.osaka.cherry.addressbook.service.history;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 履歴.
 */
public class History implements Parcelable {

    /**
     * @serial 日付
     */
    public long date;

    /**
     * @serial 名前
     */
    public String title;

    /**
     * @serial メッセージ
     */
    public String message;

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
        parcel.writeString(title);
        parcel.writeString(message);
    }

    /**
     * コンストラクタ
     *
     * @param parcel パーシャル
     */
    public History(Parcel parcel) {
        date = parcel.readLong();
        title = parcel.readString();
        message = parcel.readString();
    }

    /**
     * コンストラクタ
     */
    public History() {
    }

    /**
     * コンストラクタ
     */
    public History(long date, String title, String message) {
        this.date = date;
        this.title = title;
        this.message = message;
    }

    /**
     * @serial 生成
     */
    public static final Creator<History> CREATOR =
            new Creator<History>() {

                /**
                 * Parcelableクラス作成
                 * @see Creator#createFromParcel(Parcel)
                 */
                @Override
                public History createFromParcel(Parcel source) {
                    return new History(source);
                }

                /**
                 * 配列生成
                 * @see Creator#newArray(int)
                 */
                @Override
                public History[] newArray(int size) {
                    return new History[size];
                }
            };
}
