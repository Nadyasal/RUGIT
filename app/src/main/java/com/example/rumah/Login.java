package com.example.rumah;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rumah.admin.AdminDashboardActivity;
import com.example.rumah.data.local.SharedPref;
import com.example.rumah.data.network.ApiClient;
import com.example.rumah.data.network.EndPoint;
import com.example.rumah.data.network.response.login.ResponseLogin;
import com.example.rumah.pembeli.DashboardPembeli;
import com.example.rumah.penjual.DashboardPenjual;
import retrofit2.Call;
import retrofit2.Callback;


public class Login extends AppCompatActivity {
    EditText username, password;
    Button signIn;
    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        username = (EditText) findViewById(R.id.usernameLogin);
        password = (EditText) findViewById(R.id.passwordLogin);
        signIn = (Button) findViewById(R.id.signInLogin);
        signUp = (TextView) findViewById(R.id.signUpLogin);

        checkUser();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameKey = username.getText().toString();
                String passwordKey = password.getText().toString();

                if (usernameKey.trim().isEmpty() || passwordKey.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lengkapi data untuk masuk !!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    EndPoint endPoint = ApiClient.getClient().create(EndPoint.class);
                    Call<ResponseLogin> call = endPoint.login(usernameKey, passwordKey);

                    call.enqueue(new Callback<ResponseLogin>() {
                        @Override
                        public void onResponse(Call<ResponseLogin> call, retrofit2.Response<ResponseLogin> response) {
                            if (response.body().getStatus().equals("Berhasil")) {
                                switch (response.body().getData().getRole()) {
                                    case "1": {
                                        SharedPref.setLoggedIn(getApplicationContext(),
                                                true, response.body().getData().getIdPengguna(),
                                                response.body().getData().getRole());
                                        Intent penjual = new Intent(Login.this, AdminDashboardActivity.class);
                                        startActivity(penjual);
                                        finish();
                                        break;
                                    }
                                    case "2": {
                                        AlertDialog builder = new AlertDialog.Builder(view.getContext()).create();
                                        View dialogView= LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_login_alert,null);

                                        Button btnLanjut;

                                        btnLanjut = dialogView.findViewById(R.id.btn_detail_beli);

                                        btnLanjut.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SharedPref.setLoggedIn(getApplicationContext(),
                                                        true, response.body().getData().getIdPengguna(),
                                                        response.body().getData().getRole());
                                                Intent penjual = new Intent(Login.this, DashboardPenjual.class);
                                                startActivity(penjual);
                                                finish();
                                            }
                                        });

                                        builder.setView(dialogView);
                                        builder.setCancelable(false);
                                        builder.show();

                                        break;
                                    }
                                    case "3":
                                        SharedPref.setLoggedIn(getApplicationContext(),
                                                true, response.body().getData().getIdPengguna(),
                                                response.body().getData().getRole());
                                        Intent pembeli = new Intent(Login.this, DashboardPembeli.class);
                                        startActivity(pembeli);
                                        finish();
                                        break;
                                    default:
                                        Toast.makeText(getApplicationContext(), "Login Gagal",
                                                Toast.LENGTH_SHORT).show();
                                        break;
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Login Gagal", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseLogin> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Periksa data kembali "+t, Toast.LENGTH_SHORT).show();
                            Log.d("TAG ", "onFailure: "+t);
                        }
                    });
                }

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent up = new Intent(Login.this, Regist.class);
                Login.this.startActivity(up);
                finish();
            }
        });
    }

    private void checkUser() {
        if (SharedPref.getLoggedStatus(getApplicationContext())) {
            if (SharedPref.getRole(getApplicationContext()).equals("2")) {
                Intent penjual = new Intent(Login.this, DashboardPenjual.class);
                Login.this.startActivity(penjual);
                finish();
            }else if(SharedPref.getRole(getApplicationContext()).equals("1")) {
                Intent penjual = new Intent(Login.this, AdminDashboardActivity.class);
                Login.this.startActivity(penjual);
                finish();
            }else {
                Intent pembeli = new Intent(Login.this, DashboardPembeli.class);
                Login.this.startActivity(pembeli);
                finish();
            }
        }
    }
}