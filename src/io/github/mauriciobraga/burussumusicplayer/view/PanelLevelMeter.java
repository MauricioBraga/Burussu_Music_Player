// modificado a partir do código
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
        // clear component
        // g.setColor(new Color(104,5,0));
        // g.clearRect(0, 0, d.width, d.height);
        // g.fillRect(0, 0, d.width, d.height);
        repaint();
        // paint
        g.setColor(Color.green);
        int meterWidth = (int) (level * (float) d.width);
        /*
         * System.out.println ("level = " + level +
         * ", meterWidth = " + meterWidth);
         */
        g.fillRect(0, 0, meterWidth, d.height);
    }

}

class MinhaThread extends Thread {
    private String nome;

    public MinhaThread(String nome) {
        this.nome = nome;
    }

    public void run() {
        for (int i = 0; i < 10; i++)
            System.out.println("Thread " + nome + ":  " + i);
        System.out.println("Thread " + nome + " concluída!");

    }
}
