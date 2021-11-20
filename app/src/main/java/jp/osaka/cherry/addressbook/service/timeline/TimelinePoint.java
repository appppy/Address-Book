package jp.osaka.cherry.addressbook.service.timeline;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;

/**
 * タイムポイント
 */
public class TimelinePoint implements Parcelable {

    /**
     * @serial 日付
     */
    @ColumnInfo(name = "date")
    public long date;

    /**
     * @serial タイトル
     */
    @ColumnInfo(name = "title")
    public String title;

    /**
     * @serial メッセージ
     */
    @ColumnInfo(name = "message")
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
    public TimelinePoint(Parcel parcel) {
        date = parcel.readLong();
        title = parcel.readString();
        message = parcel.readString();
    }

    /**
     * コンストラクタ
     */
    public TimelinePoint() {
    }

    /**
     * コンストラクタ
     */
    public TimelinePoint(long date, String title, String message) {
        this.date = date;
        this.title = title;
        this.message = message;
    }

    /**
     * @serial 生成
     */
    public static final Creator<TimelinePoint> CREATOR =
            new Creator<TimelinePoint>() {

                /**
                 * Parcelableクラス作成
                 * @see Creator#createFromParcel(Parcel)
                 */
                @Override
                public TimelinePoint createFromParcel(Parcel source) {
                    return new TimelinePoint(source);
                }

                /**
                 * 配列生成
                 * @see Creator#newArray(int)
                 */
                @Override
                public TimelinePoint[] newArray(int size) {
                    return new TimelinePoint[size];
                }
            };

    /**
     * @serial カテゴリ数
     */
    private int mCategory = 2;

    /**
     * カテゴリの設定
     *
     * @param category カテゴリ番号
     */
    public void setCategory(int category) {
        mCategory = category;
    }

    /**
     * カテゴリ取得
     *
     * @return カテゴリ番号
     */
    public int getCategory() {
        return mCategory;
    }
}
