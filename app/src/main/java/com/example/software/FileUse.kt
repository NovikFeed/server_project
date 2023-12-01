package com.example.software

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceActivity.Header
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.activity.result.contract.ActivityResultContracts
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import java.io.File
import java.io.InputStream
import java.lang.Thread.sleep
import java.net.URI
import kotlin.concurrent.thread

class FileUse : AppCompatActivity() {
    lateinit var progressBar: ProgressBar
    lateinit var streamFile : InputStream
    lateinit var fileName: String
    lateinit var typeFile:String
    lateinit var btnChoiseFile :Button
    lateinit var btnUploadFile : Button
    lateinit var btnDownloadFile : Button
    lateinit var enterFileTexxt : TextView
    lateinit var btnBack: ImageButton
    lateinit var preficsEnterFile : TextView
    private val ActivityRes = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
        if(result.resultCode == RESULT_OK){
            val data: Intent? = result.data
            val path = data?.data
            if(path != null){
                val tmp = createInputStream(this, path)
                val mime = MimeTypeMap.getSingleton()
                var tmp3 = mime.getExtensionFromMimeType(this.contentResolver.getType(path))
                fileName = File(path.getPath()).nameWithoutExtension
                enterFileTexxt.setText(fileName)
                enterFileTexxt.visibility = View.VISIBLE
                preficsEnterFile.visibility = View.VISIBLE
                if((tmp != null)){
                    streamFile = tmp
                }
                if(tmp3 != null){
                    typeFile = tmp3
                }

            }
        }
    }


    private fun startActivityFor(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type ="*/*"
        ActivityRes.launch(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_use)
        preficsEnterFile = findViewById(R.id.textView3)
        enterFileTexxt = findViewById(R.id.textView4)
        btnChoiseFile = findViewById(R.id.button2)
        btnBack = findViewById(R.id.imageButton)
        btnUploadFile = findViewById(R.id.button3)
        progressBar = findViewById(R.id.progressBar3)
        btnDownloadFile = findViewById(R.id.button4)
        btnChoiseFile.setOnClickListener{
            startActivityFor()
        }
        btnUploadFile.setOnClickListener{
            if((isInternetAvailable())&&(ServerManeger.tryConnect())){
            statusBar(true)
            thread {
                ServerManeger.uploadFile(streamFile, fileName, typeFile)

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        this,
                        "ФАЙЛ УСПІШНО ЗАВАНТАЖЕНО НА СЕРВЕР",
                        Toast.LENGTH_LONG
                    ).show()
                }
                Handler(Looper.getMainLooper()).post { statusBar(false) }
            }
            }
            else{
                Toast.makeText(this,"ВИНИКЛА ПОМИЛКА ПІДКЛЮЧЧЕННЯ ДО СЕРВЕРУ АБО ВІДСУТНЄ ІНТЕРНЕТ ЗʼЄДНАННЯ", Toast.LENGTH_LONG).show()
                backToLogin()
            }
        }
        btnBack.setOnClickListener{backToLogin()}
    }
     fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    fun createInputStream(context: Context, uri:Uri):InputStream?{
        return context.contentResolver.openInputStream(uri)
    }
    fun statusBar(bl: Boolean){
        if(bl){
            progressBar.visibility =View.VISIBLE
            btnUploadFile.isEnabled = false
            btnChoiseFile.isEnabled =false
            btnDownloadFile.isEnabled = false
        }
        else{
            progressBar.visibility =View.INVISIBLE
            btnUploadFile.isEnabled = true
            btnChoiseFile.isEnabled =true
            btnDownloadFile.isEnabled = true
            preficsEnterFile.visibility = View.INVISIBLE
            enterFileTexxt.visibility = View.INVISIBLE
        }
    }
    fun backToLogin(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    }