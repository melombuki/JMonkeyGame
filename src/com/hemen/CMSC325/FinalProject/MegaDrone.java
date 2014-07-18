/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author melombuki
 */
public class MegaDrone extends Enemy {
    private final int MAX_MINIONS = 3;
    private Sphere s;
    private Geometry g;
    private RigidBodyControl control;
    private GhostControl gControl;
    private final float radius = 10;
    private long lastSpawnTime = 0;
    private int hitPoints = 100;
    
    Set<MicroDrone> minions;
  
    
    public MegaDrone(String name, int points, Material mat) {
        s = new Sphere(32, 32, radius);
        g = new Geometry(name, s);
        g.setMaterial(mat);
        setPoints(500);
        
        
        // Set up the minion queue
        minions = new HashSet<MicroDrone>();
        
        // Greater radius than geo radius makes for much hit better detection
        control = new RigidBodyControl(new SphereCollisionShape(radius + 0.1f), 1f);
        control.setLinearDamping(0.7f);
        
        // Set up the ghost control as a proximity detector
        gControl = new GhostControl(new SphereCollisionShape(radius*3f));
        
        g.addControl(control);
        g.addControl(gControl);
    }
    
    public Geometry getGeo() {return g;}
    
    public RigidBodyControl getRigidBodyControl() {return control;}
    
    public GhostControl getGhostControl() {return gControl;}
    
    public MicroDrone createMicroDrone(Material mat, Vector3f playerLocation) {
        System.out.println(System.currentTimeMillis() - lastSpawnTime);
        if(System.currentTimeMillis() - lastSpawnTime > 1000 &&
                minions.size() <= MAX_MINIONS) {
            lastSpawnTime = System.currentTimeMillis();
            MicroDrone m = new MicroDrone("microDrone", mat);
            minions.add(m);
            return m;
        }
        return null; // no minion was added to the scene
    }

    public int getHitPoints() {return hitPoints;}
    
    @Override
    public void hit() {
        super.hit();
        hitPoints -= 10;
    }
}
