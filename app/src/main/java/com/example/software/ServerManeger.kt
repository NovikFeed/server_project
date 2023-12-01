package com.example.software

import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.os.Looper
import android.webkit.JsPromptResult
import android.widget.Toast
import com.jcraft.jsch.Channel
import android.util.Log
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import java.io.File
import java.io.InputStream
import java.util.logging.Handler

object ServerManeger{
    private val jsch = JSch()
    private var session : Session? = null
    private var chanel : ChannelSftp? = null
    fun connect(host : String, username: String, password: String, port: String):Boolean{
        session= jsch.getSession(host,username,port.toInt())
        session?.setConfig("StrictHostKeyChecking", "no")
        session?.setPassword(password)
        session?.connect()
        chanel = session?.openChannel("sftp") as ChannelSftp
            chanel?.connect()
       return tryConnect()
    }
    fun disconnect(){
        chanel?.let{
        if(it.isConnected()){
            it.disconnect()
        }
    }

        session?.let {
            if(it.isConnected()){
                it.disconnect()
            }
        }
    }
    fun tryConnect() : Boolean{
        var br: Boolean = false
        if((session != null) &&(chanel != null)){
            if((session?.isConnected() == true) && (chanel?.isConnected() == true)){
                br = true
            }
        }
        return true
    }
    fun downloadFile(){
        val localFilePath = File(Environment.getExternalStorageDirectory(), "image.jpg").absolutePath
        try{
            chanel?.get("image.jpg")
            Log.i("My class", chanel?.get("image.jpg", localFilePath).toString())

        }
        catch (e: JSchException){

            e.printStackTrace()
        }
    }
    fun uploadFile(file: InputStream, fileName:String){
        chanel?.cd("/uploads/")
        chanel?.put(file, fileName+".jpg")

    }

}