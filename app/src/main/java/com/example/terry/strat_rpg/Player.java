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

    public Player() {
        super();
    }

    public Player(String agentName, int maxHealth, int currentHealth, int level, int strength, int dexterity, int dodgeRate, int armor,
                  int lifeSteal, float experienceToLevel, int criticalRate, float attackSpeed, float timeUntilAttack) {

                  super(agentName, maxHealth, currentHealth, level, strength, dexterity, dodgeRate, armor, lifeSteal,
                  experienceToLevel, criticalRate, attackSpeed, timeUntilAttack);
    }

}
