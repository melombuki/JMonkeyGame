/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hemen.CMSC325.FinalProject;

/**
 * This class encapsulates a high score entry. It implements Comaparable and 
 * will result in items being sorted in reverse order.
 * @author Joshua P. Hemen
 */
public class HighScoreEntry implements Comparable<HighScoreEntry> {
    private final Integer score;
    private final String initials;
    
    public HighScoreEntry(Integer score, String initials) {
        this.score = score;
        this.initials = initials;
    }
    
    public Integer getScore() {return score;}
    
    public String getInitials() {return initials;}

    public int compareTo(HighScoreEntry o) {
        if(score > o.getScore()) {return -1;}
        else if (score == o.getScore()) {return 0;}
        else return 1;
    }
}
