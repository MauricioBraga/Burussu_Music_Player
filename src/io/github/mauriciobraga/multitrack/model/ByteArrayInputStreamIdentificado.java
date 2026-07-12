package io.github.mauriciobraga.multitrack.model;

import java.io.ByteArrayInputStream;

public class ByteArrayInputStreamIdentificado extends ByteArrayInputStream {

    private int id = -1;
    private boolean play;
    private Sound sound;

    public ByteArrayInputStreamIdentificado(byte[] samples, Sound s) {
        super(samples);
        this.id = s.getId();
        this.play = s.isPlay();
        this.sound = s;
    }

    public int getId() {
        return sound.getId();
    }

    public void setId(int id) {
        this.sound.setId(id);
    }

    public void setLevel(float f) {
        this.sound.setLevel(f);
    }

    public float getLevel() {
        return sound.getLevel();
    }

    public boolean isPlay() {
        return sound.isPlay();
    }

    public void setPlay(boolean play) {
        this.sound.setPlay(play);
    }

}
