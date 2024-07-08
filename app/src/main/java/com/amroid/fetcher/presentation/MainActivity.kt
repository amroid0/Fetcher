package com.amroid.fetcher.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.amroid.fetcher.R
import com.amroid.fetcher.presentation.cacheList.CacheListViewModel
import com.amroid.fetcher.presentation.cacheList.CacheRequestListFragment
import com.amroid.fetcher.presentation.home.HomeFragment
import com.amroid.fetcher.presentation.reposneDetail.ResponseDetailFragment

class MainActivity : AppCompatActivity() {
  private lateinit var viewModel:SharedViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    viewModel = ViewModelProvider(this, SharedViewModel.Factory)[SharedViewModel::class.java]
    val cacheButton = findViewById<ImageView>(R.id.cache_button)
    if (savedInstanceState == null) {
      replaceFragment(HomeFragment())
    }
    cacheButton.setOnClickListener {
      replaceFragment(CacheRequestListFragment())
    }
    viewModel.responseDetailState().observe(this) {
      replaceFragment(ResponseDetailFragment())
    }
  }

  private fun replaceFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null)
      .commit()

  }
  override fun onBackPressed() {
    val fragmentManager = supportFragmentManager
    if (fragmentManager.backStackEntryCount <= 1) {
      finish()
    }
    super.onBackPressed()
  }

}