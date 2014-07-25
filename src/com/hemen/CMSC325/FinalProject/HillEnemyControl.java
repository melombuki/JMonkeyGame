/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import java.util.Random;

/**
 *
 * @author melombuki
 */
public class HillEnemyControl extends RigidBodyControl {
    Random gRand = new Random();
    
    public HillEnemyControl(CollisionShape shape, float mass) {
        super(shape, mass);
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        applyCentralForce(new Vector3f((float)gRand.nextGaussian(),
                                       0,
                                       (float)gRand.nextGaussian()).mult(20f));
    }
}
