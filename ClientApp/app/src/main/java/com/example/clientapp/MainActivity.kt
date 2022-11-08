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
    var determined: Boolean = true

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

        binding.findDesireButton.setOnClickListener {
            if (determined){
                try {
                    val desierMeaning = iMyAidlInterface?.desiresMeaning
                    val desireName = iMyAidlInterface?.getDesiresName(desierMeaning)
                    binding.desireName.text = desireName
                    binding.desireMeaning.text = desierMeaning
                }catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            determined = false
        }

        binding.removeDesireButton.setOnClickListener {
            binding.removeDesire.text = "よかったですね、これであなたの「" + binding.desireName.text + "」はなくなりました\nこのまま全ての煩悩が消えるといいですね"

            binding.desireName.text = "煩悩の名前"
            binding.desireMeaning.text = "煩悩の意味"
            determined = true
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