package com.battleships.game.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.battleships.game.BodyFactory;
import com.battleships.game.ShapeFactory;
import com.battleships.game.entity.components.*;
import com.battleships.game.views.MainScreen;
import com.battleships.game.LevelFactory;

public class TiledMapCollisionSystem extends EntitySystem {
    private PooledEngine engine;

    public TiledMapCollisionSystem(PooledEngine en) {
        //MapObjects objects = MainScreen.tiledMap.getLayers().get(4).getObjects();
        MapObjects objects = new MapObjects();
        engine = en;

        for (MapObject object : objects) {
            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;
            if (object instanceof RectangleMapObject) {
                shape = ShapeFactory.getRectangle((RectangleMapObject) object);
            } else if (object instanceof PolygonMapObject) {
                shape = ShapeFactory.getPolygon((PolygonMapObject) object);
            } else if (object instanceof PolylineMapObject) {
                shape = ShapeFactory.getPolyline((PolylineMapObject) object);
            } else if (object instanceof CircleMapObject) {
                shape = ShapeFactory.getCircle((CircleMapObject) object);
            } else {
                continue;
            }

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            Body body = LevelFactory.world.createBody(bodyDef);
            body.createFixture(shape, 1);
            shape.dispose();
        }
    }
}