package com.example.pills.homePage.presentation.BLE


import android.Manifest
import android.app.Application
import androidx.compose.runtime.Composable
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.*


@Composable
fun BLEScreen() {

    val viewModel: BLEViewModel = koinViewModel()
    val context = LocalContext.current
    val devices = remember { mutableStateListOf<BluetoothDevice>() }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                viewModel.bleManager.scanDevices { device ->
                    if (!devices.contains(device)) {
                        devices.add(device)
                    }
                }
            }
        } else {
            viewModel.bleManager.scanDevices { device ->
                if (!devices.contains(device)) {
                    devices.add(device)
                }
            }
        }
    }

    Column {
        Text("Dispositivos encontrados:")
        devices.forEach { device ->
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        viewModel.connectToDevice(device)
                    } else {
                        Toast.makeText(context, "Falta permiso BLUETOOTH_CONNECT", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    viewModel.connectToDevice(device)
                }
            }) {
                Text("${device.name ?: "Sin nombre"} - ${device.address}")
            }
        }
    }
}


class BLEViewModel(application: Application) : AndroidViewModel(application) {
    val bleManager = BLEManager(application)

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice) {
        bleManager.connectToDevice(device, {
            Log.d("BLE", "Conectado!")
        }, { data ->
            Log.d("BLE", "Datos recibidos: ${data.decodeToString()}")
        })
    }
}
class BLEManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private var bluetoothGatt: BluetoothGatt? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun scanDevices(onDeviceFound: (BluetoothDevice) -> Unit) {
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                result.device?.let { onDeviceFound(it) }
            }
        }
        scanner?.startScan(callback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice, onConnected: () -> Unit, onDataReceived: (ByteArray) -> Unit) {
        bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                    onConnected()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                gatt.services.forEach { service ->
                    service.characteristics.forEach { characteristic ->
                        Log.d("BLE", "Service: ${service.uuid}, Characteristic: ${characteristic.uuid}")
                    }
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                onDataReceived(characteristic.value)
            }
        })
    }

    @androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    fun sendData(serviceUUID: UUID, characteristicUUID: UUID, data: ByteArray) {
        bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let{ characteristic ->
            characteristic.value = data
            bluetoothGatt?.writeCharacteristic(characteristic)
        }
    }
}
