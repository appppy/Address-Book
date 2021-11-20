
package jp.osaka.cherry.addressbook.service;

import jp.osaka.cherry.addressbook.service.SimpleAsset;

interface ISimpleServiceCallback {
    /**
     * 更新
     */
    void update(in List<SimpleAsset> list);
}
