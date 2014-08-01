package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * This class creates a simple dumb enemy that explodes when in proximity of the
 * player and also shoots at the player in a very dumb manner (ie directly at
 * the player's current position.
 * 
 * @author Joshua P. Hemen
 */
public class SlideEnemy extends Enemy {
    private Box s;
    private Geometry g;
    private SlideEnemyControl control;
    private GhostControl gControl;
    private Spatial target; //need to know location to shoot at it
    private final float size = 2; //half the total length of the enemy cube
    public final static int points = 10;
    private static final float outterRadius = 10;
    private long shotMark = 0; //time since last shot fired
    private long spawnMark = 0;
  
    public SlideEnemy(String name, Material mat, Spatial target) {
        spawnMark = System.currentTimeMillis();
        // Set up the visual parts of this object
        s = new Box(size, size, size);
        g = new Geometry(name, s);
        g.setMaterial(mat);
        this.target = target;
        
        // Set up the physical parts of this object. It has a kinematic control 
        // with an extended ghost control for detection proximity.
        control = new SlideEnemyControl(g, new BoxCollisionShape(Vector3f.UNIT_XYZ.mult(size)), 1f);
        control.setKinematic(true);
        gControl = new GhostControl(new SphereCollisionShape(outterRadius));
        g.addControl(control);
        g.addControl(gControl);
    }
    
    /**
     * This method returns the geometry for this object.
     * @return g The object's geometry
     */
    public Geometry getGeo() {return g;}
    
    /**
     * This mehtod returns the physical control for this object.
     * @return control The actual physical control for the object
     */
    public SlideEnemyControl getEnemyControl() {return control;}

    /**
     * This method returns the proximity sensor ghost control for this object.
     * It is a shpere that extends from the center to a distance of outterRadius.
     * @return gControl The proximity sensor
     */
    public GhostControl getGhostControl() {return gControl;}
    
    /*
     * This method creates a bullet every 2 seconds with the appropriate
     * direction towards the target. The bullet object is null when it should not
     * fire, and will return the actual bullet when it should be fired.
     */
    public Vector3f shoot(Material mat_bullet) {
        if(System.currentTimeMillis() - (shotMark + spawnMark) > 2000 && !isHit()) {
            spawnMark = 0; //nullify spawn marks influence on shooting
            shotMark = System.currentTimeMillis(); //reset the time mark
            return target.getWorldTranslation().subtract(control.getPhysicsLocation()).normalize();
        }
        return null;
    }
}
