/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author melombuki
 */
public class HillEnemyControl extends CharacterControl {
    private Node target;
    private Vector3f steering;
    private Vector3f direction;
    
    public HillEnemyControl(CollisionShape shape, Node target) {
        super(shape, 0.05f);
        this.target = target;
    }    
}
