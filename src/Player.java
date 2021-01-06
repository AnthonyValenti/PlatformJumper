import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player extends Object {
	//Declares variables
	public boolean right, left, leftBlocked, rightBlocked;
	public int xSpeed;
	protected BufferedImage image;

	//Initializes variables
	public Player(int x, int y, int width, int height, String imageName) {
		super(x, y, width, height);
		try {
			this.image = ImageIO.read(new File(imageName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		xSpeed = 0;
		right = false;
		left = false;
		leftBlocked = false;
		rightBlocked = false;
	}

	//Draws a specified image
	public void render(Graphics g) {
		g.drawImage(image, x, y, width, height, null);
	}

	//Changes the coordinates of the object depending on certain conditions
	public void move() {
		if (left && right == false) {
			xSpeed = -5;
		} else if (right && left == false) {
			xSpeed = 5;
		} else if (right == left) {
			xSpeed = 0;
		}
		if (left && leftBlocked) {
			xSpeed = 0;
		} else if (right && rightBlocked) {
			xSpeed = 0;
		}
		x += xSpeed;

	}
}
