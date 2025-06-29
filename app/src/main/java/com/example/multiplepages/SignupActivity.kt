package com.example.multiplepages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.multiplepages.databinding.ActivitySignupBinding
// Removed all Facebook imports
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Date

// User data class at the top level
data class User(
    val uid: String,
    val name: String,
    val email: String,
    val createdAt: Date = Date() // Using Date instead of timestamp
) {
    fun toMap(): Map<String, Any> = hashMapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "createdAt" to createdAt
    )
}

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    // Removed callbackManager

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { firebaseAuthWithGoogle(it) }
        } catch (e: ApiException) {
            showToast("Google sign in failed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase services
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Removed Facebook initialization completely

        // Set up click listeners
        binding.signupbutton.setOnClickListener {
            val name = binding.editTextText.text.toString().trim()
            val email = binding.editTextTextEmailAddress2.text.toString().trim()
            val password = binding.editTextTextPassword2.text.toString().trim()

            if (validateInputs(name, email, password)) {
                registerUser(name, email, password)
            }
        }

        binding.button6.setOnClickListener {
            signInWithGoogle()
        }

        // Facebook button - disabled for now
        binding.button5.setOnClickListener {
            showToast("Facebook signup coming soon")
        }

        binding.Alreadyhavebutton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.editTextText.error = "Name is required"
            binding.editTextText.requestFocus()
            isValid = false
        }

        if (email.isEmpty()) {
            binding.editTextTextEmailAddress2.error = "Email is required"
            binding.editTextTextEmailAddress2.requestFocus()
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextTextEmailAddress2.error = "Please enter a valid email"
            binding.editTextTextEmailAddress2.requestFocus()
            isValid = false
        }

        if (password.isEmpty()) {
            binding.editTextTextPassword2.error = "Password is required"
            binding.editTextTextPassword2.requestFocus()
            isValid = false
        } else if (password.length < 6) {
            binding.editTextTextPassword2.error = "Password must be at least 6 characters"
            binding.editTextTextPassword2.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userData = User(
                            uid = user.uid,
                            name = name,
                            email = email
                        )
                        saveUserToFirestore(userData)
                    }
                } else {
                    handleRegistrationError(task.exception)
                }
            }
    }

    private fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.uid)
            .set(user.toMap())
            .addOnSuccessListener {
                showToast("Registration successful!")
                navigateToMain()
            }
            .addOnFailureListener { e ->
                showToast("Failed to save user data: ${e.message}")
            }
    }

    private fun handleRegistrationError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                showToast("Email already in use. Please login instead.")
                navigateToLogin()
            }
            is FirebaseAuthWeakPasswordException -> {
                binding.editTextTextPassword2.error = "Password is too weak"
                binding.editTextTextPassword2.requestFocus()
            }
            else -> {
                showToast("Registration failed: ${exception?.message}")
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveSocialUserToFirestore()
                } else {
                    showToast("Google authentication failed")
                }
            }
    }

    // Removed all Facebook-related methods:
    // - signInWithFacebook()
    // - setupFacebookLoginCallback()
    // - handleFacebookAccessToken()

    private fun saveSocialUserToFirestore() {
        val user = auth.currentUser
        user?.let {
            val userData = User(
                uid = user.uid,
                name = user.displayName ?: "User",
                email = user.email ?: ""
            )
            saveUserToFirestore(userData)
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Removed onActivityResult since we no longer need Facebook callback handling
}
