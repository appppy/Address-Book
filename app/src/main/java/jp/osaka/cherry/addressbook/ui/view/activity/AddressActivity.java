package jp.osaka.cherry.addressbook.ui.view.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.IDialog;
import jp.osaka.cherry.addressbook.android.view.ICollectionView;
import jp.osaka.cherry.addressbook.constants.ActivityTransition;
import jp.osaka.cherry.addressbook.constants.COLOR;
import jp.osaka.cherry.addressbook.constants.LAYOUT;
import jp.osaka.cherry.addressbook.databinding.ActivityAddressBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.service.SimpleClient;
import jp.osaka.cherry.addressbook.service.history.History;
import jp.osaka.cherry.addressbook.service.history.HistoryProxy;
import jp.osaka.cherry.addressbook.ui.Redo;
import jp.osaka.cherry.addressbook.ui.State;
import jp.osaka.cherry.addressbook.ui.Undo;
import jp.osaka.cherry.addressbook.ui.view.EditDialog;
import jp.osaka.cherry.addressbook.ui.view.ListFragment;
import jp.osaka.cherry.addressbook.ui.view.ModuleFragment;
import jp.osaka.cherry.addressbook.utils.AssetHelper;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
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
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_HISTORY;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LAYOUT;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_SIMPLE_ASSET;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_SORT;
import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_LAT;
import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_LNG;
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
import static jp.osaka.cherry.addressbook.service.SimpleAsset.createInstance;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.getStartActivity;
import static jp.osaka.cherry.addressbook.utils.ActivityHelper.startCreateActivity;
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
import static jp.osaka.cherry.addressbook.utils.FileHelper.readFile;
import static jp.osaka.cherry.addressbook.utils.FileHelper.writeFile;


/**
 * 住所一覧画面
 */
public class AddressActivity extends AppCompatActivity implements
        IDialog.Callbacks<String>,
        SimpleClient.Callbacks,
        ICollectionView.Callbacks<SimpleAsset>,
        DrawerLayout.DrawerListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        State.Callbacks {

    /**
     * @serial 目印
     */
    private final String TAG = "AddressActivity";

    /**
     * @serial バインディング
     */
    private ActivityAddressBinding mBinding;

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
     * @serial URI
     */
    private Uri mUri;

    /**
     * @serial ファイル読み込み要求
     */
    private boolean isFile = false;

    /**
     * @serial 履歴読み込み要求
     */
    private boolean isHistory = false;

    /**
     * @serial 履歴
     */
    private History mHistory;

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
        mPref = getDefaultSharedPreferences(this);

        // テーマの設定
        setTheme(R.style.AppTheme_Teal);

        // レイアウト設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_address);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(R.string.address);
        }

        // フローティングアクションボタンの設定
        setupFloatingActionButton();

        // ナビゲーションの設定
        enableNavigationDrawer();

        // 登録
        mState.setId(R.id.address);
        mState.registerCallacks(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                CharSequence name = extras.getCharSequence(Intent.EXTRA_TITLE);
                CharSequence sub = extras.getCharSequence(Intent.EXTRA_SUBJECT);
                CharSequence phone = extras.getCharSequence(Intent.EXTRA_PHONE_NUMBER);
                //CharSequence email = extras.getCharSequence(Intent.EXTRA_EMAIL);
                CharSequence text = extras.getCharSequence(Intent.EXTRA_TEXT);
                // 項目の作成
                SimpleAsset item = createInstance();
                if (name != null) {
                    item.displayName = item.toString();
                }
                if (sub != null) {
                    item.displayName = sub.toString();
                }
                if (phone != null) {
                    item.call = phone.toString();
                }
                if (text != null) {
                    item.note = text.toString();
                }
                // レジューム後に動作させる
                mRedos.clear();
                mRedos.add(new Redo(CREATE, item));
            }
        }
        if(!mRedos.isEmpty()) {
            mPref.edit().putInt(EXTRA_CONTENT, R.id.address).apply();
        }

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();
        int id = mPref.getInt(EXTRA_CONTENT, R.id.address);
        if (id != R.id.address) {
            mState.setId(id);
            if (mState.getId() != R.id.address) {
                Intent intent = getIntent();
                intent.setClass(getApplicationContext(), getStartActivity(mState.getId()));
                startActivity(intent);
                overridePendingTransition(R.animator.fade_out, R.animator.fade_in);
                finish();
            }
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
            case CREATE_ITEM: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    SimpleAsset item = bundle.getParcelable(EXTRA_SIMPLE_ASSET);
                    // レジューム後に動作させる
                    mRedos.clear();
                    mRedos.add(new Redo(CREATE, item));
                }
                break;
            }
            case DETAIL_ITEM:
            case EDIT_ITEM: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    SimpleAsset item = bundle.getParcelable(EXTRA_SIMPLE_ASSET);
                    mRedos.clear();
                    mRedos.add(new Redo(MODIFY, item));
                }
                break;
            }
            case SYNC_FILE: {
                if (resultCode == RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        // レジューム後に動作させる
                        mUri = data.getData();
                    }
                }
                break;
            }
            case OPEN_FILE: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    mPref.edit().putString(EXTRA_FILE_NAME, data.getStringExtra(EXTRA_FILE_NAME)).apply();
                    // レジューム後に動作させる
                    isFile = true;
                }
                break;
            }
            case OPEN_HISTORY: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    Bundle bundle = data.getExtras();
                    assert bundle != null;
                    mHistory = bundle.getParcelable(EXTRA_HISTORY);
                    // レジューム後に動作させる
                    isHistory = true;
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
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
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
        switch (mState.getSelection()) {
            case SELECTING:
            case UNSELECTED: {
                if (toList(mDataSet).isEmpty()) {
                    getMenuInflater().inflate(R.menu.address_empty, menu);
                } else {
                    switch (mState.getLayout()) {
                        default:
                        case LINEAR: {
                            if (toList(mDataSet).size() == 1) {
                                getMenuInflater().inflate(R.menu.address_linear_one, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.address_linear, menu);
                            }
                            break;
                        }
                        case GRID: {
                            if (toList(mDataSet).size() == 1) {
                                getMenuInflater().inflate(R.menu.address_grid_one, menu);
                            } else {
                                getMenuInflater().inflate(R.menu.address_grid, menu);
                            }
                            break;
                        }
                    }
                }
                break;
            }
            case SELECTED: {
                getMenuInflater().inflate(R.menu.address_selected_one, menu);
                break;
            }
            case MULTI_SELECTED: {
                getMenuInflater().inflate(R.menu.address_selected, menu);
                break;
            }
            case SELECTED_ALL: {
                if (mSelected.size() == 1) {
                    getMenuInflater().inflate(R.menu.address_selected_all_one, menu);
                } else {
                    getMenuInflater().inflate(R.menu.address_selected_all, menu);
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
                SimpleAsset asset = getAsset(mSelected.get(0).uuid, mDataSet);
                mSelected.clear();
                asset.timestamp = System.currentTimeMillis();
                mClient.setList(mDataSet);
                startEditActivity(mSelf, mSelf, asset);
                return true;
            }
            case R.id.menu_info: {
                SimpleAsset asset = getAsset(mSelected.get(0).uuid, mDataSet);
                mSelected.clear();
                asset.timestamp = System.currentTimeMillis();
                mClient.setList(mDataSet);
                // 詳細画面の表示
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
                    //intent.putExtra(Intent.EXTRA_EMAIL, asset.send);
                    intent.putExtra(Intent.EXTRA_TEXT, asset.note);
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
     * {@inheritDoc}
     */
    @Override
    public void onUpdated(Object object, final List<SimpleAsset> assets) {
        mHandler.post(() -> {
            try {
                boolean result;
                result = mDataSet.isEmpty() || mDataSet.size() != assets.size() || !mDataSet.equals(assets);
                if (result) {
                    // レジューム状態の確認
                    if (!mState.isResumed()) {
                        return;
                    }
                    // データ設定
                    mDataSet.clear();
                    mDataSet.addAll(assets);
                    // 表示の更新
                    updateView(toList(mDataSet));
                    // FABの設定
                    setupFloatingActionButton();
                    mBinding.fab.show();
                    // ジオフェンス開始
                    //addGeofencesHandler(mDataSet);
                }

                // 次の動作を指定
                for (Redo doAction : mRedos) {
                    if (doAction.action.equals(CREATE)) {
                        CreateRunner runner = new CreateRunner(doAction.object);
                        mHandler.post(runner);
                    }
                    if (doAction.action.equals(MODIFY)) {
                        SimpleAsset dest = getAsset(doAction.object.uuid, mDataSet);
                        if (!dest.equals(doAction.object)) {
                            switch (doAction.object.content) {
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
                if (mUri != null) {
                    SyncRunner runner = new SyncRunner(mUri);
                    mHandler.post(runner);
                    mUri = null;
                }
                if (isFile) {
                    // ファイル名を取得
                    String filename = mPref.getString(EXTRA_FILE_NAME, INVALID_STRING_VALUE);
                    ReadFileRunner runner = new ReadFileRunner(filename);
                    mHandler.post(runner);
                    isFile = false;
                }
                if (isHistory) {
                    ReadHistoryRunner runner = new ReadHistoryRunner(mHistory);
                    mHandler.post(runner);
                    isHistory = false;
                }

                // 選択解除
                mState.changeSelection(UNSELECTED);

            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if (mState.getId() != R.id.address) {
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
     * 項目のポップアップメニュー選択
     *
     * @param collectionView 一覧表示
     * @param view           項目表示
     * @param item           項目
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
        popup.getMenuInflater().inflate(R.menu.address_selected_all_one, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(menuItem -> {
            // 項目を選択
            mSelected.clear();
            mSelected.add(item);

            // メニュー選択
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

        // データ設定
        mClient.setList(mDataSet);

        // 詳細画面の表示
        LAYOUT layout = LAYOUT.valueOf(mPref.getString(EXTRA_LAYOUT, LINEAR.name()));
        if (layout == GRID) {
            startDetailActivity_from_Module(this, this, view, asset);
        } else {
            startDetailActivity_from_Line(this, this, view, asset);
        }
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
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            } else {
                if (LOG_D) {
                    Log.d(TAG, "NOP selected all");
                }
            }
        } else if (mSelected.size() > 1) {
            // マルチ選択
            if (!mState.changeSelection(MULTI_SELECTED)) {
                // 遷移なしの場合
                // 選択数を変更
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            } else {
                if (LOG_D) {
                    Log.d(TAG, "NOP multi selected");
                }
            }
        } else if (mSelected.size() == 1) {
            // 選択
            if (!mState.changeSelection(SELECTED)) {
                // 遷移なしの場合
                // 選択数を変更
                mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                // メニュー更新
                invalidateOptionsMenu();
            } else {
                if (LOG_D) {
                    Log.d(TAG, "NOP selected");
                }
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
    public void onSwiped(ICollectionView<SimpleAsset> collectionView, SimpleAsset item) {
        // バックアップ
        mBackup.clear();
        copy(mBackup, mDataSet);

        // 一覧表示の更新
        mUndos.clear();
        int position = mCollectionView.remove(toItem(item));
        mUndos.add(new Undo(INSERT, position, item));
        Collections.reverse(mUndos);

        // データの設定
        SimpleAsset asset = getAsset(item.uuid, mDataSet);
        asset.content = ARCHIVE;
        mClient.setList(mDataSet);

        // スナックバーの生成
        String message = getString(R.string.moved_to_archive_item);
        makeUndoSnackbar(mBinding.coordinatorLayout, message);
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
     * 更新通知
     *
     * @param view       表示
     * @param collection 一覧
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
     * {@inheritDoc}
     */
    @Override
    public void onScroll(ICollectionView<SimpleAsset> view) {
        mBinding.fab.hide();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollFinished(ICollectionView<SimpleAsset> view) {
        mBinding.fab.show();
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
                        mCollectionView.diselect();

                        // ナビゲーションドローワーの設定
                        enableNavigationDrawer();

                        // ツールバーの変更
                        updateTitle(toList(mDataSet));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.teal_600));
                        // ステータスバーの変更
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.teal_800));
                        }
                        // 背景色の変更
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.teal_800));


                        // フローティングアクションボタンの表示
                        setupFloatingActionButton();
                        mBinding.fab.show();

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
                        mSelected.clear();
                        mSelected.addAll(mCollectionView.selectedAll());

                        // ツールバーの設定
                        mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.red_600));
                        // ステータスバーの変更
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.red_800));
                        }
                        // 背景色の変更
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.red_800));


                        // フローティングアクションボタンの非表示
                        setupFloatingActionButton();

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
                        mBinding.toolbar.setTitle(String.valueOf(mSelected.size()));
                        mBinding.toolbar.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.red_600));
                        // ステータスバーの変更
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            getWindow().setStatusBarColor(ContextCompat.getColor(mSelf, R.color.red_800));
                        }
                        // 背景色の変更
                        mBinding.mainContainer.setBackgroundColor(ContextCompat.getColor(mSelf, R.color.red_800));

                        // フローティングアクションボタンの非表示
                        setupFloatingActionButton();

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
     * フローティングアクションボタンの設定
     */
    private void setupFloatingActionButton() {
        if (mState.getSelection() == UNSELECTED) {
            mBinding.fab.setImageResource(R.drawable.ic_add_white_36dp);
            mBinding.fab.setOnClickListener(v -> {
                if (LOG_D) {
                    Log.d(TAG, "size:" + mDataSet.size());
                }
                // レジューム状態の確認
                if (!mState.isResumed()) {
                    return;
                }

                // 詳細表示
                startCreateActivity(mSelf, mSelf, v, createInstance());
            });
        } else {
            if (mSelected.size() == 1) {
                mBinding.fab.setImageResource(R.drawable.ic_create_white_36dp);
                mBinding.fab.setOnClickListener(v -> {
                    // レジューム状態の確認
                    if (!mState.isResumed()) {
                        return;
                    }

                    SimpleAsset asset = getAsset(mSelected.get(0).uuid, mDataSet);
                    asset.timestamp = System.currentTimeMillis();
                    mClient.setList(mDataSet);
                    // 選択解除
                    mState.changeSelection(UNSELECTED);
                    startEditActivity(mSelf, mSelf, v, asset);
                });
            } else {
                mBinding.fab.hide();
            }
        }
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
     * ナビゲーションの無効
     */
    private void disableNavigationDrawer() {
        if (mToggle != null) {
            mToggle.setDrawerIndicatorEnabled(false);
            mToggle.syncState();
            mBinding.drawerLayout.removeDrawerListener(mToggle);
        }
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    /**
     * ダウロードピッカー
     */
    private void downloadPicker(Uri uri) {
        new FileAsyncTask(this, this, uri).execute();
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
     * プログレスバーの更新
     */
    private void disableProgressBar() {
        ProgressBar bar = mBinding.productImageLoading;
        bar.setVisibility(View.INVISIBLE);
    }

    /**
     * 空表示の更新
     *
     * @param collection 一覧
     */
    private void updateEmptyView(List<SimpleAsset> collection) {
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
            sb.append(this.getString(R.string.address));
        } else {
            sb.append(this.getString(R.string.address)).append("  ").append(collection.size());
        }
        mBinding.toolbar.setTitle(sb.toString());
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
     * ポジティブボタン押下.
     *
     * @param dialog ダイアログ
     * @param name   設定値
     */
    @Override
    public void onPositiveButtonClicked(IDialog dialog, String name) {
        if (name.equals(INVALID_STRING_VALUE)) {
            Toast.makeText(mSelf, R.string.failed_save_file, Toast.LENGTH_LONG).show();
        } else {
            // ファイルを上書き保存
            mHandler.post(new WriteFileRunner(name));
        }
    }

    /**
     * ネガティブボタン押下.
     *
     * @param dialog ダイアログ
     * @param name   設定値
     */
    @Override
    public void onNegativeButtonClicked(IDialog dialog, String name) {

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

                // データの変更
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
                makeUndoSnackbar(mBinding.coordinatorLayout, message);

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
                makeUndoSnackbar(mBinding.coordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 同期
     */
    private class SyncRunner implements Runnable {

        /**
         * @serial データ
         */
        Uri mUri;

        /**
         * コンストラクタ
         *
         * @param uri データ
         */
        SyncRunner(Uri uri) {
            mUri = uri;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                Cursor cursor = getContentResolver().query(mUri, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    //int docIdIndex = cursor.getColumnIndex("document_id");
                    int index = cursor.getColumnIndex("mime_type");
                    String type = cursor.getString(index);
                    if (type != null) {
                        downloadPicker(mUri);
                    }
                    cursor.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ダウンロード
     */
    private class FileAsyncTask {

        //ProgressDialog mProgressDialog;

        boolean isActive = false;

        Activity mActivity;

        private final Context context;

        private final Uri uri;

        FileAsyncTask(Activity activity, Context context, Uri uri) {
            mActivity = activity;
            this.context = context;
            this.uri = uri;
        }

        private void execute() {
            final Handler handler = new Handler(Looper.getMainLooper());
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                // do something in background
                File cacheFile = new File(context.getExternalCacheDir(), "file_cache");
                try {
                    //ここで取り出し
                    InputStream is = context.getContentResolver().openInputStream(uri);

                    int data;
                    OutputStream os = new FileOutputStream(cacheFile);
                    byte[] readBytes;
                    if (is != null) {
                        readBytes = new byte[is.available()];
                        while ((data = is.read(readBytes)) != -1) {
                            os.write(readBytes, 0, data);
                        }
                    }
                    // onPreExecute
                    //
                    //mProgressDialog = new ProgressDialog(mActivity);

                    // プログレスダイアログの設定
                    //mProgressDialog.setMessage(getString(R.string.loading));  // メッセージをセット

                    // プログレスダイアログの表示
                    //mProgressDialog.show();

                    isActive = true;
                    //
                    // update UI
                    handler.post(() -> {
                        ArrayList<SimpleAsset> collection = readFile(context, cacheFile);

                        if (mDataSet.isEmpty()) {
                            // 空の場合

                            // データの変更
                            mDataSet.addAll(collection);

                            // 表示の更新
                            updateView(toList(mDataSet));

                            // 設定
                            mClient.setList(mDataSet);

                            // スナックバーの生成
                            String message = getString(R.string.sync_item);
                            Snackbar.make(mBinding.coordinatorLayout, message, Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.undo), new View.OnClickListener() {
                                        /**
                                         * {@inheritDoc}
                                         */
                                        @Override
                                        public void onClick(View v) {
                                            // データ設定
                                            mDataSet.clear();
                                            // 一覧表示の更新
                                            updateView(toList(mDataSet));
                                            // データ保存
                                            mClient.setList(mDataSet);
                                        }
                                    })
                                    .show();

                        } else {

                            // バックアップ
                            mBackup.clear();
                            copy(mBackup, mDataSet);

                            // 履歴更新
                            mProxy.setHistory(new History(System.currentTimeMillis(), String.valueOf(mBackup.size()), AssetHelper.toJSONString(mBackup)));

                            // 一覧表示の更新
                            mUndos.clear();

                            boolean isFound;
                            ArrayList<SimpleAsset> stack = new ArrayList<>();
                            for (SimpleAsset src : collection) {
                                isFound = false;
                                for (SimpleAsset dest : mDataSet) {
                                    // 表示名が同じデータの確認
                                    if (dest.displayName.equals(src.displayName)) {
                                        isFound = true;
                                        switch (dest.content) {
                                            case ARCHIVE:
                                            case TRASH: {
                                                break;
                                            }
                                            default: {
                                                boolean isMod = false;
                                                // noteを更新
                                                if (!src.note.equals(INVALID_STRING_VALUE)) {
                                                    dest.note = src.note;
                                                    isMod = true;
                                                }
                                                if (src.latitude != DEFAULT_LAT) {
                                                    dest.latitude = src.latitude;
                                                    isMod = true;
                                                }
                                                if (src.longitude != DEFAULT_LNG) {
                                                    dest.longitude = src.longitude;
                                                    isMod = true;
                                                }
                                                if (src.radius != 0) {
                                                    dest.radius = src.radius;
                                                    isMod = true;
                                                }
                                                if (!src.call.equals(INVALID_STRING_VALUE)) {
                                                    dest.call = src.call;
                                                    isMod = true;
                                                }
                                                if (!src.url.equals(INVALID_STRING_VALUE)) {
                                                    dest.url = src.url;
                                                    isMod = true;
                                                }
                                                if (isMod) {
                                                    dest.modifiedDate = src.modifiedDate;
                                                    int position = mCollectionView.change(toItem(dest));
                                                    mUndos.add(new Undo(CHANGE, position, getAsset(dest.uuid, mBackup)));
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                                if(!isFound) {
                                    stack.add(src);
                                }
                            }
                            Collections.reverse(mUndos);

                            // データ変更
                            if(!stack.isEmpty()) {
                                mDataSet.addAll(stack);

                                // 表示の更新
                                updateView(toList(mDataSet));
                            }

                            // 設定
                            mClient.setList(mDataSet);

                            // スナックバーの生成
                            String message = getString(R.string.sync_item);
                            makeUndoSnackbar(mBinding.coordinatorLayout, message);
                        }

                        //if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    //    mProgressDialog.dismiss();
                    //}

                    isActive = false;
                    });
                } catch (Exception ex) {
                    // something went wrong
                    ex.printStackTrace();
                }
            });
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
         * @param list データ
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
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.moved_to_archive_some_items, mUndos.size());
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
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
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
                } else if (mUndos.size() > 1) {
                    message = getString(R.string.moved_to_trash_some_items, mUndos.size());
                    makeUndoSnackbar(mBinding.coordinatorLayout, message);
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
                makeUndoSnackbar(mBinding.coordinatorLayout, message);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ファイル読み込み
     */
    private class ReadFileRunner implements Runnable {
        /**
         * @serial ファイル名
         */
        String filename;

        /**
         * コンストラクタ
         *
         * @param filename ファイル名
         */
        ReadFileRunner(String filename) {
            this.filename = filename;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {

                // 履歴更新
                mProxy.setHistory(new History(System.currentTimeMillis(), String.valueOf(mDataSet.size()), AssetHelper.toJSONString(mDataSet)));

                // データ設定
                mDataSet.clear();
                mDataSet.addAll(readFile(mSelf, filename + ".csv"));

                // 一覧表示の更新
                mCollectionView.changeAll(toList(mDataSet));

                // 設定
                mClient.setList(mDataSet);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ファイル書き込み
     */
    private class WriteFileRunner implements Runnable {
        /**
         * @serial ファイル名
         */
        String filename;

        /**
         * コンストラクタ
         *
         * @param filename ファイル名
         */
        WriteFileRunner(String filename) {
            this.filename = filename;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // ファイル保存
                writeFile(mSelf, filename + ".csv", mDataSet);
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
                    // 表示の更新
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
                // 一覧表示が空でなく
                // 項目が空でない場合
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
     * 履歴読み込み
     */
    private class ReadHistoryRunner implements Runnable {
        /**
         * @serial ファイル名
         */
        History mHistory;

        /**
         * コンストラクタ
         *
         * @param history ファイル名
         */
        ReadHistoryRunner(History history) {
            mHistory = history;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                // データ設定
                ArrayList<SimpleAsset> assets = AssetHelper.toAssets(mHistory.message);
                mDataSet.clear();
                mDataSet.addAll(assets);

                // 一覧表示の更新
                if (mCollectionView != null) {
                    mCollectionView.changeAll(toList(mDataSet));
                }

                // 設定
                mClient.setList(mDataSet);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 連絡先一覧取得
     *
     * @param collection 一覧
     * @return 連絡先一覧
     */
    public ArrayList<SimpleAsset> toList(Collection<SimpleAsset> collection) {
        ArrayList<SimpleAsset> result = new ArrayList<>();
        for (SimpleAsset asset : collection) {
            switch (asset.content) {
                case ARCHIVE:
                case TRASH: {
                    break;
                }
                default: {
                    result.add(toItem(asset));
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 項目に変更
     *
     * @param asset アセット
     * @return アセット
     */
    public SimpleAsset toItem(SimpleAsset asset) {
        asset.title = asset.displayName;
        asset.subtitle = asset.note;
        return asset;
    }
}
