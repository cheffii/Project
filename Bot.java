package snakes;

import java.util.Arrays;
import java.util.Iterator;

/**
 * This interface provides functions that should be implemented
 * to create smart snake bot for the game
 */
public interface Bot {
	static final Direction[] DIRECTIONS = new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
    /**
     * Smart snake bot (brain of your snake) should choose step (direction where to go)
     * on each game step until the end of game
     *
     * @param snake    Your snake's body with coordinates for each segment
     * @param opponent Opponent snake's body with coordinates for each segme
     * @param mazeSize Size of the board
     * @param apple    Coordinate of an apple
     * @return Direction in which snake should crawl next game step
     */
	public default Direction chooseDirection(Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
    
    Coordinate head = snake.getHead();
    /* Get the coordinate of the second element of the snake's body
     * to prevent going backwards */
    Coordinate afterHeadNotFinal = null;
    if (snake.body.size() >= 2) {
        Iterator<Coordinate> it = snake.body.iterator();
        it.next();
        afterHeadNotFinal = it.next();
    }
    
    final Coordinate afterHead = afterHeadNotFinal;
    
    /* The only illegal move is going backwards. Here we are checking for not doing it */
    Direction[] validMoves = Arrays.stream(DIRECTIONS)
            .filter(d -> !head.moveTo(d).equals(afterHead)) // Filter out the backwards move
            .sorted()
            .toArray(Direction[]::new);
    
    /* Just naïve greedy algorithm that tries not to die at each moment in time */
    Direction[] notLosing = Arrays.stream(validMoves)
            .filter(d -> head.moveTo(d).inBounds(mazeSize))             // Don't leave maze
            .filter(d -> !snake.elements.contains(head.moveTo(d)))      // and yourself
            .sorted()
            .toArray(Direction[]::new);
	
    if (notLosing.length > 0) return notLosing[0];
    return validMoves[0];
    
    

    }
}

