package battle.snake;

import java.util.ArrayList;

public class ValidMoves {
	public boolean left = true;
	public boolean right = true;
	public boolean up = true;
	public boolean down = true;
	public ArrayList<Direction> getValidDirections() {
		ArrayList<Direction> dirs = new ArrayList<Direction>();
		if (left) dirs.add(Direction.LEFT);
		if (right) dirs.add(Direction.RIGHT);
		if (up) dirs.add(Direction.UP);
		if (down) dirs.add(Direction.DOWN);
		return dirs;
	}
	public boolean isValid(Direction dir) {
		switch (dir) {
			case UP:
				return up;
			case DOWN:
				return down;
			case RIGHT:
				return right;
			case LEFT:
				return left;
			default:
				break;
		}
		return false;
	}
	public void disableDirection(Direction dir) {
		switch (dir) {
			case UP:
				up = false;
				break;
			case DOWN:
				down = false;
				break;
			case RIGHT:
				right = false;
				break;
			case LEFT:
				left = false;
				break;
			default:
				break;
		}
	}
}
