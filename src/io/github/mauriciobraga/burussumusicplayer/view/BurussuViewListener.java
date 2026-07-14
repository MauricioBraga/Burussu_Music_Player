package io.github.mauriciobraga.burussumusicplayer.view;

/**
 * Contract used by {@code BurussuView} to notify a Controller about user
 * intent, expressed in domain terms (open a file, toggle karaoke, etc.)
 * rather than as raw Swing {@code ActionEvent}/{@code AbstractButton}
 * objects.
 * <p>
 * This keeps the Controller free of any dependency on the concrete UI
 * toolkit: an implementation of this interface can be unit-tested without
 * instantiating any Swing component.
 */
public interface BurussuViewListener {

    void onOpenRequested();

    void onExitRequested();

    void onPauseToggled(boolean paused);

    void onKaraokeToggled(boolean enabled);

    void onAcapellaToggled(boolean enabled);

    void onPlayRequested();

    void onChannelToggled(int channelIndex, boolean enabled);
}
