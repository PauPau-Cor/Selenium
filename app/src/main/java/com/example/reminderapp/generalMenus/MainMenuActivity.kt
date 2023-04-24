package com.example.reminderapp.generalMenus

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.reminderapp.R
import com.example.reminderapp.authMenus.LoginActivity
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.ActivityMainMenuBinding
import com.example.reminderapp.models.UserModel
import com.example.reminderapp.toDoMenus.AllToDoFragment
import com.example.reminderapp.toDoMenus.DoneTasksFragment
import com.example.reminderapp.toDoMenus.FoldersFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var mAuth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var userModel : UserModel
    private lateinit var token : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //Using deprecated because newer solution requires API33.
        @Suppress("DEPRECATION")
        userModel = intent.getParcelableExtra(Constants.PutExUser)!!

        drawerInit()
        notifChannelsInit()
        getToken()
    }

    private fun notifChannelsInit() {

    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            this.token = token
            if(userModel.token.contains(token)){
                return@addOnSuccessListener
            }
            val batch = db.batch()
            val userRef = db.collection(Constants.UsersCollection).document(userModel.userID!!)
            batch.update(userRef, Constants.tokenField, FieldValue.arrayUnion(token))
            db.collection(Constants.TasksCollection).whereEqualTo(Constants.userIDField, userModel.userID).get().addOnSuccessListener { querySnapshot ->
                querySnapshot.forEach{
                    batch.update(it.reference, Constants.tokenField, FieldValue.arrayUnion(token))
                    Toast.makeText(this, "waaa", Toast.LENGTH_SHORT).show()
                }
                batch.commit()
            }
        }
    }

    override fun onResume() {
        binding.NavigationView.checkedItem?.let { drawerOptions(it) }
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun drawerInit() {
        setSupportActionBar(binding.Toolbar)
        toggle = ActionBarDrawerToggle(this, binding.DrawerLayout, R.string.openMn, R.string.closeMn)
        binding.DrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.NavigationView.setCheckedItem(R.id.nav_feed)
        showFragment(HomeFragment.newInstance(userModel))

        binding.NavigationView.setNavigationItemSelectedListener {
            binding.DrawerLayout.closeDrawers()
            binding.DrawerLayout.postDelayed({
                drawerOptions(it)
            }, 240)
            true
        }
    }

    private fun drawerOptions(item: MenuItem){
        when(item.itemId){
            R.id.nav_feed -> showFragment(HomeFragment.newInstance(userModel))
            R.id.nav_tasks -> showFragment(AllToDoFragment.newInstance(userModel))
            R.id.nav_done_tasks -> showFragment(DoneTasksFragment.newInstance(userModel))
            R.id.nav_folders -> showFragment(FoldersFragment.newInstance(userModel))
            R.id.nav_logout -> {
                signOut()
            }
        }
    }

    private fun showFragment(fragmentToShow : Fragment){
        val fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragContainer.id, fragmentToShow)
        fragmentTransaction.commit()
    }

    private fun signOut() {
        val batch = db.batch()
        val userRef = db.collection(Constants.UsersCollection).document(userModel.userID!!)
        batch.update(userRef, Constants.tokenField, FieldValue.arrayRemove(token))
        db.collection(Constants.TasksCollection).whereEqualTo(Constants.userIDField, userModel.userID).get().addOnSuccessListener { querySnapshot ->
            querySnapshot.forEach{
                batch.update(it.reference, Constants.tokenField, FieldValue.arrayRemove(token))
            }
            batch.commit().addOnSuccessListener {
                mAuth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}