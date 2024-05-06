package eu.ostrzyciel.rb_load_time.benchmark;

import java.io.File;
import java.util.Arrays;
/**
 * Created by Anh Le-Tuan
 * Email: anh.letuan@tu-berlin.de
 * <p>
 * Date: 05.01.20
 */
public abstract class ExperimentInput<Engine> extends Experiment {

    private final int batchSize;

    protected ExperimentInput(ExperimentContext experimentContext) {
        super(experimentContext);
        this.batchSize = experimentContext.getBatchSize();
    }

    @Override
    public void run() {
        LinuxCommand.run(null, "rm", "-rf", this.getPath2StoreFolder());
        LinuxCommand.run(null, "mkdir", "-p", this.getPath2StoreFolder());
        Engine engine = initializeEngine();
        File file = new File(path2DataFolder);

        resultWriter.write("count\tsize\tstart\tend\tcpuTimeS\tvmSizeKb\tvmRssKb\tstorageSizeKb");

        int count = 1;
        File[] files = file.listFiles();
        Arrays.sort(files);

        for (File input : files) {
            if (input.isHidden()) continue;
            if (input.isDirectory()) continue;
            if (input.getName().contains(".txt")) continue;

            long size = (long) count * batchSize;
            long start = StopWatch.getTimeStampMillis();

            doInput(input, engine);

            long end = StopWatch.getTimeStampMillis();
            long duration = end - start;

            var stats = getEngineStats(engine);

            System.out.println("Inserting file: " + input.getName() + " storageSize " + size +  " speed " +  (batchSize * 1000f) / duration + " tps" + " in " + duration + "ms");
            String toWrite = count +
                    "\t" +
                    size +
                    "\t" +
                    start +
                    "\t" +
                    end +
                    "\t" +
                    stats;

            resultWriter.write(toWrite);
            count++;
        }

        close(engine);
    }

    private EngineStats getEngineStats(Engine engine) {
        long pid = getPid(engine);
        var psOut = LinuxCommand.run(null, "ps", "-p", Long.toString(pid), "-o", "cputimes,vsize,rss");
        var psSplit = Arrays.stream(psOut.get(1).split("\\s+")).filter(s -> !s.isEmpty()).toArray(String[]::new);

        // Note: we are measuring the actual storage size on disk, not the length of the file
        // This makes a difference for sparse files.
        var duOut = LinuxCommand.run(null, "du", "-d0", this.getPath2StoreFolder());
        var storageSizeKb = Long.parseLong(duOut.get(0).split("\\s+")[0]);

        return new EngineStats(
                Long.parseLong(psSplit[0]),
                Long.parseLong(psSplit[1]),
                Long.parseLong(psSplit[2]),
                storageSizeKb
        );
    }

    protected abstract Engine initializeEngine();

    protected abstract void doInput(File fileInput, Engine engine);

    protected long getPid(Engine engine) {
        return ProcessHandle.current().pid();
    }

    protected abstract void close(Engine engine);
}