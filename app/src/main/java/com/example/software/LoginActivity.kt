package com.example.software

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceActivity.Header
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.Toast
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.security.interfaces.EdECKey
import kotlin.concurrent.thread

    class LoginActivity : AppCompatActivity() {
        lateinit var shared:SharedPreferences
        lateinit var serverField:EditText
        lateinit var nameField:EditText
        lateinit var passField:EditText
        lateinit var portField:EditText
        lateinit var progresBar:ProgressBar
        lateinit var btnStart: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        shared = getSharedPreferences("authData", Context.MODE_PRIVATE )
        val editorShared = shared.edit()
        btnStart = findViewById(R.id.button)
        serverField = findViewById(R.id.editTextTextEmailAddress)
        nameField = findViewById(R.id.editTextText)
        passField = findViewById(R.id.editTextTextPassword)
        portField = findViewById(R.id.editTextNumber)

        if(chekShared(shared)){
            resetData()
        }
        btnStart.setOnClickListener {
            progresBar = findViewById<ProgressBar>(R.id.progressBar2)
            statusBar(true)
            if(chekInput(serverField) && chekInput(nameField)&& chekInput(passField)&&chekInput(portField)) {
                val checkBox = findViewById<Switch>(R.id.switch1)
                val chek: Boolean = checkBox.isChecked
                if (chek) {
                    saveData()
                } else {
                    editorShared.clear()
                    editorShared.apply()
                }

                connectToSftpServer(serverField, nameField, passField, portField)
            }
            else{
                Toast.makeText(this, "Заповніть всі поля",Toast.LENGTH_SHORT).show()
                statusBar(false)
            }
        }
    }

    fun connectToSftpServer(inputAdres:EditText,inputLogin: EditText, inputPass: EditText, inputPort: EditText) {
        val bundle = Bundle()
        val host = inputAdres.text.toString()
        val username = inputLogin.text.toString()
        val password = inputPass.text.toString()
        val port= inputPort.text.toString()


        bundle.putString("host", host)
        bundle.putString("username", username)
        bundle.putString("password", password)
        bundle.putString("port", port.toString())
    thread {
//        val host = "165.22.77.123"
//        val port = "333"
//        val username = "usersftp"
//        val password = "j2i6gKy8"
//    val jsch = JSch()
//    val session = jsch.getSession(username, host, port)
//    session.setConfig("StrictHostKeyChecking", "no");
//    session.setPassword(password)
    if(isInternetAvailable()){
        try {
            ServerManeger.connect(username, host,  password, port)
            Handler(Looper.getMainLooper()).post{
                val intent = Intent(this,FileUse::class.java)
                intent.putExtras(bundle)
                statusBar(false)
                startActivity(intent)
                finish()
            }
        }
        catch (e:JSchException){
            statusBar(false)

            e.printStackTrace()
            if(e.message == "Auth fail"){
                Handler(Looper.getMainLooper()).post{(Toast.makeText(this,"Неправильні введені данні авторизації", Toast.LENGTH_LONG).show())}
            }
            else{
                Handler(Looper.getMainLooper()).post{(Toast.makeText(this,"Неправильно введений ip або port/АБО/Сервер не доступний", Toast.LENGTH_LONG).show())}
            }


        }
//        try {
//            session.connect()
//            val chanel = session.openChannel("sftp") as ChannelSftp
//            chanel.connect()
////            serverField.setText("")
////            nameField.setText("")
////            passField.setText("")
////            portField.setText("")
//            chanel.disconnect()
//            session.disconnect()

//
//
//
//        }
//        catch (e: JSchException){
//            e.printStackTrace()
////            Handler(Looper.getMainLooper()).post{(Toast.makeText(this,e.message, Toast.LENGTH_LONG).show())}
//
//            if(e.message == "Auth fail"){
//                Handler(Looper.getMainLooper()).post{(Toast.makeText(this,"Неправильні введені данні авторизації", Toast.LENGTH_LONG).show())}
//            }
//            else{
//                Handler(Looper.getMainLooper()).post{(Toast.makeText(this,"Неправильно введений ip або port/АБО/Сервер не доступний", Toast.LENGTH_LONG).show())}
//            }
//        }
        }
    else{
        Handler(Looper.getMainLooper()).post{(Toast.makeText(this, "Відсутнє підключення до інтернету", Toast.LENGTH_LONG).show())}
    }

    }
    }
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    fun saveData(){
        val editorShared = shared.edit()
        val host = serverField.text.toString()
        val username = nameField.text.toString()
        val password = passField.text.toString()
        val port = portField.text.toString().toInt()
        editorShared.putString("ip", host)
        editorShared.apply()
        editorShared.putString("name", username)
        editorShared.apply()
        editorShared.putString("pass", password)
        editorShared.apply()
        editorShared.putString("port", port.toString())
        editorShared.apply()
    }
    fun resetData(){
        if(shared.getString("ip", "") != "") {
            val check = findViewById<Switch>(R.id.switch1)
            val getIp = shared.getString("ip", "")
            val getName = shared.getString("name", "")
            val getPass = shared.getString("pass", "")
            val getPort = shared.getString("port", "")
            serverField.setText(getIp)
            nameField.setText(getName)
            passField.setText(getPass)
            portField.setText(getPort)
            check.isChecked = true
        }
    }
        fun chekInput(input: EditText) : Boolean{
            return input.text.toString().isNotEmpty()
        }
        fun chekShared(shared : SharedPreferences) : Boolean{
            var res : Boolean = false
            if(!shared.contains(null)){
            val getIp = shared.getString("ip", "")
            val getName = shared.getString("name", "")
            val getPass = shared.getString("pass", "")
            val getPort = shared.getString("port", "")
                res = (getIp != "") && (getName != "") && (getPass != "") && (getPort != "")
            }
            else {
                res = false

            }
            return res
        }
        fun statusBar(bl:Boolean){
            if(bl){
                progresBar.visibility = View.VISIBLE
                btnStart.isEnabled = false
            }
            else{
                progresBar.visibility = View.INVISIBLE
                btnStart.isEnabled = true
            }
        }
}