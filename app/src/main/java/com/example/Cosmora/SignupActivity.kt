package com.example.Cosmora

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.google.firebase.Timestamp

// Updated User data class to use Firestore Timestamp
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Timestamp = Timestamp.now()
) {
    // No-argument constructor for Firestore deserialization
    constructor() : this("", "", "", Timestamp.now())

    fun toMap(): Map<String, Any> = hashMapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "createdAt" to createdAt
    )
}

class SignupActivity : AppCompatActivity() {

    // UI Components
    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var signupButton: Button
    private lateinit var facebookButton: Button
    private lateinit var googleButton: Button
    private lateinit var alreadyHaveAccountText: TextView

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { firebaseAuthWithGoogle(it) }
        } catch (e: ApiException) {
            showToast("Google sign in failed: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initializeViews()
        initializeFirebase()
        setupClickListeners()
    }

    private fun initializeViews() {
        editTextName = findViewById(R.id.editTextText)
        editTextEmail = findViewById(R.id.editTextTextEmailAddress2)
        editTextPassword = findViewById(R.id.editTextTextPassword2)
        signupButton = findViewById(R.id.signupbutton)
        facebookButton = findViewById(R.id.button5)
        googleButton = findViewById(R.id.button6)
        alreadyHaveAccountText = findViewById(R.id.Alreadyhavebutton)
    }

    private fun initializeFirebase() {
        // Initialize Firebase services
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupClickListeners() {
        signupButton.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (validateInputs(name, email, password)) {
                registerUser(name, email, password)
            }
        }

        googleButton.setOnClickListener {
            signInWithGoogle()
        }

        // Facebook button - disabled for now
        facebookButton.setOnClickListener {
            showToast("Facebook signup coming soon")
        }

        alreadyHaveAccountText.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        // Clear previous errors
        editTextName.error = null
        editTextEmail.error = null
        editTextPassword.error = null

        var isValid = true

        if (name.isEmpty()) {
            editTextName.error = "Name is required"
            editTextName.requestFocus()
            isValid = false
        }

        if (email.isEmpty()) {
            editTextEmail.error = "Email is required"
            if (isValid) editTextEmail.requestFocus()
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Please enter a valid email"
            if (isValid) editTextEmail.requestFocus()
            isValid = false
        }

        if (password.isEmpty()) {
            editTextPassword.error = "Password is required"
            if (isValid) editTextPassword.requestFocus()
            isValid = false
        } else if (password.length < 6) {
            editTextPassword.error = "Password must be at least 6 characters"
            if (isValid) editTextPassword.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun registerUser(name: String, email: String, password: String) {
        // Disable button during registration to prevent double-tap
        signupButton.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                signupButton.isEnabled = true

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userData = User(
                            uid = user.uid,
                            name = name,
                            email = email
                        )
                        saveUserToFirestore(userData)
                    } ?: run {
                        showToast("Failed to get user information")
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
                // Don't auto-navigate, let user decide
            }
            is FirebaseAuthWeakPasswordException -> {
                editTextPassword.error = "Password is too weak"
                editTextPassword.requestFocus()
            }
            else -> {
                showToast("Registration failed: ${exception?.message ?: "Unknown error"}")
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
                    val user = auth.currentUser
                    user?.let {
                        // Check if user already exists in Firestore
                        checkAndSaveSocialUser(it.uid, it.displayName ?: "User", it.email ?: "")
                    }
                } else {
                    showToast("Google authentication failed: ${task.exception?.message}")
                }
            }
    }

    private fun checkAndSaveSocialUser(uid: String, name: String, email: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // User doesn't exist, create new user document
                    val userData = User(
                        uid = uid,
                        name = name,
                        email = email
                    )
                    saveUserToFirestore(userData)
                } else {
                    // User already exists, just navigate
                    showToast("Welcome back!")
                    navigateToMain()
                }
            }
            .addOnFailureListener {
                // If check fails, still try to save (will overwrite if exists)
                val userData = User(
                    uid = uid,
                    name = name,
                    email = email
                )
                saveUserToFirestore(userData)
            }
    }

    private fun navigateToMain() {
        val intent = Intent(this, UserActivity::class.java)  // Changed from HomeActivity to UserActivity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources if needed
    }
}