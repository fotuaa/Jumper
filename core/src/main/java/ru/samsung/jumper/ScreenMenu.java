package ru.samsung.jumper;

import static ru.samsung.jumper.Main.*;
import static ru.samsung.jumper.Main.SCR_HEIGHT;
import static ru.samsung.jumper.Main.SCR_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ScreenMenu implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Vector3 touch;
    private BitmapFont font;
    private Main main;

    Texture imgBackGround;

    SunButton btnPlay;
    SunButton btnSettings;
    SunButton btnLeaderBoard;
    SunButton btnAbout;
    SunButton btnExit;

    public ScreenMenu(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font = main.font70white;

        imgBackGround = new Texture("Backgraund.png");

        btnPlay = new SunButton("Играть", font, 100, 600);
        btnSettings = new SunButton("Настройки", font, 100, 500);
        btnLeaderBoard = new SunButton("Рекорды", font, 100, 400);
        btnAbout = new SunButton("О проекте", font, 100, 300);
        btnExit = new SunButton("Выход", font, 100, 200);
    }

    @Override
    public void show() {
        Gdx.graphics.setForegroundFPS(10);
    }

    @Override
    public void render(float delta) {
        // касания
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            if(btnPlay.hit(touch.x, touch.y)){
                main.setScreen(main.screenGame);
            }
            if(btnSettings.hit(touch.x, touch.y)){
                main.setScreen(main.screenSettings);
            }
            if(btnLeaderBoard.hit(touch.x, touch.y)){
                main.setScreen(main.screenLeaderBoard);
            }
            if(btnAbout.hit(touch.x, touch.y)){
                main.setScreen(main.screenAbout);
            }
            if(btnExit.hit(touch.x, touch.y)){
                Gdx.app.exit();
            }
        }
        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        btnPlay.font.draw(batch, btnPlay.text, btnPlay.x, btnPlay.y);
        btnSettings.font.draw(batch, btnSettings.text, btnSettings.x, btnSettings.y);
        btnLeaderBoard.font.draw(batch, btnLeaderBoard.text, btnLeaderBoard.x, btnLeaderBoard.y);
        btnAbout.font.draw(batch, btnAbout.text, btnAbout.x, btnAbout.y);
        btnExit.font.draw(batch, btnExit.text, btnExit.x, btnExit.y);
        batch.end();
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

    }
}
