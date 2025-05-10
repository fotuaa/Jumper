package ru.samsung.jumper;

import static ru.samsung.jumper.Main.W_WIDTH;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class KinematicObject {
    public static final short CATEGORY_DYNAMIC = 0x0001;
    public static final short CATEGORY_KINEMATIC = 0x0002;
    public static final short CATEGORY_OTHER = 0x0004;

    public float x, y;
    public float width, height;
    private float vx = 0;
    private float va = 0;
    Body body;

    public KinematicObject(World world, float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f;
        //fixtureDef.isSensor = false; // или true, если это триггер
        fixtureDef.filter.categoryBits = CATEGORY_KINEMATIC;

        body.createFixture(fixtureDef);

        shape.dispose();

        body.setLinearVelocity(vx, 0);
        body.setAngularVelocity(va);
    }

    public void move(){
        x = body.getPosition().x;
        if(x > W_WIDTH+width || x < -width) {
            vx = -vx;
            va = -va;
            body.setLinearVelocity(vx, 0);
            body.setAngularVelocity(va);
        }
    }

    public Body getBody() {
        return this.body;
    }
}
