package eu.ostrzyciel.rb_load_time.benchmark;

public class EngineStats {
    // ps -p 1 -o cputimes,rss,vsize
    private final long cpuTimeS;
    private final long vmSizeKb;
    private final long vmRssKb;
    private final long storageSizeKb;

    public EngineStats(long cpuTimeS, long vmSizeKb, long vmRssKb, long storageSizeKb) {
        this.cpuTimeS = cpuTimeS;
        this.vmSizeKb = vmSizeKb;
        this.vmRssKb = vmRssKb;
        this.storageSizeKb = storageSizeKb;
    }

    public long getCpuTimeS() {
        return cpuTimeS;
    }

    public long getVmSizeKb() {
        return vmSizeKb;
    }

    public long getVmRssKb() {
        return vmRssKb;
    }

    public long getStorageSizeKb() {
        return storageSizeKb;
    }

    @Override
    public String toString() {
        // Tab-separated output
        return cpuTimeS + "\t" + vmSizeKb + "\t" + vmRssKb + "\t" + storageSizeKb;
    }
}
