package com.example.learning_music_project;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RecordService extends Service {
    private static final String TAG = "RecordingService";
    private boolean isRunning = false;
    private Looper looper;
    private MyServiceHandler myServiceHandler;

    @Override
    public void onCreate() {
        HandlerThread handlerthread = new HandlerThread("RecordingThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerthread.start();
        looper = handlerthread.getLooper();
        myServiceHandler = new MyServiceHandler(looper);
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = myServiceHandler.obtainMessage();
        msg.arg1 = startId;
        myServiceHandler.sendMessage(msg);
        Toast.makeText(this, "Recording has started.", Toast.LENGTH_SHORT).show();
        //If service is killed while starting, it restarts.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Toast.makeText(this, "Recording has finished.", Toast.LENGTH_SHORT).show();
    }

    private final class MyServiceHandler extends Handler {
        public MyServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
			
			
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ParcelFileDescriptor[] descriptors;
			
			
			
			
			
		
			
            int bufferSize = AudioRecord.getMinBufferSize(16384,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_8BIT);

            if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                bufferSize = 2048;
            }
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    16384,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_8BIT,
                    bufferSize);

            byte[] audioBuffer = new byte[bufferSize];



            if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.e("RECORD_SERVICE", "Audio Record can't initialize!");
                return;
            }

            record.startRecording();

            Log.v("RECORD_SERVICE", "Start recording");

           /* long shortsRead = 0;
            while (isRunning) {
                int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                shortsRead += numberOfShort;
            }*/
			
			
			synchronized (this) {
					int read;
                    while ((read = record.read(audioBuffer, 0, audioBuffer.length)) != -1) {
                        byteArrayOutputStream.write(audioBuffer, 0, read);
                        if (!isRunning) {
                            break;
                        }
                   }
            } // end synchronized
			//byteArrayOutputStream.flush();


            record.stop();
            record.release();

           // Log.v("RECORD_SERVICE", String.format("Recording stopped. Samples read: %d", shortsRead));

			byte[] recordingInByteArray = byteArrayOutputStream.toByteArray();
			
			Intent intentRecording = new Intent();
            intentRecording.setAction("recordingInByteArray");
            intentRecording.putExtra("recordingInByteArray",recordingInByteArray);
            sendBroadcast(intentRecording);
			
			MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(new MyAudioSource(recordingInByteArray));
            mediaPlayer.prepare();
            mediaPlayer.start();


			
			
            stopSelfResult(msg.arg1);
        }
    }
}