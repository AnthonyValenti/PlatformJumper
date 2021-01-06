import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Block extends Object{

	public Block(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(x, y, width, height);
		g.setColor(Color.BLACK);
		g.fillRect(x + 2, y + 2, width - 4, height - 4);
	}

	public Rectangle spawning() {
		hitBox.setBounds(x - 40, y - 60, width + 80, height + 120);
		return hitBox;
	}
}
