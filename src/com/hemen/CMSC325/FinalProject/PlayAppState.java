//TODO: Stuff
// 1. Give yourself a health meter. (optional)
// 2. Add explosion to the slideEnemy when player gets too close (optional)
//      |--> If not, get rid of it's ghostcontrol


package com.hemen.CMSC325.FinalProject;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Listener;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
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
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class contains all of the necessary objects and variables for the actual
 * gameplay. It loads three balls and alternates between player 1 and 2 for 2
 * consecutive turns each. A cycle is considered one complete turn for all players
 * selected. A round is one player's turn.
 * 
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
    private Spatial FemaleChar;
    private BetterCharacterControl player;
    private GhostControl playerGhost;
    private boolean left = false, right = false, up = false, down = false;
    private MegaDrone megaDrone;
    private SlideEnemy slideEnemy;
    private HillEnemy hillEnemy;
    private Material mat_green, mat_red, mat_blue, mat_bullet, mat_invis;
    private int totalRounds = 0; //1 round for each player * desired # of cycles
    private int totalPoints = 0;
    private int currentRound = 1;
    private AmbientLight al;
    private DirectionalLight dl;
    private boolean isRoundOver = false;
    
    //Testing
    // Respawn queue to pull cycles out of collision method
    Queue<String> respawnQ;

    // Temporary vectors used on each frame.
    // They here to avoid instanciating new vectors on each frame
    private Vector3f walkDirection = new Vector3f(0f, 0f, 0f);
    private Vector3f boost = new Vector3f(0f, 0f, 0f);
    private Vector3f slideEnemyBullet = null;
    
    // Temp vars for collision method
    Spatial NodeA;
    Spatial NodeB;
    String NameA;
    String NameB;
    String queueString;
    
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
        bulletAppState.setEnabled(false);
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
        
        // Init the respawnQ
        respawnQ = new LinkedList<String>();

        // We set up collision detection for the player by creating
        // a capsule collision shape and a CharacterControl.
        // We also put the player in its starting position.
        playerNode = new Node("player");
        player = new BetterCharacterControl(1.5f, 3f, 8f); 
        player.setViewDirection(new Vector3f(1, 0, 0));
        player.setJumpForce(new Vector3f(0f, 20f, 0f));
        playerNode.addControl(player);
              
        listener.setVolume(0); //mute
        
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
        FemaleChar = assetManager.loadModel("Models/femaleModelBody271/femaleModelBody271.j3o");
        FemaleChar.setLocalScale(0.25f);
        FemaleChar.setName("FemaleChar");
        playerGhost = new GhostControl(new SphereCollisionShape(2f));
        playerGhost.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        FemaleChar.addControl(playerGhost);
        playerNode.attachChild(FemaleChar);

        // Set up the camera bits
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, FemaleChar, inputManager);
        chaseCam.setDefaultHorizontalRotation(FastMath.QUARTER_PI * 3f);
        chaseCam.setSmoothMotion(false);
        chaseCam.setLookAtOffset(new Vector3f(0, 6f, 0));
        chaseCam.setZoomSensitivity(0);
        chaseCam.setDragToRotate(false);
        chaseCam.setMinVerticalRotation(-(FastMath.HALF_PI+ 0.7f));
        chaseCam.setMaxVerticalRotation(FastMath.HALF_PI*0.8f);
        chaseCam.setInvertVerticalAxis(true);

        // Init the mothership and make the sensor field only collide with the player
        megaDrone = new MegaDrone("megaDrone", mat_blue, FemaleChar,
                assetManager.loadModel("Models/Mothership/Mothership.j3o"));
        // Only allow the ghost control to collide with the player
        megaDrone.getGhostControl().setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        megaDrone.getGhostControl().setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_02);
        
        // Init the simple slide enemy and hill enemy
        hillEnemy = new HillEnemy("hillEnemy", mat_blue);
        slideEnemy = new SlideEnemy("slideEnemy", mat_green, FemaleChar);
        // Only allow the ghost control to collide with the player
        slideEnemy.getGhostControl().setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        slideEnemy.getGhostControl().setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_02); 
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

            Vector3f modelForwardDir = playerNode.getWorldRotation().mult(Vector3f.UNIT_Z);
            Vector3f modelLeftDir = playerNode.getWorldRotation().mult(Vector3f.UNIT_X);

            // WalkDirection is global!
            // You *can* make your character fly with this.
            walkDirection.set(0, 0, 0);
            if (left) {
                walkDirection.addLocal(modelLeftDir.mult(15));
            } else if (right) {
                walkDirection.addLocal(modelLeftDir.negate().multLocal(15));
            }
            if (up) {
                walkDirection.addLocal(modelForwardDir.mult(15));
            } else if (down) {
                walkDirection.addLocal(modelForwardDir.negate().multLocal(15));
            }
            player.setWalkDirection(walkDirection);
            
            // ViewDirection is local to characters physics system!
            // The final world rotation depends on the gravity and on the state of
            // setApplyPhysicsLocal()
            player.setViewDirection(player.getViewDirection().interpolate(
                    cam.getDirection(), 0.05f));

            // Attempt to have slideEnemy shoot
            if(rootNode.getChild("slideEnemy") != null) {
                slideEnemyBullet = slideEnemy.shoot(mat_bullet);
                if(slideEnemyBullet != null) {
                    // Create and move the bullet
                    Geometry bulletg = new Geometry("bullet1", bullet);
                    bulletg.setMaterial(mat_bullet);
                    bulletg.setLocalTranslation(slideEnemy.getEnemyControl()
                            .getPhysicsLocation().add(slideEnemyBullet.mult(12f)));
                    bulletg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                    bulletg.addControl(new RigidBodyControl(bulletCollisionShape, 5f));
                    bulletg.getControl(RigidBodyControl.class).setCcdMotionThreshold(0.01f);
                    bulletg.getControl(RigidBodyControl.class).setLinearVelocity(
                            slideEnemyBullet.mult(50f));
                    rootNode.attachChild(bulletg);
                    getPhysicsSpace().add(bulletg);
                }
            }
            
            while(!respawnQ.isEmpty()) {
                queueString = respawnQ.poll();
                if(rootNode.getChild("slideEnemy") == null && 
                        queueString.equals("slideEnemy")) {
                    slideEnemy = new SlideEnemy("slideEnemy", mat_green, FemaleChar);
                    rootNode.attachChild(slideEnemy.getGeo());
                    bulletAppState.getPhysicsSpace().add(slideEnemy.getEnemyControl());
                    bulletAppState.getPhysicsSpace().add(slideEnemy.getGhostControl());
                    resetSlideEnemy();
                } else if(rootNode.getChild("hillEnemy") == null && 
                        queueString.equals("hillEnemy")) {
                    hillEnemy = new HillEnemy("hillEnemy", mat_blue);
                    rootNode.attachChild(hillEnemy.getGeo());
                    bulletAppState.getPhysicsSpace().add(hillEnemy.getEnemyControl());
                    resetHillEnemy();
                }
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

            // Attach everything to the scene model
            rootNode.attachChild(sceneModel);
            rootNode.addLight(al);
            rootNode.addLight(dl);
            rootNode.attachChild(shotSound);
            rootNode.attachChild(boomSound);
            rootNode.attachChild(playerNode);
            rootNode.attachChild(megaDrone.getSpatial());
            rootNode.attachChild(slideEnemy.getGeo());
            rootNode.attachChild(hillEnemy.getGeo());
            
            // Add everything to the physics space
            bulletAppState.getPhysicsSpace().addAll(sceneModel);
            bulletAppState.getPhysicsSpace().add(player);
            bulletAppState.getPhysicsSpace().addAll(FemaleChar);
            bulletAppState.getPhysicsSpace().add(megaDrone.getEnemyControl());
            bulletAppState.getPhysicsSpace().add(megaDrone.getGhostControl());
            bulletAppState.getPhysicsSpace().add(slideEnemy.getEnemyControl());
            bulletAppState.getPhysicsSpace().add(slideEnemy.getGhostControl());
            bulletAppState.getPhysicsSpace().add(hillEnemy.getEnemyControl());
            bulletAppState.getPhysicsSpace().addCollisionListener(this);
            
            // Establish player controls
            enableControls(true);
            enableCrossHairs(true);
            
            // Enable the physics app state
            bulletAppState.setEnabled(true);
        }
        else {
            // Remove playser input controls
            enableControls(false);
            enableCrossHairs(false);
            bulletAppState.setEnabled(false);
            left = false; right = false; up = false; down = false;
            rootNode.detachAllChildren();
            rootNode.removeLight(al);
            rootNode.removeLight(dl);
            megaDrone.getMinions().clear();
        }
    }

    /*
     * This method handles what to do when a collision occurs. It turns a ball
     * green if it has not been hit yet.
     */
    public void collision(PhysicsCollisionEvent e) {
        // Check for collision with any microDrone and a bullet
        NodeA = e.getNodeA();
        NodeB = e.getNodeB();
        NameA = NodeA.getName();
        NameB = NodeB.getName();
        
        // Check for a collision with a bullet
        if(NameA.equals("bullet")) {
            // Remove the bullet
            bulletAppState.getPhysicsSpace().remove(NodeA);
            NodeA.removeFromParent();
            
            if(NameB.equals("microDrone")) {
                shockwave.explode(NodeB.getWorldTranslation());
                bulletAppState.getPhysicsSpace().remove(NodeB);
                NodeB.removeFromParent();
                megaDrone.removeMinion(NodeB);
                boomSound.setLocalTranslation(NodeB.getWorldTranslation());
                boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NameB, MicroDrone.points);
            } else if(NameB.equals("megaDrone")) {
                 megaDrone.hit(); //decrements health but only sets flag when "dead"
                 
                if(megaDrone.gethealth() > 0) { //hit but not dead
                    megaDroneHitSound.setLocalTranslation(megaDrone.getSpatial().getWorldTranslation());
                    megaDroneHitSound.playInstance();
                    stateManager.getState(GuiAppState.class).showHitObject(NameB, MegaDrone.hitPoint);
                } else { //hit and dead
                    boomSound.setLocalTranslation(megaDrone.getSpatial().getWorldTranslation());
                    boomSound.playInstance();
                    stateManager.getState(GuiAppState.class).showHitObject(NameB, MegaDrone.killPoint);
                }
            } else if(NameB.equals("slideEnemy")) {
                slideEnemy.hit();
                shockwave.explode(NodeB.getWorldTranslation());
                bulletAppState.getPhysicsSpace().remove(slideEnemy.getEnemyControl());
                bulletAppState.getPhysicsSpace().remove(slideEnemy.getGhostControl());
                NodeB.removeFromParent();
                boomSound.setLocalTranslation(NodeB.getWorldTranslation());
                boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NameB, SlideEnemy.points);
                respawnQ.add(NameB);
            } else if(NameB.equals("hillEnemy")) {
                hillEnemy.hit();
                shockwave.explode(NodeB.getWorldTranslation());
                bulletAppState.getPhysicsSpace().remove(NodeB);
                NodeB.removeFromParent();
                boomSound.setLocalTranslation(NodeB.getWorldTranslation());
                boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NameB, HillEnemy.points);
                respawnQ.add(NameB);
            }
        } else if(NameB.equals("bullet")) {
            // Remove the bullet
            bulletAppState.getPhysicsSpace().remove(NodeB);
            NodeB.removeFromParent();
            
            if(NameA.equals("microDrone")) {
                shockwave.explode(NodeA.getWorldTranslation());
                bulletAppState.getPhysicsSpace().remove(NodeA);
                NodeA.removeFromParent();
                megaDrone.removeMinion(NodeA);
                boomSound.setLocalTranslation(NodeA.getWorldTranslation());
                boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NameA, MicroDrone.points);
            } else if(NameA.equals("megaDrone")) {
                megaDrone.hit(); //decrements health but only sets flag when "dead"
                
                if(megaDrone.gethealth() > 0) { //hit but not dead
                    megaDroneHitSound.setLocalTranslation(megaDrone.getSpatial().getWorldTranslation());
                    megaDroneHitSound.playInstance();
                    stateManager.getState(GuiAppState.class).showHitObject(NameA, MegaDrone.hitPoint);
                } else { //hit and dead
                    boomSound.setLocalTranslation(megaDrone.getSpatial().getWorldTranslation());
                    boomSound.playInstance();
                    stateManager.getState(GuiAppState.class).showHitObject(NameA, MegaDrone.killPoint);
                }
            } else if(NameA.equals("slideEnemy")) {
                shockwave.explode(NodeA.getWorldTranslation());
                bulletAppState.getPhysicsSpace().remove(slideEnemy.getEnemyControl());
                bulletAppState.getPhysicsSpace().remove(slideEnemy.getGhostControl());
                NodeA.removeFromParent();
                boomSound.setLocalTranslation(NodeA.getWorldTranslation());
                boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NameA, SlideEnemy.points);
                respawnQ.add(NameA);
            } else if(NameA.equals("hillEnemy")) {
                hillEnemy.hit();
                shockwave.explode(NodeA.getWorldTranslation());
                bulletAppState.getPhysicsSpace().remove(NodeA);
                NodeA.removeFromParent();
                boomSound.setLocalTranslation(NodeB.getWorldTranslation());
                boomSound.playInstance();
                stateManager.getState(GuiAppState.class).showHitObject(NameA, HillEnemy.points);
                respawnQ.add(NameA);
            }
        }
        
        // Check for infiltration of the mother ship's airspace
        if(megaDrone.getGhostControl().getOverlappingObjects().contains(FemaleChar.getControl(GhostControl.class))) {
            // Spawn a new drone if there is less than 4 in scene already
            MicroDrone m = megaDrone.createMicroDrone(mat_red); // returns null if should not add new drone
            if(m != null) {
                // PLace the new drone between the player and the megaDrone
                Vector3f v = FemaleChar.getWorldTranslation().
                        subtract(megaDrone.getSpatial().getWorldTranslation());
                v.setY(0).normalizeLocal();
                v = v.mult(20f);
                v = megaDrone.getSpatial().getWorldTranslation().add(v);
                m.getEnemyControl().setPhysicsLocation(v);
                                
                // Attach the new drone to the scene and physics space
                rootNode.attachChild(m.getGeo());
                bulletAppState.getPhysicsSpace().add(m.getEnemyControl()); 
            }
        }
        
        // Remove bullet1 physics control from the world
        if(NameA.equals("bullet1")) {
            bulletAppState.getPhysicsSpace().remove(NodeA);
            NodeA.removeFromParent();
        } else if(NameB.equals("bullet1")) {
            bulletAppState.getPhysicsSpace().remove(NodeB);
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
        mat_green = new Material(
                assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_green.setBoolean("UseMaterialColors",true);
        mat_green.setColor("Diffuse", ColorRGBA.Green);
        mat_green.setColor("Ambient", new ColorRGBA(0.0f, 0.5f, 0.0f, 1.0f));
        
        // For player 1
        mat_red = new Material(
                assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_red.setBoolean("UseMaterialColors",true);
        mat_red.setColor("Diffuse", ColorRGBA.Red);
        mat_red.setColor("Ambient", new ColorRGBA(0.5f, 0.0f, 0.0f, 1.0f));
        
        // For player 2
        mat_blue = new Material(
                assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_blue.setBoolean("UseMaterialColors",true);
        mat_blue.setColor("Diffuse", ColorRGBA.Blue);
        mat_blue.setColor("Ambient", new ColorRGBA(0.0f, 0.0f, 0.5f, 1.0f));
        
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
            // Increment counter of rounds fired for the GUI HUD
            stateManager.getState(GuiAppState.class).addRound();
            // Play the gun firing sound
            shotSound.setLocalTranslation(FemaleChar.getWorldTranslation());
            shotSound.playInstance();
            
            // Create and move the bullet
            Geometry bulletg = new Geometry("bullet", bullet);
            bulletg.setMaterial(mat_bullet);
            bulletg.setName("bullet");
            //don't just use cam.getLocation. bullets will not work when back is on a wall
            bulletg.setLocalTranslation(FemaleChar.getWorldTranslation().add(0, 6f, 0));
            bulletg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            bulletg.addControl(new RigidBodyControl(bulletCollisionShape, 0.1f));
            bulletg.getControl(RigidBodyControl.class).setCcdMotionThreshold(0.01f);
            bulletg.getControl(RigidBodyControl.class).setLinearVelocity(cam.getDirection().mult(500));
            rootNode.attachChild(bulletg);
            getPhysicsSpace().add(bulletg);
        } 
        if(binding.equals("booster")) {
            boost = cam.getDirection().clone().setY(0).mult(14f);
        }
    }

    /*
     * This method sets up the custom key bindings for the player control, or
     * removes them.
     */
    private void enableControls(boolean enable) {
        if(enable) {
            inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
            inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
            inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
            inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
            inputManager.addMapping("booster", new KeyTrigger(KeyInput.KEY_E));
            inputManager.addListener(this, "Left");
            inputManager.addListener(this, "Right");
            inputManager.addListener(this, "Up");
            inputManager.addListener(this, "Down");
            inputManager.addListener(this, "Jump");
            inputManager.addListener(this, "shoot");
            inputManager.addListener(this, "booster");
        } else {
            inputManager.deleteMapping("Left");
            inputManager.deleteMapping("Right");
            inputManager.deleteMapping("Up");
            inputManager.deleteMapping("Down");
            inputManager.deleteMapping("Jump");
            inputManager.deleteMapping("shoot");
            inputManager.deleteMapping("booster");
            inputManager.removeListener(this);
        }
    }

    /*
     * This method resets everything necessary to play a new round of the game
     * for one player.
     */
    public void resetLevel() { 
        // Reset the total points counter back to nil
        totalPoints = 0;
        
        // Clear all movement from the physics space
        bulletAppState.getPhysicsSpace().clearForces();
        bulletAppState.getPhysicsSpace().applyGravity(); //reset effects of grav
        
        // Reset the mothership back to start location
        resetMegaDrone();
        
        // Reset the slideEnemy
        resetSlideEnemy();
        
        // Reset the hillEnemy
        resetHillEnemy();
        
        // Reset the player back to start location and stop all movements
        resetPlayer(); 
    }
    
    /*
     * This method clears the mothership's drones, hit points, and resets its
     * location.
     */
    public void resetHillEnemy() {
        // Reset the hit flag
        hillEnemy.unhit();
        
        // Put it back into the proper starting location
        hillEnemy.getEnemyControl().setPhysicsLocation(new Vector3f(-50f, 32f, -33f));
        hillEnemy.getEnemyControl().setLinearVelocity(new Vector3f(0f, -9.81f, 0f));
    }
    
    /*
     * This method clears the mothership's drones, hit points, and resets its
     * location.
     */
    public void resetSlideEnemy() {
        // Reset the hit flag
        slideEnemy.unhit();
        
        // Put it back into the proper starting location
        slideEnemy.getGeo().setLocalTranslation(new Vector3f(0f, 15f, 0f));
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
            bulletAppState.getPhysicsSpace().remove(m.getEnemyControl());
            m.getGeo().removeFromParent();  
        }
        
        // Get rid of the minions from their actual mothership (megaDrone)
        megaDrone.clearMinions();
        
        // Put it back into the proper starting location
        megaDrone.getEnemyControl().setPhysicsLocation(new Vector3f(65f, 35f, -65f));
    }
    
    /*
     * This method places the character at the starting location.
     */
    public void resetPlayer() {
        // Put the player back to their proper start location
        playerNode.setLocalTranslation(new Vector3f(-95f, 30f, 95f));
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
    private void enableCrossHairs(boolean enable) {
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