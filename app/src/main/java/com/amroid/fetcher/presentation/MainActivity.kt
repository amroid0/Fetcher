package com.amroid.fetcher.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amroid.fetcher.R
import com.amroid.fetcher.presentation.home.HomeFragment

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val home = HomeFragment()
    supportFragmentManager.beginTransaction().add(R.id.container, home).commit()
  }
}