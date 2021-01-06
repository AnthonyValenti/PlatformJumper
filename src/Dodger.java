
public class Dodger extends Player {

	//Constructor
	public Dodger(int x, int y, int width, int height, String imageName) {
		super(x, y, width, height, imageName);
	}

	//Prevents the object from moving off screen
	public void restrictMovement() {
		leftBlocked = false;
		rightBlocked = false;
		if (x < 0) {
			x = -1;
			leftBlocked = true;
		} else if (x > 400 - width) {
			x = 401 - width;
			rightBlocked = true;
		}
	}
}
