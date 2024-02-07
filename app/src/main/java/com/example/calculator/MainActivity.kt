package com.example.calculator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var button : Button
    private var menuPage: Int = 1
    private lateinit var cameraExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        //Permission can be better
        if(!hasRequeiredPermission()){
            ActivityCompat.requestPermissions(
                this, CAMERAX_RERMISSIONS,0
            )
        }

        setContentView(R.layout.activity_main)
        button = findViewById(R.id.buttonCalculate)
        button.setOnClickListener{
            culculate()
        }

        //fuck yyyyyyyeeeeeeeeeaaaaaaaaaa, i am so fucking hard right now, fucking menu
        this.setSupportActionBar(findViewById(R.id.materialToolbar))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.item1->{
                if(menuPage==2) {
                    setContentView(R.layout.activity_main)
                    this.setSupportActionBar(findViewById(R.id.materialToolbar))
                    menuPage=1
                    button = findViewById(R.id.buttonCalculate)
                    button.setOnClickListener{
                        culculate()
                    }
                }
                true
            }
            R.id.item2->{
                if(menuPage==1) {
                    setContentView(R.layout.item2)
                    this.setSupportActionBar(findViewById(R.id.materialToolbar))

                    //camara
                    startCamera()
                    menuPage=2
                }
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }

    private fun culculate(){
        val text1 : EditText = findViewById(R.id.editTextNumber1)
        val text2 : EditText = findViewById(R.id.editTextNumber2)
        val number1=text1.text.toString().toDouble()
        val number2=text2.text.toString().toDouble()
        val rez : TextView = findViewById(R.id.textViewResult)
        val oper : Spinner =findViewById(R.id.spinnerOperation);
        val result = when (oper.selectedItem as String) {
            "Add" -> number1 + number2
            "Subtract" -> number1 - number2
            "Multiply" -> number1 * number2
            "Divide" -> if (number2 != 0.0) number1 / number2 else Double.NaN
            else -> Double.NaN
        }
        rez.text = "Result: $result"
    }

    private fun startCamera(){
        val processCameraProvider = ProcessCameraProvider.getInstance(this)

        processCameraProvider.addListener({
            try {
                val cameraProvider = processCameraProvider.get()
                val previewUseCase = androidx.camera.core.Preview.Builder().build()
                val previewView =findViewById(R.id.PreviewView)as PreviewView
                previewUseCase.setSurfaceProvider(previewView.surfaceProvider)

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this,CameraSelector.DEFAULT_BACK_CAMERA,previewUseCase)
            }catch (e:Exception){
                Log.d("ERROR",e.message.toString())
           }

        },ContextCompat.getMainExecutor(this))

    }

    private fun hasRequeiredPermission():Boolean{
        return CAMERAX_RERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            )==PackageManager.PERMISSION_GRANTED
        }
    }
    companion object {
        private val CAMERAX_RERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

    }
}