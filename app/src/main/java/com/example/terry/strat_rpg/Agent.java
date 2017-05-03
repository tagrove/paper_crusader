package com.example.terry.strat_rpg;


import java.io.Serializable;

/**
 * Object class for Monsters
 *
 */
public abstract class Agent implements Serializable {

    private String agentName;
    private int maxHealth;
    private int currentHealth;
    private int level;
    private int strength;
    private int dexterity;
    private int dodgeRate;
    private int armor;
    private int lifeSteal;
    private float experienceToLevel;
    private int criticalRate;
    private float attackSpeed;
    private float timeUntilAttack;

    public Agent() {

        this.agentName = "noName";
        this.maxHealth = 10;
        this.currentHealth = 10;
        this.level = 1;
        this.strength = 5;
        this.dexterity = 5;
        this.lifeSteal = 0;
        this.dodgeRate = 10;
        this.armor = 3;
        this.experienceToLevel = (level*2) + 10;
        this.criticalRate = 10;
        this.attackSpeed = 2;
        this.timeUntilAttack = attackSpeed * 1000;
    }

    public Agent(String agentName, int maxHealth, int currentHealth, int level, int strength, int dexterity,
                 int dodgeRate, int armor, int lifeSteal, float experienceToLevel, int criticalRate,
                 float attackSpeed) {
        this.agentName = agentName;
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
        this.level = level;
        this.strength = strength;
        this.dexterity = dexterity;
        this.dodgeRate = dodgeRate;
        this.armor = armor;
        this.lifeSteal = lifeSteal;
        this.experienceToLevel = experienceToLevel;
        this.criticalRate = criticalRate;
        this.attackSpeed = attackSpeed;
        this.timeUntilAttack = attackSpeed*1000;
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

    public void setLifeSteal(int lifeSteal) {
        this.lifeSteal = lifeSteal;
    }

    public float getExperienceToLevel() {
        return experienceToLevel;
    }

    public void setExperienceToLevel(float experienceToLevel) {
        this.experienceToLevel = experienceToLevel;
    }

    public int getCriticalRate() {
        return criticalRate;
    }

    public void setCriticalRate(int criticalRate) {
        this.criticalRate = criticalRate;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public float getTimeUntilAttack() {
        return timeUntilAttack;
    }

    public void setTimeUntilAttack(float timeUntilAttack) {
        this.timeUntilAttack = timeUntilAttack;
    }

    public int getDodgeRate() {
        return dodgeRate;
    }

    public void setDodgeRate(int dodgeRate) {
        this.dodgeRate = dodgeRate;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
}
