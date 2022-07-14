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

class AccountHelper(act: MainActivity) {
    val act = act
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.myAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailVerification(task.result?.user!!)
                        act.uiUpdate(task.result?.user)
                    } else {
                        Log.d("MyLog", "Exception : " + task.exception)
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            val exception = task.exception as FirebaseAuthUserCollisionException
                            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                                Toast.makeText(act, "Адрес электронной почты уже используется другим аккаунтом.", Toast.LENGTH_LONG).show()
                            }
                        } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception = task.exception as FirebaseAuthInvalidCredentialsException
                            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                                Toast.makeText(act, "Адрес электронной почты неправильного формата", Toast.LENGTH_LONG).show()
                            }
                        }
                            if (task.exception is FirebaseAuthWeakPasswordException) {
                            val exception = task.exception as FirebaseAuthWeakPasswordException
                            if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                                Toast.makeText(act, "Пароль должен быть больше 6 символов", Toast.LENGTH_LONG).show()
                            }
                        }
                        Toast.makeText(act, act.resources.getString(R.string.sign_up_error), Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    act.uiUpdate(task.result?.user)
                } else {
                    Log.d("MyLog", "Exception : " + task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        val exception = task.exception as FirebaseAuthInvalidCredentialsException
                        Log.d("MyLog", "Exception : " + exception.errorCode)
                        if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                            Toast.makeText(act, "Не верный пароль", Toast.LENGTH_LONG).show()
                        }
                        if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                            Toast.makeText(act, "В адрессе почты не должно быть пробелов", Toast.LENGTH_LONG).show()
                        }
                    } else if (task.exception is FirebaseAuthInvalidUserException) {
                        val exception = task.exception as FirebaseAuthInvalidUserException
                        Log.d("MyLog", "Exception : " + exception.errorCode)
                        if (exception.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                            Toast.makeText(act, "Аккаунта с такой почтой не создано", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE)      //_______________________устарела____________________
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.myAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, "Sign In GOOGLE done", Toast.LENGTH_SHORT).show()
                act.uiUpdate(task.result?.user)
            }else{
                Log.d("MyLog", "Google SignIn Exception : " + task.exception)
//                if (task.exception is FirebaseAuthUserCollisionException) {
//                    val exception = task.exception as FirebaseAuthUserCollisionException
//                    if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
//                        Toast.makeText(act, "Адрес электронной почты уже используется другим аккаунтом.", Toast.LENGTH_LONG).show()
//                    }
//                }
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_email_done),
                    Toast.LENGTH_LONG
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
}