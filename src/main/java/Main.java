import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    @FunctionalInterface
    interface Procedure {
        void run(String[] args) throws Exception;
    }

    private final Scanner scanner = new Scanner(System.in);
    private final Map<String, Procedure> COMMANDS = new HashMap<>();
    private final Map<String, Integer> variables = new HashMap<>();

    public Main() {
        COMMANDS.put("exit", args -> System.exit(0));
        COMMANDS.put("echo", args -> System.out.println(String.join(" ", args)));
        COMMANDS.put("type", this::typeCommand);
        COMMANDS.put("pwd", args -> System.out.println(
            System.getProperty("user.dir")
        ));
        COMMANDS.put("cd", args -> {
            if (args.length != 1) {
                System.out.println("cd: missing argument");
                return;
            }
            File dir = new File(args[0]);
            if (!dir.exists() || !dir.isDirectory()) {
                System.out.println("cd: " + args[0] + ": No such file or directory");
                return;
            }
            System.setProperty("user.dir", dir.toPath().toAbsolutePath().normalize().toString());
        });
    }

    public void run() throws Exception {
        while (true) {
            System.out.print("$ ");
            String input = scanner.nextLine();
            execute(input);
        }
    }

    private void execute(String command) throws Exception {
        String[] tokens = command.split(" ");
        String commandName = tokens[0];
        String[] commandArgs = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (COMMANDS.containsKey(commandName)) {
            COMMANDS.get(commandName).run(commandArgs);
            return;
        }

        String path = findInPath(commandName);
        if (path == null) {
            System.out.println(commandName + ": command not found");
        } else {
            ProcessBuilder pb = new ProcessBuilder(tokens);
            pb.inheritIO();
            pb.start().waitFor();
        }
    }

    private void typeCommand(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid argument");
            return;
        }
        String target = args[0];
        if (COMMANDS.containsKey(target)) {
            System.out.println(target + " is a shell builtin");
            return;
        }
        String path = findInPath(target);
        if (path != null) System.out.println(target + " is " + path);
        else System.out.println(target + ": not found");
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

    public void set(String name, int value) { variables.put(name, value); }
    public int get(String name) { return variables.get(name); }

    public static void main(String[] args) throws Exception {
        new Main().run();
    }
}
