/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

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
public class MicroDroneControlTest {
    
    public MicroDroneControlTest() {
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
     * Test of update method, of class MicroDroneControl.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        float tpf = 0.0F;
        MicroDroneControl instance = null;
        instance.update(tpf);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSpeed method, of class MicroDroneControl.
     */
    @Test
    public void testGetSpeed() {
        System.out.println("getSpeed");
        MicroDroneControl instance = null;
        float expResult = 0.0F;
        float result = instance.getSpeed();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setForce method, of class MicroDroneControl.
     */
    @Test
    public void testSetForce() {
        System.out.println("setForce");
        float force = 0;
        MicroDroneControl instance = null;
        instance.setForce(force);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}