package alpha.soft.weather.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.SettingsClient

class EasyLocationProvider private constructor(builder: Builder) : LifecycleObserver {

    @SuppressLint("MissingPermission")
    fun requestLocationUpdate() {
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback!!,
            Looper.myLooper()
        )
    }

    private val callback: EasyLocationCallback?
    private val context: Context
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private val interval: Long
    private val fastestInterval: Long
    private val priority: Int
    private var latitude = 0.0
    private var longitude = 0.0

    init {
        context = builder.context
        callback = builder.callback
        interval = builder.interval
        fastestInterval = builder.fastestInterval
        priority = builder.priority
    }

    private fun connectGoogleClient() {
        val googleAPI = GoogleApiAvailability.getInstance()
        val resultCode = googleAPI.isGooglePlayServicesAvailable(context)
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient!!.connect()
        } else {
            val REQUEST_GOOGLE_PLAY_SERVICE = 988
            googleAPI.getErrorDialog(
                context as AppCompatActivity,
                resultCode,
                REQUEST_GOOGLE_PLAY_SERVICE
            )
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreateLocationProvider() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onLocationResume() {
        buildGoogleApiClient()
    }

    @SuppressLint("MissingPermission")
    @Synchronized
    private fun buildGoogleApiClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        mSettingsClient = LocationServices.getSettingsClient(context)

        mGoogleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                override fun onConnected(@Nullable bundle: Bundle?) {
                    callback!!.onGoogleAPIClient(mGoogleApiClient, "Connected")

                    mLocationRequest = LocationRequest()
                    mLocationRequest!!.interval = interval
                    mLocationRequest!!.fastestInterval = fastestInterval
                    mLocationRequest!!.priority = priority
                    mLocationRequest!!.smallestDisplacement = 0f

                    val builder = LocationSettingsRequest.Builder()
                    builder.addLocationRequest(mLocationRequest!!)
                    builder.setAlwaysShow(true)
                    mLocationSettingsRequest = builder.build()

                    mSettingsClient!!
                        .checkLocationSettings(mLocationSettingsRequest)
                        .addOnSuccessListener {
                            showLog("GPS is Enabled Requested Location Update")
                            requestLocationUpdate()
                        }.addOnFailureListener { e ->
                            when ((e as ApiException).statusCode) {
                                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                                    val REQUEST_CHECK_SETTINGS = 214
                                    val rae = e as ResolvableApiException
                                    rae.startResolutionForResult(
                                        context as AppCompatActivity,
                                        REQUEST_CHECK_SETTINGS
                                    )
                                } catch (sie: IntentSender.SendIntentException) {
                                    showLog("Unable to Execute Request")
                                }

                                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> showLog("Location Settings are Inadequate, and Cannot be fixed here. Fix in Settings")
                            }
                        }.addOnCanceledListener { showLog("onCanceled") }
                }

                override fun onConnectionSuspended(i: Int) {
                    connectGoogleClient()
                    callback!!.onGoogleAPIClient(mGoogleApiClient, "Connection Suspended")
                }
            })
            .addOnConnectionFailedListener { connectionResult ->
                callback!!.onGoogleAPIClient(
                    mGoogleApiClient,
                    "" + connectionResult.errorCode + " " + connectionResult.errorMessage
                )
            }
            .addApi(LocationServices.API)
            .build()

        connectGoogleClient()

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                latitude = locationResult!!.lastLocation.latitude
                longitude = locationResult.lastLocation.longitude

                if (latitude == 0.0 && longitude == 0.0) {
                    showLog("New Location Requested")
                    requestLocationUpdate()
                } else {
                    callback?.onLocationUpdated(latitude, longitude)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun removeUpdates() {
        try {
            callback!!.onLocationUpdateRemoved()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showLog(message: String) {
        Log.e("EasyLocationProvider", "" + message)
    }

    interface EasyLocationCallback {
        fun onGoogleAPIClient(googleApiClient: GoogleApiClient?, message: String)

        fun onLocationUpdated(latitude: Double, longitude: Double)

        fun onLocationUpdateRemoved()
    }

    class Builder(val context: Context) {
        var callback: EasyLocationCallback? = null
        var interval = (10 * 1000).toLong()
        var fastestInterval = (5 * 1000).toLong()
        var priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fun build(): EasyLocationProvider {
            if (callback == null) {
                Toast.makeText(
                    context,
                    "EasyLocationCallback listener can not be null",
                    Toast.LENGTH_SHORT
                ).show()
            }

            return EasyLocationProvider(this)
        }

        fun setListener(callback: EasyLocationCallback): Builder {
            this.callback = callback
            return this
        }

        fun setInterval(interval: Long): Builder {
            this.interval = interval
            return this
        }

        fun setFastestInterval(fastestInterval: Int): Builder {
            this.fastestInterval = fastestInterval.toLong()
            return this
        }

        fun setPriority(priority: Int): Builder {
            this.priority = priority
            return this
        }
    }
}