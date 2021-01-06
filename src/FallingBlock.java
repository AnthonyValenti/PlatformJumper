
public class FallingBlock extends Object {
	//Declares variables
	private int ySpeed;

	//Initializes variables
	public FallingBlock(int x, int y, int width, int height) {
		super(x, y, width, height);
		ySpeed = 10;
	}

	//Changes the objects y position
	public void fall() {
		y += ySpeed;
	}
}
