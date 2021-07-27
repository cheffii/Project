package linewise;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

import java.util.*;
//import java.lang.Math.*;

public class lineBot implements Bot{
    private Queue<Direction> Path;
    private Coordinate Apple_old;
    private final Direction[] DIRECTIONS = new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

    @Override
    public Direction chooseDirection(Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        if(Path == null) Path = new LinkedList<Direction>();
        if(Apple_old != apple || Path.isEmpty()){   //new Path if the apple has been eaten or the last path was interrupted
            linePath(snake.getHead(),apple);
            Apple_old = apple;
        }
        Direction next = Path.remove();             //get the next Step of thr path

        Coordinate head = snake.getHead();          //simple chek for
        Direction[] notLosing = Arrays.stream(DIRECTIONS)
                .filter(d -> head.moveTo(d).inBounds(mazeSize))             // Don't leave maze
                .filter(d -> !opponent.elements.contains(head.moveTo(d)))   // Don't collide with opponent...
                .filter(d -> !snake.elements.contains(head.moveTo(d)))      // and yourself
                .sorted()
                .toArray(Direction[]::new);
        for(int i =0; i< notLosing.length; i++){
            if(next==notLosing[i])return next;
        }
        Path.clear();//throw away path since it is now worthless
        if(notLosing.length > 0){
            Random r = new Random();
            return notLosing[r.nextInt(notLosing.length)];
        }
        return Direction.DOWN;
    }
    //Straight forward implementation of Bresenham's line algorithm
    private void linePath(Coordinate s, Coordinate  f){
        //Queue<Coordinate> line = new ArrayDeque<Coordinate>();
        int x0 =s.x;
        int y0 = s.y;
        int dx = Math.abs(f.x - s.x);
        int dy = Math.abs(f.y - s.y);
        int sx = s.x < f.x ? 1 : -1;
        int sy = s.y < f.y ? 1 : -1;
        int err = dx-dy;
        int e2;
        Coordinate prev = null;
        Coordinate next = null;
        while (true) {

            next = new Coordinate(x0,y0);
            if (prev != null) PutInPath(prev, next);
            prev = next;

            //line.add(new Coordinate(x0,y0));
            //System.out.println(x0+"|"+y0);
            if (x0 == f.x && y0 == f.y) break;
            e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x0 = x0 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }

    }
    //takes two coordinates and converts it in to moves. The Dx and Dy can´t be more tan -1;0;1.
    private void PutInPath(Coordinate S, Coordinate F){
        //System.out.println("from"+S.x+"|"+S.y+" to "+F.x+"|"+F.y);
        int dx = F.x - S.x;
        int dy = F.y - S.y;
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
    }
}
