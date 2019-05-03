package com.ngopidevteam.pranadana.mapandroid;

import com.ngopidevteam.pranadana.mapandroid.helper.DirectionMapsV2;
import com.ngopidevteam.pranadana.mapandroid.helper.HeroHelper;
import com.ngopidevteam.pranadana.mapandroid.model.Distance;
import com.ngopidevteam.pranadana.mapandroid.model.Duration;
import com.ngopidevteam.pranadana.mapandroid.model.LegsItem;
import com.ngopidevteam.pranadana.mapandroid.model.ResponseMap;
import com.ngopidevteam.pranadana.mapandroid.model.RoutesItem;
import com.ngopidevteam.pranadana.mapandroid.network.MyRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsPresenter implements MapsContract.MapPresenter {

    MapsContract.MapView view;
    private List<RoutesItem> dataMap;
    private List<LegsItem> legs;
    private Distance distance;
    private Duration duration;
    private String dataGaris;

    public MapsPresenter(MapsContract.MapView view) {
        this.view = view;
    }

    @Override
    public void getData(String lokasiawal, String lokasitujuan, String key) {
        MyRetrofit.getInstaceRetrofit().getRute(
                lokasiawal,
                lokasitujuan,
                key).enqueue(new Callback<ResponseMap>() {
            @Override
            public void onResponse(Call<ResponseMap> call, Response<ResponseMap> response) {
                if (response.isSuccessful()) {
                    dataMap = response.body().getRoutes();
                    legs = dataMap.get(0).getLegs();
                    distance = legs.get(0).getDistance();
                    duration = legs.get(0).getDuration();
                    view.datajarak(distance.getText());
                    view.datadurasi(duration.getText());

                    double harga = Math.ceil(Double.valueOf(distance.getValue() / 1000) * 1000);
                    view.dataharga(HeroHelper.toRupiahFormat2(String.valueOf(harga)));
                    view.dataMap(dataMap);
                    view.pesan("berhasil menampilkan data");
                } else {
                    view.pesan("gagal tampil data");
                }
            }

            @Override
            public void onFailure(Call<ResponseMap> call, Throwable t) {
                view.pesanerror(t.getMessage());
            }
        });
    }
}
