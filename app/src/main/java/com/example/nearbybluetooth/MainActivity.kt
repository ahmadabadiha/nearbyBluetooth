package com.example.nearbybluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.nearbybluetooth.databinding.ActivityMainBinding

@RequiresApi(Build.VERSION_CODES.N)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: BroadcastReceiver
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkLocationPermissions()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bluetoothScanPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) startDiscovery()
                else Toast.makeText(this, "Required permission was not granted!", Toast.LENGTH_SHORT).show()
            }
            bluetoothScanPermissionRequest.launch(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            startDiscovery()
        }
    }

    private fun checkLocationPermissions() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Toast.makeText(this, "Precise location access granted.", Toast.LENGTH_SHORT).show()

                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Toast.makeText(this, "Only approximate location access granted.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "No location access granted.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission") // permission must be checked when android version is more than "S" which is done before in code
    private fun startDiscovery() {

        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        if (!bluetoothAdapter.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activityResultLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    showNearbyBluetooth()

                } else Toast.makeText(this, "Bluetooth is off!", Toast.LENGTH_SHORT).show()
            }
            activityResultLauncher.launch(intent)
        } else showNearbyBluetooth()
    }

    private fun showNearbyBluetooth() {
        val list = mutableListOf<String>()
        val recyclerAdapter = RecyclerAdapter()
        binding.recyclerView.adapter = recyclerAdapter
        receiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                            } else {
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            }
                        val deviceName: String
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            deviceName = if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.BLUETOOTH_CONNECT
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(
                                    this@MainActivity,
                                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                                    100
                                )
                                return
                            } else {
                                device?.name ?: ""
                            }
                        } else {
                            deviceName = device?.name ?: ""
                        }

                        val deviceHardwareAddress = device?.address
                        list.add("device name: $deviceName\nMAC address: $deviceHardwareAddress")
                        recyclerAdapter.submitList(list)
                    }
                }
            }
        }
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
        bluetoothAdapter.startDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}