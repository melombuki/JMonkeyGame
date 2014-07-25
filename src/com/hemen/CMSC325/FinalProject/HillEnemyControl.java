package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import java.util.Random;

/**
 * This class controls an object by making it move with random Gaussian motion
 * in the X and Y components of it's location. It can still roll around and
 * follow the terrain as long as there are no hills that are too big for it to
 * roll over.
 * @author Joshua P. Hemen
 */
public class HillEnemyControl extends RigidBodyControl {
    private Random gRand = new Random();
    private float speed = 30f;
    
    public HillEnemyControl(CollisionShape shape, float mass) {
        super(shape, mass);
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        applyCentralForce(new Vector3f((float)gRand.nextGaussian(),
                                       0,
                                       (float)gRand.nextGaussian()).mult(speed));
    }
}
