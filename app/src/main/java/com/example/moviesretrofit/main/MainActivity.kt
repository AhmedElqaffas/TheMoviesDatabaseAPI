package com.example.moviesretrofit.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.moviesretrofit.*
import com.example.moviesretrofit.main.fragments.FindMultiMediaFragment
import com.example.moviesretrofit.main.fragments.MoviesFragment
import com.example.moviesretrofit.main.fragments.SeriesFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setViewPagerAdapter()
        linkViewPagerAndBottomNavigation()
    }


    private fun setViewPagerAdapter() {
        val fragmentsList = listOf(
            MoviesFragment(),
            SeriesFragment(),
            FindMultiMediaFragment() as Fragment)
        viewPager.adapter = ViewPagerAdapter(
            fragmentsList,
            supportFragmentManager,
            lifecycle
        )
    }

    private fun linkViewPagerAndBottomNavigation() {
        setViewPagerChangeListener()
        setupBottomNavigationChangeListener()
    }

    private fun setViewPagerChangeListener() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun setupBottomNavigationChangeListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.movies ->
                    viewPager.setCurrentItem(0, true)
                R.id.series ->
                    viewPager.setCurrentItem(1, true)
                R.id.search ->
                    viewPager.setCurrentItem(2,true)
            }
            true
        }
    }
}