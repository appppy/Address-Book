package jp.osaka.cherry.addressbook.ui;


import jp.osaka.cherry.addressbook.constants.ACTION;
import jp.osaka.cherry.addressbook.service.SimpleAsset;

/**
 * 実行
 */
public class Redo {

    /**
     * 識別子
     */
    public ACTION action;

    /**
     * オブジェクト
     */
    public SimpleAsset object;

    /**
     * コンストラクタ
     *
     * @param action 識別子
     * @param object オブジェクト
     */
    public Redo(ACTION action, SimpleAsset object) {
        this.action = action;
        this.object = object;
    }
}
