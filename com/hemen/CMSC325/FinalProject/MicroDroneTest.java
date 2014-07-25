/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.bullet.control.RigidBodyControl;
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
public class MicroDroneTest {
    
    public MicroDroneTest() {
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
     * Test of getGeo method, of class MicroDrone.
     */
    @Test
    public void testGetGeo() {
        System.out.println("getGeo");
        MicroDrone instance = null;
        Geometry expResult = null;
        Geometry result = instance.getGeo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEnemyControl method, of class MicroDrone.
     */
    @Test
    public void testGetEnemyControl() {
        System.out.println("getEnemyControl");
        MicroDrone instance = null;
        RigidBodyControl expResult = null;
        RigidBodyControl result = instance.getEnemyControl();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}