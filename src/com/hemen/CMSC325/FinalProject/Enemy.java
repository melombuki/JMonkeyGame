/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

/**
 *
 * @author melombuki
 */
abstract class Enemy {
    private boolean isHit;
  
    public Enemy() {
        this.isHit = false;
    }
    
    public boolean isHit() {return isHit;}
    
    public void hit() {isHit = true;}
    
    public void unhit() {isHit = false;}
}
