package junk;

import java.util.Vector;
import org.jbox2d.dynamics.Body;

/**
 * A class for importing and exporting worlds or sections thereof.
 * @author uberblah
 */
public class Scene
{
    private Vector<Body> bodies;
    
    void addBody(Body b)
    {
        
    }
    
    Vector<Body> getBodies()
    {
        return new Vector<Body>();
    }
    
    boolean save(String filename)
    {
        return false;
    }
    
    boolean load(String filename)
    {
        return false;
    }
}
