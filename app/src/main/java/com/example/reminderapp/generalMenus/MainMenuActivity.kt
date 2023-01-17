package com.example.reminderapp.generalMenus

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.reminderapp.R
import com.example.reminderapp.authMenus.LoginActivity
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.ActivityMainMenuBinding
import com.example.reminderapp.models.UserModel
import com.google.firebase.auth.FirebaseAuth

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var mAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        val userModel : UserModel? = intent.getParcelableExtra<UserModel>(Constants.PutExUser)

        drawerInit()
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
        binding.NavigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_logout -> {
                    signOut()
                    return@setNavigationItemSelectedListener false
                }
            }
            true
        }
    }

    private fun signOut() {
        mAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}