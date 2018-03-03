package battle.snake;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.ArrayList;
import java.util.Map;

public class App {
	private static Gson gson = new Gson();
	private static SnakeAI snake = new GoodSnake();

	public static String startRequest(Request request, Response response) {
		Map data = gson.fromJson(request.body(), Map.class);
		if (!data.containsKey("game_id")) {
			System.out.println("Invalid start request, no game_id");
			return "";
		}
		response.type("application/json");
		String responseString =  "{\"color\":\"" + snake.getColor() + "\"," + "\"name\":\"" + snake.getName() + "\"" + "}";
		System.out.println(responseString);
		return responseString;
	}

	public static String moveRequest(Request request, Response response) {
		Map data = gson.fromJson(request.body(), Map.class);
		int width = 0;
		int height = 0;
		if (data.containsKey("width")) {
			width = (int)(double)data.get("width");
		}
		if (data.containsKey("height")) {
			height = (int)(double)data.get("height");
		}
		ArrayList<Snake> snakes = new ArrayList<>();
		if (data.containsKey("snakes")) {
			LinkedTreeMap map = (LinkedTreeMap)data.get("snakes");
			for (Object entry : map.entrySet()) {
				Snake curSnake = new Snake();
				LinkedTreeMap snakeEntry = (LinkedTreeMap)entry;
				curSnake.health = (int)(double)snakeEntry.get("health_points");
				curSnake.id = (String)snakeEntry.get("id");
				curSnake.name = (String)snakeEntry.get("name");
				curSnake.coords = new ArrayList<>();
				for (Object coord : (ArrayList)snakeEntry.get("coords")) {
					ArrayList coordArray = (ArrayList)(coord);
					Point p = new Point((int)(double)coordArray.get(0), (int)(double)coordArray.get(1));
					curSnake.coords.add(p);
				}
				snakes.add(curSnake);
			}
		}
		ArrayList<Point> food = new ArrayList<>();
		if (data.containsKey("food")) {
			ArrayList foodArray = (ArrayList)data.get("food");
			for (Object entry : foodArray) {
				ArrayList coordArray = (ArrayList)(entry);
				Point p = new Point((int)(double)coordArray.get(0), (int)(double)coordArray.get(1));
				food.add(p);
			}
		}

		String self = "";
		if (data.containsKey("you")) {
			self = (String)data.get("you");
		}
		int turn = 0;
		if (data.containsKey("turn")) {
			turn = (int)(double)data.get("turn");
		}
		String gameId = "";
		if (data.containsKey("game_id")) {
			gameId = (String)data.get("game_id");
		}
		Direction toMove = snake.move(width, height, snakes, food, self, turn, gameId);
		response.type("application/json");
		String responseString = "{\"move\":\"" + toMove.toString() + "\", \"taunt\": \"" + snake.getTaunt() + "\"}";
		System.out.println(responseString);
		return responseString;
	}

	public static void main(String[] args) {
		Spark.port(getHerokuAssignedPort());
		Spark.get("/", (request, response) -> "no");
		Spark.post("/start", App::startRequest);
		Spark.post("/move", App::moveRequest);
		Direction dir = Direction.fromString("down");
	}

	private static int getHerokuAssignedPort() {
		final ProcessBuilder processBuilder = new ProcessBuilder();

		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}

		return 4567; // default Spark port
	}

}
