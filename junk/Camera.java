package junk;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;

public class Camera
{
    Vector2f worldPos;
    float pixelsPerMeter;
    float minPPM;
    float maxPPM;
    
    public Camera()
    {
        worldPos = new Vector2f();
        pixelsPerMeter = 10f;
        maxPPM = 50f;
        minPPM = 5f;
    }
    
    public void setPosition(Vector2f pos)
    {
        worldPos = pos;
    }
    
    public void addPosition(Vector2f dp)
    {
        worldPos.add(dp);
    }
    
    public Vector2f getPosition()
    {
        return worldPos;
    }
    
    public void setMaxPPM(float newMax)
    {
        maxPPM = newMax;
    }
    
    public float getMaxPPM()
    {
        return maxPPM;
    }
    
    public void setMinPPM(float newMin)
    {
        minPPM = newMin;
    }
    
    public float getMinPPM()
    {
        return minPPM;
    }
    
    public void setPixelsPerMeter(float pixels)
    {
        pixelsPerMeter = pixels;
    }
    
    public void addPixelsPerMeter(float dp)
    {
        pixelsPerMeter += dp;
        if(pixelsPerMeter > maxPPM) pixelsPerMeter = maxPPM;
        else if(pixelsPerMeter < minPPM) pixelsPerMeter = minPPM;
    }
    
    public float getPixelsPerMeter()
    {
        return pixelsPerMeter;
    }
    
    public Vector2f screenToWorld(Vector2f screenCoords, GameContainer gc)
    {
        float metersPerPixel = 1.f / pixelsPerMeter;
        return new Vector2f
        (
            (screenCoords.x - 0.5f * gc.getWidth()) * metersPerPixel + worldPos.x,
           -(screenCoords.y - 0.5f * gc.getHeight()) * metersPerPixel - worldPos.y
        );
    }
    
    public Vector2f worldToScreen(Vector2f worldCoords, GameContainer gc)
    {
        return new Vector2f
        (
            (worldCoords.x - worldPos.x) * pixelsPerMeter + (0.5f * gc.getWidth()),
            (worldCoords.y - worldPos.y) * pixelsPerMeter + (0.5f * gc.getHeight())
        );
    }
    
    public void render(World w, Graphics g, GameContainer gc)
    {
        Transform scale = Transform.createScaleTransform(pixelsPerMeter, pixelsPerMeter);
        g.pushTransform();
        g.translate(-worldPos.x * pixelsPerMeter + (0.5f * gc.getWidth()), 
                    worldPos.y * pixelsPerMeter + (0.5f * gc.getHeight()));
        
        Joint j = w.getJointList();
        while(j != null)
        {
            Object o = j.getUserData();
            if(o != null)
            {
                JointData jd = (JointData)o;
                Shape s;
                switch(j.getType())
                {
                    case DISTANCE:
                        Polygon p = new Polygon();
                        Vec2 a1 = new Vec2();
                        Vec2 a2 = new Vec2();
                        j.getAnchorA(a1);
                        j.getAnchorB(a2);
                        a1.mulLocal(pixelsPerMeter);
                        a2.mulLocal(pixelsPerMeter);
                        p.addPoint(a1.x, -a1.y);
                        p.addPoint(a2.x, -a2.y);
                        s = p;
                        break;
                    default:
                        s = null;
                }
                if(s != null)
                {
                    
                    g.setLineWidth(jd.lineWidth);
                    g.setColor(jd.lineColor);
                    g.draw(s);
                    g.setColor(jd.fillColor);
                    g.fill(s);
                }
            }
            j = j.getNext();
        }
        
        Body current = w.getBodyList();
        while(current != null)
        {
            Transform t = new Transform(scale);
            t.concatenate(Transform.createRotateTransform(-current.getAngle() + MathUtils.PI));
            Vec2 pos = current.getPosition();
            g.pushTransform();
            g.translate(pos.x * pixelsPerMeter, -pos.y * pixelsPerMeter);
            Fixture f = current.getFixtureList();
            while(f != null)
            {
                Object o = f.getUserData();
                if(o != null)
                {
                    FixtureData fd = (FixtureData)o;
                    Shape shape = fd.shape.transform(t);

                    g.setColor(fd.fillColor);
                    g.fill(shape);
                    g.setLineWidth(fd.lineWidth);
                    g.setColor(fd.lineColor);
                    g.draw(shape);
                }
                f = f.getNext();
            }
            g.popTransform();
            current = current.getNext();
        }
        g.popTransform();
    }
}
