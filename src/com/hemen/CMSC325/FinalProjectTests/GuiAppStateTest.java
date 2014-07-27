/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProjectTests;

import com.hemen.CMSC325.FinalProject.GuiAppState;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains the guiAppState tests.
 * 
 * @author Joshua P. Hemen
 */
public class GuiAppStateTest extends TestMethods {
    GuiAppState instance = new GuiAppState();
    
    public GuiAppStateTest() {     
    }
    
    public static void main(String[] args) {
        GuiAppStateTest gT = new GuiAppStateTest();
        try {
            gT.testIsValid();
            gT.testAddRound();
        } catch (Exception ex) {
            Logger.getLogger(GuiAppStateTest.class.getName()).log(Level.SEVERE, "GuiAppState tests failed.", ex);
            System.err.println(ex.getMessage());
            System.exit(0);
        }
        System.out.println("GuiAppState tests all pass.");
    }
    /**
     * Test of isValid method, of class GuiAppState.
     */
    public void testIsValid() throws Exception {
        System.out.println("isValid");
        //instance = new GuiAppState();
        boolean expResult = false;
        
        // Test no initials initials
        boolean result = instance.isValid("");
        if(!assertEquals(expResult, result))
            throw new Exception("isValid Failed");
        
        // Test less than three initials
        result = instance.isValid("ab");
        if(!assertEquals(expResult, result))
            throw new Exception("isValid Failed");
        
        // Test more than three initials
        result = instance.isValid("abcd");
        if(!assertEquals(expResult, result))
            throw new Exception("isValid Failed");
        
        // Test numbers in initials
        result = instance.isValid("1bc");        
        if(!assertEquals(expResult, result))
            throw new Exception("isValid Failed");
        
        result = instance.isValid("123");        
        if(!assertEquals(expResult, result))
            throw new Exception("isValid Failed");
        
        // Test a special character
        result = instance.isValid("!bc");
        if(!assertEquals(expResult, result))
            throw new Exception("isValid Failed");
        
        result = instance.isValid(" ab"); //space
        if(!assertEquals(expResult, result))
            throw new Exception("isValid Failed");
        
        // Test correct initials
        expResult = true;
        result = instance.isValid("jph");
        if(!assertEquals(expResult, result))
            throw new Exception("isValid Failed");
    }

    /**
     * Test of setTotalPointsEnd method, of class GuiAppState.
     */
    public void testAddRound() throws Exception {
        System.out.println("addRound");
        instance = new GuiAppState();
        
        // Test before call to method
        int expResult = 0;
        int result = instance.getRounds();
        if(!assertEquals(expResult, result))
            throw new Exception("addRound Failed");
                
        // Test after call to method
        expResult++;
        instance.addRound();
        result = instance.getRounds();
        if(!assertEquals(expResult, result))
            throw new Exception("addRound Failed");
    }
}