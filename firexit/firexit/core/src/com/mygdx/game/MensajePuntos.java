package com.mygdx.game;

import com.badlogic.gdx.Input;

public class MensajePuntos implements Input.TextInputListener {
    int puntos;

    public MensajePuntos(int puntos) {
        this.puntos = puntos;
    }

    @Override
    public void input(String text) {
        if (text.equals("")){
            text = "Usuario";
        }
        LeaderboardScreen.nuevoRegistro(text, puntos);
    }

    @Override
    public void canceled() {

    }
}
