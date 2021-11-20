package jp.osaka.cherry.addressbook.utils;

import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 表示ヘルパ
 */
public class ViewHelper {

    /**
     * マージン設定
     *
     * @param view 表示
     * @param margin マージン
     */
    public static void setMargins(ImageView view, int margin) {
        // ImageViewからマージンを取得
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        // 移動させたい距離に変更
        lp.setMargins(margin, margin, margin, margin);
        // ImageViewへ反映
        view.setLayoutParams(lp);
    }

}
