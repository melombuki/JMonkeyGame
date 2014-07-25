/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
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
public class ShockwaveTest {
    
    public ShockwaveTest() {
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
     * Test of explode method, of class Shockwave.
     */
    @Test
    public void testExplode_3args() {
        System.out.println("explode");
        float x = 0.0F;
        float y = 0.0F;
        float z = 0.0F;
        Shockwave instance = null;
        instance.explode(x, y, z);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of explode method, of class Shockwave.
     */
    @Test
    public void testExplode_Vector3f() {
        System.out.println("explode");
        Vector3f v = null;
        Shockwave instance = null;
        instance.explode(v);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getShockWave method, of class Shockwave.
     */
    @Test
    public void testGetShockWave() {
        System.out.println("getShockWave");
        Shockwave instance = null;
        ParticleEmitter expResult = null;
        ParticleEmitter result = instance.getShockWave();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}