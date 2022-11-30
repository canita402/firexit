package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;


public class GameScreen implements Screen {
    final Firexit game;
    private static final int RUNNING = 0;
    private static final int PAUSED = 1;
    private static final int OVER = 2;
    private int estado = 0;
    private final BitmapFont textoPausa;
    private final BitmapFont textoScore;
    private final Array<Texture> obstaculo = new Array<>(9);
    private final Animation<TextureRegion> correr;
    private int dibujo;
    private float puntos;
    private boolean extintor;
    private Texture iconoPausa;
    private float ratio;
    private final Rectangle player = new Rectangle();
    private final Rectangle obstacle = new Rectangle();
    private final Rectangle esquinaPausa = new Rectangle();
    private float tiempo= 0f;
    private float Yacelera;
    private int velocidadCorrer;
    private float gravedad;
    private float altura;
    private  float anchura;
    private boolean salto = false;
    private Music ost;


    public GameScreen(final Firexit game){
        this.game = game;
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        comprobarTamanno();
        gravedad = 1000 * ratio;
        Texture stickman = new Texture("runSheet.png");
        game.parametros.size = (int)(game.camera.viewportHeight*0.05f);
        game.parametros.borderWidth = 0;
        textoScore = game.fuente.generateFont(game.parametros);
        textoScore.setColor(Color.BLACK);
        game.parametros.size = (int)(game.camera.viewportHeight*0.25f);
        game.parametros.borderWidth = 3;
        textoPausa = game.fuente.generateFont(game.parametros);
        game.palabrasBasicas.setColor(Color.BLACK);
        TextureRegion[][] temp = TextureRegion.split(stickman, stickman.getWidth()/9, stickman.getHeight());
        TextureRegion[] frames = new TextureRegion[9];
        for (int i = 0; i < 9; i++) {
            frames[i] = temp[0][i];
        }
        correr = new Animation<>(0.05f, frames);
        esquinaPausa.x = game.camera.viewportWidth-iconoPausa.getWidth();
        esquinaPausa.y = 0;
        esquinaPausa.width = iconoPausa.getWidth();
        esquinaPausa.height = iconoPausa.getHeight();
        puntos = 0;
        player.x = game.camera.viewportWidth*0.05f;
        player.y = 0;
        altura = (stickman.getHeight() * ratio);
        anchura = ((stickman.getWidth()/9) * ratio);
        player.height = altura*0.9f;
        player.width = anchura*0.9f;
        Yacelera = 0;
        Gdx.graphics.setContinuousRendering(true);
        ost = Gdx.audio.newMusic(Gdx.files.internal("GameAudio/Running.mp3"));
        ost.setLooping(true);
        ost.play();
        spawnObstaculo();
    }



    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        switch (estado){
            case RUNNING:
                System.out.println(game.camera.viewportWidth);
                System.out.println(Gdx.graphics.getFramesPerSecond());
                dibujar();
                //debugHitbox();
                mecanicasObstaculo();
                puntos += (10f * Gdx.graphics.getDeltaTime());
                if (Gdx.input.justTouched()) {
                    if (esquinaPausa.contains(Gdx.input.getX(), Gdx.input.getY())){
                        estado = PAUSED;
                        salto = false;
                    }
                }
                saltar();
                break;
            case PAUSED:
                dibujar();
                pause();
                break;
            case OVER:
                game.setScreen(new GameOverScreen(game, (int)puntos));
                dispose();
                break;
            default:
                estado = 1;
                break;
        }
    }

    private void mecanicasObstaculo() {
        obstacle.x -= (velocidadCorrer + puntos * 0.5) * Gdx.graphics.getDeltaTime();
        if(obstacle.x < -300){
            spawnObstaculo();
        }
        if (obstacle.overlaps(player) && dibujo != 0){
            estado = OVER;
        } else if(obstacle.overlaps(player) && !extintor ){
            extintor = true;
            puntos += 250;
        }
    }

    private void dibujar() {
        Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
        ScreenUtils.clear(Color.valueOf("24fd5e"));
        tiempo += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = correr.getKeyFrame(tiempo, true);
        game.batch.begin();
        if (!extintor) {
            game.batch.draw(obstaculo.get(dibujo), obstacle.x, obstacle.y);
        }
        game.batch.draw(currentFrame,player.x, player.y, anchura, altura);
        textoScore.draw(game.batch, "Score: " + ((int) puntos), game.camera.viewportWidth * 0.03f, game.camera.viewportHeight * 0.97f);
        if (estado == PAUSED){
            textoPausa.draw(game.batch, "PAUSADO", game.camera.viewportWidth/2 - new GlyphLayout(textoPausa,"PAUSADO").width/2, game.camera.viewportHeight/2 + textoPausa.getCapHeight()/2);
            game.palabrasBasicas.draw(game.batch, "Pulse para volver al juego", game.camera.viewportWidth/2 - new GlyphLayout(game.palabrasBasicas,"Pulse para volver al juego").width/2, game.camera.viewportHeight/2 + game.palabrasBasicas.getCapHeight()/2 - textoPausa.getCapHeight());
        } else {
            game.batch.draw(iconoPausa, esquinaPausa.getX(), game.camera.viewportHeight - iconoPausa.getHeight());
        }
        game.batch.end();
    }

    private void spawnObstaculo() {
        dibujo = MathUtils.random(0, obstaculo.size - 1);
        extintor = false;
        obstacle.height = obstaculo.get(dibujo).getHeight();
        obstacle.width = obstaculo.get(dibujo).getWidth();
        obstacle.x = game.camera.viewportWidth + 200;
        if (dibujo == 2 || dibujo == 6){
            obstacle.y = (game.camera.viewportHeight * 0.25f) * ratio;
        } else if (dibujo == 0){
            obstacle.y = 40 * ratio;
            if(MathUtils.random(1) == 1){
                obstacle.y = 250 * ratio;
            }
        } else {
            obstacle.y = 0;
        }
    }

    private void saltar(){
        Rectangle izquierda = new Rectangle(0,0,game.camera.viewportWidth/2, game.camera.viewportHeight);
        Rectangle derecha = new Rectangle(game.camera.viewportWidth/2,0,game.camera.viewportWidth/2, game.camera.viewportHeight);
        if (Gdx.input.isTouched() && derecha.contains(Gdx.input.getX(), Gdx.input.getY())){
            gravedad = (1000 * ratio)*2.5f;
        }
        if ((Gdx.input.justTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)) && salto && izquierda.contains(Gdx.input.getX(), Gdx.input.getY())){
            Yacelera = 650 * ratio;
            salto = false;
        } else if (player.y > 0){
            Yacelera -= gravedad * Gdx.graphics.getDeltaTime();
        } else{
            Yacelera = 0;
            player.y = 0;
            salto = true;
        }
        player.y += Yacelera * Gdx.graphics.getDeltaTime();
        gravedad = 1000 * ratio;
    }
    @Override
    public void resize(int width, int height) {
        game.camera.setToOrtho(false, width, height);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
    }

    @Override
    public void pause() {
        Gdx.graphics.setContinuousRendering(false);
        estado = PAUSED;
        if (Gdx.input.justTouched()){
            estado = RUNNING;
            Gdx.graphics.setContinuousRendering(true);
        }
    }

    private void comprobarTamanno() {
        if (game.camera.viewportWidth > 2799){
            obstaculo.add(new Texture ("extintorx25.png"),new Texture ("lameculosx25.png"),new Texture ("lamparitax25.png"),new Texture ("tablerbx25.png"));
            obstaculo.add(new Texture ("sillax25.png"),new Texture ("vendingx25.png"), new Texture("proyectorx25.png"), new Texture("hombrecaferbx25.png"));
            obstaculo.add(new Texture ("archivadorx25.png"));
            iconoPausa = new Texture("pausax25.png");
            ratio = 2.5f;
            velocidadCorrer = 1000;
        }else if (game.camera.viewportWidth > 1999){
            obstaculo.add(new Texture ("extintorx2.png"),new Texture ("lameculosx2.png"),new Texture ("lamparitax2.png"),new Texture ("tablerbx2.png"));
            obstaculo.add(new Texture ("sillax2.png"),new Texture ("vendingx2.png"), new Texture("proyectorx2.png"), new Texture("hombrecaferbx2.png"));
            obstaculo.add(new Texture ("archivadorx2.png"));
            iconoPausa = new Texture("pausax2.png");
            ratio = 2f;
            velocidadCorrer = 800;
        }else if (game.camera.viewportWidth > 1199){
            obstaculo.add(new Texture ("extintor1.png"),new Texture ("lameculos1.png"),new Texture ("lamparita1.png"),new Texture ("tablerb1.png"));
            obstaculo.add(new Texture ("silla1.png"),new Texture ("vending1.png"), new Texture("proyector1.png"), new Texture("hombrecaferb1.png"));
            obstaculo.add(new Texture ("archivador1.png"));
            iconoPausa = new Texture("pausa1.png");
            ratio = 1.5f;
            velocidadCorrer = 600;
        }else{
            obstaculo.add(new Texture ("extintor.png"),new Texture ("lameculos.png"),new Texture ("lamparita.png"),new Texture ("tablerb.png"));
            obstaculo.add(new Texture ("silla.png"),new Texture ("vending.png"), new Texture("proyector.png"), new Texture("hombrecaferb.png"));
            obstaculo.add(new Texture ("archivador.png"));
            iconoPausa = new Texture("pausa.png");
            ratio = 1f;
            velocidadCorrer = 500;
        }
    }

    private void debugHitbox() {
        game.debug.begin(ShapeRenderer.ShapeType.Line);
        game.debug.setColor(Color.BLACK);
        game.debug.rect(player.x,0 + player.y, player.width, player.height);
        game.debug.end();
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        textoScore.dispose();
        textoPausa.dispose();
        iconoPausa.dispose();
        ost.dispose();
        for (int i = 0; i < obstaculo.size; i++) {
            obstaculo.get(i).dispose();
        }
    }
}
