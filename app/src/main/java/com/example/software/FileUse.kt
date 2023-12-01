package com.example.software

import android.app.Activity
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
    lateinit var pathFile :String
    lateinit var streamFile : InputStream
    lateinit var fileName: String
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
                fileName = File(path.getPath()).name
                enterFileTexxt.setText(fileName)
                enterFileTexxt.visibility = View.VISIBLE
                preficsEnterFile.visibility = View.VISIBLE
                val tmp2 = path.getPath()
                if((tmp != null)&&(tmp2 != null)){
                    streamFile = tmp
                    pathFile = tmp2
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
        btnChoiseFile.setOnClickListener{
            startActivityFor()
        }
        btnUploadFile.setOnClickListener{
            if((isInternetAvailable())&&(ServerManeger.tryConnect())){
            statusBar(true)
//            Toast.makeText(this, fileName, Toast.LENGTH_LONG).show()
            thread {
//                val start: Long = System.currentTimeMillis()
                ServerManeger.uploadFile(streamFile, fileName)
//                val endtime = System.currentTimeMillis()
//                Log.i("TIME", (endtime-start).toString())


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
//    fun connectToServer(data:ArrayList<String>){
//        val host = data[0]
//        val user = data[1]
//        val pass = data[2]
//        val port = data[3]
//        val jsch = JSch()
//        val btnPut = findViewById<Button>(R.id.button2)
//        val session = jsch.getSession(user, host, port.toInt())
//        session.setConfig("StrictHostKeyChecking", "no");
//        session.setPassword(pass)
//        if(isInternetAvailable()) {
//            thread {
//                try {
//                    session.connect()
//                    val chanel = session.openChannel("sftp") as ChannelSftp
//                    chanel.connect()
//                } catch (e: JSchException) {
//                    val eror = findViewById<TextView>(R.id.textView3)
//                    val job = Thread {
//                        Handler(Looper.getMainLooper()).post{eror.visibility = View.VISIBLE}
//                        Thread.sleep(5000)
//                        Handler(Looper.getMainLooper()).post{val intent = Intent(this, LoginActivity::class.java)
//                        startActivity(intent)
//                            finish()}}
//                    job.start()
//                    job.join()
//                }
//            }
//        }
//
//    }
//    fun getDataFromBundle():ArrayList<String>{
//        val data:ArrayList<String> = ArrayList()
//        val bundle = intent.extras
//        val host : String? = bundle?.getString("host")
//        val user = bundle?.getString("username")
//        val pass = bundle?.getString("password")
//        val port = bundle?.getString("port")
//        if((host != null)&&(user != null)&&(pass != null)&&(port != null)) {
//            data.add(host)
//            data.add(user)
//            data.add(pass)
//            data.add(port)
//        }
//        return data
//    }
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