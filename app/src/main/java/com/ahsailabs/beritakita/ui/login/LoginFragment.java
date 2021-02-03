package com.ahsailabs.beritakita.ui.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavAction;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ahsailabs.beritakita.R;
import com.ahsailabs.beritakita.configs.Config;
import com.ahsailabs.beritakita.ui.login.models.LoginData;
import com.ahsailabs.beritakita.ui.login.models.LoginResponse;
import com.ahsailabs.beritakita.utils.HttpUtil;
import com.ahsailabs.beritakita.utils.InfoUtil;
import com.ahsailabs.beritakita.utils.SessionUtil;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {
    private TextInputLayout tilUserName;
    private TextInputEditText etUsername;
    private TextInputLayout tilPassword;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private LinearLayout llLoginForm;
    private LinearLayout llLoadingPanel;
    private ProgressBar pbLoadingIndicator;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadViews(view);
        registerClickListener();
    }

    private void loadViews(View root) {
        tilUserName = root.findViewById(R.id.tilUserName);
        etUsername = root.findViewById(R.id.etUsername);
        tilPassword = root.findViewById(R.id.tilPassword);
        etPassword = root.findViewById(R.id.etPassword);
        btnLogin = root.findViewById(R.id.btnLogin);
        llLoginForm = root.findViewById(R.id.llLoginForm);
        llLoadingPanel = root.findViewById(R.id.llLoadingPanel);
        pbLoadingIndicator = root.findViewById(R.id.pbLoadingIndicator);
    }

    private void registerClickListener(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : get all data, then validate, then login process
                tilPassword.setError(null);
                tilUserName.setError(null);

                //get all data
                String strUserName = etUsername.getText().toString();
                String strPassword = etPassword.getText().toString();


                //validasi inputan
                if(TextUtils.isEmpty(strUserName)){
                    tilUserName.setError("Username wajib diisi");
                    return;
                }

                if(TextUtils.isEmpty(strPassword)){
                    tilPassword.setError("Password wajib diisi");
                    return;
                }

                doLogin(strUserName, strPassword);
            }
        });
    }


    private void showLoading() {
        llLoginForm.setVisibility(View.INVISIBLE);
        btnLogin.setVisibility(View.INVISIBLE);
        llLoadingPanel.setVisibility(View.VISIBLE);
        pbLoadingIndicator.setProgress(50);
    }

    private void hideLoading() {
        llLoginForm.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        llLoadingPanel.setVisibility(View.INVISIBLE);
        pbLoadingIndicator.setProgress(0);
    }

    private void doLogin(String strUserName, String strPassword) {
        showLoading();
        AndroidNetworking.post(Config.getLoginUrl())
                .setOkHttpClient(HttpUtil.getCLient(getContext()))
                .addBodyParameter("username", strUserName)
                .addBodyParameter("password", strPassword)
                .setTag("login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(LoginResponse.class, new ParsedRequestListener<LoginResponse>() {
                    @Override
                    public void onResponse(LoginResponse response) {
                        if (response.getStatus() == 1) {
                            //TODO : handleSuccessLogin
                            LoginData loginData = response.getData();
                            SessionUtil.login(requireContext(), loginData);
                            InfoUtil.showToast(getContext(), "Login anda berhasil");
                            //TODO : close login page & show news list page
                            NavOptions navOptions = new NavOptions.Builder()
                                    .setPopUpTo(R.id.nav_home, true)
                                    .build();
                            NavHostFragment.findNavController(LoginFragment.this)
                                    .navigate(R.id.nav_home, null, navOptions);
                        } else {
                            switch (response.getStatus()){
                                case -6: InfoUtil.showToast(getContext(), "Username atau password anda salah"); break;
                                case -1: InfoUtil.showToast(getContext(), "Anda tidak berhak menggunakan api ini"); break;
                                default:
                                    InfoUtil.showToast(getContext(), response.getMessage());
                            }

                        }
                        hideLoading();
                    }

                    @Override
                    public void onError(ANError anError) {
                        InfoUtil.showToast(getContext(), anError.getMessage());
                        hideLoading();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        AndroidNetworking.cancel("login");
        super.onDestroyView();
    }
}