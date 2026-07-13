package io.github.mauriciobraga.burussumusicplayer.view;

import javax.swing.Timer;

import io.github.mauriciobraga.burussumusicplayer.model.Sound;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class PanelLevelMeter extends JPanel {
    Sound sound;
    float level;

    public PanelLevelMeter(Sound s) {
        sound = s;
        setOpaque(false);
        Timer timer = new Timer(50,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // level = line.getLevel();
                        if (sound != null)
                            level = sound.getLevel();
                        repaint();
                    }
                });
        timer.start();
    }

    public void setSound(Sound s) {
        sound = s;
    }

    public void paint(Graphics g) {
        super.paint(g);
        Dimension d = getSize();

        repaint();

        g.setColor(Color.green);
        int meterWidth = (int) (level * (float) d.width);
        g.fillRect(0, 0, meterWidth, d.height);
    }

}

