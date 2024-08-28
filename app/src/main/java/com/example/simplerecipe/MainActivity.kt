package com.example.simplerecipe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.simplerecipe.databinding.ActivityMainBinding
import com.example.simplerecipe.fragment.HomeFragment
import com.example.simplerecipe.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()

        loadFragment(HomeFragment())

        binding.bottomNavBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(homeFragment)
                    true
                }
                R.id.profile -> {
                    loadFragment(profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, fragment)
            transaction.commit()
        }
    }
}