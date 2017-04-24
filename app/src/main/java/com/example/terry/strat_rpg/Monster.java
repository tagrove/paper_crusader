package com.example.terry.strat_rpg;

/**
 * Created by Terry on 4/23/2017.
 */


public class Monster extends Agent {

    public Monster() {
        super();
    }

    public Monster(String agentName, int maxHealth, int currentHealth, int level, int strength, int dexterity,
                   int dodgeRate, int armor, int lifeSteal, float experienceToLevel, int criticalRate, float attackSpeed,
                   float timeUntilAttack) {
        super(agentName, maxHealth, currentHealth, level, strength, dexterity, dodgeRate, armor,
                lifeSteal, experienceToLevel, criticalRate, attackSpeed, timeUntilAttack);
    }



}
