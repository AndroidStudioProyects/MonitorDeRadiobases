package com.example.giovanazzi.monitorderadiobases.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.giovanazzi.monitorderadiobases.Actividades.MainActivity;
import com.example.giovanazzi.monitorderadiobases.Actividades.Pantalla_Principal;
import com.example.giovanazzi.monitorderadiobases.Funciones.ConexionIP;



/**
 * Created by Diego on 30/04/2015.
 */
public class Booteo extends BroadcastReceiver {
    static final String TAG = "Movistar";

    int alarma=7;
    int Puerto;
    String IP;
    String  idRadiobase;
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intento= new Intent(context,Pantalla_Principal.class);
        intento.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intento);

        SharedPreferences mispreferencias=context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);

        IP = mispreferencias.getString("edit_IP", "localhost");
        idRadiobase = mispreferencias.getString("IdRadio","1");
        Puerto=Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        ConexionIP ClienteTCP=new ConexionIP(IP,Puerto," "+idRadiobase+" "+alarma);
        ClienteTCP.start();
        Log.d(TAG, "IP: " + IP + " Puerto: " + Puerto + " idRadiobase: " + idRadiobase + " alarma: " + alarma);
    }
}
