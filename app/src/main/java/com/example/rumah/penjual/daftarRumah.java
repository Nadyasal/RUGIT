package com.example.rumah.penjual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.rumah.R;
import com.example.rumah.adapter.adapterRumah;
import com.example.rumah.data.local.SharedPref;
import com.example.rumah.data.network.ApiClient;
import com.example.rumah.data.network.EndPoint;
import com.example.rumah.data.network.response.get_rumah.DataItem;
import com.example.rumah.data.network.response.get_rumah.ResponseGetRumahByPengguna;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class daftarRumah extends AppCompatActivity {
    ImageButton back;
    RecyclerView rcvRumah;
    List<DataItem> dataItem;
    private ArrayList<DataItem> dataRumah = new ArrayList<DataItem>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_rumah);

        back = (ImageButton) findViewById(R.id.back);
        rcvRumah = (RecyclerView) findViewById(R.id.rcvRumah);

        rcvRumah.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getPenggunaId();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dahsboard = new Intent(daftarRumah.this, DashboardPenjual.class);
                daftarRumah.this.startActivity(dahsboard);
                finish();
            }
        });
    }

    private void getPenggunaId() {
        String idPengguna = SharedPref.getIdPengguna(getApplicationContext());
        if(!idPengguna.isEmpty()){
            EndPoint endPoint = ApiClient.getClient().create(EndPoint.class);
            Call<ResponseGetRumahByPengguna> call = endPoint.getRumahByPenggunaId(idPengguna);

            call.enqueue(new retrofit2.Callback<ResponseGetRumahByPengguna>() {
                @Override
                public void onResponse(Call<ResponseGetRumahByPengguna> call, retrofit2.Response<ResponseGetRumahByPengguna> response) {
                    if(response.isSuccessful()){
                        dataRumah = response.body().getData();
                        adapterRumah rumah= new adapterRumah(dataRumah);
                        rcvRumah.setAdapter(rumah);
                    }
                }

                @Override
                public void onFailure(Call<ResponseGetRumahByPengguna> call, Throwable t) {
                    Log.d("Rumah", "onFailure: "+t.getMessage());
                }
            });
        }

    }
}