package com.pk4us.declarationtable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init(){
        var toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.id_my_ads ->{
                Toast.makeText(this,"Pressed id_my_ads",Toast.LENGTH_SHORT).show()
            }
            R.id.id_car ->{
                Toast.makeText(this,"Pressed id_car",Toast.LENGTH_SHORT).show()
            }
            R.id.id_pc ->{
                Toast.makeText(this,"Pressed id_pc",Toast.LENGTH_SHORT).show()
            }
            R.id.id_smartphone ->{
                Toast.makeText(this,"Pressed id_smartphone",Toast.LENGTH_SHORT).show()
            }
            R.id.id_bd ->{
                Toast.makeText(this,"Pressed id_bd",Toast.LENGTH_SHORT).show()
            }
            R.id.id_sign_up ->{
                Toast.makeText(this,"Pressed id_sign_up",Toast.LENGTH_SHORT).show()
            }
            R.id.id_sign_in ->{
                Toast.makeText(this,"Pressed id_sign_in",Toast.LENGTH_SHORT).show()
            }
            R.id.id_sign_out ->{
                Toast.makeText(this,"Pressed id_sign_out",Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}