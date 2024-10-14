package com.example.walkypet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.walkypet.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.name
    private lateinit var binding: ActivityMainBinding
    private val VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)
    private lateinit var mAuth: FirebaseAuth
    lateinit var emailEdit: EditText
    lateinit var passEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailEdit = binding.editTextTextEmail
        passEdit = binding.editTextTextPassword

        mAuth = Firebase.auth

        // Configurar el botón de login
        binding.button.setOnClickListener {
            Toast.makeText(this, "Iniciar sesión", Toast.LENGTH_SHORT).show()
            login()

        }

        // Configurar el botón de Facebook
        binding.facebook.setOnClickListener {
            Toast.makeText(this, "Iniciar sesión con Facebook", Toast.LENGTH_SHORT).show()
            // Lógica para login con Facebook
            //loginFacebook()
        }

        // Configurar el botón de Google
        binding.google.setOnClickListener {
            Toast.makeText(this, "Iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            // Lógica para login con Google
            //loginGoogle()
        }

        // Configurar el botón de "Regístrate aquí"
        binding.textView3.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
        binding.textViewPaseadorOnboarding.setOnClickListener {
            val intent = Intent(this, PaseadorOnboarding::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }


    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(baseContext, MainActivity3::class.java)
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

    private fun signInUser(email: String, password: String) {
        if (validateForm()) {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI
                        Log.d(TAG, "signInWithEmail: Success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If Sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail: Failure", task.exception)
                        Toast.makeText(this@MainActivity, "Autenticación fallida.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun isEmailValid(emailStr: String?): Boolean {
        val matcher: Matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr)
        return matcher.find()
    }


    private fun login() {
        val email = emailEdit.text.toString()
        val pass = passEdit.text.toString()
        if (!isEmailValid(email)) {
            Toast.makeText(this@MainActivity, "El correo no tiene un formato válido", Toast.LENGTH_SHORT).show()
            return
        }
        signInUser(email, pass)
    }
}
