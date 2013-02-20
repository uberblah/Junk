package junk;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

public class TheoJansen extends BasicGame
{
    World world;
    Camera camera;
    final int velocityIterations = 15;
    final int positionIterations = 15;
    final float pixelsPerMeter = 20f;
    final float simRate = 1.f/1.f;
    
    Vec2 m_offset = new Vec2();
    Body m_chassis;
    Body m_wheel;
    RevoluteJoint m_motorJoint;
    boolean m_motorOn;
    float m_motorSpeed;
    
    public TheoJansen()
    {
        super("Theo Jansen Walker");
    }
    
    void createLeg(float s, Vec2 wheelAnchor)
    {
        Vec2 p1 = new Vec2(5.4f * s, -6.1f);
        Vec2 p2 = new Vec2(7.2f * s, -1.2f);
        Vec2 p3 = new Vec2(4.3f * s, -1.9f);
        Vec2 p4 = new Vec2(3.1f * s, 0.8f);
        Vec2 p5 = new Vec2(6.0f * s, 1.5f);
        Vec2 p6 = new Vec2(2.5f * s, 3.7f);

        FixtureDef fd1 = new FixtureDef();
        FixtureDef fd2 = new FixtureDef();
        fd1.filter.groupIndex = -1;
        fd2.filter.groupIndex = -1;
        fd1.density = 1.0f;
        fd2.density = 1.0f;

        PolygonShape poly1 = new PolygonShape();
        PolygonShape poly2 = new PolygonShape();

        if (s > 0.0f)
        {
            Vec2[] vertices = new Vec2[3];

            vertices[0] = p1;
            vertices[1] = p2;
            vertices[2] = p3;
            poly1.set(vertices, 3);

            vertices[0] = new Vec2();
            vertices[1] = p5.sub(p4);
            vertices[2] = p6.sub(p4);
            poly2.set(vertices, 3);
        }
        else
        {
            Vec2[] vertices = new Vec2[3];

            vertices[0] = p1;
            vertices[1] = p3;
            vertices[2] = p2;
            poly1.set(vertices, 3);

            vertices[0] = new Vec2();
            vertices[1] = p6.sub(p4);
            vertices[2] = p5.sub(p4);
            poly2.set(vertices, 3);
        }

        fd1.shape = poly1;
        fd2.shape = poly2;

        BodyDef bd1 = new BodyDef(), bd2 = new BodyDef();
        bd1.type = BodyType.DYNAMIC;
        bd2.type = BodyType.DYNAMIC;
        bd1.position = m_offset;
        bd2.position = p4.add(m_offset);

        bd1.angularDamping = 10.0f;
        bd2.angularDamping = 10.0f;

        Body body1 = world.createBody(bd1);
        Body body2 = world.createBody(bd2);

        Fixture f1 = body1.createFixture(fd1);
        Fixture f2 = body2.createFixture(fd2);
        FixtureData d1 = new FixtureData();
        FixtureData d2 = new FixtureData();
        d1.fillColor = Color.red;
        d2.fillColor = Color.blue;
        d1.fillColor.a = 0.2f;
        d2.fillColor.a = 0.2f;
        d1.setFixture(f1);
        d2.setFixture(f2);

        DistanceJointDef djd = new DistanceJointDef();

        // Using a soft distance constraint can reduce some jitter.
        // It also makes the structure seem a bit more fluid by
        // acting like a suspension system.
        djd.dampingRatio = 0.5f;
        djd.frequencyHz = 10.0f;

	djd.initialize(body1, body2, p2.add(m_offset), p5.add(m_offset));
	Joint j = world.createJoint(djd);
        JointData jd = new JointData();
        jd.setJoint(j);

	djd.initialize(body1, body2, p3.add(m_offset), p4.add(m_offset));
	j = world.createJoint(djd);
        jd = new JointData();
        jd.setJoint(j);

	djd.initialize(body1, m_wheel, p3.add(m_offset), wheelAnchor.add(m_offset));
	j = world.createJoint(djd);
        jd = new JointData();
        jd.setJoint(j);

	djd.initialize(body2, m_wheel, p6.add(m_offset), wheelAnchor.add(m_offset));
	j = world.createJoint(djd);
        jd = new JointData();
        jd.setJoint(j);

	RevoluteJointDef rjd = new RevoluteJointDef();

	rjd.initialize(body2, m_chassis, p4.add(m_offset));
	j = world.createJoint(rjd);
        jd = new JointData();
        jd.setJoint(j);
    }
    
    @Override
    public void init(GameContainer gc) throws SlickException
    {
        gc.getGraphics().setBackground(Color.lightGray);
        camera = new Camera();
        camera.setPixelsPerMeter(pixelsPerMeter);
        camera.setPosition(new Vector2f(0.f, 0.f));
        world = new World(new Vec2(0f, -9.81f), true);
        
        m_offset.set(0.0f, 8.0f);
        m_motorSpeed = 2.0f;
        m_motorOn = true;
        Vec2 pivot = new Vec2(0.0f, 0.8f);

        // Ground
        {
            BodyDef bd = new BodyDef();
            Body ground = world.createBody(bd);

            PolygonShape shape = new PolygonShape();
            shape.setAsEdge(new Vec2(-50.0f, 0.0f), new Vec2(50.0f, 0.0f));
            Fixture f = ground.createFixture(shape, 0.0f);
            FixtureData data = new FixtureData();
            data.setFixture(f);

            shape.setAsEdge(new Vec2(-50.0f, 0.0f), new Vec2(-50.0f, 10.0f));
            f = ground.createFixture(shape, 0.0f);
            data = new FixtureData();
            data.setFixture(f);

            shape.setAsEdge(new Vec2(50.0f, 0.0f), new Vec2(50.0f, 10.0f));
            f = ground.createFixture(shape, 0.0f);
            data = new FixtureData();
            data.setFixture(f);
        }

        // Balls
        for (int i = 0; i < 40; ++i)
        {
            CircleShape shape = new CircleShape();
            shape.m_radius = 0.25f;

            BodyDef bd = new BodyDef();
            bd.type = BodyType.DYNAMIC;
            bd.position.set(-40.0f + 2.0f * i, 0.5f);

            Body body = world.createBody(bd);
            Fixture f = body.createFixture(shape, 1.0f);
            FixtureData data = new FixtureData();
            data.setFixture(f);
        }

        // Chassis
        {
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(2.5f, 1.0f);

            FixtureDef sd = new FixtureDef();
            sd.density = 1.0f;
            sd.shape = shape;
            sd.filter.groupIndex = -1;
            BodyDef bd = new BodyDef();
            bd.type = BodyType.DYNAMIC;
            bd.position.set(pivot).addLocal(m_offset);
            m_chassis = world.createBody(bd);
            Fixture f = m_chassis.createFixture(sd);
            FixtureData data = new FixtureData();
            data.setFixture(f);
        }

        {
            CircleShape shape = new CircleShape();
            shape.m_radius = 1.6f;

            FixtureDef sd = new FixtureDef();
            sd.density = 1.0f;
            sd.shape = shape;
            sd.filter.groupIndex = -1;
            BodyDef bd = new BodyDef();
            bd.type = BodyType.DYNAMIC;
            bd.position.set(pivot).addLocal(m_offset);
            m_wheel = world.createBody(bd);
            Fixture f = m_wheel.createFixture(sd);
            FixtureData data = new FixtureData();
            data.setFixture(f);
        }

        {
            RevoluteJointDef jd = new RevoluteJointDef();

            jd.initialize(m_wheel, m_chassis, pivot.add(m_offset));
            jd.collideConnected = false;
            jd.motorSpeed = m_motorSpeed;
            jd.maxMotorTorque = 400.0f;
            jd.enableMotor = m_motorOn;
            m_motorJoint = (RevoluteJoint)world.createJoint(jd);
        }

        Vec2 wheelAnchor;

        wheelAnchor = pivot.add(new Vec2(0.0f, -0.8f));

        createLeg(-1.0f, wheelAnchor);
        createLeg(1.0f, wheelAnchor);

        m_wheel.setTransform(m_wheel.getPosition(), 120.0f * MathUtils.PI / 180.0f);
        createLeg(-1.0f, wheelAnchor);
        createLeg(1.0f, wheelAnchor);

        m_wheel.setTransform(m_wheel.getPosition(), -120.0f * MathUtils.PI / 180.0f);
        createLeg(-1.0f, wheelAnchor);
        createLeg(1.0f, wheelAnchor);
    }

    @Override
    public void update(GameContainer gc, int delta) throws SlickException
    {
        Vec2 pos = m_chassis.getPosition();
        camera.setPosition(new Vector2f(pos.x, pos.y));
        world.step(simRate * (float)delta / 1000.f, velocityIterations, positionIterations);
    }

    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException
    {
        camera.render(world, g, gc);
    }
}