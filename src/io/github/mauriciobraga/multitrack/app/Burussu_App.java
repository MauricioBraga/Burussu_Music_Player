package io.github.mauriciobraga.multitrack.app;

import io.github.mauriciobraga.multitrack.controller.BurussuController;
import io.github.mauriciobraga.multitrack.view.BurussuView;

public class Burussu_App {
    public static void main(String[] args) {
        BurussuView view = new BurussuView("Burussu MVC Music Player 0.0011");
        new BurussuController(view);
    }
}
