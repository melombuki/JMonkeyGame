/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class contains the guiAppState tests.
 * 
 * @author Joshua P. Hemen
 */
public class GuiAppStateTest {
    
    public GuiAppStateTest() {
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
     * Test of initialize method, of class GuiAppState.
     */
    @Test
    public void testInitialize() {
        System.out.println("initialize");
        AppStateManager stateManager = null;
        Application app = null;
        GuiAppState instance = new GuiAppState();
        instance.initialize(stateManager, app);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class GuiAppState.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        float tpf = 0.0F;
        GuiAppState instance = new GuiAppState();
        instance.update(tpf);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cleanup method, of class GuiAppState.
     */
    @Test
    public void testCleanup() {
        System.out.println("cleanup");
        GuiAppState instance = new GuiAppState();
        instance.cleanup();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEnabled method, of class GuiAppState.
     */
    @Test
    public void testSetEnabled() {
        System.out.println("setEnabled");
        boolean enabled = false;
        GuiAppState instance = new GuiAppState();
        instance.setEnabled(enabled);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of startGame method, of class GuiAppState.
     */
    @Test
    public void testStartGame() {
        System.out.println("startGame");
        GuiAppState instance = new GuiAppState();
        instance.startGame();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of quit method, of class GuiAppState.
     */
    @Test
    public void testQuit() {
        System.out.println("quit");
        GuiAppState instance = new GuiAppState();
        instance.quit();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of bind method, of class GuiAppState.
     */
    @Test
    public void testBind() {
        System.out.println("bind");
        Nifty nifty = null;
        Screen screen = null;
        GuiAppState instance = new GuiAppState();
        instance.bind(nifty, screen);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onStartScreen method, of class GuiAppState.
     */
    @Test
    public void testOnStartScreen() {
        System.out.println("onStartScreen");
        GuiAppState instance = new GuiAppState();
        instance.onStartScreen();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onEndScreen method, of class GuiAppState.
     */
    @Test
    public void testOnEndScreen() {
        System.out.println("onEndScreen");
        GuiAppState instance = new GuiAppState();
        instance.onEndScreen();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of goToPause method, of class GuiAppState.
     */
    @Test
    public void testGoToPause() {
        System.out.println("goToPause");
        GuiAppState instance = new GuiAppState();
        instance.goToPause();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of goToEnd method, of class GuiAppState.
     */
    @Test
    public void testGoToEnd() {
        System.out.println("goToEnd");
        GuiAppState instance = new GuiAppState();
        instance.goToEnd();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of continueGame method, of class GuiAppState.
     */
    @Test
    public void testContinueGame() {
        System.out.println("continueGame");
        GuiAppState instance = new GuiAppState();
        instance.continueGame();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of goToStart method, of class GuiAppState.
     */
    @Test
    public void testGoToStart() {
        System.out.println("goToStart");
        GuiAppState instance = new GuiAppState();
        instance.goToStart();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of goToHud method, of class GuiAppState.
     */
    @Test
    public void testGoToHud() {
        System.out.println("goToHud");
        GuiAppState instance = new GuiAppState();
        instance.goToHud();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of initDropDown method, of class GuiAppState.
     */
    @Test
    public void testInitDropDown() {
        System.out.println("initDropDown");
        GuiAppState instance = new GuiAppState();
        instance.initDropDown();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of initTextField method, of class GuiAppState.
     */
    @Test
    public void testInitTextField() {
        System.out.println("initTextField");
        GuiAppState instance = new GuiAppState();
        instance.initTextField();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of showHitObject method, of class GuiAppState.
     */
    @Test
    public void testShowHitObject() {
        System.out.println("showHitObject");
        String name = "";
        int points = 0;
        GuiAppState instance = new GuiAppState();
        instance.showHitObject(name, points);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of ackEnd method, of class GuiAppState.
     */
    @Test
    public void testAckEnd() {
        System.out.println("ackEnd");
        GuiAppState instance = new GuiAppState();
        instance.ackEnd();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTotalPointsEnd method, of class GuiAppState.
     */
    @Test
    public void testSetTotalPointsEnd() {
        System.out.println("setTotalPointsEnd");
        int points = 0;
        GuiAppState instance = new GuiAppState();
        instance.setTotalPointsEnd(points);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}