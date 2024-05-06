package eu.ostrzyciel.rb_load_time.benchmark.virtuoso;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.ostrzyciel.rb_load_time.benchmark.ExperimentContext;
import eu.ostrzyciel.rb_load_time.benchmark.ExperimentInput;
import eu.ostrzyciel.rb_load_time.benchmark.LinuxCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class VirtuosoInput extends ExperimentInput<String> {
    private static final Path VIRTUOSO_PATH = Path.of("/opt/virtuoso-opensource/bin");

    long pid = 0;

    private static String vPath(String path) {
        return Path.of(VIRTUOSO_PATH.toString(), path).toString();
    }

    private void runIsql(String command) {
        LinuxCommand.run(
                new File(path2StoreFolder),
                vPath("isql"),
                "-H",
                "localhost",
                "exec=\"" + command + "\""
        );
    }

    protected VirtuosoInput(ExperimentContext experimentContext) {
        super(experimentContext);
    }

    @Override
    protected String initializeEngine() {
        LinuxCommand.runAndPrint(new File(path2StoreFolder), "touch", "virtuoso.ini");
        System.out.println("Shutting down Virtuoso if already running");
        this.close("");
        System.out.println("Starting Virtuoso");
        LinuxCommand.runAndPrint(
                new File(path2StoreFolder),
                vPath("virtuoso-t"),
                "virtuoso.ini"
        );
        System.out.println("Waiting for Virtuoso to start");
        LinuxCommand.runAndPrint(
                null,
                "sh",
                "-c",
                """
                        until nc -z localhost 1111; do
                            printf '.'
                            sleep 3
                        done"""
        );
        System.out.println("Virtuoso started");
        var sPid = LinuxCommand.run(null, "pgrep", "virtuoso-t").getFirst().trim();
        pid = Long.parseLong(sPid);
        return sPid;
    }

    @Override
    protected void doInput(File fileInput, String s) {
        runIsql("ld_dir('" + path2DataFolder + "', '" + fileInput.getName() + "', 'http://example.com');\n" +
                        "rdf_loader_run();\n" +
                        "checkpoint;\n" +
                        "delete from db.dba.load_list;"
                );
    }

    @Override
    protected void close(String s) {
        runIsql("shutdown;");
    }

    @Override
    protected long getPid(String engine) {
        return pid;
    }

    public static void main(String[] args) {
        String path2File = args[0];
        File file = new File(path2File);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ExperimentContext experimentContext = objectMapper.readValue(file, ExperimentContext.class);
            VirtuosoInput virtuosoInput = new VirtuosoInput(experimentContext);
            virtuosoInput.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}