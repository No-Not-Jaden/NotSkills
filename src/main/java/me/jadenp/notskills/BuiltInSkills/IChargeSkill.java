package me.jadenp.notskills.BuiltInSkills;

public interface IChargeSkill {
    /**
     * @return ticks since the start of charging
     */
    int getCharge();

    /**
     * @return total ticks required for full charge
     */
    int getRequiredCharge();

    /**
     * Sets the charge time.
     * This is called at the activation of the skill.
     */
    void startCharge();

    /**
     * Reset the charge time.
     * Realistically, this shouldn't get called, instead the skill should be canceled.
     */
    void cancelCharge();

    /**
     * @return true if the skill is fully charged
     */
    boolean isCharged();

    /**
     * Optional method to have an action occur while the skill is charging
     */
    void chargeAction();
}
