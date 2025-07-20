package com.example.pills.pills.configuration

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.UUID



class BluetoothViewModel(
    private val context: Context
) : ViewModel() {

    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager)?.adapter

    private val _connectionState = MutableStateFlow("Desconectado")
    val connectionState: StateFlow<String> = _connectionState

    private var scanReceiver: BroadcastReceiver? = null

    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun startScan(devicesList: MutableList<BluetoothDevice>) {
        Log.d("BluetoothViewModel", "Intentando iniciar escaneo...")

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.e("BluetoothViewModel", "Bluetooth no disponible o desactivado")
            return
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e("BluetoothViewModel", "Permisos no otorgados")
            return
        }

        scanReceiver = object : BroadcastReceiver() {
            @RequiresPermission(
                allOf = [
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ]
            )
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        Log.d("BluetoothViewModel", "Dispositivo encontrado: ${it.name} - ${it.address}")
                        if (!devicesList.contains(it)) {
                            devicesList.add(it)
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(scanReceiver, filter)

        val started = bluetoothAdapter.startDiscovery()
        Log.d("BluetoothViewModel", "Discovery iniciado: $started")
    }

    @RequiresPermission(
        allOf = [
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT
        ]
    )
    fun connectToDevice(device: BluetoothDevice) {
        _connectionState.value = "Intentando conectar a ${device.name ?: device.address}"
        val thread = object : Thread() {
            @RequiresPermission(
                allOf = [
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                ]
            )
            override fun run() {
                try {
                    val socket = device.createRfcommSocketToServiceRecord(uuid)
                    bluetoothAdapter?.cancelDiscovery()
                    socket.connect()

                    _connectionState.value = "¡Conectado a ${device.name ?: device.address}!"

                    // Aquí podrías iniciar un hilo para manejar la comunicación
                    // manageConnectedSocket(socket)

                } catch (e: IOException) {
                    Log.e("BluetoothService", "Error conectando", e)
                    _connectionState.value = "Error al conectar con ${device.name ?: device.address}"
                }
            }
        }
        thread.start()
    }

    override fun onCleared() {
        super.onCleared()
        try {
            context.unregisterReceiver(scanReceiver)
        } catch (e: Exception) {
            Log.w("BluetoothViewModel", "Receiver ya removido o no registrado")
        }
        scanReceiver = null
    }
}

