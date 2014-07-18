//TODO: get rid of the walls for the new largeScene. No longer need them as
// the mountains fence the char in anyway. 
// 1. Add invisible plane triggers to case enemies to spawn at some location.
// 2. Create more types of enemies and allow them to shoot at the player.
// 3. Give yourself a health meter.
// 4. Create a new player type with the same motion controls as the current balls
//     but make it move slower, increase the linear dampening, and on applyImpulse
//     setY to 1 every time. This will keep it in the air. It will release the current balls when they get close.

package com.hemen.CMSC325.FinalProject;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Listener;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.util.List;

/**
 * This class contains all of the necessary objects and variables for the actual
 * gameplay. It loads three balls and alternates between player 1 and 2 for 2
 * consecutive turns each. A cycle is considered one complete turn for all players
 * selected. A round is one player's turn.
 * @author Joshua P. Hemen
 */
public class PlayAppState extends AbstractAppState implements 
        PhysicsCollisionListener, ActionListener {
    // Create normal app variables for ease of use
    private SimpleApplication app;
    private Node              rootNode;
    private Node              guiNode;
    private AssetManager      assetManager;
    private AppStateManager   stateManager;
    private InputManager      inputManager;
    private ViewPort          viewPort;
    private FlyByCamera       flyCam;
    private ChaseCamera       chaseCam;
    private Camera            cam;
    private Listener          listener;
    
    // Appstate specific fields
    private static final int MAX_OBS = 3; //number of balls player must hit
    private static final int MAX_SPAWN = 1; //# of respawns for enemies
    private Spatial sceneModel;
    private BulletAppState bulletAppState;
    private Node playerNode; //wraps player CharControl with a name
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    private MicroDrone[] microDrones;
    private MegaDrone megaDrone;
    private Material ball_hit, ball_A, ball_B, mat_bullet, mat_invis;
    private int totalRounds = 0; //1 round for each player * desired # of cycles
    private int totalPoints = 0;
    private int currentRound = 1;
    private int spawnCount = 0; //the number current of spawns
    private AmbientLight al;
    private DirectionalLight dl;
    private Quaternion hoverJetQ;
    private Quaternion yaw90 = new Quaternion().fromAngleAxis(
                FastMath.HALF_PI, Vector3f.UNIT_Y);
    private boolean isStart = true; //fixes bug where ball is sometimes hit at start of turn
    private List l;
    
    // Temporary vectors used on each frame.
    // They here to avoid instanciating new vectors on each frame
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    
    // Special Effects
    private Shockwave shockwave;
    
    // Bullet fields
    private Sphere bullet;
    private SphereCollisionShape bulletCollisionShape;
    private AudioNode shotSound, boomSound;
    
    // Gui Stuff
    private BitmapFont guiFont;
    private BitmapText ch;
     
    public PlayAppState() {
        totalRounds = 2;  // one round per player 
    }
    
    public PlayAppState(int cycles, int totalPlayers) {
        this.totalRounds = cycles * totalPlayers;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        // Fields copies for convinience 
        super.initialize(stateManager, app);
        this.app            = (SimpleApplication) app;
        this.rootNode       = this.app.getRootNode();
        this.guiNode        = this.app.getGuiNode();
        this.assetManager   = this.app.getAssetManager();
        this.stateManager   = this.app.getStateManager();
        this.inputManager   = this.app.getInputManager();
        this.viewPort       = this.app.getViewPort();
        this.flyCam         = this.app.getFlyByCamera();
        this.cam            = this.app.getCamera();
        this.listener       = this.app.getListener();
        
        // Get the physics app state
        bulletAppState = stateManager.getState(BulletAppState.class);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);

        // Set up the bullet object
        bullet = new Sphere(32, 32, 0.4f, true, false);
        bullet.setTextureMode(Sphere.TextureMode.Projected);
        bulletCollisionShape = new SphereCollisionShape(0.1f);
        
        // Set up the lights for the scene
        setUpLight();
    
        // Set up special effects
        shockwave = new Shockwave(rootNode, assetManager);

        // Init the spawn trigger walls
        initInvisibleWalls();

        // We load the scene
        sceneModel = assetManager.loadModel("Scenes/largeScene.j3o");
        sceneModel.setLocalTranslation(0, -12, 0);

        // Init materials
        initMaterials();

        // Init text for crosshair text
        initCrossHairText();  

        // Init all arrays
        Vector3f[] objLocations = new Vector3f[MAX_OBS];
        microDrones = new MicroDrone[MAX_OBS];
        megaDrone = new MegaDrone("megaDrone", 500, ball_B);

        // We load the 3 objectives for the player to hit.
        initObjLocations(objLocations);
        setUpObjectives(objLocations, ball_A);

        // We set up collision detection for the player by creating
        // a capsule collision shape and a CharacterControl.
        // The CharacterControl offers extra settings for
        // size, stepheight, jumping, falling, and gravity.
        // We also put the player in its starting position.
        playerNode = new Node("player");
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 3f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);   
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
        playerNode.addControl(player);
        
        // Set up the shooting audio
        shotSound = new AudioNode(assetManager, "Sound/Effects/Gun.wav");
        boomSound = new AudioNode(assetManager, "Sound/Effects/Bang.wav");
        boomSound.setPositional(true);
        rootNode.attachChild(shotSound); //make the positional sound actually work
        rootNode.attachChild(boomSound);
        
        // Set up the hover Jet
        Spatial hoverJet = assetManager.loadModel("Models/FighterBomber.mesh.xml");
        Material mat_hj = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        mat_hj.setTexture("LightMap", assetManager.loadTexture(
                new TextureKey("Models/FighterBomber.png", false)));
        hoverJet.setMaterial(mat_hj);
        hoverJet.setName("hoverJet");
        hoverJetQ = Quaternion.IDENTITY;
        playerNode.attachChild(hoverJet);

        // Set up the camera bits
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, playerNode, inputManager);
        chaseCam.setSmoothMotion(false);
        chaseCam.setLookAtOffset(new Vector3f(0, 6f, 0));
        chaseCam.setZoomSensitivity(0);
        chaseCam.setDragToRotate(false);
        chaseCam.setMinVerticalRotation(-(FastMath.HALF_PI+ 0.7f));
        chaseCam.setMaxVerticalRotation(FastMath.HALF_PI*0.8f);
        chaseCam.setInvertVerticalAxis(true);
        
        // Move the player to the starting location
        player.setPhysicsLocation(new Vector3f(-480, 8f, -480f));

        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        bulletAppState.getPhysicsSpace().addAll(sceneModel);
        bulletAppState.getPhysicsSpace().add(playerNode);
        bulletAppState.getPhysicsSpace().add(player);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
  }
    
   /*
   * This is the main event loop--walking happens here.
   * We check in which direction the player is walking by interpreting
   * the camera direction forward (camDir) and to the side (camLeft).
   * The setWalkDirection() command is what lets a physics-controlled player walk.
   * We also make sure here that the camera moves with player.
   */
    @Override
    public void update(float tpf) {
        // Fixes bug where ball may show as hit at beginning of players turn
        if(isStart) {
            for(int i = 0; i < MAX_OBS; i++) {
                microDrones[i].unhit();
                // Set appropriate color for ball
                microDrones[i].getGeo().setMaterial(ball_A);
                microDrones[i].getRigidBodyControl().clearForces();
            }
        }
        
        // Handle player movement (the user's character)
        camDir = cam.getDirection().multLocal(0.6f); // The use of multLocal here is to control the rate of movement multiplier for character walk speed.
        camLeft = cam.getLeft().multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        
        // Slowly adjust the hoverJet to match camera direction
        Spatial hJ = rootNode.getChild("hoverJet");
        float angles[] = new float[3];
        if(hJ != null) {
            hoverJetQ.slerp(cam.getRotation().mult(yaw90), 0.025f);
            hoverJetQ.toAngles(angles);
            hoverJetQ = hoverJetQ.fromAngleAxis(
                    angles[1], Vector3f.UNIT_Y).normalizeLocal();
            hJ.setLocalRotation(hoverJetQ);
        }
        
        // Keep the audio listener moving with the camera
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
        
        // Find out if there are balls left to hit
        boolean isRemaining = false; //assume all targets are hit
        for(MicroDrone b : microDrones) {
            // Game continues if there are balls yet to hit
            if(!b.isHit()) {
                isRemaining = true;
                break;
            }
        }
        
        // Check if round is completed (i.e. all balls hit), or the game is over
        if(!isRemaining) {
            if(spawnCount < MAX_SPAWN - 1) {
                resetBalls();
                spawnCount++;
            } else {
                spawnCount = 0; //reset the counter
                // Game over. No rounds left, quit this appstate
                if(currentRound == totalRounds) {gameOver();}
                else {roundOver();} // Just the end the current round
            }
        }
        
        // Make the balls move towards the player.
        updateEnemyPositions();
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        
        // Clear all mappings and listeners, detach all nodes and physics
        setEnabled(false);
        stateManager.detach(bulletAppState);
        rootNode.detachAllChildren();
        rootNode.removeLight(al);
        rootNode.removeLight(dl);
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        //Pause and unpause
        super.setEnabled(enabled);
        
        if(enabled) {
            // Set up a color for the sky
            //viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
            if(!chaseCam.isEnabled()) {
                // Seems to be a bug with JME3's chaseCam. Set enable method
                // never changes the protected field canRotate back to true
                // when it is re-enabled, but setDragToRotate(false) does. :)
                // I submitted a ticket to the JME3's tracking system with solution.
                // Issue #643
                chaseCam.setEnabled(true);
                chaseCam.setDragToRotate(false);
            }
            // This block is where everything gets attached rootNode
            for(int i = 0; i < MAX_OBS; i++) {
                rootNode.attachChild(microDrones[i].getGeo());
            }
            rootNode.attachChild(sceneModel);
            rootNode.addLight(al);
            rootNode.addLight(dl);
            rootNode.attachChild(shotSound);
            rootNode.attachChild(boomSound);
            rootNode.attachChild(shockwave.getShockWave());
            rootNode.attachChild(playerNode);
            rootNode.attachChild(megaDrone.getGeo());
            
            // Establish player controls
            initControls();
            initCrossHairs(true);
            
            // Enable the physics app state
            bulletAppState.setEnabled(true);
        }
        else {
            // Remove playser input controls
            removeControls();
            initCrossHairs(false);
            bulletAppState.setEnabled(false);
            left = false; right = false; up = false; down = false;
            rootNode.detachAllChildren();
            rootNode.removeLight(al);
            rootNode.removeLight(dl);
        }
    }
    
    /*
     * This method creates a sets the locations of three spawn points for the
     * first round of balls.
     */ 
    public void initObjLocations(Vector3f[] pts) {       
         // Create the spawn locations for the microDrones
        for(int i = 0; i < MAX_OBS; i++) {
            //Position at the far end of the "tunnel"
            pts[i] = new Vector3f(50, 12, -456+(i*75));
        }
    }

    /*
     * This method fills an array of type MicroDrone with new ball objects and
     * attaches their physical controls.
     */
    public void setUpObjectives(Vector3f[] objLocations, Material ball_type) {
        MicroDrone s;
               
        for(int i = 0; i < MAX_OBS; i++) {
            s = new MicroDrone("S" + i, 100, ball_type);
            microDrones[i] = s;
            microDrones[i].getGeo().setLocalTranslation(objLocations[i]); //set spawn location
            bulletAppState.getPhysicsSpace().add(s.getRigidBodyControl()); //add it to phys space
        }
        
        // Set up the big drone mother ship
        bulletAppState.getPhysicsSpace().add(megaDrone.getRigidBodyControl());
        bulletAppState.getPhysicsSpace().add(megaDrone.getGhostControl());
        megaDrone.getRigidBodyControl().setPhysicsLocation(new Vector3f(40, 12, -388));
    }

    /*
     * This method handles what to do when a collision occurs. It turns a ball
     * green if it has not been hit yet.
     */
    public void collision(PhysicsCollisionEvent e) {
        // Check for collision with any microDrone and a bullet
        Enemy enemy;
        String NodeA = e.getNodeA().getName();
        String NodeB = e.getNodeB().getName();
  
        if((enemy = getDrone(NodeA, microDrones)) != null) { //check NodeA
            if(NodeB.equals("bullet")) {
                if(!enemy.isHit()) {
                    final Geometry geo = ((MicroDrone)enemy).getGeo();
                    geo.setMaterial(ball_hit); //set material to hit
                    rootNode.detachChild(geo);
                    bulletAppState.getPhysicsSpace().remove(geo);
                    enemy.hit(); //update ball as hit
                    shockwave.explode(geo.getLocalTranslation()); //add special effect
                    boomSound.setLocalTranslation(geo.getLocalTranslation());
//                    boomSound.playInstance(); //comment out to mute
                    stateManager.getState(GuiAppState.class).showHitObject("ball", enemy.getPoints());
                }
            isStart = false; //there was a collision = no longer start of turn
            } else if(NodeB.equals("player")) {
                 //TODO: make the player move

            }
        } else if((enemy = getDrone(NodeB, microDrones)) != null ) {  //check NodeB
            if(NodeA.equals("bullet")) {
                if(!enemy.isHit()) {
                    final Geometry geo = ((MicroDrone)enemy).getGeo();
                    geo.setMaterial(ball_hit); //set material to hit
                    rootNode.detachChild(geo);
                    bulletAppState.getPhysicsSpace().remove(geo);
                    enemy.hit(); //update ball as hit
                    shockwave.explode(geo.getLocalTranslation()); //add special effect
                    boomSound.setLocalTranslation(geo.getLocalTranslation());
//                    boomSound.playInstance();
                    stateManager.getState(GuiAppState.class).showHitObject("ball", enemy.getPoints());
                }
                isStart = false; //there was a collision = no longer start of turn
            } else if (NodeA.equals("player")) {
                 //TODO: make the player move

            }
        }
        l = megaDrone.getGhostControl().getOverlappingObjects();
        // Check for infiltration of the mother ship's airspace
        if(megaDrone.getGhostControl().getOverlappingObjects().contains(playerNode.getControl(CharacterControl.class))) {
            System.out.println("You are too close, run away!!!");
        }
        
        // Remove the bullet physics control from the world
        if(NodeA.equals("bullet")) {
            bulletAppState.getPhysicsSpace().remove(e.getNodeA());
            e.getNodeA().removeFromParent();
        } else if(NodeB.equals("bullet")) {
            bulletAppState.getPhysicsSpace().remove(e.getNodeB());
            e.getNodeB().removeFromParent();
        }
    }
    
    /*
     * This method creates a white ambient light as well as a white
     * directional light.
     */
    private void setUpLight() {
        // We add light so we see the scene
        al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));

        dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());   
    }

    /* 
     * This method sets up all materials used for for the ball objects.
     */
    private void initMaterials() {
        // For a ball hit by player
        ball_hit = new Material(
                assetManager, "Common/MatDefs/Light/Lighting.j3md");
        ball_hit.setBoolean("UseMaterialColors",true);
        ball_hit.setColor("Diffuse", ColorRGBA.Green);
        ball_hit.setColor("Ambient", new ColorRGBA(0.0f, 0.5f, 0.0f, 1.0f));
        
        // For player 1
        ball_A = new Material(
                assetManager, "Common/MatDefs/Light/Lighting.j3md");
        ball_A.setBoolean("UseMaterialColors",true);
        ball_A.setColor("Diffuse", ColorRGBA.Red);
        ball_A.setColor("Ambient", new ColorRGBA(0.5f, 0.0f, 0.0f, 1.0f));
        
        // For player 2
        ball_B = new Material(
                assetManager, "Common/MatDefs/Light/Lighting.j3md");
        ball_B.setBoolean("UseMaterialColors",true);
        ball_B.setColor("Diffuse", ColorRGBA.Blue);
        ball_B.setColor("Ambient", new ColorRGBA(0.0f, 0.0f, 0.5f, 1.0f));
        
        // For the bullets
        mat_bullet = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_bullet.setColor("Color", ColorRGBA.Black);
        
        // For invisible objects
        mat_invis = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_invis.setColor("Color", ColorRGBA.BlackNoAlpha);
    }
    
    /*
     * This method returns MicroDrone object if it is found by name in an array
     * of type MicroDrone, or null if it is not found. Calling method must check
     * for null return.
     */
    private MicroDrone getDrone(String a, MicroDrone[] balls) {
        for(MicroDrone b : balls) {
            //If the ball's name is found, make it hit and report found
            if(a.equals(b.getGeo().getName())) {
                return b; //name found
            }
        }
        return null; //name not in objective list
    }
    
    /*
     * This method handles action events.
     */
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
          if (value) { left = true; } else { left = false; }
        } else if (binding.equals("Right")) {
          if (value) { right = true; } else { right = false; }
        } else if (binding.equals("Up")) {
          if (value) { up = true; } else { up = false; }
        } else if (binding.equals("Down")) {
          if (value) { down = true; } else { down = false; }
        } else if (binding.equals("Jump")) {
          player.jump();
        } else if (binding.equals("shoot") && value) {
            // Play the gun firing sound
//            shotSound.playInstance();
            
            // Create and move the bullet
            Geometry bulletg = new Geometry("bullet", bullet);
            bulletg.setMaterial(mat_bullet);
            bulletg.setName("bullet");
            //don't just use cam.getLocation. bullets will not work when back is on a wall
            bulletg.setLocalTranslation(playerNode.getWorldTranslation().add(0, 6f, 0));
            bulletg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            bulletg.addControl(new RigidBodyControl(bulletCollisionShape, 1));
            bulletg.getControl(RigidBodyControl.class).setCcdMotionThreshold(0.01f);
            bulletg.getControl(RigidBodyControl.class).setLinearVelocity(cam.getDirection().mult(500));
            rootNode.attachChild(bulletg);
            getPhysicsSpace().add(bulletg);
        }
        if(binding.equals("reset")) {
            resetBalls();
        }
    }

    /*
     * This method sets up the custom key bindings for the player control.
     */
    private void initControls() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("reset", new KeyTrigger(KeyInput.KEY_R)); //TESTING ONLY
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
        inputManager.addListener(this, "shoot");
        inputManager.addListener(this, "reset");
    }

    /*
     * This method removes the custom player key bindings.
     */
    private void removeControls() {
        inputManager.deleteMapping("Left");
        inputManager.deleteMapping("Right");
        inputManager.deleteMapping("Up");
        inputManager.deleteMapping("Down");
        inputManager.deleteMapping("Jump");
        inputManager.deleteMapping("shoot");
        inputManager.deleteMapping("reset");
        inputManager.removeListener(this);
    }

    /*
     * This method resets everything necessary to play a new round of the game
     * for one player.
     */
    private void resetLevel() { 
        // Reset the balls back to start location
        resetBalls();       
        
        // Reset the player back to start location and stop all movements
        resetPlayer(); 
    }
    
    /*
     * This method places the character at the starting location.
     */
    public void resetPlayer() {
        // Reset the player back to origin and stop all movements
        player.setPhysicsLocation(new Vector3f(-480, 8f, -480f)); //largeScene
    }
    
    /*
     * This method resets the balls to the starting location.
     */
    public void resetBalls() {
        // Detach all balls and remove phyisics control
        for(int i = 0; i < MAX_OBS; i++) {
            rootNode.detachChildNamed("S" + i);
            bulletAppState.getPhysicsSpace().remove(microDrones[i].getGeo());
        }

        // Update actual ball/ball physics locations with new locations and reattach
        Vector3f[] objLocations = new Vector3f[MAX_OBS];
        initObjLocations(objLocations); //get new locations
        
        Geometry g; //temp variable for ball's geometry
        for(int i = 0; i < MAX_OBS; i++) {
            g = microDrones[i].getGeo(); //get handle for a ball
            
            //reset ball to not be hit
            microDrones[i].unhit();
            
            //set new random location of ball geometry and physics control
            g.setLocalTranslation(objLocations[i]);
            microDrones[i].getRigidBodyControl().setPhysicsLocation(objLocations[i]);
                        
            //set appropriate color for ball
            g.setMaterial(ball_A); //color player 1
                       
            //reattach the ball physics control
            bulletAppState.getPhysicsSpace().add(microDrones[i].getRigidBodyControl());
            
            //reattach ball to scene graph
            rootNode.attachChild(g); 
        }      
    }
    
    /*
     * This method is a helper function to get the physics space with less
     * typing.
     */
    private PhysicsSpace getPhysicsSpace() {
            return bulletAppState.getPhysicsSpace();
    }
    
    /*
     * This method adds or removes a crosshair to the screen to make shooting
     * much easier.
     */
    private void initCrossHairs(boolean enable) {
        if(enable) {
            // Center the crosshairs
            ch.setLocalTranslation(
                app.getContext().getSettings().getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                app.getContext().getSettings().getHeight() / 2 + ch.getLineHeight() / 2, 0);
            guiNode.attachChild(ch);
        } else {
            guiNode.detachAllChildren();
        }
    }
    
    /*
     * This method sets the total number of rounds a player will play before the
     * game is over.
     */
    public void setTotalRounds(int r) {
        totalRounds = r;
    }
    
    /*
     * This method returns the total points.
     */
    public int getTotalPoints() {
        return totalPoints;
    }
    
    /*
     * This method adds the points to the total points.
     */
    public void addPoints(int points) {
        this.totalPoints += points;
    }
    
    /*
     * This method sets up the font and text for the crosshair before adding it
     * to the guiNode.
     */
    public void initCrossHairText() {
        guiFont = assetManager.loadFont("Interface/Fonts/Arial.fnt");
        ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
    }
    
    /*
     * This method prevents any scene objects from flying off into outerspace.
     */
    public void initInvisibleWalls () {
        // Create an invisible top cover to prevent rouge objects going into outerspace
        Box cover = new Box(1024, 0.1f, 1024);
        
        // Create the shape
        Geometry cube = new Geometry("cube", cover);
        
        // Set the colors to be invisible
        cube.setMaterial(mat_invis);
                
        // Set up the static rigid body physics control       
        RigidBodyControl cubePhys = new RigidBodyControl(0);
        
        // Attach the control to the wall
        cube.addControl(cubePhys);
        
        // Translate them appropriately
        cubePhys.setPhysicsLocation(new Vector3f(0, 20, 0));        
     
        
        // Add the physical control to the physics space
        bulletAppState.getPhysicsSpace().add(cubePhys);
    }
    
    /*
     * This method returns the current round.
     */
    public int getCurrentRound() {
        return currentRound;
    }
    
    /*
     * This method returns the total rounds to be played.
     */
    public int getTotalRounds() {
        return totalRounds;
    }
    
    /*
     * This method makes the floating balls move around straight to the player.
     */
    public void updateEnemyPositions() {
        for(int i = 0; i < MAX_OBS; i++) {
            // Make the balls move directly towards the player's location
            Vector3f v = player.getPhysicsLocation();
            v = v.subtract(microDrones[i].getRigidBodyControl().getPhysicsLocation()).normalizeLocal();
            //v.setY(1);
            microDrones[i].getRigidBodyControl().applyImpulse(v.mult(0.3f), Vector3f.ZERO);
        }
    }
    
    /*
     * This method hadles what to do when the game is over, i.e. all rounds have
     * been completed by the player. It freezes the physics, i.e. nothing
     * can translate in the scene. And the camera freezes.
     */
    public void gameOver() {
        // Disable the physics and game play app states and look at the ship
        bulletAppState.setEnabled(false);

        chaseCam.setEnabled(false);
        cam.setLocation(new Vector3f(-499f, 18f, -480f));
        cam.lookAt(new Vector3f(-480f, 12f, -480f), Vector3f.UNIT_Y);
        
        // Save any parting information for the player to see
        stateManager.getState(GuiAppState.class).setTotalPointsEnd(totalPoints);
        
        // Reset everything else in case player starts over
        currentRound = 1; //reset the current round back to beginning
        totalPoints = 0;
        resetLevel();
        
        // Change the app state and go to appropriate screen
        stateManager.getState(GuiAppState.class).setEnabled(true);
        stateManager.getState(GuiAppState.class).goToEnd();       
    }
    
    /*
     * This method hadles what to do when a round is over, i.e. all enemies have
     * been destroyed, or the time limit has been reached.
     */
    public void roundOver() {
        // Pause the game for some light reading first
        setEnabled(false);
        bulletAppState.setEnabled(false);
        
        // Go to next round and reset the player position and balls
        currentRound++;
        resetLevel();
        isStart = true;

        // Here is the light reading
        stateManager.getState(GuiAppState.class).setEnabled(true);
        stateManager.getState(GuiAppState.class).goToPause();
    }
}