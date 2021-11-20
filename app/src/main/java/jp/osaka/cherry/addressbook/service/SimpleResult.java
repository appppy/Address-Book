package jp.osaka.cherry.addressbook.service;

import java.util.Collection;

import jp.osaka.cherry.addressbook.utils.controller.status.BaseStatus;


/**
 * 結果
 */
class SimpleResult extends BaseStatus {

    /**
     * @serial 一覧
     */
    public Collection<SimpleAsset> collection;

    /**
     * コンストラクタ
     *
     * @param collection 一覧
     */
    SimpleResult(Collection<SimpleAsset> collection) {
        this.collection = collection;
    }

}
