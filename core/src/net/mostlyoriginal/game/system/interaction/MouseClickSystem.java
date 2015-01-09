package net.mostlyoriginal.game.system.interaction;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.system.physics.CollisionSystem;
import net.mostlyoriginal.game.component.ui.Clickable;

/**
 * Track mouse over clickables. will indicate hover or clicked.
 *
 * @author Daan van Yperen
 */
@Wire
public class MouseClickSystem extends EntityProcessingSystem {

    CollisionSystem system;
    TagManager tagManager;

    protected ComponentMapper<Clickable> mClickable;
    private boolean leftMouseTapped;
    private boolean rightMouseTapped;
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

        leftMouseTapped = Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !leftAlreadyDown;
        rightMouseTapped = Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && !rightAlreadyDown;
        middleMouseTapped = Gdx.input.isButtonPressed(Input.Buttons.MIDDLE) && !middleAlreadyDown;
    }

    @Override
    protected void end() {
        super.end();

        leftAlreadyDown = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        rightAlreadyDown = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
        middleAlreadyDown = Gdx.input.isButtonPressed(Input.Buttons.MIDDLE);
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
