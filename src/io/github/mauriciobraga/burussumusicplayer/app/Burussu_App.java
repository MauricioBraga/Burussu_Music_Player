package io.github.mauriciobraga.burussumusicplayer.app;

import io.github.mauriciobraga.burussumusicplayer.controller.BurussuController;
import io.github.mauriciobraga.burussumusicplayer.view.BurussuView;

public class Burussu_App {
    public static void main(String[] args) {
        BurussuView view = new BurussuView("Burussu Music Player 0.0012");
        new BurussuController(view);
    }
}
