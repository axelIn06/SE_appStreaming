package com.example.streamcuscoapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pedro.common.ConnectChecker
import com.pedro.library.rtmp.RtmpCamera1

class MainActivity : AppCompatActivity(),
    ConnectChecker,
    SurfaceHolder.Callback {

    private var rtmpCamera1: RtmpCamera1? = null
    private lateinit var surfaceView: SurfaceView
    private lateinit var bStartStop: Button

    // Sin http, sin espacios, solo rtmp y la IP
    private val endpoint = "rtmp://3.149.90.139/live/stream"
    private val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    private var isSurfaceReady = false
    private var permissionsGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surfaceView = findViewById(R.id.surfaceView)
        bStartStop = findViewById(R.id.b_start_stop)
        bStartStop.isEnabled = false

        surfaceView.holder.addCallback(this)

        bStartStop.setOnClickListener {
            val camera = rtmpCamera1 ?: return@setOnClickListener

            if (!camera.isStreaming) {
                if (camera.prepareAudio() && camera.prepareVideo()) {
                    camera.startStream(endpoint)
                } else {
                    Toast.makeText(this, "Error preparando hardware", Toast.LENGTH_SHORT).show()
                }
            } else {
                camera.stopStream()
                bStartStop.text = "INICIAR STREAM"
            }
        }

        checkPermissions()
    }

    // ---------------- PERMISOS ----------------

    private fun checkPermissions() {
        if (hasPermissions()) {
            permissionsGranted = true
            initCameraIfReady()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && hasPermissions()) {
            permissionsGranted = true
            initCameraIfReady()
        } else {
            Toast.makeText(
                this,
                "La app no puede funcionar sin permisos",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun hasPermissions(): Boolean {
        return PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    // ---------------- SURFACE ----------------

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
        isSurfaceReady = true
        initCameraIfReady()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceReady = false
        rtmpCamera1?.apply {
            if (isStreaming) stopStream()
            stopPreview()
        }
        rtmpCamera1 = null
    }

    // ---------------- INIT SEGURO ----------------

    private fun initCameraIfReady() {
        if (!permissionsGranted || !isSurfaceReady) return
        if (rtmpCamera1 != null) return

        rtmpCamera1 = RtmpCamera1(surfaceView, this)
        rtmpCamera1?.startPreview()

        bStartStop.isEnabled = true
    }

    // ---------------- CALLBACKS RTMP ----------------

    override fun onConnectionStarted(url: String) {}

    override fun onConnectionSuccess() {
        runOnUiThread {
            Toast.makeText(this, "Conexión exitosa", Toast.LENGTH_SHORT).show()
            bStartStop.text = "DETENER STREAM"
        }
    }

    override fun onConnectionFailed(reason: String) {
        runOnUiThread {
            Toast.makeText(this, "Error: $reason", Toast.LENGTH_LONG).show()
            rtmpCamera1?.stopStream()
            bStartStop.text = "INICIAR STREAM"
        }
    }

    override fun onDisconnect() {
        runOnUiThread {
            Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show()
            bStartStop.text = "INICIAR STREAM"
        }
    }

    override fun onNewBitrate(bitrate: Long) {}
    override fun onAuthError() {}
    override fun onAuthSuccess() {}
}
