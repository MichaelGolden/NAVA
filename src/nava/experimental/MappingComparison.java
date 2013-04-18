/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import nava.data.io.CsvReader;
import nava.utils.Mapping;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MappingComparison {

    public static void main(String[] args) throws Exception {
      //  File csvFile = new File("C:/hcv/1a_coding_aligned_100.nex.csv");
        //File dataAlignment = new File("C:/hcv/1a_coding_aligned_100.fas");
        //File csvFile = new File("C:/Users/Michael/Dropbox/Weeks/Fubar/2 partition/fubar_2.csv");
        //File dataAlignment = new File("C:/Users/Michael/Dropbox/Weeks/Fubar/100_coding.fas");
      //File csvFile = new File("C:/dev/thesis/dengue/300/fubar.csv");        
       //File dataAlignment = new File("C:/dev/thesis/dengue/300/dengue_polyprotein_300_aligned.fas");
        File csvFile = new File("C:/dev/thesis/dengue/300/site rates.csv");   
        File dataAlignment = new File("C:/dev/thesis/dengue/300/all_300_aligned.fas");
        boolean codon = false;
       //File csvFile = new File("C:/dev/thesis/dengue2/300/fubar.csv");
         //File dataAlignment = new File("C:/dev/thesis/dengue2/300/dengue2_polyprotein_300_aligned.fas");
        

       // File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
         File referenceAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.fas");

        ArrayList<String> values = CsvReader.getColumn(csvFile, 1);
        values.remove(0);

        //System.out.println(values.subList(0, 100));

        MappedData mappedData = MappedData.getMappedData(referenceAlignment, dataAlignment, values, codon, "");
        
        // (new File("c:/hcv/hcv_conservation_all.csv")
        ArrayList<String> values2 = CsvReader.getColumn(new File("c:/dev/thesis/dengue/dengue_conservation_300.csv"), 0);
        double[] arr2 = new double[mappedData.values.length];
        for (int i = 0; i < values2.size(); i++) {
            System.out.println(i+"\t"+mappedData.codon[i] +"\t" + mappedData.mapping.alignedA0.charAt(Mapping.getUngappedPosition(mappedData.mapping.alignedA0, i)) + "\t" + mappedData.values[i] + "\t" + values2.get(i));
            arr2[i] = Double.parseDouble(values2.get(i));
        }
        
        ArrayList<Double> list1 = new ArrayList<>();
        ArrayList<Double> list2 = new ArrayList<>();
        for(int i = 0 ; i < arr2.length ; i++)
        {
            if(mappedData.used[i] && arr2[i] != 0 )
            {
                // && mappedData.codon[i] == 1
                list1.add(mappedData.values[i]);
                list2.add(arr2[i]);
            }
        }
        
        Random random = new Random();
        double[][] data = new double[list1.size()][2];
        for(int i = 0 ; i < list1.size() ; i++)
        {
            data[i][0] = list1.get(i);
            data[i][1] = list2.get(i);
        }
        SpearmansCorrelation spearmansCorrelation = new SpearmansCorrelation(new Array2DRowRealMatrix(data));
         System.out.println(spearmansCorrelation.getCorrelationMatrix());
        System.out.println(spearmansCorrelation.getRankCorrelation().getCorrelationPValues());
        System.out.println(spearmansCorrelation.getRankCorrelation().getCorrelationMatrix());
        // System.out.println(spearmansCorrelatiion.getRankCorrelation().);

    }
}
