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
import jp.osaka.cherry.addressbook.utils.SettingsHelper;
import jp.osaka.cherry.addressbook.utils.GeofenceHelper;
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
 * ????????????
 */
public class ImageEditActivity extends AppCompatActivity implements DatePicker.OnDateChangedListener {

    /**
     * @serial ??????
     */
    private final String TAG = "ImageEditActivity";

    /**
     * @serial ??????
     */
    private ImageEditActivity mSelf;

    /**
     * @serial ?????????????????????
     */
    private ActivityImageCreateBinding mBinding;

    /**
     * @serial ??????????????????
     */
    private SimpleAsset mDataSet;

    /**
     * @serial ??????????????????
     */
    private SimpleAsset mBackup;


    /**
     * @serial ??????
     */
    private final SettingsHelper mSettingsHelper = new SettingsHelper();

    /**
     * ????????????????????????
     *
     * @param context ??????????????????
     * @param item    ??????
     * @return ???????????????
     */
    public static Intent createIntent(Context context, SimpleAsset item) {
        Intent intent = new Intent(context, ImageEditActivity.class);
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

        mSelf = this;

        // ????????????????????????
        Intent intent = getIntent();
        mDataSet = intent.getParcelableExtra(EXTRA_SIMPLE_ASSET);
        mBackup = SimpleAsset.createInstance();
        mBackup.copy(mDataSet);

        // ??????????????????
        setTheme(ThemeHelper.getImageEditTheme(mDataSet.color));

        // ????????????????????????
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_create);

        mSettingsHelper.setParent(this);

        // ????????????????????????
        setSupportActionBar(mBinding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        // ??????????????????????????????????????????????????????
        setupFloatingActionButton();

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
        setView();
    }

    /**
     * ???????????????
     */
    void setView() {
        if (mDataSet.imagePath.equals(INVALID_STRING_VALUE)) {
            mBinding.imageView.setScaleType(ImageView.ScaleType.CENTER);
            mBinding.imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_image_black_100dp));
        } else {
            try {
                mBinding.imageView.setColorFilter(null);
                // ???????????????????????????
                Bitmap bitmap = decodeFile(mDataSet.imagePath);
                // imageView????????????
                mBinding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mBinding.imageView.setImageURI(null);
                // ImageView????????????
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
            // ?????????????????????
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
        mBinding.detailContainer.editPlace.setOnClickListener(v -> {
            backup();
            // ?????????????????????
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
     * ???????????????
     *
     * @param activity   ?????????????????????
     * @return ???????????????
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
     * ??????????????????????????????????????????????????????
     */
    protected void setupFloatingActionButton() {
        if (!hasSelfPermission(this)) {
            // ????????????????????????????????????????????????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS.ordinal());
            } else {
                invisibleFab();
            }
        } else {
            // ?????????????????????????????????????????????????????????????????????
            visibleFab();
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
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
     * ?????????????????????????????????????????????????????????
     */
    private void invisibleFab() {
        mBinding.fab.hide();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBackPressed() {
        //???????????????
        setResult();
        super.onBackPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {//???????????????
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

        // ????????????
        ActivityTransition type = ActivityTransition.get(requestCode);
        switch (Objects.requireNonNull(type)) {
            case CHOOSER: {
                if (resultCode == RESULT_OK) {
                    if (mDataSet == null) {
                        Log.d(TAG, "mDataSet is null");
                    }
                    mBinding.imageView.setColorFilter(null);
                    mBinding.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    // ????????????????????????Uri?????????
                    Uri uri = data.getData();
                    // ???????????????????????????
                    Bitmap bitmap = decodeUri(this, uri, mBinding.imageView.getWidth());
                    mBinding.imageView.setImageBitmap(bitmap);
                    // ???????????????
                    mDataSet.imagePath = toPath(this, uri);
                }
                break;
            }
            case MODIFY_GEOFENCE: {
                if (resultCode == RESULT_OK) {
                    // ??????????????????
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
     * ???????????????
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
            // ???????????????????????????????????????????????????
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
     * ??????????????????
     */
    public void backup() {
        mDataSet.displayName = mBinding.detailContainer.editName.getText().toString();
        mDataSet.place = mBinding.detailContainer.editPlace.getText().toString();
        mDataSet.call = mBinding.detailContainer.editTel.getText().toString();
        mDataSet.url = mBinding.detailContainer.editUrl.getText().toString();
        //mDataSet.send = mBinding.detailContainer.editEmail.getText().toString();
    }
}
