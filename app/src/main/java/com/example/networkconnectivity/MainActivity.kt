package com.example.networkconnectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.networkconnectivity.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

/*
There are a vast majority of ways to handle network connectivity in android.

In this main activity, I will show pretty much boilerplate code of how to use
a broadcast receiver for network connectivity.
 */
@Suppress("Deprecation")
class MainActivity : AppCompatActivity() {

    /*
    Here we are going to use ViewBinding. This is completely optional.
    Could very well use regular findViewById method.
     */
    private lateinit var binding: ActivityMainBinding

    /*
    Must first declare a broadcast receiver variable, and override
    the onReceive method.
     */
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager
                    .EXTRA_NO_CONNECTIVITY, false
            )
            if (notConnected) {
                disconnected()
            } else {
                connected()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        Hide action bar and theme(OPTIONAL).
         */
        supportActionBar?.hide()
        this@MainActivity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


    }

    /*
    When activity starts, register receiver, and when it stops, unregister receiver.
     */
    override fun onStart() {
        super.onStart()
        registerReceiver(
            broadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
        Toast.makeText(this, "Activity started.", Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
        Toast.makeText(
            this, "Activity stopped.",
            Toast.LENGTH_LONG
        ).show()
    }


    /*
    methods called in override method above onCreate for displaying a toast message
    when disconnected or connected.

    When connected and disconnected, messages and images are shown.
     */
    private fun disconnected() {
        binding.apply {
            Snackbar.make(
                mainActivity,
                "Disconnected", Snackbar.LENGTH_INDEFINITE
            ).show()

            tvConnected.alpha = 0F
            ivConnected.alpha = 0F
            tvDisconnected.alpha = 1F
            ivDisconnected.alpha = 1F
        }
    }

    private fun connected() {
        Toast.makeText(
            this, "Connected...",
            Toast.LENGTH_LONG
        ).show()
        binding.apply {
            tvConnected.alpha = 1F
            ivConnected.alpha = 1F
            tvDisconnected.alpha = 0F
            ivDisconnected.alpha = 0F
        }
    }

    /*
    When activity is destroyed, be sure to unregister it just in case. More
    of a fail safe method just to be sure.
     */
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}