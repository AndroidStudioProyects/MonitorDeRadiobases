package com.example.giovanazzi.monitorderadiobases.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.laboratorio.usbarduino.Funciones.ConexionIP;

/**
 * Created by Diego on 14/05/2015.
 */
public class SmsRecibido extends BroadcastReceiver {
   // Context contexto;
   static final String TAG = "USB_ARDUINO";

    int alarma=9;// SMS recibido
    String IP;
    int Puerto;
    String  idRadiobase;
    @Override
    public void onReceive(Context context, Intent intent) {
       // this.contexto=context;
      //  Toast.makeText(context,"Sms Recibido",Toast.LENGTH_SHORT).show();

        SharedPreferences mispreferencias=context.getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE);
        IP=mispreferencias.getString("edit_IP", "localhost");

        Puerto=Integer.parseInt(mispreferencias.getString("edit_Port", "9001"));
        idRadiobase  = mispreferencias.getString("IdRadio", "1");
        ConexionIP ClienteTCP=new ConexionIP(IP,Puerto," "+idRadiobase+" "+alarma);
        ClienteTCP.start();
        Log.d(TAG, "IP: " + IP + " Puerto: " + Puerto + " idRadiobase: " + idRadiobase + " alarma: " + alarma);
        Bundle b = intent.getExtras();

        if (b != null) {
            Object[] pdus = (Object[]) b.get("pdus");

            SmsMessage[] mensajes = new SmsMessage[pdus.length];

            for (int i = 0; i < mensajes.length; i++) {
                mensajes[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                String idMensaje = mensajes[i].getOriginatingAddress();
                String textoMensaje = mensajes[i].getMessageBody();

                Log.d("ReceptorSMS", "Remitente: " + idMensaje);
                Log.d("ReceptorSMS", "Mensaje: " + textoMensaje);
            }
        }

    }
}
