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
public class myBall {
    private boolean isHit = false;
    private Sphere s;
    private Geometry g;
    private RigidBodyControl control;
    public final int points = 100;
  
    public myBall(String name, int zSamples, int radialSamples, float radius,
            Material mat) {
        s = new Sphere(zSamples, radialSamples, radius);
        g = new Geometry(name, s);
        g.setMaterial(mat);
        
        // Greater radius than geo radius makes for much hit better detection
        control = new RigidBodyControl(new SphereCollisionShape(2.1f), 1f);
        control.setLinearDamping(0.6f);
    }
    
    public boolean isHit() {return isHit;}
    
    public void hitBall() {isHit = true;}
    
    public void unhitBall() {isHit = false;}
    
    public Geometry getGeo() {return g;}
    
    public RigidBodyControl getRigidBodyControl() {return control;}
}
