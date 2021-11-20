// IHistoryServiceCallback.aidl
package jp.osaka.cherry.addressbook.service.history;

import jp.osaka.cherry.addressbook.service.history.History;

interface IHistoryServiceCallback {
    /**
     * 更新
     */
    void update(in List<History> history);
}
