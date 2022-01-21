package com.projects.wallpaperkotlin

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.projects.wallpaperkotlin.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationView: NavigationView
    private lateinit var blurView: BlurView
    private lateinit var drawer: DrawerLayout
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawer = binding.drawerLayout
        setSupportActionBar(binding.appBarMain.toolbar)
        navigationView = binding.navView
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_popular,
                R.id.nav_random,
                R.id.nav_liked,
                R.id.nav_history,
                R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.appBarMain.contentMain.dotHome.visibility = View.VISIBLE


        binding.appBarMain.contentMain.homeIcon.setOnClickListener { v ->
            Navigation.findNavController(
                this,
                R.id.nav_host_fragment_content_main
            ).navigate(R.id.nav_home)
            setVisibility(0)
        }
        binding.appBarMain.contentMain.popularIcon.setOnClickListener { v ->
            Navigation.findNavController(
                this,
                R.id.nav_host_fragment_content_main
            ).navigate(R.id.nav_popular)
            setVisibility(1)
        }
        binding.appBarMain.contentMain.randomIcon.setOnClickListener { v ->
            Navigation.findNavController(
                this,
                R.id.nav_host_fragment_content_main
            ).navigate(R.id.nav_random)
            setVisibility(2)
        }
        binding.appBarMain.contentMain.likedIcon.setOnClickListener { v ->
            Navigation.findNavController(
                this,
                R.id.nav_host_fragment_content_main
            ).navigate(R.id.nav_liked)
            setVisibility(3)
        }
        binding.appBarMain.contentMain.appbarlayout.outlineProvider = null


        val radius = 10f

        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background
        blurView = findViewById(R.id.blurView)
        blurView.setupWith(rootView)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(radius)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(true)

        navigationView.itemIconTintList = null

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> setVisibility(0)
                R.id.nav_popular -> setVisibility(1)
                R.id.nav_random -> setVisibility(2)
                R.id.nav_liked -> setVisibility(3)
            }
            NavigationUI.onNavDestinationSelected(item, navController)
            drawer.closeDrawer(GravityCompat.START)
            true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main1, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun setVisibility(count: Int) {
        binding.appBarMain.contentMain.dotHome.visibility = View.GONE
        binding.appBarMain.contentMain.dotPopular.visibility = View.GONE
        binding.appBarMain.contentMain.dotRandom.visibility = View.GONE
        binding.appBarMain.contentMain.dotLiked.visibility = View.GONE
        when (count) {
            0 -> binding.appBarMain.contentMain.dotHome.visibility = View.VISIBLE
            1 -> binding.appBarMain.contentMain.dotPopular.visibility = View.VISIBLE
            2 -> binding.appBarMain.contentMain.dotRandom.visibility = View.VISIBLE
            3 -> binding.appBarMain.contentMain.dotLiked.visibility = View.VISIBLE
        }
    }


    fun hideBlurView() {
        blurView.visibility = View.GONE
        supportActionBar!!.hide()
    }


    fun showBlurView() {
        blurView.visibility = View.VISIBLE
        supportActionBar!!.show()
    }


    fun hideOnlyBlurView() {
        blurView.visibility = View.GONE
    }


    fun showOnlyBlurView() {
        blurView.visibility = View.VISIBLE
    }


    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed();
        }
    }
}