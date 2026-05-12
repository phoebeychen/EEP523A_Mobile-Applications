package com.example.swapsense

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController // Navigation组件
import androidx.navigation.ui.AppBarConfiguration // Navigation组件
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController // Navigation组件
import com.example.swapsense.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 绑定布局
        // 绑定布局
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 获取底部导航栏
        val navView: BottomNavigationView = binding.navView


// 正确方式：从 NavHostFragment 里获取 NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // 告诉 AppBar 哪些是顶级页面（不显示返回按钮）
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_draw,
                R.id.navigation_camera
            )
        )

        // 把 AppBar 和底部导航栏都连接到 NavController
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
