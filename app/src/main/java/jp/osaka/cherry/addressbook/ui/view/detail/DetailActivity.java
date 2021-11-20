package jp.osaka.cherry.addressbook.ui.view.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.Objects;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.constants.COLOR;
import jp.osaka.cherry.addressbook.constants.RESULT;
import jp.osaka.cherry.addressbook.databinding.ActivityDetailBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.utils.ThemeHelper;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.CREATE_ITEM;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_RESULT;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_SIMPLE_ASSET;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_LONG_VALUE;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;
import static jp.osaka.cherry.addressbook.utils.AssetHelper.getUri;


/**
 * 詳細アクティビティ
 */
public class DetailActivity extends AppCompatActivity {

    /**
     * @serial 目印
     */
    private final String TAG = "DetailActivity";

    /**
     * @serial バインディング
     */
    private ActivityDetailBinding mBinding;

    /**
     * @serial データセット
     */
    private SimpleAsset mDataSet;

    /**
     * インテントの生成
     *
     * @param context コンテキスト
     * @param item    項目
     * @return インテント
     */
    public static Intent createIntent(Context context, SimpleAsset item) {
        Intent intent = new Intent(context, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_SIMPLE_ASSET, item);
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

        // インテントの取得
        Intent intent = getIntent();
        mDataSet = intent.getParcelableExtra(EXTRA_SIMPLE_ASSET);

        // テーマの設定
        setTheme(ThemeHelper.getTheme(Objects.requireNonNull(mDataSet).color));

        // レイアウトの設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        // メニュー設定
        switch (mDataSet.content) {
            case TRASH: {
                getMenuInflater().inflate(R.menu.trash_detail, menu);
                break;
            }
            case ARCHIVE: {
                getMenuInflater().inflate(R.menu.archive_detail, menu);
                break;
            }
            default: {
                getMenuInflater().inflate(R.menu.main_detail, menu);
                break;
            }
        }
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
            case R.id.menu_unarchive:
            case R.id.menu_untrash: {
                // アーカイブ解除
                mDataSet.content = SimpleAsset.CONTENT.CONTACT;
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
            }// ゴミ箱から戻す
            case R.id.menu_share: {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TITLE, mDataSet.displayName);
                    intent.putExtra(Intent.EXTRA_SUBJECT, mDataSet.displayName);
                    intent.putExtra(Intent.EXTRA_PHONE_NUMBER, mDataSet.call);
                    intent.putExtra(Intent.EXTRA_TEXT, mDataSet.note);
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
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(mDataSet.displayName);
        }
        // レイアウトの設定
        if(mDataSet.call.equals(INVALID_STRING_VALUE)) {
            mBinding.detailContainer.layoutItem1.setVisibility(View.GONE);
        } else {
            mBinding.detailContainer.icon1.setVisibility(View.VISIBLE);
            mBinding.detailContainer.subtitle1.setText(mDataSet.call);
            mBinding.detailContainer.item1.setOnClickListener(view -> {
                try {
                    Intent intent = new Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:" + mDataSet.call));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            mBinding.detailContainer.layoutItem1.setVisibility(View.VISIBLE);
        }

        if(mDataSet.url.equals(INVALID_STRING_VALUE)) {
            mBinding.detailContainer.layoutItem2.setVisibility(View.GONE);
        } else {
            mBinding.detailContainer.icon2.setVisibility(View.VISIBLE);
            mBinding.detailContainer.subtitle2.setText(mDataSet.url);
            mBinding.detailContainer.layoutItem2.setOnClickListener(view -> {
                try {
                    Uri uri = Uri.parse(mDataSet.url);
                    Intent i = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            mBinding.detailContainer.layoutItem2.setVisibility(View.VISIBLE);
        }
        // 住所
        if (mDataSet.place.equals(INVALID_STRING_VALUE)) {
            mBinding.detailContainer.layoutPlace.setVisibility(View.GONE);
        } else {
            mBinding.detailContainer.iconPlace.setVisibility(View.VISIBLE);
            mBinding.detailContainer.placeText.setText(mDataSet.place);
            mBinding.detailContainer.layoutPlace.setOnClickListener(view -> {
                // マップアプリ呼び出し
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, getUri(mDataSet));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            mBinding.detailContainer.layoutPlace.setVisibility(View.VISIBLE);
        }


        // 空表示の設定
        if(mDataSet.call.equals(INVALID_STRING_VALUE)
                && mDataSet.url.equals(INVALID_STRING_VALUE)
                && mDataSet.note.equals(INVALID_STRING_VALUE)
                && (mDataSet.date == INVALID_LONG_VALUE)
                && mDataSet.place.equals(INVALID_STRING_VALUE)) {
            mBinding.emptyView.setVisibility(View.VISIBLE);
        } else {
            mBinding.emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * 結果の設定
     */
    private void setResult() {
        Intent intent = getIntent();
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
