package Test.mybots;

import org.junit.jupiter.api.Test;
import snakes.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JerryTest {
    static public Path modelPath = Jerry.modelPath;
    private int totalMatchesPlayed = 0;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        if (!modelPath.toFile().exists())
            modelPath.toFile().mkdir();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void chooseDirection() {
    }

    @org.junit.jupiter.api.Test
    void generatePopulation() throws FileNotFoundException {
        Jerry[] bots = new Jerry[100];
        for (int i = 0; i < bots.length; i++) {
            bots[i] = new Jerry();
        }
        saveGeneration(bots, 0);
    }


    private boolean logStepsOfContest = false;
    private boolean generationStatistic = true;
    private int snapshotInterval = 50; // speichere jede 2 ,0. generation

    @Test
    void nnTest() {
        int pop = 200;
        int trainingEvaluations = 20;
        Jerry[] bots = new Jerry[pop];
//        for (int i = 0; i < bots.length; i++) {
//            bots[i] = new Jerry();
//            bots[i].layers = new Jerry.Layer[]{
//                    new Jerry.Layer(10, 6, "tanh"),
//                    new Jerry.Layer(5, 10, "tanh"),
//                    new Jerry.Layer(5, 5, "tanh"),
//                    new Jerry.Layer(5, 5, "tanh"),
//                    new Jerry.Layer(3, 5, "lin")
//            };
//        }
//        System.out.println(String.format(String.format("average Score;best bot evaluation;pop:%d;layers:%d;training evaluations:%d", pop, bots[0].layers.length, trainingEvaluations)));

        // Training
        for (int i = 0; i < 300; i++) {
            Jerry[] population = generateChildren2(bots, 4);

            // let bots try problem
            float botScore;
            scoreSum = 0;
            for (Jerry bot : population) {
                botScore = evaluateBot(bot, trainingEvaluations);
                scoreSum += botScore; // für statistik
            }

            // take best
            // sortiere population nach score. beste am anfang
            Iterator<Jerry> it = Arrays.stream(population).sorted((a, b) -> {
                if (a.score > b.score) return 1;
                else if (a.score < b.score) return -1;
                return 0;
            }).iterator();
            for (int j = 0; j < bots.length; j++) {
//                bots[j] = new Jerry(it.next(), 1f - (i/100f));
                bots[j] = new Jerry(it.next(), 0.1f);
            }
            System.out.println(String.format("%f;%f", scoreSum / population.length, evaluateBot(bots[0],trainingEvaluations*10)));
        }
//        System.out.println(String.format("score of best bot: %f", bots[0].score));
//        System.out.println(String.format("best bot evaluation after 100 iterations: %f", evaluateBot(bots[0], trainingEvaluations)));
    }

    private float evaluateBot(Jerry bot, int iterations){
        float sum = 0;
        for (int i = 0; i < iterations; i++) {
            float x = (float) random() + 2;
            float y = (float) random() + 2;
            float[] input = {
                    x, y,
                    x * x, y * y,
                    (float) log(x), (float) log(y)
            };

            float correctSolution = target4(x,y);
            // let bots try problem
            float prediction;
            float[] output = bot.evaluate(input);
            prediction = (float) (output[0]+ pow(2, output[1]) + pow(output[2], 3));
//            prediction = (prediction>0)? prediction * prediction: -(prediction * prediction);
            bot.score = cost(prediction, correctSolution);
            sum += bot.score; // für statistik
        }
        bot.score = sum / iterations;
        return sum / iterations;
    }

    private float target(float x, float y) {
        return (float) ((5 * x + Math.log(2 * y) - 4 * x * sin(x * y) * y) / sqrt(x + y));
    }
    private float target2(float x, float y) {
        return (float) ((5 * x + (2 * y) - 4 * x * (x * y) * y) / sqrt(x + y));
    }
    private float target3(float x, float y) {
        return (float) ((5 * x + (2 * y) - 4 * x * (x * y) * y) / sqrt(x + y)) + 10;
    }
    private float target4(float x, float y) {
        return (float) ((5 * x + (2 * y) - 4 * x * (x * y) * y) / sqrt(x + y))/y + 10;
    }
    private float target5(float x, float y) {
        return (float) ((5 * x + (2 * y) - 4 * x * (x * y) * y) / sqrt(x + y))/4 + 10;
    }
    private float target6(float x, float y) {
        return (float) ((5 * x + (2 * y) - 4 * x * (x * y) * y) / sqrt(x + y))*y + 10;
    }
    private float target7(float x, float y) {
        return (float) ((5 * x + (2 * y) - 4 * x * (x * y) * y) / sqrt(x + y))/(y+2) + 10;
    }

    private float cost(float pred, float solution) {
        float diff = pred - solution;
        return abs(diff);
    }

    @org.junit.jupiter.api.Test
    void makeRealProgress() throws IOException, InterruptedException {
        int fromGeneration = 2050;
        int numGenerations = 200;
        System.out.println("generation;mutationsstärke;avg steps of generation;average score");
        Jerry[] population = loadGeneration(fromGeneration);
        int i;
//        for (i = fromGeneration; i <= fromGeneration + numGenerations - (numGenerations % snapshotInterval); i++) {
        for (i = fromGeneration; true; i++) {
            stepSum = 0;
            scoreSum = 0;
            totalMatchesPlayed = 0;
//            Jerry.mutationsStärke = (numGenerations-i)/(float)numGenerations;
//            Jerry.mutationsStärke = (float) pow(Jerry.mutationsStärke, 4) ;
            Jerry.mutationsStärke = 0;
            Jerry.mutationsStärke += 0.1f;
//            Jerry.mutationsStärke *= pow(0.9f, i);

            try {
                population = simulateSomeGeneration(population);
            } catch (InterruptedException e) {
                e.printStackTrace();
                saveGeneration(population, i);
            }
            if (generationStatistic)
                System.out.println(String.format("%d;%f;%.4f;%.2f", i, Jerry.mutationsStärke,  (float) stepSum / totalMatchesPlayed, scoreSum / totalMatchesPlayed));

            if (i % snapshotInterval == 0) {
                saveGeneration(population, i);
            }
        }
//        saveGeneration(population, i);
    }

    Jerry[] simulateSomeGeneration(Jerry[] parents) throws IOException, InterruptedException {
        // erstelle 200 Kinder aus den 100 bots
        Jerry[] children = generateChildren2(parents, 2);
//        for (int i = 0; i < 4; i++) {
//            children[i].mutateRandomValue();
//            children[i].mutateRandomValue();
//            children[i].mutateRandomValue();
//            children[i].mutateRandomValue();
//        }

        List<Jerry> list = Arrays.asList(children);
        Collections.shuffle(list);

        Iterator<Jerry> winner = list.iterator();

        int i = 0;
        assertEquals(0, list.size()%2);
        ArrayList<Jerry> winners = new ArrayList<>(list.size()/2);
        while (winner.hasNext()) {
            winners.add(contest(winner.next(), winner.next()));
        }
        winners.sort((a,b)-> Float.compare(b.score, a.score));
        winner = winners.iterator();
//        list.sort((a, b) -> Float.compare(b.score, a.score));
//        it = list.iterator();
        Jerry[] newpop = new Jerry[parents.length];
        for (int w = 0; w < newpop.length; w++) {
            newpop[w] = winner.next();
        }
        return newpop;
    }
    Jerry[] tournamentLikeGeneration(Jerry[] parents) throws InterruptedException {
        // Beispiel: erstelle 400 Kinder aus den 100 bots
        Jerry[] children = generateChildren2(parents, 2);
//        for (int i = 0; i < 4; i++) {
//            children[i].mutateRandomValue();
//            children[i].mutateRandomValue();
//            children[i].mutateRandomValue();
//            children[i].mutateRandomValue();
//        }

        List<Jerry> list = Arrays.asList(children); // length 400
        Collections.shuffle(list);
        Iterator<Jerry> luckyOne = list.iterator();

        int i = 0;
        assertEquals(0, list.size()%2);
//        ArrayList<Jerry> winners = new ArrayList<>(list.size()/4);
        Jerry[] newpop = new Jerry[parents.length]; // length 100
        int popI = 0;
        ArrayList<Jerry> secondChance = new ArrayList<>(list.size()/2); // 200
        while (luckyOne.hasNext()) { // 100 wiederholungen
            Jerry win1 = contest(luckyOne.next(), luckyOne.next());
            Jerry win2 = contest(luckyOne.next(), luckyOne.next());
            Jerry finalWin = contest(win1, win2);
            newpop[popI++] = finalWin;
            secondChance.add((finalWin == win1)? win2 : win1); // the other gets a chance to qualify by score
        }
        secondChance.sort((a,b)-> Float.compare(b.score, a.score));
        luckyOne = secondChance.iterator();
//        list.sort((a, b) -> Float.compare(b.score, a.score));
//        it = list.iterator();
        while (popI < newpop.length) {
            newpop[popI++] = luckyOne.next();
        }
        assertEquals(newpop.length, popI);
        return newpop;
    }

    @org.junit.jupiter.api.Test
    void singleGeneration() throws IOException, InterruptedException {
        Jerry[] population = getLastGeneration();
        tournamentLikeGeneration(population);
    }

    //    private Jerry[] generateChildren(Jerry[] parents) {
//        Jerry [] children = new Jerry[parents.length * 2];
//        for (int i = 0; i < children.length; i++) {
//            children[i] = new Jerry(parents[i/2], 1);
//        }
//        return children;
//    }
    private Jerry[] generateChildren2(Jerry[] parents, int multiplier) {

        Jerry[] children = new Jerry[parents.length * multiplier];
        for (int i = 0; i < children.length; i += multiplier) {
            for (int j = 0; j < multiplier; j++) {
                double otherParentIndex = (random()* random() * parents.length);
//                otherParentIndex *= parents.length;
                children[i + j] = new Jerry(parents[i / multiplier], parents[(int) (otherParentIndex % parents.length)]);
            }
        }
        return children;
    }



    int stepcounter = 0;

    int stepSum = 0;

    float scoreSum = 0;

    Jerry contest(Jerry a, Jerry b) throws InterruptedException {
        final int matches = 5;
        totalMatchesPlayed += matches;
        a.score = 0;
        b.score = 0;
        Jerry[] bots = {a, b};
        int aWins = 0, bWins = 0;

        for (int i = 0; i < matches; i++) {
            bots[0] = (i % 2 == 0) ? a : b;
            bots[1] = (i % 2 == 0) ? b : a;
            int height = 20;
            int width = 20;
            snakes.Coordinate mazeSize = new snakes.Coordinate(width, height);
//            SnakeGame game = new SnakeGame(
//                    mazeSize,// größe von spielfeld
//                    getRandom(mazeSize), // schlange 1
//                    Direction.UP,
//                    new Coordinate(1, 7),// schlange 2
//                    Direction.UP, 4,
//                    bots[0],
//                    bots[1]
//            );
            snakes.SnakeGame game = snakes.SnakeGame.makeRandom(
                    bots
            );

            stepcounter = 0;
            while (game.runOneStep()) {
                stepcounter++;
                if (stepcounter > 100) {
                    break;
                }
            }
            stepSum += stepcounter;

            if (logStepsOfContest) System.out.println("steps: " + stepcounter);
            if (bots[0] == a) {
                aWins += Integer.parseInt(game.gameResult.split(" ")[0]);
                bWins += Integer.parseInt(game.gameResult.split(" ")[2]);
            }else{
                aWins += Integer.parseInt(game.gameResult.split(" ")[2]);
                bWins += Integer.parseInt(game.gameResult.split(" ")[0]);
            }
//            score[0] += game.snake0.body.size();
//            score[1] += game.snake1.body.size();
        }
//        Jerry winner = (a.score > b.score) ? a : b;
        Jerry winner = (aWins > bWins) ? a : b;
        scoreSum += (a.score + b.score) / 2;
        return winner;
    }

    private float manhattan(snakes.Coordinate a, snakes.Coordinate b) {
        return abs(a.x - b.x) + abs(a.y - b.y);
    }

    Jerry[] getLastGeneration() throws FileNotFoundException {
        File f = modelPath.toFile();
        File[] all = f.listFiles();
        File highest = all[0];
        for (File file : all) {
            if (file.compareTo(highest) > 0)
                highest = file;
        }
        return loadGeneration(highest); // nehme an sie sind sortiert
    }

    private void saveGeneration(Jerry[] population, int generationIndex) throws FileNotFoundException {
        int i;
        i = 0;
        String generationPath = modelPath + String.format("/generation%04d", generationIndex);
        File dir = new File(generationPath);
        dir.mkdir();
        for (Jerry winner : population) {
            winner.save(new File(generationPath + String.format("/Jerry%04d", i++)));
        }
    }

    private Jerry[] loadGeneration(int generationIndex) throws FileNotFoundException {
        return loadGeneration(new File(String.format("%s/generation%04d", modelPath, generationIndex)));
    }

    private Jerry[] loadGeneration(File directory) throws FileNotFoundException {
        File[] population = directory.listFiles();
        Jerry[] bots = new Jerry[population.length];
        for (int i = 0; i < population.length; i++) {
            bots[i] = new Jerry(population[i]);
        }
        return bots;
    }

    @Test
    void save() throws IOException {
        Jerry j = new Jerry();
        File testJerry = new File("testJerry");
        j.save(testJerry);
        Jerry j2 = new Jerry(testJerry);
        File testJerry2 = new File("testJerry2");
        j2.save(testJerry2);
        InputStream f1 = new FileInputStream(testJerry);
        InputStream f2 = new FileInputStream(testJerry2);
        while (f1.available() > 0) {
            assertEquals(f1.read(), f2.read());
        }
    }
}