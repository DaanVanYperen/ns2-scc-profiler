package net.mostlyoriginal.game.system.interaction;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.system.logic.InputSystem;

/**
 * Track mouse over clickables. will indicate hover or clicked.
 *
 * @author Daan van Yperen
 */
@Wire
public class MouseClickSystem extends EntityProcessingSystem {

    CollisionSystem system;
    TagManager tagManager;
    
    InputSystem inputSystem;

    protected ComponentMapper<Clickable> mClickable;
    public boolean leftMouseTapped;
    public boolean rightMouseTapped;
    private boolean middleMouseTapped;
    private boolean leftAlreadyDown;
    private boolean rightAlreadyDown;
    private boolean middleAlreadyDown;

    @SuppressWarnings("unchecked")
    public MouseClickSystem() {
        super(Aspect.getAspectForAll(Clickable.class, Bounds.class));
    }

    @Override
    protected void begin() {
        super.begin();

        leftMouseTapped = inputSystem.leftClicked && !leftAlreadyDown;
        rightMouseTapped = inputSystem.rightClicked && !rightAlreadyDown;
        middleMouseTapped = inputSystem.middleClicked && !middleAlreadyDown;
    }

    @Override
    protected void end() {
        super.end();

        leftAlreadyDown  = inputSystem.leftClicked;
        rightAlreadyDown = inputSystem.rightClicked;
        middleAlreadyDown= inputSystem.middleClicked;

        inputSystem.middleClicked = inputSystem.leftClicked = inputSystem.rightClicked = false;
    }

    @Override
    protected void process(Entity e) {
        final Entity cursor = tagManager.getEntity("cursor");
        if ( cursor != null )
        {
            // update state based on cursor.
            final Clickable clickable = mClickable.get(e);
            final boolean overlapping = system.overlaps(cursor, e);
            if ( overlapping )
            {
                clickable.state =
                        middleMouseTapped ? Clickable.ClickState.CLICKED_MIDDLE :
                        rightMouseTapped ? Clickable.ClickState.CLICKED_RIGHT :
                        leftMouseTapped ?  Clickable.ClickState.CLICKED_LEFT : Clickable.ClickState.HOVER;
            } else {
                clickable.state = Clickable.ClickState.NONE;
            }
        }
    }
}
