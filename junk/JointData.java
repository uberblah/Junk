package junk;

import org.jbox2d.dynamics.joints.Joint;
import org.newdawn.slick.Color;

public class JointData
{
    public Joint joint;
    public Color lineColor;
    public Color fillColor;
    public float lineWidth;
    
    public JointData()
    {
        joint = null;
        lineColor = Color.black;
        fillColor = Color.darkGray;
        lineWidth = 1.5f;
    }
    
    public void setJoint(Joint j)
    {
        joint = j;
        joint.setUserData(this);
    }
}
