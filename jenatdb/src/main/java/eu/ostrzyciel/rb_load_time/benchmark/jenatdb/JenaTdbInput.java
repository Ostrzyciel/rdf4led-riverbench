package eu.ostrzyciel.rb_load_time.benchmark.jenatdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.system.Txn;
import org.apache.jena.tdb2.TDB2Factory;
import eu.ostrzyciel.rb_load_time.benchmark.ExperimentContext;
import eu.ostrzyciel.rb_load_time.benchmark.ExperimentInput;

import java.io.File;
import java.io.IOException;

/**
 * Created by Anh Le-Tuan
 * Email: anh.letuan@tu-berlin.de
 */
public class JenaTdbInput extends ExperimentInput<Dataset> {

    protected JenaTdbInput(ExperimentContext experimentContext) {
        super(experimentContext);
    }

    @Override
    protected Dataset initializeEngine() {
        return TDB2Factory.connectDataset(this.path2StoreFolder);
    }

    @Override
    protected void doInput(File fileInput, Dataset ds) {
        Txn.executeWrite(ds, () -> RDFDataMgr.read(ds, fileInput.toURI().toString(), Lang.NTRIPLES));
    }

    @Override
    protected void close(Dataset ds) {
        ds.close();
    }

    public static void main(String[] args) {
        String path2File = args[0];
        File file = new File(path2File);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ExperimentContext experimentContext = objectMapper.readValue(file, ExperimentContext.class);
            JenaTdbInput jenaTdbInput = new JenaTdbInput(experimentContext);
            jenaTdbInput.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
