//TODO: 
// 1. Give yourself a health meter.
// 2. Create an output file that is read in every time to load the high scores.
// 3. Add explosion to the slideEnemy when player gets too close
// 4. Add the hillEnemy functionality
// 5. Make slideEnemy and hillEnemey respawn after being shot

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
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
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
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

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
    private FlyByCamera       flyCam;
    private ChaseCamera       chaseCam;
    private Camera            cam;
    private Listener          listener;
    
    // Appstate specific fields
    private Spatial sceneModel;
    private BulletAppState bulletAppState;
    private Node playerNode; //wraps player CharacterControl with a name
    private CharacterControl player;
    private boolean left = false, right = false, up = false, down = false;
    private MegaDrone megaDrone;
    private SlideEnemy slideEnemy;
    private Material ball_hit, ball_A, ball_B, mat_bullet, mat_invis;
    private int totalRounds = 0; //1 round for each player * desired # of cycles
    private int totalPoints = 0;
    private int currentRound = 1;
    private AmbientLight al;
    private DirectionalLight dl;
    private Quaternion hoverJetQ; //rotation of the hover jet
    private boolean isRoundOver = false;

    // Temporary vectors used on each frame.
    // They here to avoid instanciating new vectors on each frame
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private Vector3f walkDirection = new Vector3f();
    private Vector3f slideEnemyBullet = null;
    
    // Special Effects
    private Shockwave shockwave;
    
    // Bullet fields
    private Sphere bullet;
    private SphereCollisionShape bulletCollisionShape;
    private AudioNode shotSound, boomSound, megaDroneHitSound;
    
    // Gui Stuff
    private BitmapFont guiFont;
    private BitmapText ch;
    public boolean isRunning = false; //encasulate with a getter method
     
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
        this.flyCam         = this.app.getFlyByCamera();
        this.cam            = this.app.getCamera();
        this.listener       = this.app.getListener();
        
        // Get the physics app state
        bulletAppState = stateManager.getState(BulletAppState.class);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);

        // Set up the bullet object
        bullet = new Sphere(32, 32, 0.2f, true, false);
        bullet.setTextureMode(Sphere.TextureMode.Projected);
        bulletCollisionShape = new SphereCollisionShape(0.2f);
        
        // Set up the lights for the scene
        setUpLight();
    
        // Set up special effects
        shockwave = new Shockwave(rootNode, assetManager);

        // Init the spawn trigger walls
        initInvisibleWalls();

        // We load the scene
        sceneModel = assetManager.loadModel("Scenes/SunakSunakLa.j3o");
        sceneModel.setLocalTranslation(0, -30, 0);

        // Init materials
        initMaterials();

        // Init text for crosshair text
        initCrossHairText();

        // We set up collision detection for the player by creating
        // a capsule collision shape and a CharacterControl.
        // We also put the player in its starting position.
        playerNode = new Node("player");
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 3f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setViewDirection(new Vector3f(1, 0, 0));
        player.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
        playerNode.addControl(player);
        
        // Set up the shooting audio
        shotSound = new AudioNode(assetManager, "Sounds/Laser_Shoot3.wav");
        shotSound.setReverbEnabled(false);
        boomSound = new AudioNode(assetManager, "Sounds/Explosion.wav");
        boomSound.setPositional(true);
        megaDroneHitSound = new AudioNode(assetManager, "Sounds/MegaDroneHit.wav");
        megaDroneHitSound.setPositional(true);
        megaDroneHitSound.setReverbEnabled(false);
        megaDroneHitSound.setVolume(0.15f);
        rootNode.attachChild(shotSound); //make the positional sound actually work
        rootNode.attachChild(boomSound);
        rootNode.attachChild(megaDroneHitSound);
        
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

        // Init the mothership and make the sensor field only collide with the player
        megaDrone = new MegaDrone("megaDrone", ball_B, playerNode, assetManager);
        megaDrone.getGhostControl().setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        megaDrone.getGhostControl().setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_02);
        
        // Init the simple sliding back and forth enemy
        slideEnemy = new SlideEnemy("slideEnemy", ball_hit, playerNode);
        megaDrone.getGhostControl().setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        megaDrone.getGhostControl().setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_02);
        
        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        bulletAppState.getPhysicsSpace().addAll(sceneModel);
        bulletAppState.getPhysicsSpace().add(player);
        bulletAppState.getPhysicsSpace().add(megaDrone.getRigidBodyControl());
        bulletAppState.getPhysicsSpace().add(megaDrone.getGhostControl());
        bulletAppState.getPhysicsSpace().add(slideEnemy.getEnemyControl());
        bulletAppState.getPhysicsSpace().add(slideEnemy.getGhostControl());
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
        // Only update anything when the player is playing
        if(isRunning) {
            // Check if round is completed (i.e. all balls hit), or the game is over
            if(megaDrone.gethealth() <= 0) {
                isRoundOver = true;
            }

            if(isRoundOver) {
                // Game over. No rounds left, quit this appstate
                if(currentRound == totalRounds) {gameOver();}
                else {roundOver();} // Just the end the current round
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
                hoverJetQ.slerp(cam.getRotation(), 0.025f);
                hoverJetQ.toAngles(angles);
                hoverJetQ.fromAngleAxis(angles[1], Vector3f.UNIT_Y).normalizeLocal();
                hJ.setLocalRotation(hoverJetQ);
            }

            // Attempt to have slideEnemy shoot
            slideEnemyBullet = slideEnemy.shoot(mat_bullet);
            if(slideEnemyBullet != null) {
                // Create and move the bullet
                Geometry bulletg = new Geometry("bullet1", bullet);
                bulletg.setMaterial(mat_bullet);
                bulletg.setLocalTranslation(slideEnemy.getEnemyControl().getPhysicsLocation().add(slideEnemyBullet.mult(12f)));
                bulletg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                bulletg.addControl(new RigidBodyControl(bulletCollisionShape, 10f));
                bulletg.getControl(RigidBodyControl.class).setCcdMotionThreshold(0.01f);
                bulletg.getControl(RigidBodyControl.class).setLinearVelocity(slideEnemyBullet.mult(250f));
                rootNode.attachChild(bulletg);
                getPhysicsSpace().add(bulletg);
            }

            // Keep the audio listener moving with the camera
            listener.setLocation(cam.getLocation());
            listener.setRotation(cam.getRotation());
        }
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
            if(!chaseCam.isEnabled()) {
                // Seems to be a bug with JME3's chaseCam. Set enable method
                // never changes the protected field canRotate back to true
                // when it is re-enabled, but setDragToRotate(false) does. :)
                // I submitted a ticket to the JME3's tracking system with solution.
                // Issue #643
                chaseCam.setEnabled(true);
                chaseCam.setDragToRotate(false); //sets chaseCam.canRotate back to true
            }

            rootNode.attachChild(sceneModel);
            rootNode.addLight(al);
            rootNode.addLight(dl);
            rootNode.attachChild(shotSound);
            rootNode.attachChild(boomSound);
            rootNode.attachChild(playerNode);
            rootNode.attachChild(megaDrone.getSpatial());
            rootNode.attachChild(slideEnemy.getGeo());
            
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
            megaDrone.getMinions().clear();
        }
    }

    //TODO: Clean this method up. First check for a bullet collision, then
    //      if/else if for the second object involved and handle them there.
    //      this should cut down on the number of checks required to be made.
    //      Still need the mothership's airspace in a separate if though.
    /*
     * This method handles what to do when a collision occurs. It turns a ball
     * green if it has not been hit yet.
     */
    public void collision(PhysicsCollisionEvent e) {
        // Check for collision with any microDrone and a bullet
        Spatial NodeA = e.getNodeA();
        Spatial NodeB = e.getNodeB();
  
        // Check for collision with a microDrone
        if(NodeA.getName().equals("microDrone")) { //check NodeA
            if(NodeB.getName().equals("bullet")) {
                shockwave.explode(NodeA.getWorldTranslation());
                bulletAppState.getPhysicsSpace().remove(e.getNodeA());
                e.getNodeA().removeFromParent();
                megaDrone.removeMinion(NodeA);
                boomSound.setLocalTranslation(NodeA.getWorldTranslation());
                boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NodeA.getName(), MicroDrone.points);
            } else if(NodeB.getName().equals("player")) {
                 //TODO: make the player move or mess up the controls a bit or score

            }
        } else if(NodeB.getName().equals("microDrone")) {  //check NodeB
            if(NodeA.getName().equals("bullet")) {
                shockwave.explode(NodeA.getWorldTranslation());
                bulletAppState.getPhysicsSpace().remove(e.getNodeB());
                e.getNodeB().removeFromParent();
                megaDrone.removeMinion(NodeB);
                boomSound.setLocalTranslation(NodeB.getWorldTranslation());
                boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NodeB.getName(), MicroDrone.points);
            } else if (NodeA.getName().equals("player")) {
                 //TODO: make the player move or mess up the controls a bit or score
            }
        }
        
        // Check for infiltration of the mother ship's airspace
        if(megaDrone.getGhostControl().getOverlappingObjects().contains(playerNode.getControl(CharacterControl.class))) {
            // Spawn a new drone if there is less than 4 in scene already
            MicroDrone m = megaDrone.createMicroDrone(ball_A); // returns null if should not add new drone
            if(m != null) {
                // PLace the new drone between the player and the megaDrone
                Vector3f v = playerNode.getWorldTranslation().
                        subtract(megaDrone.getSpatial().getWorldTranslation()).normalize();
                v.setY(playerNode.getWorldTranslation().y);
                m.getRigidBodyControl().setPhysicsLocation(
                        megaDrone.getSpatial().getWorldTranslation().add(v.mult(new Vector3f(15f, 1f, 15f))));
                
                // Attach the new drone to the scene and physics space
                rootNode.attachChild(m.getGeo());
                bulletAppState.getPhysicsSpace().add(m.getRigidBodyControl()); 
            }
        }
        
        // Check for bullet hitting megaDrone
        if(NodeA.getName().equals("megaDrone") && NodeB.getName().equals("bullet")) {
            megaDrone.hit();
            if(megaDrone.gethealth() > 0) { //hit but not dead
                megaDroneHitSound.setLocalTranslation(megaDrone.getSpatial().getWorldTranslation());
                megaDroneHitSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NodeA.getName(), MegaDrone.hitPoint);
            } else { //hit and dead
                boomSound.setLocalTranslation(megaDrone.getSpatial().getWorldTranslation());
                boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NodeA.getName(), MegaDrone.killPoint);
            }
            
        } else if(NodeB.getName().equals("megaDrone") && NodeA.getName().equals("bullet")) {
            megaDrone.hit();
            if(megaDrone.gethealth() > 0) { //hit but not dead
                megaDroneHitSound.setLocalTranslation(megaDrone.getSpatial().getWorldTranslation());
                megaDroneHitSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NodeA.getName(), MegaDrone.hitPoint);
            } else { //hit and dead
                boomSound.setLocalTranslation(megaDrone.getSpatial().getWorldTranslation());
                boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NodeA.getName(), MegaDrone.killPoint);
            }
        }
        
        // Remove the bullet physics control from the world
        if(NodeA.getName().equals("bullet") || NodeA.getName().equals("bullet1")) {
            bulletAppState.getPhysicsSpace().remove(e.getNodeA());
            NodeA.removeFromParent();
        } else if(NodeB.getName().equals("bullet") || NodeB.getName().equals("bullet1")) {
            bulletAppState.getPhysicsSpace().remove(e.getNodeB());
            NodeB.removeFromParent();       
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
            shotSound.setLocalTranslation(player.getPhysicsLocation());
            shotSound.playInstance();
            
            // Create and move the bullet
            Geometry bulletg = new Geometry("bullet", bullet);
            bulletg.setMaterial(mat_bullet);
            bulletg.setName("bullet");
            //don't just use cam.getLocation. bullets will not work when back is on a wall
            bulletg.setLocalTranslation(playerNode.getWorldTranslation().add(0, 6f, 0));
            bulletg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            bulletg.addControl(new RigidBodyControl(bulletCollisionShape, 0.25f));
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
    public void resetLevel() { 
        // Reset the mothership back to start location
        resetMegaDrone();
        
        // Reset the slideEnemy
        resetSlideEnemy();
        
        // Reset the player back to start location and stop all movements
        resetPlayer(); 
    }
    
    /*
     * This method clears the mothership's drones, hit points, and resets its
     * location.
     */
    public void resetSlideEnemy() {
        // Reset the hit flag
        slideEnemy.unhit();
        
        // Put it back into the proper starting location
        slideEnemy.getGeo().setLocalTranslation(new Vector3f(0f, 10f, 0f));
    }
    
    /*
     * This method clears the mothership's drones, hit points, and resets its
     * location.
     */
    public void resetMegaDrone() {
        // Reset the isHit flag and health
        megaDrone.unhit();
        
        // Get rid of all of the minions that might be out in the scene graph
        for(MicroDrone m : megaDrone.getMinions()) {
            bulletAppState.getPhysicsSpace().remove(m.getRigidBodyControl());
            m.getGeo().removeFromParent();  
        }
        
        // Get rid of the minions from their actual mothership (megaDrone)
        megaDrone.clearMinions();
        
        // Put it back into the proper starting location
        megaDrone.getRigidBodyControl().setPhysicsLocation(new Vector3f(65f, 35f, -65f));
    }
    
    /*
     * This method places the character at the starting location.
     */
    public void resetPlayer() {
        // Put the player back to their proper start location
        player.setPhysicsLocation(new Vector3f(-95f, 30f, 95f));
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
        Box cover = new Box(256f, 0.1f, 256f);
        
        // Create the shape
        Geometry cube = new Geometry("cube", cover);     
        
        // Set the colors to be invisible
        cube.setMaterial(mat_invis);
                
        // Set up the static rigid body physics control       
        RigidBodyControl cubePhys = new RigidBodyControl(0);
        
        // Attach the control to the wall
        cube.addControl(cubePhys);
        
        // Translate them appropriately
        cubePhys.setPhysicsLocation(new Vector3f(0, 150, 0));        
     
        
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
     * This method hadles what to do when the game is over, i.e. all rounds have
     * been completed by the player. It freezes the physics, i.e. nothing
     * can translate in the scene. And the camera freezes.
     */
    public void gameOver() {
        // Disable the physics and game play app states and look at the ship
        bulletAppState.setEnabled(false);
        isRunning = false;


        chaseCam.setEnabled(false);
        cam.setLocation(new Vector3f(-499f, 18f, -480f));
        cam.lookAt(new Vector3f(-480f, 12f, -480f), Vector3f.UNIT_Y);
        
        // Save any parting information for the player to see
        stateManager.getState(GuiAppState.class).setTotalPointsEnd(totalPoints);
        
        // Reset everything else in case player starts over
        currentRound = 1; //reset the current round back to beginning
        totalPoints = 0;
        isRoundOver = false;
        resetLevel();
        
        // Change the app state and go to appropriate screen
        //Testing
        setEnabled(false);
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
        isRoundOver = false;
        resetLevel();

        // Here is the light reading
        stateManager.getState(GuiAppState.class).setEnabled(true);
        stateManager.getState(GuiAppState.class).goToPause();
    }
}