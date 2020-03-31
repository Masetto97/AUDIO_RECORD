package com.example.learning_music_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

  public class MyReceiver extends BroadcastReceiver {
    @Override
      public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
          case "recordingInByteArray":
            byte[] recordingInByteArray = intent.getByteArrayExtra("recordingInByteArray");
            MainActivity.sendtoFlutterRecording(recordingInByteArray);
            break;
          case "intent1Min":
            MainActivity.periodicCallToFlutter();
            break;
        }
    }
  }
