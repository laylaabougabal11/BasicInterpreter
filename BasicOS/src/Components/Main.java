package Components;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

        public static void main(String[] args) throws IOException {
                // test the project with program_1.txt

                Interpreter interpreter = new Interpreter();

                Scanner scanner = new Scanner(System.in);
                System.out.println("Enter 1st delay in seconds: ");
                int firstDelay = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter 1st program name: ");
                final String firstProgram = scanner.nextLine();

                System.out.println("Enter 2nd delay in seconds: ");
                int secondDelay = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter 2nd program name: ");
                final String secondProgram = scanner.nextLine();

                System.out.println("Enter 3rd delay in seconds: ");
                int thirdDelay = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter 3rd program name: ");
                final String thirdProgram = scanner.nextLine();

                // Run code at time 0 (0 seconds)
                interpreter.addProgram(
                                System.getProperty("user.dir") + File.separator + "Data" + File.separator + firstProgram
                                                + ".txt",
                                firstDelay);

                // Run code at time 1 (1 second)
                interpreter.addProgram(
                                System.getProperty("user.dir") + File.separator + "Data" + File.separator
                                                + secondProgram
                                                + ".txt",
                                secondDelay);

                // Run code at time 4 (4 seconds)
                interpreter.addProgram(
                                System.getProperty("user.dir") + File.separator + "Data" + File.separator + thirdProgram
                                                + ".txt",
                                thirdDelay);

                System.out.println();

                interpreter.execute();

        }

}
