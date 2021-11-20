
package jp.osaka.cherry.addressbook.admob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


/**
 * モバイル広告フラグメント
 */
public abstract class AdMobFragment extends androidx.fragment.app.Fragment {

    /**
     * @serial モバイル広告表示
     */
    private AdView mAdView;

    /**
     * @serial モバイル広告要求
     */
    private AdRequest mAdRequest;

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // モバイル広告の生成
        mAdView = new AdView(requireActivity());
        mAdView.setAdUnitId(getUnitId());
        mAdView.setAdSize(getAdSize());

        // モバイル広告要求の生成
        mAdRequest = new AdRequest.Builder().build();

        // リスナの登録
        final AdListener listener = getAdListener();
        if (listener != null) {
            mAdView.setAdListener(listener);
        }

        return mAdView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // モバイル広告の読み込み
        mAdView.loadAd(mAdRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // モバイル広告の削除
        mAdView.destroy();
    }

    /**
     * モバイル広告のサイズ取得
     *
     * @return モバイル広告のサイズ
     */
    protected abstract AdSize getAdSize();

    /**
     * ユニットIDを取得
     *
     * @return ユニットID
     */
    protected abstract String getUnitId();

    /**
     * モバイル広告のリスナ取得
     *
     * @return モバイル広告のリスナ
     */
    protected AdListener getAdListener() {
        return null;
    }

}
