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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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
    private DroneControl control;
    private GhostControl gControl;
    private final float innerRadius = 10;
    private final float outterRadius = 50;
    private long lastSpawnTime = 0;
    private int hitPoints = 100;
    public static final int points = 500;
    
    private Set<MicroDrone> minions;
  
    
    public MegaDrone(String name, Material mat, Node target) {
        s = new Sphere(32, 32, innerRadius);
        g = new Geometry(name, s);
        g.setMaterial(mat);
        
        // Set up the minion queue
        minions = new HashSet<MicroDrone>();
        
        // Greater innerRadius than geo innerRadius makes for much hit better detection
        control = new DroneControl(new SphereCollisionShape(innerRadius + 0.1f), 3f, target);
        control.setLinearDamping(0.7f);
        control.setAngularDamping(1.0f);
        
        // Set up the ghost control as a proximity detector
        gControl = new GhostControl(new SphereCollisionShape(outterRadius));
        
        g.addControl(control);
        g.addControl(gControl);
    }
    
    public Geometry getGeo() {return g;}
    
    public RigidBodyControl getRigidBodyControl() {return control;}
    
    public GhostControl getGhostControl() {return gControl;}
    
    public MicroDrone createMicroDrone(Material mat, Vector3f playerLocation) {
        if(System.currentTimeMillis() - lastSpawnTime > 1000 &&
                getMinions().size() <= MAX_MINIONS) {
            lastSpawnTime = System.currentTimeMillis();
            MicroDrone m = new MicroDrone("microDrone", mat);
            getMinions().add(m);
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
    
    @Override
    public void unhit() {
        super.unhit();
        this.hitPoints = 100;
    }

    /**
     * @return the minions
     */
    public Set<MicroDrone> getMinions() {
        return minions;
    }
    
    public boolean removeMinion(Spatial m) {
        for(MicroDrone md : minions) {
            if(md.getGeo().hashCode() == m.hashCode()) {
                if(minions.remove(md)) {
                    return false;
                }
            }
        }
        return false;
    }
    
    public void clearMinions() {
        minions.clear();
    }
}
