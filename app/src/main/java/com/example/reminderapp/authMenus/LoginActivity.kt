package com.example.reminderapp.authMenus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.reminderapp.R
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.ActivityLoginBinding
import com.example.reminderapp.generalMenus.MainMenuActivity
import com.example.reminderapp.generalUtilities.ErrorRemover
import com.example.reminderapp.generalUtilities.FieldsValidator
import com.example.reminderapp.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.LogLogin.setOnClickListener {
            validateFields()
        }

        binding.LogReg.setOnClickListener{
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.LogFrgtPass.setOnClickListener{
            startActivity(Intent(this, ForgotPassActivity::class.java))
        }
    }

    private fun validateFields() {
        val validator = FieldsValidator()

        if(!validator.checkIfNotEmpty(arrayOf(binding.LogMail, binding.LogPass), this)){
            return
        }
        if(!validator.checkEmail(binding.LogMail, this)){
            return
        }

        userLogin()
    }

    private fun userLogin() {
        val view = this.currentFocus
        if (view != null) {
            val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
        binding.PB.visibility= View.VISIBLE

        mAuth.signInWithEmailAndPassword(binding.LogMail.editText!!.text.toString(), binding.LogPass.editText!!.text.toString())
        .addOnSuccessListener{
            db.collection(Constants.UsersCollection).document(mAuth.uid!!).get().addOnSuccessListener { document ->
                binding.PB.visibility= View.GONE
                val userModel : UserModel? = document.toObject()
                startActivity(Intent(this, MainMenuActivity::class.java).putExtra(Constants.PutExUser, userModel))
            }
        }.addOnFailureListener {
            binding.PB.visibility= View.GONE
            handleAuthError(it)
        }
    }

    private fun handleAuthError(error: Exception) {
        when(error){
            is FirebaseAuthInvalidUserException -> {
                binding.LogMail.error = this.getString(R.string.err_wrong_email)
                binding.LogMail.isErrorEnabled = true
                binding.LogMail.requestFocus()
                binding.LogMail.editText?.addTextChangedListener(ErrorRemover(binding.LogMail))
            }
            is FirebaseAuthInvalidCredentialsException -> {
                binding.LogPass.error = this.getString(R.string.err_wrong_pass)
                binding.LogPass.isErrorEnabled = true
                binding.LogPass.requestFocus()
                binding.LogPass.editText?.addTextChangedListener(ErrorRemover(binding.LogPass))
            }
        }
    }
}