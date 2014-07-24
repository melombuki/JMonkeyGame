package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * This class controls the movement of an object by making it "fly" towards
 * its target. It is extremely simple and always tries to fly in a straight line
 * directly to the target.
 * 
 * @author Joshua P. Hemen
 */
public class MicroDroneControl extends RigidBodyControl {
    private Node target;
    private Vector3f force = new Vector3f(5f, 5f, 5f); //default value
    private Vector3f steering;
    
    // Will not float if no value is passed for the height
    public MicroDroneControl(CollisionShape shape, float mass, Node target) {
        super(shape, mass);
        this.target = target;
    }
    
    public MicroDroneControl(CollisionShape shape, Node target) {
        super(shape, 1.0f);
        this.target = target;
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);

        // Update the steering influence
        steering = target.getWorldTranslation().clone();
        steering = steering.subtract(getPhysicsLocation()).normalize();
        
        // Actually move the object towards the target
        applyCentralForce(steering.mult(force));
    }
    
    /*
     * @return the "speed" of the object.
     */
    public float getSpeed() {
        return getLinearVelocity().length();
    }

    /**
     * @param impulse the impulse to set
     */
    public void setForce(Vector3f force) {
        this.force = force;
    }
}
