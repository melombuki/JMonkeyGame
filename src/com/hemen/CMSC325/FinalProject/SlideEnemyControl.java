/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author melombuki
 */
public class SlideEnemyControl extends RigidBodyControl {
    private Node target;
    private float speed = 10.0f;
    private Geometry g;
    private Vector3f location;
    
    public SlideEnemyControl(Geometry g, CollisionShape shape, float mass, Node target) {
        super(shape, mass);
        this.target = target;
        this.g = g;
    }    
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        // Find the current location of the geo controled by this
        location = getPhysicsLocation();
        
        // Move it back and forth in a straight line
        if(Math.abs(location.z) > 65f) {
            speed *= -1;
        }
        
        g.setLocalTranslation(location.add(Vector3f.UNIT_Z.mult(speed*tpf)));
    }
}
