/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author melombuki
 */
public class DroneControl extends RigidBodyControl {
    //private Persuit persuit = new Persuit();
    
    private Node target;
    private Vector3f steering;
    private Vector3f prevVelocity;
    private Vector3f currVelocity;
    
    public DroneControl(CollisionShape shape, float mass, Node target) {
        super(shape, mass);
        prevVelocity = currVelocity = Vector3f.ZERO;
        this.target = target;
    }
    
    public DroneControl(CollisionShape shape, Node target) {
        super(shape, 1.0f);
        prevVelocity = currVelocity = Vector3f.ZERO;
        this.target = target;
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        // Update the steering influence
        steering = target.getWorldTranslation().clone();
        steering = steering.subtract(getPhysicsLocation()).normalizeLocal();
        steering.setY(1);
        applyImpulse(steering.mult(0.3f), Vector3f.ZERO);
//        steering = persuit.calculateForce(getPhysicsLocation(),
//                                           getLinearVelocity(),
//                                           getSpeed(),
//                                           1.5f,
//                                           tpf,
//                                           target.,
//                                           currVelocity);
    }
    
    public float getSpeed() {
        return getLinearVelocity().length();
    }
}
