package jp.osaka.cherry.addressbook.service.timeline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static jp.osaka.cherry.addressbook.Config.LOG_I;

/**
 * タイムラインアクセサ
 */
public class TimelineAccessor {

    /**
     * @serial 最大数
     */
    private static final int MAX_TIMELINE = 100;

    /**
     * @serial 目印
     */
    private static final String TAG = "TimelineAccessor";

    /**
     * タイムラインの取得
     *
     * @return 履歴
     */
    static List<TimelinePoint> getData(Context context) {
        if (LOG_I) {
            Log.i(TAG, "getData#enter");
        }
        List<TimelinePoint> result = new ArrayList<>();

        try (Cursor cursor = context.getContentResolver().query(TimelineColumns.CONTENT_URI, null, null, null, null)) {
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    TimelinePoint history = new TimelinePoint();

                    int index = 1;
                    history.date = cursor.getLong(index++);
                    history.title = cursor.getString(index++);
                    history.message = cursor.getString(index);

                    result.add(history);
                }
            }
        } catch (IllegalArgumentException | UnsupportedOperationException | ClassCastException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        // 終了時にはCursorをcloseする
        if (LOG_I) {
            Log.i(TAG, "getData#leave");
        }
        return result;
    }

    /**
     * タイムラインの挿入
     */
    static public void insert(Context context, TimelinePoint history) {
        if (LOG_I) {
            Log.i(TAG, "insert#enter");
        }

        int recordCnt;
        try {
            ContentValues insertValue = new ContentValues();

            insertValue.put(TimelineColumns.DATE, history.date);
            insertValue.put(TimelineColumns.TITLE, history.title);
            insertValue.put(TimelineColumns.MESSAGE, history.message);

            context.getContentResolver().insert(TimelineColumns.CONTENT_URI, insertValue);

            Cursor cursor = context.getContentResolver().query(TimelineColumns.CONTENT_URI, null, null, null, null);
            if (cursor == null) {
                return;
            }
            try {
                recordCnt = cursor.getCount();
                if (recordCnt == MAX_TIMELINE) {
                    cursor.close();
                    return;
                } else if (recordCnt > MAX_TIMELINE) {
                    int count = recordCnt - MAX_TIMELINE;

                    for (int i = 0; i < count; i++) {
                        cursor.moveToFirst();
                        int delIndex = cursor.getInt(0);

                        context.getContentResolver().delete(TimelineColumns.CONTENT_URI, TimelineColumns.ID + " = ?", new String[]{String.valueOf(delIndex)});
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (LOG_I) {
            Log.i(TAG, "insert#leave");
        }
    }

    /**
     * タイムラインの削除
     */
    static public void delete(Context context) {
        if (LOG_I) {
            Log.i(TAG, "delete#enter");
        }

        Cursor cursor = context.getContentResolver().query(TimelineColumns.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return;
        }
        try {
            cursor.moveToFirst();
            do {
                int delIndex = cursor.getInt(0);
                context.getContentResolver().delete(TimelineColumns.CONTENT_URI, TimelineColumns.ID + " = ?", new String[]{String.valueOf(delIndex)});
            } while(cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        if (LOG_I) {
            Log.i(TAG, "delete#leave");
        }
    }

}
