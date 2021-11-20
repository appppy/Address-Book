
package jp.osaka.cherry.addressbook.service.timeline;

import java.util.Collection;

import jp.osaka.cherry.addressbook.utils.controller.status.BaseStatus;


/**
 * タイムライン
 */
class Timeline extends BaseStatus {

    /**
     * @serial 一覧
     */
    public Collection<TimelinePoint> collection;

    /**
     * コンストラクタ
     *
     * @param collection 一覧
     */
    Timeline(Collection<TimelinePoint> collection) {
        this.collection = collection;
    }
}
