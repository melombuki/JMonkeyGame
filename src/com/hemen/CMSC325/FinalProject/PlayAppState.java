package com.hemen.CMSC325.FinalProject;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Listener;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
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
import java.util.Random;

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
    private Spatial sceneModel;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private Node playerNode; //wraps player CharControl with a name
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    private myBall[] goals;
    private Material ball_hit, ball_A, ball_B, mat_bullet, mat_invis;
    private int totalRounds = 0; //1 round for each player * desired # of cycles
    private int currentRound = 1;
    private AmbientLight al;
    private DirectionalLight dl;
    private Quaternion hoverJetQ;
    private Quaternion yaw90 = new Quaternion().fromAngleAxis(
                FastMath.HALF_PI, Vector3f.UNIT_Y);
    private boolean isStart = true; //fixes bug where ball is sometimes hit at start of turn
    private Node cube = new Node("Cube");
    
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
        //TODO: add options in GUI to choose how many cycles to play
        
        //totalRounds must = totalPlayers * cycles
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
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);

        //Set up the bullet object
        bullet = new Sphere(32, 32, 0.4f, true, false);
        bullet.setTextureMode(Sphere.TextureMode.Projected);
        bulletCollisionShape = new SphereCollisionShape(0.1f);
        
        // We re-use the flyby camera for rotation, while positioning is handled by physics
        //flyCam.setMoveSpeed(100);
        setUpLight();
    
        // Set up special effects
        shockwave = new Shockwave(rootNode, assetManager);

        // We load the scene from the zip file and adjust its size.
        assetManager.registerLocator("town.zip", ZipLocator.class);
        sceneModel = assetManager.loadModel("main.scene");
        sceneModel.setLocalScale(2f);
        initInvisibleWalls();
              
//        //Testing ---- TODO: Need to make it with only triangles,
//        //Problem: Currently get arryIndexOutOfBoundsException with it sometimes
//        // We load the scene
//        sceneModel = assetManager.loadModel("Scenes/NewPlanet.j3o");
//        sceneModel.setLocalTranslation(0, -10, 0);
//        //Testing ----

        // Init materials
        initMaterials();
        
        // Init text for crosshair text
        initCrossHairText();    

        // Init all arrays
        Vector3f[] objLocations = new Vector3f[MAX_OBS];
        goals = new myBall[MAX_OBS];

        // We load the 3 objectives for the player to hit.
        initObjLocations(objLocations);
        setUpObjectives(objLocations, ball_A);
        
        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape((Node) sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);

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
        shotSound = new AudioNode(assetManager, "Sound/Effects/Bang.wav");
        boomSound = new AudioNode(assetManager, "Sound/Effects/Beep.ogg");
        
        // Set up the hover Jet
        Spatial hoverJet = assetManager.loadModel("Models/Cube.001.mesh.xml");
        Material mat_hj = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        
        mat_hj.setTexture("LightMap", assetManager.loadTexture(
                new TextureKey("Models/FighterBomber.png", false)));
        hoverJet.setMaterial(mat_hj);
        hoverJet.setName("hoverJet");
        hoverJet.setLocalScale(3f);
        hoverJetQ = Quaternion.IDENTITY;
        playerNode.attachChild(hoverJet);
        
        // Set up the camera aiming location
        Node gunFocalPoint = new Node("gunFocalPoint");
        gunFocalPoint.setLocalTranslation(
                hoverJet.getLocalTranslation().add(new Vector3f(0, 2.5f, 0)));
        playerNode.attachChild(gunFocalPoint);

        // Set up the camera bits
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, gunFocalPoint, inputManager);
        chaseCam.setSmoothMotion(false);
        chaseCam.setLookAtOffset(Vector3f.UNIT_Y.mult(3));
        chaseCam.setDefaultDistance(5.0f);
        chaseCam.setZoomSensitivity(0);
        chaseCam.setDragToRotate(false);
        chaseCam.setMinDistance(5.0f);
        chaseCam.setMaxVerticalRotation(FastMath.QUARTER_PI);
        chaseCam.setInvertVerticalAxis(true);
        
        // Set player start place
        player.setPhysicsLocation(new Vector3f(0, 10, 0));

        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        bulletAppState.getPhysicsSpace().add(landscape);
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
        // Fixes bug where ball may be hit at beginning of players turn
        if(isStart) {
            for(int i = 0; i < MAX_OBS; i++) {
                goals[i].unhitBall();
                //set appropriate color for ball
                goals[i].getGeo().setMaterial(ball_A); //color player 1
            }
        }
        
        // Handles player movement
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
            hoverJetQ.slerp(cam.getRotation().mult(yaw90), 0.008f);
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
        for(myBall b : goals) {
            // Game continues if there are balls yet to hit
            if(!b.isHit()) {
                isRemaining = true;
                break;
            }
        }
        
        // Check if round is completed (i.e. all balls hit), or the game is over
        if(!isRemaining) {
            // Game over. No rounds left, quit this appstate
            if(currentRound == totalRounds) {gameOver();}        
            else {roundOver();} // Just the end the current round
        }
        
        // Make the balls move towards the player.
        updateBallPositions();
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
            // Enable the physics app state
            bulletAppState.setEnabled(true);
            
            // Set up a color for the sky
            viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
            
            // This block is where everything get attached rootNode
            for(int i = 0; i < MAX_OBS; i++) {
                rootNode.attachChild(goals[i].getGeo());
            }
            rootNode.attachChild(sceneModel);
            rootNode.addLight(al);
            rootNode.addLight(dl);
            rootNode.attachChild(shockwave.getShockWave());
            rootNode.attachChild(playerNode);
            rootNode.attachChild(cube);
            // Establish player controls
            initControls();
            initCrossHairs(true);
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
     * This method creates a new random location in donut around statue in 
     * scene graph of town.zip file by converting from polar to rectagular 
     * coordinates and adding a fixed Y value to create the final 3D vector
     */ 
    public void initObjLocations(Vector3f[] pts) {
        float theta, radius;
        Random rand = new Random();

        // Create the random locations
        for(int i = 0; i < MAX_OBS; i++) {
            radius = (rand.nextFloat()*42)+18;         //produce a number [18-60)
            theta = rand.nextFloat()*FastMath.TWO_PI; //a number [0,2*pi)

            //Position center around statue at approx (40, 10.0f, -17), not origin
            pts[i] = new Vector3f(40 + (float)(radius*Math.cos(theta)), 
                    10.0f, -17 + (float)(radius*Math.sin(theta)));
        }
    }

    /*
     * This method fills an array of type myBall with new ball objects and 
     * attaches their physical controls.
     */
    public void setUpObjectives(Vector3f[] objLocations, Material ball_type) {
        myBall s;
        String name;
               
        for(int i = 0; i < MAX_OBS; i++) {
            s = new myBall("S" + i, 10, 10, 2f, ball_type);
            goals[i] = s;
            goals[i].getGeo().setLocalTranslation(objLocations[i]); //set random location
            goals[i].getGeo().addControl(s.getRigidBodyControl()); //add phy control to geo
            bulletAppState.getPhysicsSpace().add(s.getRigidBodyControl()); //add it to phys space
        }
    }

    /*
     * This method handles what to do when a collision occurs. It turns a ball
     * green if it has not been hit yet.
     */
    public void collision(PhysicsCollisionEvent e) {
        // Check for collision with any ball in objective list
        myBall tempBall;
        String NodeA = e.getNodeA().getName();
        String NodeB = e.getNodeB().getName();
  
        if((tempBall = getBall(NodeA, goals)) != null && NodeB.equals("bullet")) { //check NodeA
            if(!tempBall.isHit()) {
                final Geometry geo = tempBall.getGeo();
                geo.setMaterial(ball_hit); //set material to hit
                tempBall.hitBall(); //update ball as hit
                shockwave.explode(geo.getLocalTranslation()); //add special effect
                boomSound.setLocalTranslation(geo.getLocalTranslation());
                //boomSound.playInstance(); //comment out to mute
                stateManager.getState(GuiAppState.class).showHitObject("ball");
            }
            isStart = false; //there was a collision = no longer start of turn
        } else if((tempBall = getBall(NodeB, goals)) != null && NodeA.equals("bullet")) {  //check NodeB
            if(!tempBall.isHit()) {
                final Geometry geo = tempBall.getGeo();
                geo.setMaterial(ball_hit); //set material to hit
                tempBall.hitBall(); //update ball as hit
                shockwave.explode(geo.getLocalTranslation()); //add special effect
                boomSound.setLocalTranslation(geo.getLocalTranslation());
                //boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject("ball");
            }
            isStart = false; //there was a collision = no longer start of turn
        }
        
        // Remove the bullet physics control from the world
        if(NodeA.equals("bullet")) {
            bulletAppState.getPhysicsSpace().remove(e.getNodeA().getControl(RigidBodyControl.class));
            e.getNodeA().removeFromParent();
        } else if(NodeB.equals("bullet")) {
            bulletAppState.getPhysicsSpace().remove(e.getNodeB().getControl(RigidBodyControl.class));
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
     * This method returns myBall object if it is found by name in an array
     * of type myBall, or null if it is not found. Calling method must check
     * for null return.
     */
    private myBall getBall(String a, myBall[] balls) {
        for(myBall b : balls) {
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
            //shotSound.playInstance();
            
            // Create and move the bullet
            Geometry bulletg = new Geometry("bullet", bullet);
            bulletg.setMaterial(mat_bullet);
            bulletg.setName("bullet");
            
            // Problem was collision with character physics control.
            // Had to move the starting location of the bullet outside of the 
            // character's capsuleCollisionShape
            bulletg.setLocalTranslation(cam.getLocation().addLocal(cam.getDirection().normalize().mult(5f)));
            // New chaseCam fixes the problem and makes vehicle much better
            //bulletg.setLocalTranslation(cam.getLocation());
            bulletg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            bulletg.addControl(new RigidBodyControl(bulletCollisionShape, 1));
            bulletg.getControl(RigidBodyControl.class).setCcdMotionThreshold(0.01f);
            bulletg.getControl(RigidBodyControl.class).setLinearVelocity(cam.getDirection().mult(500));
            rootNode.attachChild(bulletg);
            getPhysicsSpace().add(bulletg);
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
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
        inputManager.addListener(this, "shoot");
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
        inputManager.removeListener(this);
    }

    /*
     * This method resets everything necessary to play a new round of the game
     * for one player.
     */
    private void resetLevel() { 
        // Detach all balls and remove phyisics control
        for(int i = 0; i < MAX_OBS; i++) {
            rootNode.detachChildNamed("S" + i);
            bulletAppState.getPhysicsSpace().remove(goals[i].getGeo());
        }
        
        // Reset the player back to origin and stop all movements
        player.setPhysicsLocation(new Vector3f(0, 10, 0));
        //playerNode.setLocalTranslation(0, 10, 0);

        // Update actual ball/ball physics locations with new locations and reattach
        Vector3f[] objLocations = new Vector3f[MAX_OBS];
        initObjLocations(objLocations); //get new locations
        
        Geometry g; //temp variable for ball's geometry
        for(int i = 0; i < MAX_OBS; i++) {
            g = goals[i].getGeo(); //get handle for a ball
            
            //reset ball to not be hit
            goals[i].unhitBall();
            
            //set new random location of ball geometry and physics control
            g.setLocalTranslation(objLocations[i]);
            goals[i].getRigidBodyControl().setPhysicsLocation(objLocations[i]);
                        
            //set appropriate color for ball
            g.setMaterial(ball_A); //color player 1
                       
            //reattach the ball physics control
            bulletAppState.getPhysicsSpace().add(goals[i].getRigidBodyControl());
            
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
    
    public void setTotalRounds(int r) {
        totalRounds = r;
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
     * This method prevents the player from falling off the map. Need to fix the
     * bounds to match the world.
     */
    public void initInvisibleWalls () {
        // Create the standard wall template for all wall geometries
        Box bLR = new Box(100, 0.1f, 400);
        Box bFB = new Box(600, 0.1f, 100);
        
        // Create the 4 sides of the edge of the town
        Geometry cubeLeft = new Geometry("cubeLeft", bLR);
        Geometry cubeRight = new Geometry("cubeRight", bLR);
        Geometry cubeFront = new Geometry("cubeFront", bFB);
        Geometry cubeBack = new Geometry("cubeBack", bFB);
        
        // Set the colors to be invisible
        cubeLeft.setMaterial(mat_invis);
        cubeRight.setMaterial(mat_invis);
        cubeFront.setMaterial(mat_invis);
        cubeBack.setMaterial(mat_invis);
                
        // Set up the static rigid body physics controls for the wall         
        RigidBodyControl cubeLeftPhys = new RigidBodyControl(0);
        RigidBodyControl cubeRightPhys = new RigidBodyControl(0);
        RigidBodyControl cubeFrontPhys = new RigidBodyControl(0);
        RigidBodyControl cubeBackPhys = new RigidBodyControl(0);
        
        // Attach the controls to the walls
        cubeLeft.addControl(cubeLeftPhys);
        cubeRight.addControl(cubeRightPhys);
        cubeFront.addControl(cubeFrontPhys);
        cubeBack.addControl(cubeBackPhys);
        
        // Rotate & Translate them appropriately
        cubeLeftPhys.setPhysicsRotation(new Quaternion(0, 0, -1, 1)); //90 deg around Z
        cubeLeftPhys.setPhysicsLocation(new Vector3f(-158f, 0f, 0f));        
        cubeRightPhys.setPhysicsRotation(new Quaternion(0, 0, 1, 1));//-90 deg around Z
        cubeRightPhys.setPhysicsLocation(new Vector3f(440f, 0f, 0f));        
        cubeBackPhys.setPhysicsRotation(new Quaternion(-1, 0, 0, 1));//90 deg around X
        cubeBackPhys.setPhysicsLocation(new Vector3f(0f, 0f, -212f));       
        cubeFrontPhys.setPhysicsRotation(new Quaternion(-1, 0, 0, 1));//-90 deg around X
        cubeFrontPhys.setPhysicsLocation(new Vector3f(0f, 0f, 182f));       
        
        // Add the physical controls to the physics space
        bulletAppState.getPhysicsSpace().add(cubeLeftPhys);
        bulletAppState.getPhysicsSpace().add(cubeRightPhys);
        bulletAppState.getPhysicsSpace().add(cubeFrontPhys);
        bulletAppState.getPhysicsSpace().add(cubeBackPhys);
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
    public void updateBallPositions() {
        for(int i = 0; i < MAX_OBS; i++) {
            // Get a random direction from Ken Perlin's ImprovedNoise class
            Vector3f v = player.getPhysicsLocation();
            v = v.subtract(goals[i].getRigidBodyControl().getPhysicsLocation());
            v.setY(0);
            goals[i].getRigidBodyControl().setLinearVelocity(v.mult(0.5f));
        }
        
    }
    
    /*
     * This method hadles what to do when the game is over, i.e. all rounds have
     * been completed by the player.
     */
    public void gameOver() {
        // Disable the physics and game play app states
        bulletAppState.setEnabled(false);
        setEnabled(false);
        
        // Reset everything else in case player starts over 
        currentRound = 1; //reset the current round back to beginning
        resetLevel();
        stateManager.getState(GuiAppState.class).setEnabled(true);
        stateManager.getState(GuiAppState.class).goToStart();
    }
    
    /*
     * This method hadles what to do when a round is over, i.e. all enemies have
     * been destroyed, or the time limit has been reached.
     */
    public void roundOver() {
        // Go to next round and reset the player position and balls
        currentRound++;
        resetLevel();
        isStart = true;

        // Pause the game for some light reading first
        setEnabled(false);
        bulletAppState.setEnabled(false);
        stateManager.getState(GuiAppState.class).setEnabled(true);
        stateManager.getState(GuiAppState.class).goToPause();
    }
}