package eu.ostrzyciel.rb_load_time.benchmark.rdf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.lmdb.LmdbStore;
import org.eclipse.rdf4j.sail.lmdb.config.LmdbStoreConfig;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import eu.ostrzyciel.rb_load_time.benchmark.ExperimentContext;
import eu.ostrzyciel.rb_load_time.benchmark.ExperimentInput;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Anh Le-Tuan
 * Email: anh.letuan@tu-berlin.de
 */
public class RDF4JInput extends ExperimentInput<RepositoryConnection> {

    private Repository repository;

    protected RDF4JInput(ExperimentContext experimentContext) {
        super(experimentContext);
    }

    @Override
    protected RepositoryConnection initializeEngine() {
        File dataDir = new File(this.path2StoreFolder);
        String indexes = "spoc,ospc,psoc";

        Sail store;
        if (this.getEngine().endsWith("lmdb")) {
            // Note: auto-grow enabled by default
            // https://rdf4j.org/documentation/programming/lmdb-store/
            var config = new LmdbStoreConfig()
                    .setTripleIndexes(indexes);
            store = new LmdbStore(dataDir, config);
        } else {
            store = new NativeStore(dataDir, indexes);
        }

        this.repository = new SailRepository(store);
        repository.isInitialized();
        return repository.getConnection();
    }

    @Override
    protected void doInput(File fileInput, RepositoryConnection connection) {
        try {
            connection.begin();

            try (FileReader fileReader = new FileReader(fileInput)) {
                connection.add(fileReader, "", RDFFormat.NTRIPLES);
            }

            connection.commit();
        } catch (RepositoryException | RDFParseException | IOException e) {
            if (connection.isActive()) connection.rollback();
            throw new RuntimeException(e.toString());
        }
    }

    @Override
    protected void close(RepositoryConnection connection) {
        try {
            connection.commit();
            connection.close();
            repository.shutDown();
        } catch (RepositoryException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public static void main(String[] args) {
        String path2File = args[0];
        File file = new File(path2File);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ExperimentContext experimentContext = objectMapper.readValue(file, ExperimentContext.class);
            RDF4JInput rdf4JInput = new RDF4JInput(experimentContext);
            rdf4JInput.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
