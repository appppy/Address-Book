package jp.osaka.cherry.addressbook.service.timeline;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * タイムラインコラム
 */
class TimelineColumns implements BaseColumns {

    /**
     * @serial URIパス
     */
    static final String PATH = "timeline";

    /**
     * @serial コンテントURI
     */
    static final Uri CONTENT_URI = Uri.parse("content://" + TimelineProvider.AUTHORITY + "/" + PATH);

    /**
     * @serial テーブル名
     */
    static final String TABLE = "timeline";

    /**
     * @serial カラム 識別子
     */
    static final String ID = "_id";

    /**
     * @serial カラム 日付
     */
    static final String DATE = "date";

    /**
     * @serial カラム タイトル
     */
    static final String TITLE = "title";

    /**
     * @serial カラム メッセージ
     */
    static final String MESSAGE = "message";

    /**
     * コンストラクタ
     */
    private TimelineColumns() {}
}
