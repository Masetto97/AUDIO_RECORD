import 'package:learning_music_project/pitch_tracking/audioParams.dart';

class PitchTracking {

    int timeIndex;
    int inputIndex; // pos in original C++ algorithm
    int frameSize;
    int overlap; 

    List<double> inputBuf;
    List<double> inputBuf2;
    List<double> processBuf; 

    PitchTracking(int frameArg, int overlapArg){
      timeIndex = 0; // Used to control the position in inputbuf & processBuf
      inputIndex = 0; // Used to control the xurrent position in the input recording
      overlap = 1;
      setFrameSize(frameArg);
      setOverlap(overlapArg);
      // Initially filled with nulls 
      inputBuf = new List<double>(frameSize);
      inputBuf2 = new List<double>(frameSize);
      processBuf = new List<double>(2*frameSize); // double because they are complex number (REAL & IMAGINARY parts)
    }

    void setFrameSize(int frame){
      if(!((frame==128)|(frame==256)|(frame==512)|(frame==1024)|(frame==2048)))
        frame = AudioParams.DEFFRAMESIZE;
      frameSize = frame;
    }

    void setOverlap(int lap){
      if(!((lap==1)|(lap==2)|(lap==4)|(lap==8)))
        lap = AudioParams.DEFOVERLAP;
      overlap = lap;
    }

    int slice(List<num> input, int size){ // input can be very large...; size:  block' size for analysis 
      int mask = frameSize - 1;
      int outputIndex = 0;

    
      // call analysis function when it is time
      if( (timeIndex & (frameSize ~/ overlap - 1)) == 0 ) analyzeFrame();
    
      while(size>0) {
        inputBuf[timeIndex] = input[inputIndex];
        inputIndex++;
        //   out[outputIndex++] = processbuf[timeindex++];
        timeIndex++;
        timeIndex &= mask;
        size--;
      }
      return inputIndex;
    }

    void analyzeFrame(){
      print("ENTRA");
    }    
}