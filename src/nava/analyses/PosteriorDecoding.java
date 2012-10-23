/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.analyses;

import java.util.ArrayList;
import java.util.List;
import nava.data.types.*;
import nava.ui.MainFrame;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael
 */
public class PosteriorDecoding implements Application {

    Matrix matrix;
    ArrayList<ApplicationOutput> outputFiles = new ArrayList<>();
    boolean started = false;
    boolean running = false;
    boolean canceled = false;

    @Override
    public void start() {
        if (started) {
            System.err.println("Cannot start this process more than once.");
        } else {
            started = true;
            running = true;
            RNAFoldingTools rnaTools = new RNAFoldingTools();
            DenseMatrixData matrixData = (DenseMatrixData) matrix.getObject(MainFrame.dataSourceCache);
            int[] pairedSites = new RNAFoldingTools().getPosteriorDecodingConsensusStructureMultiThreaded(matrixData.matrix);

            ApplicationOutput outputFile1 = new ApplicationOutput();
            outputFile1.file = null;
            SecondaryStructure structure = new SecondaryStructure();
            structure.title = matrix.title;
            structure.parentSource = matrix;
            outputFile1.dataSource = structure;
            outputFile1.object = new SecondaryStructureData(matrix.title, "", pairedSites);
            outputFiles.add(outputFile1);
            running = false;
        }
    }

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cancel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canProcessDataSource(DataSource dataSource) {
        if (dataSource instanceof Matrix) {
            return true;
        }
        return false;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
         this.matrix = (Matrix) dataSource;
    }

    @Override
    public boolean isStarted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRunning() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCanceled() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isFinished() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ApplicationOutput> getOutputFiles() {
         return outputFiles;
    }

    @Override
    public String getName() {
        return "Posterior-decoding";
    }

    @Override
    public String getDescription() {
        return "Given a base-pairing probability matrix, returns the secondary structures that maximises the expected number of correctly predicted positions.";
    }
}
