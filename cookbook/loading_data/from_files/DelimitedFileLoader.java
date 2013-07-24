import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import org.voltdb.CLIConfig;
import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcedureCallback;

public class DelimitedFileLoader {

    final LoaderConfig config;
    Client client;
    private long requests = 0;
    private static AtomicLong successes = new AtomicLong();
    private static AtomicLong errors = new AtomicLong();
    private static long maxErrors = 50;



    // define command line parameters
    static class LoaderConfig extends CLIConfig {
        @Option(desc = "Comma separated list of the form server[:port] to connect to.")
        String servers = "localhost";

        @Option(desc = "Filename of file to be loaded.")
        String filename = "data/NYSE.csv";

        @Option(desc = "Insert Procedure.")
        String procedure = "SYMBOLS.insert";

        @Option(desc = "Skip lines (for header)")
        int skiplines = 1;

    }

    // Callback class to track results from async procedure calls
    static class LoaderCallback implements ProcedureCallback {
	@Override
	public void clientCallback(ClientResponse cr) {
	    if (cr.getStatus() == ClientResponse.SUCCESS) {
		successes.incrementAndGet();
	    } else {
		long total_errors = errors.incrementAndGet();
		if (total_errors >= maxErrors) {
		    System.err.println("exceeded maximum of " + maxErrors + " database errors.");
		    System.exit(-1);
		}
		System.out.println("DATABASE ERROR: " + cr.getStatusString());
	    }
	}
    }


    // constructor
    public DelimitedFileLoader(LoaderConfig config) throws Exception {
        this.config = config;

	connect();
	
    } 


    // connect to VoltDB
    private void connect() throws IOException {
	String[] hostnames = config.servers.split(",");
	if (client == null) {
	    ClientConfig config = new ClientConfig();
	    client = ClientFactory.createClient(config);
	    if (hostnames != null && hostnames.length > 0) {
		for (String host : hostnames) {
                    System.out.println("connecting to " + host.trim());
		    client.createConnection(host.trim());
			
		}
	    } else {
		throw new IOException("No servers specified");
	    }
	}
    }

    
    // load file
    protected void loadFile(String filename) throws Exception {
	int counter = 0;
	String line = null;
	try {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
            System.out.println("reading from file: " + filename);

	    while ((line = reader.readLine()) != null) {
		counter++;
                if (counter > config.skiplines) {
                    loadRecord(line);
                }

		if (counter % 100000 == 0)
		    System.out.println("  read " + counter + " lines");
	    }
	    reader.close();
            System.out.println("finished loading " + filename + " (loaded " + Integer.toString(counter) + " lines)");

	} catch (Exception e) {
	    System.err.println("Exception in loadFlatFile on reading line " + counter + " of " + filename);
	    throw new Exception(e);
	}
    }

    protected void loadRecord(String iline) throws Exception {

        // callProcedure can take an Object[] for specific types
        // can be a String[], the server will try to cast String values to appropriate types
        Object[] line = iline.split("\\|");
        //System.out.println(iline);
        //System.out.println(line[0] + "|" + line[1] + "|" + line[2]);
	ProcedureCallback cb = new LoaderCallback();
	client.callProcedure(cb,
                             config.procedure,
                             line
                             );

    }


    public void printResults() throws Exception {
	client.drain();
	System.out.println();
	System.out.println("Transaction Results");
	System.out.printf(
            " - %,9d Requests sent to VoltDB\n"
            + " - %,9d Committed\n"
            + " - %,9d Rolled Back due to failure\n"
            + "\n"
            , requests
            , successes.get()
            , errors.get()
			  );
    }

    public void close() throws Exception {
	client.drain();
	client.close();
    }



    public static void main(String[] args) throws Exception {

        LoaderConfig config = new LoaderConfig();
        config.parse(DelimitedFileLoader.class.getName(), args);

        DelimitedFileLoader loader = new DelimitedFileLoader(config);

        loader.loadFile(config.filename);
        loader.close();

    }


}
