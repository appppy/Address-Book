package jp.osaka.cherry.addressbook.constants;

/**
 * アクティビティ遷移定義
 */
public enum ActivityTransition {
    PERMISSIONS,
    CREATE_ITEM,
    EDIT_ITEM,
    DETAIL_ITEM,
    CHOOSER,
    SYNC_FILE,
    OPEN_FILE,
    MODIFY_GEOFENCE,
    OPEN_HISTORY;
    /**
     * 値に合致する enum 定数を返す。
     *
     * @param index インデックス
     * @return メッセージ
     */
    public static ActivityTransition get(int index) {
        // 値から enum 定数を特定して返す処理
        for (ActivityTransition request : ActivityTransition.values()) {
            if (request.ordinal() == index) {
                return request;
            }
        }
        return null; // 特定できない場合
    }
}