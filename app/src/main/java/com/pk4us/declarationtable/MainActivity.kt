package com.pk4us.declarationtable

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.pk4us.declarationtable.act.EditAdsAct
import com.pk4us.declarationtable.databinding.ActivityMainBinding
import com.pk4us.declarationtable.dialoghelper.DialogConst
import com.pk4us.declarationtable.dialoghelper.DialogHelper
import com.pk4us.declarationtable.dialoghelper.GoogleAccConst

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener{

    private lateinit var tvAccount: TextView
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val  myAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_new_ads){
            val i = Intent(this,EditAdsAct::class.java)
            startActivity(i)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE){
//            Log.d("MyLog","Sign in result")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null){
                    Log.d("MyLog","Api 0")

                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }else{
                    Log.d("MyLog","Token id NULL")
                }
            }catch (e:ApiException){
                Log.d("MyLog","Api error : ${e.message}" )
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(myAuth.currentUser)
    }

    private fun init(){
        setSupportActionBar(binding.mainContent.toolbar)
        var toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.mainContent.toolbar,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
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
               dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            }
            R.id.id_sign_in ->{
                dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            }
            R.id.id_sign_out ->{
                uiUpdate(null)
                myAuth.signOut()
                dialogHelper.accHelper.signOutGoogle()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user:FirebaseUser?){
        tvAccount.text = if (user==null){
            resources.getString(R.string.not_reg)
        }else{
            user.email
        }
    }
}