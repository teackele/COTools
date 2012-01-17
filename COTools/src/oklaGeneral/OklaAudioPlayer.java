package oklaGeneral;

import java.util.ArrayList;

import processing.core.PApplet;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;


public class OklaAudioPlayer {
	
	Minim minim;
	PApplet pApplet;
	
	class APlay{
		AudioPlayer audioPlayer = null;
		String name;
		
		public APlay(String fName, String name) {
			super();
			this.name = name;
			audioPlayer=minim.loadFile(fName, 2048);
		}
		
		public APlay(String fName, String name, float gain , float pan) {
			super();
			this.name = name;
			audioPlayer=minim.loadFile(fName, 2048);
			audioPlayer.setPan(pan);
			audioPlayer.setGain(gain);
		}
	}
	
	ArrayList<APlay> aPlayers;
	
	
	public OklaAudioPlayer(PApplet pApplet) {
		super();
		this.pApplet=pApplet;
		minim=new Minim(pApplet);
		aPlayers=new ArrayList<APlay>(); 
	}
	
	public void play(String fileName,String name) {
		APlay ap = new APlay(fileName,name);
		ap.audioPlayer.play();
		aPlayers.add(ap);
	}
	
	public void loop(String fileName,String name) {
		APlay ap = new APlay(fileName,name);
		ap.audioPlayer.loop();
		aPlayers.add(ap);
	}
	
	public void play(String fileName,String name, float gain, float pan) {
		APlay ap = new APlay(fileName,name,gain,pan);
		ap.audioPlayer.play();
		aPlayers.add(ap);
	}
	
	public void loop(String fileName,String name, float gain, float pan) {
		APlay ap = new APlay(fileName,name,gain,pan);
		ap.audioPlayer.loop();
		aPlayers.add(ap);
	}
	
	public AudioPlayer getPlayer(String name) {
		int i = 0;
		while(i < aPlayers.size()) {
			if (aPlayers.get(i).name.equals(name)) return aPlayers.get(i).audioPlayer;
			i++;
		}
		return null;
	}
	
	public void stopAll() {
		println("stopall audio players");
		while (aPlayers.size() > 0) {
			println(aPlayers.get(0).name);
			aPlayers.get(0).audioPlayer.close();
			aPlayers.remove(0);
		}
	}
	
	public void stopPlayer(String n) {
		for (int i=0 ; i< aPlayers.size();i++) {
			//println("stopplayer " + n + " i=" + i);
			if (aPlayers.get(i).name.equals(n)) {
				aPlayers.get(i).audioPlayer.close();
			}
		}
		int j=aPlayers.size()-1;
		while(j>=0) {
			//println("stopplayer " + n + " j=" + j);
			if (aPlayers.get(j).name.equals(n)) {
				aPlayers.remove(j);
			}
			j--;
		}
		aPlayers.trimToSize();
	}
	
	public void stop() {
		stopAll();
		minim.stop();
	}
	
	void println(String s) {
		PApplet.println(s);
	}
	
}
