package com.hemen.CMSC325.FinalProjectTests;

import com.hemen.CMSC325.FinalProject.Enemy;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class launches the final project in all its 8 week old glory!
 * 
 * @author Joshua P. Hemen
 */
public class EnemyTest extends TestMethods {
    
    public EnemyTest() {
    }
    
    public static void main(String[] args) {
        EnemyTest eT = new EnemyTest();
        
        try {
            eT.testHit();
            eT.testIsHit();
            eT.testUnhit();
        } catch (Exception ex) {
            Logger.getLogger(EnemyTest.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
            System.exit(0);
        }
        System.out.println("Enemy tests all pass.");
    }

    /**
     * Test of isHit method, of class Enemy.
     */
    public void testIsHit() throws Exception {
        System.out.println("isHit");
        Enemy instance = new EnemyImpl();
        boolean expResult = false;
        boolean result = instance.isHit();
        if(!assertEquals(expResult, result))
            throw new Exception("isHit Failed");
    }

    /**
     * Test of hit method, of class Enemy.
     */
    public void testHit() throws Exception {
        System.out.println("hit");
        Enemy instance = new EnemyImpl();
        instance.hit();
        boolean expResult = true;
        boolean result = instance.isHit();
        if(!assertEquals(expResult, result))
            throw new Exception("hit Failed");
    }

    /**
     * Test of unhit method, of class Enemy.
     */
    public void testUnhit() throws Exception {
        System.out.println("unhit");
        Enemy instance = new EnemyImpl();
        instance.unhit();
        boolean expResult = false;
        
        // Test when already false
        boolean result = instance.isHit();
        if(!assertEquals(expResult, result))
            throw new Exception("unHit Failed");
        
        // Test resetting after it's been hit already
        instance.hit();
        instance.unhit();
        result = instance.isHit();
        if(!assertEquals(expResult, result))
            throw new Exception("unHit Failed");
    }

    public class EnemyImpl extends Enemy {
    }
}