package com.example.networkconnectivity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
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

    /*
    This is the variable we will use for our Live Data connection
     */
    private lateinit var cld: ConnectionLiveData


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
        requestPermissions()
        checkNetworkConnection()
    }

    /*
    Here is the method call we will use for our live data connection.
    This is not necessary as I prefer to use a broadcast receiver,
    however this is another way of doing it.
     */
    private fun checkNetworkConnection() {
        cld = ConnectionLiveData(application)

        cld.observe(this) { isConnected ->

            binding.apply {
                if (isConnected) {
                    binding.apply {
                        tvConnected.alpha = 1F
                        ivConnected.alpha = 1F
                        tvDisconnected.alpha = 0F
                        ivDisconnected.alpha = 0F
                    }
                } else {
                    tvConnected.alpha = 0F
                    ivConnected.alpha = 0F
                    tvDisconnected.alpha = 1F
                    ivDisconnected.alpha = 1F
                }
            }

        }
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

    /*
    ------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------
    Above is all the code you need to be able to monitor connectivity with a broadcast receiver.

    Below, I will just demonstrate how we can request permissions as well.

    It will give a better picture of how some android apps perform in a more professional
    environment.

    This is pretty boilerplate code as this is how I ask for multiple permissions for
    majority of my applications.

    Also, this approach isn't usually used to internet and network permissions as those are usually
    already turned on in the phones. However, these same methods can be used for asking a user
    for their fine and coarse location.
    ------------------------------------------------------------------------------------------------
    -----------------------------------------------------------------------------------------------
     */


    private fun hasInternet() =
        ActivityCompat.checkSelfPermission(

            this,
            Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED


    private fun hasNetworkState() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_NETWORK_STATE
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasWifiState() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_WIFI_STATE
        ) == PackageManager.PERMISSION_GRANTED


    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // If user did not accept permissions, then add it to the list.
        if (!hasInternet()) {
            permissionsToRequest.add(Manifest.permission.INTERNET)
        }
        if (!hasNetworkState()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_NETWORK_STATE)
        }
        if (!hasWifiState()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_WIFI_STATE)
        }


        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                0
            )
        }
    }

    /*
    Method called when the permissions are requested from the user.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty()) {
            // can loop through grant results array.
            for (i in grantResults.indices) {// indices = size -1
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(
                        "MainActivity Permissions Request.",
                        "${permissions[i]} granted."
                    ) // print the permissions granted
                }
            }
        }
    }
}