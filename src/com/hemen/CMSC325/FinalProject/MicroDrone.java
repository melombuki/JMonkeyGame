package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 * This class contains fields necessary to create a static physical sphere. And
 * also keeps track of whether it has been hit already by the player or not.
 * 
 * @author Joshua P. Hemen
 */
public class MicroDrone extends Enemy {
    private Sphere s;
    private Geometry g;
    private RigidBodyControl control;
    private final float radius = 2;
  
    public MicroDrone(String name, int points, Material mat) {
        s = new Sphere(32, 32, radius);
        g = new Geometry(name, s);
        g.setMaterial(mat);
        this.setPoints(points);
        
        // Greater radius than geo radius makes for much hit better detection
        control = new RigidBodyControl(new SphereCollisionShape(radius + 0.1f), 1f);
        control.setLinearDamping(0.8f);
        g.addControl(control);
    }
    
    public Geometry getGeo() {return g;}
    
    public RigidBodyControl getRigidBodyControl() {return control;}
}
