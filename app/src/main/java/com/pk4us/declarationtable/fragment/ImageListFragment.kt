package com.pk4us.declarationtable.fragment

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.databinding.ListImageFragmentBinding
import com.pk4us.declarationtable.dialoghelper.ProgressDialog
import com.pk4us.declarationtable.utils.AdapterCallback
import com.pk4us.declarationtable.utils.ImageManager
import com.pk4us.declarationtable.utils.ImagePicker
import com.pk4us.declarationtable.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFragment(private val fragmentCloseInterface:FragmentCloseInterface,private val newList:ArrayList<String>?): Fragment(),AdapterCallback {
    val adapter = SelectImageRvAdapter(this)
    val dragCallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null
    lateinit var binding: ListImageFragmentBinding
    private var addImageItem:MenuItem? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ListImageFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        touchHelper.attachToRecyclerView(binding.rcViewSelectImage)
        binding.rcViewSelectImage.layoutManager = LinearLayoutManager(activity)
        binding.rcViewSelectImage.adapter =  adapter
        if (newList != null) resizeSelectedImages(newList,true)
    }

    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>){
        adapter.updateAdapter(bitmapList,true)
    }

    override fun onDetach() {
        super.onDetach()
        fragmentCloseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    private fun resizeSelectedImages(newList: ArrayList<String>,needClear:Boolean){
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity as Activity)
            val bitmapList = ImageManager.imageResize(newList)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList,needClear)
            if (adapter.mainArray.size>2) addImageItem?.isVisible = false
        }
    }

    private fun setUpToolbar(){
        binding.tb.inflateMenu(R.menu.menu_choose_image)
        val deleteImageItem = binding.tb.menu.findItem(R.id.id_delete_image)
        addImageItem = binding.tb.menu.findItem(R.id.id_add_image)

        binding.tb.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        deleteImageItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(),true)
            addImageItem?.isVisible = true
            true
        }

        addImageItem?.setOnMenuItemClickListener {
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.getImages(activity as AppCompatActivity,imageCount,ImagePicker.REQUEST_CODE_GET_SINGLE_IMAGES)
            true
        }
    }

    fun updateAdapter(newList:ArrayList<String>){
        resizeSelectedImages(newList,false)
    }

    fun setSingleImage(uri:String, pos:Int){
        val pBar = binding.rcViewSelectImage[pos].findViewById<ProgressBar>(R.id.pBar)
        job = CoroutineScope(Dispatchers.Main).launch {
            pBar.visibility= View.VISIBLE
            val bitmapList = ImageManager.imageResize(listOf(uri))
            pBar.visibility= View.GONE
            adapter.mainArray[pos] = bitmapList[0]
            adapter.notifyItemChanged(pos)
        }
    }


}