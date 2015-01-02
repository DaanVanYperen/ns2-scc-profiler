package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * @author Daan van Yperen
 *
 * @todo probably want to implement validation differently, but this will do for now.
 */
public class Input extends Component {
	public String allowedCharacters = "0123456789";
	public int minLength = 0;
	public int maxLength = 64;

	public Input(int minLength, int maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
}
