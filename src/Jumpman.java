import java.awt.Rectangle;

public class Jumpman extends Player {

	public boolean up, falling;
	public int ySpeed;
	private int counter;
/**
 * 
 * @param x location of jumpman on the x-axis
 * @param y location of jumpman on the y-axis
 * @param width width of jumpman
 * @param height height of jumpman
 * @param imageName determines the image being used as the character
 */
	public Jumpman(int x, int y, int width, int height, String imageName) {
		super(x, y, width, height, imageName);
		counter = 0;
		ySpeed = 0;
		up = false;
		falling = false;
	}

	public void move() {
		if (up && falling == false) {
			ySpeed = -11;
		} else if (falling) {
			if (counter == 1) {
				ySpeed++;
				counter = 0;
			} else {
				counter++;
			}
		}
		super.move();
		y += ySpeed;
	}

	public Rectangle topBotBox() {
		hitBox.setBounds(x + 7, y, width - 14, height);
		return hitBox;
	}

	public Rectangle leftBox() {
		hitBox.setBounds(x, y + 2, width / 2, height - 4);
		return hitBox;
	}

	public Rectangle rightBox() {
		hitBox.setBounds(x + width / 2, y + 2, width / 2, height - 4);
		return hitBox;
	}
}
