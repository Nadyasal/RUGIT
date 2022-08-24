package com.example.rumah.pembeli;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.rumah.R;
import com.example.rumah.adapter.adapterRumah;
import com.example.rumah.data.network.ApiClient;
import com.example.rumah.data.network.EndPoint;
import com.example.rumah.data.network.response.get_rumah.DataItem;
import com.example.rumah.data.network.response.get_rumah.ResponseGetRumahByPengguna;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;
    EditText edt_cari_rumah;
    private ProgressDialog progress;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_home_pembeli);
        edt_cari_rumah = view.findViewById(R.id.edt_cari_rumah);
        recyclerView.setHasFixedSize(true);

        edt_cari_rumah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            private Timer timer = new Timer();
            private final long DELAY = 500; // Milliseconds delay

            @Override
            public void afterTextChanged(Editable editable) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                getRumah(editable.toString());
                            }
                        },
                        DELAY
                );
            }
        });
        if(getActivity()!=null){
            getRumah("");
        }
    }


    public void showLoadingDialog() {

        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress = new ProgressDialog(getActivity());
                    progress.setMessage("Loading...");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.show();
                }
            });

        }
    }

    public void dismissLoadingDialog() {

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    private void getRumah(String q) {
        showLoadingDialog();
        EndPoint endPoint = ApiClient.getClient().create(EndPoint.class);
        Call<ResponseGetRumahByPengguna> call = endPoint.getAllRumah(q.isEmpty() ? "" : q);

        call.enqueue(new retrofit2.Callback<ResponseGetRumahByPengguna>() {
            @Override
            public void onResponse(Call<ResponseGetRumahByPengguna> call, retrofit2.Response<ResponseGetRumahByPengguna> response) {
                if (response.isSuccessful()) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    ArrayList<DataItem> data = response.body().getData();
                    Set<String> nameSet = new HashSet<>();
                    if (data != null) {
                        List<DataItem> dataUniqe = data.stream()
                                .filter(e -> nameSet.add(e.getGambar()))
                                .collect(Collectors.toList());
                        adapterRumah rumah = new adapterRumah(dataUniqe, false, getContext());
                        recyclerView.setAdapter(rumah);
                    }
                }
                dismissLoadingDialog();
            }

            @Override
            public void onFailure(Call<ResponseGetRumahByPengguna> call, Throwable t) {
                dismissLoadingDialog();
            }
        });
    }
}