package net.mostlyoriginal.game.component;

import com.artemis.Component;
import net.mostlyoriginal.game.component.ui.ButtonListener;

/**
 * @author Daan van Yperen
 */
public class Tool extends Component {

	// not the best design but hey, ludum haste!
	public ButtonListener listener;
	public boolean continuous = false;

	public Tool(ButtonListener listener) {
		this.listener = listener;
	}
}
