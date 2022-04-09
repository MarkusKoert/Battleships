package com.battleships.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class BodyFactory {
    private final float DEGTORAD = 0.0174533f;
    private static BodyFactory thisInstance;
    private World world;
    public static final int SHIP = 1;
    public static final int BULLET = 2;

    private BodyFactory(World world) {
        this.world = world;
    }

    public static BodyFactory getInstance(World world){
        if(thisInstance == null){
            thisInstance = new BodyFactory(world);
        }
        return thisInstance;
    }

    /**
     * @param material - Material for fixture
     * @param shape - shape for fixture
     * @return - Fixturedef
     */
    static public FixtureDef makeFixture(int material, Shape shape){
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        switch(material){
            case BULLET:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0.01f;
                fixtureDef.restitution = 0.01f;
            case SHIP:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0.5f;
                fixtureDef.restitution = 0.01f;
            default:
                fixtureDef.density = 7f;
                fixtureDef.friction = 0.5f;
                fixtureDef.restitution = 0.3f;
        }
        return fixtureDef;
    }

    public Body makeCirclePolyBody(float posx, float posy, float radius, int material, BodyDef.BodyType bodyType, boolean fixedRotation){
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius /2);
        boxBody.createFixture(makeFixture(material,circleShape));
        circleShape.dispose();
        return boxBody;
    }

    public Body makeCirclePolyBody(float posx, float posy, float radius, int material, BodyDef.BodyType bodyType){
        return makeCirclePolyBody(posx, posy, radius, material, bodyType, false);
    }

    public Body makeCirclePolyBody(float posx, float posy, float radius, int material){
        return makeCirclePolyBody(posx, posy, radius, material, BodyDef.BodyType.DynamicBody,  false);
    }

    public Body makeBoxPolyBody(float posx, float posy, float width, float height,int material, BodyDef.BodyType bodyType){
        return makeBoxPolyBody(posx, posy, width, height, material, bodyType, false);
    }

    public Body makeBoxPolyBody(float posx, float posy, float width, float height, int material, BodyDef.BodyType bodyType, boolean fixedRotation){
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(width/2, height/2);
        boxBody.createFixture(makeFixture(material,poly));
        poly.dispose();

        return boxBody;
    }

    public Body makePolygonShapeBody(Vector2[] vertices, float posx, float posy, int material, BodyDef.BodyType bodyType){
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        Body boxBody = world.createBody(boxBodyDef);

        PolygonShape polygon = new PolygonShape();
        polygon.set(vertices);
        boxBody.createFixture(makeFixture(material,polygon));
        polygon.dispose();

        return boxBody;
    }

    public void makeConeSensor(Body body, float size){

        FixtureDef fixtureDef = new FixtureDef();
        //fixtureDef.isSensor = true; // will add in future

        PolygonShape polygon = new PolygonShape();

        float radius = size;
        Vector2[] vertices = new Vector2[5];
        vertices[0] = new Vector2(0,0);
        for (int i = 2; i < 6; i++) {
            float angle = (float) (i  / 6.0 * 145 * DEGTORAD); // convert degrees to radians
            vertices[i-1] = new Vector2( radius * ((float)Math.cos(angle)), radius * ((float)Math.sin(angle)));
        }
        polygon.set(vertices);
        fixtureDef.shape = polygon;
        body.createFixture(fixtureDef);
        polygon.dispose();
    }

    public void makeAllFixturesSensors(Body bod){
        for(Fixture fix :bod.getFixtureList()){
            fix.setSensor(true);
        }
    }

}
