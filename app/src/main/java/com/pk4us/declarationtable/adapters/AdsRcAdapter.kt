package com.pk4us.declarationtable.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pk4us.declarationtable.MainActivity
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.act.DescriptionActivity
import com.pk4us.declarationtable.act.EditAdsAct
import com.pk4us.declarationtable.model.Ad
import com.pk4us.declarationtable.databinding.AddListItemBinding
import com.squareup.picasso.Picasso

class AdsRcAdapter(val act:MainActivity):RecyclerView.Adapter<AdsRcAdapter.AdHolder>() {
    val adArray = ArrayList<Ad>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AddListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdHolder(binding,act)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adArray[position])
    }

    override fun getItemCount(): Int {
        return adArray.size
    }

    fun updateAdapter(newList:List<Ad>){
        val difResult = DiffUtil.calculateDiff(DiffUtilHelper(adArray,newList))
        difResult.dispatchUpdatesTo(this)
        adArray.clear()
        adArray.addAll(newList)
    }

    class AdHolder(val binding: AddListItemBinding, val act: MainActivity) : RecyclerView.ViewHolder(binding.root) {
        fun setData(ad: Ad) = with(binding) {
            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvTitle.text = ad.title
            tvViewCounter.text = ad.viewsCounter
            tvFavCounter.text = ad.favCounter
            Picasso.get().load(ad.mainImage).into(mainImage)

            isFav(ad)
            showEditPanel(isOwner(ad))
            mainOnClick(ad)
        }

        private fun mainOnClick(ad: Ad) = with (binding){
            ibFav.setOnClickListener {
                if (act.myAuth.currentUser?.isAnonymous == false) act.onFavClicked(ad)
            }
            itemView.setOnClickListener{
                act.onAdViewed(ad)
            }
            ibEditAd.setOnClickListener(onClickEdit(ad))
            ibDeleteAd.setOnClickListener{act.onDeleteItem(ad)}
            itemView.setOnClickListener {
                val i = Intent(binding.root.context,DescriptionActivity::class.java)
                i.putExtra("AD",ad)
                binding.root.context.startActivity(i)
            }
        }

        private fun isFav(ad: Ad){
            if (ad.isFav){
                binding.ibFav.setImageResource(R.drawable.ic_fav_pressed)
            }else binding.ibFav.setImageResource(R.drawable.ic_fav_norm)
        }

        private fun onClickEdit(ad: Ad): View.OnClickListener {
            return View.OnClickListener {
                val editIntent = Intent(act,EditAdsAct::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE,true)
                    putExtra(MainActivity.ADS_DATA,ad)
                }
                act.startActivity(editIntent)
            }
        }

        private fun isOwner(ad: Ad):Boolean{
            return ad.uid == act.myAuth.uid
        }


        private fun showEditPanel(isOwner:Boolean){
            if (isOwner){
                binding.editPanel.visibility = View.VISIBLE
            }else{
                binding.editPanel.visibility = View.GONE
            }
        }
    }
    interface Listener{
        fun onDeleteItem(ad: Ad)
        fun onAdViewed(ad: Ad)
        fun onFavClicked(ad: Ad)
    }
}