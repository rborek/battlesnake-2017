package battle.snake;

public class Point {
	public int x;
	public int y;
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void setTo(Point p) {
		x = p.x;
		y = p.y;
	}

	public void add(Direction dir) {
		switch (dir) {
			case DOWN:
				y += 1;
				break;
			case UP:
				y -= 1;
				break;
			case LEFT:
				x -= 1;
				break;
			case RIGHT:
				x += 1;
				break;
			default:
				break;
		}
	}
}
