package fw;


import junit.framework.TestCase;

import java.io.*;

public class AltoScannerTest extends TestCase
{
    public void testRead() throws FileNotFoundException,IOException {
        try (final InputStream fis = open("MMMVC01_000000029_01055_alto.xml")) {
            AltoScanner as =new AltoScanner(){{in=fis;}};
            as.process();
//            as.render(System.out);
            FileOutputStream out = new FileOutputStream("/tmp/output.txt");
            as.render(new PrintStream(out));
            out.close();

        }
    }

    private InputStream open(String filename) {
        // return new FileInputStream(new File(filename));
        return this.getClass().getResourceAsStream(filename);
    }
}
