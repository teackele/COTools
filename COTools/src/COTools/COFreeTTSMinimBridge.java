package COTools;


import javax.sound.sampled.AudioFormat;
import ddf.minim.*;
//import ddf.minim.spi.AudioStream;

public class COFreeTTSMinimBridge implements com.sun.speech.freetts.audio.AudioPlayer, ddf.minim.AudioSignal {



	public COFreeTTSMinimBridge() {
		System.setProperty("com.sun.speech.freetts.voice.defaultAudioPlayer","COTools.COFreeTTSMinimBridge");
	}

	@Override
	public void begin(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean drain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean end() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AudioFormat getAudioFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVolume() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetTime() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAudioFormat(AudioFormat arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVolume(float arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showMetrics() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startFirstSampleTimer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean write(byte[] arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean write(byte[] arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	public void generate(float[] samp) {
		/*
	    float range = map(mouseX, 0, width, 0, 1);
	    float peaks = map(mouseY, 0, height, 1, 20);
	    float inter = float(samp.length) / peaks;
	    for ( int i = 0; i < samp.length; i += inter )
	    {
	      for ( int j = 0; j < inter && (i+j) < samp.length; j++ )
	      {
	        samp[i + j] = map(j, 0, inter, -range, range);
	      }
	    }
	    */
	  }
	  
	  // this is a stricly mono signal
	public void generate(float[] left, float[] right)  {
		generate(left);
	    generate(right);
	}
}
