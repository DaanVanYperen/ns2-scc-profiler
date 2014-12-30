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
import net.mostlyoriginal.game.component.buildings.ResourceNode;
import net.mostlyoriginal.game.component.buildings.Techpoint;
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
    protected ComponentMapper<Persistable> mPersistable;
    protected ComponentMapper<RenderMask> mRenderMask;
    private ComponentMapper<Routable> mRoutable;


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
                Persistable.class,
                RenderMask.class,
                ResourceNode.class,
                TeamMember.class,
                Routable.class
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
                Persistable.class,
                RenderMask.class,
                Routable.class,
                TeamMember.class,
                Techpoint.class
        ).build(world);
        marine = new ArchetypeBuilder().add(
                Pos.class,
                Anim.class,
                Bounds.class,
                Traveler.class,
                TeamMember.class,
                Transient.class,
                RenderMask.class,
                Renderable.class
        ).build(world);
        alien = new ArchetypeBuilder().add(
                Pos.class,
                Anim.class,
                Bounds.class,
                Traveler.class,
                TeamMember.class,
                Transient.class,
                RenderMask.class,
                Renderable.class
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
                Persistable.class,
                RenderMask.class,
                Bounds.class
        ).build(world);

        createInstancingButton("resource-node", "resourceNode", 50);
        createInstancingButton("techpoint", "techpoint", 50 + 40*1);
        createInstancingButton("duct", "duct", 50 + 40*2);
        createInstancingButton("wall", "wall", 50 + 40 * 3);

        addMaskTitle(RenderMask.Mask.BASIC, "Map overview", "Drag and place techpoints, rts, blockades and ducts.", "", "Rightclick to delete. Middleclick to cycle team on techpoints.");
        addMaskTitle(RenderMask.Mask.RT_PRESSURE, "RT head start", "highlight team that reaches RT first.", "Number signifies seconds head start.","Assign at least two techpoints to different teams.");
        addMaskTitle(RenderMask.Mask.RT_SYMMETRY_ALIEN, "Alien - RT run times", "Travel time in seconds between techpoints and RTs for aliens.", "", "");
        addMaskTitle(RenderMask.Mask.RT_SYMMETRY_MARINE, "Marine - RT run times", "Travel time in seconds between techpoints and RTs for marines.", "", "");
        addMaskTitle(RenderMask.Mask.PATHFIND_ALIEN, "Alien - all routes", "Travel time in seconds for aliens.", "", "");
        addMaskTitle(RenderMask.Mask.PATHFIND_MARINE, "Marine - all routes", "Travel time in seconds for marines.", "", "");
    }

    private void createInstancingButton(String animId, final String entityId, int x) {
        Entity button = createBasicButton(animId, x, new ButtonListener() {
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

        // make only visible when rendering.
        button.edit().add(new RenderMask(RenderMask.Mask.BASIC));
    }

    private void addMaskTitle(RenderMask.Mask mask, String title, String subTitle1, String subTitle2, String help) {

        int y = 760;
        writeLabel(mask, title, 3, 50, y);
        if ( !subTitle1.isEmpty() ) writeLabel(mask, subTitle1, 2, 50, y -40);
        if ( !subTitle2.isEmpty() ) writeLabel(mask, subTitle2, 2, 50, y -60);
    }

    private void writeLabel(RenderMask.Mask mask, String title, int scale, int x, int y) {
        Label font = new Label(title);
        font.scale = scale;
        new EntityBuilder(world).with(
                new Renderable(1000),
                new net.mostlyoriginal.api.component.graphics.Color(0f,0f,0f,1f),
                new RenderMask(mask),
                new Pos(x, y),
                font)
                .build();
    }

    public Entity createBasicButton(String animId, int x, ButtonListener buttonListener) {
        Anim anim = new Anim(animId);
        anim.scale = 2;
        return new EntityBuilder(world)
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

        mRenderMask.get(marine).visible = EnumSet.allOf(RenderMask.Mask.class);

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

        mRenderMask.get(alien).visible = EnumSet.allOf(RenderMask.Mask.class);

        return alien;
    }

    private Entity createResourceNode() {
        Entity node = world.createEntity(this.resourceNode);

        Anim anim = mAnim.get(node);
        anim.id = "resource-node";

        Bounds bounds = mBounds.get(node);
        bounds.maxx = 16;
        bounds.maxy = 16;

        mPersistable.get(node).saveId = "resourceNode";

        mRenderMask.get(node).visible = EnumSet.allOf(RenderMask.Mask.class);

        TeamMember teamMember = mTeamMember.get(node);
        teamMember.artUnaligned = "resource-node";
        teamMember.art.put(Team.ALIEN, "resource-node-alien");
        teamMember.art.put(Team.MARINE, "resource-node-marine");

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

        mPersistable.get(node).saveId = "duct";

        mRenderMask.get(node).visible = EnumSet.of(RenderMask.Mask.BASIC);

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

        mPersistable.get(node).saveId = "wall";

        mRenderMask.get(node).visible = EnumSet.of(RenderMask.Mask.BASIC);

        return node;
    }

    private Entity createTechpoint() {
        Entity techpoint = world.createEntity(this.techpoint);

        Anim anim = mAnim.get(techpoint);
        anim.id = "techpoint";

        // since there is always a techpoint around we never use this for preferred route calculations.
        Routable routable = mRoutable.get(techpoint);
        routable.setIgnoreForPreferred(true);

        Bounds bounds = mBounds.get(techpoint);
        bounds.maxx = 16;
        bounds.maxy = 16;

        mPersistable.get(techpoint).saveId = "techpoint";

        mRenderMask.get(techpoint).visible = EnumSet.allOf(RenderMask.Mask.class);

        TeamMember teamMember = mTeamMember.get(techpoint);
        teamMember.artUnaligned = "techpoint";
        teamMember.art.put(Team.ALIEN, "techpoint-alien");
        teamMember.art.put(Team.MARINE, "techpoint-marine");

        return techpoint;
    }
}
