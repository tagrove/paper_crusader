package com.example.terry.strat_rpg;

/**
 * Player is an object class representing a player.  This may be changed in the future to be
 * a parent class which may then be converted to the player or monster.
 *
 * The game loop from Game will deal with this class in order to calculate experience, attackspeed,
 * strength, health, and other stats.
 *
 * TODO - Need to implement every method necessary for the game to work still.
 */
public class Player extends Agent{

    private int levelStep;
    private int healthGainPerLevel;

    public Player() {
        super();
        levelStep = 4;
        healthGainPerLevel = 2;
    }

    public Player(String agentName, int maxHealth, int currentHealth, int healthGainPerLevel, int level, int expLevelStep, int strength, int dexterity, int dodgeRate, int armor,
                  int lifeSteal, int experienceToLevel, int criticalRate, float attackSpeed) {

                  super(agentName, maxHealth, currentHealth, level, strength, dexterity, dodgeRate, armor, lifeSteal,
                  experienceToLevel, criticalRate, attackSpeed);
        levelStep = expLevelStep;
        this.healthGainPerLevel = healthGainPerLevel;
    }

    public void setLevelStep(int levelStep){
        this.levelStep = levelStep;
    }

    public int getLevelStep(){
        return levelStep;
    }

    public void levelUp(){
        levelStep = (int)Math.ceil((levelStep*1.1) + super.getLevel());
        System.out.println("Level step is now = " + levelStep);
        setExperienceToLevel(levelStep);
        super.setStrength(super.getStrength() + 1);
        super.setDexterity(super.getDexterity() + 1);
        super.setCurrentHealth(super.getCurrentHealth() + healthGainPerLevel);
        super.setLevel(super.getLevel() + 1);
    }

}
