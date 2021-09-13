package com.jonsung.goeunj.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object FirebaseUserManager {

    var auth: FirebaseAuth? = null
    private var userAccount: FirebaseUser? = null

    fun getCurrentUser(): FirebaseUser? {
        return auth?.currentUser
    }

    fun setUserAccount(account: FirebaseUser) {
        userAccount = account
    }

    fun getUserAccount(): FirebaseUser? {
        return userAccount
    }


}