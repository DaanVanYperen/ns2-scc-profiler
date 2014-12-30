package net.mostlyoriginal.game.manager;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.utils.EntityUtil;

/**
 * @todo Split game logic and library logic.
 * @author Daan van Yperen
 */
@Wire
public class AssetSystem extends net.mostlyoriginal.api.manager.AbstractAssetSystem {

    private TagManager tagManager;

    ComponentMapper<Pos> pm;

    public AssetSystem() {
        super();

        add("resource-node-marine", 225+16,0, 16,16,1);
        add("techpoint-marine", 225,0, 16,16,1);
        add("resource-node-alien", 225+16,16, 16,16,1);
        add("techpoint-alien", 225,16, 16,16,1);

        add("resource-node", 136,0, 16,16,1);
        add("techpoint", 136-16,0, 16,16,1);
        add("duct", 136-16,16, 16,16,1);
        add("wall", 136, 16, 16, 16,1);

        add("refresh", 136-16,32, 16,16,1);
        add("save", 136,32, 16,16,1);
        add("load", 136+16,32, 16,16,1);

        add("layer-1", 136-16,48, 16,16,1);
        add("layer-2", 136,48, 16,16,1);
        add("layer-3", 136+16,48, 16,16,1);
        add("layer-4", 136+32,48, 16,16,1);
        add("layer-5", 136+48+16,48, 16,16,1);
        add("layer-6", 136+48,48, 16,16,1);
        add("layer-7", 136+48+32,48, 16,16,1);

        add("agent-marine", 136+16, 0, 16,16,1);
        add("agent-alien",  136+16,16, 16,16,1);

        add("cursor", 170, 1, 11,12, 1);

        loadSounds(new String[]{
        });
    }


    public void playSfx(String name, Entity origin) {
        if (sfxVolume > 0 )
        {
            Entity player = tagManager.getEntity("player");
            float distance = EntityUtil.distance(origin, player);

            float volume = sfxVolume - (distance / 2000f);
            if ( volume > 0.01f )
            {
                float balanceX = pm.has(origin) && pm.has(player) ? MathUtils.clamp((pm.get(origin).x - pm.get(player).x) / 100f, -1f, 1f) : 0;
                Sound sfx = getSfx(name);
                sfx.stop();
                sfx.play(volume, MathUtils.random(1f, 1.04f), balanceX);
            }
        }
    }

}
