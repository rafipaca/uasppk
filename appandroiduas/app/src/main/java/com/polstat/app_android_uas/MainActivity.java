package com.polstat.app_android_uas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Aplikasi akan berpindah ke halaman selanjutnya setelah 3 detik
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences userPref = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
                boolean isLoggedIn = userPref.getBoolean("isLoggedIn", false);


                if (isLoggedIn) {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                } else {
//                    startActivity(new Intent(MainActivity.this, AuthActivity.class));
//                    finish();
                    isFirstTime();
                }
            }
        }, 1500); //3000 L = 3 detik
    }

    private void isFirstTime() {
        // Untuk mengecek apakah aplikasi ini sudah pernah dijalankan atau belum
        SharedPreferences preferences = getApplication().getSharedPreferences("onboard", MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

        // Jika aplikasi belum pernah dijalankan maka akan berpindah ke halaman OnBoarding
        if (isFirstTime) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            // Mulai OnBoard activity
            startActivity(new Intent(MainActivity.this, OnBoardActivity.class));
            finish();
        } else {
            // Mulai main activity
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();
        }
    }
}