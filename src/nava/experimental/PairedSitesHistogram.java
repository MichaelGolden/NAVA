/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;
import nava.data.io.FileImport;
import nava.data.types.DataType;
import nava.data.types.SecondaryStructureData;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class PairedSitesHistogram {

    public static void main(String[] args) throws Exception {
        //File referenceAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
        //File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2.dbn");
       // File structureAlignment = new File("C:/dev/thesis/dengue_50x4.dbn");
        //File structureAlignment = new File("C:/dev/thesis/westnile/westnile_all_200.dbn");
        File structureAlignment = new File("C:/dev/thesis/hiv_full/hiv_full.dbn");
        File outFile = new File(structureAlignment.getAbsolutePath() + "_distances.csv");
        System.out.println(outFile);
        BufferedWriter buffer = new BufferedWriter(new FileWriter(outFile));
        //buffer.write("Distance\n");
        ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
        Random random = new Random();
        for (SecondaryStructureData d : structureData) {
            for (int i = 0; i < d.pairedSites.length; i++) {
                if (d.pairedSites[i] != 0 && i < d.pairedSites[i] - 1) {
                    int distance = d.pairedSites[i] - 1 - i;
                    if (distance <= 250 && random.nextDouble() < 0.5) {
                        buffer.write(distance + "\n");
                    }

                }
            }
        }
        buffer.close();
    }
}
