package com.example.clientapp

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import com.example.clientapp.databinding.ActivityMainBinding
import com.example.serviceapp.IMyAidlInterface

//class MainActivity : Activity() {
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val it = Intent("MyRemoteService")
        it.setPackage("com.example.serviceapp")
        bindService(it, connection, Context.BIND_AUTO_CREATE)

        binding.button.setOnClickListener {
            try {
                val desires = iMyAidlInterface?.desires
                binding.desires.text = desires
            }catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private var iMyAidlInterface: IMyAidlInterface? = null

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            iMyAidlInterface = IMyAidlInterface.Stub.asInterface(binder)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            iMyAidlInterface = null
        }
    }
}