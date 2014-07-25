package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.PQTorus;

/**
 *
 * @author melombuki
 */
public class HillEnemy extends Enemy {
    private PQTorus s;
    private Geometry g;
    private HillEnemyControl control;
    private final float radius = 2f;
    public final static int points = 5;
  
    public HillEnemy(String name, Material mat) {
        s = new PQTorus(5, 3, radius/2, 1f, 32, 32); //Spiral torus
        g = new Geometry(name, s);
        g.setMaterial(mat);
        
        // Greater radius than geo radius makes for much hit better detection
        control = new HillEnemyControl(new SphereCollisionShape(radius), 1.0f);
        g.addControl(control);
    }
    
    public Geometry getGeo() {return g;}
    
    public HillEnemyControl getEnemyControl() {return control;}
}
