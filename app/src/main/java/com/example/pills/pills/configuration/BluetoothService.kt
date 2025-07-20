package com.example.pills.pills.configuration

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

class ConnectThread(
    private val context: Context,
    private val device: BluetoothDevice
) : Thread() {

    private var mmSocket: BluetoothSocket? = null

    override fun run() {
        // Verificar permisos
        if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("BluetoothConnection", "Permiso BLUETOOTH_CONNECT no concedido.")
            return
        }

        try {
            mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID)
            bluetoothAdapter?.cancelDiscovery()
            mmSocket?.connect()
            Log.d("BluetoothConnection", "¡Conexión establecida con ${device.name}!")

            // Aquí puedes iniciar ConnectedThread para manejar entrada/salida

        } catch (e: IOException) {
            Log.e("BluetoothConnection", "Fallo en conexión", e)
            try {
                mmSocket?.close()
            } catch (closeException: IOException) {
                Log.e("BluetoothConnection", "No se pudo cerrar el socket", closeException)
            }
        }
    }
}


class AcceptThread(private val context: Context) : Thread() {

    private var mmServerSocket: BluetoothServerSocket? = null

    override fun run() {
        // Verificar permisos
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("AcceptThread", "Permiso BLUETOOTH_CONNECT no concedido")
            return
        }

        try {
            mmServerSocket = bluetoothAdapter
                ?.listenUsingRfcommWithServiceRecord("MyAppName", MY_UUID)

            val socket: BluetoothSocket? = mmServerSocket?.accept() // Bloqueante

            socket?.also {
                Log.d("AcceptThread", "Cliente conectado: ${it.remoteDevice.name}")
                // Aquí puedes manejar entrada/salida en otro hilo
                mmServerSocket?.close()
            }

        } catch (e: SecurityException) {
            Log.e("AcceptThread", "Permisos insuficientes para aceptar conexión", e)
        } catch (e: IOException) {
            Log.e("AcceptThread", "Error aceptando conexión", e)
        }
    }
}
