package com.battleships.game.entity.components;

import com.badlogic.ashley.core.Component;
import static com.badlogic.gdx.math.MathUtils.random;

public class LootComponent implements Component {
    public enum LootType {
        HealthPack(0), CannonUpgrade(1), HealthUpgrade(2), SpeedUpgrade(3), ReloadUpgrade(4), BulletSpeedUpgrade(5);
        private final int value;
        LootType (int value) {
            this.value = value;
        }
    };
    public boolean isDead = false;
    public LootType lootType;
    public int id;

    public LootComponent() {
        this.lootType = randomEnum(LootType.class);
    }

    private static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
