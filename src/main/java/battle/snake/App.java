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
			ArrayList snakeArray = (ArrayList)((Map)data.get("snakes")).get("data");
			for (Object entry : snakeArray) {
				Snake curSnake = new Snake();
				LinkedTreeMap snakeEntry = (LinkedTreeMap)entry;
				curSnake.health = (int)(double)snakeEntry.get("health");
				curSnake.id = (String)snakeEntry.get("id");
				curSnake.name = (String)snakeEntry.get("name");
				curSnake.coords = new ArrayList<>();
				for (Object coord : (ArrayList)((Map)snakeEntry.get("body")).get("data")) {
					Map coordMap = (Map)coord;
					Point p = new Point((int)(double)coordMap.get("x"), (int)(double)coordMap.get("y"));
					curSnake.coords.add(p);
				}
				snakes.add(curSnake);
			}
		}
		ArrayList<Point> food = new ArrayList<>();
		if (data.containsKey("food")) {
			ArrayList foodArray = (ArrayList)((Map)data.get("food")).get("data");
			for (Object entry : foodArray) {
				ArrayList coordArray = (ArrayList)(entry);
				Point p = new Point((int)(double)coordArray.get(0), (int)(double)coordArray.get(1));
				food.add(p);
			}
		}

		String self = "";
		if (data.containsKey("you")) {
			self = (String)((Map)data.get("you")).get("id");
		}
		int turn = 0;
		if (data.containsKey("turn")) {
			turn = (int)(double)data.get("turn");
		}
		String gameId = "";
		if (data.containsKey("id")) {
			gameId = (String)data.get("id");
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
