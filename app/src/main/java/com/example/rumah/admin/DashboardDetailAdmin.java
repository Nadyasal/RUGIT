package com.example.rumah.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rumah.Login;
import com.example.rumah.R;
import com.example.rumah.Regist;
import com.example.rumah.data.Constant;
import com.example.rumah.data.network.response.get_dashboard_admin.DataItem;

public class DashboardDetailAdmin extends AppCompatActivity {

    TextView tglTransaksi, statusTransaksi, judulRumah, pemilikRumah, pembeliRumah, alamatRumah, deskripsiRumah, hargaRumah;
    ImageView picHome;
    ImageButton back;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_detail_admin);

        DataItem mr = (DataItem) getIntent().getSerializableExtra("data");

        back = findViewById(R.id.backButton);
        tglTransaksi = findViewById(R.id.tglTransaksi);
        statusTransaksi = findViewById(R.id.status_transaksi);
        judulRumah = findViewById(R.id.judul_rumah);
        pemilikRumah = findViewById(R.id.pemilikRUmah);
        pembeliRumah = findViewById(R.id.pembeliRumah);
        alamatRumah = findViewById(R.id.alamatRumah);
        deskripsiRumah = findViewById(R.id.deskripsiRumah);
        hargaRumah = findViewById(R.id.hargaRumah);
        picHome = findViewById(R.id.pic_home);

        Glide.with(getApplicationContext())
                .load(Constant.baseImageURL + mr.getGambar())
                .into(picHome);

        String status = mr.getStatus() == null ? "tersedia" : mr.getStatus();
        statusTransaksi.setText(status);
        pemilikRumah.setText(mr.getPenjual());
        pembeliRumah.setText(mr.getPembeli());
        judulRumah.setText(mr.getJudulRumah());
        alamatRumah.setText(mr.getKelurahan()+" "+mr.getAlamatRumah());
        hargaRumah.setText( mr.getHargaRumah());
        deskripsiRumah.setText(mr.getDescRumah());
        tglTransaksi.setText(mr.getTgl());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(DashboardDetailAdmin.this, AdminDashboardActivity.class);
                DashboardDetailAdmin.this.startActivity(back);
                finish();
            }
        });
    }
}