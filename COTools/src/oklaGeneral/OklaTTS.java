package oklaGeneral;

import processing.core.PApplet;
//import spraak.oklahoma.spraak;
import guru.ttslib.TTS;

public class OklaTTS implements Runnable{
	TTS guruTTS;
//	spraak quadmoreTTS;
	int ttsEngine=0; //0=guru, 1=quadmore
	String tekst="";
	boolean isTalking = false;
	
	
	Thread talkThread;

	public OklaTTS(int ttsEngine) {
		super();
		
		this.ttsEngine = ttsEngine;

		if (ttsEngine==0) {
			guruTTS = new TTS();
//			guruTTS.voice.setVolume((float) 0.6);
		}
		else if (ttsEngine==1) {
	//		quadmoreTTS=new spraak();
		}
		else {
			PApplet.println("No tts engine defined");
		}
		talkThread = new Thread(this);
		talkThread.start();
	}
	
	public OklaTTS(int ttsEngine, String voiceName) {
		super();
		this.ttsEngine = ttsEngine;

		if (ttsEngine==0) {
			guruTTS = new TTS(voiceName);
		}
		else if (ttsEngine==1) {
	//		quadmoreTTS=new spraak();
	//		quadmoreTTS.setVoiceToken(voiceName);
		}
		else {
			PApplet.println("No tts engine defined");
		}
		talkThread = new Thread(this);
		talkThread.start();
	}


	public void speak(String zeg) {
		tekst = zeg;
		isTalking = true;
		if (ttsEngine==0) {
			//guruTTS.speak("");
		}
		else if (ttsEngine==1) {
			//quadmoreTTS.talk("");
		}
		else {
			PApplet.println("No tts engine defined");
		}
	}
	
	public void speakNT(String zeg) {
		if (ttsEngine==0) {
			guruTTS.speak(zeg);
		}
		else if (ttsEngine==1) {
	//		quadmoreTTS.talk(zeg);
		}
		else {
			PApplet.println("No tts engine defined");
		}
	}
	
	public void talkNt(String zeg) {
		speakNT(zeg);
	}
	
	public void talk(String zeg) {
		speak(zeg);
	}
	

	public void run() {
		while (true) {
			if (tekst != "") {
				if (ttsEngine==0) {
					guruTTS.speak(tekst);
				}
				else if (ttsEngine==1) {
//					quadmoreTTS.talk(tekst);
				}
				else {
					PApplet.println("No tts engine defined");
				}
				isTalking = false;
				tekst = "";
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isTalking() {
		return isTalking;
	}

}

