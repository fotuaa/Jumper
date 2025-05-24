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
import com.badlogic.gdx.utils.Align;

public class ScreenAbout implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Vector3 touch;
    private BitmapFont font;
    private Main main;

    Texture imgBackGround;

    SunButton btnBack;
    private String text = "Добро пожаловать!\n" +
        "Ваша цель - \nвзлететь выше \nоблаков\n\nАвтор:\nВиктория\n2025";

    public ScreenAbout(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font = main.font50white;

        imgBackGround = new Texture("Backgraund.png");

        btnBack = new SunButton("Назад", font, 75);
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

            if(btnBack.hit(touch.x, touch.y)){
                main.setScreen(main.screenMenu);
            }
        }
        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        font.draw(batch, "О ПРОЕКТЕ", 0, 900, SCR_WIDTH, Align.center, false);
        font.draw(batch, text, 0, 800, SCR_WIDTH, Align.center, false);
        btnBack.font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
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
