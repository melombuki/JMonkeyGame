/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author melombuki
 */
public class SlideEnemyTest {
    
    public SlideEnemyTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getGeo method, of class SlideEnemy.
     */
    @Test
    public void testGetGeo() {
        System.out.println("getGeo");
        SlideEnemy instance = null;
        Geometry expResult = null;
        Geometry result = instance.getGeo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEnemyControl method, of class SlideEnemy.
     */
    @Test
    public void testGetEnemyControl() {
        System.out.println("getEnemyControl");
        SlideEnemy instance = null;
        SlideEnemyControl expResult = null;
        SlideEnemyControl result = instance.getEnemyControl();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGhostControl method, of class SlideEnemy.
     */
    @Test
    public void testGetGhostControl() {
        System.out.println("getGhostControl");
        SlideEnemy instance = null;
        GhostControl expResult = null;
        GhostControl result = instance.getGhostControl();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of shoot method, of class SlideEnemy.
     */
    @Test
    public void testShoot() {
        System.out.println("shoot");
        Material mat_bullet = null;
        SlideEnemy instance = null;
        Vector3f expResult = null;
        Vector3f result = instance.shoot(mat_bullet);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}