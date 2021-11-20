// IHistoryService.aidl
package jp.osaka.cherry.addressbook.service.history;

import jp.osaka.cherry.addressbook.service.history.IHistoryServiceCallback;
import jp.osaka.cherry.addressbook.service.history.History;

interface IHistoryService {
    /**
     * コールバック登録
     */
    void registerCallback(IHistoryServiceCallback callback);

    /**
     * コールバック解除
     */
    void unregisterCallback(IHistoryServiceCallback callback);

    /**
     * 取得
     */
    void getHistoryList();

    /**
     * 削除
     */
    void delete();
}
