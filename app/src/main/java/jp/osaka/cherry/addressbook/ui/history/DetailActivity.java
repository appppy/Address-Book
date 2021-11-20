package jp.osaka.cherry.addressbook.ui.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.ICollectionView;
import jp.osaka.cherry.addressbook.constants.RESULT;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.ui.State;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.CREATE_ITEM;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_ASSETS;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_NAME;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_RESULT;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_SIMPLE_ASSET;


/**
 * 詳細アクティビティ
 */
public class DetailActivity extends AppCompatActivity implements
        DetailListFragment.Callbacks<SimpleAsset> {

    /**
     * @serial 目印
     */
    private final String TAG = "DetailActivity";

    /**
     * @serial データセット
     */
    private ArrayList<SimpleAsset> mDataSet;

    /**
     * @serial 状態
     */
    private final State mState = new State();

    /**
     * インテントの生成
     *
     * @param context コンテキスト
     * @param item    項目
     * @return インテント
     */
    public static Intent createIntent(Context context, ArrayList<SimpleAsset> item, String name) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_ASSETS, item);
        bundle.putString(EXTRA_NAME, name);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        setTheme(R.style.AppTheme_BlueGrey);

        // インテントの取得
        Intent intent = getIntent();
        mDataSet = intent.getParcelableArrayListExtra(EXTRA_ASSETS);
        String name = intent.getStringExtra(EXTRA_NAME);

        // レイアウトの設定
        jp.osaka.cherry.addressbook.databinding.ActivityHistoryDetailBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_history_detail);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(name);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        // 表示の設定
        setView();

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

        mState.setResumed(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        mState.setResumed(false);

        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // メニュー設定
        getMenuInflater().inflate(R.menu.history_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // レジューム状態の確認
        if(!mState.isResumed()) {
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.menu_open: {
                setResult();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#enter");
        }
        // 結果確認
        if (requestCode == CREATE_ITEM.ordinal()) {
            if (resultCode == RESULT_OK) {
                // データの取得
                Bundle bundle = data.getExtras();
                mDataSet = Objects.requireNonNull(bundle).getParcelable(EXTRA_SIMPLE_ASSET);
                setView();
            }
        }
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#leave");
        }
    }

    /**
     * 表示の設定
     */
    private void setView() {
        // 一覧表示の更新
        updateCollectionView(DetailListHelper.toList(mDataSet));
    }

    /**
     * 一覧表示の更新
     *
     * @param collection 一覧
     */
    private void updateCollectionView(ArrayList<SimpleAsset> collection) {
        // 一覧表示の取得
        ICollectionView<SimpleAsset> mCollectionView = DetailListFragment.newInstance(collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
                .commit();
    }

    /**
     * 結果の設定
     */
    private void setResult() {
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_ASSETS, mDataSet);
        bundle.putString(EXTRA_RESULT, RESULT.FINISH.name());
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        //結果の設定
        setResult();
        super.onBackPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedMore(ICollectionView<SimpleAsset> collectionView, View view, SimpleAsset item) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelected(ICollectionView<SimpleAsset> collectionView, View view, SimpleAsset item) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedChanged(ICollectionView<SimpleAsset> collectionView, View view, SimpleAsset item, Collection<? extends SimpleAsset> collection) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSwiped(ICollectionView<SimpleAsset> collectionView, SimpleAsset item) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(ICollectionView<SimpleAsset> view) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollFinished(ICollectionView<SimpleAsset> view) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMoveChanged(ICollectionView<SimpleAsset> view, Collection<? extends SimpleAsset> collection) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdated(ICollectionView<SimpleAsset> view, Collection<? extends SimpleAsset> collection) {
        // 処理なし
    }
}
