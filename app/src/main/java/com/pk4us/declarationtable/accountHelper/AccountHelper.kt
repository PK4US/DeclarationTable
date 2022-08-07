package com.pk4us.declarationtable.accountHelper

import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.pk4us.declarationtable.MainActivity
import com.pk4us.declarationtable.R
import com.pk4us.declarationtable.constants.FirebaseAuthConstants
import com.pk4us.declarationtable.dialoghelper.GoogleAccConst
import java.lang.Exception

class AccountHelper(act: MainActivity) {
    val act = act
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.myAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    act.myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            signUpWithEmailSuccessful(task.result.user!!)
                        } else {
                            signUpWithEmailException(task.exception!!, email, password)
                        }
                    }
                }
            }
        }
    }

    private fun signUpWithEmailSuccessful(user: FirebaseUser){
        sendEmailVerification(user)
        act.uiUpdate(user)
    }

    private fun signUpWithEmailException(e: Exception,email:String, password: String){
        if (e is FirebaseAuthUserCollisionException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                linkEmailToGoogle(email,password)
            }
        } else if (e is FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(act, "Адрес электронной почты неправильного формата", Toast.LENGTH_LONG).show()
            }
        }
        if (e is FirebaseAuthWeakPasswordException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                Toast.makeText(act, "Пароль должен быть больше 6 символов", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.myAuth.currentUser?.delete()?.addOnCompleteListener {
                task ->
                if (task.isSuccessful){
                    act.myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            act.uiUpdate(task.result?.user)
                        } else {
                            signInWithEmailException(task.exception!!,email,password)
                        }
                    }
                }
            }
        }
    }

    private fun signInWithEmailException(e: Exception,email:String, password: String){
        if (e is FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                Toast.makeText(act, "Не верный пароль", Toast.LENGTH_LONG).show()
            }
            if (e.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(act, "В поле почты не должно быть пробелов", Toast.LENGTH_LONG).show()
            }
        } else if (e is FirebaseAuthInvalidUserException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                Toast.makeText(act, "Аккаунта с такой почтой не создано", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun linkEmailToGoogle(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (act.myAuth.currentUser != null) {
            act.myAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) { Toast.makeText(act, "Email усмешно подключен к Google аккаунту", Toast.LENGTH_LONG).show() } }
        } else { Toast.makeText(act, "По данной почте уже создан аккаунт через Google", Toast.LENGTH_LONG).show() }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.googleSignInLauncher.launch(intent)
    }

    fun signOutGoogle(){
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.myAuth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                act.myAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(act, "Упешно зарегестировались через Google", Toast.LENGTH_SHORT).show()
                        act.uiUpdate(task.result?.user)
                    }else{
                        Toast.makeText(act, "Ошибка авторизации через Google", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_email_done), Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_email_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun signInAnonymously(listener:Listener){
        act.myAuth.signInAnonymously().addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                listener.onComplete()
                Toast.makeText(act, "Вы вошли как гость", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(act, "Не удалось войти как гость", Toast.LENGTH_SHORT).show()

            }
        }
    }

    interface Listener{
        fun onComplete()
    }
}