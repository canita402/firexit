package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Arrays;

public class LeaderboardScreen implements Screen {
    final Firexit game;
    private Rectangle boton;
    private String[] datos = new String[8];
    private String[] nombres = {"", "", "", "", "", "", "", ""};
    private String[] legible;
    private int[] puntosGrandes = {0, 0, 0, 0, 0, 0, 0, 0};
    private int orden = -1;
    private Music ost;

    public LeaderboardScreen(Firexit game) {
        this.game = game;
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        FileHandle archivo = Gdx.files.local("leaderboard.txt");
        String todo = archivo.readString();
        legible = todo.split("\n");
        int punto;
        int contador = 0;
        for (int i = 0; i < legible.length; i++) {
                if (i % 2 != 0) {
                    punto = Integer.parseInt(legible[i]);

                    if (contador < 8) {
                        puntosGrandes[contador] = punto;
                        contador++;
                    } else{
                        Arrays.sort(puntosGrandes);
                        if (puntosGrandes[0] <= punto){
                            puntosGrandes[0] = punto;
                        }
                    }
                }
        }
        Arrays.sort(puntosGrandes);
        for(int i = 0; i < puntosGrandes.length / 2; i++)
        {
            int temp = puntosGrandes[i];
            puntosGrandes[i] = puntosGrandes[puntosGrandes.length - i - 1];
            puntosGrandes[puntosGrandes.length - i - 1] = temp;
        }
        String nombre = "";
        punto = -1;
        for (int i = 0; i < legible.length; i++) {
            if (i % 2 != 0) {
                punto = Integer.parseInt(legible[i]);
                for (int j = 0; j < puntosGrandes.length; j++) {
                    if (punto == puntosGrandes[j]){
                        nombres[j] = nombre;
                        break;
                    }
                }
            } else {
                nombre = legible[i];
            }
        }
        for (int i = 0; i < nombres.length; i++) {
            System.out.println(nombres[i]);
        }
        System.out.println("---------");
        for (int i = 0; i < puntosGrandes.length; i++) {
            System.out.println(puntosGrandes[i]);
        }
        System.out.println("---------");
        System.out.println(todo);
        boton = new Rectangle();
        boton.setSize(game.camera.viewportWidth * 0.2f, game.camera.viewportHeight * 0.1f);
        boton.setCenter(game.camera.viewportWidth * 0.15f, game.camera.viewportHeight * 0.9f);
        ost = Gdx.audio.newMusic(Gdx.files.internal("GameAudio/Leaderboard.mp3"));
        ost.setLooping(true);
        ost.play();
        Gdx.graphics.requestRendering();
    }

    @Override
    public void render(float delta) {
        dibujarTexto();
        boton();
        texto();
        if (Gdx.input.justTouched()) {
            toqueBoton();
        }
    }

    private void dibujarTexto() {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        ScreenUtils.clear(Color.valueOf("29FF5F"));

    }

    private void boton() {
        game.debug.begin(ShapeRenderer.ShapeType.Filled);
        game.debug.setColor(Color.GRAY);
        game.debug.rect(boton.x, game.camera.viewportHeight - boton.y - boton.height, boton.width, boton.height);
        game.debug.end();
        game.debug.begin(ShapeRenderer.ShapeType.Line);
        game.debug.setColor(Color.BLACK);
        game.debug.rect(boton.x, game.camera.viewportHeight - boton.y - boton.height, boton.width, boton.height);
        game.debug.end();
    }

    private void texto() {
        game.batch.begin();
        game.palabrasBasicas.setColor(Color.WHITE);
        game.palabrasBasicas.draw(game.batch, "VOLVER", boton.x + boton.getWidth() / 2 - new GlyphLayout(game.palabrasBasicas, "VOLVER").width / 2, game.camera.viewportHeight - boton.y - boton.height + boton.getHeight() / 2 + new GlyphLayout(game.palabrasBasicas, "VOLVER").height / 2);
        game.palabrasBasicas.setColor(Color.BLACK);
        int imprimir;
        if (legible.length > 16){
            imprimir = 8;
        } else {
            imprimir = legible.length/2;
        }
        for (int i = 0; i < imprimir; i++) {
            if (i < 3) {
                datos[i] = nombres[i].toUpperCase() + "         " + puntosGrandes[i];
            } else {
                datos[i] = nombres[i].toLowerCase() + "         " + puntosGrandes[i];
            }
            game.palabrasBasicas.draw(game.batch, datos[i], game.camera.viewportWidth / 2 - new GlyphLayout(game.palabrasBasicas, datos[i]).width / 2, game.camera.viewportHeight * (0.95f - (i * 0.1f)));
        }
        game.batch.end();
    }

    private void toqueBoton() {
        if (boton.contains(Gdx.input.getX(), Gdx.input.getY())) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    public static void nuevoRegistro(String usuario, int puntos) {
        FileHandle archivo = Gdx.files.local("leaderboard.txt");
        archivo.writeString(usuario + "\n", true);
        archivo.writeString(puntos + "\n", true);
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        ost.dispose();
    }

}
