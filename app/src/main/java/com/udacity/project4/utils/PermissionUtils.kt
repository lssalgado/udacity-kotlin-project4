package com.udacity.project4.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun checkSelfPermissions(permissions: Array<String>, context: Context): Array<String> {
    val arrayList = arrayListOf<String>()

    for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            arrayList.add(permission)
        }
    }

    return arrayList.toTypedArray()
}