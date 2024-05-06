package eu.ostrzyciel.rb_load_time.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Vector;

public class LinuxCommand {
    public static Vector<String> run(File directory, String... command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            if (directory != null) {
                processBuilder.directory(directory);
            }

            Process process = processBuilder.start();
            Vector<String> output = new Vector<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            reader.lines().forEach(output::add);
            process.waitFor();
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void print(Vector<String> output) {
        for (String o : output) {
            System.out.println(o);
        }
    }

    public static Vector<String> runAndPrint(File directory, String... command) {
        Vector<String> output = run(directory, command);
        print(output);
        return output;
    }
}
