package com.pk4us.declarationtable

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pk4us.declarationtable.accountHelper.AccountHelper
import com.pk4us.declarationtable.act.DescriptionActivity
import com.pk4us.declarationtable.act.EditAdsAct
import com.pk4us.declarationtable.adapters.AdsRcAdapter
import com.pk4us.declarationtable.databinding.ActivityEditAdsBinding
import com.pk4us.declarationtable.databinding.ActivityMainBinding
import com.pk4us.declarationtable.dialoghelper.DialogConst
import com.pk4us.declarationtable.dialoghelper.DialogHelper
import com.pk4us.declarationtable.dialoghelper.GoogleAccConst
import com.pk4us.declarationtable.model.Ad
import com.pk4us.declarationtable.viewModel.FirebaseViewModel
import com.squareup.picasso.Picasso

class   MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener,AdsRcAdapter.Listener{

    private lateinit var tvAccount: TextView
    private lateinit var imAccount: ImageView
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val  myAuth = Firebase.auth
    val adapter = AdsRcAdapter(this)
    lateinit var googleSignInLauncher:ActivityResultLauncher<Intent>
    private val firebaseViewModel :FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        initRecyclerView()
        init()
        initViewModel()
        firebaseViewModel.loadAllAds()
        bottomMenuOnClick()
        scrollListener()
    }

    private fun onActivityResult() {
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    Log.d("MyLog", "Api 0")
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                } else { Log.d("MyLog", "Token id NULL") }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error : ${e.message}")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(myAuth.currentUser)
    }

    private fun initViewModel(){
        firebaseViewModel.liveAdsData.observe(this,{ adapter.updateAdapter(it)
            binding.mainContent.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    private fun init(){
        setSupportActionBar(binding.mainContent.toolbar)
        onActivityResult()
        navViewSettings()
        var toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.mainContent.toolbar,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
        imAccount = binding.navView.getHeaderView(0).findViewById(R.id.imAccountImage)
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
                    firebaseViewModel.loadMyFavs()
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
                if (myAuth.currentUser?.isAnonymous == true ){
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return true}
                uiUpdate(null)
                myAuth.signOut()
                dialogHelper.accHelper.signOutGoogle()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user:FirebaseUser?){
        if (user==null){
            dialogHelper.accHelper.signInAnonymously(object:AccountHelper.Listener{
                override fun onComplete() {
                    tvAccount.text = "Гость"
                    imAccount.setImageResource(R.drawable.ic_account_def)
                }
            })
        }else if (user.isAnonymous){
            tvAccount.text = "Гость"
            imAccount.setImageResource(R.drawable.ic_account_def)
        }else if (!user.isAnonymous){
            tvAccount.text = user.email
            Picasso.get().load(user.photoUrl).into(imAccount)
        }
    }

    override fun onDeleteItem(ad: Ad) {
        firebaseViewModel.deleteItem(ad)
    }

    override fun onAdViewed(ad: Ad) {
        firebaseViewModel.adViewed(ad)
        val i = Intent(this, DescriptionActivity::class.java)
        i.putExtra("AD", ad)
        startActivity(i)
    }

    override fun onFavClicked(ad: Ad) {
        firebaseViewModel.onFavClick(ad)
    }

    private fun navViewSettings() = with(binding){
        val menu = navView.menu
        val adsCat = menu.findItem(R.id.adsCat)
        val spanAdsCat = SpannableString(adsCat.title)
        spanAdsCat.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.green_main)),0,adsCat.title.length,0)
        adsCat.title = spanAdsCat

        val accCat = menu.findItem(R.id.accCat)
        val spanAccCat = SpannableString(accCat.title)
        spanAccCat.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.green_main)),0,accCat.title.length,0)
        accCat.title = spanAccCat
    }

    private fun scrollListener() = with(binding.mainContent){
        rcView.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recView, newState)
                if (!recView.canScrollVertically(SCROLL_DOWN) && newState == RecyclerView.SCROLL_STATE_IDLE){
                    Log.d("MyLog","Can scroll down")
                }
            }
        })
    }

    companion object{
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        const val SCROLL_DOWN = -1
    }
}