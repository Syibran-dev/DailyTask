package com.example.dailytask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = DatabaseHelper(this)

        val email = findViewById<EditText>(R.id.etEmail)
        val pass = findViewById<EditText>(R.id.etPassword)
        val btn = findViewById<Button>(R.id.btnLogin)
        val tvReg = findViewById<TextView>(R.id.tvRegister)

        tvReg.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btn.setOnClickListener {
            val e = email.text.toString()
            val p = pass.text.toString()

            if (db.login(e, p)) {
                Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Email/password salah!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
