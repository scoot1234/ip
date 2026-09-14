package bloop.gui;

import javafx.application.Application;

/** Launches Orbit without triggering the Java launcher special-case for JavaFX application classes. */
public final class BloopLauncher {
    private BloopLauncher() {
        // Utility class.
    }

    /** Starts the Orbit JavaFX application. */
    public static void main(String[] args) {
        Application.launch(BloopApp.class, args);
    }
}
