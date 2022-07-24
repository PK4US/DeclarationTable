package com.pk4us.declarationtable.fragment

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.act.EditAdsAct
import com.pk4us.declarationtable.databinding.SelectImageFragmentItemBinding
import com.pk4us.declarationtable.utils.AdapterCallback
import com.pk4us.declarationtable.utils.ImageManager
import com.pk4us.declarationtable.utils.ImagePicker
import com.pk4us.declarationtable.utils.ItemTouchMoveCallback

class SelectImageRvAdapter(val adapterCallback:AdapterCallback): RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(),ItemTouchMoveCallback.ItemTouchAdapter{
    val mainArray = ArrayList<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val viewBinding = SelectImageFragmentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageHolder(viewBinding,parent.context, this)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])
    }

    override fun onViewDetachedFromWindow(holder: ImageHolder) {
        super.onViewDetachedFromWindow(holder)
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

    class ImageHolder(private val viewBinding:SelectImageFragmentItemBinding, val context: Context, val adapter: SelectImageRvAdapter): RecyclerView.ViewHolder(viewBinding.root) {
        fun setData(bitmap:Bitmap){
            viewBinding.imEditImage.setOnClickListener {
                ImagePicker.launcher(context as EditAdsAct,context.launcherSingleSelectImage,1)
                context.editImagePosition = adapterPosition
            }

            viewBinding.imDeleteImage.setOnClickListener {
                adapter.mainArray.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
                for (n in 0 until adapter.mainArray.size) adapter.notifyItemChanged(n)
                adapter.adapterCallback.onItemDelete()
            }

            viewBinding.tvTitle.text = context.resources.getStringArray(R.array.title_array)[adapterPosition]
            ImageManager.chooseScaleType(viewBinding.imageContent,bitmap)
            viewBinding.imageContent.setImageBitmap(bitmap)
        }
    }

    fun updateAdapter(newList: List<Bitmap>,needClear:Boolean){
        if (needClear)mainArray.clear()
        mainArray.addAll(newList)
        notifyDataSetChanged()
    }
}