// original code written by David Brackeen, 2002, modified by Mauricio Braga.

package io.github.mauriciobraga.burussumusicplayer.model;

import javax.sound.sampled.AudioFormat;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
// import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;



public class SoundManager extends ThreadPool {

    private AudioFormat playbackFormat;
    private ThreadLocal<SourceDataLine> localLine;
    private ThreadLocal<byte[]> localBuffer;
    
    private Object pausedLock;
    private boolean paused;

    final static float MAX_8_BITS_SIGNED = Byte.MAX_VALUE;
    final static float MAX_8_BITS_UNSIGNED = 0xff;
    final static float MAX_16_BITS_SIGNED = Short.MAX_VALUE;
    final static float MAX_16_BITS_UNSIGNED = 0xffff;

    /**
     * Creates a new SoundManager using the maximum number of
     * simultaneous sounds.
     */
    public SoundManager(AudioFormat playbackFormat) {

        this(playbackFormat, getMaxSimultaneousSounds(playbackFormat));
        // int maxsimultaneoussounds2 = getMaxSimultaneousSounds(playbackFormat);
    }

    /**
     * Creates a new SoundManager with the specified maximum
     * number of simultaneous sounds.
     */
    public SoundManager(AudioFormat playbackFormat,
            int maxSimultaneousSounds) {
        super(Math.min(maxSimultaneousSounds,
                getMaxSimultaneousSounds(playbackFormat)));
        this.playbackFormat = playbackFormat;
        localLine = new ThreadLocal<SourceDataLine>();
        localBuffer = new ThreadLocal<byte[]>();
        pausedLock = new Object();
        // notify threads in pool it's ok to start
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * Gets the maximum number of simultaneous sounds with the
     * specified AudioFormat that the default mixer can play.
     */
    public static int getMaxSimultaneousSounds(
            AudioFormat playbackFormat) {
        DataLine.Info lineInfo = new DataLine.Info(
                SourceDataLine.class, playbackFormat);
        Mixer mixer = AudioSystem.getMixer(null);
        int maxLines = mixer.getMaxLines(lineInfo);
        if (maxLines == AudioSystem.NOT_SPECIFIED) {
            maxLines = 32;
        }
        return maxLines;
    }

    /**
     * Does any clean up before closing.
     */
    protected void cleanUp() {
        // signal to unpause
        setPaused(false);

        // close the mixer (stops any running sounds)
        Mixer mixer = AudioSystem.getMixer(null);
        if (mixer.isOpen()) {
            mixer.close();
        }
    }

    public void close() {
        cleanUp();
        super.close();
    }

    public void join() {
        cleanUp();
        super.join();
    }

    /**
     * Sets the paused state. Sounds may not pause immediately.
     */
    public void setPaused(boolean paused) {
        if (this.paused != paused) {
            synchronized (pausedLock) {
                this.paused = paused;
                if (!paused) {
                    // restart sounds
                    pausedLock.notifyAll();
                }
            }
        }
    }

    /**
     * Returns the paused state.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Loads a Sound from the file system. Returns null if an
     * error occurs.
     */
    public Sound getSound(String filename) {
        return getSound(getAudioInputStream(filename));
    }

    /**
     * Loads a Sound from an input stream. Returns null if an
     * error occurs.
     */
    public Sound getSound(InputStream is) {
        return getSound(getAudioInputStream(is));
    }

    /**
     * Loads a Sound from an AudioInputStream.
     */
    public Sound getSound(AudioInputStream audioStream) {
        if (audioStream == null) {
            return null;
        }
        int framesize = audioStream.getFormat().getFrameSize();
        long frameLength = audioStream.getFrameLength();
        // get the number of bytes to read
        int length = (int) (frameLength * framesize);

        // read the entire stream
        byte[] samples = new byte[length];
        DataInputStream is = new DataInputStream(audioStream);
        try {
            is.readFully(samples);
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // return the samples
        return new Sound(samples);
    }

    /**
     * Creates an AudioInputStream from a sound from the file
     * system.
     */
    public AudioInputStream getAudioInputStream(String filename) {
        try {
            return getAudioInputStream(
                    new FileInputStream(filename));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Creates an AudioInputStream from a sound from an input
     * stream
     */
    public AudioInputStream getAudioInputStream(InputStream is) {

        try {
            if (!is.markSupported()) {
                is = new BufferedInputStream(is);
            }
            // open the source stream
            AudioInputStream source = AudioSystem.getAudioInputStream(is);

            // convert to playback format
            return AudioSystem.getAudioInputStream(
                    playbackFormat, source);
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Plays a sound. This method returns immediately.
     */
    public InputStream play(Sound sound) {
        return play(sound, null, false);
    }

    /**
     * Plays a sound with an optional SoundFilter, and optionally
     * looping. This method returns immediately.
     */
    public InputStream play(Sound sound, SoundFilter filter,
            boolean loop) {
        InputStream is;
        if (sound != null) {
            if (loop) {
                is = new LoopingByteInputStream(
                        sound.getSamples());
            } else {
                // is = new ByteArrayInputStream(sound.getSamples());
                is = new ByteArrayInputStreamIdentificado(sound.getSamples(), sound);
            }

            return play(is, filter);
        }
        return null;
    }

    /**
     * Plays a sound from an InputStream. This method
     * returns immediately.
     */
    public InputStream play(InputStream is) {
        return play(is, null);
    }

    /**
     * Plays a sound from an InputStream with an optional
     * sound filter. This method returns immediately.
     */
    public InputStream play(InputStream is, SoundFilter filter) {
        if (is != null) {
            if (filter != null) {
                is = new FilteredSoundStream(is, filter);
            }
            runTask(new SoundPlayer(is));
        }
        return is;
    }

    /**
     * Signals that a PooledThread has started. Creates the
     * Thread's line and buffer.
     */
    protected void threadStarted() {
        // wait for the SoundManager constructor to finish
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }

        // use a short, 100ms (1/10th sec) buffer for filters that
        // change in real-time
        int bufferSize = playbackFormat.getFrameSize() *
                Math.round(playbackFormat.getSampleRate() / 20);

        // create, open, and start the line
        SourceDataLine line;
        DataLine.Info lineInfo = new DataLine.Info(
                SourceDataLine.class, playbackFormat);
        try {
            line = (SourceDataLine) AudioSystem.getLine(lineInfo);
            line.open(playbackFormat, bufferSize);
        } catch (LineUnavailableException ex) {
            // the line is unavailable - signal to end this thread
            Thread.currentThread().interrupt();
            return;
        }

        line.start();

        // create the buffer
        byte[] buffer = new byte[bufferSize];

        // set this thread's locals
        localLine.set(line);
        localBuffer.set(buffer);
    }

    /**
     * Signals that a PooledThread has stopped. Drains and
     * closes the Thread's Line.
     */
    protected void threadStopped() {
        SourceDataLine line = localLine.get();
        if (line != null) {
            line.drain();
            line.close();
        }
    }

    /**
     * The SoundPlayer class is a task for the PooledThreads to
     * run. It receives the threads's Line and byte buffer from
     * the ThreadLocal variables and plays a sound from an
     * InputStream.
     * <p>
     * This class only works when called from a PooledThread.
     */
    protected class SoundPlayer implements Runnable {

        private InputStream source;
        // private int id = -1;
        // private boolean play = true;
        SourceDataLine line;

        public SoundPlayer(InputStream source) {
            this.source = source;
        }

        public void run() {
            // get line and buffer from ThreadLocals
            line = localLine.get();
            byte[] buffer = localBuffer.get();
            if (line == null || buffer == null) {
                // the line is unavailable
                return;
            }

            // copy data to the line
            try {
                int numBytesRead = 0;
                while (numBytesRead != -1) {
                    // if paused, wait until unpaused
                    synchronized (pausedLock) {
                        if (paused) {
                            try {
                                pausedLock.wait();
                            } catch (InterruptedException ex) {
                                return;
                            }
                        }
                    }
                    // copy data
                    numBytesRead = source.read(buffer, 0, buffer.length);
                    if (numBytesRead != -1) {
                        if (source instanceof ByteArrayInputStreamIdentificado) {
                            ByteArrayInputStreamIdentificado x = (ByteArrayInputStreamIdentificado) source;
                            boolean play = x.isPlay();
                            if (play) {
                                // calcula o nível do som e seta o valor para gerar o gráfico na tela
                                // float temp = (float) new Random().nextFloat();
                                // x.setLevel( temp);
                                calculateLevel(buffer, 0, 0, x);

                                line.write(buffer, 0, numBytesRead);
                            } else {

                                // Keep pushing the level to zero every
                                // iteration while muted. This makes the
                                // zeroing self-correcting: even if a
                                // calculateLevel() call for a buffer read
                                // just before the mute took effect races
                                // past the Controller's one-shot
                                // setLevel(0f) and overwrites it with a
                                // non-zero value, the very next buffer here
                                // brings it back to zero - so the meter
                                // cannot get stuck showing a stale level.
                                x.setLevel(0f);

                                byte[] buffervazio = new byte[numBytesRead];
                                line.write(buffervazio, 0, numBytesRead);
                            }
                        }
                        // else
                        // line.write(buffer, 0, numBytesRead);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    private void calculateLevel(byte[] buffer,
            int readPoint,
            int leftOver, ByteArrayInputStreamIdentificado bytearrayinput) {
        int max = 0;

        boolean use16Bit = (playbackFormat.getSampleSizeInBits() == 16);
        boolean signed = (playbackFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED);
        boolean bigEndian = (playbackFormat.isBigEndian());
        if (use16Bit) {
            for (int i = readPoint; i < buffer.length - leftOver; i += 2) {
                int value = 0;
                // deal with endianness
                int hiByte = (bigEndian ? buffer[i] : buffer[i + 1]);
                int loByte = (bigEndian ? buffer[i + 1] : buffer[i]);
                if (signed) {
                    short shortVal = (short) hiByte;
                    shortVal = (short) ((shortVal << 8) | (byte) loByte);
                    value = shortVal;
                } else {
                    value = (hiByte << 8) | loByte;
                }
                max = Math.max(max, value);
            } // for
        } else {
            // 8 bit - no endianness issues, just sign
            for (int i = readPoint; i < buffer.length - leftOver; i++) {
                int value = 0;
                if (signed) {
                    value = buffer[i];
                } else {
                    short shortVal = 0;
                    shortVal = (short) (shortVal | buffer[i]);
                    value = shortVal;
                }
                max = Math.max(max, value);
            } // for
        } // 8 bit
          // express max as float of 0.0 to 1.0 of max value
          // of 8 or 16 bits (signed or unsigned)
        if (signed) {
            if (use16Bit) {
                bytearrayinput.setLevel((float) max / MAX_16_BITS_SIGNED);
            } else {
                bytearrayinput.setLevel((float) max / MAX_8_BITS_SIGNED);
            }
        } else {
            if (use16Bit) {
                bytearrayinput.setLevel((float) max / MAX_16_BITS_UNSIGNED);
            } else {
                bytearrayinput.setLevel((float) max / MAX_8_BITS_UNSIGNED);
            }
        }
    } // calculateLevel

}
