package com.example.rumah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rumah.dialog.CustomDialog;
import com.example.rumah.dialog.OtpDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Regist extends AppCompatActivity {

    private String tname,taddress,tphone,temail,tusername,tpass,trek;
    public String ip ="192.168.95.152";
    private StringRequest request;
    private RequestQueue queue;
    Button signUp;
    ImageButton back;
    RadioGroup pendaftar;
    RadioButton penjual,pembeli;
    EditText name,address,phone,email,username,pass,rek;
    OtpDialog Sotp = new OtpDialog(Regist.this);
    CustomDialog dia = new CustomDialog(Regist.this);
    public FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        name        = (EditText)findViewById(R.id.name);
        address     = (EditText)findViewById(R.id.address);
        phone       = (EditText)findViewById(R.id.phone);
        email       = (EditText)findViewById(R.id.email);
        username    = (EditText)findViewById(R.id.user);
        pass        = (EditText)findViewById(R.id.pass);
        signUp      = (Button) findViewById(R.id.signUp);
        back        = (ImageButton) findViewById(R.id.back);
        pendaftar   = (RadioGroup) findViewById(R.id.pendaftar);
        penjual     = (RadioButton) findViewById(R.id.penjual);
        pembeli     = (RadioButton) findViewById(R.id.pembeli);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                tname = name.getText().toString().trim();
                taddress = address.getText().toString().trim();
                tphone = phone.getText().toString().trim();
                temail = email.getText().toString().trim();
                tusername = username.getText().toString().trim();
                tpass = pass.getText().toString().trim();




                if (name.getText().toString().trim().isEmpty()) {
                    name.setError("Lengkapi Nama Anda !");
                } else  if (address.getText().toString().trim().isEmpty()) {
                    address.setError("Lengkapi Alamat Anda !");
                }else  if (phone.getText().toString().trim().isEmpty() || phone.getText().toString().trim().length() <= 10 ) {
                    phone.setError("Lengkapi Nomor Telepon Anda dengan Benar !");
                }else  if (username.getText().toString().trim().isEmpty()) {
                    username.setError("Lengkapi Nama Pengguna Anda !");
                }else  if (pass.getText().toString().trim().isEmpty() ) {
                    pass.setError("Lengkapi Kata Sandi Anda !");
                }else{
                    daftar();
                    sendOtp();
                }
            }
        });

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent login = new Intent(Regist.this, Login.class);
                        Regist.this.startActivity(login);
                        finish();
                    }
                });
    }

    public void daftar() {
        queue = Volley.newRequestQueue(Regist.this);
        String URL1 = "http://"+ip+"/rumah/insertPengguna.php";
        request = new StringRequest(Request.Method.POST, URL1, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                // on below line we are displaying a success toast message.

            } catch (JSONException e) {
                e.printStackTrace();
            }
            // and setting data to edit text as empty
            name.setText("");
            address.setText("");
            phone.setText("");
            email.setText("");
            phone.setText("");
            username.setText("");
            pass.setText("");
            pendaftar.clearCheck();
        }, error -> {
            // method to handle errors.
            Log.e("Error", error.getMessage());
            Toast.makeText(Regist.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public String getBodyContentType() {
                // as we are passing data in the form of url encoded
                // so we are passing the content type below
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                // below line we are creating a map for storing
                // our values in key and value pair.
                HashMap<String, String> data = new HashMap<>();

                // on below line we are passing our
                // key and value pair to our parameters.
                data.put("tname", tname);
                data.put("taddress", taddress);
                data.put("tphone", tphone);
                data.put("temail", temail);
                data.put("tusername", tusername);
                data.put("tpass", tpass);
                if (penjual.isChecked()){
                    data.put("trole", "2");
                }else if(pembeli.isChecked()){
                    data.put("trek", "-");
                    data.put("trole", "3");
                }

                // at last we are returning our params.
                return data;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }

    private void sendOtp() {


        signUp.setVisibility(View.INVISIBLE);
        Sotp.startDialog();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+62" + phone.getText().toString(),
                60,
                TimeUnit.SECONDS,
                Regist.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                            progressBar.setVisibility(View.GONE);
                        signUp.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        signUp.setVisibility(View.VISIBLE);
                        Toast.makeText(Regist.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        signUp.setVisibility(View.VISIBLE);
                        Sotp.startDialog();
                        Intent intent=new Intent(getApplicationContext(),kodeOtp.class);

//                        Toast.makeText(Regist.this, backendotp, Toast.LENGTH_SHORT).show();
//                        Log.d(TAG,"onCodeSent : "+ backendotp)
                        intent.putExtra("mobile",phone.getText().toString().trim());
                        intent.putExtra("backendotp",backendotp);
                        startActivity(intent);
                    }
                }

        );
    }
}
