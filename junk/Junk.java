package junk;

import java.util.Scanner;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class Junk extends BasicGame
{
    World world;
    Camera camera;
    final int velocityIterations = 15;
    final int positionIterations = 15;
    final float pixelsPerMeter = 7f;
    final float simRate = 1.f/5.f;
    //DOMINOES SETTINGS
    final float dwidth = 0.7f;
    final float dheight = 2f;
    float ddensity = 20f;
    final float dfriction = 0.3f;
    final float drestitution = 0.2f;
    final float bdensity = 1f;
    final int baseCount = 10;
    final float bsize = 1f;
    final float brestitution = 0.1f;
    final float bfriction = 0.1f;
    
    public Junk()
    {
        super("Domino Tower");
    }
    
    public void makeDomino(float x, float y, boolean horizontal, World world) 
    {
        PolygonShape sd = new PolygonShape();
        sd.setAsBox(.5f * dwidth, .5f * dheight);
        FixtureDef fd = new FixtureDef();
        fd.shape = sd;
        fd.density = ddensity;
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        fd.friction = dfriction;
        fd.restitution = drestitution;
        bd.position = new Vec2(x, y);
        bd.angle = horizontal ? (float) (Math.PI / 2.0) : 0f;
        Body myBody = world.createBody(bd);
        Fixture f = myBody.createFixture(fd);
        FixtureData fda = new FixtureData();
        fda.setFixture(f);
    }

    @Override
    public void init(GameContainer gc) throws SlickException
    {
        gc.getGraphics().setBackground(Color.lightGray);
        camera = new Camera();
        camera.setPixelsPerMeter(pixelsPerMeter);
        camera.setPosition(new Vector2f(0.f, 50.f));
        world = new World(new Vec2(0f, -9.81f), true);
        
      { // Floor
        PolygonShape sd = new PolygonShape();
        sd.setAsEdge(new Vec2(-50f, 0f), new Vec2(50f, 0f));
        FixtureDef fd = new FixtureDef();
        fd.shape = sd;
        fd.density = 0f;
        fd.friction = 0.3f;

        BodyDef bd = new BodyDef();
        bd.position = new Vec2(0.0f, 0.0f);
        bd.type = BodyType.STATIC;
        Body b = world.createBody(bd);
        Fixture f = b.createFixture(fd);
        FixtureData fda = new FixtureData();
        fda.setFixture(f);
        
        sd = new PolygonShape();
        sd.setAsEdge(new Vec2(-50f, 0f), new Vec2(-50f, 100f));
        fd.shape = sd;
        f = b.createFixture(fd);
        fda = new FixtureData();
        fda.setFixture(f);
        
        sd = new PolygonShape();
        sd.setAsEdge(new Vec2(-50f, 100f), new Vec2(50f, 100f));
        fd.shape = sd;
        f = b.createFixture(fd);
        fda = new FixtureData();
        fda.setFixture(f);
        
        sd = new PolygonShape();
        sd.setAsEdge(new Vec2(50f, 100f), new Vec2(50f, 0f));
        fd.shape = sd;
        f = b.createFixture(fd);
        fda = new FixtureData();
        fda.setFixture(f);
      }

      {
        // Make bullet
        PolygonShape sd = new PolygonShape();
        sd.setAsBox(bsize, bsize);
        FixtureDef fd = new FixtureDef();
        fd.density = bdensity;
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        fd.shape = sd;
        fd.friction = bfriction;
        fd.restitution = brestitution;
        bd.bullet = true;
        // bd.addShape(sd);
        bd.position = new Vec2(30f, 50f);
        Body b = world.createBody(bd);
        Fixture f = b.createFixture(fd);
        FixtureData fda = new FixtureData();
        fda.setFixture(f);
        f.setUserData(fda);
        b.setLinearVelocity(new Vec2(-25f, -25f));
        b.setAngularVelocity(6.7f);

        fd.density = bdensity;
        bd.position = new Vec2(-30f, 25f);
        b = world.createBody(bd);
        f = b.createFixture(fd);
        fda = new FixtureData();
        fda.setFixture(f);
        f.setUserData(fda);
        b.setLinearVelocity(new Vec2(35f, -10f));
        b.setAngularVelocity(-8.3f);
      }

      {
        float currX;
        // Make base
        for (int i = 0; i < baseCount; ++i) {
          currX = i * 1.5f * dheight - (1.5f * dheight * baseCount / 2f);
          makeDomino(currX, dheight / 2.0f, false, world);
          makeDomino(currX, dheight + dwidth / 2.0f, true, world);
        }
        currX = baseCount * 1.5f * dheight - (1.5f * dheight * baseCount / 2f);
        // Make 'I's
        for (int j = 1; j < baseCount; ++j) {
          if (j > 3)
            ddensity *= .8f;
          float currY = dheight * .5f + (dheight + 2f * dwidth) * .99f * j; // y at center of 'I'
                                                                            // structure

          for (int i = 0; i < baseCount - j; ++i) {
            currX = i * 1.5f * dheight - (1.5f * dheight * (baseCount - j) / 2f);// +
                                                                                 // parent.random(-.05f,
                                                                                 // .05f);
            ddensity *= 2.5f;
            if (i == 0) {
              makeDomino(currX - (1.25f * dheight) + .5f * dwidth, currY - dwidth, false, world);
            }
            if (i == baseCount - j - 1) {
              // if (j != 1) //djm: why is this here? it makes it off balance
              makeDomino(currX + (1.25f * dheight) - .5f * dwidth, currY - dwidth, false, world);
            }
            ddensity /= 2.5f;
            makeDomino(currX, currY, false, world);
            makeDomino(currX, currY + .5f * (dwidth + dheight), true, world);
            makeDomino(currX, currY - .5f * (dwidth + dheight), true, world);
          }
        }
      }
      
      {
          BodyDef bd = new BodyDef();
          bd.position = new Vec2(0f, 60f);
          bd.type = BodyType.DYNAMIC;
          Body b = world.createBody(bd);
          PolygonShape ps = new PolygonShape();
          Vec2[] verts = 
          {
              new Vec2(0f, 0f),
              new Vec2(-1f, -1f),
              new Vec2(-2f, -3f),
              new Vec2(2f, -10f),
              new Vec2(3f, -1f)
          };
          ps.set(verts, 5);
          Fixture f = b.createFixture(ps, 50f);
          FixtureData data = new FixtureData();
          data.setFixture(f);
          b.setAngularVelocity(30f);
          b.setLinearVelocity(new Vec2(0f, -60f));
      }
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException
    {
        world.step(simRate * (float)delta / 1000.f, velocityIterations, positionIterations);
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException
    {
        camera.render(world, g, gc);
    }

    public static void main(String[] args) throws SlickException
    {
        System.out.println("Enter the demo you'd like to see.\n"
                + "Options are...\n"
                + "theojansen\n"
                + "tower\n");
        
        Scanner s = new Scanner(System.in);
        String name = s.nextLine();
        AppGameContainer app;
        if(name.equals("theojansen"))
        {
            app = new AppGameContainer(new TheoJansen());
        }
        else
        {
            app = new AppGameContainer(new Junk());
        }
        
        //app.setDisplayMode(800, 750, false);
        app.setDisplayMode(1280, 800, true);
        app.setTitle("UCAR-Personal-Projects: Project Codename Junk");
        //app.setShowFPS(false);
        app.start();
    }
}