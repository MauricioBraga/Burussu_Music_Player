package io.github.mauriciobraga.burussumusicplayer.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import io.github.mauriciobraga.burussumusicplayer.model.Sound;

/**
 * Renders a horizontal level meter for a single audio channel.
 * <p>
 * This panel no longer polls the Model on a {@code javax.swing.Timer}.
 * Instead it subscribes to {@link Sound}'s level property and repaints
 * only when the Model actually notifies a change (Observer pattern),
 * which restores the "push" direction expected in MVC: Model -> View.
 */
class PanelLevelMeter extends JPanel implements PropertyChangeListener {

    private Sound sound;
    private volatile float level;

    public PanelLevelMeter(Sound s) {
        setOpaque(false);
        setSound(s);
    }

    public void setSound(Sound s) {
        if (sound != null) {
            sound.removeLevelListener(this);
        }
        sound = s;
        if (sound != null) {
            level = sound.getLevel();
            sound.addLevelListener(this);
        }
        repaint();
    }

    // Sound.setLevel() may be called from the audio playback thread, so
    // updates are marshalled back onto the Event Dispatch Thread before
    // touching any Swing state, per Swing's single-thread rule.
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        float newLevel = (Float) evt.getNewValue();
        SwingUtilities.invokeLater(() -> {
            level = newLevel;
            repaint();
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Dimension d = getSize();
        g.setColor(Color.green);
        int meterWidth = (int) (level * (float) d.width);
        g.fillRect(0, 0, meterWidth, d.height);
    }

}
