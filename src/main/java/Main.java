import java.io.File;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final Set<String> BUILTINS = Set.of("echo", "exit", "type");

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            String[] parts = input.split(" ");
            String cmd = parts[0];

            if (cmd.equals("exit")) {
                break;
            } else if (cmd.equals("echo")) {
                System.out.println(input.substring(5));
            } else if (cmd.equals("type")) {
                String target = parts[1];
                if (BUILTINS.contains(target)) {
                    System.out.println(target + " is a shell builtin");
                } else {
                    String path = findInPath(target);
                    if (path != null) {
                        System.out.println(target + " is " + path);
                    } else {
                        System.out.println(target + ": not found");
                    }
                }
            } else {
                String path = findInPath(cmd);
                if (path != null) {
                    ProcessBuilder pb = new ProcessBuilder(parts);
                    pb.inheritIO();
                    pb.start().waitFor();
                } else {
                    System.out.println(cmd + ": command not found");
                }
            }
        }
    }

    private static String findInPath(String cmd) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null) return null;
        for (String dir : pathEnv.split(":")) {
            File file = new File(dir, cmd);
            if (file.exists() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }
}
