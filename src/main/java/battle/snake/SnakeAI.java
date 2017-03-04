package battle.snake;

import java.util.ArrayList;

public interface SnakeAI {
	String getName();
	String getColor();
	String getTaunt();
	Direction move(int width, int height, ArrayList<Snake> snakes, ArrayList<Point> food, String self, int turn, String gameId);
}
