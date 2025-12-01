package com.example.dailytask

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class ProfileActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var currentUserEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = DatabaseHelper(this)

        currentUserEmail = intent.getStringExtra("EXTRA_EMAIL") ?: ""
        
        val currentUsername = if (currentUserEmail.isNotEmpty()) db.getUsername(currentUserEmail) else "User"
        val currentAvatarId = if (currentUserEmail.isNotEmpty()) db.getUserAvatar(currentUserEmail) else 0

        val tvUsername = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val imgAvatar = findViewById<ImageView>(R.id.imgProfileAvatar)
        val btnLogout = findViewById<Button>(R.id.btnProfileLogout)
        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)
        val btnEditAvatar = findViewById<ImageView>(R.id.btnEditAvatar)
        val cardAppInfo = findViewById<CardView>(R.id.cardAppInfo)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        tvUsername.text = currentUsername
        tvEmail.text = currentUserEmail
        
        updateAvatarIcon(imgAvatar, currentAvatarId)

        btnBack.setOnClickListener {
            finish()
        }

        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
        
        btnEditProfile.setOnClickListener {
            showEditProfileDialog(currentUsername, currentAvatarId)
        }
        
        btnEditAvatar.setOnClickListener {
             showEditProfileDialog(currentUsername, currentAvatarId)
        }
        
        cardAppInfo.setOnClickListener {
             showAppInfoDialog()
        }
    }

    private fun updateAvatarIcon(imageView: ImageView, avatarId: Int) {
        val iconRes = when(avatarId) {
            1 -> android.R.drawable.ic_menu_myplaces
            2 -> android.R.drawable.ic_menu_camera
            3 -> android.R.drawable.ic_menu_compass
            else -> android.R.drawable.sym_def_app_icon
        }
        imageView.setImageResource(iconRes)
    }

    private fun showEditProfileDialog(currentName: String, currentAvatarId: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<EditText>(R.id.etEditName)
        val imgOption1 = dialogView.findViewById<ImageView>(R.id.imgOption1)
        val imgOption2 = dialogView.findViewById<ImageView>(R.id.imgOption2)
        val imgOption3 = dialogView.findViewById<ImageView>(R.id.imgOption3)
        val imgOptionDefault = dialogView.findViewById<ImageView>(R.id.imgOptionDefault)

        etName.setText(currentName)
        var selectedAvatarId = currentAvatarId

        fun updateSelection(id: Int) {
            selectedAvatarId = id
            imgOptionDefault.alpha = if (id == 0) 1.0f else 0.3f
            imgOption1.alpha = if (id == 1) 1.0f else 0.3f
            imgOption2.alpha = if (id == 2) 1.0f else 0.3f
            imgOption3.alpha = if (id == 3) 1.0f else 0.3f
        }
        updateSelection(selectedAvatarId)

        imgOptionDefault.setOnClickListener { updateSelection(0) }
        imgOption1.setOnClickListener { updateSelection(1) }
        imgOption2.setOnClickListener { updateSelection(2) }
        imgOption3.setOnClickListener { updateSelection(3) }

        AlertDialog.Builder(this)
            .setTitle("Edit Profil")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = etName.text.toString().trim()
                if (newName.isNotEmpty()) {
                    if (db.updateProfile(currentUserEmail, newName, selectedAvatarId)) {
                        Toast.makeText(this, "Profil diperbarui!", Toast.LENGTH_SHORT).show()
                        finish()
                        startActivity(intent) 
                    } else {
                        Toast.makeText(this, "Gagal update profil", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    private fun showAppInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle("Info Aplikasi")
            .setMessage("DailyTask v1.0\n\nAplikasi manajemen tugas harian sederhana.\nDibuat untuk membantu produktivitas Anda.")
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
            .setPositiveButton("Logout") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}
