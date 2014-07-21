package com.hemen.CMSC325.FinalProject;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author melombuki
 */
public class MegaDrone extends Enemy {
    private final static int MAX_MINIONS = 3;
    private Node target;
    private Spatial s;
    private DroneControl control;
    private GhostControl gControl;
    private final float innerRadius = 9;
    private final float outterRadius = 50;
    private long lastSpawnTime = 0;
    private int health = 100;
    public static final int hitPoint = 5;
    public static final int killPoint = 100;
    
    private Set<MicroDrone> minions;
  
    
    public MegaDrone(String name, Material mat, Node target, AssetManager assetManager) {
        s =  assetManager.loadModel("Models/Mothership/Mothership.j3o");
        s.setLocalTranslation(0f, -1f, 0f);
        s.setLocalScale(6f);
        s.setName(name);
        
        // Store handle to the target to pass to minions
        this.target = target;
        
        // Set up the minion queue
        minions = new HashSet<MicroDrone>();
        
        // Creates a rough approximation of the shape and makes it float at Y = 35 
        control = new DroneControl(new SphereCollisionShape(innerRadius), 3f, target, 35f);
        control.setLinearDamping(0.7f);
        control.setAngularDamping(1.0f);
        control.setFriction(0f);
        
        // Set up the ghost control as a proximity detector
        gControl = new GhostControl(new SphereCollisionShape(outterRadius));
        
        s.addControl(control);
        s.addControl(gControl);
    }
    
    public Spatial getSpatial() {return s;}
    
    public RigidBodyControl getRigidBodyControl() {return control;}
    
    public GhostControl getGhostControl() {return gControl;}
    
    public MicroDrone createMicroDrone(Material mat, Vector3f playerLocation) {
        if(System.currentTimeMillis() - lastSpawnTime > 1000 &&
                getMinions().size() <= MAX_MINIONS) {
            lastSpawnTime = System.currentTimeMillis();
            MicroDrone m = new MicroDrone("microDrone", mat, target);
            getMinions().add(m);
            return m;
        }
        return null; // no minion was added to the scene
    }

    public int gethealth() {return health;}
    
    @Override
    public void hit() {
        super.hit();
        health -= 10;
    }
    
    @Override
    public void unhit() {
        super.unhit();
        this.health = 100;
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
