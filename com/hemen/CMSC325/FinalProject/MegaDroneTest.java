/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import java.util.Set;
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
public class MegaDroneTest {
    
    public MegaDroneTest() {
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
     * Test of getSpatial method, of class MegaDrone.
     */
    @Test
    public void testGetSpatial() {
        System.out.println("getSpatial");
        MegaDrone instance = null;
        Spatial expResult = null;
        Spatial result = instance.getSpatial();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEnemyControl method, of class MegaDrone.
     */
    @Test
    public void testGetEnemyControl() {
        System.out.println("getEnemyControl");
        MegaDrone instance = null;
        RigidBodyControl expResult = null;
        RigidBodyControl result = instance.getEnemyControl();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGhostControl method, of class MegaDrone.
     */
    @Test
    public void testGetGhostControl() {
        System.out.println("getGhostControl");
        MegaDrone instance = null;
        GhostControl expResult = null;
        GhostControl result = instance.getGhostControl();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createMicroDrone method, of class MegaDrone.
     */
    @Test
    public void testCreateMicroDrone() {
        System.out.println("createMicroDrone");
        Material mat = null;
        MegaDrone instance = null;
        MicroDrone expResult = null;
        MicroDrone result = instance.createMicroDrone(mat);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of gethealth method, of class MegaDrone.
     */
    @Test
    public void testGethealth() {
        System.out.println("gethealth");
        MegaDrone instance = null;
        int expResult = 0;
        int result = instance.gethealth();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hit method, of class MegaDrone.
     */
    @Test
    public void testHit() {
        System.out.println("hit");
        MegaDrone instance = null;
        instance.hit();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of unhit method, of class MegaDrone.
     */
    @Test
    public void testUnhit() {
        System.out.println("unhit");
        MegaDrone instance = null;
        instance.unhit();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMinions method, of class MegaDrone.
     */
    @Test
    public void testGetMinions() {
        System.out.println("getMinions");
        MegaDrone instance = null;
        Set expResult = null;
        Set result = instance.getMinions();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeMinion method, of class MegaDrone.
     */
    @Test
    public void testRemoveMinion() {
        System.out.println("removeMinion");
        Spatial m = null;
        MegaDrone instance = null;
        boolean expResult = false;
        boolean result = instance.removeMinion(m);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearMinions method, of class MegaDrone.
     */
    @Test
    public void testClearMinions() {
        System.out.println("clearMinions");
        MegaDrone instance = null;
        instance.clearMinions();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}