// IHistoryService.aidl
package jp.osaka.cherry.addressbook.service.history;

import jp.osaka.cherry.addressbook.service.history.History;

interface IHistoryProxy {
    /**
     * 設定
     */
    void setHistory(in History history);
}
