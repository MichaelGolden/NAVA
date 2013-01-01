package nava.analyses;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.types.*;
import nava.ui.MainFrame;
import nava.utils.RNAFoldingTools;

/**
 * Given an alignment of sequence this class shells to the RNAalifold
 * appropriately and returns a RNAalifold secondary structure prediction.
 *
 */
public class RNAalifold1 extends Application {

    private static String executable = "bin/RNAalifold.exe";
    private static boolean useOldParams = false;
    private Process process = null;

    public boolean checkRNAalifold() {
        try {
            ArrayList<String> sequences = new ArrayList<String>();
            ArrayList<String> sequenceNames = new ArrayList<String>();
            sequences.add("GGGUGCUUGAAGCUGUCUGCUUUAAGUGCUUGCA----UCAGGCUGAGAGUAGGCAGAGAAAAGCCCCGUAUCA-----A----------------UGUUAAUCAAUACGAGGC-CCUCUGUAAUG");
            sequences.add("GGGUGCUUGAGGCUGUCUGCCUCGGG------CAUGCC---ACCGUAAGGCAGACAGAGAAAAGCCCCAGUUAACAUUACGCGUCCUGCAAGACGCCUAACAUUAAUCUGAGGC-CAAUUU-CAUG");
            sequenceNames.add("a");
            sequenceNames.add("b");

            useOldParams = false;
            String newparams = " -T " + 37 + " --cfactor " + 1 + " --nfactor " + 1 + " ";
            RNAalifoldResult res = null;
            try {
                res = fold(sequences, sequenceNames, newparams, true);
            } catch (Exception ex) {
                //System.err.println("The following error occured with RNAalifold: " + ex.getMessage());
            }

            if (res != null) {
                return true;
            } else {
                String oldparams = " -T " + 37 + " -cv " + 1 + " -nc " + 1 + " ";
                useOldParams = true;
                res = fold(sequences, sequenceNames, oldparams, true);
                return res != null;
            }
        } catch (Exception ex) {
            //	System.err.println("The following error occured with RNAalifold: " + ex.getMessage());
        }

        useOldParams = false;

        return false;
    }

    public RNAalifoldResult fold(List<String> sequences, List<String> sequenceNames, String arguments) throws Exception {
        return fold(sequences, sequenceNames, arguments, true, false);
    }

    public RNAalifoldResult fold(List<String> sequences, List<String> sequenceNames, String arguments, boolean noErrorMessages) throws Exception {
        return fold(sequences, sequenceNames, arguments, true, noErrorMessages);
    }

    public RNAalifoldResult fold(List<String> sequences, List<String> sequenceNames, String arguments, boolean useMatrix, boolean noErrorMessages) throws Exception {
        if (useOldParams) {
            arguments = arguments.replaceAll(" --cfactor ", " -cv ");
            arguments = arguments.replaceAll(" --nfactor ", " -nc ");
        }

        try {
            String tempPath = System.getProperty("java.io.tmpdir") + "/";
            File tempClustalFile = new File(tempPath + "temp.clustalw");
            saveClustalW(sequences, sequenceNames, tempClustalFile);
            String args = executable + " " + "-p " + arguments;
            String file = tempClustalFile.getAbsolutePath();
            if (useOldParams) {
                args = executable + " " + "-p " + arguments;
            }


            // create a process builder to execute in temporary directory
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(tempPath));
            ArrayList<String> commands = new ArrayList<String>();
            String[] split = args.split("(\\s)+");
            for (int i = 0; i < split.length; i++) {
                commands.add(split[i]);
            }
            commands.add(file);
            //System.out.println(commands);
            processBuilder.command(commands);

            process = processBuilder.start();

            InputStream is = process.getErrorStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            String textline = null;
            String errorString = "";
            boolean first = true;
            while ((textline = buffer.readLine()) != null) {
                if (first) {
                    first = false;
                } else {
                    errorString += "\n";
                }
                errorString += textline;
            }
            buffer.close();
            int exitCode = process.waitFor();

            System.out.println(exitCode);
            if (exitCode != 0) {
                if (!noErrorMessages) {
                    System.err.println("RNAalifold generated the following error during execution:"
                            + "\n\"" + errorString + "\"");
                }
                return null;
            }

            RNAalifoldResult result = new RNAalifoldResult();
            if (useMatrix) {
                result.matrix = loadBasePairProbMatrix(new File(tempPath + "alidot.ps"), sequences.get(0).length());
            }
            result.pairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(loadDotBracketStructure(new File(tempPath + "alifold.out")));
            result.firstSequence = sequences.get(0);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void saveClustalW(List<String> sequences, List<String> sequenceNames, File outFile) {
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(outFile));
            buffer.write("CLUSTAL W(1.60) multiple sequence alignment\n");
            buffer.write("\n");
            for (int i = 0; i < sequences.size(); i++) {
                buffer.write(sequenceNames.get(i) + "\t" + sequences.get(i) + " " + sequences.get(i).length() + "\n");
            }
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String loadDotBracketStructure(File alifoldOut) {
        String dbs = null;

        try {
            BufferedReader buffer = new BufferedReader(new FileReader(alifoldOut));
            String textline = null;

            while ((textline = buffer.readLine()) != null) {
                dbs = textline;
            }
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return dbs;
    }

    public static double[][] loadBasePairProbMatrix(File basePairFile, int length) {
        double[][] matrix = new double[length][length];
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(basePairFile));
            String textline = null;
            boolean cont = false;
            while ((textline = buffer.readLine()) != null) {
                if (cont) {
                    String[] split = textline.split("(\\s)+");
                    if (split.length >= 7 && split[6].endsWith("ubox")) {
                        int x = Integer.parseInt(split[3]) - 1;
                        int y = Integer.parseInt(split[4]) - 1;
                        double prob = Double.parseDouble(split[5]);
                        matrix[x][y] = prob;
                        matrix[y][x] = prob;
                    }
                } else if (textline.startsWith("drawgrid")) {
                    cont = true;
                }
            }
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return matrix;
    }
    
    private Alignment inputDataSource;
    boolean started = false;
    boolean running = false;
    boolean canceled = false;
    ArrayList<ApplicationOutput> outputFiles = new ArrayList<>();
    
    @Override
    public void start() {
        if(started)
        {
            System.err.println("Cannot start this process more than once.");
        }
        else
        {
            started = true;
            running = true;
            AlignmentData alignmentData = inputDataSource.getObject(MainFrame.dataSourceCache);
            RNAalifoldResult result = null;
            try {
                ArrayList<String> sequenceNames = new ArrayList<String>();
                for(int i = 0 ; i < alignmentData.sequences.size() ; i++)
                {
                    sequenceNames.add("S"+i);
                }

                result = fold(alignmentData.sequences, sequenceNames, "");
                ApplicationOutput outputFile1 = new ApplicationOutput();

                outputFile1.file = null;
                SecondaryStructure structure = new SecondaryStructure();
                structure.title = inputDataSource.title;
                structure.parentSource = inputDataSource;
                outputFile1.dataSource = structure;
                outputFile1.object = new SecondaryStructureData(inputDataSource.title, result.firstSequence, result.pairedSites);
                outputFiles.add(outputFile1);
                
                ApplicationOutput outputFile2 = new ApplicationOutput();
                outputFile2.file = null;
                Matrix matrix = new Matrix();
                matrix.title = inputDataSource.title;
                matrix.parentSource = inputDataSource;
                outputFile2.dataSource = matrix;
                outputFile2.object = new DenseMatrixData(result.matrix);
                outputFiles.add(outputFile2);
            } catch (Exception ex) {
                Logger.getLogger(RNAalifold1.class.getName()).log(Level.SEVERE, null, ex);
            }
            running = false;
        }
    }
    

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cancel() {
        process.destroy();
        running = false;
        canceled = true;
    }

    @Override
    public boolean canProcessDataSource(DataSource dataSource) {
        if(dataSource instanceof Alignment)
        {
            Alignment alignment = (Alignment) dataSource;
            if(alignment.type.equals(Alignment.Type.NUCLEOTIDE) || alignment.type.equals(Alignment.Type.CODING))
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setDataSource(DataSource dataSource)
    {
        this.inputDataSource = (Alignment) dataSource;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public boolean isFinished() {
        return started && !running;
    }

    @Override
    public List<ApplicationOutput> getOutputFiles() {
        return outputFiles;
    }

    @Override
    public String getName() {
        return "RNAalifold";
    }

    @Override
    public String getDescription() {
        return "Predicts a consensus secondary structure from a set of aligned sequences.";
    }
}