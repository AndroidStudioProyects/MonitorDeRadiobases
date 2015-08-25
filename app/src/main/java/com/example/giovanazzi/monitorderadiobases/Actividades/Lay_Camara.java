package com.example.giovanazzi.monitorderadiobases.Actividades;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.giovanazzi.monitorderadiobases.Ftp.ConnectUploadAsync;
import com.example.giovanazzi.monitorderadiobases.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * Created by Diego on 25/06/2015.
 */
public class Lay_Camara extends Activity{

    File ImagenFile;


    Boolean isRecording = false;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    Camera.Parameters parameters;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private int calidadFoto = 90;

    /// FTP////////////
    ConnectUploadAsync cliente;

    ////// COMPONENTES DE XML +++///////////////

    private Button btn_Video,btn_Foto,btn_Enviar;
    private FrameLayout preview;
    private SurfaceView mPreview;
    static final  String  TAG ="MONITOR RADIOBASE";
    public ProgressBar progressBar;
    public TextView text_BytesFTP;

    ////// COMPONENTES DE XML ------///////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_camara);
        LevantarXML();
        Botones();
        CAMARA_ON();
        Log.d(TAG,"Termino OnCreate");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Termino OnResume");
        Retardo tiempoFoto=new Retardo();
        tiempoFoto.start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestroy inicio");
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event

        Log.d(TAG,"Termino OnDestroy");
    }

    private void Botones() {
        btn_Foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SacarFoto mSacarFoto=new SacarFoto();
                mSacarFoto.start();

               /* mCamera.takePicture(null, null, mPicture);
                finish();
                Log.d(TAG, "Boton de Foto");
                EnviarFTP();*/

            }
        });

        btn_Enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EnviarFTP();

            }
        });

        btn_Video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filmacion();
            }
        });
        Log.d(TAG,"Termino Botones");
    }

    private void LevantarXML() {

        btn_Video=(Button)findViewById(R.id.btn_Video);
        btn_Enviar=(Button)findViewById(R.id.btn_Enviar);
        btn_Foto=(Button)findViewById(R.id.btn_Foto);


        preview = (FrameLayout) findViewById(R.id.camera_preview);
        mPreview = (SurfaceView) findViewById(R.id.surfaceView);

        text_BytesFTP=(TextView)findViewById(R.id.text_BytesFTP);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        Log.d(TAG,"Termino LevantarXML");

    }

    private void CAMARA_ON() {
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getApplicationContext(), mCamera);
        preview.addView(mPreview);
        Log.d(TAG,"Termino CAMARA_ON");
    }

    public void Filmacion() {

        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

         //   EnviarFTP();
            // inform the user that recording has stopped
            btn_Video.setText("Rec");
            isRecording = false;
            Log.d(TAG, "Filmacion Detenida");

        } else {
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording

                mMediaRecorder.start();
                Log.d(TAG, "Filmacion Comenzada");

                // inform the user that recording has started
                btn_Video.setText("Stop");
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                Log.d(TAG, "Se libero el MadiaRecorder");

                // inform user
            }
        }


    }


    public void PARAMETROS() {

        parameters =mCamera.getParameters();
        if(parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
        {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        }
        if(parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        parameters.setRotation(90);
        parameters.setJpegQuality(calidadFoto);
        //  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //  parameters.setGpsTimestamp(Long.parseLong(timeStamp));
        // parameters.setZoom(4);

        parameters.setVideoStabilization(true);


        mCamera.setParameters(parameters);
        Log.d(TAG,"Parametros de la Camara Cargados");
    }



    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
         //   Log.d(TAG,"Camara Abierta");
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
       //     Log.d(TAG,"Camara Cerrada");
        }
        return c; // returns null if camera is unavailable
    }

    /** A basic Camera preview class */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);


            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here
            mCamera.setDisplayOrientation(90);

            PARAMETROS();

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");// +  e.getMessage());
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

                //EnviarFTP();


            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
            Log.d(TAG, "Camara Liberada");
        }
    }

    private boolean prepareVideoRecorder(){


        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
            Log.d("prepareVideoRecorder", " mMediaRecorder.prepare()");
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }
    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use

        }
    }

    /////////////////////////////////////////////////////////////

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Radiobases");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
     /*    String IP = edit_IP.getText().toString();
       String idRadiobase = edit_IdRadio.getText().toString();
        int Puerto=Integer.parseInt(edit_Port.getText().toString());
*/
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +"_IMG_"+ timeStamp + ".jpg");
                    //"ID_"+edit_IdRadio.getText().toString()+"_IMG_"+ timeStamp + ".jpg");


            //   ConexionIP mensaje=new ConexionIP(IP,Puerto," "+idRadiobase+" 11");
            //    mensaje.start();

        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +"_VID_"+ timeStamp + ".mp4");
                    //"ID_"+edit_IdRadio.getText().toString()+"_VID_"+ timeStamp + ".mp4");
            //     ConexionIP mensaje=new ConexionIP(IP,Puerto," "+idRadiobase+" 12");
            //   mensaje.start();

        } else {
            return null;
        }

        return mediaFile;
    }

    //////////////////////////--- FOTO Y VIDEO ----//////////////////////

    //////////// ftp ++ ///////////////////


    private void EnviarFTP(){


        String ip="idirect.dlinkddns.com";//edit_IP.getText().toString();
        String ID_Radio="1";
        String userName="idirect";
        String pass="IDIRECT";

        cliente = new ConnectUploadAsync(getApplicationContext(),ip,userName,pass,Lay_Camara.this,ID_Radio);
        cliente.execute();

        finish();

    }

    ////////////FTP---(//////////////////////


   public class SacarFoto extends Thread{

       public void run(){

           mCamera.takePicture(null, null, mPicture);
           finish();
           Log.d(TAG, "Boton de Foto");
           EnviarFTP();
 }

    }


    public class Retardo extends Thread{

        public void run(){

            try {
                Thread.sleep(500);
                mCamera.takePicture(null, null, mPicture);
                Thread.sleep(200);
                EnviarFTP();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
