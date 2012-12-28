package Game;

import Core.Logger;
import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {
	
	private Clip clip;
	
	public SoundPlayer (String file) {
		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream (
				new BufferedInputStream (this.getClass().getResourceAsStream (file)));
			Info info = new Info (Clip.class, stream.getFormat ());
			this.clip = (Clip) AudioSystem.getLine (info);
			this.clip.open (stream);
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
			Logger.log (ex.getMessage () + "\n" + ex.getCause ());
		}
	}

	public void play () {
		this.clip.start ();
		this.clip.setFramePosition (0);
	}
	
	public void stop () {
		this.clip.stop ();
		this.clip.setFramePosition (0);
	}
	
	public void cleanup () {
		this.clip.close ();
	}
}

