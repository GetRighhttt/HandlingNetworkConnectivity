package com.example.networkconnectivity

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData

/*
Here is the LiveData class we can use to check for our network connectivity.
This is pretty much boilerplate code as well from what I have researched.

However, there are many ways of observing, so be sure to take this approach and
the broadcast receiver approach as necessary.

I personally believe using a broadcast receiver is faster and the purpose of
a broadcast receiver is literally to monitor system(Android) events and return
data..so do with that information as you please.
 */
class ConnectionLiveData(private val connectivityManager: ConnectivityManager) :
    LiveData<Boolean>(){

    constructor(application: Application) : this(
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )

    private val networkCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ConnectivityManager.NetworkCallback(){

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActive() {
        super.onActive()
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}