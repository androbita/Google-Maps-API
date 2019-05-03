package com.ngopidevteam.pranadana.mapandroid;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ngopidevteam.pranadana.mapandroid.helper.DirectionMapsV2;
import com.ngopidevteam.pranadana.mapandroid.helper.GPStrack;
import com.ngopidevteam.pranadana.mapandroid.helper.HeroHelper;
import com.ngopidevteam.pranadana.mapandroid.helper.MyConstant;
import com.ngopidevteam.pranadana.mapandroid.helper.MyFunction;
import com.ngopidevteam.pranadana.mapandroid.model.Distance;
import com.ngopidevteam.pranadana.mapandroid.model.Duration;
import com.ngopidevteam.pranadana.mapandroid.model.LegsItem;
import com.ngopidevteam.pranadana.mapandroid.model.ResponseMap;
import com.ngopidevteam.pranadana.mapandroid.model.RoutesItem;
import com.ngopidevteam.pranadana.mapandroid.network.MyRetrofit;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ngopidevteam.pranadana.mapandroid.helper.MyConstant.REQ_AKHIR;
import static com.ngopidevteam.pranadana.mapandroid.helper.MyConstant.REQ_AWAL;

public class MapsActivity extends MyFunction implements OnMapReadyCallback, MapsContract.MapView {

//    private static final int REQUEST_LOCATION = 1;
    @BindView(R.id.edtawal)
    EditText edtawal;
    @BindView(R.id.edtakhir)
    EditText edtakhir;
    @BindView(R.id.textjarak)
    TextView textjarak;
    @BindView(R.id.textwaktu)
    TextView textwaktu;
    @BindView(R.id.textharga)
    TextView textharga;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.btnlokasiku)
    Button btnlokasiku;
    @BindView(R.id.btnpanorama)
    Button btnpanorama;
    @BindView(R.id.linearbottom)
    LinearLayout linearbottom;
    @BindView(R.id.spinmode)
    Spinner spinmode;
    @BindView(R.id.relativemap)
    RelativeLayout relativemap;
    @BindView(R.id.frame1)
    FrameLayout frame1;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private GPStrack gps;
    private double lat;
    private double lon;
    private String name_location;
    private LatLng lokasiku;
    private Intent intent;
    private List<RoutesItem> dataMap;
    private List<LegsItem> legs;
    private Distance distance;
    private Duration duration;
    private String dataGaris;
    private double latawal;
    private double lonawal;
    private double latakhir;
    private double lonakhir;
    private MapsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cekStatusGPS();
        presenter = new MapsPresenter(this);
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        myLocation();
    }

    private void myLocation() {
        gps = new GPStrack(this);
        lat = gps.getLatitude();
        lon = gps.getLongitude();
        name_location = convertLocation(lat, lon);
        Toast.makeText(this, "lat :" + lat + "\n lon :" + lon, Toast.LENGTH_SHORT).show();
        lokasiku = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(lokasiku).title(name_location)).
                setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku, 17));
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private String convertLocation(double lat, double lon) {
        name_location = null;
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);
            if (list != null && list.size() > 0) {
                name_location = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();

                //fetch data from addresses
            } else {
                Toast.makeText(this, "kosong", Toast.LENGTH_SHORT).show();
                //display Toast message
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name_location;
    }

    @OnClick({R.id.edtawal, R.id.edtakhir, R.id.btnlokasiku, R.id.btnpanorama})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edtawal:
                searchLocation(REQ_AWAL);
                break;
            case R.id.edtakhir:
                searchLocation(REQ_AKHIR);
                break;
            case R.id.btnlokasiku:
                myLocation();
                break;
            case R.id.btnpanorama:
                myPanorama();
                break;
        }
    }

    private void myPanorama() {
        relativemap.setVisibility(View.GONE);
        frame1.setVisibility(View.VISIBLE);
        SupportStreetViewPanoramaFragment panorama = (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                .findFragmentById(R.id.panorama);
        panorama.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                streetViewPanorama.setPosition(lokasiku);
            }
        });
    }

    private void searchLocation(int reqcode) {
        AutocompleteFilter filter = new AutocompleteFilter.Builder().
                setCountry("ID")
                .build();

        try {
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filter)
                    .build(MapsActivity.this);
            startActivityForResult(intent, reqcode);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Place place = PlaceAutocomplete.getPlace(this,data);
        if (requestCode==REQ_AWAL&&resultCode==RESULT_OK){
            latawal =place.getLatLng().latitude;
            lonawal =place.getLatLng().longitude;
            name_location =place.getName().toString();
            edtawal.setText(name_location);
            mMap.clear();
            addMarker(lat,lon);
        }else if  (requestCode==REQ_AKHIR&&resultCode==RESULT_OK){
            latakhir =place.getLatLng().latitude;
            lonakhir =place.getLatLng().longitude;
            name_location =place.getName().toString();
            edtakhir.setText(name_location);
//            mMap.clear();
            addMarker(lat,lon);
//            aksesRute();
            String key = getString(R.string.google_maps_key);
            String lokasiawal = String.valueOf(latawal+","+lonawal);
            String lokasiakhir = String.valueOf(latakhir+","+lonakhir);
            presenter.getData(lokasiawal, lokasiakhir, key);
        }
    }

    private void aksesRute() {
        String key = getString(R.string.google_maps_key);
        String lokasiawal = String.valueOf(latawal + ","+lonawal);
        String lokasiakhir = String.valueOf(latakhir+","+lonakhir);
        MyRetrofit.getInstaceRetrofit().getRute(
//                edtawal.getText().toString().trim(),
//                edtakhir.getText().toString().trim(),
                lokasiawal,
                lokasiakhir,
                key
        ).enqueue(new Callback<ResponseMap>() {
            @Override
            public void onResponse(Call<ResponseMap> call, Response<ResponseMap> response) {
                if (response.isSuccessful()){
                    String status = response.body().getStatus();
                    if (status.equals("OK")){
                        dataMap = response.body().getRoutes();
                        legs = dataMap.get(0).getLegs();
                        distance = legs.get(0).getDistance();
                        duration = legs.get(0).getDuration();
                        textjarak.setText(distance.getText());
                        textwaktu.setText(distance.getText());

                        double harga = Math.ceil(Double.valueOf(distance.getValue()/1000)*1000);
                        textharga.setText(HeroHelper.toRupiahFormat2(String.valueOf(harga)));

                        DirectionMapsV2 mapsV2 = new DirectionMapsV2(MapsActivity.this);
                        dataGaris = dataMap.get(0).getOverviewPolyline().getPoints();
                        mapsV2.gambarRoute(mMap, dataGaris);
                    }else {
                        Toast.makeText(MapsActivity.this, "gagal tampil data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMap> call, Throwable t) {

            }
        });
    }

    private void addMarker(double lat, double lon) {
        lokasiku = new LatLng(lat, lon);
        name_location = convertLocation(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku, 15));
        mMap.addMarker(new MarkerOptions().position(lokasiku).title(name_location));
    }

    @Override
    public void pesan(String isipesan) {
        Toast.makeText(MapsActivity.this, isipesan, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void datajarak(String jarak) {
        textjarak.setText(jarak);
    }

    @Override
    public void datadurasi(String durasi) {
        textwaktu.setText(durasi);
    }

    @Override
    public void dataharga(String harga) {
        textharga.setText(harga);
    }

    @Override
    public void dataMap(List<RoutesItem> datamap) {
        DirectionMapsV2 mapsV2 = new DirectionMapsV2(MapsActivity.this);
        dataGaris = datamap.get(0).getOverviewPolyline().getPoints();
        mapsV2.gambarRoute(mMap, dataGaris);
    }

    @Override
    public void pesanerror(String msg) {

    }
}
