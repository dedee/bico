package de.dedee.bico;

/**
 * Contains one Widget Resolution information.
 * 
 * @author dedee
 * 
 */
public class Resolution {

	private int width;
	private int height;

	/**
	 * @param width
	 * @param height
	 */
	public Resolution(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	// /

	public String getWidgetIdentifier() {
		return "bico_widget_" + width + "_" + height;
	}

	public String getWidgetDescription() {
		return "bico Widget (" + width + "x" + height + ")";
	}

}
