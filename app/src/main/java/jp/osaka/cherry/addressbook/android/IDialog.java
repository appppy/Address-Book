package jp.osaka.cherry.addressbook.android;

/**
 * ダイアログ
 */
public interface IDialog {

    /**
     * @serial コールバック定義
     */
    interface Callbacks<T> {
        /**
         * ポジティブボタン押下.
         *
         * @param dialog ダイアログ
         * @param arg 設定値
         */
        void onPositiveButtonClicked(IDialog dialog, T arg);

        /**
         * ネガティブボタン押下.
         *
         * @param dialog ダイアログ
         * @param arg 設定値
         */
        void onNegativeButtonClicked(IDialog dialog, T arg);
    }
}
