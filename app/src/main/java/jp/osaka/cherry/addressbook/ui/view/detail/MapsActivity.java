package jp.osaka.cherry.addressbook.ui.view.detail;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.constants.GEOFENCE;
import jp.osaka.cherry.addressbook.ui.GeofenceAsset;
import jp.osaka.cherry.addressbook.ui.view.BaseAdmobActivity;
import jp.osaka.cherry.addressbook.ui.view.GoogleMapPropety;
import jp.osaka.cherry.addressbook.utils.GeofenceHelper;
import jp.osaka.cherry.addressbook.utils.SettingsHelper;

import static android.R.layout.simple_expandable_list_item_1;
import static jp.osaka.cherry.addressbook.Config.LOG_I;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_DWELL_DATE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_ENTER_DATE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_EXIT_DATE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_EXPIRATION_DURATION;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_GEOFENCE_ASSET;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_GOOGLE_MAP_PROPETY;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LATITUDE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LOCATION;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LOITERING_DELAY;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_LONGITUDE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_MODIFIED_DATE;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_NOTIFICATION_RESPONSIVENSS;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_RADIUS;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_TRANSITION;
import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_TRANSITION_TYPE;
import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_EXPIRATION_DURATION_VAUE;
import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_LOITERING_DELAY_VALUE;
import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_NOTIFICATION_RESPONSIVENESS_VALUE;
import static jp.osaka.cherry.addressbook.constants.GEOFENCE.DEFAULT_TRANSITION_TYPE_VALUE;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_INT_VALUE;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_LONG_VALUE;
import static jp.osaka.cherry.addressbook.constants.INVALID.INVALID_STRING_VALUE;


/**
 * ????????????
 */
public class MapsActivity extends BaseAdmobActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    /**
     * @serial ??????
     */
    private static final String TAG = "MapsActivity";

    /**
     * @serial ??????
     */
    private MapsActivity mSelf;

    /**
     * @serial ??????
     */
    private GoogleMap mMap;

    /**
     * @serial ????????????
     */
    private SearchView searchView;

    /**
     * @serial ????????????
     */
    private Location mLocation;

    /**
     * @serial ??????????????????????????????
     */
    private GeofenceAsset mGeofenceAsset;

    /**
     * @serial ??????
     */
    private final SettingsHelper mSettingsHelper = new SettingsHelper();

    /**
     * ????????????????????????
     *
     * @param context        ??????????????????
     * @param location       ????????????
     * @param geofence ??????????????????????????????
     * @return ???????????????
     */
    public static Intent createIntent(Context context, Location location, GeofenceAsset geofence, GoogleMapPropety propety) {
        Intent intent = new Intent(context, MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_LOCATION, location);
        bundle.putParcelable(EXTRA_GEOFENCE_ASSET, geofence);
        bundle.putParcelable(EXTRA_GOOGLE_MAP_PROPETY, propety);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * ????????????????????????
     *
     * @param context ??????????????????
     * @param location ????????????
     * @param propety ???????????????
     * @return ???????????????
     */
    public static Intent createIntent(Context context, Location location, GoogleMapPropety propety) {
        Intent intent = new Intent(context, MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_LOCATION, location);
        bundle.putParcelable(EXTRA_GOOGLE_MAP_PROPETY, propety);
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

        mSettingsHelper.setParent(this);

        setContentView(R.layout.activity_maps);

        // ???????????????????????????
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        // ???????????????????????????????????????
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLocation = bundle.getParcelable(EXTRA_LOCATION);
            mGeofenceAsset = bundle.getParcelable(EXTRA_GEOFENCE_ASSET);
        }

        // ???????????????
        // ?????????????????????
        searchView = findViewById(R.id.search_view);
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.onActionViewCollapsed();
        searchView.clearFocus();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean onQueryTextChange(String s) {
                List<Address> members = getAddress(s);
                CustomAdapter adapter = new CustomAdapter(mSelf,
                        simple_expandable_list_item_1, members);

                @SuppressLint("CutPasteId") ListView lv = findViewById(R.id.listview);
                ViewGroup.LayoutParams params = lv.getLayoutParams();
                // ?????????????????????
                params.width = searchView.getWidth();
                lv.setLayoutParams(params);
                lv.setAdapter(adapter);
                lv.setHeaderDividersEnabled(true);
                // ??????????????????????????????????????????
                lv.setOnItemClickListener((parent, view, position, id) -> {
                    ListView listView = (ListView) parent;
                    Address item = (Address) listView.getItemAtPosition(position);
                    LatLng latlng = getLatLng(item);
                    moveMap(latlng);
                    searchView.setIconified(false);
                    searchView.setIconifiedByDefault(false);
                    searchView.onActionViewCollapsed();
                    searchView.clearFocus();
                    @SuppressLint("CutPasteId") ListView lv1 = findViewById(R.id.listview);
                    lv1.setAdapter(null);
                });
                return false;
            }
        });

        if (LOG_I) {
            Log.i(TAG, "onCreate#leave");
        }
    }

    /**
     * ????????????
     */
    public static class CustomAdapter extends ArrayAdapter<Address> {

        /**
         * @serial ???????????????
         */
        private final LayoutInflater layoutInflater_;

        /**
         * ?????????????????????
         *
         * @param context ??????????????????
         * @param textViewResourceId ????????????ID
         * @param objects ??????????????????
         */
        CustomAdapter(Context context, int textViewResourceId, List<Address> objects) {
            super(context, textViewResourceId, objects);
            layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // ????????????(position)?????????????????????
            Address item = getItem(position);

            // convertView??????????????????????????????????????????????????????null???????????????????????????
            if (null == convertView) {
                convertView = layoutInflater_.inflate(R.layout.item_line_map, null);
            }

            TextView textView;
            textView = convertView.findViewById(R.id.title);
            if (textView != null && item != null) {
                textView.setText(GeofenceHelper.getLabel(item));
            }

            return convertView;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchView.setQuery(query, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // ???????????????
        mMap = googleMap;
        GeofenceHelper.setup(googleMap, mSettingsHelper.getGoogleMapPropety());
        mMap.setOnMapClickListener(this);

        // ???????????????
        if (mGeofenceAsset == null) {

            // ???????????????
            if (mLocation != null) {
                moveMap(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
            } else {
                moveMap(new LatLng(GEOFENCE.DEFAULT_LAT, GEOFENCE.DEFAULT_LNG));
            }

        } else {

            // ???????????????????????????
            showGeofence(mGeofenceAsset);

            // ???????????????
            moveMap(mGeofenceAsset);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (mMap != null) {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            float zoom = cameraPosition.zoom;
            float radius = GeofenceHelper.toRadius(zoom);
            addGeofence(latLng, radius);
            showGeofence(mGeofenceAsset);
        }
    }


    /**
     * ???????????????????????????
     *
     * @param geofenceAsset ??????????????????
     */
    private void showGeofence(GeofenceAsset geofenceAsset) {
        int color = 0x80007b50;
        mMap.clear();
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(geofenceAsset.latitude, geofenceAsset.longitude))
                .fillColor(color)
                .strokeColor(0x00ffffff)
                .radius(geofenceAsset.radius);
        mMap.addCircle(circleOptions);
    }

    /**
     * ???????????????????????????
     *
     * @param latlng ???????????????
     * @param radius ??????
     */
    private void addGeofence(LatLng latlng, float radius) {
        if (mGeofenceAsset == null) {
            mGeofenceAsset = new GeofenceAsset(
                    INVALID_STRING_VALUE,
                    latlng.latitude,
                    latlng.longitude,
                    radius,
                    DEFAULT_EXPIRATION_DURATION_VAUE,
                    DEFAULT_TRANSITION_TYPE_VALUE,
                    DEFAULT_LOITERING_DELAY_VALUE,
                    DEFAULT_NOTIFICATION_RESPONSIVENESS_VALUE,
                    String.valueOf(UUID.randomUUID()),
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    INVALID_LONG_VALUE,
                    INVALID_LONG_VALUE,
                    INVALID_LONG_VALUE,
                    INVALID_INT_VALUE,
                    INVALID_STRING_VALUE,
                    false,
                    false,
                    INVALID_STRING_VALUE
            );
        } else {
            Bundle bundle = mGeofenceAsset.toBundle();
            bundle.putDouble(EXTRA_LATITUDE, latlng.latitude);
            bundle.putDouble(EXTRA_LONGITUDE, latlng.longitude);
            bundle.putFloat(EXTRA_RADIUS, radius);
            bundle.putLong(EXTRA_EXPIRATION_DURATION, DEFAULT_EXPIRATION_DURATION_VAUE);
            bundle.putInt(EXTRA_TRANSITION_TYPE, DEFAULT_TRANSITION_TYPE_VALUE);
            bundle.putInt(EXTRA_LOITERING_DELAY, DEFAULT_LOITERING_DELAY_VALUE);
            bundle.putInt(EXTRA_NOTIFICATION_RESPONSIVENSS, DEFAULT_NOTIFICATION_RESPONSIVENESS_VALUE);
            bundle.putLong(EXTRA_MODIFIED_DATE, System.currentTimeMillis());
            bundle.putLong(EXTRA_ENTER_DATE, INVALID_LONG_VALUE);
            bundle.putLong(EXTRA_DWELL_DATE, INVALID_LONG_VALUE);
            bundle.putLong(EXTRA_EXIT_DATE, INVALID_LONG_VALUE);
            bundle.putInt(EXTRA_TRANSITION, INVALID_INT_VALUE);
            mGeofenceAsset = new GeofenceAsset(bundle);
        }

        // ????????????
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_GEOFENCE_ASSET, mGeofenceAsset);
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
    }

    /**
     * ???????????????
     *
     * @param latLng ????????????
     */
    private void moveMap(LatLng latLng) {
        if (mMap != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15.0f).build();
            CameraUpdate camera = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(camera);
        }
    }

    /**
     * ???????????????
     *
     * @param simpleGeofence ??????????????????????????????
     */
    private void moveMap(GeofenceAsset simpleGeofence) {
        LatLng target = new LatLng(simpleGeofence.latitude, simpleGeofence.longitude);
        float zoom = GeofenceHelper.toZoomLevel(simpleGeofence.radius);
        if (mMap != null) {
            CameraPosition pos = new CameraPosition(target, zoom, 0, 0);
            CameraUpdate camera = CameraUpdateFactory.newCameraPosition(pos);
            mMap.animateCamera(camera);
        }
    }


    /**
     * ?????????????????????
     *
     * @param query ?????????
     */
    private List<Address> getAddress(String query) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocationName(query, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressList;
    }

    /**
     * ?????????????????????
     *
     * @param address ????????????
     */
    private LatLng getLatLng(Address address) {
        return new LatLng(address.getLatitude(), address.getLongitude());
    }
}
