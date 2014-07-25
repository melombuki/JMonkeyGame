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
public class HighScoreEntryTest {
    
    public HighScoreEntryTest() {
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
     * Test of getScore method, of class HighScoreEntry.
     */
    @Test
    public void testGetScore() {
        System.out.println("getScore");
        HighScoreEntry instance = null;
        Integer expResult = null;
        Integer result = instance.getScore();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInitials method, of class HighScoreEntry.
     */
    @Test
    public void testGetInitials() {
        System.out.println("getInitials");
        HighScoreEntry instance = null;
        String expResult = "";
        String result = instance.getInitials();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compareTo method, of class HighScoreEntry.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        HighScoreEntry o = null;
        HighScoreEntry instance = null;
        int expResult = 0;
        int result = instance.compareTo(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}