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

	public static getFoodDirections toFood(Point head, Point food) {
		int xDiff = food.x - head.x;
		int yDiff = food.y - head.y;

		getFoodDirections move = new getFoodDirections();
		if (Math.abs(xDiff) > Math.abs(yDiff)) {
			if (xDiff < 0) {
				move.primary = Direction.LEFT;
				if (yDiff < 0) {
					move.backup = Direction.UP;
					return move;
				} else {
					move.backup = Direction.DOWN;
					return move;
				}
			} else {
				move.primary = Direction.RIGHT;
				if (yDiff < 0) {
					move.backup = Direction.UP;
					return move;
				} else {
					move.backup = Direction.DOWN;
					return move;
				}
			}
		} else {
			if (yDiff < 0) {
				move.primary = Direction.UP;
				if(xDiff < 0) {
					move.backup = Direction.LEFT;
				} else {
					move.backup = Direction.RIGHT;
				}
			} else {
				move.primary = Direction.DOWN;
				if(xDiff < 0) {
					move.backup = Direction.LEFT;
				} else {
					move.backup = Direction.RIGHT;
				}
			}
		}
	}

	static final int EMPTY = 0;
	static final int SNAKE_OCCUPIED = 1;
	static final int FOOD = 2;

	@Override
	public Direction move(int width, int height, ArrayList<Snake> snakes, ArrayList<Point> food, String self, int turn, String gameId) {
		Snake us = null;
		for(int i = 0; i < snakes.size(); i++) {
			if (snakes.get(i).id.equals(self)) {
				us = snakes.get(i);
			}
		}
		Point head = us.coords.get(0);
		ValidMoves valid = new ValidMoves();

		int[][] occupiedSpaces = new int[width][height];
		for(int i = 0; i < snakes.size(); i++) {
			for (int j = 0; j < snakes.get(i).coords.size(); j++) {
				Point p = snakes.get(i).coords.get(j);
				occupiedSpaces[p.x][p.y] = SNAKE_OCCUPIED;
			}
		}

		for(int i = 0; i < food.size(); i++) {
				Point p = food.get(i);
				occupiedSpaces[p.x][p.y] = FOOD;
		}

		valid.disableDirection(lastMoved(us).oppositeDir());



		if (head.x + 1 >= width || occupiedSpaces[head.x + 1][head.y] == SNAKE_OCCUPIED) {
			valid.right = false;
		}
		if (head.x - 1 < 0 || occupiedSpaces[head.x - 1][head.y] == SNAKE_OCCUPIED) {
			valid.left = false;
		}
		if (head.y + 1 >= height || occupiedSpaces[head.x][head.y + 1] == SNAKE_OCCUPIED) {
			valid.down = false;
		}
		if (head.y - 1 < 0 || occupiedSpaces[head.x][head.y - 1] == SNAKE_OCCUPIED) {
			valid.up = false;
		}

		// Right wall
		if (head.x == width-1) {
			if (head.y == 0) { // Top-right corner
				if (lastMoved(us) == Direction.UP) {
					return Direction.LEFT;
				} else {
					return Direction.DOWN;
				}
			} else if (head.y == height-1) { // Bottom-right corner
				if (lastMoved(us) == Direction.RIGHT) {
					return Direction.UP;
				} else {
					return Direction.LEFT;
				}
			} else if (lastMoved(us) == Direction.RIGHT) { // Pointing at right wall
				valid.right = false;
			}
		}


		// Left wall
		if (head.x == 0) {
			if (head.y == height-1) { // Bottom-left corner
				if (lastMoved(us) == Direction.DOWN) {
					return Direction.RIGHT;
				} else {
					return Direction.UP;
				}
			} else if (head.y == 0) { // Top-left corner
				if (lastMoved(us) == Direction.UP) {
					return Direction.RIGHT;
				} else {
					return Direction.DOWN;
				}
			} else if (lastMoved(us) == Direction.LEFT) { // Pointing at left wall
				valid.left = false;
			}
		}

		// Top wall
		if (head.y == 0) {
			valid.up = false;
		} else if (head.y == height - 1) {
			valid.down = false;
		}

		if (valid.up) {
			return Direction.UP;
		}
		if (valid.down) {
			return Direction.DOWN;
		}
		if (valid.left) {
			return Direction.LEFT;
		}
		if (valid.right) {
			return Direction.RIGHT;
		}

		return lastMoved(us);

	}
}
