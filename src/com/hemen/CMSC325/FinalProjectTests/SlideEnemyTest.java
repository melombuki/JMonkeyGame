package com.hemen.CMSC325.FinalProjectTests;

import com.hemen.CMSC325.FinalProject.SlideEnemy;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author melombuki
 */
public class SlideEnemyTest extends TestMethods {
    SlideEnemy sE = new SlideEnemy("sE", null, new Node());
    
    public SlideEnemyTest() {
    }

    public static void main(String[] args) {
        SlideEnemyTest sET = new SlideEnemyTest();
        try {
            sET.testShoot();
        } catch (Exception ex) {
            Logger.getLogger(SlideEnemyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getMessage());
            System.exit(0);
        }
        System.out.println("SlideEnemy tests all pass.");
    }
    
    /**
     * Test of shoot method, of class SlideEnemy.
     */
    public void testShoot() throws Exception {
        System.out.println("shoot");
        
        // Test trying to shoot in first 2 seconds after being instantiated
        Vector3f result = sE.shoot(null);
        if(!assertNull(result))
            throw new Exception("shoot Failed: fired before 2 seconds have passed");
        
        // "Pause" the program until at leat 2 seconds have passed before
        // trying to shoot again
        System.out.println("Waiting 2.1 seconds, please wait.....");
        synchronized(this) {
            wait(2100);
        }
        
        // Test firing after the 2 seconds have gone by
        result = sE.shoot(null);
        if(assertNull(result))
            throw new Exception("shoot Failed: successfully fired after 2 seconds");   
    }
}