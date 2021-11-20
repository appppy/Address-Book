package jp.osaka.cherry.addressbook.ui.view.timeline;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.databinding.ActivityTimelineBinding;
import jp.osaka.cherry.addressbook.service.timeline.TimelineClient;
import jp.osaka.cherry.addressbook.service.timeline.TimelinePoint;
import jp.osaka.cherry.addressbook.ui.State;
import jp.osaka.cherry.addressbook.ui.view.BaseAdmobActivity;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_CONTENT;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.getStartActivity;

/**
 * タイムライン画面
 */
public class TimelineActivity extends BaseAdmobActivity implements
        ICollectionView.Callbacks,
        DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        State.Callbacks, TimelineClient.Callbacks {

    /**
     * @serial バインディング
     */
    private ActivityTimelineBinding mBinding;

    /**
     * @serial プリファレンス
     */
    private SharedPreferences mPref;

    /**
     * @serial 状態
     */
    private final State mState = new State();

    /**
     * @serial クライアント
     */
    private final TimelineClient mClient = new TimelineClient(this, this);

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial データセット
     */
    private final ArrayList<TimelinePoint> mDataSet = new ArrayList<>();

    /**
     * @serial トグル
     */
    private ActionBarDrawerToggle mToggle;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String TAG = "TimelineActivity";
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // プリファレンスの設定
        mPref = getDefaultSharedPreferences(this);

        // テーマの設定
        setTheme(R.style.AppTheme_BlueGrey);

        // レイアウト設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.timeline);
        }

        // ナビゲーションの設定
        enableNavigationDrawer();

        // 登録
        mState.setId(R.id.timeline);
        mState.registerCallacks(this);

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        // レジューム状態の設定
        mState.setResumed(true);
        // サービスの接続
        mClient.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        // レジューム状態の設定
        mState.setResumed(false);
        // サービスの非接続
        mClient.disconnect();
        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        // 解除
        mState.unregisterCallacks();
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mState.setId(item.getItemId());
        // コンテンツ識別子の保存
        mPref.edit().putInt(EXTRA_CONTENT, item.getItemId()).apply();
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerOpened(@NonNull View drawerView) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        if (mState.getId() != R.id.timeline) {
            Intent intent = getIntent();
            intent.setClass(getApplicationContext(), getStartActivity(mState.getId()));
            startActivity(intent);
            overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
            finish();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawerStateChanged(int newState) {
        // 処理なし
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectChanged(State state) {
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisplayChanged(State state) {
        // 処理なし
    }

    /**
     * ナビゲーションの有効
     */
    private void enableNavigationDrawer() {
        if (mToggle == null) {
            mToggle = new ActionBarDrawerToggle(
                    this,
                    mBinding.drawerLayout,
                    mBinding.toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        }
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.setToolbarNavigationClickListener(this);
        mToggle.syncState();
        mBinding.drawerLayout.addDrawerListener(mToggle);
        mBinding.drawerLayout.addDrawerListener(this);
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mBinding.navView.setNavigationItemSelectedListener(this);
    }

    /**
     * 表示の更新
     *
     * @param collection 一覧
     */
    private void updateView(ArrayList<TimelinePoint> collection) {
        // プログレスの終了
        updateProgressBar();
        // 空表示の更新
        updateEmptyView(collection);
        // メニューの更新
        updateMenu();
        // 一覧表示の更新
        updateCollectionView(collection);
        // タイトルの更新
        updateTitle(collection);
    }

    /**
     * プログレスバーの更新
     *
     */
    private void updateProgressBar() {
        ProgressBar bar = mBinding.productImageLoading;
        bar.setVisibility(View.INVISIBLE);
    }

    /**
     * 空表示の更新
     *
     * @param collection 一覧
     */
    private void updateEmptyView(List<TimelinePoint> collection) {
        // 空表示の更新
        ImageView view = mBinding.emptyView;
        boolean isEmpty = collection.isEmpty();
        if (isEmpty) {
            view.setVisibility(View.VISIBLE);
        } else {

            view.setVisibility(View.GONE);
        }
    }

    /**
     * メニューの更新
     *
     */
    private void updateMenu() {
        invalidateOptionsMenu();
    }

    /**
     * 一覧表示の更新
     *
     * @param collection 一覧
     */
    private void updateCollectionView(ArrayList<TimelinePoint> collection) {
        // 一覧表示の取得
        ICollectionView mCollectionView = getCollectionView(collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
                .commit();
    }

    /**
     * タイトルの更新
     */
    private void updateTitle(ArrayList<TimelinePoint> collection) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getString(R.string.timeline));
        if(!collection.isEmpty()) {
            sb.append(" ").append(collection.size());
        }
        mBinding.toolbar.setTitle(sb.toString());
        sb.delete(0, sb.length());
    }

    /**
     * 一覧表示の取得
     *
     * @param collection 一覧
     * @return 一覧表示
     */
    private ICollectionView getCollectionView(ArrayList<TimelinePoint> collection) {
        return TimelineFragment.newInstance(collection);
    }

    /**
     * スクロール開始
     *
     * @param view 一覧表示
     */
    @Override
    public void onScroll(ICollectionView view) {

    }

    /**
     * スクロール終了
     *
     * @param view 一覧表示
     */
    @Override
    public void onScrollFinished(ICollectionView view) {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

    /**
     * コマンド更新通知
     *
     * @param object   オブジェクト
     * @param timeline タイムライン
     */
    @Override
    public void onUpdatedTimeline(Object object, final List<TimelinePoint> timeline) {
        mHandler.post(() -> {
            // データ設定
            mDataSet.clear();
            mDataSet.addAll(toSortByDateCollection(timeline));
            Collections.reverse(mDataSet);
            // 表示の更新
            updateView(mDataSet);
        });
    }

    /**
     * 作成日でソートした一覧の取得
     *
     * @param collection 一覧
     * @return 作成日でソートした一覧
     */
    public static Collection<TimelinePoint> toSortByDateCollection(Collection<TimelinePoint> collection) {
        Collections.sort((List<TimelinePoint>) collection, (lhs, rhs) -> (int) (lhs.date - rhs.date));
        return collection;
    }
}
