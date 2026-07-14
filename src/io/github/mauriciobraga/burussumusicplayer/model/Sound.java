// original code written by David Brackeen, 2002, modified by Mauricio Braga.

package io.github.mauriciobraga.burussumusicplayer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Sound {

    /** Property name fired whenever the playback level changes. */
    public static final String LEVEL_PROPERTY = "level";

    // Minimum variation worth notifying listeners about, so the View isn't
    // flooded with events for imperceptible level fluctuations (setLevel is
    // called once per audio buffer, from the playback thread).
    private static final float LEVEL_CHANGE_THRESHOLD = 0.01f;

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    private byte[] samples;
    private int id = -1;

    // Written by the EDT (Controller reacting to a checkbox/Karaoke/Acapella
    // toggle) and read by the audio playback thread (SoundManager). volatile
    // guarantees the mute/unmute takes effect promptly and visibly across
    // threads, the same reasoning as for `level` above.
    private volatile boolean play = true;


    // Written by the audio playback thread (see SoundManager.calculateLevel)
    // and read by the Swing EDT (see PanelLevelMeter). volatile guarantees
    // visibility of the latest value across threads.
    private volatile float level = 0.8f;

    public Sound(byte[] samples) {
        this.samples = samples;
    }

    public Sound(byte[] samples, int id, boolean play) {
        this.samples = samples;
        this.id = id;
        this.play = play;
    }

    public float getLevel() {
        return level;
    }

    /**
     * Updates the playback level and notifies any registered listeners.
     * <p>
     * This is how the Model pushes state changes to the View (Observer
     * pattern), instead of the View polling the Model on a timer.
     * {@link PropertyChangeSupport} is thread-safe, so this may safely be
     * called from the audio playback thread while listeners run on the EDT.
     */
    public void setLevel(float f) {
        float old = level;
        level = f;
        boolean changedEnough = Math.abs(f - old) >= LEVEL_CHANGE_THRESHOLD;
        // A transition down to exact silence (e.g. a channel being muted by
        // Karaoke/Acapella) always matters to the View, even if `old` was
        // already a very small value below the throttling threshold -
        // otherwise the meter could be left showing a small stale residual
        // bar that never gets cleared.
        boolean becameSilent = f == 0f && old != 0f;
        if (changedEnough || becameSilent) {
            changeSupport.firePropertyChange(LEVEL_PROPERTY, old, f);
        }

    }

    /** Registers a listener to be notified whenever the level changes. */
    public void addLevelListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(LEVEL_PROPERTY, listener);
    }

    /** Unregisters a previously registered level listener. */
    public void removeLevelListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(LEVEL_PROPERTY, listener);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public byte[] getSamples() {
        return samples;
    }

}
