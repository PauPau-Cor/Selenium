package com.example.reminderapp.generalMenus

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.reminderapp.R
import com.example.reminderapp.authMenus.LoginActivity
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.ActivityMainMenuBinding
import com.example.reminderapp.models.UserModel
import com.example.reminderapp.toDoMenus.AllToDoFragment
import com.example.reminderapp.toDoMenus.DoneTasksFragment
import com.google.firebase.auth.FirebaseAuth

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var mAuth : FirebaseAuth
    private lateinit var userModel : UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        //Using deprecated because newer solution requires API33.
        @Suppress("DEPRECATION")
        userModel = intent.getParcelableExtra(Constants.PutExUser)!!
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

        binding.NavigationView.setCheckedItem(R.id.nav_feed)
        showFragment(HomeFragment.newInstance(userModel))

        binding.NavigationView.setNavigationItemSelectedListener {
            binding.DrawerLayout.closeDrawers()
            binding.DrawerLayout.postDelayed({
                when(it.itemId){
                    R.id.nav_feed -> showFragment(HomeFragment.newInstance(userModel))
                    R.id.nav_tasks -> showFragment(AllToDoFragment.newInstance(userModel))
                    R.id.nav_done_tasks -> showFragment(DoneTasksFragment.newInstance(userModel))
                    R.id.nav_logout -> {
                        signOut()
                    }
                }
            }, 240)
            true
        }
    }

    private fun showFragment(fragmentToShow : Fragment){
        val fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FragContainer.id, fragmentToShow)
        fragmentTransaction.commit()
    }

    private fun signOut() {
        mAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}