package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {

    final Firexit game;
    private Rectangle boton1;
    private Rectangle boton2;
    private Texture titulo;
    private Music grito;
    public GameOverScreen(final Firexit game, int punto) {
        this.game = game;
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        boton1 = new Rectangle();
        boton2 = new Rectangle();
        boton1.setSize(game.camera.viewportWidth*0.3f, game.camera.viewportHeight*0.18f);
        boton1.setCenter(game.camera.viewportWidth/2 - game.camera.viewportWidth/2 * 0.35f,game.camera.viewportHeight/2 + game.camera.viewportHeight/2 * 0.3f);
        boton2.setSize(game.camera.viewportWidth*0.3f, game.camera.viewportHeight*0.18f);
        boton2.setCenter(game.camera.viewportWidth/2 + game.camera.viewportWidth/2 * 0.35f,game.camera.viewportHeight/2 + game.camera.viewportHeight/2 * 0.3f);
        titulo = new Texture("gameover.png");
        game.palabrasBasicas.setColor(Color.WHITE);
        grito = Gdx.audio.newMusic(Gdx.files.internal("GameAudio/GameOver.mp3"));
        grito.setLooping(false);
        grito.play();
        MensajePuntos mensaje = new MensajePuntos(punto);
        Gdx.input.getTextInput(mensaje, "Introduce tu nombre, por favor", "", "Usuario");
        Gdx.graphics.setContinuousRendering(false);
        Gdx.graphics.requestRendering();
    }

    private void dibujar() {
        Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        ScreenUtils.clear(Color.valueOf("29FF5F"));
        game.batch.begin();
        game.batch.draw(titulo, game.camera.viewportWidth/2-titulo.getWidth()/2,game.camera.viewportHeight - titulo.getHeight() - game.camera.viewportHeight*0.05f);
        game.batch.end();
    }

    private void botones() {
        game.debug.begin(ShapeRenderer.ShapeType.Filled);
        game.debug.setColor(Color.GRAY);
        game.debug.rect(boton1.x,game.camera.viewportHeight - boton1.y - boton1.height, boton1.width, boton1.height);
        game.debug.rect(boton2.x,game.camera.viewportHeight - boton2.y - boton2.height,boton2.width,boton2.height);
        game.debug.end();
        game.debug.begin(ShapeRenderer.ShapeType.Line);
        game.debug.setColor(Color.BLACK);
        game.debug.rect(boton1.x,game.camera.viewportHeight - boton1.y - boton1.height, boton1.width, boton1.height);
        game.debug.rect(boton2.x,game.camera.viewportHeight - boton2.y - boton2.height,boton2.width,boton2.height);
        game.debug.end();
    }

    private void texto() {
        game.batch.begin();
        game.palabrasBasicas.draw(game.batch, "REINTENTAR", boton1.x + boton1.getWidth()/2 - new GlyphLayout(game.palabrasBasicas,"REINTENTAR").width/2, game.camera.viewportHeight - boton1.y - boton1.height + boton1.getHeight()/2 + new GlyphLayout(game.palabrasBasicas,"REINTENTAR").height/2);
        game.palabrasBasicas.draw(game.batch, "MENU PRINCIPAL", boton2.x + boton2.getWidth()/2 - new GlyphLayout(game.palabrasBasicas,"MENU PRINCIPAL").width/2, game.camera.viewportHeight - boton2.y - boton2.height + boton2.getHeight()/2 + new GlyphLayout(game.palabrasBasicas,"MENU PRINCIPAL").height/2);
        game.batch.end();
    }

    private void toqueBoton() {
        if (boton1.contains(Gdx.input.getX(), Gdx.input.getY())) {
            game.setScreen(new GameScreen(game));
            dispose();
        } else if (boton2.contains(Gdx.input.getX(), Gdx.input.getY())) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        dibujar();
        botones();
        texto();
        if (Gdx.input.justTouched()) {
            toqueBoton();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.camera.setToOrtho(false, width, height);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
    }

    @Override
    public void show() {

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
        titulo.dispose();
        grito.dispose();
    }



}
