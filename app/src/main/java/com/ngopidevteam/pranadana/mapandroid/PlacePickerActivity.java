package com.ngopidevteam.pranadana.mapandroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.ngopidevteam.pranadana.mapandroid.helper.MyConstant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacePickerActivity extends AppCompatActivity {

    @BindView(R.id.btnplace)
    Button btnplace;
    @BindView(R.id.txtdetailalamat)
    TextView txtdetailalamat;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btnplace)
    public void onViewClicked() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(PlacePickerActivity.this)
            , MyConstant.REQ_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        place = PlacePicker.getPlace(PlacePickerActivity.this, data);

            if (requestCode == MyConstant.REQ_PICKER && resultCode == RESULT_OK) {
                String detailInfo = String.format("Detail Lokasi \n Place : %s " +
                                "\n alamat : %s \n latlong : %s ", place.getName(), place.getAddress(),
                        place.getLatLng().latitude + "," + place.getLatLng().longitude);

                txtdetailalamat.setText(detailInfo);
            }
        }

    public void onDirect(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr="+
                place.getLatLng().latitude+","+ place.getLatLng().longitude));
        startActivity(intent);
    }

}
