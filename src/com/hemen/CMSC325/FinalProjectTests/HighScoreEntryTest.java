/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProjectTests;

import com.hemen.CMSC325.FinalProject.HighScoreEntry;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author melombuki
 */
public class HighScoreEntryTest extends TestMethods{
    public HighScoreEntry hSE = new HighScoreEntry(1000, "john");
    
    public HighScoreEntryTest() {

    }
    
    public static void main(String[] args) {
        HighScoreEntryTest hSET = new HighScoreEntryTest();
        
        try {
            hSET.testCompareTo();
            hSET.testGetInitials();
            hSET.testGetScore();
        } catch (Exception ex) {
            Logger.getLogger(EnemyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
            System.exit(0);
        }
        System.out.println("HighScoreEntry tests all pass.");
    }

    /**
     * Test of getScore method, of class HighScoreEntry.
     */
    public void testGetScore() throws Exception {
        System.out.println("getScore");
        
        Integer expResult = 1000;
        Integer result = hSE.getScore();
        if(!assertEquals(expResult, result))
            throw new Exception("getScore Failed");

    }

    /**
     * Test of getInitials method, of class HighScoreEntry.
     */
    public void testGetInitials() throws Exception {
        System.out.println("getInitials");
        String expResult = "john";
        String result = hSE.getInitials();
        if(!assertEquals(expResult, result))
            throw new Exception("getInitials Failed");
        
    }

    /**
     * Test of compareTo method, of class HighScoreEntry.
     */
    public void testCompareTo() throws Exception {
        System.out.println("compareTo");
        
        // Test same score entry
        int expResult = 0;
        HighScoreEntry same = new HighScoreEntry(1000, "john");
        int result = same.compareTo(hSE);
        if(!assertEquals(expResult, result))
            throw new Exception("compareTo Failed: same score");
        
        // Test lower score entry
        expResult = 1;
        HighScoreEntry less = new HighScoreEntry(1, "john");
        result = less.compareTo(hSE);
        if(!assertEquals(expResult, result))
            throw new Exception("compareTo Failed: higher score");
        
        // Test higher score entry
        expResult = -1;
        HighScoreEntry more = new HighScoreEntry(99999, "john");
        result = more.compareTo(hSE);
        if(!assertEquals(expResult, result))
            throw new Exception("compareTo Failed: lower score");
        
    }
}