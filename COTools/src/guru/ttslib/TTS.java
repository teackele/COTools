package guru.ttslib;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 * @author guru
 * 
 * a freetts wrapper for processing
 */

public class TTS {
    public Voice voice;

    public TTS() {
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice( "kevin16" );
        voice.allocate();
    }

    public TTS( String name ) {
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice( name );
        voice.allocate();
    }
    
    public void speak( String in ) {
        voice.speak( in );
    }

    public void setPitch( float p ) {
        voice.setPitch( p );    
    }    

    public void setPitchShift( float p ) {
        voice.setPitchShift( p );    
    }    
    public void setPitchRange( float p ) {
        voice.setPitchRange( p );    
    }    

}

