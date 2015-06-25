package com.example.giovanazzi.monitorderadiobases.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.laboratorio.usbarduino.Funciones.ConexionIP;

/**
 * Created by Diego on 30/04/2015.
 */
public class SistemaEnBateria extends BroadcastReceiver {

int alarma=4;// red electrica coratada ,, sistema en baterias
    static final String TAG = "USB_ARDUINO";
    String IP;
    int Puerto;
    String  idRadiobase;
    @Override
    public void onReceive(Context context, Intent intent) {



        SharedPreferences mispreferencias=context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        IP=mispreferencias.getString("edit_IP", "localhost");

        Puerto = Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));

        idRadiobase  = mispreferencias.getString("IdRadio", "1");

      //  Toast.makeText(context, "Sistema Sobre Baterias", Toast.LENGTH_SHORT).show();
    /*   SmsManager manager = SmsManager.getDefault();
        PendingIntent sentIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);
        PendingIntent deliveryIntent = PendingIntent.getActivity(contexto, 0, new Intent(), 0);*/
        //   manager.sendTextMessage("2235776581", null, "Sistema en Baterias", sentIntent, deliveryIntent);


        Log.d(TAG,"IP: "+IP+" Puerto: "+Puerto+" idRadiobase: "+idRadiobase+" alarma: "+alarma+" bateria baja!!! ");


        ConexionIP ClienteTCP =new ConexionIP(IP,Puerto," "+idRadiobase+" "+alarma);
        ClienteTCP.start();


    }


}
