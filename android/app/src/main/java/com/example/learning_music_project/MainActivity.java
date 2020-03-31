package com.example.learning_music_project;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.view.FlutterView;

public class MainActivity extends FlutterActivity {

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private static  FlutterView flutterView;
    private static final String CHANNEL_1MIN = "es.uclm.mami.periodic_1min";
    private static final String CHANNEL_RECORD_START = "es.uclm.mami.record/start";
    private static final String CHANNEL_RECORD_STOP = "es.uclm.mami.record/stop";
    private static final String CHANNEL_LAST_RECORDING = "es.uclm.mami.record/last_recording";

    // Control variable to finish recording 
    private RecordService recordingService;

    // MethodChanels
    private static MethodChannel methodChannel1Min;
    private static MethodChannel methodChannelRecordStart;
    private static MethodChannel methodChannelRecordStop;
    private static MethodChannel methodChannelLastRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        flutterView=getFlutterView();
        GeneratedPluginRegistrant.registerWith(this);

        // Permission for recording
        getPermissionToRecordAudio();

        // Registering BroadcastReceiver MyReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("recordingInByteArray");
        registerReceiver(new MyReceiver(),intentFilter);

        // This intent is sent every 1 min for periodic background tasks
        Intent intent1Min = new Intent(this, MyReceiver.class);
        intent1Min.setAction("intent1Min");
        pendingIntent = PendingIntent.getBroadcast(this, 1019662, intent1Min, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pendingIntent);

        // This other intent is for the recording service
        Intent intentRecording = new Intent(MainActivity.this, RecordService.class);

        methodChannel1Min=new MethodChannel(flutterView, CHANNEL_1MIN);

        methodChannelRecordStart=new MethodChannel(flutterView, CHANNEL_RECORD_START);
        methodChannelRecordStart.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                startService(intentRecording);
            }
        });

        methodChannelRecordStop=new MethodChannel(flutterView, CHANNEL_RECORD_STOP);
        methodChannelRecordStop.setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                stopService(intentRecording);
            }
        });

        methodChannelLastRecording = new MethodChannel(flutterView, CHANNEL_LAST_RECORDING);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alarmManager.cancel(pendingIntent);
    }

    static void periodicCallToFlutter(){
        methodChannel1Min.invokeMethod("everyMinute","");
    }

    static void sendtoFlutterRecording(byte[] recordingInByteArray) {
        // Print the Byte array
//        for (byte b : recordingInByteArray) {
//            System.out.println(b);
//        }
        methodChannelLastRecording.invokeMethod("lastRecording",recordingInByteArray);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...)
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }
}