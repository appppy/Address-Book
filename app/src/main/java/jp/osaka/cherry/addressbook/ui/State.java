package jp.osaka.cherry.addressbook.ui;


import jp.osaka.cherry.addressbook.constants.LAYOUT;
import jp.osaka.cherry.addressbook.constants.SELECTION;

import static jp.osaka.cherry.addressbook.constants.SELECTION.UNSELECTED;

/**
 * 状態
 */
public class State {

    /**
     * @serial コンテンツ識別子
     */
    private int id;

    /**
     * @serial レジューム状態の有無
     */
    private boolean isResumed = false;

    /**
     * @serial 選択状態
     */
    private SELECTION selection = UNSELECTED;

    /**
     * @serial 表示状態
     */
    private LAYOUT layout = LAYOUT.LINEAR;

    /**
     * @serial コールバック
     */
    private Callbacks mCallbacks;

    /**
     * レジューム状態の設定
     */
    public void setResumed(boolean onoff) {
        isResumed = onoff;
    }

    /**
     * レジューム状態の有無の取得
     *
     * @return レジューム状態の有無
     */
    public boolean isResumed() {
        return isResumed;
    }

    /**
     * コールバック登録
     *
     * @param callbacks コールバック
     */
    public void registerCallacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    /**
     * コールバック解除
     */
    public void unregisterCallacks() {
        mCallbacks = null;
    }

    /**
     * コンテンツIDの設定
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * コンテンツIDの取得
     */
    public int getId() {
        return id;
    }

    /**
     * 選択状態の取得
     *
     * @return 選択状態
     */
    public SELECTION getSelection() {
        return selection;
    }

    /**
     * 選択状態の変更
     *
     * @return 遷移の有無
     */
    public boolean changeSelection(SELECTION selection) {
        boolean result = false;
        // 状態の確認
        if (this.selection != selection) {
            // 状態の変更
            this.selection = selection;
            // 変更通知
            if (mCallbacks != null) {
                mCallbacks.onSelectChanged(this);
            }
            result = true;
        }
        return result;
    }


    /**
     * 表示状態の取得
     *
     * @return 表示状態
     */
    public LAYOUT getLayout() {
        return layout;
    }

    /**
     * 表示状態の変更
     */
    public void changeLayout(LAYOUT display) {
        // 状態の確認
        if (this.layout != display) {
            // 状態の変更
            this.layout = display;
            // 変更通知
            if (mCallbacks != null) {
                mCallbacks.onDisplayChanged(this);
            }
        }
    }

    /**
     * コールバックインタフェース
     */
    public interface Callbacks {
        /**
         * 選択状態変更通知
         */
        void onSelectChanged(State state);

        /**
         * 表示状態変更通知
         */
        void onDisplayChanged(State state);
    }
}
