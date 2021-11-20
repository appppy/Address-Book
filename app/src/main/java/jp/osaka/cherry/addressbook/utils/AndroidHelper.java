package jp.osaka.cherry.addressbook.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

/**
 * アンドロイドヘルパ
 */
public class AndroidHelper {
    /**
     * ハニーコンボの有無
     *
     * @return ハニーコンボ
     */
    @SuppressLint("ObsoleteSdkInt")
    private static boolean isHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * ハニーコンボタブレットの有無
     *
     * @return ハニーコンボタブレット
     */
    public static boolean isHoneycombTablet(Context context) {
        return isHoneycomb() && (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                == Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
}
