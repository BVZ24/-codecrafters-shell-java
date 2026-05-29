package base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Tauminal {
    Stage stage;
    Map<String, Integer> variables;
    private final Map<String, Procedure> COMMANDS = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in); // ✅ Created once

    @FunctionalInterface
    interface Procedure {
        void run(String[] args) throws Exception;
    }

    public Tauminal() {
        this.stage = Stage.LOOP;
        this.variables = new HashMap<>(); // ✅ Mutable map, set() won't crash
        COMMANDS.put("exit", args -> System.exit(0));
        COMMANDS.put("echo", args -> System.out.println(String.join(" ", args)));
        COMMANDS.put("type", this::typeCommand);
        COMMANDS.put("pwd", args -> System.out.println(
            Paths.get("").toAbsolutePath().normalize().toString() // ✅ pwd
        ));
    }

    public void run() throws Exception {
        while (stage == Stage.LOOP) {
            stage = Stage.READ;
            String input = read();
            stage = Stage.EXECUTE;
            execute(input);
            stage = Stage.LOOP;
        }
    }

    public String read() {
        System.out.print("$ ");
        return scanner.nextLine(); // ✅ Reuses the single Scanner instance
    }

    public void execute(String command) throws Exception {
        String[] tokens = command.split(" ");
        String commandName = tokens[0];
        String[] commandArgs = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (COMMANDS.containsKey(commandName)) {
            COMMANDS.get(commandName).run(commandArgs);
            return;
        }

        // ✅ Use ProcessBuilder instead of Runtime.exec
        String path = Main.getPath(commandName, Files::isExecutable);
        if (path == null) {
            System.out.println(commandName + ": command not found");
        } else {
            ProcessBuilder pb = new ProcessBuilder(tokens);
            pb.inheritIO();                    // ✅ Handles stderr too
            pb.start().waitFor();              // ✅ Waits for process to finish
        }
    }

    void typeCommand(String[] args) {
        if (args.length != 1) {
            System.out.println("Invalid argument");
            return;
        }
        String target = args[0];
        if (COMMANDS.containsKey(target)) {
            System.out.println(target + " is a shell builtin");
            return;
        }
        String path = Main.getPath(target, Files::isExecutable);
        if (path != null) System.out.println(target + " is " + path);
        else System.out.println(target + ": not found");
    }

    public void set(String name, int value) {
        variables.put(name, value); // ✅ remove() before put() was redundant anyway
    }

    public int get(String name) {
        return variables.get(name);
    }
}
