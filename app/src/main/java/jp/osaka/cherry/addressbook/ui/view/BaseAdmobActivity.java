
package jp.osaka.cherry.addressbook.ui.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import jp.osaka.cherry.addressbook.R;


/**
 * 基本モバイル広告アクティビティ
 */
public abstract class BaseAdmobActivity extends AppCompatActivity {
    /**
     * @serial モバイル広告の表示時間
     */
    static private final int TIMEOUT_ADMOB;

    static {
        TIMEOUT_ADMOB = 30 * 1000;
    }

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial タスク
     */
    private final Runnable mTask = () -> {
        View AdMob = findViewById(R.id.AdMob);
        if (AdMob != null) {
            AdMob.setVisibility(View.INVISIBLE);
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        // モバイル広告の表示
        View AdMob = findViewById(R.id.AdMob);
        if (AdMob != null) {
            AdMob.setVisibility(View.VISIBLE);
        }

        // 実行
        mHandler.postDelayed(mTask, TIMEOUT_ADMOB);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        // モバイル広告の非表示
        View AdMob = findViewById(R.id.AdMob);
        if (AdMob != null) {
            AdMob.setVisibility(View.INVISIBLE);
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
