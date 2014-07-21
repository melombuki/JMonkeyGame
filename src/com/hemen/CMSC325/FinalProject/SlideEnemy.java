/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author melombuki
 */
public class SlideEnemy extends Enemy {
    private Box s;
    private Geometry g;
    private SlideEnemyControl control;
    private final float size = 2;
    public final static int points = 25;
  
    public SlideEnemy(String name, Material mat, Node target) {
        s = new Box(size, size, size);
        g = new Geometry(name, s);
        g.setMaterial(mat);
        
        // Greater radius than geo radius makes for much hit better detection
        control = new SlideEnemyControl(new BoxCollisionShape(Vector3f.UNIT_XYZ.mult(size)), target);
        g.addControl(control);
    }
    
    public Geometry getGeo() {return g;}
    
    public SlideEnemyControl getEnemyControl() {return control;}
}
