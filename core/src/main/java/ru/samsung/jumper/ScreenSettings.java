package ru.samsung.jumper;

import static ru.samsung.jumper.Main.ACCELEROMETER;

import static ru.samsung.jumper.Main.RIGHT;
import static ru.samsung.jumper.Main.SCREEN;
import static ru.samsung.jumper.Main.SCR_HEIGHT;
import static ru.samsung.jumper.Main.SCR_WIDTH;
import static ru.samsung.jumper.Main.controls;
import static ru.samsung.jumper.Main.isSoundOn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

public class ScreenSettings implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Vector3 touch;
    private BitmapFont font70white, font70gray;
    private BitmapFont font50white;
    private Main main;
    private InputKeyboard keyboard;

    Texture imgBackGround;

    SunButton btnName;
    SunButton btnControl;
    SunButton btnScreen;
    SunButton btnJoystick;
    SunButton btnAccelerometer;
    SunButton btnSound;
    SunButton btnBack;

    public ScreenSettings(Main main) {
        this.main = main;
        batch = main.batch;
        camera = main.camera;
        touch = main.touch;
        font70white = main.font70white;
        font70gray = main.font70gray;
        font50white = main.font50white;
        keyboard = new InputKeyboard(font50white, SCR_WIDTH, SCR_HEIGHT/2, 7);

        imgBackGround = new Texture("Backgraund.png");

        loadSettings();
        btnName = new SunButton("Имя: "+main.player.name, font50white, 50, 800);
        btnControl = new SunButton("Управление:", font50white, 50, 730);
        btnScreen = new SunButton("Касание", font50white, 100, 660);
        btnAccelerometer = new SunButton("Наклон", font50white, 100, 600);
        setFontColorByControls();
        btnSound = new SunButton(isSoundOn ? "Звук вкл." : "Звук выкл.", font50white, 50, 530);
        btnBack = new SunButton("Назад", font50white, 65);
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

            if(keyboard.isKeyboardShow) {
                if (keyboard.touch(touch)) {
                    main.player.name = keyboard.getText();
                    btnName.setText("Имя: "+main.player.name);
                }
            } else {
                if (btnName.hit(touch)) {
                    keyboard.start();
                }
                if (btnScreen.hit(touch)) {
                    controls = SCREEN;
                    setFontColorByControls();
                }
                if (btnAccelerometer.hit(touch)) {
                    if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
                        controls = ACCELEROMETER;
                        setFontColorByControls();
                    }
                }
                if (btnSound.hit(touch)) {
                    isSoundOn = !isSoundOn;
                    btnSound.setText(isSoundOn ? "Звук вкл." : "Звук выкл.");
                }
                if (btnBack.hit(touch)) {
                    main.setScreen(main.screenMenu);
                }
            }
        }
        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBackGround, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        font70white.draw(batch, "НАСТРОЙКИ", 0, 900, SCR_WIDTH, Align.center, false);
        btnName.font.draw(batch, btnName.text, btnName.x, btnName.y);
        btnControl.font.draw(batch, btnControl.text, btnControl.x, btnControl.y);
        btnScreen.font.draw(batch, btnScreen.text, btnScreen.x, btnScreen.y);
        //btnJoystick.font.draw(batch, btnJoystick.text, btnJoystick.x, btnJoystick.y);
        btnAccelerometer.font.draw(batch, btnAccelerometer.text, btnAccelerometer.x, btnAccelerometer.y);
        btnSound.font.draw(batch, btnSound.text, btnSound.x, btnSound.y);
        btnBack.font.draw(batch, btnBack.text, btnBack.x, btnBack.y);
        keyboard.draw(batch);
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
        saveSettings();
    }

    @Override
    public void dispose() {
        keyboard.dispose();
    }

    private void setFontColorByControls(){
        btnScreen.setFont(controls == SCREEN ? font70white : font50white);
        btnAccelerometer.setFont(controls == ACCELEROMETER ? font70white : font50white);
    }

    private void saveSettings(){
        Preferences prefs = Gdx.app.getPreferences("JumperSettings");
        prefs.putString("name", main.player.name);
        prefs.putInteger("controls", controls);
        prefs.putBoolean("sound", isSoundOn);
        prefs.flush();
    }

    private void loadSettings(){
        Preferences prefs = Gdx.app.getPreferences("JumperSettings");
        main.player.name = prefs.getString("name", "Noname");
        controls = prefs.getInteger("controls", SCREEN);
        isSoundOn = prefs.getBoolean("sound", true);
    }
}
