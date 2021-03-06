package com.dalugoga.mother.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dalugoga.mother.MotherloadDemo;
import com.dalugoga.mother.Objects.Driller;
import com.dalugoga.mother.Scenes.Hud;


public class PlayScreen implements Screen{

    private MotherloadDemo game;
    Texture texture;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d
    private World world;
    private Box2DDebugRenderer b2dr;

    Driller driller;


    public PlayScreen(MotherloadDemo game){
        this.game = game;
        texture = new Texture("badlogic.jpg");
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(MotherloadDemo.V_WIDTH/ MotherloadDemo.PPM, MotherloadDemo.V_HEIGHT/ MotherloadDemo.PPM, gamecam);
        hud = new Hud(game.batch);

        maploader = new TmxMapLoader();
        map = maploader.load("map.tmx");

        renderer = new OrthogonalTiledMapRenderer(map, 1/ MotherloadDemo.PPM);

        gamecam.position.set(gamePort.getWorldWidth()/2, 1200, 0);


        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect =((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MotherloadDemo.PPM, (rect.getY() + rect.getHeight() /2) / MotherloadDemo.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2/ MotherloadDemo.PPM, rect.getHeight() / 2/ MotherloadDemo.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect =((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2)/ MotherloadDemo.PPM, (rect.getY() + rect.getHeight() /2)/ MotherloadDemo.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2/ MotherloadDemo.PPM, rect.getHeight() / 2/ MotherloadDemo.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        driller = new Driller(world);
    }

    @Override
    public void show(){

    }

    public void inputhandler(float dt){
        /*
        int x, y, dx, dy;
        if(Gdx.input.isTouched()) {
            x = Gdx.input.getX();
            y = Gdx.input.getY();


            dx = Gdx.graphics.getWidth()/2 - x;
            dy = Gdx.graphics.getHeight()/2 - y;

            gamecam.position.x -= dx *dt;
            gamecam.position.y += dy *dt;
        }
        */


        if(Gdx.input.isKeyPressed(Input.Keys.UP) && driller.b2body.getLinearVelocity().y <= 6.5){
            driller.b2body.applyLinearImpulse(new Vector2(0, 3f), driller.b2body.getWorldCenter(), true);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && driller.b2body.getLinearVelocity().x >= -6.5){
            driller.b2body.applyLinearImpulse(new Vector2(-0.8f, 0), driller.b2body.getWorldCenter(), true);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && driller.b2body.getLinearVelocity().x <= 6.5){
            driller.b2body.applyLinearImpulse(new Vector2(0.8f, 0), driller.b2body.getWorldCenter(), true);
        }


    }

    public void update(float dt)    {
        inputhandler(dt);

        world.step(1/60f, 6, 2);
        gamecam.position.x = driller.b2body.getPosition().x;
        gamecam.position.y = driller.b2body.getPosition().y;
/*
        System.out.print(driller.getX());
        System.out.print(" ");
        System.out.print(driller.getY());
        System.out.print("\n\n");*/
        gamecam.update();
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        renderer.render();

        b2dr.render(world, gamecam.combined);
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
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

    @Override
    public void dispose() {

    }
}
