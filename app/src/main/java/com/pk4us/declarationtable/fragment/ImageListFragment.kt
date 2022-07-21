package com.pk4us.declarationtable.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.databinding.ListImageFragmentBinding
import com.pk4us.declarationtable.utils.ImageManager
import com.pk4us.declarationtable.utils.ImagePicker
import com.pk4us.declarationtable.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFragment(private val fragmentCloseInterface:FragmentCloseInterface,private val newList:ArrayList<String>): Fragment() {
    val adapter = SelectImageRvAdapter()
    val dragCallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)
    private lateinit var job: Job
    lateinit var binding: ListImageFragmentBinding

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
        job = CoroutineScope(Dispatchers.Main).launch {
            val text = ImageManager.imageResize(newList)
            Log.d("MyLog", "Result: $text")
        }

//        adapter.updateAdapter(newList,true)

    }

    override fun onDetach() {
        super.onDetach()
        fragmentCloseInterface.onFragClose(adapter.mainArray)
        job.cancel()
    }

    private fun setUpToolbar(){
        binding.tb.inflateMenu(R.menu.menu_choose_image)
        val deleteImageItem = binding.tb.menu.findItem(R.id.id_delete_image)
        val addImageItem = binding.tb.menu.findItem(R.id.id_add_image)

        binding.tb.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        deleteImageItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(),true)
            true
        }

        addImageItem.setOnMenuItemClickListener {
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.getImages(activity as AppCompatActivity,imageCount,ImagePicker.REQUEST_CODE_GET_SINGLE_IMAGES)
            true
        }
    }

    fun updateAdapter(newList:ArrayList<String>){
        adapter.updateAdapter(newList,false)
    }

    fun setSingleImage(uri:String, pos:Int){
        adapter.mainArray[pos] = uri
        adapter.notifyDataSetChanged()
    }
}