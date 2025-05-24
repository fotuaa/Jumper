package ru.samsung.jumper;

import static ru.samsung.jumper.Main.isSoundOn;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class BoostContactListener implements ContactListener {
    private final Body dynamicBody;
    private final Body[] kinematicBodies;
    private final float impulseStrength = 4.5f;
    Sound snd;

    public BoostContactListener(DynamicObjectCircle dynamic, KinematicObject[] kinematics, Sound snd) {
        this.dynamicBody = dynamic.body;
        kinematicBodies = new Body[kinematics.length];
        for (int i = 0; i < kinematics.length; i++) {
            kinematicBodies[i] = kinematics[i].body;
        }
        this.snd=snd;
    }

    private boolean isKinematicBody(Body body) {
        for (Body kinematicBody : kinematicBodies) {
            if (kinematicBody == body) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        // Проверяем контакт нашего динамического тела с любым из кинематических
        if ((bodyA == dynamicBody && isKinematicBody(bodyB)) && bodyA.getPosition().y > bodyB.getPosition().y ||
            (bodyB == dynamicBody && isKinematicBody(bodyA)) && bodyB.getPosition().y > bodyA.getPosition().y) {

            // Придаем импульс вверх
            dynamicBody.setLinearVelocity(0, 0);
            Vector2 impulse = new Vector2(0, impulseStrength);
            dynamicBody.applyLinearImpulse(impulse, dynamicBody.getWorldCenter(), true);
            if (isSoundOn)
                snd.play();
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}}
