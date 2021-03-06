package kevinbots;

import snakes.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JerryTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        //File dir = new File("models");
        //dir.mkdir();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void chooseDirection() {
    }

    @org.junit.jupiter.api.Test
    void generatePopulation() throws InterruptedException, FileNotFoundException {

        Jerry_Kevin[] bots = new Jerry_Kevin[200];
        for (int i = 0; i < bots.length; i++) {
            bots[i] = new Jerry_Kevin();
        }
        int botId = 0;

        File directory = new File("models/generation0");
        for (int pair = 0; pair < bots.length; pair+=2) {

            Jerry_Kevin bot1 = bots[pair];
            Jerry_Kevin bot2 = bots[pair + 1];

            SnakeGame game = new SnakeGame(
                    new Coordinate(20,20),// größe von spielfeld
                    new Coordinate(5,10), // schlange 1
                    Direction.DOWN,
                    new Coordinate(15,10),// schlange 2
                    Direction.UP,4,
                    bot1, bot2
            );

            int stepcounter = 0;
            while (game.runOneStep()){
                stepcounter++;
                if (stepcounter > 100) {
                    break;
                }
            }
            
            System.out.println("steps: " + stepcounter);
            String score = game.gameResult; // beispiel "1 - 0"
            Jerry_Kevin winner = (score.charAt(0) == '1') ? bot1 : bot2;
            if (!directory.exists())
                directory.mkdir();
            winner.save(new File("models/generation0/Jerry" + botId++));
        }
    }


    private boolean logStepsOfContest = false;
    private boolean logTotalSteps = true;

    @org.junit.jupiter.api.Test
    void makeRealProgress()  throws IOException,  InterruptedException{
        int fromGeneration = 46;
        int numGenerations = 30;
        for (int i = fromGeneration; i < fromGeneration + numGenerations; i++) {
            stepSum = 0;
            simulateSomeGenerations(i);
            if (logTotalSteps) System.out.println("total steps of generation " + i +" : " + stepSum);
        }
    }
    
    @org.junit.jupiter.api.Test
    void generateSingleNewGeneration()  throws IOException,  InterruptedException{
        //TODO
        int fromGeneration = 46;
        int numGenerations = 30;
        for (int i = fromGeneration; i < fromGeneration + numGenerations; i++) {
            stepSum = 0;
            simulateSomeGenerations(i);
            if (logTotalSteps) System.out.println("total steps of generation " + i +" : " + stepSum);
        }
    }
    
    void simulateSomeGenerations(int generationIndex) throws IOException,  InterruptedException {
        Jerry_Kevin[] parents = new Jerry_Kevin[100];

        // lade population                      (im ordner sind 100 bots definiert)
        for (int i = 0; i < 100; i++) {
            parents[i] = new Jerry_Kevin(new File("models/generation"+ (generationIndex -1) + "/Jerry" + i));
        }
        
        // erstelle 200 Kinder aus den 100 bots
        Jerry_Kevin[] children = new Jerry_Kevin[200];
        for (int i = 0; i < 200; i++) {
            children[i] = new Jerry_Kevin(parents[i/2], 1);
        }

        
        List<Jerry_Kevin> list = Arrays.asList(children);
        Collections.shuffle(list);

        Iterator<Jerry_Kevin> it = list.iterator();
        Jerry_Kevin[] winners = new Jerry_Kevin[100];

        int i = 0;
        while (it.hasNext()) {
            winners[i++] = contest(it.next() , it.next());
        }

        i = 0;
        File dir = new File("models/generation" + generationIndex);
        dir.mkdir();
        for (Jerry_Kevin winner : winners) {
            winner.save(new File("models/generation"+ generationIndex + "/Jerry" + i++));
        }
    }

    int stepcounter = 0;
    int stepSum = 0;
    
    Jerry_Kevin contest(Jerry_Kevin a, Jerry_Kevin b) throws  InterruptedException{
        SnakeGame game = new SnakeGame(
                new Coordinate(20,20),// größe von spielfeld
                new Coordinate(5,10), // schlange 1
                Direction.DOWN,
                new Coordinate(15,10),// schlange 2
                Direction.UP,4,
                a, b
        );

        stepcounter = 0;
        while (game.runOneStep()){
            stepcounter++;
            if (stepcounter > 100) {
                break;
            }
        }
        stepSum += stepcounter;

        if (logStepsOfContest) System.out.println("steps: " + stepcounter);
        String score = game.gameResult; // beispiel "1 - 0"
        Jerry_Kevin winner = (score.charAt(0) == '1') ? a : b;
        return winner;
    }
}