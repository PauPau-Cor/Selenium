package com.example.reminderapp.generalDialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.reminderapp.R
import com.example.reminderapp.databinding.DialogAddFolderBinding

class DialogAddFolder (val listener: (title: String) -> Unit) : DialogFragment(){

    private lateinit var binding: DialogAddFolderBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogAddFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.confirmButton.setOnClickListener {
            valTitle()
        }
    }
    private fun valTitle(){
        val title : String = binding.FolderTitle.editText?.text.toString().trim()
        binding.FolderTitle.error = null
        if(title.isBlank()){
            binding.FolderTitle.error = this.getString(R.string.err_empty_field)
            return
        }
        if(title.length > 20){
            binding.FolderTitle.error = this.getString(R.string.err_too_long)
            return
        }
        listener(title)
    }
}