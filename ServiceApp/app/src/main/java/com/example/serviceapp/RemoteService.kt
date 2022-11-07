package com.example.serviceapp

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RemoteService : Service() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        // Return the interface
        return binder
    }


    private val binder = object : IMyAidlInterface.Stub() {
        override fun getDesires(): String {
            return "愛"
        }
    }

}