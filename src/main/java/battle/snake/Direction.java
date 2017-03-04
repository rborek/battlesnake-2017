package battle.snake;

public enum Direction {
	UP("up"),
	DOWN("down"),
	LEFT("left"),
	RIGHT("right"),
	INVALID("invalid");
	private String name;
	private Direction(String name) {
		this.name = name;
	}
	public String toString() {
		return name;
	}
	public static Direction fromString(String str) {
		if (str.equals("up")) {
			return UP;
		} else if (str.equals("down")) {
			return DOWN;
		} else if (str.equals("left")) {
			return LEFT;
		} else if (str.equals("right")) {
			return RIGHT;
		} else {
			return INVALID;
		}
	}
	public static Direction oppositeDir(Direction dir) {
		switch (dir) {
			case UP:
				return DOWN;
			case DOWN:
				return UP;
			case RIGHT:
				return LEFT;
			case LEFT:
				return RIGHT;
			default:
				return INVALID;
		}
	}


}

