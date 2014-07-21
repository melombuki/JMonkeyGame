package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
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
    private Node target; //need to know location to shoot at it
    private final float size = 2;
    public final static int points = 10;
    private static final float outterRadius = 10;
  
    public SlideEnemy(String name, Material mat, Node target) {
        // Set up the visual parts of this object
        s = new Box(size, size, size);
        g = new Geometry(name, s);
        g.setMaterial(mat);
        this.target = target;
        
        // Set up the physical parts of this object. It has a kinematic control 
        // with an extended ghost control for detection proximity.
        control = new SlideEnemyControl(g, new BoxCollisionShape(Vector3f.UNIT_XYZ.mult(size)), 1f, target);
        control.setKinematic(true);
        gControl = new GhostControl(new SphereCollisionShape(outterRadius));
        g.addControl(control);
        g.addControl(gControl);
    }
    
    public Geometry getGeo() {return g;}
    
    public SlideEnemyControl getEnemyControl() {return control;}

    /**
     * @return the gControl
     */
    public GhostControl getGhostControl() {return gControl;}
}
