/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.AlignmentMetadata;
import nava.data.io.IO;
import nava.utils.Mapping;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class MappedData {

    String name;
    String[] names;
    double[] values;
    int[] codon;
    boolean[] used;
    Mapping mapping;
    Mapping[] mappings;

    public static MappedData getMappedData(File referenceAlignment, File inputAlignment, double[] values, boolean codon, String name, boolean useMUSCLE) {
        Mapping mapping = Mapping.createMapping(referenceAlignment, inputAlignment, 1,useMUSCLE);

        MappedData mappedData = new MappedData();

        mappedData.values = new double[mapping.getALength()];
        mappedData.used = new boolean[mapping.getALength()];
        mappedData.names = new String[mapping.getALength()];
        mappedData.mappings = new Mapping[mapping.getALength()];
        mappedData.name = name;
        mappedData.mapping = mapping;

        for (int i = 0; i < mappedData.values.length; i++) {
            int aToB = mapping.aToB(i);
            if (aToB != -1) {
                mappedData.values[i] = codon ? values[aToB / 3] : values[aToB];
                mappedData.used[i] = true;
                mappedData.names[i] = name;
                mappedData.mappings[i] = mapping;

                if (codon) {
                    mappedData.codon[i] = aToB % 3 + 1;
                }
            }
        }

        return mappedData;
    }
    
    public static MappedData getMappedData(File referenceAlignment, MappableData mappableData, int select, boolean useMUSCLE) {
        ArrayList<MappableData> list = new ArrayList<>();
        list.add(mappableData);
        return getMappedData(referenceAlignment, list, select, useMUSCLE);
    }

    public static MappedData getMappedData(File referenceAlignment, ArrayList<MappableData> mappableData, int select, boolean useMUSCLE) {
        try {
            AlignmentMetadata referenceMetadata = IO.getAlignmentMetadata(referenceAlignment);

            ArrayList<MappedData> mappedData = new ArrayList<>();
            for (MappableData data : mappableData) {
                mappedData.add(getMappedData(referenceAlignment, data.inputAlignment, data.values, data.codon, data.name, select, useMUSCLE));
            }
            
            MappedData finalData = new MappedData();
            finalData.values = new double[mappedData.get(0).values.length];
            finalData.codon = new int[mappedData.get(0).values.length];
            finalData.used = new boolean[mappedData.get(0).values.length];
            finalData.names = new String[mappedData.get(0).values.length];
            finalData.mappings = new Mapping[mappedData.get(0).values.length];
            finalData.name = mappedData.get(0).name;
            finalData.mapping = mappedData.get(0).mapping;

            for (MappedData data : mappedData) {
                for (int i = 0; i < finalData.values.length; i++) {
                    if (data.used[i]) {
                        if (finalData.used[i]) {
                            System.out.println("Overwriting " + i + "\t" + finalData.names[i] + " with " + data.names[i]);
                        }

                        finalData.values[i] = data.values[i];
                        finalData.codon[i] = data.codon[i];
                        finalData.used[i] = data.used[i];
                        finalData.names[i] = data.names[i];
                        finalData.mappings[i] = data.mappings[i];
                    }
                }
            }

            return finalData;

        } catch (IOException ex) {
            Logger.getLogger(MappedData.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static MappedData getMappedData(File referenceAlignment, File inputAlignment, ArrayList<String> values, boolean codon, String name, int select, boolean useMUSCLE) {
        Mapping mapping = Mapping.createMapping(referenceAlignment, inputAlignment, select, useMUSCLE);

        MappedData mappedData = new MappedData();

        mappedData.values = new double[mapping.getALength()];
        mappedData.codon = new int[mapping.getALength()];
        mappedData.used = new boolean[mapping.getALength()];
        mappedData.names = new String[mapping.getALength()];
        mappedData.mappings = new Mapping[mapping.getALength()];
        mappedData.name = name;
        mappedData.mapping = mapping;

        for (int i = 0; i < mappedData.values.length; i++) {
            int aToB = mapping.aToB(i);
            if (aToB != -1) {
                try {
                    mappedData.values[i] = codon ? Double.parseDouble(values.get(aToB / 3)) : Double.parseDouble(values.get(aToB));
                    mappedData.used[i] = true;
                    mappedData.names[i] = name;
                    mappedData.mappings[i] = mapping;
                    if (codon) {
                        mappedData.codon[i] = aToB % 3 + 1;
                    }
                } catch (NumberFormatException ex) {
                }
            }
        }
        return mappedData;
    }

    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < values.length; i++) {
            ret += (i + 1) + "\t" + mapping.alignedA0.charAt(Mapping.getUngappedPosition(mapping.alignedA0, i)) + "\t" + names[i] + "\t" + (used[i] ? values[i] : "") + "\n";
        }
        return ret;
    }
}
