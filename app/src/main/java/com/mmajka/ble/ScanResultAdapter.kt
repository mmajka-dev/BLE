package com.mmajka.ble

import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mmajka.ble.databinding.DeviceCardBinding

class ScanResultAdapter(val items: List<ScanResult>, val deviceListener: DeviceListener): RecyclerView.Adapter<ScanResultViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =  ScanResultViewHolder(
        DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.device_card,
        parent, false)
    )

    override fun onBindViewHolder(holder: ScanResultViewHolder, position: Int) {
        holder.bind(items[position], deviceListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ScanResultViewHolder(binding: DeviceCardBinding): RecyclerView.ViewHolder(binding.root){
    val name = binding.deviceName
    val addr = binding.deviceAddr
    val btn = binding.pair

    fun bind(scanResult: ScanResult, deviceListener: DeviceListener){
        name.text = scanResult.device.name
        addr.text = scanResult.device.address

        itemView.setOnClickListener {
            deviceListener.onDeviceClick(scanResult, adapterPosition, btn)
        }
    }
}

interface DeviceListener{
    fun onDeviceClick(scanResult: ScanResult, position: Int, button: FloatingActionButton)
}