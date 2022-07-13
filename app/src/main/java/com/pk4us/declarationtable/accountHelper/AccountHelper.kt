package com.pk4us.declarationtable.accountHelper

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.pk4us.declarationtable.MainActivity
import com.pk4us.declarationtable.R

class AccountHelper(act:MainActivity) {
    val act = act
    fun signUpWithEmail(email:String,password:String){
        if (email.isNotEmpty() && password.isNotEmpty()){
            act.myAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task ->
                if (task.isSuccessful){
                    sendEmailVerification(task.result?.user!!)
                    act.uiUpdate(task.result?.user)
                }else{
                    Toast.makeText(act,act.resources.getString(R.string.sign_up_error), Toast.LENGTH_LONG).show()
                    Log.d("MyLog","Exception : " + task.exception)
                }
            }
        }
    }

    fun signInWithEmail(email:String,password:String){
        if (email.isNotEmpty() && password.isNotEmpty()){
            act.myAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{task ->
                if (task.isSuccessful){
                    act.uiUpdate(task.result?.user)
                }else{
                    Toast.makeText(act,act.resources.getString(R.string.sign_in_error), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser){
        user.sendEmailVerification().addOnCompleteListener { task->
            if (task.isSuccessful){
                Toast.makeText(act,act.resources.getString(R.string.send_verification_email_done), Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(act,act.resources.getString(R.string.send_verification_email_error), Toast.LENGTH_LONG).show()
            }
        }
    }
}