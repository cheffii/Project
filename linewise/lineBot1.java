package linewise;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
//import java.lang.Math.*;

public class lineBot1 implements Bot{
    private Queue<Direction> Path;
    private Coordinate Apple_old;
    private static final Direction[] DIRECTIONS = new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

    @Override
    public Direction chooseDirection(Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        if(Path == null) Path = new LinkedList<Direction>();
        if(!Path.isEmpty()) return Path.remove();
        int dx = apple.x -snake.getHead().x;
        int dy = apple.y - snake.getHead().y;
        if(dx >= 1) dx= 1;
        else if(dx <= -1) dx = -1;
        if(dy >= 1) dy= 1;
        else if(dy <= -1) dy = -1;

        if(dx == 1){
            switch (dy){
                case 0:
                    Path.add(Direction.RIGHT);
                    break;
                case 1:
                    Path.add(Direction.RIGHT);
                    Path.add(Direction.UP);
                    break;
                case -1:
                    Path.add(Direction.RIGHT);
                    Path.add(Direction.DOWN);
                    break;
            }
        }
        else if(dx == 0){
            switch (dy){
                case 1:
                    Path.add(Direction.UP);
                    break;
                case -1:
                    Path.add(Direction.DOWN);
                    break;
            }
        }
        else if(dx == -1){
            switch (dy){
                case 0:
                    Path.add(Direction.LEFT);
                    break;
                case 1:
                    Path.add(Direction.LEFT);
                    Path.add(Direction.UP);
                    break;
                case -1:
                    Path.add(Direction.LEFT);
                    Path.add(Direction.DOWN);
                    break;
            }
        }
        Direction next = Path.remove();
        Coordinate head = snake.getHead();
        Direction[] notLosing = Arrays.stream(DIRECTIONS)
                .filter(d -> head.moveTo(d).inBounds(mazeSize))             // Don't leave maze
                .filter(d -> !opponent.elements.contains(head.moveTo(d)))   // Don't collide with opponent...
                .filter(d -> !snake.elements.contains(head.moveTo(d)))      // and yourself
                .sorted()
                .toArray(Direction[]::new);
        for(int i =0; i< notLosing.length; i++){
            if(next==notLosing[i])return next;
        }
        return notLosing[0];
    }
}
