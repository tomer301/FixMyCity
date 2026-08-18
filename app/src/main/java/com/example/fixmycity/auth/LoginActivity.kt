package com.example.fixmycity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fixmycity.databinding.ActivityLoginBinding
import com.example.fixmycity.utils.AuthValidator
import com.example.fixmycity.utils.navigateToMain
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        binding.tvRegister.setOnClickListener {
            binding.etEmail.text?.clear()
            binding.etPassword.text?.clear()
            binding.tilEmail.error = null
            binding.tilPassword.error = null

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            navigateToMain()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val emailError = AuthValidator.validateEmail(email)
        val passwordError = AuthValidator.validatePassword(password)

        binding.tilEmail.error = emailError
        binding.tilPassword.error = passwordError

        if (emailError != null || passwordError != null) return

        binding.btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                binding.btnLogin.isEnabled = true
                if(task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    Toast.makeText(this, "Login error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

}