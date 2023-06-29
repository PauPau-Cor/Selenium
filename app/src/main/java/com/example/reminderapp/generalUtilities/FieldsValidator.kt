package com.example.reminderapp.generalUtilities

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.reminderapp.R
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.util.Date


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

    fun checkPasswords(pass: TextInputLayout, passConf: TextInputLayout, context: Context): Boolean {
        if(pass.editText?.text.toString() != passConf.editText?.text.toString()){
            pass setError context.getString(R.string.err_pass_not_match)
            return false
        }

        val passRegex = Regex("""^(?=.*\d)(?=.*[A-ZÑ])(?=.*[a-zñ]).{8,}$""")
        if(!(passRegex matches pass.editText!!.text)){
            pass setError context.getString(R.string.err_pass_not_secure)
            return false
        }
        return true
    }

    fun checkDateAfterToday(field : TextInputLayout, date: Date, context: Context): Boolean {
        try{
            val currentDate = Date()
            if(!date.before(currentDate)) {
                field setError context.getString(R.string.err_date_not_past)
                return false
            }

        } catch (e : ParseException){
            field setError context.getString(R.string.err_wrong_format)
            Log.e(TAG, "checkDateAfterToday: Parse error", e)
            return false
        }
        return true
    }

    fun checkEmail(email: TextInputLayout, context: Context): Boolean {
        val mailRegex = Regex("""^[\w-.]+@([\w-]+\.)+[\w-]{2,4}${'$'}""")
        if(!(mailRegex matches email.editText!!.text)){
            email setError context.getString(R.string.err_wrong_format)
            return false
        }
        return true
    }

    private infix fun TextInputLayout.setError(error: String){
        this.error = error
        this.isErrorEnabled = true
        this.requestFocus()
        this.editText?.addTextChangedListener(ErrorRemover(this))
    }



}