package com.example.giovanazzi.monitorderadiobases.Ftp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.example.giovanazzi.monitorderadiobases.Actividades.Pantalla_Principal;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Created by Diego on 26/05/2015.
 */
public class ConnectUploadAsync extends AsyncTask <Void,Integer,Boolean> {

    FTPClient mFtpClient;
    Context contexto;
    boolean status = false;
    boolean completed;
    boolean done;
    String Errores=null;
    File fileLast;
    String ip, userName,pass;
    Pantalla_Principal ac;
    int IdRadiobase;
    final static String TAG="Movistar";


    public ConnectUploadAsync(Context contexto, String ip, String userName, String pass, Pantalla_Principal ac, int IdRadiobase){

        this.contexto=contexto;
        this.ip=ip;
        this.userName=userName;
        this.pass=pass;
        this.IdRadiobase=IdRadiobase;
        this.ac=ac;


    }



    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            mFtpClient = new FTPClient();
            mFtpClient.setConnectTimeout(10 * 1000);
            mFtpClient.connect(InetAddress.getByName(ip));
            status = mFtpClient.login(userName, pass);



          //  Log.d(TAG, "Se conecto: " + String.valueOf(status));
            if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                mFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFtpClient.enterLocalPassiveMode();

                FTPFile[] mFileArray = mFtpClient.listFiles();

              //  Log.d("Api FTP", "Numeros de archivos" + String.valueOf(mFileArray.length));

                for(int i=0;i<mFileArray.length;i++){
            //        Log.d(TAG,"nombre archivo"+ mFileArray[i].getName());
                  }

                Log.d(TAG, "IP Server:" + String.valueOf(mFtpClient.getRemoteAddress()));



                // APPROACH #2: uploads second file using an OutputStream

                //Defino la ruta donde busco los ficheros
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Radiobases");
                //Creo el array de tipo File con el contenido de la carpeta
                File[] files = f.listFiles();

                for (int i = 0; i < files.length; i++)

                {
                    //Sacamos del array files un fichero
                    File file = files[i];

                    //Si es directorio...
               //    System.out.println("[" + i + "]" + file.getName());
                }

        try {  fileLast = files[files.length-1];
            Errores=null;

            float tamaño= (float)fileLast.length()/1024;
            System.out.println("Longitud en kbites" +tamaño+"kb");
            String NomFileaTransferir=fileLast.getName();
            System.out.println("fileLast: " +NomFileaTransferir+" Tamaño: "+(int)tamaño+"kb");
            File secondLocalFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Radiobases/"+NomFileaTransferir);




            String secondRemoteFile =NomFileaTransferir;
            InputStream inputStream = new FileInputStream(secondLocalFile);

            System.out.println("Start uploading second file");

            boolean created =  mFtpClient.makeDirectory("ID_"+IdRadiobase);

            if (created) {

                // the directory is created, everything is going well
         //       Log.d(TAG, "Directorio creado");

            } else {
           //     Log.d(TAG, "No se pudo Crear directorio");

                // something unexpected happened...
            }


            OutputStream outputStream = mFtpClient.storeFileStream("ID_"+IdRadiobase+"/"+secondRemoteFile);

            int ancho = (int) secondLocalFile.length();

            byte[] bytesIn = new byte[4096];
            float cantidad =(float)ancho/4096;
            int cant=(int)cantidad+1;
            int read = 0;
            int c=0;

            while ((read = inputStream.read(bytesIn)) != -1) {
                outputStream.write(bytesIn, 0, read);
                c++;
                float porcentage=(float)((c*100)/cant);
                publishProgress((int)porcentage);


            }

            inputStream.close();
            outputStream.close();

            completed = mFtpClient.completePendingCommand();
            if (completed) {

                fileLast.delete();
                mFtpClient.disconnect();

            }


        }catch (Exception e){
            Errores="No hay Archivos";
            mFtpClient.disconnect();
        }

                ////////////////////////////

            }
        } catch (SocketException e) {
            e.printStackTrace();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return completed;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
       super.onProgressUpdate(values);

        ac.progressBar_P.setProgress(values[0]);
      ac.text_BytesFTP_P.setText("%" + values[0]);

    }

    @Override
    protected void onPostExecute(Boolean o) {
        super.onPostExecute(o);
        if(o){
            Toast.makeText(contexto,"archivo trasferido !!!"+o, Toast.LENGTH_SHORT).show();
            ac.progressBar_P.setProgress(0);

           ac.text_BytesFTP_P.setText("Transmision Finalizada");

        }
        else{
         Toast.makeText(contexto,"no se pudo transmitir"+o,Toast.LENGTH_SHORT).show();
                }

    }



}
