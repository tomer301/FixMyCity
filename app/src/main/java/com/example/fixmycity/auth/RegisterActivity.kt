package com.example.fixmycity.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.fixmycity.databinding.ActivityRegisterBinding
import com.example.fixmycity.utils.AuthValidator
import com.example.fixmycity.utils.navigateToMain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSubmitRegister.setOnClickListener {
            performRegistration()
        }

        binding.tvBackToLogin.setOnClickListener {
            finish() //going back to Login page (last in stack)
        }
    }

    private fun performRegistration() {
        val email = binding.etRegisterEmail.text.toString().trim()
        val password = binding.etRegisterPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        val emailError = AuthValidator.validateEmail(email)
        val passwordError = AuthValidator.validatePassword(password)
        val confirmError = AuthValidator.validatePasswordConfirm(password,confirmPassword)

        binding.tilRegisterEmail.error = emailError
        binding.tilRegisterPassword.error = passwordError
        binding.tilConfirmPassword.error = confirmError

        if (emailError != null || passwordError != null || confirmError != null) return

        binding.btnSubmitRegister.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.btnSubmitRegister.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(this, "הרשמה בוצעה בהצלחה!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        binding.tilRegisterEmail.error = "כתובת הדואר האלקטרוני כבר רשומה במערכת"
                    } else {
                        Toast.makeText(this, "שגיאה בהרשמה: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }
}