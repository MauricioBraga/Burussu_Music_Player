package io.github.mauriciobraga.burussumusicplayer.controller;

import java.io.File;

import javax.sound.sampled.AudioFormat;

import io.github.mauriciobraga.burussumusicplayer.model.Sound;
import io.github.mauriciobraga.burussumusicplayer.model.SoundManager;
import io.github.mauriciobraga.burussumusicplayer.view.BurussuView;
import io.github.mauriciobraga.burussumusicplayer.view.BurussuViewListener;

// The Controller depends only on the domain-level BurussuViewListener
// contract and on model/view types of this application - no java.awt or
// javax.swing imports remain here, so this class can be unit-tested with a
// stub BurussuView/view listener and no AWT/Swing runtime involved.
public class BurussuController implements BurussuViewListener {

    private static final AudioFormat PLAYBACK_FORMAT = new AudioFormat(44100, 16, 1, true, false);

    private final BurussuView view;
    private SoundManager soundManager;
    private Sound[] canais;
    private boolean alreadyPlaying;

    public BurussuController(BurussuView view) {
        this.view = view;
        // register this controller to be notified of user intent.
        this.view.setViewListener(this);
        initSoundManager();
    }

    private void initSoundManager() {
        soundManager = new SoundManager(PLAYBACK_FORMAT);
    }

    @Override
    public void onOpenRequested() {
        soundManager.close();
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        view.clearPlayerPanel();
        openFiles();
    }

    @Override
    public void onExitRequested() {
        soundManager.close();
        view.requestClose();
    }

    @Override
    public void onPauseToggled(boolean paused) {
        soundManager.setPaused(paused);
    }

    @Override
    public void onKaraokeToggled(boolean enabled) {
        if (canais == null || canais.length == 0) {
            return;
        }
        int lastIndex = canais.length - 1;
        canais[lastIndex].setPlay(!enabled);
        canais[lastIndex].setLevel(enabled ? 0f : 0.8f);
    }

    @Override
    public void onAcapellaToggled(boolean enabled) {
        if (canais == null || canais.length < 2) {
            return;
        }
        for (int i = 0; i < canais.length - 1; i++) {
            canais[i].setPlay(!enabled);
            canais[i].setLevel(enabled ? 0f : 0.8f);
        }
    }

    @Override
    public void onPlayRequested() {
        if (alreadyPlaying || canais == null || canais.length == 0) {
            return;
        }

        view.renderChannels(canais);
        for (Sound canal : canais) {
            soundManager.play(canal);
        }
        alreadyPlaying = true;
    }

    @Override
    public void onChannelToggled(int channelIndex, boolean enabled) {
        if (canais == null || channelIndex < 0 || channelIndex >= canais.length) {
            return;
        }
        canais[channelIndex].setPlay(enabled);
        canais[channelIndex].setLevel(0f);
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
