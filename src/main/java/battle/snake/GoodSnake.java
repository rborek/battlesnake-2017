package battle.snake;


import java.util.*;

public class GoodSnake implements SnakeAI {

	private List<Direction> currentPathToFood = null;
	private Point targetFood = null;
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

	private static List<Direction> getPathToPoint(Point head, Point dest, ValidMoves validMoves, TileEntry[][] grid,
			ArrayList<Snake> snakes, Snake self) {
		List<Direction> path = new ArrayList<Direction>();
		ArrayDeque<BFSContext> queue = new ArrayDeque<>();
		ArrayList<Direction> startDir = validMoves.getValidDirections();

		// starting points
		for (Direction d : startDir) {
			BFSContext context = new BFSContext();
			context.path = new ArrayList<>();
			context.visitedPoint = new HashSet<>();
			context.p.x = head.x;
			context.p.y = head.y;
			context.p.add(d);
			context.path.add(d);
			queue.add(context);
		}

		while (!queue.isEmpty()) {
			BFSContext context = queue.poll();
			if (context.p.equals(dest)) {
				return context.path;
			}

			Direction lastDir = context.path.get(context.path.size()-1);
			Direction invalidDir = lastDir.oppositeDir();
			for (Direction dir : Direction.ALL_DIRECTIONS) {
				if (dir != invalidDir) {
					BFSContext newContext = new BFSContext();
					newContext.p.setTo(context.p);

					if(context.visitedPoint.contains(newContext.p) || !tileIsSafe(grid[context.p.x][context.p.y], snakes, self)) {
						continue;
					}
					context.path = new ArrayList<>();
					newContext.path.addAll(context.path);
					newContext.visitedPoint = (HashSet<Point>)context.visitedPoint.clone();
					newContext.path.add(dir);
					newContext.visitedPoint.add(context.p);
					queue.add(newContext);
				}
			}
		}
		return path;
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

	public static boolean tileIsSafe(TileEntry entry, ArrayList<Snake> snakes, Snake self) {
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
				entry.type = TileType.CAN_KILL;
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

	void addAreaForDirection(ArrayList<DirectionArea> areas, TileEntry[][] grid, Point head, Point p, Direction direction) {
		if (!alreadyAddedDir(areas, direction)) {
			p.setTo(head);
			p.add(direction);
			DirectionArea da = new DirectionArea();
			if (grid[p.x][p.y] != null && grid[p.x][p.y].type == TileType.CAN_KILL) {
				da.couldKill = true;
			}
			da.dir = direction;
			da.area = floodArea(p, grid);
			da.leadsToFood = false;
			areas.add(da);
		}
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
		FoodDirection dirs = null;
		Point closestFood = null;
		if (food.size() > 0) {
			int closest = Integer.MAX_VALUE;
			for (Point _p : food) {
				int dist = Math.abs(head.x - _p.x) + Math.abs(head.y - _p.y);
				if (dist < closest) {
					closest = dist;
					closestFood = _p;
				}
			}
		 dirs = toFood(head, closestFood);
		}

		if (valid.up) {
			addAreaForDirection(areas, grid, head, p, Direction.UP);
		}
		if (valid.down) {
			addAreaForDirection(areas, grid, head, p, Direction.DOWN);
		}
		if (valid.left) {
			addAreaForDirection(areas, grid, head, p, Direction.LEFT);
		}
		if (valid.right) {
			addAreaForDirection(areas, grid, head, p, Direction.RIGHT);
		}

		if (dirs != null) {
			for (DirectionArea da : areas) {
				if (da.dir == dirs.primary || da.dir == dirs.backup) {
					da.leadsToFood = true;
				}

			}
		}

		ArrayList<DirectionArea> maxes = new ArrayList<DirectionArea>();
		if (!areas.isEmpty()) {
			int max = Integer.MIN_VALUE;
			DirectionArea maxArea = null;
			for (DirectionArea d : areas) {
				if (d.couldKill) {
					return d.dir;
				}
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

		if (us.health < 75 && closestFood != null) {

			for (DirectionArea d : maxes) {
				if (d.leadsToFood) {
					return d.dir;
				}
			}

		}


		if (!maxes.isEmpty()) {
			return maxes.get((int)(Math.random() * maxes.size())).dir;
		}
		System.out.println("whoops");
		return Direction.INVALID;

	}
}
