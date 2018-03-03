package battle.snake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BFSContext {
    List<Direction> path = null;
    Point p = new Point(0,0);
    HashSet<Point> visitedPoint = null;
}
