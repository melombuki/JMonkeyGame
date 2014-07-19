/**
 * Pursuit is similar to seek except that the quarry (target) is another moving 
 * character. Effective pursuit requires a prediction of the target's 
 * future position.
 * 
 * @author Brent Owens
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.math.Vector3f;

public class Persuit {
    public Vector3f calculateForce(Vector3f location, 
                                    Vector3f velocity, 
                                    float speed, 
                                    float targetSpeed,  
                                    float tpf,
                                    Vector3f targetVelocity,
                                    Vector3f targetLocation) {
        
        // calculate speed difference to see how far ahead we need to leed
        float speedDiff = targetSpeed - speed;
        float desiredSpeed = (targetSpeed + speedDiff)*tpf;
        Vector3f projectedLocation = targetLocation.add(targetVelocity.mult(desiredSpeed));
        Vector3f desierdVel = projectedLocation.subtract(location).normalize().mult(speed);
        Vector3f steering = desierdVel.subtract(velocity);
        
        return steering;
    }
}
