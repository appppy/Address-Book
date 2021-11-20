package jp.osaka.cherry.addressbook.ui.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import jp.osaka.cherry.addressbook.BuildConfig;
import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.view.ICollectionView;
import jp.osaka.cherry.addressbook.constants.ActivityTransition;
import jp.osaka.cherry.addressbook.constants.COLOR;
import jp.osaka.cherry.addressbook.constants.LAYOUT;
import jp.osaka.cherry.addressbook.databinding.ActivityCallAdmobBinding;
import jp.osaka.cherry.addressbook.databinding.ActivityCallBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.service.SimpleClient;
import jp.osaka.cherry.addressbook.service.history.History;
import jp.osaka.cherry.addressbook.service.history.HistoryProxy;
import jp.osaka.cherry.addressbook.ui.Redo;
import jp.osaka.cherry.addressbook.ui.State;
import jp.osaka.cherry.addressbook.ui.Undo;
import jp.osaka.cherry.addressbook.ui.view.BaseAdmobActivity;
import jp.osaka.cherry.addressbook.ui.view.EditDialog;
import jp.osaka.cherry.addressbook.ui.view.ListFragment;
import jp.osaka.cherry.addressbook.ui.view.ModuleFragment;
import jp.osaka.cherry.addressbook.utils.AssetHelper;

import static jp.osaka.cherry.addressbook.Config.LOG_D;
import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.ACTION.CHANGE;
import static jp.osaka.cherry.addressbook.constants.ACTION.CREATE;
import static jp.osaka.cherry.addressbook.constants.ACTION.INSERT;
import static jp.osaka.cherry.addressbook.constants.ACTION.MODIFY;
import static jp.osaka.cherry.addressbook.constants.ACTION.REMOVE;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.SYNC_FILE;
import static jp.osaka.cherry.addressbook.constants.COLOR.AMBER;
import static jp.osaka.cherry.addressbook.constants.COLOR.BLUE;
import static jp.osaka.cherry.addressbook.constants.COLOR.BLUE_GREY;
import static jp.osaka.cherry.addressbook.constants.COLOR.BROWN;
import static jp.osaka.cherry.addressbook.constants.COLOR.DEEP_ORANGE;
import static jp.osaka.cherry.addressbook.constants.COLOR.DEEP_PURPLE;
import static jp.osaka.cherry.addressbook.constants.COLOR.GREEN;
import static jp.osaka.cherry.addressbook.constants.COLOR.INDIGO;
import static jp.osaka.cherry.addressbook.constants.COLOR.LIGHT_GREEN;
import static jp.osaka.cherry.addressbook.constants.COLOR.LIME;
import static jp.osaka.cherry.addressbook.constants.COLOR.ORANGE;
import static jp.osaka.cherry.addressbook.constants.COLOR.PINK;
import static jp.osaka.cherry.addressbook.constants.COLOR.PURPLE;
import static jp.osaka.cherry.addressbook.constants.COLOR.RED;
import static jp.osaka.cherry.addressbook.constants.COLOR.WHITE;
import static jp.osaka.cherry.addressbook.constants.COLOR.YELLOW;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_CONTENT;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_FILE_NAME;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LAYOUT;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_SIMPLE_ASSET;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_SORT;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;
import static jp.osaka.cherry.addressbook.constants.LAYOUT.GRID;
import static jp.osaka.cherry.addressbook.constants.LAYOUT.LINEAR;
import static jp.osaka.cherry.addressbook.constants.SELECTION.MULTI_SELECTED;
import static jp.osaka.cherry.addressbook.constants.SELECTION.SELECTED;
import static jp.osaka.cherry.addressbook.constants.SELECTION.SELECTED_ALL;
import static jp.osaka.cherry.addressbook.constants.SELECTION.SELECTING;
import static jp.osaka.cherry.addressbook.constants.SELECTION.UNSELECTED;
import static jp.osaka.cherry.addressbook.constants.SORT.BY_DATE_CREATED;
import static jp.osaka.cherry.addressbook.constants.SORT.BY_DATE_MODIFIED;
import static jp.osaka.cherry.addressbook.constants.SORT.BY_NAME;
import static jp.osaka.cherry.addressbook.service.SimpleAsset.CONTENT.ARCHIVE;
import static jp.osaka.cherry.addressbook.service.SimpleAsset.CONTENT.TRASH;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.getStartActivity;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startDetailActivity_from_Line;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startDetailActivity_from_Module;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startEditActivity;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startFolderActivity;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startHistoryActivity;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startNoteDetailActivity;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startSearchActivity;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.copy;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.getAsset;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.getSelectedCollection;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.isModified;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.toSortByDateCreatedCollection;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.toSortByDateModifiedCollection;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.toSortByNameCollection;


/**
 * 電話番号一覧画面
 */
public class CallActivity extends BaseAdmobActivity implements
        SimpleClient.Callbacks,
        ICollectionView.Callbacks<SimpleAsset>,
        DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        State.Callbacks {

    /**
     * @serial 目印
     */
    private final String TAG = "CallActivity";

    /**
     * @serial プリファレンス
     */
    private SharedPreferences mPref;

    /**
     * @serial 自身
     */
    private Activity mSelf;

    /**
     * @serial 状態
     */
    private final State mState = new State();

    /**
     * @serial データセット
     */
    private final ArrayList<SimpleAsset> mDataSet = new ArrayList<>();

    /**
     * @serial バックアップ
     */
    private final ArrayList<SimpleAsset> mBackup = new ArrayList<>();

    /**
     * @serial 選択データセット
     */
    private final ArrayList<SimpleAsset> mSelected = new ArrayList<>();

    /**
     * @serial クライアント
     */
    private final SimpleClient mClient = new SimpleClient(this, this);

    /**
     * @serial クライアント
     */
    private final HistoryProxy mProxy = new HistoryProxy(this);

    /**
     * @serial ハンドラ
     */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * @serial 戻る
     */
    private final ArrayList<Undo> mUndos = new ArrayList<>();

    /**
     * @serial 進む
     */
    private final ArrayList<Redo> mRedos = new ArrayList<>();

    /**
     * @serial 一覧表示
     */
    private ICollectionView<SimpleAsset> mCollectionView;

    /**
     * @serial トグル
     */
    private ActionBarDrawerToggle mToggle;

    /**
     * @serial ツールバー
     */
    private Toolbar mToolbar;

    /**
     * @serial ドローレイアウト
     */
    private DrawerLayout mDrawerLayout;

    /**
     * @serial ナビゲーション表示
     */
    private NavigationView mNavigationView;

    /**
     * @serial レイアウト
     */
    private CoordinatorLayout mCoordinatorLayout;

    /**
     * @serial プログレスバー
     */
    private ProgressBar mProgressBar;

    /**
     * @serial 空画面表示
     */
    private ImageView mEmptyView;

    /**
     * @serial レイアウト
     */
    private FrameLayout mLayout;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // 自身の取得
        mSelf = this;

        // プリファレンスの設定
        mPref = PreferenceManager.getDefaultSharedPreferences(this);

        // テーマの設定
        setTheme(R.style.AppTheme_LightBlue);

        // レイアウト設定
        if(BuildConfig.DEBUG) {
            ActivityCallBinding  binding = DataBindingUtil.setContentView(this, R.layout.activity_call);
            mToolbar = binding.toolbar;
            mDrawerLayout = binding.drawerLayout;
            mNavigationView = binding.navView;
            mCoordinatorLayout = binding.coordinatorLayout;
            mProgressBar = binding.productImageLoading;
            mEmptyView = binding.emptyView;
           mLayout = binding.mainContainer;
        } else {
            ActivityCallAdmobBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_call_admob);
            mToolbar = binding.toolbar;
            mDrawerLayout = binding.drawerLayout;
            mNavigationView = binding.navView;
            mCoordinatorLayout = binding.coordinatorLayout;
            mProgressBar = binding.productImageLoading;
            mEmptyView = binding.emptyView;
            mLayout = binding.mainContainer;
            MobileAds.initialize(this, initializationStatus -> {
                 //
             });
        }

        // ツールバーの設定
        setSupportActionBar(mToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.call);
        }

        // ナビゲーションの設定
        enableNavigationDrawer();

        // 登録
        mState.setId(R.id.call);
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
        mProxy.connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        // レジューム状態の設定
        mState.setResumed(false);
        // サービスの非接続
        mProxy.disconnect();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#enter");
        }

        // 結果確認
        ActivityTransition type = ActivityTransition.get(requestCode);
        switch (Objects.requireNonNull(type)) {
            case DETAIL_ITEM:
            case EDIT_ITEM: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    SimpleAsset asset = bundle.getParcelable(EXTRA_SIMPLE_ASSET);
                    // レジューム後に動作させる
                    mRedos.clear();
                    mRedos.add(new Redo(MODIFY, asset));
                    break;
                }
                break;
            }
            default: {
                break;
            }
        }
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // 選択状態の取得
            switch (mState.getSelection()) {
                case SELECTED_ALL:
                case MULTI_SELECTED:
                case SELECTING:
                case SELECTED: {
                    // 選択解除
                    mState.changeSelection(UNSELECTED);
                    break;
                }
                default: {
                    super.onBackPressed();
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 選択状態の確認
        switch (mState.getSelection()) {
            case SELECTING:
            case UNSELECTED: {
                if(toList(mDataSet).isEmpty()) {
                    getMenuInflater().inflate(R.menu.main_empty, menu);
                } else {
                    switch (mState.getLayout()) {
                        default:
                        case LINEAR: {
                            if (toList(mDataSet).size() == 1) {
                                getMenuInflater().inflate(R.menu.main_linear_one, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.main_linear, menu);
                            }
                            break;
                        }
                        case GRID: {
                            if (toList(mDataSet).size() == 1) {
                                getMenuInflater().inflate(R.menu.main_grid_one, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.main_grid, menu);
                            }
                            break;
                        }
                    }
                }
                break;
            }
            case SELECTED: {
                getMenuInflater().inflate(R.menu.main_selected_one, menu);
                break;
            }
            case MULTI_SELECTED: {
                getMenuInflater().inflate(R.menu.main_selected, menu);
                break;
            }
            case SELECTED_ALL: {
                if (mSelected.size() == 1) {
                    getMenuInflater().inflate(R.menu.main_selected_all_one, menu);
                } else {
                    getMenuInflater().inflate(R.menu.main_selected_all, menu);
                }
                break;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // レジューム状態の確認
        if(!mState.isResumed()) {
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();

        // 識別子ごとの処理
        switch (id) {
            case R.id.menu_edit_note: {
                SimpleAsset asset = getAsset(mSelected.get(0).uuid, mDataSet);
                mSelected.clear();
                asset.timestamp = System.currentTimeMillis();
                mClient.setList(mDataSet);
                startNoteDetailActivity(mSelf, mSelf, asset);
                return true;
            }
            case R.id.menu_edit: {
                // アセットを取得
                SimpleAsset asset = getAsset(mSelected.get(0).uuid, mDataSet);
                // 選択一覧のクリア
                mSelected.clear();
                // タイムスタンプを保存
                asset.timestamp = System.currentTimeMillis();
                // アセット一覧を設定
                mClient.setList(mDataSet);
                // 編集画面を開始
                startEditActivity(this, this, asset);
                return true;
            }
            case R.id.menu_info: {
                // アセットを取得
                SimpleAsset asset = getAsset(mSelected.get(0).uuid, mDataSet);
                // 選択一覧のクリア
                mSelected.clear();
                // タイムスタンプを保存
                asset.timestamp = System.currentTimeMillis();
                // 一覧を設定
                mClient.setList(mDataSet);
                // 詳細画面を開始
                LAYOUT layout = LAYOUT.valueOf(mPref.getString(EXTRA_LAYOUT, LINEAR.name()));
                if (layout == GRID) {
                    startDetailActivity_from_Module(this, this, asset);
                } else {
                    startDetailActivity_from_Line(this, this, asset);
                }
                return true;
            }
            // 一覧表示
            case R.id.menu_linear: {
                mHandler.post(() -> {
                    if(mState.isResumed()) {
                        mPref.edit().putString(EXTRA_LAYOUT, LINEAR.name()).apply();
                        updateView(toList(mDataSet));
                    }
                });
                return true;
            }
            // ギャラリー表示
            case R.id.menu_grid: {
                mHandler.post(() -> {
                    if(mState.isResumed()) {
                        mPref.edit().putString(EXTRA_LAYOUT, GRID.name()).apply();
                        updateView(toList(mDataSet));
                    }
                });
                return true;
            }
            case R.id.menu_by_name: {
                mPref.edit().putString(EXTRA_SORT, BY_NAME.name()).apply();
                ArrayList<SimpleAsset> collection = (ArrayList<SimpleAsset>) toSortByNameCollection(mDataSet);
                updateView(toList(collection));
                mClient.setList(collection);
                break;
            }
            case R.id.menu_by_date_modified: {
                mPref.edit().putString(EXTRA_SORT, BY_DATE_MODIFIED.name()).apply();
                ArrayList<SimpleAsset> collection = (ArrayList<SimpleAsset>) toSortByDateModifiedCollection(mDataSet);
                Collections.reverse(collection);
                updateView(toList(collection));
                mClient.setList(collection);
                break;
            }
            case R.id.menu_by_date_created: {
                mPref.edit().putString(EXTRA_SORT, BY_DATE_CREATED.name()).apply();
                ArrayList<SimpleAsset> collection = (ArrayList<SimpleAsset>) toSortByDateCreatedCollection(mDataSet);
                Collections.reverse(collection);
                updateView(toList(collection));
                mClient.setList(collection);
                break;
            }
            case R.id.menu_swap_vert: {
                Collections.reverse(mDataSet);
                updateView(toList(mDataSet));
                return true;
            }
            case R.id.menu_sync_file: {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/*");
                startActivityForResult(intent, SYNC_FILE.ordinal());
                return true;
            }
            case R.id.menu_search: {
                startSearchActivity(this);
                return true;
            }
            case R.id.menu_archive: {
                // アーカイブに移動
                mHandler.post(new ArchiveRunner(mSelected));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_trash: {
                // ゴミ箱に移動
                mHandler.post(new TrashRunner(mSelected));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_copy: {
                // コピー
                mHandler.post(new CopyRunner(mSelected));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_selected_all: {
                // 全選択
                mState.changeSelection(SELECTED_ALL);
                return true;
            }
            case R.id.menu_share: {
                SimpleAsset asset = getAsset(mSelected.get(0).uuid, mDataSet);
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TITLE, asset.displayName);
                    intent.putExtra(Intent.EXTRA_SUBJECT, asset.displayName);
                    intent.putExtra(Intent.EXTRA_PHONE_NUMBER, asset.call);
                    intent.setType("text/plain");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            case R.id.menu_open_file: {
                startFolderActivity(this);
                return true;
            }
            case R.id.menu_save_file: {
                // ファイル名を取得
                String filename = mPref.getString(EXTRA_FILE_NAME, INVALID_STRING_VALUE);

                // 編集ダイアログの表示
                EditDialog fragment = EditDialog.newInstance(filename);
                fragment.show(getSupportFragmentManager(), "EditDialog");

                return true;
            }
            case R.id.menu_open_history: {
                startHistoryActivity(this);
                return true;
            }
            case R.id.menu_leave_history: {
                // 履歴更新
                mProxy.setHistory(new History(System.currentTimeMillis(), String.valueOf(mDataSet.size()), AssetHelper.toJSONString(mDataSet)));
                return true;
            }
            case R.id.menu_white: {
                mHandler.post(new ChangeColorRunner(mSelected, WHITE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_red: {
                mHandler.post(new ChangeColorRunner(mSelected, RED));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_pink: {
                mHandler.post(new ChangeColorRunner(mSelected, PINK));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_purple: {
                mHandler.post(new ChangeColorRunner(mSelected, PURPLE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_deep_purple: {
                mHandler.post(new ChangeColorRunner(mSelected, DEEP_PURPLE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_indigo: {
                mHandler.post(new ChangeColorRunner(mSelected, INDIGO));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_blue: {
                mHandler.post(new ChangeColorRunner(mSelected, BLUE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_green: {
                mHandler.post(new ChangeColorRunner(mSelected, GREEN));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_light_green: {
                mHandler.post(new ChangeColorRunner(mSelected, LIGHT_GREEN));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_lime: {
                mHandler.post(new ChangeColorRunner(mSelected, LIME));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_yellow: {
                mHandler.post(new ChangeColorRunner(mSelected, YELLOW));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_amber: {
                mHandler.post(new ChangeColorRunner(mSelected, AMBER));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_orange: {
                mHandler.post(new ChangeColorRunner(mSelected, ORANGE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_deep_orange: {
                mHandler.post(new ChangeColorRunner(mSelected, DEEP_ORANGE));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_brown: {
                mHandler.post(new ChangeColorRunner(mSelected, BROWN));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            case R.id.menu_blue_grey: {
                mHandler.post(new ChangeColorRunner(mSelected, BLUE_GREY));
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);
                return true;
            }
            default: {
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ナビゲーションドローワ―の有効化
     */
    private void enableNavigationDrawer() {
        // ナビゲーションドローワ―の生成
        if (mToggle == null) {
            mToggle = new ActionBarDrawerToggle(
                    this,
                    mDrawerLayout,
                    mToolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        }
        // ナビゲーションドローワ―の設定
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.setToolbarNavigationClickListener(this);
        mToggle.syncState();
        mDrawerLayout.addDrawerListener(mToggle);
        mDrawerLayout.addDrawerListener(this);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * ナビゲーションドローワ―の無効化
     */
    private void disableNavigationDrawer() {
        // ナビゲーションドローワ―の設定解除
        if (mToggle != null) {
            mToggle.setDrawerIndicatorEnabled(false);
            mToggle.syncState();
            mDrawerLayout.removeDrawerListener(mToggle);
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdated(Object object, final List<SimpleAsset> assets) {
        mHandler.post(() -> {
            boolean result;
            result = mDataSet.isEmpty() || mDataSet.size() != assets.size() || !mDataSet.equals(assets);
            if (result) {
                // データ設定
                mDataSet.clear();
                mDataSet.addAll(assets);
                // データ更新
                updateView(toList(mDataSet));
                // ジオフェンス開始
                //addGeofencesHandler(mDataSet);
            }

            // 次の動作を指定
            for (Redo doAction : mRedos) {
                if (doAction.action.equals(CREATE)) {
                    CreateRunner runner = new CreateRunner(getAsset(doAction.object.uuid, mDataSet));
                    mHandler.post(runner);
                }
                if (doAction.action.equals(MODIFY)) {
                    SimpleAsset dest = getAsset(doAction.object.uuid, mDataSet);
                    if (!dest.equals(doAction.object)) {
                        switch (dest.content) {
                            case TRASH: {
                                TrashRunner runner = new TrashRunner(doAction.object);
                                mHandler.post(runner);
                                break;
                            }
                            case ARCHIVE: {
                                ArchiveRunner runner = new ArchiveRunner(doAction.object);
                                mHandler.post(runner);
                                break;
                            }
                            default: {
                                if (isModified(dest, doAction.object)) {
                                    ModifyRunner runner = new ModifyRunner(doAction.object);
                                    mHandler.post(runner);
                                } else {
                                    ChangeRunner runner = new ChangeRunner(doAction.object);
                                    mHandler.post(runner);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            mRedos.clear();

            // 選択解除
            mState.changeSelection(UNSELECTED);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mState.setId(item.getItemId());
        // コンテンツ識別子の保存
        mPref.edit().putInt(EXTRA_CONTENT, item.getItemId()).apply();
        mDrawerLayout.closeDrawer(GravityCompat.START);
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
        if (mState.getId() != R.id.call) {
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
     * 表示の更新
     *
     * @param collection 一覧
     */
    private void updateView(ArrayList<SimpleAsset> collection) {
        // プログレスの終了
        disableProgressBar();
        // 空表示の更新
        updateEmptyView(collection);
        // メニューの更新
        updateMenu();
        // 一覧表示の更新
        LAYOUT layout = LAYOUT.valueOf(mPref.getString(EXTRA_LAYOUT, LINEAR.name()));
        updateCollectionView(layout, collection);
        // タイトルの更新
        updateTitle(collection);
    }

    /**
     * プログレスバーの非表示
     */
    private void disableProgressBar() {
        // プログレスバーの更新
        ProgressBar bar = mProgressBar;
        if (bar != null) {
            bar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 空表示の更新
     *
     * @param collection 一覧
     */
    private void updateEmptyView(List<SimpleAsset> collection) {
        // 空表示の更新
        ImageView view = mEmptyView;
        if (view != null) {
            boolean isEmpty = collection.isEmpty();
            if (isEmpty) {
                // 空の場合
                view.setVisibility(View.VISIBLE);
            } else {
                // 空でない場合
                view.setVisibility(View.GONE);
            }
        }
    }

    /**
     * メニューの更新
     *
     */
    private void updateMenu() {
        String layout = mPref.getString(EXTRA_LAYOUT, LINEAR.name());
        if (LAYOUT.valueOf(layout) == GRID) {
            mState.changeLayout(LINEAR);
        } else {
            mState.changeLayout(GRID);
        }
        invalidateOptionsMenu();
    }

    /**
     * 一覧表示の更新
     *
     * @param id         識別子
     * @param collection 一覧
     */
    private void updateCollectionView(LAYOUT id, ArrayList<SimpleAsset> collection) {
        // 一覧表示の取得
        mCollectionView = getCollectionView(id, collection);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, (Fragment) mCollectionView)
                .commit();
    }

    /**
     * タイトルの更新
     *
     * @param collection 一覧
     */
    private void updateTitle(ArrayList<SimpleAsset> collection) {
        StringBuilder sb = new StringBuilder();
        if (collection.isEmpty()) {
            sb.append(this.getString(R.string.call));
        } else {
            sb.append(this.getString(R.string.call)).append("  ").append(collection.size());
        }
        mToolbar.setTitle(sb.toString());
        sb.delete(0, sb.length());
    }

    /**
     * 一覧表示の取得
     *
     * @param id         識別子
     * @param collection 一覧
     * @return 一覧表示
     */
    private ICollectionView<SimpleAsset> getCollectionView(LAYOUT id, ArrayList<SimpleAsset> collection) {
        if (id == GRID) {
            return ModuleFragment.newInstance(collection);
        }
        return ListFragment.newInstance(collection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedMore(ICollectionView<SimpleAsset> collectionView, final View view, final SimpleAsset item) {
        // We need to post a Runnable to show the file_selected_one to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        view.post(() -> showPopupMenu(view, item));
    }

    // BEGIN_INCLUDE(show_popup)
    private void showPopupMenu(final View view, final SimpleAsset item) {

        // Create a PopupMenu, giving it the clicked view for an anchor
        final PopupMenu popup = new PopupMenu(this, view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.main_selected_all_one, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(menuItem -> {

            // 項目を選択する
            mSelected.clear();
            mSelected.add(item);

            // 項目のメニューを選択する
            onOptionsItemSelected(menuItem);

            return false;
        });

        // Finally show the PopupMenu
        popup.show();
    }
    // END_INCLUDE(show_popup)


    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelected(ICollectionView<SimpleAsset> collectionView, View view, SimpleAsset item) {
        // レジューム状態の確認
        if(!mState.isResumed()) {
            return;
        }

        // アセットを取得
        SimpleAsset asset = getAsset(item.uuid, mDataSet);

        // タイムスタンプを保存
        asset.timestamp = System.currentTimeMillis();

        // 設定
        mClient.setList(mDataSet);

        try {
            // 電話アプリ呼び出し
            Intent intent = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + asset.call));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSwiped(ICollectionView<SimpleAsset> collectionView, SimpleAsset item) {
        // バックアップ
        mBackup.clear();
        copy(mBackup, mDataSet);

        // 一覧表示の更新
        mUndos.clear();
        int position = mCollectionView.remove(item);
        mUndos.add(new Undo(INSERT, position, toItem(item)));
        Collections.reverse(mUndos);

        // データの設定
        SimpleAsset asset = getAsset(item.uuid, mDataSet);
        asset.content = SimpleAsset.CONTENT.ARCHIVE;
        mClient.setList(mDataSet);

        // スナックバーの生成
        String message = getString(R.string.moved_to_archive_item);
        makeUndoSnackbar(mCoordinatorLayout, message);
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
    public void onClick(View view) {
        // 選択解除
        mState.changeSelection(UNSELECTED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectChanged(State state) {
        mHandler.post(() -> {
            try {
                // 選択状態の取得
                switch (mState.getSelection()) {
                    case UNSELECTED: {
                        // アクションバーの設定
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(false);
                        }

                        // 選択の解除
                        if (mCollectionView != null) {
                            mCollectionView.diselect();
                        }

                        // ナビゲーションドローワーの設定
                        enableNavigationDrawer();

                        // ツールバーの変更
                        updateTitle(toList(mDataSet));
                        mToolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.light_blue_600));
                        // ステータスバーの変更
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.light_blue_800));
                        }
                        // 背景色の変更
                        mLayout.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.light_blue_800));


                        // メニューを設定
                        invalidateOptionsMenu();

                        break;
                    }
                    // 何もしない
                    case SELECTED_ALL: {

                        // ナビゲーションの設定解除
                        disableNavigationDrawer();

                        // アクションバーの設定
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(true);
                        }

                        // 全選択
                        if (mCollectionView != null) {
                            mSelected.clear();
                            mSelected.addAll(mCollectionView.selectedAll());
                        }

                        // ツールバーの設定
                        mToolbar.setTitle(String.valueOf(mSelected.size()));
                        mToolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));
                        }
                        // 背景色の変更
                        mLayout.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));

                        // メニュー更新
                        invalidateOptionsMenu();

                        break;
                    }
                    case MULTI_SELECTED:
                    case SELECTED: {

                        // ナビゲーションの設定解除
                        disableNavigationDrawer();

                        // アクションバーの設定
                        ActionBar bar = getSupportActionBar();
                        if (bar != null) {
                            bar.setDisplayHomeAsUpEnabled(true);
                        }

                        // ツールバーの設定
                        mToolbar.setTitle(String.valueOf(mSelected.size()));
                        mToolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_600));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));
                        }
                        // 背景色の変更
                        mLayout.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.blue_grey_800));

                        // メニュー更新
                        invalidateOptionsMenu();
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisplayChanged(State state) {
        // 処理なし
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSelectedChanged(ICollectionView<SimpleAsset> collectionView, View view, SimpleAsset item, Collection<? extends SimpleAsset> collection) {
        // 選択数の変更
        mSelected.clear();
        mSelected.addAll(getSelectedCollection(collection));

        // 選択状態の確認
        if (mSelected.size() == collection.size()) {
            // 全選択
            if (!mState.changeSelection(SELECTED_ALL)) {
                // 遷移なしの場合
                // 選択数を変更
                mToolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            }
        } else if (mSelected.size() > 1) {
            // マルチ選択
            if (!mState.changeSelection(MULTI_SELECTED)) {
                // 遷移なしの場合
                // 選択数を変更
                mToolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
               invalidateOptionsMenu();
            }
        } else if (mSelected.size() == 1) {
            // 選択
            if (!mState.changeSelection(SELECTED)) {
                // 遷移なしの場合
                // 選択数を変更
                mToolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            }
        } else {
            // 非選択
            mState.changeSelection(UNSELECTED);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMoveChanged(ICollectionView<SimpleAsset> view, Collection<? extends SimpleAsset> collection) {
        // キャストする
        ArrayList<SimpleAsset> list = new ArrayList<>(collection);

        // 選択状態を選択中とする
        mState.changeSelection(SELECTING);

        // アクションバーを設定する
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
        }

        // タイトルを更新する
        updateTitle(list);

        // ナビゲーションバーを有効にする
        enableNavigationDrawer();

        // メニューを更新する
        invalidateOptionsMenu();

        //データを設定する
        ArrayList<SimpleAsset> assets = new ArrayList<>();
        for (SimpleAsset item : list) {
            for (SimpleAsset asset : mDataSet) {
                if (asset.uuid.equals(item.uuid)) {
                    assets.add(asset);
                    break;
                }
            }
        }
        mDataSet.clear();
        copy(mDataSet, assets);
        mClient.setList(mDataSet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpdated(ICollectionView<SimpleAsset> view, Collection<? extends SimpleAsset> collection) {
        // キャスト
        ArrayList<SimpleAsset> arrayList = new ArrayList<>(collection);

        // 空表示の更新
        updateEmptyView(arrayList);
        // タイトルの更新
        updateTitle(arrayList);
        // メニューの更新
        updateMenu();
    }

    /**
     * スナックバーの生成
     *
     * @param layout  レイアウト
     * @param message メッセージ
     */
    private void makeUndoSnackbar(CoordinatorLayout layout, String message) {
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo), new View.OnClickListener() {
                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    public void onClick(View v) {
                        try {
                            // 一覧表示の更新
                            for (Undo undo : mUndos) {

                                // 選択状態は解除しておく
                                undo.object.isSelected = false;

                                switch (undo.action) {
                                    case INSERT: {
                                        mCollectionView.insert(undo.arg, toItem(undo.object));
                                        break;
                                    }
                                    case CHANGE: {
                                        mCollectionView.change(toItem(undo.object));
                                        break;
                                    }
                                    case REMOVE: {
                                        mCollectionView.remove(toItem(undo.object));
                                        break;
                                    }
                                }
                            }
                            mUndos.clear();

                            // データ取得
                            ArrayList<SimpleAsset> list = toList(mBackup);
                            // 空表示の更新
                            updateEmptyView(list);
                            // タイトルの更新
                            updateTitle(list);
                            // メニューの更新
                            updateMenu();

                            // データ設定
                            mDataSet.clear();
                            copy(mDataSet, mBackup);

                            // 設定
                            mClient.setList(mDataSet);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    /**
     * 生成
     */
    private class CreateRunner implements Runnable {

        /**
         * @serial データ
         */
        SimpleAsset mData;

        /**
         * コンストラクタ
         *
         * @param data データ
         */
        CreateRunner(SimpleAsset data) {
            mData = data;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // バックアップ
                mBackup.clear();
                copy(mBackup, mDataSet);

                // データ変更
                mDataSet.add(0, mData);

                // 一覧表示の更新
                mUndos.clear();
                mCollectionView.insert(0, toItem(mData));
                mUndos.add(new Undo(REMOVE, 0, mData));
                Collections.reverse(mUndos);

                // 設定
                mClient.setList(mDataSet);

                // スナックバー生成
                String message = getString(R.string.created_item);
                makeUndoSnackbar(mCoordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 編集
     */
    private class ModifyRunner implements Runnable {

        /**
         * @serial データ
         */
        SimpleAsset mData;

        /**
         * コンストラクタ
         *
         * @param data データ
         */
        ModifyRunner(SimpleAsset data) {
            mData = data;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // バックアップ
                mBackup.clear();
                copy(mBackup, mDataSet);

                // データ変更
                for (SimpleAsset dest : mDataSet) {
                    // 一致
                    if (dest.equal(mData)) {
                        dest.setParams(mData);
                    }
                }

                // 一覧表示の更新
                mUndos.clear();
                int position = mCollectionView.change(toItem(mData));
                mUndos.add(new Undo(CHANGE, position, getAsset(mData.uuid, mBackup)));
                Collections.reverse(mUndos);

                // 設定
                mClient.setList(mDataSet);

                // スナックバーの生成
                String message = getString(R.string.modified_item);
                makeUndoSnackbar(mCoordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * アーカイブ
     */
    private class ArchiveRunner implements Runnable {

        /**
         * データ
         */
        List<SimpleAsset> mList;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        ArchiveRunner(SimpleAsset item) {
            mList = new ArrayList<>();
            mList.add(item);
        }

        /**
         * コンストラクタ
         *
         * @param list 一覧
         */
        ArchiveRunner(ArrayList<SimpleAsset> list) {
            mList = list;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // バックアップ
                mBackup.clear();
                copy(mBackup, mDataSet);

                if(LOG_D) {
                    for (SimpleAsset dest : mDataSet) {
                        Log.d(TAG, "mData:" + dest.uuid);
                    }
                    for (SimpleAsset src : mList) {
                        Log.d(TAG, "mList:" + src.uuid);
                    }
                }

                // データの変更
                for (SimpleAsset dest : mDataSet) {
                    for (SimpleAsset src : mList) {
                        // 一致
                        if (dest.equal(src)) {
                            dest.content = ARCHIVE;
                        }
                    }
                }

                // 一覧表示の更新
                mUndos.clear();
                for (SimpleAsset src : mList) {
                    int position;
                    position = mCollectionView.remove(toItem(src));
                    mUndos.add(new Undo(INSERT, position, getAsset(src.uuid, mBackup)));
                }
                Collections.reverse(mUndos);

                // 設定
                mClient.setList(mDataSet);

                // クリア
                mList.clear();

                // スナックバーの生成
                String message;
                if (mUndos.size() == 1) {
                    message = getString(R.string.moved_to_archive_item);
                    makeUndoSnackbar(mCoordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.moved_to_archive_some_items, mUndos.size());
                    makeUndoSnackbar(mCoordinatorLayout, message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ゴミ箱
     */
    private class TrashRunner implements Runnable {

        /**
         * データ
         */
        List<SimpleAsset> mList;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        TrashRunner(SimpleAsset item) {
            mList = new ArrayList<>();
            mList.add(item);
        }

        /**
         * コンストラクタ
         *
         * @param list データ
         */
        TrashRunner(ArrayList<SimpleAsset> list) {
            mList = list;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // バックアップ
                mBackup.clear();
                copy(mBackup, mDataSet);

                // データの変更
                for (SimpleAsset data : mDataSet) {
                    for (SimpleAsset src : mList) {
                        // 一致
                        if (data.equal(src)) {
                            data.content = TRASH;
                        }
                    }
                }

                // 一覧表示の更新
                mUndos.clear();
                for (SimpleAsset src : mList) {
                    int position;
                    position = mCollectionView.remove(toItem(src));
                    mUndos.add(new Undo(INSERT, position, getAsset(src.uuid, mBackup)));
                }
                Collections.reverse(mUndos);

                // 設定
                mClient.setList(mDataSet);

                // クリア
                mList.clear();

                // スナックバーの生成
                String message;
                if (mUndos.size() == 1) {
                    message = getString(R.string.moved_to_trash_item);
                    makeUndoSnackbar(mCoordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.moved_to_trash_some_items, mUndos.size());
                    makeUndoSnackbar(mCoordinatorLayout, message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * コピー
     */
    private class CopyRunner implements Runnable {

        /**
         * データ
         */
        List<SimpleAsset> mList;

        /**
         * コンストラクタ
         *
         * @param list データ
         */
        CopyRunner(ArrayList<SimpleAsset> list) {
            mList = list;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // バックアップ
                mBackup.clear();
                copy(mBackup, mDataSet);

                // データを変更
                SimpleAsset dst = SimpleAsset.createInstance();
                SimpleAsset src = getAsset(mList.get(0).uuid, mDataSet);
                dst.setParams(src);
                mDataSet.add(0, dst);

                // 一覧表示の更新
                mUndos.clear();
                mCollectionView.insert(0, toItem(dst));
                mUndos.add(new Undo(REMOVE, 0, dst));
                Collections.reverse(mUndos);

                // 設定
                mClient.setList(mDataSet);

                // 選択一覧の削除
                mSelected.clear();

                // スナックバー生成
                String message = getString(R.string.created_item);
                makeUndoSnackbar(mCoordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 選択したアーカイブの色変更
     */
    private class ChangeColorRunner implements Runnable {

        /**
         * データ
         */
        List<SimpleAsset> mList;

        /**
         * 色
         */
        COLOR mCOLOR;

        /**
         * コンストラクタ
         *
         * @param color 色
         */
        ChangeColorRunner(ArrayList<SimpleAsset> list, COLOR color) {
            mList = list;
            mCOLOR = color;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // 選択状態の解除
                mState.changeSelection(UNSELECTED);

                // データの設定
                for (SimpleAsset src : mList) {
                    // 選択状態の解除
                    src.isSelected = false;
                    // 色の設定
                    src.color = mCOLOR;
                    // 画像の設定
                    src.imagePath = INVALID_STRING_VALUE;
                }
                for (SimpleAsset dest : mDataSet) {
                    for (SimpleAsset src : mList) {
                        // 一致
                        if (dest.equal(src)) {
                            // データの変更
                            dest.copy(src);
                        }
                    }
                }

                // プログレスバーの更新
                disableProgressBar();
                // 一覧表示の更新
                for (SimpleAsset item : mList) {
                    // 表示を更新
                    mCollectionView.change(toItem(item));
                }

                // 設定
                mClient.setList(mDataSet);

                // 選択一覧の削除
                mList.clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 変更
     */
    private class ChangeRunner implements Runnable {

        /**
         * @serial 項目
         */
        SimpleAsset mItem;

        /**
         * コンストラクタ
         *
         * @param item 項目
         */
        ChangeRunner(SimpleAsset item) {
            mItem = item;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            try {
                // 一覧が空でなく
                // 一覧が空でない場合
                if ((mCollectionView != null) && (mItem != null)) {

                    // 設定
                    for (SimpleAsset dest : mDataSet) {
                        // 一致
                        if (dest.equal(mItem)) {
                            dest.copy(mItem);
                        }
                    }

                    // プログレスバーの更新
                    disableProgressBar();

                    // 一覧表示の更新
                    mCollectionView.change(toItem(getAsset(mItem.uuid, mDataSet)));

                    // 設定
                    mClient.setList(mDataSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 電話一覧の取得
     *
     * @param collection 一覧
     * @return 電話一覧
     */
    public ArrayList<SimpleAsset> toList(Collection<SimpleAsset> collection) {
        ArrayList<SimpleAsset> result = new ArrayList<>();
        for (SimpleAsset asset : collection) {
            try {
                switch (asset.content) {
                    case ARCHIVE:
                    case TRASH: {
                        break;
                    }
                    default: {
                        if (!asset.call.equals(INVALID_STRING_VALUE)) {
                            result.add(toItem(asset));
                        }
                        break;
                    }

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 項目に変換
     *
     * @param asset アセット
     */
    public SimpleAsset toItem(SimpleAsset asset) {
        asset.title = asset.displayName;
        asset.subtitle = asset.call;
        return asset;
    }
}
