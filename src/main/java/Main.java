import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
0
        Scanner scanner = new Scanner(System.in);
        0000

        while (true) {

            System.out.print("$ ");

            String input = scanner.nextLine();
	     if (input.equals("exit")) {

                break;

            }



            System.out.println(input + ": command not found");

        }

    }

}


