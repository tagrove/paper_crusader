package com.example.terry.strat_rpg;

/**
 * Created by Terry on 3/30/2017.
 */

public class Player {
    private int maxHealth;
    private int currentHealth;
    private int level;
    private int strength;
    private int dexterity;
    private int lifeSteal;
    private float experienceToLevel;
    private float criticalRate;
    private float attackSpeed;

    public Player(){
        maxHealth = 10;
        currentHealth = 10;
        level = 1;
        strength = 1;
        dexterity = 1;
        lifeSteal = 0;
        criticalRate = .05f;
        attackSpeed = 1.0f;
    }

    public Player(int maxHealth, int currentHealth, int level, int strength, int dexterity, int lifeSteal,
                  int experienceToLevel, float criticalRate, float attackSpeed){
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
        this.level = level;
        this.strength = strength;
        this.dexterity = dexterity;
        this.lifeSteal = lifeSteal;
        this.experienceToLevel = experienceToLevel;
        this.criticalRate = criticalRate;
        this.attackSpeed = attackSpeed;

    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getLifeSteal() {
        return lifeSteal;
    }

    public void setLifeSteal(int lifesteal) {
        this.lifeSteal = lifesteal;
    }

    public float getCriticalRate() {
        return criticalRate;
    }

    public void setCriticalRate(float criticalRate) {
        this.criticalRate = criticalRate;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public float getExperienceToLevel(){
        return experienceToLevel;
    }

    public void setExperienceToLevel(float experienceToLevel){
        this.experienceToLevel = experienceToLevel;
    }


    public void levelUp(){
        this.experienceToLevel = (this.experienceToLevel * 1.3f);

        System.out.println("Made it into levelUp()!  Exp to level = " + experienceToLevel);
    }

    public void printPlayerAttributes(){
        System.out.println("Player attributes: \n Strength = " + strength
        + "\nDexterity = " + dexterity + "\n");

    }



}
