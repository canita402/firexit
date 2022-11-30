package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;



public class Firexit extends Game {
	public SpriteBatch batch;
	public OrthographicCamera camera;
	public FreeTypeFontGenerator fuente;
	public FreeTypeFontGenerator.FreeTypeFontParameter parametros;
	public BitmapFont palabrasBasicas;
	public ShapeRenderer debug;
	@Override
	public void create () {
		FileHandle archivo = Gdx.files.local("leaderboard.txt");
		if (!archivo.exists()) {
			LeaderboardScreen.nuevoRegistro("Avelina", 3500);
			LeaderboardScreen.nuevoRegistro("Maria", 1900);
			LeaderboardScreen.nuevoRegistro("Alvaro", 2350);
			LeaderboardScreen.nuevoRegistro("Daniel", 1280);
			LeaderboardScreen.nuevoRegistro("Alex", 540);
			LeaderboardScreen.nuevoRegistro("Ricardo", 320);
			LeaderboardScreen.nuevoRegistro("Alba", 930);
			LeaderboardScreen.nuevoRegistro("William", 2890);
		}
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		fuente = new FreeTypeFontGenerator(Gdx.files.internal("fuente/Titillium-Regular.otf"));
		parametros = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parametros.size = (int)(camera.viewportHeight*0.05f);
		palabrasBasicas = fuente.generateFont(parametros);
		debug = new ShapeRenderer();
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		debug.dispose();
		batch.dispose();
		fuente.dispose();
		palabrasBasicas.dispose();
	}
}
