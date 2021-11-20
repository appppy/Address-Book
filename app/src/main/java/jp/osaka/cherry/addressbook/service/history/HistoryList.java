package jp.osaka.cherry.addressbook.service.history;

import java.util.Collection;

import jp.osaka.cherry.addressbook.utils.controller.status.BaseStatus;


/**
 * 履歴一覧
 */
class HistoryList extends BaseStatus {

    /**
     * @serial 一覧
     */
    public Collection<History> collection;

    /**
     * コンストラクタ
     *
     * @param collection 一覧
     */
    HistoryList(Collection<History> collection) {
        this.collection = collection;
    }

}
