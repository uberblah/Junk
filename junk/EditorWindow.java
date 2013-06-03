package junk;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class EditorWindow extends BasicGame
{
    World world;
    Camera camera;
    boolean paused;
    final int velocityIterations = 15;
    final int positionIterations = 15;
    final float pixelsPerMeter = 20f;
    final float simRate = 1.f/1.f;
    
    public EditorWindow()
    {
        super("Editor Window");
    }
    
    @Override
    public void init(GameContainer gc) throws SlickException
    {
        gc.getGraphics().setBackground(Color.lightGray);
        camera = new Camera();
        camera.setPixelsPerMeter(pixelsPerMeter);
        camera.setPosition(new Vector2f(0.f, 0.f));
        world = new World(new Vec2(0f, -9.81f), true);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException
    {
        if(!paused) world.step(simRate * (float)delta / 1000.f, velocityIterations, positionIterations);
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException
    {
        camera.render(world, g, gc);
    }
}