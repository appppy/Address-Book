package jp.osaka.cherry.addressbook.service.timeline;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import static jp.osaka.cherry.addressbook.service.timeline.TimelineColumns.DATE;
import static jp.osaka.cherry.addressbook.service.timeline.TimelineColumns.ID;
import static jp.osaka.cherry.addressbook.service.timeline.TimelineColumns.MESSAGE;
import static jp.osaka.cherry.addressbook.service.timeline.TimelineColumns.TABLE;
import static jp.osaka.cherry.addressbook.service.timeline.TimelineColumns.TITLE;


/**
 * タイムラインへルパ
 */
class TimelineHelper extends SQLiteOpenHelper {

    /**
     * @serial DB名
     */
    private static final String DB_NAME = "timeline.db";

    /**
     * @serial バージョン
     */
    private static final int DB_VERSION = 1;

    /**
     * @serial SQL
     */
    private static final String CREATE_TABLE_SQL =
            "create table " + TABLE  + " "
                    + "(" + ID +" integer primary key autoincrement not null,"
                    + DATE + " real not null,"
                    + TITLE + " text not null,"
                    + MESSAGE + " text not null)";

    /**
     * コンストラクタ
     */
    TimelineHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            // データベースのテーブルを作成する
            sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // 処理なし
    }
}
