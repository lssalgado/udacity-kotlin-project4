package com.udacity.project4.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import timber.log.Timber

fun checkSelfPermissions(permissions: Array<String>, context: Context): Array<String> {
    val arrayList = arrayListOf<String>()

    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            arrayList.add(permission)
        }
    }

    return arrayList.toTypedArray()
}

fun Fragment.onPermissionResult(permissions: Array<String>, view: View, onSuccess: () -> Unit) {
    val missingPermissions = checkSelfPermissions(permissions, context!!)
    if (missingPermissions.isNotEmpty()) {
        Timber.e("The following permissions were not granted: ${missingPermissions.contentDeepToString()}")
        Snackbar.make(
            view,
            getString(R.string.missing_location_permissions),
            Snackbar.LENGTH_LONG
        )
            // Extracted from: https://github.com/udacity/android-kotlin-geo-fences/blob/master/app/src/main/java/com/example/android/treasureHunt/HuntMainActivity.kt#L145
            .setAction(R.string.settings) {
                // Displays App settings screen.
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
    } else {
        onSuccess()
    }
}
