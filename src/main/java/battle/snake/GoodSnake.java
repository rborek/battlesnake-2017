package battle.snake;


import java.util.ArrayList;

public class GoodSnake implements SnakeAI {
	@Override
	public String getName() {
		return "I Can't Believe It's Not A Snake!";
	}

	@Override
	public String getColor() {
		return "gold";
	}

	public String getTaunt() {
		String[] quotes = {"Who am I?", "Are you my dog?", "Why am I here?", "Who are you?", "I want cake.", "I like trains.", "What am I even doing?"};
		int len = quotes.length;
		int rand = (int)(Math.random() * len);
		return quotes[rand];
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

	public static FoodDirection toFood(Point head, Point food) {
		int xDiff = food.x - head.x;
		int yDiff = food.y - head.y;

		FoodDirection move = new FoodDirection();
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
		return move;
	}

	public boolean tileIsSafe(TileEntry entry, ArrayList<Snake> snakes, Snake self) {
		if (entry == null) {
			return true;
		}
		if (entry.type == TileType.FOOD) {
			return true;
		}
		if (entry.type == TileType.FUTURE_SNAKE_HEAD) { // enemy snake could move here
			Snake enemy = null;
			for (int i = 0; i < snakes.size(); i++) {
				if (snakes.get(i).id.equals(entry.snakeId)) {
					enemy = snakes.get(i);
					break;
				}
			}
			if (enemy.coords.size() < self.coords.size()) { // can ram into enemy head
				return true;
			}
			return false;
		} else {
			return false;
		}

	}

	public boolean inGrid(Point p, int width, int height) {
		return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
	}


	private static int floodArea(Point p, TileEntry[][] grid) {
		FloodResult result = new FloodResult();
		result.area = 0;
		result.mark = new boolean[grid.length][grid[0].length];
		flood(grid, result, p.x, p.y);
		return result.area;
	}

	private static void flood(TileEntry[][] grid, FloodResult result, int row, int col) {
		// make sure row and col are inside the image
		if (row < 0) return;
		if (col < 0) return;
		if (row >= grid.length) return;
		if (col >= grid[0].length) return;

		// make sure this pixel hasn't been visited yet
		if (result.mark[row][col]) return;

		// make sure this pixel is the right color to fill
		if (grid[row][col] != null && grid[row][col].type != TileType.FOOD) return;

		// fill pixel with target color and mark it as visited
		result.mark[row][col] = true;
		result.area++;

		// recursively fill surrounding pixels
		// (this is equivelant to depth-first search)
		flood(grid, result, row - 1, col);
		flood(grid, result, row + 1, col);
		flood(grid, result, row, col - 1);
		flood(grid, result, row, col + 1);
	}

	private static boolean alreadyAddedDir(ArrayList<DirectionArea> da, Direction dir) {
		for (DirectionArea dira : da) {
			if (dira.dir == dir) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Direction move(int width, int height, ArrayList<Snake> snakes, ArrayList<Point> food, String self, int turn, String gameId) {
		Snake us = null;
		TileEntry[][] grid = new TileEntry[width][height];
		Point temp = new Point(0,0);
		ValidMoves other = new ValidMoves();
		for(int i = 0; i < food.size(); i++) {
			Point p = food.get(i);
			TileEntry entry = new TileEntry();
			entry.type = TileType.FOOD;
			grid[p.x][p.y] = entry;
		}
		for(Snake snake : snakes) {
			if (snake.id.equals(self)) {
				us = snake;
			} else {
				other.left = true;
				other.right = true;
				other.up = true;
				other.down = true;
				other.disableDirection(lastMoved(snake).oppositeDir());
				for (Direction dir : other.getValidDirections()) {
					temp.setTo(snake.coords.get(0)); // temp points to snake's head
					temp.add(dir);
					if (inGrid(temp, width, height)) {
						if (grid[temp.x][temp.y] != null && grid[temp.x][temp.y].type == TileType.FOOD) {
							snake.couldEat = true;
						}
						TileEntry potential = new TileEntry();
						potential.type = TileType.FUTURE_SNAKE_HEAD;
						potential.snakeId = snake.id;
						grid[temp.x][temp.y] = potential;
					}
				}
			}
		}
		Point head = us.coords.get(0);
		ValidMoves valid = new ValidMoves();

		for(int i = 0; i < snakes.size(); i++) {
			for (int j = 0; j < snakes.get(i).coords.size(); j++) {
				if (j != snakes.get(i).coords.size() - 1 || snakes.get(i).couldEat) {
					Point p = snakes.get(i).coords.get(j);
					TileEntry entry = new TileEntry();
					entry.type = TileType.SNAKE_BODY;
					entry.snakeId = snakes.get(i).id;
					grid[p.x][p.y] = entry;

				}
			}
		}


		valid.disableDirection(lastMoved(us).oppositeDir());



		if (head.x + 1 >= width) {
			valid.right = false;
		} else {
			valid.right = tileIsSafe(grid[head.x + 1][head.y], snakes, us);
		}
		if (head.x - 1 < 0) {
			valid.left = false;
		} else {
			valid.left = tileIsSafe(grid[head.x - 1][head.y], snakes, us);
		}
		if (head.y + 1 >= height) {
			valid.down = false;
		} else {
			valid.down = tileIsSafe(grid[head.x][head.y + 1], snakes, us);
		}
		if (head.y - 1 < 0) {
			valid.up = false;
		} else {
			valid.up = tileIsSafe(grid[head.x][head.y - 1], snakes, us);
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
		Point p = new Point(0,0);
		ArrayList<DirectionArea> areas = new ArrayList<DirectionArea>();
		if (food.size() > 0) {
			Point closestFood = null;
			int closest = Integer.MAX_VALUE;
			for (Point _p : food) {
				int dist = Math.abs(head.x - _p.x) + Math.abs(head.y - _p.y);
				if (dist < closest) {
					closest = dist;
					closestFood = _p;
				}
			}
			FoodDirection dirs = toFood(head, closestFood);


			if (valid.isValid(dirs.primary)) {
				p.setTo(head);
				p.add(dirs.primary);
				DirectionArea da = new DirectionArea();
				da.dir = dirs.primary;
				da.area = floodArea(p, grid);
				da.leadsToFood = true;
				areas.add(da);
			}
			if (valid.isValid(dirs.backup)) {
				p.setTo(head);
				p.add(dirs.backup);
				DirectionArea da = new DirectionArea();
				da.dir = dirs.backup;
				da.area = floodArea(p, grid);
				da.leadsToFood = true;
				areas.add(da);
			}

		}
		if (valid.up) {
			if (!alreadyAddedDir(areas, Direction.UP)) {
				p.setTo(head);
				p.add(Direction.UP);
				DirectionArea da = new DirectionArea();
				da.dir = Direction.UP;
				da.area = floodArea(p, grid);
				da.leadsToFood = false;
				areas.add(da);
			}
		}
		if (valid.down) {
			if (!alreadyAddedDir(areas, Direction.DOWN)) {
				p.setTo(head);
				p.add(Direction.DOWN);
				DirectionArea da = new DirectionArea();
				da.dir = Direction.DOWN;
				da.area = floodArea(p, grid);
				da.leadsToFood = false;
				areas.add(da);
			}
		}
		if (valid.left) {
			if (!alreadyAddedDir(areas, Direction.LEFT)) {
				p.setTo(head);
				p.add(Direction.LEFT);
				DirectionArea da = new DirectionArea();
				da.dir = Direction.LEFT;
				da.area = floodArea(p, grid);
				da.leadsToFood = false;
				areas.add(da);
			}
		}
		if (valid.right) {
			if (!alreadyAddedDir(areas, Direction.RIGHT)) {
				p.setTo(head);
				p.add(Direction.RIGHT);
				DirectionArea da = new DirectionArea();
				da.dir = Direction.RIGHT;
				da.area = floodArea(p, grid);
				da.leadsToFood = false;
				areas.add(da);
			}
		}

		ArrayList<DirectionArea> maxes = new ArrayList<DirectionArea>();
		if (!areas.isEmpty()) {
			int max = Integer.MIN_VALUE;
			DirectionArea maxArea = null;
			for (DirectionArea d : areas) {
				if (d.area > max) {
					max = d.area;
					maxArea = d;
				}
			}
			for (DirectionArea d : areas) {
				if (d.area == max) {
					maxes.add(d);
				}
			}
		}
		if (us.health < 60) {
			for (DirectionArea d : maxes) {
				if (d.leadsToFood) {
					return d.dir;
				}
			}
		}
		for (DirectionArea d : maxes) {
			return d.dir;
		}
		System.out.println("whoops");
		return Direction.INVALID;

	}
}
