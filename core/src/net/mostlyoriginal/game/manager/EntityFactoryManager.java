package net.mostlyoriginal.game.manager;

import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.maps.MapProperties;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.game.component.*;
import net.mostlyoriginal.game.component.ui.*;

import java.util.EnumSet;

/**
 * Game specific entity factory.
 *
 * @todo transform this into a manager.
 * @author Daan van Yperen
 */
@Wire
public class EntityFactoryManager extends Manager {

    private Archetype resourceNode;
    private Archetype techpoint;
    private Archetype duct;

    private Archetype marine;
    private Archetype alien;

    protected ComponentMapper<Pos> mPos;
    protected ComponentMapper<Anim> mAnim;
    protected ComponentMapper<Bounds> mBounds;
    protected ComponentMapper<Blockade> mBlockade;
    protected ComponentMapper<Draggable> mDraggable;
    protected ComponentMapper<Renderable> mRenderable;
    protected ComponentMapper<TeamMember> mTeamMember;


    @Override
    protected void initialize() {
        super.initialize();

        resourceNode = new ArchetypeBuilder().add(
                Pos.class,
                Anim.class,
                Bounds.class,
                Renderable.class,
                TeamAsset.class,
                Deletable.class,
                Draggable.class,
                Clickable.class,
                Routable.class
        ).build(world);
        marine = new ArchetypeBuilder().add(
                Pos.class,
                Anim.class,
                Bounds.class,
                Traveler.class,
                TeamMember.class,
                Transient.class,
                Renderable.class
        ).build(world);
        alien = new ArchetypeBuilder().add(
                Pos.class,
                Anim.class,
                Bounds.class,
                Traveler.class,
                TeamMember.class,
                Transient.class,
                Renderable.class
        ).build(world);
        techpoint = new ArchetypeBuilder().add(
                Pos.class,
                Anim.class,
                Bounds.class,
                Draggable.class,
                Renderable.class,
                TeamAsset.class,
                Deletable.class,
                Clickable.class,
                Routable.class
        ).build(world);
        duct = new ArchetypeBuilder().add(
                Pos.class,
                Anim.class,
                Bounds.class,
                Renderable.class,
                Draggable.class,
                Blockade.class,
                Deletable.class,
                Clickable.class,
                Bounds.class
        ).build(world);

        createInstancingButton("resource-node", "resourceNode", 50);
        createInstancingButton("techpoint", "techpoint", 50 + 40*1);
        createInstancingButton("duct", "duct", 50 + 40*2);
        createInstancingButton("wall", "wall", 50 + 40 * 3);
    }

    private void createInstancingButton(String animId, final String entityId, int x) {
        createBasicButton(animId, x, new ButtonListener() {
            @Override
            public void run() {
                Entity entity = createEntity(entityId, 0, 0, null);

                Draggable draggable = mDraggable.get(entity);
                draggable.dragging = true;
            }

            @Override
            public boolean enabled() {
                return true;
            }
        });
    }

    public void createBasicButton(String animId, int x, ButtonListener buttonListener) {
        Anim anim = new Anim(animId);
        anim.scale = 2;
        new EntityBuilder(world)
                .with(
                        new Pos(x,50),
                        new Bounds(32,32),
                        anim,
                        new Clickable(),
                        new Renderable(800),
                        new Button(animId,animId, animId, buttonListener)).build();
    }

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
            case "duct":
                e = createDuct();
                break;
            case "wall":
                e = createWall();
                break;
            case "marine":
                e = createMarine();
                break;
            case "alien":
                e = createAlien();
                break;
        }

        if ( e != null && mPos.has(e))
        {
            Pos pos = mPos.get(e);
            pos.x = cx;
            pos.y = cy;
        }

        return e;
    }

    private Entity createMarine() {
        Entity marine = world.createEntity(this.marine);

        Anim anim = mAnim.get(marine);
        anim.id = "agent-marine";

        Renderable renderable = mRenderable.get(marine);
        renderable.layer = 999;

        TeamMember teamMember = mTeamMember.get(marine);
        teamMember.team = Team.MARINE;

        Bounds bounds = mBounds.get(marine);
        bounds.maxx = 16;
        bounds.maxy = 16;

        return marine;
    }

    private Entity createAlien() {
        Entity alien = world.createEntity(this.alien);

        Anim anim = mAnim.get(alien);
        anim.id = "agent-alien";

        Renderable renderable = mRenderable.get(alien);
        renderable.layer = 1000;

        TeamMember teamMember = mTeamMember.get(alien);
        teamMember.team = Team.ALIEN;

        Bounds bounds = mBounds.get(alien);
        bounds.maxx = 16;
        bounds.maxy = 16;

        return alien;
    }

    private Entity createResourceNode() {
        Entity node = world.createEntity(this.resourceNode);

        Anim anim = mAnim.get(node);
        anim.id = "resource-node";

        Bounds bounds = mBounds.get(node);
        bounds.maxx = 16;
        bounds.maxy = 16;

        return node;
    }

    private Entity createDuct() {
        Entity node = world.createEntity(this.duct);

        Anim anim = mAnim.get(node);
        anim.id = "duct";

        Bounds bounds = mBounds.get(node);
        bounds.maxx=16;
        bounds.maxy=16;

        // ducts can be passed by aliens.
        Blockade blockade = mBlockade.get(node);
        blockade.passableBy = EnumSet.of(Team.ALIEN);

        return node;
    }

    private Entity createWall() {
        Entity node = world.createEntity(this.duct);

        Anim anim = mAnim.get(node);
        anim.id = "wall";

        Bounds bounds = mBounds.get(node);
        bounds.maxx=16;
        bounds.maxy=16;


        // walls block both teams (null).
        Blockade blockade = mBlockade.get(node);
        blockade.passableBy = EnumSet.noneOf(Team.class);

        return node;
    }

    private Entity createTechpoint() {
        Entity techpoint = world.createEntity(this.techpoint);

        Anim anim = mAnim.get(techpoint);
        anim.id = "techpoint";

        Bounds bounds = mBounds.get(techpoint);
        bounds.maxx = 16;
        bounds.maxy = 16;

        return techpoint;
    }
}
