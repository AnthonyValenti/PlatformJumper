import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Object {
	//Declares variables
	public int x, y, width, height;
	protected Rectangle hitBox;

	//Initializes variables
	public Object(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		hitBox = new Rectangle(x, y, width, height);
	}

	//Returns the hit box of the objects. Used for collision detection
	public Rectangle hitBox() {
		hitBox.setBounds(x, y, width, height);
		return hitBox;
	}
	
	//Draws the object as a rectangle with a specified color
	public void render(Graphics g, Color color) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}
}
