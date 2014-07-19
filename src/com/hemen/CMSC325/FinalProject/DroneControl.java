/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author melombuki
 */
public class DroneControl extends RigidBodyControl {
    private Node target;
    private Vector3f steering;
    private float speed = 0;
    private Vector3f prevVelocity;
    private Vector3f currVelocity;

    v = playerLocation.clone();
    v = v.subtract(md.getRigidBodyControl().getPhysicsLocation()).normalizeLocal();
    //v.setY(1);
    md.getRigidBodyControl().applyImpulse(v.mult(0.3f), Vector3f.ZERO);
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        // Update the steering influence
    }
}
