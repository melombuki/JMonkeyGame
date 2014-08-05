/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.objects.PhysicsRigidBody;

/**
 *
 * @author melombuki
 */
public class CharControl extends BetterCharacterControl {
    
    public CharControl(float radius, float height, float mass) {
        super(radius, height, mass);
    }

    public PhysicsRigidBody getRigidBody() {
        return rigidBody;
    }
}
