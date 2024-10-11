package xyz.tavenservices.spigot.knockback;

import lombok.Getter;
import lombok.Setter;
import xyz.tavenservices.spigot.config.KnockbackConfig;
import xyz.tavenservices.spigot.event.sound.KnockbackProfile;
import xyz.tavenservices.spigot.event.sound.KnockbackType;

@Setter
@Getter
public class CraftKnockbackProfile implements KnockbackProfile {

    private String name;
    private final String saveProfilePath;

    private double horizontal = 0.9055D;
    private double vertical = 0.25635D;
    private double rangeFactor = 0.025D;
    private double maxRangeReduction = 1.2D;
    private double startRangeReduction = 3.0D;
    private double friction = 2.0D;

    public CraftKnockbackProfile(String name) {
        this.name = name;
        this.saveProfilePath = "knockback.profiles." + this.name;
    }

    @Override
    public void save() {
        save(false);
    }

    private void set(String savePath, Object value) {
        KnockbackConfig.set(saveProfilePath + savePath, value);
    }

    @Override
    public void save(boolean projectiles) {

        set(".horizontal", this.horizontal);
        set(".vertical", this.vertical);
        set(".range-factor", this.rangeFactor);
        set(".max-range-reduction", this.maxRangeReduction);
        set(".start-range-reduction", this.startRangeReduction);
        set(".friction", this.friction);
        KnockbackConfig.save();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
