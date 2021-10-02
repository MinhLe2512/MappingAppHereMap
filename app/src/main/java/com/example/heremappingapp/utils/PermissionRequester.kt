package com.example.heremappingapp.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class PermissionRequester(private val activity: Activity) {
    private val PERMISSIONS_REQUEST_CODE = 42
    private var resultListener: ResultListener? = null

    private val FINE_LOCATION = 101
    private val INTERNET = 102
    private val COARSE_LOCATION = 103

    interface ResultListener {
        fun permissionsGranted()
        fun permissionsDenied()
    }

    fun request(resultListener: ResultListener) {
        this.resultListener = resultListener

        val missingPermissions: Array<String> = getPermissionsRequest()
        if (missingPermissions.isEmpty())
            resultListener.permissionsGranted()
        else {
            ActivityCompat.requestPermissions(activity, missingPermissions, PERMISSIONS_REQUEST_CODE)

        }
    }

    fun onRequestPermissionsRequest(requestCode: Int, grantResults: IntArray) {
        if (resultListener == null || grantResults.isEmpty())
            return
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            var allGranted = true
            for (result in grantResults) {
                allGranted = allGranted and (result == PackageManager.PERMISSION_GRANTED)
            }
            if (allGranted)
                resultListener!!.permissionsGranted()
            else
                resultListener!!.permissionsDenied()
        }
    }

    fun checkForPermissions(permission: String, name: String, requestCode: Int) {
        when {
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED -> {
                return
            }
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> showDialog(permission, name, requestCode)
            else -> ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }

    private fun showDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(activity)
        builder.apply {
            setTitle("Permission required")
            setMessage("Permission to access your $name is required to use this app")
            setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            })
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun isLocationEnabled(): Boolean{
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getPermissionsRequest(): Array<String> {
        val permissionList = ArrayList<String>()
        try {
            val packageInfo = activity.packageManager.getPackageInfo(
                activity.packageName,
                PackageManager.GET_PERMISSIONS
            )
            if (packageInfo.requestedPermissions != null) {
                for (permission in packageInfo.requestedPermissions) {
                    if (ContextCompat.checkSelfPermission(
                            activity,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M &&
                            permission.equals(Manifest.permission.CHANGE_NETWORK_STATE)
                        ) {
                            // Exclude CHANGE_NETWORK_STATE as it does not require explicit user approval.
                            // This workaround is needed for devices running Android 6.0.0,
                            // see https://issuetracker.google.com/issues/37067994
                            continue;
                        }
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                            (permission.equals(Manifest.permission.ACTIVITY_RECOGNITION) ||
                                    permission.equals(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                        ) {
                            continue;
                        }
                        permissionList.add(permission);
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return permissionList.toTypedArray()
    }
}