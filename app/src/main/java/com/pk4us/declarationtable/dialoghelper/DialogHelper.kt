package com.pk4us.declarationtable.dialoghelper

import android.app.AlertDialog
import com.pk4us.declarationtable.MainActivity
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.accountHelper.AccountHelper
import com.pk4us.declarationtable.databinding.SignDialogBinding

class DialogHelper (act:MainActivity){
    private val act = act
    private val accHelper = AccountHelper(act)

    fun createSignDialog(index:Int){
        val builder = AlertDialog.Builder(act)
        val binding = SignDialogBinding.inflate(act.layoutInflater)
        val view = binding.root
        builder.setView(view)
        if (index == DialogConst.SIGN_UP_STATE){
            binding.tvSignItem.text = act.resources.getString(R.string.sign_up)
            binding.btSignUpIn.text = act.resources.getString(R.string.sign_up_action)
        }else{
            binding.tvSignItem.text = act.resources.getString(R.string.sign_in)
            binding.btSignUpIn.text = act.resources.getString(R.string.sign_in_action)
        }
        val dialog = builder.create()
        binding.btSignUpIn.setOnClickListener {
            dialog.dismiss()
            if (index == DialogConst.SIGN_UP_STATE){
                accHelper.signUpWithEmail(binding.edSignEmail.text.toString(),binding.edSignPassword.text.toString())
            }else{
                accHelper.signInWithEmail(binding.edSignEmail.text.toString(),binding.edSignPassword.text.toString())
            }
        }
        dialog.show()
    }
}