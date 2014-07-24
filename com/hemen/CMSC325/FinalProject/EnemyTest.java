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
public class EnemyTest {
    
    public EnemyTest() {
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
     * Test of isHit method, of class Enemy.
     */
    @Test
    public void testIsHit() {
        System.out.println("isHit");
        Enemy instance = new EnemyImpl();
        boolean expResult = false;
        boolean result = instance.isHit();
        assertEquals(expResult, result);
    }

    /**
     * Test of hit method, of class Enemy.
     */
    @Test
    public void testHit() {
        System.out.println("hit");
        Enemy instance = new EnemyImpl();
        instance.hit();
        boolean expResult = true;
        boolean result = instance.isHit();
        assertEquals(expResult, result);
    }

    /**
     * Test of unhit method, of class Enemy.
     */
    @Test
    public void testUnhit() {
        System.out.println("unhit");
        Enemy instance = new EnemyImpl();
        instance.unhit();
        boolean expResult = false;
        
        // Test when already false
        boolean result = instance.isHit();
        assertEquals(expResult, result);
        
        // Test resetting after it's been hit already
        instance.hit();
        instance.unhit();
        result = instance.isHit();
        assertEquals(expResult, result);
    }

    public class EnemyImpl extends Enemy {
    }
}