/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author melombuki
 */
public class MegaDrone extends Enemy {
    private Sphere s;
    private Geometry g;
    private RigidBodyControl control;
    private GhostControl gControl;
    private final float radius = 5;
  
    public MegaDrone(String name, int points, Material mat) {
        s = new Sphere(32, 32, radius);
        g = new Geometry(name, s);
        g.setMaterial(mat);
        this.setPoints(points);
        
        // Greater radius than geo radius makes for much hit better detection
        control = new RigidBodyControl(new SphereCollisionShape(radius + 0.1f), 1f);
        control.setLinearDamping(0.7f);
        
        // Set up the ghost control as a proximity detector
        gControl = new GhostControl(new SphereCollisionShape(10f));
        
        g.addControl(control);
        g.addControl(gControl);
    }
    
    public Geometry getGeo() {return g;}
    
    public RigidBodyControl getRigidBodyControl() {return control;}
    
    public GhostControl getGhostControl() {return gControl;}
}
