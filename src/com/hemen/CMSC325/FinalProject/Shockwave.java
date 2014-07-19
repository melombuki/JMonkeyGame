package com.hemen.CMSC325.FinalProject;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.asset.AssetManager;

/**
* 
* @author dennisschneider
* Schneider D. (2011, June 19). HoneyPong. Retrieved from
* https://github.com/dschneider/HoneyPong/blob/master/src/pong/effects/Shockwave.java
* 
* Slightly modified by Joshua P. Hemen.
* 
* This class creates a shockwave specail effect by calling the explode() method.
*/
public class Shockwave {
    
    //private Node explosionEffect = new Node("shockwaveFX");
    private ParticleEmitter shockWave;
    
    private static final int COUNT_FACTOR = 1;
    private static final float COUNT_FACTOR_F = 1f;
    
    public Shockwave(Node rootNode, AssetManager assetManager) {
        shockWave = new ParticleEmitter("Shockwave", Type.Triangle, 1 * COUNT_FACTOR);

        //shockWave.setFaceNormal(Vector3f.UNIT_Z);
        shockWave.setFaceNormal(null);
        shockWave.setStartColor(new ColorRGBA(.48f, 0.17f, 0.01f, (float) (.8f / COUNT_FACTOR_F)));
        shockWave.setEndColor(new ColorRGBA(.48f, 0.17f, 0.01f, 0f));
        
        //Modified these two values to fit the circle
        shockWave.setStartSize(4f);
        shockWave.setEndSize(8f);
        
        shockWave.setParticlesPerSec(0);
        shockWave.setGravity(0, 0, 0);
        shockWave.setLowLife(0.5f);
        shockWave.setHighLife(0.5f);
        shockWave.setImagesX(1);
        shockWave.setImagesY(1);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        //Chaned from "m_Texture" to "Texture" on next line
        mat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/shockwave.png"));
        shockWave.setMaterial(mat);
        
        rootNode.attachChild(shockWave);
    }
    
    public void explode(float x, float y, float z) {
        shockWave.killAllParticles();
        shockWave.setLocalTranslation(x, y, z);
        shockWave.emitAllParticles();
    }

    //This method was added by Joshua P. Hemen
    public void explode(Vector3f v) {
        shockWave.killAllParticles();
        shockWave.setLocalTranslation(v);
        shockWave.emitAllParticles();
    } 
    
    //This method was added by Joshua P. Hemen
    public ParticleEmitter getShockWave() {
        return shockWave;
    }
}
