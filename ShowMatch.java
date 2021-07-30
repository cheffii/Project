import mybots.Jerry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

import static mybots.Jerry.modelPath;

public class ShowMatch {
    static Scanner inp = new Scanner(System.in);

    static Random rand = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {
        snakes.SnakesWindow window = null;
        while(true) { // Exception als Abruchbedingung
            snakes.Coordinate mazeSize = new snakes.Coordinate(14, 14);
            snakes.Coordinate head0 = new snakes.Coordinate(2, 2);
            snakes.Direction tailDirection0 = snakes.Direction.DOWN;
            snakes.Coordinate head1 = new snakes.Coordinate(5, 5);
            snakes.Direction tailDirection1 = snakes.Direction.UP;
            int snakeSize = 3;

            // select model:
            System.out.println("models:");
            File[] modelList = modelPath.getParent().toFile().listFiles();
            int i = 0;
            for (File file : modelList) {
                System.out.println(String.format("[%d]: %s", i++, file.getName()));
            }
            String line = inp.nextLine();
            if(line.length() != 0) {
                modelPath = modelList[Integer.parseInt(line)].toPath();
            }

            // schließe vorheriges Fenster, damit sie sich nicht ewig stapeln
            if (window != null)
                window.closeWindow();


            System.out.print("wähle generation: ");
            System.out.flush();
            line = inp.nextLine();
            Path selectedGeneration;
            if (line.length() != 0){
                selectedGeneration = Paths.get(modelPath.toString(), String.format("generation%04d", Integer.parseInt(line)));
            }
            else{
                selectedGeneration = Files.list(modelPath)
                        .max((f , w)->{
                            int id1 = Integer.parseInt(f.toString().substring((modelPath + "\\generation").length()));
                            int id2 = Integer.parseInt(w.toString().substring((modelPath + "\\generation").length()));
                            return id1 - id2;
                        })
                        .orElseThrow(NoSuchElementException::new);
            }
            //System.out.print("welche Generation: ");
            System.out.println("aus "+ selectedGeneration);
            System.out.println("Select Jerrys:");
            snakes.Bot bot0 = new Jerry(new File(String.format("%s/Jerry%04d", selectedGeneration, getInt(rand.nextInt(50)))));
            snakes.Bot bot1 = new Jerry(new File(String.format("%s/Jerry%04d", selectedGeneration, getInt(rand.nextInt(50)))));
            snakes.SnakeGame game = snakes.SnakeGame.makeRandom(new snakes.Bot[]{bot0, bot1});
            window = new snakes.SnakesWindow(game);
            Thread t = new Thread(window);
            t.start();
            t.join();

//            Thread.sleep(5000); // to allow users see the result
//            window.closeWindow();
        }
    }

    private static int getInt(int standard) {
        String line = inp.nextLine();
        if (line.length() == 0) {
            System.out.println(standard);
            return standard;
        }
        return Integer.parseInt(line);
    }
}
