/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author melombuki
 */
public class DroneControl extends RigidBodyControl {
    //private Persuit persuit = new Persuit();
    
    private Node target;
    private Vector3f impulse = new Vector3f(0.15f, 0f, 0.15f);
    private Vector3f steering;
    private Vector3f direction;
    private Vector3f v;
    private Quaternion rot = new Quaternion();
    
    public DroneControl(CollisionShape shape, float mass, Node target) {
        super(shape, mass);
        this.target = target;
    }
    
    public DroneControl(CollisionShape shape, Node target) {
        super(shape, 1.0f);
        this.target = target;
        rot = new Quaternion(Quaternion.IDENTITY);
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        setPhysicsLocation(getPhysicsLocation().setY(35f));
        // Update the steering influence
        steering = target.getWorldTranslation().clone();
        steering = steering.subtract(getPhysicsLocation()).normalize();
        applyImpulse(steering.mult(impulse), Vector3f.ZERO);
//        steering = persuit.calculateForce(getPhysicsLocation(),
//                                           getLinearVelocity(),
//                                           getSpeed(),
//                                           1.5f,
//                                           tpf,
//                                           target.,
//                                           currVelocity);
        direction = getPhysicsLocation().subtract(target.getWorldTranslation());
        setPhysicsRotation(rot.fromAxes(Vector3f.UNIT_Y.cross(direction).normalize(),
                           Vector3f.UNIT_Y,
                           Vector3f.UNIT_Y.cross(direction).normalize()));
    }
    
    public float getSpeed() {
        return getLinearVelocity().length();
    }

    /**
     * @param impulse the impulse to set
     */
    public void setImpulse(Vector3f impulse) {
        this.impulse = impulse;
    }
}
