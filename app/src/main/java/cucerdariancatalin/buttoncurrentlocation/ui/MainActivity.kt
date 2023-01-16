package cucerdariancatalin.buttoncurrentlocation.ui

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cucerdariancatalin.buttoncurrentlocation.Constants
import cucerdariancatalin.buttoncurrentlocation.R
import cucerdariancatalin.buttoncurrentlocation.service.LocationService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        startLocationUpdateButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE_LOCATION_PERMISSION
                )
            } else {
                startLocationService()
                startLocationUpdateButton.visibility = View.GONE
                stopLocationUpdateButton.visibility = View.VISIBLE
            }
        }

        stopLocationUpdateButton.setOnClickListener {
            stopLocationService()
            startLocationUpdateButton.visibility = View.VISIBLE
            stopLocationUpdateButton.visibility = View.GONE
        }

        if (isLocationServiceRunning()) {
            stopLocationUpdateButton.visibility = View.VISIBLE
        } else {
            startLocationUpdateButton.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLocationServiceRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        //TODO change deprecated method
        activityManager.getRunningServices(Integer.MAX_VALUE).forEach {
            if (LocationService::class.java.name == it.service.className) {
                if (it.foreground) {
                    return true
                }
            }
        }

        return false
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action =
                Constants.ACTION_START_LOCATION_SERVICE
            startService(intent)
        }
    }

    private fun stopLocationService() {
        if (isLocationServiceRunning()) {
            val intent = Intent(applicationContext, LocationService::class.java)
            intent.action =
                Constants.ACTION_STOP_LOCATION_SERVICE
            startService(intent)
        }
    }

    companion object {
        private val REQUEST_CODE_LOCATION_PERMISSION = 1
    }
}