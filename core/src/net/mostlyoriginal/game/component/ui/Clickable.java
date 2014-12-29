package net.mostlyoriginal.game.component.ui;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 */
public class Clickable extends Component {
    public enum ClickState {
        NONE,
        HOVER,
        CLICKED_LEFT,
        CLICKED_RIGHT
    }

    public ClickState state = ClickState.NONE;
}
