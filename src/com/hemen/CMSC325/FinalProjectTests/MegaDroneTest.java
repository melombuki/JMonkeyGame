package com.hemen.CMSC325.FinalProjectTests;

import com.hemen.CMSC325.FinalProject.MegaDrone;
import com.hemen.CMSC325.FinalProject.MicroDrone;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author melombuki
 */
public class MegaDroneTest extends TestMethods {
    MegaDrone mD;
    
    public MegaDroneTest() {
        mD = new MegaDrone("megaDrone", null, null, new Geometry());
    }
    
    public static void main(String[] args) {
        MegaDroneTest mDT = new MegaDroneTest();
        
        try {
           mDT.testCreateMicroDrone();
           mDT.testGetMinions();
           mDT.testClearMinions();
           mDT.testRemoveMinion();
           mDT.testGethealth();
           mDT.testHit();
           mDT.testUnhit();
        } catch (Exception ex) {
            Logger.getLogger(EnemyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
            System.exit(0);
        }
        System.out.println("MegaDrone tests all pass.");
    }

    /**
     * Test of createMicroDrone method, of class MegaDrone.
     */
    public void testCreateMicroDrone() throws Exception {
        System.out.println("createMicroDrone");
        MicroDrone resultMD;
        
        // Test returning of the minion
        resultMD = mD.createMicroDrone(null);
        if(assertNull(resultMD))
            throw new Exception("createMicroDrone Failed: returned null");
            
        // Test that the minion is added to the minion list
        int expResult = 1;
        int result = mD.getMinions().size();
        if(!assertEquals(expResult, result))
            throw new Exception("createMicroDrone Failed: list size is incorrect after adding 1 minion"); 
    }

    /**
     * Test of gethealth method, of class MegaDrone.
     */
    public void testGethealth() throws Exception {
        System.out.println("gethealth");
        int expResult = 100;
        int result = mD.gethealth();
        if(!assertEquals(expResult, result))
            throw new Exception("gethealth Failed: health did not match");
    }

    /**
     * Test of hit method, of class MegaDrone.
     */
    public void testHit() throws Exception {
        System.out.println("hit");
        mD.hit();
        
        // Test health reset
        int expResult = 90;
        int result = mD.gethealth();
        if(!assertEquals(expResult, result))
            throw new Exception("hit Failed: health did not decrease by 10");      
    }

    /**
     * Test of unhit method, of class MegaDrone.
     */
    public void testUnhit() throws Exception {
        System.out.println("unhit");
        mD.hit();
        mD.hit();
        mD.unhit();
        
        // Test unhit
        int expResult = 100;
        int result = mD.gethealth();
        if(!assertEquals(expResult, result))
            throw new Exception("unhit Failed: health is not back to 100 (max)");
    }

    /**
     * Test of getMinions method, of class MegaDrone.
     */
    public void testGetMinions() throws Exception {
        System.out.println("getMinions");
        
        // There should already be a minion in the list
        Set resultS = mD.getMinions();
        if(assertNull(resultS))
            throw new Exception("getMinions Failed: did not return null for empty minion list");
    }

    /**
     * Test of removeMinion method, of class MegaDrone.
     */
    public void testRemoveMinion() throws Exception {
        System.out.println("removeMinion");
        MegaDrone megaDrone = new MegaDrone("m", null, null, new Geometry());
        MicroDrone m = megaDrone.createMicroDrone(null);
        boolean expResult = true;
        boolean result = megaDrone.removeMinion(m.getGeo());
        if(!assertEquals(expResult, result))
            throw new Exception("removeMinion Failed: did not remove the minion");
    }

    /**
     * Test of clearMinions method, of class MegaDrone.
     */
    public void testClearMinions() throws Exception {
        System.out.println("clearMinions");
        mD.clearMinions();
        if(assertNull(mD.getMinions()))
            throw new Exception("clearMinions Failed: did not clear the minion set");
    }
}