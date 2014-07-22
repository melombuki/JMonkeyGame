package com.hemen.CMSC325.FinalProject;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.Timer;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.concurrent.TimeUnit;

/**
 * This class is an AppState that contains a nifty GUI for the start of a game.
 * It is very basic at the moment and could use some touching up by adding 
 * pictures as backgrounds instead of solid colors.
 * 
 * @author Joshua P. Hemen
 */
public class GuiAppState extends AbstractAppState implements ScreenController {
    private SimpleApplication  app;
    private Node               rootNode;
    private AssetManager       assetManager;
    private AppStateManager    stateManager;
    private InputManager       inputManager;
    private ViewPort           guiViewPort;
    private AudioRenderer      audioRenderer;
    private FlyByCamera        flyCam;
    private Nifty              nifty;
    private NiftyJmeDisplay    disp;
    private Screen             screen;
    private Timer              timer;
    private static final float timeLimit = 300f;
    private float              hitMark = 0.0f;
    private float              startMark = 0.0f;
    private DropDown           totalRounds;
    private int                totalPointsEnd = 0;
    
    public GuiAppState() {}
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app; //can cast Application to something more specific
    this.rootNode     = this.app.getRootNode();
    this.assetManager = this.app.getAssetManager();
    this.stateManager = this.app.getStateManager();
    this.inputManager = this.app.getInputManager();
    this.guiViewPort  = this.app.getGuiViewPort();
    this.flyCam       = this.app.getFlyByCamera();
    this.timer        = this.app.getTimer();

    // Create and show the beginning gui display
    disp = new NiftyJmeDisplay(
            assetManager, inputManager, audioRenderer, guiViewPort);

    nifty = disp.getNifty();
    nifty.fromXml("Interface/start.xml", "start", this);
    
    // Initialize the drop down elements that is persistent
    initDropDown();
  }
    
    @Override
    public void update(float tpf) {
        // Update a timer within the hud with time from start of screen and hanle
        // 5 minute time limit
        if(nifty.getCurrentScreen().getScreenId().equals("hud")) {
            // End the round or game at 5 minutes
            if(timer.getTimeInSeconds() - startMark > timeLimit) { //5 minute limit
                if(stateManager.getState(PlayAppState.class).getCurrentRound() == 
                        stateManager.getState(PlayAppState.class).getTotalRounds()) {
                    stateManager.getState(PlayAppState.class).gameOver();
                } else {
                    stateManager.getState(PlayAppState.class).roundOver();
                    startMark = timer.getTimeInSeconds(); //reset the start timer
                }       
            }
            
            // Update the hud with current information
            nifty.getCurrentScreen().findElementByName(
                    "timer").getRenderer(TextRenderer.class).setText("Time: " +
                        getTime(timer.getTimeInSeconds() - startMark));
            if(timer.getTimeInSeconds() - hitMark > 2) {
                nifty.getCurrentScreen().findElementByName("hitObject").getRenderer(
                    TextRenderer.class).setText("");
            }
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        
        // Clear all mappings and listeners, detach all nodes etc.
        setEnabled(false);
        rootNode.detachAllChildren();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        //Pause and unpause
        super.setEnabled(enabled);
        
        if(enabled) {         
            guiViewPort.addProcessor(disp);
            flyCam.setDragToRotate(true);
            
        } else {
            guiViewPort.removeProcessor(disp);
            flyCam.setDragToRotate(false);
            rootNode.detachAllChildren();
        }
    }
    
    /*
     * This method starts the game app state.
     */
    public void startGame() {
        startMark = timer.getTimeInSeconds();
        int rounds = totalRounds.getSelectedIndex() + 1;
        
        // Set the number of rounds and players for the game
        stateManager.getState(PlayAppState.class).setTotalRounds(rounds);
        
        // Start updating the game
        stateManager.getState(PlayAppState.class).isRunning = true;
        
        // Enable the hud and gameplay state
        goToHud();
        stateManager.getState(PlayAppState.class).resetLevel();
        stateManager.getState(PlayAppState.class).setEnabled(true);
    }
    
    /*
     * This method exits the game.
     */
    public void quit() {
        System.exit(0);
    }
    
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onStartScreen() {
        Integer totalPoints;
        
        if(nifty.getCurrentScreen().getScreenId().equals("hud")) {
            // Set the current player in top corner
            nifty.getCurrentScreen().findElementByName(
                    "currentRound").getRenderer(TextRenderer.class).setText("Round " +
                        stateManager.getState(PlayAppState.class).getCurrentRound());
            
            // Clear the object hit line if not already cleared.
            nifty.getCurrentScreen().findElementByName(
                    "hitObject").getRenderer(TextRenderer.class).setText("");
            
            // Get the current total score
            totalPoints = stateManager.getState(PlayAppState.class).getTotalPoints();
            
            // Set the total points
            nifty.getCurrentScreen().findElementByName("totalPoints").getRenderer(
                    TextRenderer.class).setText("Total Score: " + totalPoints);
        }
        
        if(nifty.getCurrentScreen().getScreenId().equals("end")) {
            // Set the end points
            nifty.getCurrentScreen().findElementByName("totalPointsEnd").getRenderer(
                    TextRenderer.class).setText("Total Score: " + totalPointsEnd);
        }
    }

    public void onEndScreen() {
    }
    
    /*
     * This method takes a time duration in seconds and returns a String 
     * in the form of HH:MM:SS.
     */
    private String getTime(float time) {
        float second = TimeUnit.SECONDS.convert(
                (long)time, TimeUnit.SECONDS) % 60;
        float minute = TimeUnit.MINUTES.convert(
                (long)time, TimeUnit.SECONDS) % 60;
        float hour   = TimeUnit.HOURS.convert(
                (long)time, TimeUnit.SECONDS) % 24;
        
        return String.format("%02.0f:%02.0f:%02.0f", hour, minute, second);
    }
    
    /*
     * This method returns the drop down control to maninulation.
     */
    private DropDown findDropDownControl(final String id) {
 	return screen.findNiftyControl(id, DropDown.class);
    }
    
    /*
     * This method goes to the pause screen.
     */
    public void goToPause() {
        nifty.gotoScreen("pause");
    }
    
    /*
     * This method goes to the end screen.
     */
    public void goToEnd() {
        nifty.gotoScreen("end");
    }
    
    /*
     * This method starts the game app state and keeps a hud for the player
     * to see various stats.
     */
    public void continueGame() {
        goToHud();
        // Enable the gameplay state
        stateManager.getState(BulletAppState.class).setEnabled(true);
        stateManager.getState(PlayAppState.class).setEnabled(true);
    }
    
    /*
     * This method goes to the start screen.
     */
    public void goToStart() {
        nifty.gotoScreen("start");
    }
    
    /*
     * This method goes to the HUD screen displayed during gameplay.
     */
    public void goToHud() {
        nifty.gotoScreen("hud");
        flyCam.setDragToRotate(false);
    }
    
    /*
     * This method initializes the drop down menu to select the number of rounds
     * to play before the game is over.
     */
    public void initDropDown() {
        // Initialize the drop down elements that is persistent
        totalRounds = findDropDownControl("totalRounds");
        if (totalRounds != null) {
            totalRounds.addItem("1");
            totalRounds.addItem("2");
            totalRounds.addItem("3");

            totalRounds.selectItemByIndex(0);
        }
    }
    
    /*
     * This method changes the hud text to show the player what they hit, the
     * amount of points they recieved for it, and updates the total points
     * displayed on screen.
     */
    public void showHitObject(String name, int points) {
        Integer totalPoints;
        
        // Reset the show time marker
        hitMark = timer.getTimeInSeconds();
        
        // Show the user what they hit
        nifty.getCurrentScreen().findElementByName("hitObject").getRenderer(
                TextRenderer.class).setText("You hit a " + name + ". + " + points);
        
        // Add the new points to the total
        stateManager.getState(PlayAppState.class).addPoints(points);
        
        // Get the current total score
        totalPoints = stateManager.getState(PlayAppState.class).getTotalPoints();
        
        // Show it
        nifty.getCurrentScreen().findElementByName("totalPoints").getRenderer(
                TextRenderer.class).setText("Total Score: " + totalPoints.toString());
    }
    
    /*
     * This method basically just keeps the hud at the end of the game, suspends
     * all gameplay, and prompts the user to click a button when they are ready
     * to continue. It allows the user to review their stats.
     */
    public void ackEnd() {
        //stateManager.getState(PlayAppState.class).setEnabled(false);
        goToStart();
    }
    
    /*
     * This method sets the total end points from a game just completed.
     */
    public void setTotalPointsEnd(int points) {
        this.totalPointsEnd = points;
    }
}