package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * This class controls the movement of an object by making it "fly" towards
 * its target. It is extremely simple and always tries to fly in a straight line
 * directly to the target.
 * 
 * @author Joshua P. Hemen
 */
public class MegaDroneControl extends RigidBodyControl {
    private Node target;
    private Vector3f impulse = new Vector3f(0.15f, 0f, 0.15f); //default value
    private Vector3f steering;
    private Vector3f direction;
    private Quaternion rot = new Quaternion();
    private float height = 0;
    
    // Allows the user to set a floating height value if desired
    public MegaDroneControl(CollisionShape shape, float mass, Node target, float height) {
        super(shape, mass);
        this.target = target;
        this.height = height;
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        // Keep the object floating in the air
            setPhysicsLocation(getPhysicsLocation().setY(height));
        
        // Update the steering influence
        steering = target.getWorldTranslation().clone();
        steering = steering.subtract(getPhysicsLocation()).normalize();
        
        // Actually move the object towards the target
        applyImpulse(steering.mult(impulse), Vector3f.ZERO);
        
        // Rotate the object to look at the target
        direction = getPhysicsLocation().subtract(target.getWorldTranslation());
        setPhysicsRotation(rot.fromAxes(Vector3f.UNIT_Y.cross(direction).normalize(),
                           Vector3f.UNIT_Y,
                           Vector3f.UNIT_Y.cross(direction).normalize()));
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
    public void setImpulse(Vector3f impulse) {
        this.impulse = impulse;
    }
}
