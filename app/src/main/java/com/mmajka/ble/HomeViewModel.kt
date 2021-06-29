package com.mmajka.ble

import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var scanResult = MutableLiveData<List<ScanResult>>()
    private val temp = mutableListOf<ScanResult>()

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {

            with(result.device) {
                Log.i("ScanCallback", "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
            }
            val indexQuery = temp.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1){
                temp[indexQuery] = result
            }else{
                temp.add(result)
                scanResult.postValue(temp)
            }
        }
    }

    fun isBluetoothEnabled(): Boolean{
        return bluetoothAdapter.isEnabled
    }

    fun checkPermisions(activity: Activity){
        val location = ContextCompat.checkSelfPermission(activity.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)

        if(location == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }

    fun startScan(){
        bleScanner.startScan(null, scanSettings, scanCallback)
    }

    fun stopScan(){
        bleScanner.stopScan(scanCallback)
    }
}