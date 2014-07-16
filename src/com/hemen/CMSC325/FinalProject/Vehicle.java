package com.hemen.CMSC325.FinalProject;

import com.jme3.ai.steering.Obstacle;
import com.jme3.ai.steering.utilities.SimpleObstacle;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * A Vehicle's "forward" is along the local +X axis
 */
public class Vehicle extends Node {
    private float speed = 1.5f; // worldUnits/second
    private float maxSpeed = 1.5f; // worldUnits/second
    private float maxTurnForce = 2f; // max steering force per second (perpendicular to velocity)
                            // if speed is 1 and turn force is 1, then it will turn 45 degrees in a second
    private float mass = 1.0f; // the higher, the slower it turns
    private float collisionRadius = 0.1f;
    private Vector3f velocity = Vector3f.UNIT_Z;

    public Vehicle() {
    }

    /**
     * Take the steering influence and apply the vehicle's mass, max speed,
     * speed, and maxTurnForce to determine the new velocity.
     */
    protected void updateVelocity(Vector3f steeringInfluence, float scale) {
        Vector3f steeringForce = truncate(steeringInfluence, getMaxTurnForce() * scale);
        Vector3f acceleration = steeringForce.divide(getMass());
        Vector3f vel = truncate(getVelocity().add(acceleration), getMaxSpeed());
        setVelocity(vel);

        setLocalTranslation(getLocalTranslation().add(getVelocity().mult(scale)));

        // rotate to face
        Quaternion rotTo = getLocalRotation().clone();
        rotTo.lookAt(getVelocity().normalize(), Vector3f.UNIT_Y);

        setLocalRotation(rotTo);
    }

    /**
     * truncate the length of the vector to the given limit
     */
    private Vector3f truncate(Vector3f source, float limit) {
        if (source.lengthSquared() <= limit*limit) {
            return source;
        } else {
            return source.normalize().scaleAdd(limit, Vector3f.ZERO);
        }
    }

    /**
     * Gets the predicted position for this 'frame', 
     * taking into account current position and velocity.
     * @param tpf time per fram
     */
    public Vector3f getFuturePosition(float tpf) {
        return getWorldTranslation().add(getVelocity());
    }

    protected Obstacle toObstacle() {
        return new SimpleObstacle(getWorldTranslation(), getCollisionRadius(), getVelocity());
    }

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @return the maxSpeed
     */
    public float getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * @param maxSpeed the maxSpeed to set
     */
    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * @return the maxTurnForce
     */
    public float getMaxTurnForce() {
        return maxTurnForce;
    }

    /**
     * @param maxTurnForce the maxTurnForce to set
     */
    public void setMaxTurnForce(float maxTurnForce) {
        this.maxTurnForce = maxTurnForce;
    }

    /**
     * @return the mass
     */
    public float getMass() {
        return mass;
    }

    /**
     * @param mass the mass to set
     */
    public void setMass(float mass) {
        this.mass = mass;
    }

    /**
     * @return the collisionRadius
     */
    public float getCollisionRadius() {
        return collisionRadius;
    }

    /**
     * @param collisionRadius the collisionRadius to set
     */
    public void setCollisionRadius(float collisionRadius) {
        this.collisionRadius = collisionRadius;
    }

    /**
     * @return the velocity
     */
    public Vector3f getVelocity() {
        return velocity;
    }

    /**
     * @param velocity the velocity to set
     */
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }
}