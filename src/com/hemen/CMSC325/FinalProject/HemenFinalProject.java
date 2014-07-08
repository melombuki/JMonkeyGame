/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.hemen.CMSC325.FinalProject;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;

/**
 * This project is a simple game in which a user must hit three floating red
 * balls in order to win. There are two players, player 1 is given red balls to
 * hit, player 2 is given blue balls to hit. Once player 1 finishes thier turn,
 * player 2 is instantly spawned at the origin for their turn.
 * 
 * @author Joshua P. Hemen
 */
public class HemenFinalProject extends SimpleApplication {
    private GuiAppState     startAppState;
    private PlayAppState    playAppState;
    private BulletAppState  bulletAppState;
    private boolean         isStart = true;
    
    public static void main(String[] args) {
        HemenFinalProject app = new HemenFinalProject();
        
        // Set some custom settings
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Hemen Final Project");
        app.setSettings(settings);

        // Start the app
        app.start();
    }
  
    public void simpleInitApp() {
        // Clear the debug display info
        setDisplayStatView(false); setDisplayFps(false);
        
        // Set up all app states and attach them
        startAppState = new GuiAppState();     //gui menu
        stateManager.attach(startAppState);
        
        bulletAppState = new BulletAppState(); //physics
        stateManager.attach(bulletAppState);
        
        playAppState = new PlayAppState();     //game play
        stateManager.attach(playAppState);
    }
  
    @Override
    public void simpleUpdate(float tpf) {
        // Start the first cycle here so everything is initialized before enabling
        if(isStart) {
            // Start with the GUI
            startAppState.setEnabled(true);
            
            // Indicate program is now started for the first time
            isStart = false;
        }
    }
}
 
