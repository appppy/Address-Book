package jp.osaka.cherry.addressbook.ui.view.detail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import java.util.Calendar;
import java.util.Objects;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.constants.ActivityTransition;
import jp.osaka.cherry.addressbook.constants.RESULT;
import jp.osaka.cherry.addressbook.databinding.ActivityImageCreateBinding;
import jp.osaka.cherry.addressbook.service.SimpleAsset;
import jp.osaka.cherry.addressbook.ui.GeofenceAsset;
import jp.osaka.cherry.addressbook.utils.GeofenceHelper;
import jp.osaka.cherry.addressbook.utils.SettingsHelper;
import jp.osaka.cherry.addressbook.utils.ThemeHelper;

import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.CHOOSER;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.MODIFY_GEOFENCE;
import static jp.osaka.cherry.addressbook.constants.ActivityTransition.PERMISSIONS;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_GEOFENCE_ASSET;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_RESULT;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_SIMPLE_ASSET;
import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_LAT;
import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_LNG;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;
import static jp.osaka.cherry.addressbook.utils.ImageHelper.decodeFile;
import static jp.osaka.cherry.addressbook.utils.ImageHelper.decodeUri;
import static jp.osaka.cherry.addressbook.utils.ImageHelper.toPath;


/**
 * 作成画面
 */
public class ImageCreateActivity extends AppCompatActivity implements DatePicker.OnDateChangedListener {

    /**
     * @serial 目印
     */
    private final String TAG = "ImageCreateActivity";

    /**
     * @serial 自身
     */
    private ImageCreateActivity mSelf;

    /**
     * @serial バインディング
     */
    private ActivityImageCreateBinding mBinding;

    /**
     * @serial データセット
     */
    private SimpleAsset mDataSet;

    /**
     * @serial バックアップ
     */
    private SimpleAsset mBackup;

    /**
     * @serial 設定
     */
    private final SettingsHelper mSettingsHelper = new SettingsHelper();

    /**
     * インテントの生成
     *
     * @param context コンテキスト
     * @param asset アセット
     * @return インテント
     */
    public static Intent createIntent(Context context, SimpleAsset asset) {
        Intent intent = new Intent(context, ImageCreateActivity.class);
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
        if (LOG_I) {
            Log.i(TAG, "onCreate#enter");
        }

        // 自身の取得
        mSelf = this;

        // インテントの取得
        Intent intent = getIntent();
        mDataSet = intent.getParcelableExtra(EXTRA_SIMPLE_ASSET);
        mBackup = SimpleAsset.createInstance();
        mBackup.copy(mDataSet);

        // テーマの設定
        setTheme(ThemeHelper.getImageCreateTheme(mDataSet.color));

        // レイアウトの設定
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_create);

        mSettingsHelper.setParent(this);

        // ツールバーの設定
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        // フローティングアクションボタンの設定
        setupFloatingActionButton();

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setView();
    }

    /**
     * 表示の設定
     */
    void setView() {
        if (mDataSet.imagePath.equals(INVALID_STRING_VALUE)) {
            mBinding.imageView.setScaleType(ImageView.ScaleType.CENTER);
            mBinding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_image_black_100dp));
        } else {
            try {
                mBinding.imageView.setColorFilter(null);
                // 画像を縮小して取得
                Bitmap bitmap = decodeFile(mDataSet.imagePath);
                // imageViewの初期化
                mBinding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mBinding.imageView.setImageURI(null);
                // ImageViewにセット
                mBinding.imageView.setImageBitmap(bitmap);
            } catch (OutOfMemoryError | Exception e) {
                e.printStackTrace();
            }
        }

        mBinding.detailContainer.editName.setText(mDataSet.displayName);
        mBinding.detailContainer.editTel.setText(mDataSet.call);
        mBinding.detailContainer.editUrl.setText(mDataSet.url);
        if (!mDataSet.place.equals(INVALID_STRING_VALUE)) {
            mBinding.detailContainer.editPlace.setText(mDataSet.place);
        }
        mBinding.detailContainer.iconPlace.setOnClickListener(v -> {
            backup();
            // 地図の呼び出し
            Location location = new Location("");
            location.setLatitude(mDataSet.latitude);
            location.setLongitude(mDataSet.longitude);
            if (Build.VERSION.SDK_INT >= 21) {
                ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                        v, 0, 0, v.getWidth(), v.getHeight());

                if (mDataSet.latitude == DEFAULT_LAT ||
                    mDataSet.longitude == DEFAULT_LNG ||
                    mDataSet.radius == 0) {
                    Intent intent = MapsActivity.createIntent(getApplicationContext(), location, mSettingsHelper.getGoogleMapPropety());
                    ActivityCompat.startActivityForResult(mSelf, intent,
                            MODIFY_GEOFENCE.ordinal(), opts.toBundle());
                } else {
                    GeofenceAsset geofenceAsset = GeofenceAsset.createInstance();
                    geofenceAsset.latitude = mDataSet.latitude;
                    geofenceAsset.longitude = mDataSet.longitude;
                    geofenceAsset.radius = mDataSet.radius;
                    Intent intent = MapsActivity.createIntent(getApplicationContext(), location, geofenceAsset, mSettingsHelper.getGoogleMapPropety());
                    ActivityCompat.startActivityForResult(mSelf, intent,
                            MODIFY_GEOFENCE.ordinal(), opts.toBundle());
                }
            } else {
                if (mDataSet.latitude == DEFAULT_LAT ||
                        mDataSet.longitude == DEFAULT_LNG ||
                        mDataSet.radius == 0) {
                    Intent intent = MapsActivity.createIntent(getApplicationContext(), location, mSettingsHelper.getGoogleMapPropety());
                    startActivityForResult(intent, MODIFY_GEOFENCE.ordinal());
                } else {
                    GeofenceAsset geofenceAsset = GeofenceAsset.createInstance();
                    geofenceAsset.latitude = mDataSet.latitude;
                    geofenceAsset.longitude = mDataSet.longitude;
                    geofenceAsset.radius = mDataSet.radius;
                    Intent intent = MapsActivity.createIntent(getApplicationContext(), location, geofenceAsset, mSettingsHelper.getGoogleMapPropety());
                    startActivityForResult(intent, MODIFY_GEOFENCE.ordinal());
                }
            }
        });
        mBinding.detailContainer.editPlace.setOnClickListener(v -> {
            backup();
            // 地図の呼び出し
            Location location = new Location("");
            location.setLatitude(mDataSet.latitude);
            location.setLongitude(mDataSet.longitude);
            if (Build.VERSION.SDK_INT >= 21) {
                if (mDataSet.latitude == DEFAULT_LAT ||
                        mDataSet.longitude == DEFAULT_LNG ||
                        mDataSet.radius == 0) {
                    ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                            v, 0, 0, v.getWidth(), v.getHeight());
                    Intent intent = MapsActivity.createIntent(getApplicationContext(), location, mSettingsHelper.getGoogleMapPropety());
                    ActivityCompat.startActivityForResult(mSelf, intent,
                            MODIFY_GEOFENCE.ordinal(), opts.toBundle());
                } else {
                    GeofenceAsset geofenceAsset = GeofenceAsset.createInstance();
                    geofenceAsset.latitude = mDataSet.latitude;
                    geofenceAsset.longitude = mDataSet.longitude;
                    geofenceAsset.radius = mDataSet.radius;
                    ActivityOptionsCompat opts = ActivityOptionsCompat.makeScaleUpAnimation(
                            v, 0, 0, v.getWidth(), v.getHeight());
                    Intent intent = MapsActivity.createIntent(getApplicationContext(), location, geofenceAsset, mSettingsHelper.getGoogleMapPropety());
                    ActivityCompat.startActivityForResult(mSelf, intent,
                            MODIFY_GEOFENCE.ordinal(), opts.toBundle());
                }
            } else {
                if (mDataSet.latitude == DEFAULT_LAT ||
                        mDataSet.longitude == DEFAULT_LNG ||
                        mDataSet.radius == 0) {
                    Intent intent = MapsActivity.createIntent(getApplicationContext(), location, mSettingsHelper.getGoogleMapPropety());
                    startActivityForResult(intent, MODIFY_GEOFENCE.ordinal());
                } else {
                    GeofenceAsset geofenceAsset = GeofenceAsset.createInstance();
                    geofenceAsset.latitude = mDataSet.latitude;
                    geofenceAsset.longitude = mDataSet.longitude;
                    geofenceAsset.radius = mDataSet.radius;
                    Intent intent = MapsActivity.createIntent(getApplicationContext(), location, geofenceAsset, mSettingsHelper.getGoogleMapPropety());
                    startActivityForResult(intent, MODIFY_GEOFENCE.ordinal());
                }
            }
        });
    }

    /**
     * 権限の確認
     *
     * @param activity   アクティビティ
     * @return 権限の有無
     */

    private boolean hasSelfPermission(Activity activity) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS.ordinal()
                && Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[0])) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                visibleFab();
            } else {
                invisibleFab();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * フローティングアクションボタンの設定
     */
    protected void setupFloatingActionButton() {
        if (!hasSelfPermission(this)) {
            // 使用許可がない場合はこれをリクエストする
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS.ordinal());
            } else {
                invisibleFab();
            }
        } else {
            // 使用許可がある場合はフローティングボタンを設定
            visibleFab();
        }
    }

    /**
     * フローティングアクションボタンの有効化
     */
    @SuppressLint("IntentReset")
    private void visibleFab() {
        mBinding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "chooser"), CHOOSER.ordinal());

        });
        mBinding.fab.show();
    }

    /**
     * フローティングアクションボタンの無効化
     */
    private void invisibleFab() {
        mBinding.fab.hide();
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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {//結果の設定
            setResult();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (LOG_I) {
            Log.i(TAG, "onActivityResult#enter");
        }

        // 結果確認
        ActivityTransition type = ActivityTransition.get(requestCode);
        switch (Objects.requireNonNull(type)) {
            case CHOOSER: {
                if (resultCode == RESULT_OK) {
                    if (mDataSet == null) {
                        Log.d(TAG, "mDataSet is null");
                    }
                    mBinding.imageView.setColorFilter(null);
                    mBinding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    // 選択された画像のUriを取得
                    Uri uri = data.getData();
                    // 画像を縮小して取得
                    Bitmap bitmap = decodeUri(this, uri, mBinding.imageView.getWidth());
                    mBinding.imageView.setImageBitmap(bitmap);
                    // 変更を保存
                    mDataSet.imagePath = toPath(this, uri);
                }
                break;
            }
            case MODIFY_GEOFENCE: {
                if (resultCode == RESULT_OK) {
                    // データの取得
                    Bundle bundle = data.getExtras();
                    GeofenceAsset item = Objects.requireNonNull(bundle).getParcelable(EXTRA_GEOFENCE_ASSET);
                    mDataSet.longitude = Objects.requireNonNull(item).longitude;
                    mDataSet.latitude = item.latitude;
                    mDataSet.radius = item.radius;
                    mDataSet.place = GeofenceHelper.getLabel(this, item);
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
     * 結果の設定
     */
    private void setResult() {
        Intent intent = getIntent();
        if (mBinding.detailContainer.editName.getText().toString().equals(INVALID_STRING_VALUE)) {
            setResult(RESULT_CANCELED, intent);
        } else if (mBinding.detailContainer.editName.getText().toString().equals(mDataSet.displayName)
                && mBinding.detailContainer.editTel.getText().toString().equals(mDataSet.call)
                && mBinding.detailContainer.editUrl.getText().toString().equals(mDataSet.url)
                && mBinding.detailContainer.editPlace.getText().toString().equals(mDataSet.place)
                && mBackup.date == mDataSet.date
                && mBackup.latitude == mDataSet.latitude
                && mBackup.longitude == mDataSet.longitude
                && mBackup.radius == mDataSet.radius
                && mBackup.imagePath.equals(mDataSet.imagePath)
                ) {
            setResult(RESULT_CANCELED, intent);
        } else {
            mDataSet.displayName = mBinding.detailContainer.editName.getText().toString();
            mDataSet.call = mBinding.detailContainer.editTel.getText().toString();
            mDataSet.url = mBinding.detailContainer.editUrl.getText().toString();
            mDataSet.place = mBinding.detailContainer.editPlace.getText().toString();
            mDataSet.modifiedDate = System.currentTimeMillis();
            // 変更ありの場合、編集日を最新にする
            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_SIMPLE_ASSET, mDataSet);
            bundle.putString(EXTRA_RESULT, RESULT.FINISH.name());
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
        }
    }

    /**
     * Called upon a date change.
     *
     * @param view        The view associated with this listener.
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility
     *                    with {@link Calendar}.
     * @param dayOfMonth  The day of the month that was set.
     */
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    }

    /**
     * バックアップ
     */
    public void backup() {
        mDataSet.displayName = mBinding.detailContainer.editName.getText().toString();
        mDataSet.place = mBinding.detailContainer.editPlace.getText().toString();
        mDataSet.call = mBinding.detailContainer.editTel.getText().toString();
        mDataSet.url = mBinding.detailContainer.editUrl.getText().toString();
    }
}
