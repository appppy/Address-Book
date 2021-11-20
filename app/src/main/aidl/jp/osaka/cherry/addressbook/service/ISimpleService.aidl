
package jp.osaka.cherry.addressbook.service;

import jp.osaka.cherry.addressbook.service.ISimpleServiceCallback;
import jp.osaka.cherry.addressbook.service.SimpleAsset;

interface ISimpleService {
    /**
     * コールバック登録
     */
    void registerCallback(ISimpleServiceCallback callback);

    /**
     * コールバック解除
     */
    void unregisterCallback(ISimpleServiceCallback callback);

    /**
     * 設定
     */
    void setList(in List<SimpleAsset> list);

    /**
     * 取得
     */
    void getList();
}
