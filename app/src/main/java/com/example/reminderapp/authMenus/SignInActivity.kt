package com.example.reminderapp.authMenus

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reminderapp.R
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.ActivitySignInBinding
import com.example.reminderapp.generalMenus.MainMenuActivity
import com.example.reminderapp.generalUtilities.DialogMaker
import com.example.reminderapp.generalUtilities.ErrorRemover
import com.example.reminderapp.generalUtilities.FieldsValidator
import com.example.reminderapp.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dialogMaker = DialogMaker()
        binding.RegBirthday.editText?.setOnClickListener { dialogMaker.pickDate(binding.RegBirthday, supportFragmentManager) }

        binding.RegRegister.setOnClickListener{ validateData() }
    }

    private fun validateData() {
        val validator = FieldsValidator()

        if(!validator.checkIfNotEmpty(arrayOf(binding.RegUser, binding.RegMail, binding.RegPass, binding.RegPassConf, binding.RegBirthday), this)){
            return
        }
        if(!validator.checkPasswords(binding.RegPass, binding.RegPassConf, this)){
            return
        }
        if(!validator.checkDateAfterToday(binding.RegBirthday,this)){
            return
        }

        registerUserToAuth()
    }

    private fun registerUserToAuth() {
        val mail : String = binding.RegMail.editText?.text.toString()
        val pass : String = binding.RegPass.editText?.text.toString()
        val name : String = binding.RegUser.editText?.text.toString()
        val birth : String = binding.RegBirthday.editText?.text.toString()

        binding.PB.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(mail, pass).addOnSuccessListener {
            val newUser = UserModel(auth.uid, name, mail, birth)
            registerUserToDB(newUser)
        }.addOnFailureListener {
            binding.PB.visibility = View.GONE
            handleAuthError(it)
        }
    }

    private fun registerUserToDB(newUser: UserModel) {
        db.collection(Constants.UsersCollection).document(auth.uid!!).set(newUser).addOnSuccessListener {
            startActivity(Intent(this, MainMenuActivity::class.java).putExtra(Constants.PutExUser, newUser))
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, this.getString(R.string.err_default, it.message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleAuthError(error: Exception) {
        when(error){
            is FirebaseAuthUserCollisionException -> {
                binding.RegMail.error = this.getString(R.string.err_email_used)
                binding.RegMail.isErrorEnabled = true
                binding.RegMail.requestFocus()
                binding.RegMail.editText?.addTextChangedListener(ErrorRemover(binding.RegMail))
            }

            is FirebaseAuthWeakPasswordException -> {
                binding.RegPass.error = this.getString(R.string.err_wrong_format)
                binding.RegPass.isErrorEnabled = true
                binding.RegPass.requestFocus()
                binding.RegPass.editText?.addTextChangedListener(ErrorRemover(binding.RegPass))
            }

            is FirebaseAuthInvalidCredentialsException -> {
                binding.RegMail.error = this.getString(R.string.err_wrong_format)
                binding.RegMail.isErrorEnabled = true
                binding.RegMail.requestFocus()
                binding.RegMail.editText?.addTextChangedListener(ErrorRemover(binding.RegMail))
            }

            else -> {
                Toast.makeText(this, this.getString(R.string.err_default, error.message), Toast.LENGTH_SHORT).show()
            }

        }
    }
}