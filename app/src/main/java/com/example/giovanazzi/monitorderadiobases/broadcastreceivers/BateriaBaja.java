package com.example.giovanazzi.monitorderadiobases.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.laboratorio.usbarduino.Funciones.ConexionIP;

/**
 * Created by Diego on 22/06/2015.
 */
public class BateriaBaja extends BroadcastReceiver {

    Context contexto;
    static final String TAG = "USB_ARDUINO";

    ConexionIP ClienteTCP;
    String IP,idRadiobase;
    int Puerto;
    int alarma=10;// baja bateria

    @Override
    public void onReceive(Context context, Intent intent) {
        this.contexto = context;

        SharedPreferences mispreferencias = contexto.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        IP = mispreferencias.getString("edit_IP", "localhost");

        Puerto = Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));
        idRadiobase  = mispreferencias.getString("IdRadio", "1");
        // Toast.makeText(contexto, "Sistema Energizado", Toast.LENGTH_SHORT).show();
        ClienteTCP = new ConexionIP(IP, Puerto, " "+idRadiobase+" "+alarma);
        ClienteTCP.start();
        Log.d(TAG, "IP: " + IP + " Puerto: " + Puerto + " idRadiobase: " + idRadiobase + " alarma: " + alarma);

    }
}
