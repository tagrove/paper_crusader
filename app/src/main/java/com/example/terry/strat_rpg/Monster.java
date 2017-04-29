package com.example.terry.strat_rpg;

/**
 * This class represents the monster.
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
