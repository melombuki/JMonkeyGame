package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 * This class creates an extremely basic enemy movement that moves it back and 
 * forth in a straight line alond the Z-axis from distance to -distance not
 * inclusive.
 * 
 * @author Joshua P. Hemen
 */
public class SlideEnemyControl extends RigidBodyControl {
    private float speed = 10.0f;
    private Geometry g;
    private Vector3f location;
    private static final float distance = 65f;
    
    public SlideEnemyControl(Geometry g, CollisionShape shape, float mass) {
        super(shape, mass);
        this.g = g;
    }    
    
    // Moves the object back and froth in a straight line.
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        // Find the current location of the geo controled by this
        location = getPhysicsLocation();
        
        // Switch direction if it reaches the max distance in either direction
        if(Math.abs(location.z) > distance) {
            speed *= -1;
        }

        // Move it back and forth in a straight line
        g.setLocalTranslation(location.add(Vector3f.UNIT_Z.mult(speed*tpf)));
    }
}
