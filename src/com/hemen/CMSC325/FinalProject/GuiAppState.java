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
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
    private String[]           scores;
    private String             initials = "unk";
    
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

    // Init the empty highscores String array
    scores = new String[] {"", "", "", "", ""};
    
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
        // Get the user's initials that they entered
        initials = findTextFieldControl("userInitials").getRealText();
        
        // Verify valid user input
        if(!isValid(initials)) return; //do nothing if not valid input

        System.out.println(initials);
        
        // Start the timer since start of game
        startMark = timer.getTimeInSeconds();
        
        // Set the number of rounds and players for the game
        int rounds = totalRounds.getSelectedIndex() + 1;
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
            
            // Fill the high scores with those already saved and update as
            //  necessary.
            updateHighScores();
            
            // Show the high scores
            nifty.getCurrentScreen().findElementByName("1st").getRenderer(
                    TextRenderer.class).setText("1: " + scores[0]);
            nifty.getCurrentScreen().findElementByName("2nd").getRenderer(
                    TextRenderer.class).setText("2: " + scores[1]);
            nifty.getCurrentScreen().findElementByName("3rd").getRenderer(
                    TextRenderer.class).setText("3: " + scores[2]);
            nifty.getCurrentScreen().findElementByName("4th").getRenderer(
                    TextRenderer.class).setText("4: " + scores[3]);
            nifty.getCurrentScreen().findElementByName("5th").getRenderer(
                    TextRenderer.class).setText("5: " + scores[4]);
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
     * This method returns the drop down control to manipulate.
     */
    private DropDown findDropDownControl(final String id) {
 	return screen.findNiftyControl(id, DropDown.class);
    }
    
    /*
     * This method returns the text field control to manipulate
     */
    private TextField findTextFieldControl(final String id) {
        return screen.findNiftyControl(id, TextField.class);
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
    
    /*
     * This method writes the the highscores to the highscore file. Each line
     * contains one set. A set consists of the player's initials and score
     * delimited by ":".
     */
    private void writeFile() {
        Charset charset = Charset.forName("US-ASCII");
        Path outfile = Paths.get("highscores");
        
        // Writer the output file in proper format
        try {
            BufferedWriter writer = Files.newBufferedWriter(outfile, charset);
            
            for(int i = 0; i < scores.length; i++) {
                writer.write(scores[i] + "\n");
            }
            writer.flush(); //make sure everything is written to the file
            writer.close(); //close the bufferedwriter
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }
    
    /*
     * This method reads in high score values from the file "highscores". Returns
     * the scores as a String array containing the player's initials and score
     * delimited by ":". Returns empty strings if the file does not exits.
     */
    private void readFile() {
        Path infile = Paths.get("highscores");
        
        // Do nothing if the file does not already exist
        if(!Files.exists(infile)) {
            return;
        }
        
        // Read the input file
        try {
            BufferedReader reader = new BufferedReader(new FileReader("highscores"));
            
            String line; //single lines from the input file
            for(int i = 0; i < 5; i++) {
                line = reader.readLine();
                if(line == null) {
                     scores[i] = "";
                } else {
                    scores[i] = line;
                }
            }
            reader.close(); //close the bufferedwriter
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }
    
    /*
     * This method sets the scores String array with the appropriate new
     * highscores and writes them out to the highscore file.
     */
    private void updateHighScores() {
        List<HighScoreEntry> list = new ArrayList<HighScoreEntry>();
        
        // Fill scores with any previous entries
        readFile();
        
        // Map all previous scores to initials
        String[] line;
        //Testing
        System.out.println(scores.length);
        for(String s: scores)
            System.out.println(s);
        //Testing
        for(int i = 0; i < scores.length; i++) {
            if(scores[i].length() > 0) {
                line = scores[i].split(":");
                list.add(new HighScoreEntry(Integer.parseInt(line[0]), line[1]));
            }
        }
        
        // Map the players score
        list.add(new HighScoreEntry(totalPointsEnd, "JPH"));

        // Place the top 5 or fewer back into the scores String array
        Collections.sort(list);
        HighScoreEntry entry;
        int i = 0;
        for(Iterator it = list.iterator(); it.hasNext() && i < 5; ) {
            entry = (HighScoreEntry)it.next();
            scores[i++] = entry.getScore() + ":" + entry.getInitials();
        }
        
        // Write the new scores to the high score file
        writeFile();
    }

    /*
     * This method returns true if the user has properly entered their initials,
     * and false if not.
     */
    private boolean isValid(String initials) {
        return initials.matches("[a-zA-Z]{3}");
    }
}