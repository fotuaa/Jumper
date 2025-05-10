package ru.samsung.jumper;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import javax.swing.JLayeredPane;

public class DynamicObjectCircle {
    public static final short CATEGORY_DYNAMIC = 0x0001;
    public static final short CATEGORY_KINEMATIC = 0x0002;
    public static final short CATEGORY_OTHER = 0x0004;

    public float x, y;
    public float radius;
    public Body body;
    private Fixture fixture;
    private Filter filter;

    public DynamicObjectCircle(World world, float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.01f;

        fixtureDef.filter.categoryBits = CATEGORY_DYNAMIC;
        fixtureDef.filter.maskBits = CATEGORY_KINEMATIC | CATEGORY_OTHER; // Изначально взаимодействует с динамическими

        fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    public boolean hit(Vector3 t){
        Array<Fixture> fixtures = body.getFixtureList();
        for(Fixture f: fixtures) {
            if(f.testPoint(t.x, t.y)) {
                return true;
            }
        }
        return false;
    }

    public float getX(){
        return body.getPosition().x-radius;
    }
    public float getY(){
        return body.getPosition().y-radius;
    }
    public float getWidth(){
        return radius*2;
    }
    public float getHeight(){
        return radius*2;
    }
    public float getAngle(){
        return body.getAngle()* MathUtils.radiansToDegrees;
    }

    public void move(){
        filter = fixture.getFilterData();
        if(body.getLinearVelocity().y>0){
            filter.maskBits &= ~CATEGORY_KINEMATIC;
        } else {
            filter.maskBits |= CATEGORY_KINEMATIC;
        }
        fixture.setFilterData(filter);
    }

    public Body getBody() {
        return this.body;
    }
}
