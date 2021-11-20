package jp.osaka.cherry.addressbook.ui.view.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.Objects;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.constants.COLOR;
import jp.osaka.cherry.addressbook.constants.RESULT;
import jp.osaka.cherry.addressbook.databinding.ActivityNoteDetailBinding;
import jp.osaka.cherry.addressbook.utils.ThemeHelper;
import jp.osaka.cherry.addressbook.service.SimpleAsset;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_RESULT;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_SIMPLE_ASSET;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;
import static jp.osaka.cherry.addressbook.utils.BackgroundHelper.setModuleBackground;


/**
 * ノート詳細アクティビティ
 */
public class NoteDetailActivity extends AppCompatActivity {

    /**
     * @serial バインディング
     */
    private ActivityNoteDetailBinding mBinding;

    /**
     * @serial データセット
     */
    private SimpleAsset mDataSet;

    /**
     * インテントの生成
     *
     * @param context コンテキスト
     * @param asset アセット
     * @return インテント
     */
    public static Intent createIntent(Context context, SimpleAsset asset) {
        Intent intent = new Intent(context, NoteDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SIMPLE_ASSET, asset);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String TAG = "NoteDetailActivity";
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // インテントの取得
        Intent intent = getIntent();
        mDataSet = intent.getParcelableExtra(EXTRA_SIMPLE_ASSET);

        // テーマの設定
        setTheme(ThemeHelper.getTheme(Objects.requireNonNull(mDataSet).color));

        // レイアウトの設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_note_detail);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(mDataSet.displayName);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        // 背景の設定
        setModuleBackground(this, mBinding.contentView, false, mDataSet.color);


        // 入力ダイアログの設定
        mBinding.editNote.setEnabled(true);
        mBinding.editNote.setFocusable(true);
        if (!mDataSet.note.equals(INVALID_STRING_VALUE)) {
            mBinding.editNote.setText(mDataSet.note);
        }

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // メニュー設定
        getMenuInflater().inflate(R.menu.main_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                //結果の設定
                setResult();
                finish();
                return true;
            }
            case R.id.menu_archive: {
                // アーカイブ
                mDataSet.content = SimpleAsset.CONTENT.ARCHIVE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_trash: {
                // ゴミ箱
                mDataSet.content = SimpleAsset.CONTENT.TRASH;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_share: {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TITLE, mDataSet.title);
                    intent.putExtra(Intent.EXTRA_SUBJECT, mDataSet.title);
                    intent.putExtra(Intent.EXTRA_TEXT, mBinding.editNote.getText());
                    intent.setType("text/plain");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            case R.id.menu_white: {
                mDataSet.color = COLOR.WHITE;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_red: {
                mDataSet.color = COLOR.RED;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_pink: {
                mDataSet.color = COLOR.PINK;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_purple: {
                mDataSet.color = COLOR.PURPLE;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_deep_purple: {
                mDataSet.color = COLOR.DEEP_PURPLE;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_indigo: {
                mDataSet.color = COLOR.INDIGO;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_blue: {
                mDataSet.color = COLOR.BLUE;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_green: {
                mDataSet.color = COLOR.GREEN;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_light_green: {
                mDataSet.color = COLOR.LIGHT_GREEN;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_lime: {
                mDataSet.color = COLOR.LIME;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_yellow: {
                mDataSet.color = COLOR.YELLOW;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_amber: {
                mDataSet.color = COLOR.AMBER;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_orange: {
                mDataSet.color = COLOR.ORANGE;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_deep_orange: {
                mDataSet.color = COLOR.DEEP_ORANGE;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_brown: {
                mDataSet.color = COLOR.BROWN;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
            case R.id.menu_blue_grey: {
                mDataSet.color = COLOR.BLUE_GREY;
                mDataSet.imagePath = INVALID_STRING_VALUE;
                setResult();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 結果の設定
     */
    private void setResult() {
        Intent intent = getIntent();
        if(!mBinding.editNote.getText().toString().equals(mDataSet.note)) {
            mDataSet.note = mBinding.editNote.getText().toString();
            mDataSet.modifiedDate = System.currentTimeMillis();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SIMPLE_ASSET, mDataSet);
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

}
