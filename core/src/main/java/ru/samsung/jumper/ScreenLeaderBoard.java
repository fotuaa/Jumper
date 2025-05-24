package ru.samsung.jumper;

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

public class ScreenLeaderBoard implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Vector3 touch;
    private BitmapFont font70;
    private BitmapFont font50;
    private Main main;

    Texture imgBackGround;

    SunButton btnSwitchGlobal;
    SunButton btnClear;
    SunButton btnBack;

    Player[] players;
    private boolean showGlobalRecords;

    public ScreenLeaderBoard(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font70 = main.font70white;
        font50 = main.font50white;
        players = main.screenGame.players;

        imgBackGround = new Texture("Backgraund.png");

        btnSwitchGlobal = new SunButton(" ", font50, 800);
        btnClear = new SunButton("Очистить", font50, 150);
        btnBack = new SunButton("Назад", font50, 75);
    }

    @Override
    public void show() {
        Gdx.graphics.setForegroundFPS(10); // Устанавливаем лимит в 10 FPS
    }

    @Override
    public void render(float delta) {
        // касания
        if(Gdx.input.justTouched()){
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);


            if (btnClear.hit(touch) && !showGlobalRecords){
                main.screenGame.clearTableOfRecords();
                main.screenGame.saveTableOfRecords();
            }
            if(btnBack.hit(touch)){
                main.setScreen(main.screenMenu);
            }
        }
        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        font70.draw(batch, "РЕКОРДЫ", 0, 900, SCR_WIDTH, Align.center, false);
        btnSwitchGlobal.font.draw(batch, btnSwitchGlobal.text, btnSwitchGlobal.x, btnSwitchGlobal.y);
        //font50.draw(batch, "score", 480, 830, 200, Align.right, false);
        if(showGlobalRecords){
            /*for (int i = 0; i < Math.min(main.screenGame.db.size(), players.length); i++) {
                font50.draw(batch, i + 1 + "", 100, 1100 - i * 70);
                font50.draw(batch, main.screenGame.db.get(i).name, 200, 1100 - i * 70);
                font50.draw(batch, main.screenGame.db.get(i).score + "", 500, 1100 - i * 70, 200, Align.right, false);
                font50.draw(batch, main.screenGame.db.get(i).kills + "", 620, 1100 - i * 70, 200, Align.right, false);
            }*/
        } else {
            for (int i = 0; i < players.length; i++) {
                font50.draw(batch, i + 1 + "", 20, 730 - i * 55);
                font50.draw(batch, players[i].name, 100, 730 - i * 55);
                font50.draw(batch, players[i].score + "", 400, 730 - i * 55, 100, Align.right, false);
            }
        }
        if(!showGlobalRecords)
            btnClear.font.draw(batch, btnClear.text, btnClear.x, btnClear.y);

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
