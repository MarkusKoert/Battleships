package com.battleships.game.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.battleships.game.entity.components.PlayerComponent;
import com.battleships.game.entity.components.TextureComponent;
import com.battleships.game.entity.components.TransformComponent;
import java.util.Comparator;

public class RenderingSystem extends SortedIteratingSystem {
 
    static final float PPM = 12.0f; // sets the amount of pixels each metre of box2d objects contains
 
    // this gets the height and width of our camera frustrum based off the width and height of the screen and our pixel per meter ratio
    static final float FRUSTUM_WIDTH = Gdx.graphics.getWidth()/PPM;
    static final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight()/PPM;
 
    public static final float PIXELS_TO_METRES = 1.0f / PPM; // get the ratio for converting pixels to metres
 
    // static method to get screen width in metres
    private static Vector2 meterDimensions = new Vector2();
    private static Vector2 pixelDimensions = new Vector2();
    public static Vector2 getScreenSizeInMeters(){
        meterDimensions.set(Gdx.graphics.getWidth()*PIXELS_TO_METRES,
                            Gdx.graphics.getHeight()*PIXELS_TO_METRES);
        return meterDimensions;
    }
 
    // static method to get screen size in pixels
    public static Vector2 getScreenSizeInPixesl(){
        pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        return pixelDimensions;
    }
 
    // convenience method to convert pixels to meters
    public static float PixelsToMeters(float pixelValue){
        return pixelValue * PIXELS_TO_METRES;
    }
 
    private SpriteBatch batch; // a reference to our spritebatch
    private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of each other
    private Comparator<Entity> comparator; // a comparator to sort images based on the z position of the transfromComponent
    private OrthographicCamera cam; // a reference to our camera
    private OrthographicCamera guiCam; // a reference to GUI our camera

    // component mappers to get components from entities
    private ComponentMapper<TextureComponent> textureM;
    private ComponentMapper<TransformComponent> transformM;
    private ComponentMapper<PlayerComponent> playerM;

    // Font
    BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/font-export.fnt"));
    Texture texture = new Texture(Gdx.files.internal("images/progress-bar-base.png"));
    Texture texture2 = new Texture(Gdx.files.internal("images/progress-bar.png"));

    @SuppressWarnings("unchecked")
	public RenderingSystem(SpriteBatch batch) {
        // gets all entities with a TransformComponent and TextureComponent
        super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator());
 
        //creates out componentMappers
        textureM = ComponentMapper.getFor(TextureComponent.class);
        transformM = ComponentMapper.getFor(TransformComponent.class);
        playerM = ComponentMapper.getFor(PlayerComponent.class);

        // create the array for sorting entities
        renderQueue = new Array<Entity>();
     
        this.batch = batch;  // set our batch to the one supplied in constructor
 
        // set up the camera to match our screen size
        cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        cam.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);

        // set up the GUI camera to match our screen size
        guiCam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        guiCam.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);
    }
 
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // sort the renderQueue based on z index
        // renderQueue.sort(comparator);

        // update camera and sprite batch
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.enableBlending();
        batch.begin();
 
        // loop through each entity in our render queue
        for (Entity entity : renderQueue) {

            TextureComponent tex = textureM.get(entity);
            TransformComponent t = transformM.get(entity);

            if (tex.region == null || t.isHidden) {
                continue;
            }

            float width = tex.region.getRegionWidth();
            float height = tex.region.getRegionHeight();
 
            float originX = width/2f;
            float originY = height/2f;

            batch.draw(tex.region,
                    t.position.x - originX, t.position.y - originY,
                    originX, originY,
                    width, height,
                    PixelsToMeters(t.scale.x), PixelsToMeters(t.scale.y),
                    t.rotation);
        }

        guiCam.update();
        batch.setProjectionMatrix(guiCam.combined);

        // loop through each entity in our render queue
        for (Entity entity : renderQueue) {
            PlayerComponent pl = playerM.get(entity);
            if (pl != null) {
                if (pl.isThisClient) {

                    float width = (float) pl.currentHealth / (float) pl.maxHealth * 11.4f;
                    batch.draw(texture,3,44,17,3);
                    batch.draw(texture2,7,44.6f,width,1.8f);
                    font.getData().setScale(0.05f);
                    font.setColor(Color.BROWN);
                    font.setUseIntegerPositions(false);
                    font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                    if (pl.lastUpgrade != null & System.currentTimeMillis() < pl.lastUpgradeTime + 5000) {
                        font.draw(batch, "LOOT: " + pl.lastUpgrade, 5, 5);
                    }
                }
            }
        }
        batch.end();
        renderQueue.clear();
    }
 
    @Override
    public void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }
 
    // convenience method to get camera
    public OrthographicCamera getCamera() {
        return cam;
    }
}