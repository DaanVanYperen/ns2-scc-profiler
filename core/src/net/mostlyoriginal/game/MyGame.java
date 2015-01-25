package net.mostlyoriginal.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class MyGame extends Game {
    private static MyGame instance;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        instance = this;
        restart();
    }

    public void restart()
    {
        setScreen(new MainScreen());
    }

    public static MyGame getInstance()
    {
        return instance;
    }
}
