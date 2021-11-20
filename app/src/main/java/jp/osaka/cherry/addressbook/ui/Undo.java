package jp.osaka.cherry.addressbook.ui;


import jp.osaka.cherry.addressbook.constants.ACTION;
import jp.osaka.cherry.addressbook.service.SimpleAsset;

/**
 * 実行
 */
public class Undo {

    /**
     * 識別子
     */
    public ACTION action;

    /**
     * パラメータ
     */
    public int arg;

    /**
     * オブジェクト
     */
    public SimpleAsset object;

    /**
     * コンストラクタ
     *
     * @param action 識別子
     * @param position パラメータ
     * @param object オブジェクト
     */
    public Undo(ACTION action, int position, SimpleAsset object) {
        this.action = action;
        this.arg = position;
        this.object = object;
    }
}
