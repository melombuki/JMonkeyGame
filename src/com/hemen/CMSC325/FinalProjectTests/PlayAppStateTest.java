package com.hemen.CMSC325.FinalProjectTests;

import com.hemen.CMSC325.FinalProject.PlayAppState;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author melombuki
 */
public class PlayAppStateTest extends TestMethods {
    PlayAppState pAS;
    
    public PlayAppStateTest() {
        pAS = new PlayAppState();
    }
    
    public static void main(String[] args) {
        PlayAppStateTest pAST = new PlayAppStateTest();
        
        try {
            pAST.testSetTotalRounds();
            pAST.testGetTotalPoints();
            pAST.testAddPoints();
            pAST.testGetTotalRounds();
            pAST.testSetTotalRounds();
        } catch (Exception ex) {
            Logger.getLogger(PlayAppStateTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
            System.exit(0);
        }
        System.out.println("PlayAppState tests all passed.");
    }

    /**
     * Test of setTotalRounds method, of class PlayAppState.
     */
    public void testSetTotalRounds() throws Exception {
        System.out.println("setTotalRounds");
        pAS.setTotalRounds(13);
        int expResult = 13;
        int result = pAS.getTotalRounds();
        if(!assertEquals(expResult, result))
            throw new Exception("setTotalRounds Failed: did not set the rounds");
    }

    /**
     * Test of getTotalPoints method, of class PlayAppState.
     */
    public void testGetTotalPoints() throws Exception {
        System.out.println("getTotalPoints");
        
        // Test initial 0 points
        int expResult = 0;
        int result = pAS.getTotalPoints();
        if(!assertEquals(expResult, result))
            throw new Exception("getTotalPoints Failed: incorrect points returned");
        
        // Test after adding some points
        expResult = 99;
        pAS.addPoints(99);
        result = pAS.getTotalPoints();
        if(!assertEquals(expResult, result))
            throw new Exception("getTotalPoints Failed: incorrect points returned after adding some points");
    }

    /**
     * Test of addPoints method, of class PlayAppState.
     */
    public void testAddPoints() throws Exception {
        System.out.println("addPoints");
        int expResult = 100; //there are already 99 points added to tthe total score
        pAS.addPoints(1);    //99 + 1 = 100
        int result = pAS.getTotalPoints();
        if(!assertEquals(expResult, result))
            throw new Exception("getTotalPoints Failed: did not add points points correctly");
    }
    
    /**
     * Test of getCurrentRound method, of class PlayAppState.
     */
    public void testGetCurrentRound() throws Exception {
        System.out.println("getCurrentRound");
        int expResult = 1;
        int result = pAS.getCurrentRound();
        if(!assertEquals(expResult, result))
            throw new Exception("getCurrentRound Failed: returns incorrect round");
    }

    /**
     * Test of getTotalRounds method, of class PlayAppState.
     */
    public void testGetTotalRounds() throws Exception {
        System.out.println("getTotalRounds");
         pAS.setTotalRounds(1);
        int expResult = 1;
        int result = pAS.getTotalRounds();
        if(!assertEquals(expResult, result))
            throw new Exception("getTotalRounds Failed: incorrect rounds returned");
    }
}