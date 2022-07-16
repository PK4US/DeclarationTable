package com.pk4us.declarationtable.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.act.EditAdsAct

class RcViewDialogSpinnerAdapter(var context:Context, var dialog: AlertDialog) : RecyclerView.Adapter<RcViewDialogSpinnerAdapter.SpViewHolder>() {

    val mainList = ArrayList<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sp_list_item,parent,false)
        return SpViewHolder(view,context,dialog)
    }

    override fun onBindViewHolder(holder: SpViewHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    class SpViewHolder(itemView: View, var context: Context,var dialog: AlertDialog) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        private var itemText = ""
        fun setData(text:String){
            val tvSpItem = itemView.findViewById<TextView>(R.id.tvSpItem)
            tvSpItem.text = text
            itemText = text
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            (context as EditAdsAct).binding.tvCounty.text = itemText
            dialog.dismiss()
        }
    }

    fun updateAdapter(list:ArrayList<String>){
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
}