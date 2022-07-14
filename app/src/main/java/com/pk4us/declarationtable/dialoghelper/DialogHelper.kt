package com.pk4us.declarationtable.dialoghelper

import android.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.pk4us.declarationtable.MainActivity
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.accountHelper.AccountHelper
import com.pk4us.declarationtable.databinding.SignDialogBinding

class DialogHelper(act: MainActivity) {
    private val act = act
    val accHelper = AccountHelper(act)

    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(act)
        val binding = SignDialogBinding.inflate(act.layoutInflater)
        val view = binding.root
        builder.setView(view)
        setDialogState(index, binding)

        val dialog = builder.create()
        binding.btSignUpIn.setOnClickListener {
            setOnClickSignUpIn(binding, index, dialog)
        }
        binding.btForgetPassword.setOnClickListener {
            setOnClickResetPassword(binding, dialog)
        }
        binding.btGoogleSignIn.setOnClickListener {
            accHelper.signInWithGoogle()
        }
        dialog.show()
    }

    private fun setOnClickResetPassword(binding: SignDialogBinding, dialog: AlertDialog?) {
        if (binding.edSignEmail.text.isNotEmpty()) {
            act.myAuth.sendPasswordResetEmail(binding.edSignEmail.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(act, R.string.email_reset_password, Toast.LENGTH_SHORT).show()
                        Log.d("MyLog", "Ex: " + task.exception)
                    }
                }
            dialog?.dismiss()
        } else {
            binding.tvDialogMessage.visibility = View.VISIBLE
            binding.edSignPassword.visibility = View.GONE
            binding.btForgetPassword.text = "Отпавить на почту"
            binding.btSignUpIn.visibility = View.GONE
        }
    }

    private fun setOnClickSignUpIn(binding: SignDialogBinding, index: Int, dialog: AlertDialog?) {
        dialog?.dismiss()
        if (index == DialogConst.SIGN_UP_STATE) {
            accHelper.signUpWithEmail(
                binding.edSignEmail.text.toString(),
                binding.edSignPassword.text.toString()
            )
        } else {
            accHelper.signInWithEmail(
                binding.edSignEmail.text.toString(),
                binding.edSignPassword.text.toString()
            )
        }
    }

    private fun setDialogState(index: Int, binding: SignDialogBinding) {
        if (index == DialogConst.SIGN_UP_STATE) {
            binding.tvSignItem.text = act.resources.getString(R.string.sign_up)
            binding.btSignUpIn.text = act.resources.getString(R.string.sign_up_action)
        } else {
            binding.tvSignItem.text = act.resources.getString(R.string.sign_in)
            binding.btSignUpIn.text = act.resources.getString(R.string.sign_in_action)
            binding.btForgetPassword.visibility = View.VISIBLE
        }
    }
}