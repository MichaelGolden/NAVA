/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import nava.data.io.CsvReader;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.data.types.DataType;
import nava.data.types.SecondaryStructureData;
import nava.utils.Mapping;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MappingComparison {
    
    public static MappedData getHIVMappedData(File referenceAlignment, int column) throws Exception
    {
        ArrayList<MappableData> mappableData = new ArrayList<>();

        File envCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_env_300_nooverlap_aligned.csv");
        File envAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_env_300_nooverlap_aligned.fas");
        ArrayList<String> envValues = CsvReader.getColumn(envCsv, column);
        envValues.remove(0);
        mappableData.add(new MappableData(envAlignment, envValues, true, "env"));

        File gagCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_gag_300_nooverlap_aligned.csv");
        File gagAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_gag_300_nooverlap_aligned.fas");
        ArrayList<String> gagValues = CsvReader.getColumn(gagCsv, column);
        gagValues.remove(0);
        mappableData.add(new MappableData(gagAlignment, gagValues, true, "gag"));

        File nefCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_nef_300_nooverlap_aligned.csv");
        File nefAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_nef_300_nooverlap_aligned.fas");
        ArrayList<String> nefValues = CsvReader.getColumn(nefCsv, column);
        nefValues.remove(0);
        mappableData.add(new MappableData(nefAlignment, nefValues, true, "nef"));

        File polCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_pol_300_nooverlap_aligned.csv");
        File polAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_pol_300_nooverlap_aligned.fas");
        ArrayList<String> polValues = CsvReader.getColumn(polCsv, column);
        polValues.remove(0);
        mappableData.add(new MappableData(polAlignment, polValues, true, "pol"));

        File tatCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_tat_300_nooverlap_aligned.csv");
        File tatAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_tat_300_nooverlap_aligned.fas");
        ArrayList<String> tatValues = CsvReader.getColumn(tatCsv, column);
        tatValues.remove(0);
        //mappableData.add(new MappableData(tatAlignment, tatValues, true, "tat"));

        File vifCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vif_300_nooverlap_aligned.csv");
        File vifAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vif_300_nooverlap_aligned.fas");
        ArrayList<String> vifValues = CsvReader.getColumn(vifCsv, column);
        vifValues.remove(0);
        // mappableData.add(new MappableData(vifAlignment, vifValues, true, "vif"));

        File vprCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vpr_300_nooverlap_aligned.csv");
        File vprAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vpr_300_nooverlap_aligned.fas");
        ArrayList<String> vprValues = CsvReader.getColumn(vprCsv, column);
        vprValues.remove(0);
        mappableData.add(new MappableData(vprAlignment, vprValues, true, "vpr"));


        File vpuCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vpu_300_nooverlap_aligned.csv");
        File vpuAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vpu_300_nooverlap_aligned.fas");
        ArrayList<String> vpuValues = CsvReader.getColumn(vpuCsv, column);
        vpuValues.remove(0);
        mappableData.add(new MappableData(vpuAlignment, vpuValues, true, "vpu"));

        MappedData mappedData = MappedData.getMappedData(referenceAlignment, mappableData, 1000, false);
        return mappedData;
    }

    public void hivMapping() throws IOException, ParserException, Exception {
        File referenceAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas");
        // File referenceAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/hiv1_all_300_aligned.fas");

        ArrayList<MappableData> mappableData = new ArrayList<>();

        File envCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_env_300_nooverlap_aligned.csv");
        File envAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_env_300_nooverlap_aligned.fas");
        ArrayList<String> envValues = CsvReader.getColumn(envCsv, 1);
        envValues.remove(0);
        mappableData.add(new MappableData(envAlignment, envValues, true, "env"));

        File gagCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_gag_300_nooverlap_aligned.csv");
        File gagAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_gag_300_nooverlap_aligned.fas");
        ArrayList<String> gagValues = CsvReader.getColumn(gagCsv, 1);
        gagValues.remove(0);
        mappableData.add(new MappableData(gagAlignment, gagValues, true, "gag"));

        File nefCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_nef_300_nooverlap_aligned.csv");
        File nefAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_nef_300_nooverlap_aligned.fas");
        ArrayList<String> nefValues = CsvReader.getColumn(nefCsv, 1);
        nefValues.remove(0);
        mappableData.add(new MappableData(nefAlignment, nefValues, true, "nef"));

        File polCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_pol_300_nooverlap_aligned.csv");
        File polAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_pol_300_nooverlap_aligned.fas");
        ArrayList<String> polValues = CsvReader.getColumn(polCsv, 1);
        polValues.remove(0);
        mappableData.add(new MappableData(polAlignment, polValues, true, "pol"));

        File tatCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_tat_300_nooverlap_aligned.csv");
        File tatAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_tat_300_nooverlap_aligned.fas");
        ArrayList<String> tatValues = CsvReader.getColumn(tatCsv, 1);
        tatValues.remove(0);
        //mappableData.add(new MappableData(tatAlignment, tatValues, true, "tat"));

        File vifCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vif_300_nooverlap_aligned.csv");
        File vifAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vif_300_nooverlap_aligned.fas");
        ArrayList<String> vifValues = CsvReader.getColumn(vifCsv, 1);
        vifValues.remove(0);
        // mappableData.add(new MappableData(vifAlignment, vifValues, true, "vif"));

        File vprCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vpr_300_nooverlap_aligned.csv");
        File vprAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vpr_300_nooverlap_aligned.fas");
        ArrayList<String> vprValues = CsvReader.getColumn(vprCsv, 1);
        vprValues.remove(0);
        mappableData.add(new MappableData(vprAlignment, vprValues, true, "vpr"));


        File vpuCsv = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vpu_300_nooverlap_aligned.csv");
        File vpuAlignment = new File("C:/dev/thesis/hiv_full/hiv1/300/clean/hiv1_vpu_300_nooverlap_aligned.fas");
        ArrayList<String> vpuValues = CsvReader.getColumn(vpuCsv, 1);
        vpuValues.remove(0);
        mappableData.add(new MappableData(vpuAlignment, vpuValues, true, "vpu"));

        MappedData mappedData = MappedData.getMappedData(referenceAlignment, mappableData, 1000, false);
        System.out.println(mappedData);

        ArrayList<String> values2 = CsvReader.getColumn(new File("c:/dev/thesis/hiv_full/hivnotsiv-conserved-sites.csv"), 0);

        double[] value2arr = new double[mappedData.values.length];
        for (int i = 0; i < values2.size(); i++) {
            value2arr[i] = Double.parseDouble(values2.get(i));
        }

        ArrayList<Double> list1 = new ArrayList<>();
        ArrayList<Double> list2 = new ArrayList<>();
        for (int i = 0; i < value2arr.length; i++) {
            if (mappedData.used[i] && value2arr[i] != 0 && mappedData.codon[i] == 1) {
                list1.add(mappedData.values[i]);
                list2.add(value2arr[i]);
            }
        }

        System.out.println(CorrelatedSitesTest.calculateCorrelation(list1, list2)[0]);
        System.out.println(CorrelatedSitesTest.calculateCorrelation(list1, list2)[1]);

        File structureAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn");
        ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
        CorrelatedSitesTest correlatedSitesTest = new CorrelatedSitesTest();
        for(SecondaryStructureData structure : structureData)
        {
            correlatedSitesTest.pairedSitesCorrelationPermutationTest(mappedData, structure.pairedSites,8);
        }
    }

    public void job() throws IOException {
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

        MappedData mappedData = MappedData.getMappedData(referenceAlignment, dataAlignment, values, codon, "", 1, false);

        // (new File("c:/hcv/hcv_conservation_all.csv")
        ArrayList<String> values2 = CsvReader.getColumn(new File("c:/dev/thesis/dengue/dengue_conservation_300.csv"), 0);
        double[] arr2 = new double[mappedData.values.length];
        for (int i = 0; i < values2.size(); i++) {
            System.out.println(i + "\t" + mappedData.codon[i] + "\t" + mappedData.mapping.alignedA0.charAt(Mapping.getUngappedPosition(mappedData.mapping.alignedA0, i)) + "\t" + mappedData.values[i] + "\t" + values2.get(i));
            arr2[i] = Double.parseDouble(values2.get(i));
        }

        ArrayList<Double> list1 = new ArrayList<>();
        ArrayList<Double> list2 = new ArrayList<>();
        for (int i = 0; i < arr2.length; i++) {
            if (mappedData.used[i] && arr2[i] != 0) {
                // && mappedData.codon[i] == 1
                list1.add(mappedData.values[i]);
                list2.add(arr2[i]);
            }
        }

        Random random = new Random();
        double[][] data = new double[list1.size()][2];
        for (int i = 0; i < list1.size(); i++) {
            data[i][0] = list1.get(i);
            data[i][1] = list2.get(i);
        }
        SpearmansCorrelation spearmansCorrelation = new SpearmansCorrelation(new Array2DRowRealMatrix(data));
        System.out.println(spearmansCorrelation.getCorrelationMatrix());
        System.out.println(spearmansCorrelation.getRankCorrelation().getCorrelationPValues());
        System.out.println(spearmansCorrelation.getRankCorrelation().getCorrelationMatrix());
        // System.out.println(spearmansCorrelatiion.getRankCorrelation().);

    }

    public static void main(String[] args) throws Exception {
        new MappingComparison().hivMapping();
    }
}
