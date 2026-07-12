package io.github.mauriciobraga.burussumusicplayer.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.swing.AbstractButton;

import io.github.mauriciobraga.burussumusicplayer.model.Sound;
import io.github.mauriciobraga.burussumusicplayer.model.SoundManager;
import io.github.mauriciobraga.burussumusicplayer.view.BurussuView;

public class BurussuController implements ActionListener {

    private static final AudioFormat PLAYBACK_FORMAT = new AudioFormat(44100, 16, 1, true, false);

    private final BurussuView view;
    private SoundManager soundManager;
    private Sound[] canais;
    private boolean alreadyPlaying;

    public BurussuController(BurussuView view) {
        this.view = view;
        // add this controller as listener of the UI events.
        this.view.setActionListener(this);
        initSoundManager();
    }

    private void initSoundManager() {
        soundManager = new SoundManager(PLAYBACK_FORMAT);
    }

    // all button click events generated in the view are processed here.
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        AbstractButton button = (AbstractButton) e.getSource();

        if (BurussuView.OPEN.equals(command)) {
            soundManager.close();
            soundManager = new SoundManager(PLAYBACK_FORMAT);
            view.clearPlayerPanel();
            openFiles();
        } else if (BurussuView.EXIT.equals(command)) {
            soundManager.close();
            view.dispatchEvent(new java.awt.event.WindowEvent(view, java.awt.event.WindowEvent.WINDOW_CLOSING));
        } else if (BurussuView.PAUSE.equals(command)) {
            soundManager.setPaused(button.isSelected());
        } else if (BurussuView.KARAOKE.equals(command)) {
            if (canais != null && canais.length > 0) {
                int lastIndex = canais.length - 1;
                boolean enabled = button.isSelected();
                canais[lastIndex].setPlay(!enabled);
                canais[lastIndex].setLevel(enabled ? 0f : 0.8f);
            }
        } else if (BurussuView.ACAPELLA.equals(command)) {
            if (canais != null && canais.length > 1) {
                boolean enabled = button.isSelected();
                for (int i = 0; i < canais.length - 1; i++) {
                    canais[i].setPlay(!enabled);
                    canais[i].setLevel(enabled ? 0f : 0.8f);
                }
            }
        } else if (BurussuView.PLAY_SOUND.equals(command)) {
            if (alreadyPlaying || canais == null || canais.length == 0) {
                return;
            }

            view.renderChannels(canais, this::onChannelSelection);
            for (Sound canal : canais) {
                soundManager.play(canal);
            }
            alreadyPlaying = true;
        }
    }

    private void onChannelSelection(ActionEvent event) {
        if (canais == null) {
            return;
        }

        for (int i = 0; i < view.getCheckboxes().length; i++) {
            if (view.getCheckboxes()[i] == event.getSource()) {
                canais[i].setPlay(view.getCheckboxes()[i].isSelected());
                canais[i].setLevel(0f);
                break;
            }
        }
    }

    // load audio files selected by the user and 
    // create Sound objects for each file.
    private void openFiles() {
        File[] files = view.selectAudioFiles();
        if (files == null || files.length == 0) {
            return;
        }

        canais = new Sound[files.length];
        for (int i = 0; i < files.length; i++) {
            canais[i] = soundManager.getSound(files[i].getAbsolutePath());
        }

        view.enablePlayButton(true);
        alreadyPlaying = false;
    }
}
