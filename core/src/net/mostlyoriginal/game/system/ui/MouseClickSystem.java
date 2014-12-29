package net.mostlyoriginal.game.system.ui;

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
    private boolean leftMousePressed;
    private boolean rightMousePressed;

    public MouseClickSystem() {
        super(Aspect.getAspectForAll(Clickable.class, Bounds.class));
    }

    @Override
    protected void begin() {
        super.begin();

        leftMousePressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        rightMousePressed = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
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
                        rightMousePressed ? Clickable.ClickState.CLICKED_RIGHT :
                        leftMousePressed ?  Clickable.ClickState.CLICKED_LEFT : Clickable.ClickState.HOVER;
            } else {
                clickable.state = Clickable.ClickState.NONE;
            }
        }
    }
}
