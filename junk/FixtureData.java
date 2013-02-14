package junk;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;

public class FixtureData
{
    public Fixture fixture;
    public Shape shape;
    public Color lineColor;
    public Color fillColor;
    public float lineWidth;
    
    public FixtureData()
    {
        shape = null;
        fixture = null;
        lineColor = Color.black;
        fillColor = Color.darkGray;
        fillColor.a = 0.2f;
        lineWidth = 1.5f;
    }
    
    public void updateShape()
    {
        if(fixture == null) return;
        ShapeType st = fixture.getType();
        switch(st)
        {
            case POLYGON:
            {
                PolygonShape ps = (PolygonShape)fixture.getShape();
                Vec2[] verts = ps.getVertices();
                int count = ps.getVertexCount();
                Polygon p = new Polygon();
                for(int i = 0; i < count; i++)
                {
                    p.addPoint(-verts[i].x, verts[i].y);
                }
                shape = p;
                shape.preCache();
                break;
            }
            case CIRCLE:
            {
                CircleShape cs = (CircleShape)fixture.getShape();
                shape = new Circle(cs.m_p.x, cs.m_p.y, cs.m_radius);
                shape.preCache();
                break;
            }
            default:
        }
    }
    
    public void setFixture(Fixture f)
    {
        fixture = f;
        fixture.setUserData(this);
        updateShape();
    }
}
