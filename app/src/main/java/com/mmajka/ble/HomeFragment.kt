package com.mmajka.ble

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mmajka.ble.databinding.HomeFragmentBinding

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2

class HomeFragment : Fragment(), DeviceListener {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: HomeFragmentBinding

    private var isScanning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        if(!viewModel.isBluetoothEnabled()){
            promptEnableBluetooth()
        }

        binding.scan.setOnClickListener {
            if (!isScanning){
                binding.scan.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_cancel))
                startScan()
            }else{
                binding.scan.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_scan))
                stopScan()
            }
        }

        viewModel.scanResult.observe(viewLifecycleOwner, Observer {
            setupRecyclerView(it)
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
        checkPermisions()
    }

    override fun onDeviceClick(
        scanResult: ScanResult,
        position: Int,
        button: FloatingActionButton
    ) {
        TODO("Not yet implemented")
    }

    private fun promptEnableBluetooth(){
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
    }

    private fun checkPermisions(){
        viewModel.checkPermisions(requireActivity())
    }


    private fun startScan() {
        isScanning = true
        viewModel.startScan()
    }

    private fun stopScan(){
        isScanning = false
        viewModel.stopScan()
    }

    private fun setupRecyclerView(items: List<ScanResult>){
        binding.devicesRv.adapter = ScanResultAdapter(items, this)
        binding.devicesRv.layoutManager = LinearLayoutManager(context)
    }


}