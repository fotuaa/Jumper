package ru.samsung.jumper;

//import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScreenGame implements Screen, InputProcessor {
    public static final float W_WIDTH = 9, W_HEIGHT = 16;
    public static final float SCR_WIDTH = 540, SCR_HEIGHT = 960;
    private SpriteBatch batch;
    private Vector3 touch;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private OrthographicCamera cameraText;
    private BitmapFont font50, font70;
    private Main main;

    Sound snd;
    Texture imgHedgehog;
    Texture imgLand;
    Texture imgBackGround;

    SunButton btnBack;
    SunButton btnRestart;

    Player[] players = new Player[10];





    private KinematicObject[] platforms = new KinematicObject[10];
    DynamicObjectCircle jumper;

    private static final float RESPAWN_Y_THRESHOLD = -2f; // Нижняя граница экрана для респавна
    private final Vector2 JUMPER_START_POS = new Vector2(W_WIDTH/2, 4); // Стартовая позиция

    private static final float PLATFORM_DROP_THRESHOLD = W_HEIGHT * 0.6f; // 60% высоты экрана
    private static final float PLATFORM_RESPAWN_Y = W_HEIGHT + MathUtils.random(0.1f, 0.3f); // Выше верхнего края
    private static final float PLATFORM_DROP_SPEED = 2f;
    private float currentDropSpeed = 0f;
    private boolean shouldPlatformsMove = false;
    private float countMetrs;
    private float oldPlatformY;
    private float oldVelocity;
    private float altitude = 0f;


    //Texture circleRed;

    private boolean isTouched = false;
    private float touchX = 0;
    private float moveSpeed = 3f; // Скорость перемещения



    //@Override
    public ScreenGame(Main main) {
        this.main = main;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        cameraText = main.camera;
        camera.setToOrtho(false, W_WIDTH, W_HEIGHT);
        //cameraText.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
        touch = new Vector3();
        font50 = new BitmapFont(Gdx.files.internal("fonts/comic50.fnt"));
        font70 = new BitmapFont(Gdx.files.internal("fonts/comic70.fnt"));

        Box2D.init();
        world = new World(new Vector2(0, -10), false);
        debugRenderer = new Box2DDebugRenderer();

        btnBack = new SunButton("x", font70, 8, 16);
        btnRestart = new SunButton("restart", font70, 4);

        snd = Gdx.audio.newSound(Gdx.files.internal("blasters.mp3"));
        imgHedgehog = new Texture("hedgehog.png");
        imgLand = new Texture("land.png");
        imgBackGround = new Texture("Backgraund.png");

        jumper = new DynamicObjectCircle(world, W_WIDTH/2, 4, 0.6f);

        platforms[0] = new KinematicObject(world, 4.5f, 0, 9, 0.005f);
        for (int i = 1; i < platforms.length; i++) {
            platforms[i] = new KinematicObject(world, MathUtils.random(1f, 8f), i*2.5f+MathUtils.random(-0.5f, 0.5f), MathUtils.random(1f, 3f), 0.005f);
        }
        /*platforms[0] = new KinematicObject(world, 3, 2, 5, 0.005f);
        platforms[1] = new KinematicObject(world, 6, 5, 5, 0.005f);
        platforms[2] = new KinematicObject(world, 3, 9, 5, 0.005f);
        platforms[3] = new KinematicObject(world, 6, 12, 5, 0.005f);*/

        BoostContactListener listener = new BoostContactListener(jumper, platforms, snd);
        world.setContactListener(listener);

        for (int i = 0; i < players.length; i++) {
            players[i] = new Player();
        }
        loadTableOfRecords();

        // Устанавливаем InputProcessor
        Gdx.input.setInputProcessor(this);
        oldPlatformY = platforms[1].getY();
    }

    @Override
    public void show() {
        /*Gdx.graphics.setForegroundFPS(60);
        Gdx.input.setInputProcessor(new SunInputProcessor());
        gameStart();*/
    }

    @Override
    public void render(float delta) {
        // Обработка касаний
        if (isTouched) {
            // Получаем текущую позицию джмпера
            Vector2 jumperPosition = jumper.getBody().getPosition();

            // Определяем направление движения
            if (touchX > W_WIDTH/2) {
                // Палец справа - двигаем вправо
                jumper.getBody().setLinearVelocity(moveSpeed, jumper.getBody().getLinearVelocity().y);
            } else if (touchX < W_WIDTH/2) {
                // Палец слева - двигаем влево
                jumper.getBody().setLinearVelocity(-moveSpeed, jumper.getBody().getLinearVelocity().y);
            }
        }

        // события
        if (jumper.getBody().getPosition().y < RESPAWN_Y_THRESHOLD) {
            respawnJumperDown();
        }

        if (jumper.getBody().getPosition().x < -0.5f) {
            float nowY = jumper.getBody().getPosition().y;
            float nowX = 9.5f;
            respawnJumperRight(nowX, nowY);

        }
        if (jumper.getBody().getPosition().x > 9.5f) {
            float nowY = jumper.getBody().getPosition().y;
            float nowX = -0.5f;
            respawnJumperRight(nowX, nowY);
        }



        float jumperY = jumper.getBody().getPosition().y;
     //   countMetrs = countMetrs + Math.abs(countMetrs - jumper.getY());

        // Активируем движение платформ только когда персонаж выше порога
        shouldPlatformsMove = jumperY > PLATFORM_DROP_THRESHOLD;

        if (shouldPlatformsMove) {
            // Увеличиваем скорость со временем
            currentDropSpeed = Math.min(currentDropSpeed + 2f, 7.5f);
            dropPlatforms();
            altitude += currentDropSpeed;

        } else {
            // Останавливаем платформы
            stopPlatforms();
            currentDropSpeed = 0.1f; // Сбрасываем базовую скорость
        }

        checkPlatformsBounds();

        jumper.move();

        if ((jumper.body.getLinearVelocity().y>0 && oldVelocity<=0) || (jumper.body.getLinearVelocity().y<0 && oldVelocity>=0)){
            countMetrs=countMetrs+Math.abs(countMetrs-jumper.getY());
        }
        oldVelocity=jumper.body.getLinearVelocity().y;

        // отрисовка
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        //debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, 9, 16);

        for (int i = 0; i < platforms.length; i++) {
            batch.draw(imgLand, platforms[i].getX(), platforms[i].getY(),
                platforms[i].getWidth(),platforms[i].getHeight());
        }
        batch.draw(imgHedgehog, jumper.getX(), jumper.getY(),
            jumper.getWidth()/2, jumper.getHeight()/2, jumper.getWidth(),jumper.getHeight(),
            1, 1, 0, 0, 0, 512, 512, jumper.getFlipX(), false);
        batch.end();
        world.step(1/60f, 6, 2);

        batch.setProjectionMatrix(cameraText.combined);
        batch.begin();
        font50.draw(batch, "Счёт: "+ Math.round(altitude/100)/*countMetrs*/, 10, SCR_HEIGHT-10);
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
   /* private void countMeters(){
        countMetrs += platforms[1].getY()-oldPlatformY;
        oldPlatformY = platforms[1].getY();
    }
*/
    private void dropPlatforms() {
        for (KinematicObject platform : platforms) {
            if (platform != null && platform.getBody() != null) {
                Vector2 velocity = platform.getBody().getLinearVelocity();
                platform.getBody().setLinearVelocity(velocity.x, -currentDropSpeed);
            }
        }
    }

    private void stopPlatforms() {
        for (KinematicObject platform : platforms) {
            if (platform != null && platform.getBody() != null) {
                Vector2 velocity = platform.getBody().getLinearVelocity();
                platform.getBody().setLinearVelocity(velocity.x, 0); // Нулевая скорость по Y
            }
        }
    }

    private void checkPlatformsBounds() {
        for (int i = 0; i < platforms.length; i++) {
            if (platforms[i] != null && platforms[i].getBody() != null
                && platforms[i].getBody().getPosition().y < -2f) {
                respawnPlatform(i);
            }
        }
    }

    private void respawnPlatform(int index) {
        if (platforms[index] != null && platforms[index].getBody() != null) {
            world.destroyBody(platforms[index].getBody());
        }
     //   oldPlatformY = platforms[1].getY();

        /*float randomX = 3 + (float)Math.random() * (W_WIDTH - 6);
        float randomWidth = 4 + (float)Math.random() * 3;*/

        platforms[index] = new KinematicObject(world, MathUtils.random(1f, 8f), PLATFORM_RESPAWN_Y, MathUtils.random(1f, 3f), 0.005f);
    }

    private void respawnJumperDown() {
        // 1. Удаляем старое тело из мира
        world.destroyBody(jumper.getBody());

        // 2. Создаем нового джампера в начальной позиции
        jumper = new DynamicObjectCircle(world, JUMPER_START_POS.x, JUMPER_START_POS.y, 0.6f);
        // 3. Восстанавливаем обработчик столкновений
        world.setContactListener(new BoostContactListener(jumper, platforms, snd));

        // 4. Останавливаем движение (на всякий случай)
        jumper.getBody().setLinearVelocity(0, 0);
        jumper.getBody().setAngularVelocity(0);
        //countMetrs = 0;

        //gameOver = true;
        main.player.score=(int)(altitude/100);
        if(main.player.score > players[players.length-1].score) {
            players[players.length - 1].clone(main.player);
            sortTableOfRecords();
            saveTableOfRecords();
        }

        altitude=0;
        main.setScreen(main.screenMenu);
    }

    private void respawnJumperRight(float nowX, float nowY) {
        // 1. Удаляем старое тело из мира
        world.destroyBody(jumper.getBody());

        // 2. Создаем нового джампера в начальной позиции
        jumper = new DynamicObjectCircle(world, nowX, nowY, 0.6f);
        // 3. Восстанавливаем обработчик столкновений
        world.setContactListener(new BoostContactListener(jumper, platforms, snd));

        // 4. Останавливаем движение (на всякий случай)
        jumper.getBody().setLinearVelocity(0, 0);
        jumper.getBody().setAngularVelocity(0);
    }


    @Override
    public void dispose() {
        batch.dispose();
        font50.dispose();
        font70.dispose();
    }


    private void sortTableOfRecords(){
        for (int j = 0; j < players.length; j++) {
            for (int i = 0; i < players.length-1; i++) {
                if(players[i].score < players[i+1].score){
                    Player tmp = players[i];
                    players[i] = players[i+1];
                    players[i+1] = tmp;
                }
            }
        }
    }

    public void saveTableOfRecords(){
        Preferences prefs = Gdx.app.getPreferences("JumperRecords");
        for (int i = 0; i < players.length; i++) {
            prefs.putString("name"+i, players[i].name);
            prefs.putInteger("score"+i, players[i].score);
        }
        prefs.flush();
    }

    private void loadTableOfRecords(){
        Preferences prefs = Gdx.app.getPreferences("JumperRecords");
        for (int i = 0; i < players.length; i++) {
            players[i].name = prefs.getString("name"+i, "Noname");
            players[i].score = prefs.getInteger("score"+i, 0);
        }
    }

    public void clearTableOfRecords(){
        for (Player player : players) player.clear();
    }




    // Методы InputProcessor
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touch.set(screenX, screenY, 0);
        camera.unproject(touch);
        touchX = touch.x;
        isTouched = true;
        return true;
    }











    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isTouched = false;
        jumper.getBody().setLinearVelocity(0, jumper.getBody().getLinearVelocity().y);
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        isTouched = false;
        jumper.getBody().setLinearVelocity(0, jumper.getBody().getLinearVelocity().y);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touch.set(screenX, screenY, 0);
        camera.unproject(touch);
        touchX = touch.x;
        return true;
    }

    // Остальные методы InputProcessor
    @Override
    public boolean keyDown(int keycode) { return false; }

    @Override
    public boolean keyUp(int keycode) { return false; }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(float amountX, float amountY) { return false; }


}
