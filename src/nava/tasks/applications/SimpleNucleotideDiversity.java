/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.tasks.applications;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nava.data.types.Alignment;
import nava.data.types.AlignmentData;
import nava.data.types.DataSource;
import nava.data.types.Tabular;
import nava.ui.MainFrame;
import nava.ui.ProjectModel;
import nava.utils.AlignmentType;
import nava.utils.AlignmentUtils;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SimpleNucleotideDiversity extends Application {
    
    Alignment inputDataSource = null;
    ArrayList<ApplicationOutput> outputFiles = new ArrayList<>();
    
    @Override
    protected void start() {
        AlignmentData data = inputDataSource.getObject(ProjectModel.path, MainFrame.dataSourceCache);
        double[] values = AlignmentUtils.calculateNucleotideDiversity(data.sequences);
        File tempDir = createTemporaryDirectory();
        File csvFile = Utils.getFile(tempDir, "output.csv");
        
        try {
            BufferedWriter buffer = new BufferedWriter(new FileWriter(csvFile));
            buffer.write("\"Position\",\"Nucleotide diversity\"\n");
            for (int i = 0; i < values.length; i++) {
                buffer.write("\""+(i+1)+"\",\""+values[i]+"\"\n");
            }
            buffer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
        ApplicationOutput outputFile1 = new ApplicationOutput();
        
        outputFile1.file = null;
        Tabular tabular = new Tabular();
        tabular.title = inputDataSource.title + " nucleotide diversity";
        tabular.originalFile = csvFile;
        outputFile1.dataSource = tabular;
        outputFiles.add(outputFile1);
    }
    
    @Override
    public boolean canProcessDataSources(List<DataSource> dataSources) {
        if (dataSources.size() == 1) {
            if (dataSources.get(0) instanceof Alignment) {
                Alignment al = (Alignment) (dataSources.get(0));
                if(al.type == AlignmentType.NUCLEOTIDE_ALIGNMENT || al.type == AlignmentType.CODON_ALIGNMENT)
                {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void setDataSource(DataSource dataSource) {
        this.inputDataSource = (Alignment) dataSource;
    }
    
    @Override
    public List<ApplicationOutput> getOutputFiles() {
        return outputFiles;
    }
    
    @Override
    protected void pause() {
        
    }
    
    @Override
    protected void resume() {
        
    }
    
    @Override
    protected void cancel() {
        
    }
    
    @Override
    public String getName() {
        return "Simple nucleotide diversity";
    }
    
    @Override
    public String getDescription() {
        return "Calculate simple nucleotide diversity per site";
    }
}
