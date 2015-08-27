package com.example.giovanazzi.monitorderadiobases.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.giovanazzi.monitorderadiobases.Funciones.ConexionIP;

import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Created by Diego on 09/05/2015.
 */
public class KeepAlive extends Service {
    static ConexionIP ClienteTCP;
    static int IdRadiobase, TiempoSeg,PuertoKA;
    static String IpPublica;
    String TAG = "Usb Arduino";
    static boolean Bool;
    Intent intento;
    Hilo hilito;
    Context contexto;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.intento=intent;

        IpPublica = intento.getExtras().getString("Ip");
        IdRadiobase = intento.getExtras().getInt("Id");
        PuertoKA=intento.getExtras().getInt("PuertoKA");
        Bool = intento.getExtras().getBoolean("bool");
        TiempoSeg = intento.getExtras().getInt("Timer");
        Toast.makeText(getApplicationContext(), "Servicio iniciado: " + Bool, Toast.LENGTH_SHORT).show();

        hilito=new Hilo();
        hilito.start();

      return START_STICKY;
    }


    @Override
    public void onDestroy() {


        Bool = false;

        Toast.makeText(getApplicationContext(), "Servicio detenido: " + Bool, Toast.LENGTH_SHORT).show();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static class Hilo extends Thread {



        @Override
        public void run() {
            while(Bool){
              //  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());dd-MM-yyyy HH:mm:ss
                String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                Log.d("Movistar", "Bool " + Bool);


            try {

                Thread.sleep(TiempoSeg*1000);
                ClienteTCP=new ConexionIP(IpPublica,PuertoKA," "+IdRadiobase+" 1 "+timeStamp);
                Log.d("Movistar", "IpPublica: "+IpPublica +"PuertoKA: "+PuertoKA+ "TiempoSeg: "+TiempoSeg+"Bool: "+Bool+"IdRadiobase: "+IdRadiobase);
                ClienteTCP.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }}
        }
    }
}