package com.pk4us.declarationtable.fragment

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pk4us.declarationtable.R

class SelectImageRvAdapter: RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>() {
    private  val mainArray = ArrayList<SelectImageItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectImageRvAdapter.ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_fragment_item,parent,false)
        return ImageHolder(view)
    }

    override fun onBindViewHolder(holder: SelectImageRvAdapter.ImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    class ImageHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        lateinit var tvTitle:TextView
        lateinit var image:ImageView
        fun setData(item:SelectImageItem){
            tvTitle = itemView.findViewById(R.id.tvTitle)
            image = itemView.findViewById(R.id.imageContent)
            tvTitle.text = item.title
            image.setImageURI(Uri.parse(item.imageUri))
        }
    }

    fun updateAdapter(newList: List<SelectImageItem>){
        mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }
}