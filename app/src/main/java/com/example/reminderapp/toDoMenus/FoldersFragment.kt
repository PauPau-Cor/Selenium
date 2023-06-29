package com.example.reminderapp.toDoMenus

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reminderapp.adapters.FoldersAdapter
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.FragmentFoldersBinding
import com.example.reminderapp.generalUtilities.DialogMaker
import com.example.reminderapp.generalUtilities.WrappedGridLayoutManager
import com.example.reminderapp.models.CategoryModel
import com.example.reminderapp.models.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FoldersFragment : Fragment() {
    private lateinit var binding : FragmentFoldersBinding
    private lateinit var userModel : UserModel
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: FoldersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            userModel = it.getParcelable(Constants.PutExUser)!!
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).setDuration(650)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).setDuration(650)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
        binding = FragmentFoldersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRV()
        setupAddFolderBT()
        setupDefaultFolderBT()
    }

    private fun setupDefaultFolderBT() {
        binding.defaultFolder.setOnClickListener {
            startActivity(Intent(context, FolderTasksActivity::class.java).putExtra(Constants.PutExUser, userModel))
        }
    }

    private fun setupAddFolderBT() {
        val dialogMaker = DialogMaker()
        binding.addFolder.setOnClickListener{
            if(isAdded){
                dialogMaker.addFolder(fragmentManager = parentFragmentManager, binding.root, db, userModel.userID!!)
            }
        }
    }

    private fun setupRV() {
        val query: Query = db.collection(Constants.CategoriesCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .orderBy(Constants.lastEditedField, Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<CategoryModel>()
            .setQuery(query, CategoryModel::class.java).setLifecycleOwner(this).build()
        adapter = FoldersAdapter(options, userModel, requireContext(), childFragmentManager, binding.root)
        binding.FoldersList.adapter = adapter
        binding.FoldersList.layoutManager = WrappedGridLayoutManager(requireContext(), 2)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        @JvmStatic
        fun newInstance(userModel:  UserModel) =
            FoldersFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.PutExUser, userModel)
                }
            }
    }
}