package com.polstat.app_android_uas.Fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.polstat.app_android_uas.Constant;
import com.polstat.app_android_uas.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {
    private View view;
    private TextInputLayout layoutEmail,layoutPassword,layoutConfirm, layoutNama, layoutUsername;
    private TextInputEditText txtEmail,txtPassword,txtConfirm, txtNama, txtUsername;
    private TextView txtSignIn;
    private Button btnSignUp;
    private ProgressDialog dialog;

    public SignUpFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sign_up,container,false);
        init();
        return view;
    }

    private void init() {
        layoutNama = view.findViewById(R.id.txtLayoutNamaSignUp);
        layoutUsername = view.findViewById(R.id.txtLayoutUsernameSignUp);
        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignUp);
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignUp);
        layoutConfirm = view.findViewById(R.id.txtLayoutConfrimSignUp);

        txtNama = view.findViewById(R.id.txtNamaSignUp);
        txtUsername = view.findViewById(R.id.txtUsernameSignUp);
        txtPassword = view.findViewById(R.id.txtPasswordSignUp);
        txtConfirm = view.findViewById(R.id.txtConfirmSignUp);
        txtSignIn = view.findViewById(R.id.txtSignIn);
        txtEmail = view.findViewById(R.id.txtEmailSignUp);

        btnSignUp = view.findViewById(R.id.btnSignUp);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        txtSignIn.setOnClickListener(v->{
            //change fragments
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new SignInFragment()).commit();
        });

        btnSignUp.setOnClickListener(v->{
            //validate fields first
            if (validate()){
                register();
            }
        });

        txtNama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtNama.getText().toString().isEmpty()){
                    layoutNama.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtEmail.getText().toString().isEmpty()){
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtUsername.getText().toString().isEmpty()){
                    layoutUsername.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtPassword.getText().toString().length()>5){
                    layoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtConfirm.getText().toString().equals(txtPassword.getText().toString())){
                    layoutConfirm.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private boolean validate (){
        if (txtNama.getText().toString().isEmpty()) {
            layoutNama.setError("Nama lengkap harus diisi");
            return false;
        }

        if (txtEmail.getText().toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Email harus diisi");
            return false;
        }

        if (txtUsername.getText().toString().isEmpty()) {
            layoutUsername.setError("Username harus diisi");
            return false;
        }

        if (txtPassword.getText().toString().length()<6){
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Password minimal 6 karakter");
            return false;
        }

        if (!txtConfirm.getText().toString().equals(txtPassword.getText().toString())){
            layoutConfirm.setErrorEnabled(true);
            layoutConfirm.setError("Password tidak sama");
            return false;
        }


        return true;
    }


    private void register(){
        dialog.setMessage("Registering");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.REGISTER, response -> {
            //We get response if connection success
            try {
                JSONObject object = new JSONObject(response);

                if (object.getString("status").equals("200")) {
                    // Jika sukses masuk ke activity login
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignInFragment()).commit();
                    Toast.makeText(getContext(), "Register Success", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Silakan Masuk", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Dapatkan data dari response jika gagal
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject object = new JSONObject(new String(response.data));

                        JSONObject errors = object.getJSONObject("messages");

                        if(errors.has("name")){
                            layoutNama.setErrorEnabled(true);
                            layoutNama.setError(errors.getString("nama_lengkap"));
                        }

                        if (errors.has("email")){
                            layoutEmail.setErrorEnabled(true);
                            layoutEmail.setError(errors.getString("email"));
                        }

                        if (errors.has("nim")){
                            layoutUsername.setErrorEnabled(true);
                            layoutUsername.setError(errors.getString("username"));
                        }

                        if (errors.has("password")){
                            layoutPassword.setErrorEnabled(true);
                            layoutPassword.setError(errors.getString("password"));
                        }

                        if (errors.has("confpassword")){
                            layoutConfirm.setErrorEnabled(true);
                            layoutConfirm.setError(errors.getString("confpassword"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }
        }){
            // Add parameters
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("name", txtNama.getText().toString());
                map.put("email",txtEmail.getText().toString().trim());
                map.put("nim",txtUsername.getText().toString().trim());
                map.put("password",txtPassword.getText().toString());
                map.put("confpassword", txtConfirm.getText().toString());

                return map;
            }
        };

        // Tambahkan request ke queue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


}

