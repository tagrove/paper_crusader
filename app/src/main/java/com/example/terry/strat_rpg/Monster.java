package com.example.terry.strat_rpg;

/**
 * This class represents the monster.
 */
public class Monster extends Agent {

    private int expValue, goldValue;
    public Monster() {
        super();
        this.expValue = 0;
        this.goldValue = 0;
    }

    public Monster(String agentName, int maxHealth, int currentHealth, int level, int strength, int dexterity,
                   int dodgeRate, int armor, int lifeSteal, float experienceToLevel, int criticalRate, float attackSpeed,
                   float timeUntilAttack, int expValue, int goldValue) {
        super(agentName, maxHealth, currentHealth, level, strength, dexterity, dodgeRate, armor,
                lifeSteal, experienceToLevel, criticalRate, attackSpeed, timeUntilAttack);
        this.expValue = expValue;
        this.goldValue = goldValue;
    }

    public void setExpValue(int expValue){
        this.expValue = expValue;
    }

    public void setGoldValue(int goldValue){
        this.goldValue = goldValue;
    }

    public int getExpValue(){
        return expValue;
    }

    public int getGoldValue(){
        return goldValue;
    }



}
