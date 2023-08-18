package net.devscape.woolwars.managers.abilities;

import lombok.Getter;
import lombok.Setter;
import net.devscape.woolwars.managers.abilities.impl.EskimoAbility;
import net.devscape.woolwars.managers.abilities.impl.ShurikenAbility;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AbilityManager {

    private List<Ability> abilities = new ArrayList<Ability>();

    public AbilityManager() {
        this.abilities.add(new ShurikenAbility());
        this.abilities.add(new EskimoAbility());
    }

    public Ability getAbility(String string) {
        for (Ability ability : this.abilities) {
            if (!ability.getName().equalsIgnoreCase(string)) {
                continue;
            }

            return ability;
        }

        return null;
    }
}
