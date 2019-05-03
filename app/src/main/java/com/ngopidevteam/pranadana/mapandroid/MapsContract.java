package com.ngopidevteam.pranadana.mapandroid;

import com.ngopidevteam.pranadana.mapandroid.model.RoutesItem;

import java.util.List;

public interface MapsContract {

    interface MapView{
        void pesan(String isipesan);
        void datajarak(String jarak);
        void datadurasi (String durasi);
        void dataharga (String harga);
        void dataMap(List<RoutesItem> datamap);
        void pesanerror (String msg);
    }

    interface MapPresenter{
        void getData(String lokasiawal, String lokasitujuan, String key);
    }
}
