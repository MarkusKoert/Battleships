package com.battleships.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.battleships.game.controller.KeyboardController;
import com.battleships.game.loader.B2dAssetManager;

public class B2DModel {
    public World world;
    private Body bodys;
    private Body bodyd;
    private Body bodyk;
    private KeyboardController controller;
    private Camera camera;
    public Body player;
    private B2dAssetManager assMan;

    private Sound buttonClick;
    private Sound buttonHover;

    public static final int BUTTON_HOVER = 0; // new
    public static final int BUTTON_CLICK = 1; //new

    public final int BODY_WIDTH = 2;
    public final int BODY_HEIGHT = 2;
    // for moving map
    public final float CAM_UPDATE = 1/16f;


    public B2DModel(KeyboardController cont, OrthographicCamera cam, B2dAssetManager assetManager){
        assMan = assetManager;
        camera = cam;
        controller = cont;
        world = new World(new Vector2(0,0), true);
        world.setContactListener(new B2dContactListener(this));
        createObject();
        // createMovingObject();
        // createStaticObject();

        // tells our asset manger that we want to load the images set in loadImages method
        assMan.queueAddSounds();
        // tells the asset manager to load the images and wait until finsihed loading.
        assMan.manager.finishLoading();
        // loads the 2 sounds we use
        buttonClick = assMan.manager.get("sounds/button_click.wav", Sound.class);
        buttonHover = assMan.manager.get("sounds/button_hover.mp3", Sound.class);

        // get our body factory singleton and store it in bodyFactory
        BodyFactory bodyFactory = BodyFactory.getInstance(world);

        // add a player
        player = bodyFactory.makeBoxPolyBody(1, 1, BODY_WIDTH, BODY_HEIGHT, BodyFactory.RUBBER, BodyDef.BodyType.DynamicBody,false);
    }

    public void logicStep(float delta) {
        // check if mouse1 is down (player click) then if true check if point intersects
        if(controller.isMouse1Down && pointIntersectsBody(player,controller.mouseLocation)){
            System.out.println("Player was clicked");
            playSound(BUTTON_HOVER);
        }

        if(controller.left){
            player.applyForceToCenter(-10, 0,true);
            camera.translate((-CAM_UPDATE),0,0);
        }else if(controller.right){
            player.applyForceToCenter(10, 0,true);
            camera.translate(CAM_UPDATE,0,0);
        }else if(controller.up){
            player.applyForceToCenter(0, 10,true);
            camera.translate(0,CAM_UPDATE,0);
        }else if(controller.down){
            player.applyForceToCenter(0, -10,true);
            camera.translate(0,-CAM_UPDATE,0);
        }
        world.step(delta, 3, 3);
    }

    /**
     * Checks if point is in first fixture
     * Does not check all fixtures.....yet
     *
     * @param body the Box2D body to check
     * @param mouseLocation the point on the screen
     * @return true if click is inside body
     */
    public boolean pointIntersectsBody(Body body, Vector2 mouseLocation){
        Vector3 mousePos = new Vector3(mouseLocation,0); //convert mouseLocation to 3D position
        camera.unproject(mousePos); // convert from screen potition to world position
        if(body.getFixtureList().first().testPoint(mousePos.x, mousePos.y)){
            return true;
        }
        return false;
    }

    private void createObject(){
        // Create a new body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0,0);

        // Add body to world
        bodyd = world.createBody(bodyDef);

        // set the shape of the body
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);

        // set object properties
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        // create physical object
        bodyd.createFixture(shape, 0.0f);

        // dispose shape as it's no longer needed
        shape.dispose();
    }

    private void createStaticObject() {
        // create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, -10);
        // add it to the world
        bodys = world.createBody(bodyDef);
        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2, 1);
        // create the physical object in our body)
        // without this our body would just be data in the world
        bodys.createFixture(shape, 0.0f);
        // we no longer use the shape object here so dispose of it.
        shape.dispose();
    }

    private void createMovingObject(){

        //create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(0,-12);


        // add it to the world
        bodyk = world.createBody(bodyDef);

        // set the shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1,1);

        // set the properties of the object ( shape, weight, restitution(bouncyness)
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        // create the physical object in our body)
        // without this our body would just be data in the world
        bodyk.createFixture(shape, 0.0f);

        // we no longer use the shape object here so dispose of it.
        shape.dispose();

        bodyk.setLinearVelocity(0, 0.75f);
    }

    public void playSound(int sound){
        switch(sound){
            case BUTTON_HOVER:
                buttonHover.play();
                break;
            case BUTTON_CLICK:
                buttonClick.play();
                break;
        }
    }
}
