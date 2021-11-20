package jp.osaka.cherry.addressbook.service.history;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 履歴コラム
 */
class HistoryColumns implements BaseColumns {
    /**
     * @serial URIパス
     */
    static final String PATH = "history";

    /**
     * @serial コンテントURI
     */
    static final Uri CONTENT_URI = Uri.parse("content://" + HistoryProvider.AUTHORITY + "/" + PATH);

    /**
     * @serial テーブル名
     */
    static final String TABLE = "history";

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
    private HistoryColumns() {}
}
