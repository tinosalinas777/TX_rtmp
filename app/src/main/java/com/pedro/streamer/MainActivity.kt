/*
 * Copyright (C) 2024 pedroSG94.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pedro.streamer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.pedro.streamer.oldapi.OldApiActivity



import com.pedro.streamer.utils.ActivityLink
import com.pedro.streamer.utils.ImageAdapter
import com.pedro.streamer.utils.toast

class MainActivity : AppCompatActivity() {
  private lateinit var btnStartActivity: Button
  //private lateinit var list: GridView
  //private val activities: MutableList<ActivityLink> = mutableListOf()

  private val permissions = mutableListOf(
    Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
  ).apply {
    if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
      this.add(Manifest.permission.POST_NOTIFICATIONS)
    }
  }.toTypedArray()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    transitionAnim(true)
    val tvVersion = findViewById<TextView>(R.id.tv_version)
    tvVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)
    btnStartActivity = findViewById(R.id.btn_start_activity)
    btnStartActivity.setOnClickListener {
      startSelectedActivity()
    }
    requestPermissions()
  }

  @Suppress("DEPRECATION")
  private fun transitionAnim(isOpen: Boolean) {
    if (Build.VERSION.SDK_INT >= VERSION_CODES.UPSIDE_DOWN_CAKE) {
      val type = if (isOpen) OVERRIDE_TRANSITION_OPEN else OVERRIDE_TRANSITION_CLOSE
      overrideActivityTransition(type, R.anim.slide_in, R.anim.slide_out)
    } else {
      overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
    }
  }
  private fun startSelectedActivity() {
    if (hasPermissions(this)) {
      val intent = Intent(this, OldApiActivity::class.java)
      startActivity(intent)
      transitionAnim(false)
    } else {
      showPermissionsErrorAndRequest()
    }
  }

  private fun requestPermissions() {
    if (!hasPermissions(this)) {
      ActivityCompat.requestPermissions(this, permissions, 1)
    }
  }





  private fun showMinSdkError(minSdk: Int) {
    val named: String = when (minSdk) {
      VERSION_CODES.JELLY_BEAN_MR2 -> "JELLY_BEAN_MR2"
      VERSION_CODES.LOLLIPOP -> "LOLLIPOP"
      else -> "JELLY_BEAN"
    }
    toast("Version minima de Android $named (API $minSdk)")
  }

  private fun showPermissionsErrorAndRequest() {
    toast("Primero necesitas permisos")
    requestPermissions()
  }

  private fun hasPermissions(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
      for (permission in permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission)
          != PackageManager.PERMISSION_GRANTED
        ) {
          return false
        }
      }
    }
    return true
  }
}