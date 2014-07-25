/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
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
public class PlayAppStateTest {
    
    public PlayAppStateTest() {
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
     * Test of initialize method, of class PlayAppState.
     */
    @Test
    public void testInitialize() {
        System.out.println("initialize");
        AppStateManager stateManager = null;
        Application app = null;
        PlayAppState instance = new PlayAppState();
        instance.initialize(stateManager, app);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class PlayAppState.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        float tpf = 0.0F;
        PlayAppState instance = new PlayAppState();
        instance.update(tpf);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cleanup method, of class PlayAppState.
     */
    @Test
    public void testCleanup() {
        System.out.println("cleanup");
        PlayAppState instance = new PlayAppState();
        instance.cleanup();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setEnabled method, of class PlayAppState.
     */
    @Test
    public void testSetEnabled() {
        System.out.println("setEnabled");
        boolean enabled = false;
        PlayAppState instance = new PlayAppState();
        instance.setEnabled(enabled);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of collision method, of class PlayAppState.
     */
    @Test
    public void testCollision() {
        System.out.println("collision");
        PhysicsCollisionEvent e = null;
        PlayAppState instance = new PlayAppState();
        instance.collision(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onAction method, of class PlayAppState.
     */
    @Test
    public void testOnAction() {
        System.out.println("onAction");
        String binding = "";
        boolean value = false;
        float tpf = 0.0F;
        PlayAppState instance = new PlayAppState();
        instance.onAction(binding, value, tpf);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resetLevel method, of class PlayAppState.
     */
    @Test
    public void testResetLevel() {
        System.out.println("resetLevel");
        PlayAppState instance = new PlayAppState();
        instance.resetLevel();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resetHillEnemy method, of class PlayAppState.
     */
    @Test
    public void testResetHillEnemy() {
        System.out.println("resetHillEnemy");
        PlayAppState instance = new PlayAppState();
        instance.resetHillEnemy();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resetSlideEnemy method, of class PlayAppState.
     */
    @Test
    public void testResetSlideEnemy() {
        System.out.println("resetSlideEnemy");
        PlayAppState instance = new PlayAppState();
        instance.resetSlideEnemy();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resetMegaDrone method, of class PlayAppState.
     */
    @Test
    public void testResetMegaDrone() {
        System.out.println("resetMegaDrone");
        PlayAppState instance = new PlayAppState();
        instance.resetMegaDrone();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resetPlayer method, of class PlayAppState.
     */
    @Test
    public void testResetPlayer() {
        System.out.println("resetPlayer");
        PlayAppState instance = new PlayAppState();
        instance.resetPlayer();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTotalRounds method, of class PlayAppState.
     */
    @Test
    public void testSetTotalRounds() {
        System.out.println("setTotalRounds");
        int r = 0;
        PlayAppState instance = new PlayAppState();
        instance.setTotalRounds(r);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotalPoints method, of class PlayAppState.
     */
    @Test
    public void testGetTotalPoints() {
        System.out.println("getTotalPoints");
        PlayAppState instance = new PlayAppState();
        int expResult = 0;
        int result = instance.getTotalPoints();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPoints method, of class PlayAppState.
     */
    @Test
    public void testAddPoints() {
        System.out.println("addPoints");
        int points = 0;
        PlayAppState instance = new PlayAppState();
        instance.addPoints(points);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of initCrossHairText method, of class PlayAppState.
     */
    @Test
    public void testInitCrossHairText() {
        System.out.println("initCrossHairText");
        PlayAppState instance = new PlayAppState();
        instance.initCrossHairText();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of initInvisibleWalls method, of class PlayAppState.
     */
    @Test
    public void testInitInvisibleWalls() {
        System.out.println("initInvisibleWalls");
        PlayAppState instance = new PlayAppState();
        instance.initInvisibleWalls();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrentRound method, of class PlayAppState.
     */
    @Test
    public void testGetCurrentRound() {
        System.out.println("getCurrentRound");
        PlayAppState instance = new PlayAppState();
        int expResult = 0;
        int result = instance.getCurrentRound();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotalRounds method, of class PlayAppState.
     */
    @Test
    public void testGetTotalRounds() {
        System.out.println("getTotalRounds");
        PlayAppState instance = new PlayAppState();
        int expResult = 0;
        int result = instance.getTotalRounds();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of gameOver method, of class PlayAppState.
     */
    @Test
    public void testGameOver() {
        System.out.println("gameOver");
        PlayAppState instance = new PlayAppState();
        instance.gameOver();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of roundOver method, of class PlayAppState.
     */
    @Test
    public void testRoundOver() {
        System.out.println("roundOver");
        PlayAppState instance = new PlayAppState();
        instance.roundOver();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}