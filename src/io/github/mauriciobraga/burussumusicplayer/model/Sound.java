package io.github.mauriciobraga.burussumusicplayer.model;

public class Sound {

    private byte[] samples;
    private int id = -1;
    private boolean play = true;
    private float level = 0.8f;

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

    public void setLevel(float f) {
        level = f;
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
