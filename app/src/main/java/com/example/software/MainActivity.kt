package com.example.software

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.MediaController
import android.widget.VideoView

class MainActivity : AppCompatActivity() {
   lateinit var videoView: VideoView
    lateinit var oflineUri: Uri

    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_main)
        val handler = Handler(Looper.getMainLooper())
        videoView = findViewById(R.id.videoView)
        oflineUri = Uri.parse("android.resource://$packageName/${R.raw.start}")
        videoView.setVideoURI(oflineUri)
        videoView.start()
        handler.postDelayed({
                            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        },3000)
    }
}