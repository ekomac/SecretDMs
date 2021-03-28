package com.ekosoftware.secretdms.app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.ekosoftware.secretdms.*
import com.ekosoftware.secretdms.data.remote.FirebaseService
import com.ekosoftware.secretdms.databinding.ActivityMainBinding
import com.ekosoftware.secretdms.presentation.AuthenticationViewModel
import com.ekosoftware.secretdms.presentation.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

const val TOPIC = "myTopic"
private const val CHANNEL_ID = "my_channel"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel by viewModels<MainViewModel>()

    private val authViewModel by viewModels<AuthenticationViewModel>()

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //viewModel.insertDummyData()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            invalidateOptionsMenu()
        }
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            FirebaseService.token = token
            //binding.tokenEditText.setText(token)

            Log.d(TAG, token!!)
        })

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)


        /*authViewModel.isUserAuthenticated().observe(this) { authState ->
            Log.d(TAG, "fetchAuthenticationData (Main): ${authState.javaClass}")
            when (authState) {
                is AuthState.Authenticated -> {
                    loginSuccessful()
                    authViewModel.performUsernameCheck()
                }
                is AuthState.ValidSession -> navController.navigate(R.id.action_global_homeFragment)
                else -> {
                }
            }
        }*/


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_account -> {
                navController.navigate(R.id.action_global_accountFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var showInfo = false

    fun loginSuccessful() {
        showInfo = true
        invalidateOptionsMenu()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menu_item_account)?.isVisible = showInfo && navController.currentDestination?.id == R.id.homeFragment
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }
}