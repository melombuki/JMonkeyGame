/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

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
public class HillEnemyTest {
    
    public HillEnemyTest() {
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
     * Test of getGeo method, of class HillEnemy.
     */
    @Test
    public void testGetGeo() {
        System.out.println("getGeo");
        HillEnemy instance = null;
        Geometry expResult = null;
        Geometry result = instance.getGeo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEnemyControl method, of class HillEnemy.
     */
    @Test
    public void testGetEnemyControl() {
        System.out.println("getEnemyControl");
        HillEnemy instance = null;
        HillEnemyControl expResult = null;
        HillEnemyControl result = instance.getEnemyControl();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}