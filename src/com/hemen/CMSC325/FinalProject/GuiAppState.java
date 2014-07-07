package com.hemen.CMSC325.FinalProject;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
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
    private SimpleApplication app;
    private Node              rootNode;
    private AssetManager      assetManager;
    private AppStateManager   stateManager;
    private InputManager      inputManager;
    private ViewPort          guiViewPort;
    private AudioRenderer     audioRenderer;
    private FlyByCamera       flyCam;
    private Nifty             nifty;
    private NiftyJmeDisplay   disp;
    private Screen            screen;
    private Timer             timer;
    private float             hitMark;
    private DropDown          totalRounds;
    private DropDown          totalPlayers;
    
    public GuiAppState() {}
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app; // can cast Application to something more specific
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
        // Update a timer within the GUI with time from start of screen
        if(nifty.getCurrentScreen().getScreenId().equals("hud")) {
            nifty.getCurrentScreen().findElementByName(
                    "timer").getRenderer(TextRenderer.class).setText("Time: " +
                        getTime(timer.getTimeInSeconds()));
            if(timer.getTimeInSeconds() - hitMark > 2) {
                nifty.getCurrentScreen().findElementByName("hitObject").getRenderer(
                    TextRenderer.class).setText("");
            }
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //clear all mappings and listeners, detach all nodes etc.
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
     * This method starts the game app state
     */
    public void startGame() {
        int rounds = totalRounds.getSelectedIndex() + 1;
        int players = totalPlayers.getSelectedIndex() + 1;
        // Set the number of rounds and players for the game
        stateManager.getState(PlayAppState.class).setTotalRounds(rounds * players);
        stateManager.getState(PlayAppState.class).setTotalPlayers(players);
        
        // Enable the hud and gameplay state
        goToHud();
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
        if(nifty.getCurrentScreen().getScreenId().equals("hud")) {
            // Set the current player in top corner
            nifty.getCurrentScreen().findElementByName(
                    "playerID").getRenderer(TextRenderer.class).setText("Player " +
                        stateManager.getState(PlayAppState.class).getCurrentPlayer());
            
            // Clear the object hit line if not already cleared.
            nifty.getCurrentScreen().findElementByName(
                    "hitObject").getRenderer(TextRenderer.class).setText("");
            timer.reset();
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
    
    private DropDown findDropDownControl(final String id) {
 	return screen.findNiftyControl(id, DropDown.class);
    }
    
    public void goToPause() {
        nifty.gotoScreen("pause");
    }
    
    /*
     * This method starts the game app state and keeps a hud for the player
     * to see various stats.
     */
    public void restartGame() {
        goToHud();
        // Enable the gameplay state
        stateManager.getState(PlayAppState.class).setEnabled(true);
    }
    
    public void goToStart() {
        nifty.gotoScreen("start");
    }
    
    public void goToHud() {
        nifty.gotoScreen("hud");
        flyCam.setDragToRotate(false);
    }
    
    public void initDropDown() {
        // Initialize the drop down elements that is persistent
        totalRounds = findDropDownControl("totalRounds");
        if (totalRounds != null) {
            totalRounds.addItem("1");
            totalRounds.addItem("2");
            totalRounds.addItem("3");

            totalRounds.selectItemByIndex(0);
        }

        totalPlayers = findDropDownControl("totalPlayers");
        if (totalPlayers != null) {
            totalPlayers.addItem("1");
            totalPlayers.addItem("2");

            totalPlayers.selectItemByIndex(0);
        }
    }
    
    /*
     * This method changes the hud text to show the player what they hit, the
     * amount of points they recieved for it, and updates the total points
     * displayed on screen.
     */
    public void showHitObject(String name) {
        nifty.getCurrentScreen().findElementByName("hitObject").getRenderer(
                TextRenderer.class).setText("You hit a " + name);
        
        hitMark = timer.getTimeInSeconds(); //reset the show time marker
        
        //TODO: update the total points and points recieved for each different 
        // type of object hit.
    }
}