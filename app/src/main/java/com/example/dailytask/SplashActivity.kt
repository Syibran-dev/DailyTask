package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val btn = findViewById<Button>(R.id.btnMasuk)
        btn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
