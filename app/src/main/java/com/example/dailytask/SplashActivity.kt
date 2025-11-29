package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {

    // Waktu delay sebelum pindah (3 detik)
    private val SPLASH_TIME_OUT: Long = 3000

    // Anotasi ini menghilangkan warning untuk kode yang sudah usang di dalam fungsi ini
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Handler untuk menunda eksekusi kode
        Handler(Looper.getMainLooper()).postDelayed({

            // Pindah ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Tambahkan Efek Animasi Transisi (Fade)
            // Warning muncul di sini, tapi di-suppress oleh anotasi di atas.
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            // Tutup Splash Activity
            finish()

        }, SPLASH_TIME_OUT)
    }
}