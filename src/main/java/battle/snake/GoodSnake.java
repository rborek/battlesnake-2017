package battle.snake;

import java.util.ArrayList;
import java.util.HashMap;

public class GoodSnake implements SnakeAI {
	@Override
	public String getName() {
		return "Good snake";
	}

	@Override
	public String getColor() {
		return "red";
	}

	public static Direction lastMoved(Snake snake) {
		Point head = snake.coords.get(0);
		Point afterHead = snake.coords.get(1);
		int yDiff = head.y - afterHead.y;
		if (yDiff != 0) {
			if (yDiff > 0) {
				return Direction.DOWN;
			} else {
				return Direction.UP;
			}
		}
		int xDiff = head.x - afterHead.x;
		if (xDiff > 0) {
			return Direction.RIGHT;
		} else {
			return Direction.LEFT;
		}
	}

	@Override
	public Direction move(int width, int height, ArrayList<Snake> snakes, ArrayList<Point> food, String self, int turn, String gameId) {
		Snake us = null;
		for(int i = 0; i < snakes.size(); i++) {
			if (snakes.get(i).id.equals(self)) {
				us = snakes.get(i);
			}
		}
		Point head = us.coords.get(0);

		// Right wall
		if (head.x == width-1) {
			if (head.y == 0) {
				if (lastMoved(us) == Direction.UP) {
					return Direction.LEFT;
				} else {
					return Direction.DOWN;
				}
			} else if (head.y == height-1) {
				if (lastMoved(us) == Direction.RIGHT) {
					return Direction.UP;
				} else {
					return Direction.LEFT;
				}
			}
		}

		// Left wall
		if (head.x == 0) {
			if (head.y == height-1) {
				if (lastMoved(us) == Direction.DOWN) {
					return Direction.RIGHT;
				} else {
					return Direction.UP;
				}
			} else if (head.y == 0) {
				if (lastMoved(us) == Direction.UP) {
					return Direction.RIGHT;
				} else {
					return Direction.DOWN;
				}
			}
		}

		return lastMoved(us);

	}
}
