package com.example.dailytask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val db = DatabaseHelper(this)

        val email = findViewById<EditText>(R.id.etRegEmail)
        val pass = findViewById<EditText>(R.id.etRegPassword)
        val btn = findViewById<Button>(R.id.btnRegister)

        btn.setOnClickListener {
            val e = email.text.toString()
            val p = pass.text.toString()

            if (db.register(e, p)) {
                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal mendaftar!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
