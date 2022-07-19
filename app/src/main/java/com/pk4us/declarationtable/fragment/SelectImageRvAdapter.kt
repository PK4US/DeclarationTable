package com.pk4us.declarationtable.fragment

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.utils.ItemTouchMoveCallback

class SelectImageRvAdapter: RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(),ItemTouchMoveCallback.ItemTouchAdapter{
    val mainArray = ArrayList<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectImageRvAdapter.ImageHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_fragment_item,parent,false)
        return ImageHolder(view,parent.context)
    }

    override fun onBindViewHolder(holder: SelectImageRvAdapter.ImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun getItemCount(): Int {
        return mainArray.size
    }

    override fun onMove(startPos: Int, targetPos: Int) {
       val targetItem = mainArray[targetPos]
        mainArray[targetPos] = mainArray [startPos]
        mainArray[startPos] = targetItem
        notifyItemMoved(startPos,targetPos)

    }

    override fun onClear() {
        notifyDataSetChanged()
    }

    class ImageHolder(itemView:View,val context: Context): RecyclerView.ViewHolder(itemView) {
        lateinit var tvTitle:TextView
        lateinit var image:ImageView
        fun setData(item:String){
            tvTitle = itemView.findViewById(R.id.tvTitle)
            image = itemView.findViewById(R.id.imageContent)
            tvTitle.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            image.setImageURI(Uri.parse(item))
        }
    }

    fun updateAdapter(newList: List<String>,needClear:Boolean){
        if (needClear)mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }
}