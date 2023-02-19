package com.bawano.semester

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Menu
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bawano.semester.databinding.ActivityMainBinding
import com.bawano.semester.models.LastPage
import com.bawano.semester.ui.home.HomeFragmentDirections
import com.bawano.semester.utils.Constants
import com.bawano.semester.utils.PreferenceManager
import com.bawano.semester.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), Utils.FragmentPage {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var b: ActivityMainBinding
    private var lastPage: LastPage = LastPage()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = b.drawerLayout
        val navView: NavigationView = b.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        lifecycleScope.launch {
            PreferenceManager(this@MainActivity).lastPage.collect {
                navGraph.setStartDestination(
                    when (it.name) {
                        Constants.COURSES -> R.id.nav_courses
                        Constants.PDFVIEW -> R.id.pdfViewFragment
                        Constants.DETAILS -> R.id.detailsFragment
                        else -> R.id.nav_courses

                    }
                )
                lastPage = if (it.name.isEmpty()) LastPage()
                else it
                it.navState?.let { bundle ->
                    navController.restoreState(bundle)
                }
                navController.graph = navGraph
                appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.nav_home, R.id.nav_courses, R.id.nav_course_units
                    ), drawerLayout
                )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)
                b.appBarMain.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val unMetered =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        }

    }

    override fun setLastPage(page: LastPage) {
        lastPage = page
    }

    override fun onPause() {
        super.onPause()
        navController.saveState()?.let {
            lastPage.navState = it
        }
        lifecycleScope.launch(Dispatchers.IO) {
            PreferenceManager(this@MainActivity).putLastPage(lastPage)
        }
    }
}