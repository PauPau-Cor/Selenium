package com.example.reminderapp.generalUtilities

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.reminderapp.R
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class FieldsValidator {

    fun checkIfNotEmpty(fields : Array<TextInputLayout>, context : Context): Boolean {
        fields.forEach {
            if(it.editText?.text.isNullOrBlank()){
                it setError context.getString(R.string.err_empty_field)
                return false
            }
        }
        return true
    }

    fun checkPasswords(Pass: TextInputLayout, PassConf: TextInputLayout, context: Context): Boolean {
        if(Pass.editText?.text.toString() != PassConf.editText?.text.toString()){
            Pass setError context.getString(R.string.err_pass_not_match)
            return false
        }

        val passRegex = Regex("""^(?=.*\d)(?=.*[A-ZÑ])(?=.*[a-zñ]).{8,}$""")
        if(!(passRegex matches Pass.editText!!.text)){
            Pass setError context.getString(R.string.err_pass_not_secure)
            return false
        }
        return true
    }

    fun checkDateAfterToday(dateIn : TextInputLayout, context: Context): Boolean {
        try{
            val dateConverted : Date = SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateIn.editText?.text.toString())!!
            val currentDate = Date()

            if(!dateConverted.before(currentDate)) {
                dateIn setError context.getString(R.string.err_date_not_past)
                return false
            }

        } catch (e : ParseException){
            dateIn setError context.getString(R.string.err_wrong_format)
            Log.e(TAG, "checkDateAfterToday: Parse error", e)
            return false
        }
        return true
    }

    fun checkEmail(Email: TextInputLayout, context: Context): Boolean {
        val mailRegex = Regex("""^[\w-.]+@([\w-]+\.)+[\w-]{2,4}${'$'}""")
        if(!(mailRegex matches Email.editText!!.text)){
            Email setError context.getString(R.string.err_wrong_format)
            return false
        }
        return true
    }

    private infix fun TextInputLayout.setError(Error: String){
        this.error = Error
        this.isErrorEnabled = true
        this.requestFocus()
        this.editText?.addTextChangedListener(ErrorRemover(this))
    }



}