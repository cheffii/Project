package kevinbots;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

public class Jerry_Kevin implements Bot {
    private static float[] model = new float[]{0.3f, 0.2f};

    public static final int seitenLänge; // links + rechts + mitte


    static int maxView = 5;
    static int maxRadarBounds = 11;
    static int minRadarBounds = 0;
    static float blockiert = -1;
    static float frei = 0;
    static float apfel = 1;


    static {
        seitenLänge = maxView + maxView + 1;
    }

    float[] inputs;

    public void setParams(float[][] params) {
        this.params = params;
    }

    public float[][] params;

    public void save(File file) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream(file));
        writer.println(params.length + " " + params[1].length);
        for (float[] param : params) {
            for (float v : param) {
                writer.print(v);
                writer.print(", ");
            }
            writer.print("\n");
        }
        writer.close();
    }

    public Jerry_Kevin(File paramFile) throws FileNotFoundException {
        Scanner scanner = new Scanner(paramFile);
        params = new float[scanner.nextInt()][scanner.nextInt()];
        scanner.nextLine();
        for (float[] param : params) {
            String[] values = scanner.nextLine().split(",");
            for (int i = 0; i < param.length; i++) {
                param[i] = Float.parseFloat(values[i]);
            }
        }
    }



     public Jerry_Kevin(){
        try{
            Scanner scanner = new Scanner("jerry.txt");
            params = new float[scanner.nextInt()][scanner.nextInt()];
            scanner.nextLine();
            for (float[] param : params) {
                String[] values = scanner.nextLine().split(",");
                for (int i = 0; i < param.length; i++) {
                    param[i] = Float.parseFloat(values[i]);
                }
            }
        }catch(Exception e) {
            inputs = new float[seitenLänge * seitenLänge + 8]; // +4 wegen gegner und apfel

            params = new float[4][inputs.length + 1];

            for (float[] param : params) {
                for (int i = 0; i < param.length; i++) {
                    param[i] = (float) ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
                }
            }
        }
     }


     public Jerry_Kevin(Jerry_Kevin parent, float mutationsStärke) {
     inputs = new float[parent.inputs.length];
     params = new float[parent.params.length][parent.params[0].length];

     for (int i = 0; i < params.length; i++) {
     for (int k = 0; k < params[i].length; k++) {
     params[i][k] = (float) (parent.params[i][k] + (mutationsStärke * ThreadLocalRandom.current().nextDouble(-.01, .01)));
     }
     }
     }


    /**
     * Transformiert die Koordinaten vom globalen Koordinatensystem in das vom "radar". Der Radar ist eine Art minimap für die schlange, damit sie die nähere Umgebung wahrnehmen kann.
     *
     * @param snake   bestimmt das Zentrum vom radar.
     * @param element ausgangskoordinaten für transformation
     * @return neue Instanz.
     */
    static Coordinate transformToRadar(final Snake snake, final Coordinate element) {
        Coordinate minimapCoordinates = new Coordinate(
                (maxView + (element.x - snake.getHead().x)),
                (maxView + (element.y - snake.getHead().y))
        );

        return minimapCoordinates;
    }

    @Override
    public boolean equals(Object o) {
        boolean ok;
        if (this == o) return true;
        if (!(o instanceof Jerry_Kevin)) return false;

        for (int i = 0; i < this.params.length; i++)
            for (int j = 0; j < this.params[i].length; j++)
                if (this.params[i][j] != ((Jerry_Kevin) o).params[i][j])
                    return false;
        return true;
    }


    @Override
    public int hashCode() {
        int result = Arrays.hashCode(inputs);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }

    /**
     * snake und element müssen selbes Koordinatensystem haben
     *
     * @param snake
     * @param element
     * @return
     */
    static boolean isVisible(Snake snake, Coordinate element) {
        int dist;
        dist = (snake.getHead().x - element.x);
        dist = abs(dist);
        if (dist > maxView)
            return false;
        dist = (snake.getHead().y - element.y);
        dist = abs(dist);
        if (dist > maxView)
            return false;
        return true;
    }



    @Override
    public Direction chooseDirection(Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        // mach aus parametern input für NN
        Arrays.fill(inputs, 0);
        setElementOnRadar(inputs, snake, apple, apfel);
        setElementOnRadar(inputs, snake, snake.getHead(), blockiert);

        setElementOnRadar(inputs, snake, opponent.getHead(), blockiert);
        snake.body.forEach(bodyElement -> setElementOnRadar(inputs, snake, bodyElement, blockiert));

        setElementOnRadar(inputs, snake, opponent.getHead(), blockiert);
        opponent.body.forEach(bodyElement -> setElementOnRadar(inputs, snake, bodyElement, blockiert));

        // Fühler für wände
        inputs[inputs.length - 8] = ((snake.mazeSize.y - snake.getHead().y) == 0)? -1: 0;
        inputs[inputs.length - 7] = ((snake.mazeSize.x - snake.getHead().x) == 0)? -1: 0;
        inputs[inputs.length - 6] = (snake.getHead().y == 0)? -1: 0;
        inputs[inputs.length - 5] = (snake.getHead().x == 0)? -1: 0;

        // richtungen von gegner und Apfel werden auch mit berücksichtigt, auch wenn sie außerhalt der sichtweite sind.
        inputs[inputs.length - 4] = (apple.x - snake.getHead().x) / (float) snake.mazeSize.x; //x apfel
        inputs[inputs.length - 3] = (apple.y - snake.getHead().y) / (float) snake.mazeSize.y;//y apfel
        inputs[inputs.length - 2] = (opponent.getHead().x - snake.getHead().x) / (float) snake.mazeSize.x; //x opponent
        inputs[inputs.length - 1] = (opponent.getHead().y - snake.getHead().y) / (float) snake.mazeSize.y;//y opponent
        /**
        //Erlaubte Moves
        Coordinate afterHeadNotFinal = null;
        if (snake.body.size() >= 2) {
            Iterator<Coordinate> it = snake.body.iterator();
            it.next();
            afterHeadNotFinal = it.next();
        }

        Direction[] validMoves = Arrays.stream(DIRECTIONS)
                .filter(d -> !snake.getHead().moveTo(d).equals(snake.body.)) // Filter out the backwards move
                .sorted()
                .toArray(Direction[]::new);

        /* Just naïve greedy algorithm that tries not to die at each moment in time */
        /**
        Direction[] notLosing = Arrays.stream(validMoves)
                .filter(d -> head.moveTo(d).inBounds(mazeSize))             // Don't leave maze
                .filter(d -> !opponent.elements.contains(head.moveTo(d)))   // Don't collide with opponent...
                .filter(d -> !snake.elements.contains(head.moveTo(d)))      // and yourself
                .sorted()
                .toArray(Direction[]::new);

        if (notLosing.length > 0) return notLosing[0];
        else return validMoves[0];
        //Versuche nicht zu verlieren
        */


        float[] decisions = evaluate(inputs);


        int maxI = 0;
        for (int i = 0; i < decisions.length; i++) {
            if (decisions[i] >= decisions[maxI]) {
                maxI = i;
            }
        }
       try{
           this.save(new File("jerry.txt"));
       }
       catch(FileNotFoundException e){}

        return DIRECTIONS[maxI];
    }


    private float[] evaluate(float[] inputs) {
        float[] results = new float[4];
        results[0] = 0;
        results[1] = 0;
        results[2] = 0;
        results[3] = 0;
        for (int i = 0; i < inputs.length; i++) {
            //results[0] += upParams[i] * inputs[i];
            results[0] += params[0][i] * inputs[i];
            results[1] += params[1][i] * inputs[i];
            results[2] += params[2][i] * inputs[i];
            results[3] += params[3][i] * inputs[i];
        }
        results[0] += params[0][inputs.length];
        results[1] += params[1][inputs.length];
        results[2] += params[2][inputs.length];
        results[3] += params[3][inputs.length];
        return results;
    }

    public void mutate(float mutationsStärke) {
        for (int i = 0; i < params.length; i++) {
            for (int j = 0; j < params.length; j++) {
                params[i][j] += ThreadLocalRandom.current().nextDouble(-.01, .01) * mutationsStärke;
            }
        }
    }

    private static int transform2dTo1d(Coordinate coordinate) {

        return ((seitenLänge * coordinate.y) + coordinate.x);
    }

    private static void setElementOnRadar(float[] inputs, Snake snake, Coordinate position, float value) {
        if (!isVisible(snake, position))
            return;

        Coordinate elementOnRadar = transformToRadar(snake, position);
        int index = transform2dTo1d(elementOnRadar);
        inputs[index] = value;
    }

    private static final Direction[] DIRECTIONS = new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
}


