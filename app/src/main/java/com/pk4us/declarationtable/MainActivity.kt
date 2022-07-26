package com.pk4us.declarationtable

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pk4us.declarationtable.act.EditAdsAct
import com.pk4us.declarationtable.adapters.AdsRcAdapter
import com.pk4us.declarationtable.databinding.ActivityMainBinding
import com.pk4us.declarationtable.dialoghelper.DialogConst
import com.pk4us.declarationtable.dialoghelper.DialogHelper
import com.pk4us.declarationtable.dialoghelper.GoogleAccConst
import com.pk4us.declarationtable.viewModel.FirebaseViewModel

class   MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener{

    private lateinit var tvAccount: TextView
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val  myAuth = Firebase.auth
    val adapter = AdsRcAdapter(myAuth)
    private val firebaseViewModel :FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        initRecyclerView()
        init()
        initViewModel()
        firebaseViewModel.loadAllAds()
        bottomMenuOnClick()
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

    private fun initViewModel(){
        firebaseViewModel.liveAdsData.observe(this,{ adapter.updateAdapter(it) })
    }

    private fun init(){
        setSupportActionBar(binding.mainContent.toolbar)
        var toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.mainContent.toolbar,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.bNavView.selectedItemId = R.id.id_home
    }

    private fun bottomMenuOnClick() = with(binding){
        mainContent.bNavView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.id_home ->{
                    firebaseViewModel.loadAllAds()
                    mainContent.toolbar.title = getString(R.string.def)
                }
                R.id.id_favs ->{
                    Toast.makeText(this@MainActivity,"Pressed id_favs",Toast.LENGTH_SHORT).show()
                }
                R.id.id_my_ads ->{
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.ad_my_ads)
                }
                R.id.id_new_ad ->{ val i = Intent(
                    this@MainActivity,EditAdsAct::class.java)
                    startActivity(i)}
            }
            true
        }

    }

    private fun initRecyclerView(){
        binding.apply {
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = adapter
        }
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