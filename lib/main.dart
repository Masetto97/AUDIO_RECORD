import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:learning_music_project/pitch_tracking/pitchTracking.dart';
import 'package:flutter_sound/flutter_sound.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Learning Music in a Natural way',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  
  MyHomePage({Key key}) : super(key: key);

  @override
  _MyHomePageState createState() { 
    return _MyHomePageState();
  }
}

class _MyHomePageState extends State<MyHomePage> {

  //Attributes:
  PitchTracking pitchTracking;

  static const methodChannel_1min =
      const MethodChannel('es.uclm.mami.periodic_1min');

  static const methodChannel_record_start =
      const MethodChannel('es.uclm.mami.record/start');

  static const methodChannel_record_stop =
      const MethodChannel('es.uclm.mami.record/stop');  

  static const methodChannel_last_recording =
      const MethodChannel('es.uclm.mami.record/last_recording');        

  bool recording = false;
  
  _MyHomePageState() {

    print("I'm ready for periodic calls!");
    FlutterSound flutterSound = new FlutterSound();
    
    methodChannel_1min.setMethodCallHandler((call) {
       
      print(call.method); 
    });
    methodChannel_last_recording.setMethodCallHandler((call) {
      print('dentro');
      List<num> input = call.arguments as List<num>; //recording
      print(input.length.toString() +" este es");

      for(int i=0;i<input.length;i++){

        print(input[i]);
      }

    }); 
  }

  // one async function to execute in flutter part, which will invoke method (Service) in
  // Android or iOS native code.
  static void _startRecordingService() async {
    // We have to handle Platform exception if the platform does not support the platform
    // API (such as when running in a simulator). So add invoking method in try catch

    try {
      await methodChannel_record_start.invokeMethod('startRecordingService');
    } on PlatformException catch (e) {
      print(e.message);
    }
  }

  static void _stopRecordingService() async {
    try {
      await methodChannel_record_stop.invokeMethod('stopRecordingService');
    } on PlatformException catch (e) {
      print(e.message);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      floatingActionButton: new FloatingActionButton(
        onPressed: () {
          if( recording == false ){
            print("Recording it!");
            setState((){
              recording = true;
            });
            _startRecordingService();
          } else{
            setState((){
              recording = false;
            });
            _stopRecordingService();
            print("Stop recording it!");
          }
        },
        child: recording ? new Icon(Icons.stop): new Icon(Icons.mic),
        backgroundColor: recording ? Colors.red : Colors.blue,
        tooltip: "Recording...",
      ),
    );
  }
}
