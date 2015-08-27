package com.example.giovanazzi.monitorderadiobases.Funciones;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by diego on 27/08/15.
 */
public class KeepAliveThread extends Thread {

    ConexionIP ClienteTCP;
    int IdRadiobase, TiempoSeg;
    String IpPublica;

    boolean BoolKAThread=true;
    int Puerto;

        String  TAG="Movistar";

    public KeepAliveThread(String IpPublica,int IdRadiobase,int Puerto,int TiempoSeg){

        this.IpPublica=IpPublica;
        this.IdRadiobase=IdRadiobase;
        this.Puerto=Puerto;
        this.TiempoSeg=TiempoSeg;

        Log.d("Movistar", "KeepAlive Thread " + BoolKAThread);

        }

    public void detener(){

        BoolKAThread=false;

    }

    public void run(){
            Log.d(TAG, "Inicia los Keeps Alives");
            while(true){

                if(!BoolKAThread){break;}
                //  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());dd-MM-yyyy HH:mm:ss
                String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                Log.d(TAG, "BoolKAThread: " + BoolKAThread);
                try {

                    Thread.sleep(TiempoSeg*1000);
                    ClienteTCP=new ConexionIP(IpPublica,Puerto," "+IdRadiobase+" 1 "+timeStamp);
                    Log.d(TAG, "IpPublica: "+IpPublica +"PuertoKA: "+Puerto+ "TiempoSeg: "+TiempoSeg+"Bool: "+BoolKAThread+"IdRadiobase: "+IdRadiobase);
                    ClienteTCP.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "Terminan los Keeps Alives");
        }

    }


