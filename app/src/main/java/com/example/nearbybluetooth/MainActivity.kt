package com.example.nearbybluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       /* val customContract = object : ActivityResultContract<Unit,Unit>(){
            override fun createIntent(context: Context, input: Unit): Intent {
                return Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Unit? {
                if (resultCode != Activity.RESULT_OK) {
                    return null
                }
                return intent?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)            }
        }*/
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "null bluetooth", Toast.LENGTH_SHORT).show()
        }
        if (bluetoothAdapter?.isEnabled == false) {
            //val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(receiver, filter)
            bluetoothAdapter.startDiscovery()
           // startActivityForResult(enableBtIntent, 10)
        }


        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { isGrantedMap: Map<String, Boolean> ->
                val v = isGrantedMap.values
                val isGranted = !v.contains(false)
                if (isGranted) {
                    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                    registerReceiver(receiver, filter)
                    Log.d("ahmad", "onCreate: granted1")
                } else {
                    Toast.makeText(this, "Permissions not granted!!!", Toast.LENGTH_SHORT).show()
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) == PackageManager.PERMISSION_GRANTED -> {
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(receiver, filter)
                Log.d("ahmad", "onCreate: granted2")
            }
            /*shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_ADMIN) -> {

            }*/
            else -> {
                Log.d("ahmad", "onCreate: requestPermissionLauncher")
                requestPermissionLauncher.launch(
                    arrayOf(
                        //Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                       // Manifest.permission.BLUETOOTH_SCAN
                    )
                )
            }
        }
        /*  if (ActivityCompat.checkSelfPermission(
                  this,
                  Manifest.permission.BLUETOOTH_CONNECT
              ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                  this,
                  Manifest.permission.BLUETOOTH_ADMIN
              ) != PackageManager.PERMISSION_GRANTED ||
              ActivityCompat.checkSelfPermission(
                  this,
                  Manifest.permission.ACCESS_COARSE_LOCATION
              ) != PackageManager.PERMISSION_GRANTED ||
              ActivityCompat.checkSelfPermission(
                  this,
                  Manifest.permission.ACCESS_FINE_LOCATION
              ) != PackageManager.PERMISSION_GRANTED ||
              ActivityCompat.checkSelfPermission(
                  this,
                  Manifest.permission.BLUETOOTH_SCAN
              ) != PackageManager.PERMISSION_GRANTED
          ) {

              ActivityCompat.requestPermissions(
                  this@MainActivity,
                  arrayOf(
                      Manifest.permission.BLUETOOTH_CONNECT,
                      Manifest.permission.BLUETOOTH_ADMIN,
                      Manifest.permission.ACCESS_COARSE_LOCATION,
                      Manifest.permission.ACCESS_FINE_LOCATION,
                      Manifest.permission.BLUETOOTH_SCAN
                  ),
                  100
              )
          }*/


    }


    private val receiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {

                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name


                    val deviceHardwareAddress = device?.address // MAC address
                    val tv = findViewById<TextView>(R.id.tv)
                    tv.text = "$deviceName $deviceHardwareAddress"
                    Log.d("ahmad", "onReceive: " + deviceName + " " + deviceHardwareAddress)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}/*

fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    btnstart = findViewById(R.id.btnstart)
    mListView = findViewById(R.id.listofdevices)
    val mAdapter: ArrayAdapter<*> = ArrayAdapter<String>(this, R.layout.simple_list_item_1)
    mListView.setAdapter(mAdapter)
    txt1 = findViewById(R.id.txt1)
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    setdevicevisible()
    val hasBluetooth: Boolean = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
    if (!hasBluetooth) {
        val dialog: AlertDialog = Builder(this@MainActivity).create()
        dialog.setTitle(getString(R.string.bluetooth_not_available_title))
        dialog.setMessage(getString(R.string.bluetooth_not_available_message))
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
            DialogInterface.OnClickListener { dialog, which -> // Closes the dialog and terminates the activity.
                dialog.dismiss()
                this@MainActivity.finish()
            })
        dialog.setCancelable(false)
        dialog.show()
    }
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            (this as Activity?)!!, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            1
        )
    }

    // If another discovery is in progress, cancels it before starting the new one.
    if (mBluetoothAdapter.isDiscovering()) {
        mBluetoothAdapter.cancelDiscovery()
    }
    mBluetoothAdapter.startDiscovery()
    mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            //Finding devices
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // Add the name and address to an array adapter to show in a ListView
                mAdapter.add(
                    """
                        ${device!!.name}
                        ${device.address}
                        """.trimIndent()
                )
            }
        }
    }
    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    registerReceiver(mReceiver, filter)
}*/