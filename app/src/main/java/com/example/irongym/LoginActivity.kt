package com.example.irongym

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.irongym.databinding.ActivityLoginBinding
import com.example.irongym.viewModel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("token", null)

        if (!savedToken.isNullOrEmpty()) {
            navigateToMain()
        }

        Glide.with(this)
            .load("https://w0.peakpx.com/wallpaper/499/867/HD-wallpaper-sports-bodybuilding-tattoo-muscle-gym-bodybuilder-man.jpg")
            .into(binding.ivBackground)

        binding.btnLogin.setOnClickListener {
            val email = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.loginUser(email, password, this)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.registerUser(email, password)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.authResult.observe(this) { result ->
            Log.d("LoginActivity", "AuthResult: success=${result.success}, token=${result.token}")

            if (result.success) {
                if (!result.token.isNullOrEmpty()) {
                    saveToken(result.token, result.usuario_id ?: -1)
                    navigateToMain()
                } else {
                    Toast.makeText(this, "Registro exitoso. Ahora inicia sesión.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveToken(token: String, userId: Int) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("token", token)
            putInt("usuario_id", userId)
            apply()
        }
    }

    private fun navigateToMain() {
        Log.d("LoginActivity", "Navegando a MainActivity...")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
