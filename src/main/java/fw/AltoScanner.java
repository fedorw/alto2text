package fw;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class AltoScanner {
    Map<int[],String> placed=new HashMap<>();
    int maxCol=0;
    int maxRow=0;

    float hscale=1.0f;
    float vscale=1.0f;

    public static void main(String args[]) throws Exception {
        Options options = new Options();
        options.addOption("s","scale",true,"scaling (-s 0.2,0.3");
        options.addOption("i","input",true,"inputfile (- is stdin)");

        CommandLineParser parser= new GnuParser();
        CommandLine cmd = parser.parse(options,args);
        final InputStream input;

	if (!cmd.hasOption("i")) {
		HelpFormatter form = new HelpFormatter();
		form.printHelp("altorender",options);
		System.exit(0);
	}
        String in=cmd.getOptionValue("i").trim();
        if ("-".equals(in)) {
            input=System.in;
        } else {
            input=new FileInputStream(new File(in));
        }

        float hs=1f;
        float vs=1f;
        if (cmd.hasOption("s")) {
            String scale=cmd.getOptionValue("s");
            StringTokenizer toks=new StringTokenizer(scale,",");
            hs =Float.parseFloat(toks.nextToken().trim());
            vs =Float.parseFloat(toks.nextToken().trim());
        }

        AltoScanner as = new AltoScanner();
        as.in=input;
        as.hscale=hs;
        as.vscale=vs;

        as.process();
        as.render(new PrintStream(System.out));




    }

    public void add(Fragment fragment) {
        if (fragment.str.startsWith("Plasl.")) {
            int i=0;
        }
        if (fragment.pos[0] > maxRow) {
            maxRow=fragment.pos[0];
        }
        if (fragment.pos[1]+fragment.str.length() > maxCol) {
            maxCol=fragment.pos[1]+fragment.str.length();
        }
        placed.put(fragment.pos, fragment.str);
    }

    public InputStream in ;
    public void process() {
        boolean inTag = false;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            int c = br.read();
            StringBuilder s = new StringBuilder();
            while (c >-1) {
                char cc=(char)c;
                switch (cc) {
                    case '<':
                        inTag = true;
                        break;
                    case '>':
                        inTag = false;
                        handle(s.toString());
                        s.setLength(0);
                        inTag = false;
                        break;
                    default:
                        if (inTag) {
                            s.append(cc);
                        }
                        break;
                }
                c = br.read();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        /*
        System.err.println(placed.size());
        System.err.println(maxCol);
        System.err.println(maxRow);
        */
    }
    public char[][] emptyGrid(int maxRow,int maxCol) {
        char grid[][]=new char[maxRow+1][maxCol+1];
        for (int r=0;r<maxRow+1;r++) {
            for (int c=0;c<maxCol+1;c++) {
                grid[r][c]=' ';
            }
        }
        return grid;
    }
    public void  render(PrintStream out) {
        char grid[][]=emptyGrid(maxRow,maxCol);
        System.err.println("max r/c:"+maxRow+"/"+maxCol);
        for (int p[]:placed.keySet()) {
            String s=placed.get(p);
            for (int i=0;i<s.length();i++) {
//                try {
                    grid[p[0]][p[1] + i] = s.charAt(i);
//                }
//                catch (ArrayIndexOutOfBoundsException e) {
//                    System.err.println("HUH:"+p[0]+":"+p[1]+" -> "+s);
//                }
            }
        }
        for (int l=0;l<grid.length;l++) {
            out.println(new String(grid[l]));
        }
    }
    private String extract(String s, String attr) {
        int pos = s.indexOf(attr+"=\"");
        if (pos==-1) {
            return null;
        }
        return s.substring(pos+attr.length()+2, s.indexOf('"',pos+attr.length()+2));
    }
    private void handle(final String s) {
        if (!s.startsWith("String ")) {
            return;
        }
        try {
            add(new Fragment(){
                {
                    str= StringEscapeUtils.unescapeXml(extract(s, "CONTENT"));
                    pos= new int[] {
                        Math.round((Integer.parseInt(extract(s,"VPOS")))*vscale),
                        Math.round((Integer.parseInt(extract(s,"HPOS")))*hscale)
                    };
                }
            });
        }catch (Exception e) {
            System.err.println("cannot parse: "+s);
        }
    }
}

class Fragment {
    int pos[]; // [row][col]
    public String str;
}
