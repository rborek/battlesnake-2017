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

	@Override
	public Direction move(int width, int height, ArrayList<Snake> snakes, ArrayList<Point> food, String self, int turn, String gameId) {
		return Direction.LEFT;
	}
}
