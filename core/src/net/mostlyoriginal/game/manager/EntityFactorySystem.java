package net.mostlyoriginal.game.manager;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.maps.MapProperties;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.manager.AbstractEntityFactorySystem;
import net.mostlyoriginal.game.component.TeamAsset;

/**
 * Game specific entity factory.
 *
 * @todo transform this into a manager.
 * @author Daan van Yperen
 */
@Wire
public class EntityFactorySystem extends AbstractEntityFactorySystem {

    private Archetype resourceNode;
    private Archetype techpoint;

    protected ComponentMapper<Pos> mPos;
    protected ComponentMapper<Anim> mAnim;

    @Override
    protected void initialize() {
        super.initialize();

        resourceNode = new ArchetypeBuilder().add(
                Pos.class,
                Anim.class,
                Renderable.class,
                TeamAsset.class
        ).build(world);
        techpoint = new ArchetypeBuilder().add(
                Pos.class,
                Anim.class,
                Renderable.class,
                TeamAsset.class
        ).build(world);
    }

    @Override
    public Entity createEntity(String entity, int cx, int cy, MapProperties properties) {

        Entity e = null;
        switch (entity)
        {
            case "resourceNode":
                e = createResourceNode();
                break;
            case "techpoint":
                e = createTechpoint();
                break;
        }

        if ( e != null && mPos.has(e))
        {
            Pos pos = mPos.get(e);
            pos.x = cx;
            pos.y = cy;
        }

        return null;
    }

    private Entity createResourceNode() {
        Entity node = world.createEntity(this.resourceNode);

        Anim anim = mAnim.get(node);
        anim.id = "resource-node";

        return node;
    }

    private Entity createTechpoint() {
        Entity techpoint = world.createEntity(this.techpoint);

        Anim anim = mAnim.get(techpoint);
        anim.id = "techpoint";

        return techpoint;
    }
}
