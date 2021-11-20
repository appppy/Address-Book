
package jp.osaka.cherry.addressbook.admob;

import com.google.android.gms.ads.AdSize;

/**
 * モバイル広告フラグメントの実装
 */
public class AdMobFragmentImpl extends AdMobFragment {

    /**
     * {@inheritDoc}
     */
    @Override
    protected AdSize getAdSize() {
        return AdSize.BANNER;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    protected String getUnitId() { return  "キーを設定してください";}
}