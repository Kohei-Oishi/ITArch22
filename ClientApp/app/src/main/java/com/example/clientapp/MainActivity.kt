package com.example.clientapp

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.example.clientapp.databinding.ActivityMainBinding
import com.example.serviceapp.IMyAidlInterface

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var soundPool: SoundPool
    private var determined: Boolean = true
    private var zyoyaSound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(1)
            .build()

        zyoyaSound = soundPool.load(this, R.raw.zyoya, 1)

        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            Log.d("debug", "sampleId=$sampleId")
            Log.d("debug", "status=$status")
        }
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
            soundPool.play(zyoyaSound, 1.0f, 1.0f, 0, 0, 1.0f)
            if(determined == false){
                binding.removeDesire.text = "よかったですね、これであなたの「" + binding.desireName.text + "」はなくなりました\nこのまま全ての煩悩が消えるといいですね"

                binding.desireName.text = "煩悩の名前"
                binding.desireMeaning.text = "煩悩の意味"
                determined = true
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