package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityPaseadorSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern


class PaseadorSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaseadorSignUpBinding
    private val VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
    lateinit var userEdit: EditText
    lateinit var emailEdit: EditText
    lateinit var passEdit: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaseadorSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailEdit = binding.editTextEmail
        passEdit = binding.editTextPassword

        mAuth = Firebase.auth

        binding.buttonSignUp.setOnClickListener {
            signUp()
        }

    }
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(baseContext, PaseadorMainActivity::class.java)
            intent.putExtra("user", currentUser.email)
            startActivity(intent)
        } else {
            emailEdit.setText("")
            passEdit.setText("")
        }
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email = emailEdit.text.toString()
        if (TextUtils.isEmpty(email)) {
            emailEdit.error = "Required"
            valid = false
        } else {
            emailEdit.error = null
        }
        val password = passEdit.text.toString()
        if (TextUtils.isEmpty(password)) {
            passEdit.error = "Required"
            valid = false
        } else {
            passEdit.error = null
        }
        return valid
    }

    private fun isEmailValid(emailStr: String?): Boolean {
        val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
        return matcher.find()
    }

    private fun signUp() {
        val email = emailEdit.text.toString()
        val pass = passEdit.text.toString()
        if (!isEmailValid(email)) {
            Toast.makeText(this@PaseadorSignUpActivity, "Correo en formato no válido", Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                Toast.makeText(
                    this@PaseadorSignUpActivity,
                    String.format("Registro exitoso", user!!.email),
                    Toast.LENGTH_LONG
                ).show()
            }
        }.addOnFailureListener(this) { e ->
            Toast.makeText(this@PaseadorSignUpActivity, e.message, Toast.LENGTH_LONG).show() }
    }
}
