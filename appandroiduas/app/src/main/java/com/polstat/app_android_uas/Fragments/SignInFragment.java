package com.polstat.app_android_uas.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.polstat.app_android_uas.AuthActivity;
import com.polstat.app_android_uas.Constant;
import com.polstat.app_android_uas.HomeActivity;
import com.polstat.app_android_uas.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInFragment extends Fragment {
    private View view;
    private TextInputLayout layoutEmail, layoutPassword;
    private TextInputEditText txtEmail, txtPassword;
    private TextView txtSignUp;
    private Button btnSignIn;
    private ProgressDialog dialog;

    public SignInFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_sign_in, container, false);
        init();
        return view;
    }

    private void init() {
        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignIn);
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignIn);
        txtPassword = view.findViewById(R.id.txtPasswordSignIn);
        txtSignUp = view.findViewById(R.id.txtSignUp);
        txtEmail = view.findViewById(R.id.txtEmailSignIn);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        txtSignUp.setOnClickListener(v -> {
            // Masuk ke fragment sign up
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignUpFragment()).commit();
        });

        btnSignIn.setOnClickListener(v -> {
            // Validate fields first
            if (validate()) {
                login();
            }
        });


        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtEmail.getText().toString().isEmpty()) {
                    layoutEmail.setErrorEnabled(false);
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
                if (txtPassword.getText().toString().length() > 5) {
                    layoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private boolean validate() {
        if (txtEmail.getText().toString().isEmpty()) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Email harus diisi");
            return false;
        }
        if (txtPassword.getText().toString().length() < 6) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Password setidaknya terdiri dari 6 karakter");
            return false;
        }
        return true;
    }


    private void login() {
        dialog.setMessage("Logging in");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.LOGIN, response -> {
            // Dapatkan data dari response jika berhasil
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("200")) {

                    JSONObject user = object.getJSONObject("user");
                    // Simpan data user ke shared preferences
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("token", object.getString("token"));
                    editor.putInt("id", user.getInt("id"));
                    editor.putString("name", user.getString("name"));
                    editor.putString("nim", user.getString("nim"));
                    editor.putString("email", user.getString("email"));
                    editor.putString("role", user.getString("role"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    //If success
                    startActivity(new Intent(((AuthActivity) getContext()), HomeActivity.class));
                    ((AuthActivity) getContext()).finish();
                    Toast.makeText(getContext(), "Login Success", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
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

                        if (errors.has("error")) {
                            Toast.makeText(getContext(), errors.getString("error"), Toast.LENGTH_SHORT).show();
                        }

                        if (errors.has("email")) {
                            layoutEmail.setErrorEnabled(true);
                            layoutEmail.setError(errors.getString("email"));
                        }

                        if (errors.has("password")) {
                            layoutPassword.setErrorEnabled(true);
                            layoutPassword.setError(errors.getString("password"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }
        }) {
            // Add parameters to request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", txtEmail.getText().toString().trim());
                map.put("password", txtPassword.getText().toString());
                return map;
            }
        };

        // Tambahkan request ke queue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}