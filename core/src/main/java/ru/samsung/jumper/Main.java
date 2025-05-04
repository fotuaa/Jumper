package ru.samsung.jumper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    public static final float W_WIDTH = 9, W_HEIGHT = 16;
    private SpriteBatch batch;
    private Vector3 touch;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    private KinematicObject[] platforms = new KinematicObject[4];
    DynamicObjectCircle jumper;
    //Texture circleRed;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, W_WIDTH, W_HEIGHT);
        touch = new Vector3();
        Box2D.init();
        world = new World(new Vector2(0, -10), false);
        debugRenderer = new Box2DDebugRenderer();

        //circleRed = new Texture("red_circle.png");
        //TextureRegion cRed = new TextureRegion(circleRed, 256, 256);

        jumper = new DynamicObjectCircle(world, W_WIDTH/2, 4, 0.4f);

        platforms[0] = new KinematicObject(world, 3, 2, 5, 0.005f);
        platforms[1] = new KinematicObject(world, 6, 5, 5, 0.005f);
        platforms[2] = new KinematicObject(world, 3, 9, 5, 0.005f);
        platforms[3] = new KinematicObject(world, 6, 12, 5, 0.005f);

        BoostContactListener listener = new BoostContactListener(jumper, platforms, 4f);
        world.setContactListener(listener);
    }

    @Override
    public void render() {
        // касания

        // события
        jumper.move();

        // отрисовка
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.end();
        world.step(1/60f, 6, 2);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
