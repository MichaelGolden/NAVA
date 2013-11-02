/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.CsvReader;
import nava.data.io.FileImport;
import nava.data.io.FileImport.ParserException;
import nava.data.types.DataType;
import nava.data.types.SecondaryStructureData;
import nava.ranking.MyMannWhitney;
import nava.ranking.RankingAnalyses;
import nava.ranking.StatUtils;
import nava.structure.MountainMetrics;
import nava.structure.StructureAlign;
import nava.structurevis.data.DataTransform;
import nava.structurevis.data.DataTransform.TransformType;
import nava.structurevis.data.Feature;
import nava.utils.ColorGradient;
import nava.utils.GraphicsUtils;
import nava.utils.Mapping;
import nava.utils.Pair;
import nava.utils.RNAFoldingTools;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class PairwiseStructureComparison {

    public static MappedData hivMapping(File referenceAlignment) throws IOException, ParserException, Exception {

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
        return mappedData;
    }

    public void runComparison() {
        try {
            int permutations = 1000;
            int windowSize = 75;
            boolean balance = true;

            boolean circular = false;
           ArrayList<MappedData> dataSources = new ArrayList<>();
            File outFile = new File("C:/dev/thesis/hiv_full/hiv_not_siv_permutations2.txt");
             String[] categories = {"hiv1b", "hiv1c", "hiv1d", "hiv1g", "hiv1o", "hiv2"};
           File structureAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn");
             File sequenceFile = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas");
              ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            
            ArrayList<File> referenceFiles = new ArrayList<>();
            referenceFiles.add(new File("C:/dev/thesis/hiv_full/hiv1-reference.fasta"));
            dataSources.add(hivMapping(sequenceFile));
            
            //  File outFile = new File("C:/dev/thesis/dengue/dengue_permutations2.txt");
            //  String[] categories = {"dengue1", "dengue2", "dengue3", "dengue4"};
            //  File structureAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
            //  ArrayList<MappedData> dataSources = new ArrayList<>();
            //  File sequenceFile = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
            //  File referenceFile = new File("C:/dev/thesis/dengue/dengue_refseq.fasta");
            //  MappableData synDataDengue = new MappableData(new File("C:/dev/thesis/dengue/300/dengue_polyprotein_300_aligned.fas"), new File("C:/dev/thesis/dengue/300/dengue_polyprotein_300_aligned.csv"), 1, 1, true, "Syn rates dengue");
            //  dataSources.add(MappedData.getMappedData(sequenceFile, synDataDengue, 1000, false));
            //  MappableData nucDataDengue = new MappableData(new File("C:/dev/thesis/dengue/300/dengue_all_300_aligned.fas"), new File("C:/dev/thesis/dengue/300/site rates.csv"), 1, 1, true, "Nuc rates dengue");
            // dataSources.add(MappedData.getMappedData(sequenceFile, nucDataDengue, 1000, false));

            //File outFile = new File("C:/dev/thesis/westnile/westnile_permutations.txt");
            //  String[] categories = {"westnile"};
            //File outFile = new File("C:/dev/thesis/westnile_dengue_permutations_gap.txt");
            //String[] categories = {"westnile", "dengue1", "dengue2", "dengue3", "dengue4"};
            //File structureAlignment = new File("C:/dev/thesis/structure_dengue_westnile_align_400.dbn");



            //  File outFile = new File("C:/dev/thesis/jev_westnile_permutations2.txt");
            //  String[] categories = {"westnile", "jev"};
            //  File structureAlignment = new File("C:/dev/thesis/structure_jev_westnile_aligned.dbn");
            //  ArrayList<MappedData> dataSources = new ArrayList<>();
            //  File sequenceFile = new File("C:/dev/thesis/structure_jev_westnile_aligned.fas");
            // File referenceFile = new File("C:/dev/thesis/structure_jev_westnile_aligned.fas");

            // File outFile = new File("C:/dev/thesis/dengue/dengue_permutations2.txt");
            //String[] categories = {"dengue1", "dengue2", "dengue3","dengue4"};
            //File structureAlignment = new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
            //ArrayList<MappedData> dataSources = new ArrayList<>();
            //File sequenceFile = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
            //File referenceFile = new File("C:/dev/thesis/dengue/dengue-alignment.fas");


            //   File outFile = new File("C:/dev/thesis/jev_tbv_westnile/jev_tbv_westnile_permutations_1000_2.txt");
            //  String[] categories = {"jev", "tbv", "westnile"};
            //  File structureAlignment = new File("C:/dev/thesis/jev_tbv_westnile/jev_westnile_tbv_aligned.dbn");
            //  ArrayList<MappedData> dataSources = new ArrayList<>();
            //  File sequenceFile = new File("C:/dev/thesis/jev_tbv_westnile/jev_westnile_tbv_aligned.fas");
            //   File referenceFile = new File("C:/dev/thesis/jev_tbv_westnile/visualisation/westnile_reference.fasta");
            //  MappableData synDataJEV= new MappableData(new File("C:/dev/thesis/jev_tbv_westnile/visualisation/jev_polyprotein_300_aligned.fas"), new File("C:/dev/thesis/jev_tbv_westnile/visualisation/jev_polyprotein_300_aligned.csv"), 1, 1, true, "Syn rates JEV");
            //   dataSources.add(MappedData.getMappedData(sequenceFile, synDataJEV, 1000, false));
            //   MappableData synDataTBV= new MappableData(new File("C:/dev/thesis/jev_tbv_westnile/visualisation/tbv_polyprotein_300_aligned.fas"), new File("C:/dev/thesis/jev_tbv_westnile/visualisation/tbv_polyprotein_300_aligned.csv"), 1, 1, true, "Syn rates TBV");
            //  dataSources.add(MappedData.getMappedData(sequenceFile, synDataTBV, 1000, false));
            //  MappableData synDataWestnile= new MappableData(new File("C:/dev/thesis/jev_tbv_westnile/visualisation/westnile_polyprotein_300_aligned.fas"), new File("C:/dev/thesis/jev_tbv_westnile/visualisation/westnile_polyprotein_300_aligned.csv"), 1, 1, true, "Syn rates westnile");
            //  dataSources.add(MappedData.getMappedData(sequenceFile, synDataWestnile, 1000, false));

            /*boolean circular = true;
            String [] categories = {"GeminiTYLCV","GeminiEACMV", "GeminiMYVYV"};
            File structureAlignment = new File("C:/brej/gemini_begomo/Gemini_Begomovirus_aligned.dbn");
            File sequenceFile = new File("C:/brej/gemini_begomo/Gemini_Begomovirus_aligned.fas");
            File outFile = new File("C:/brej/gemini_begomo/begomo_permutations_saved.txt");
             ArrayList<File> referenceFiles = new ArrayList<>();
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Geminiviruses/GeminiTYLCV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Geminiviruses/GeminiEACMV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Geminiviruses/GeminiMYVYV.fas"));
           
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            ArrayList<MappedData> dataSources = new ArrayList<>();*/
          
         /*  boolean circular = false;
            String [] categories = {"ParvoAAV", "ParvoMPV","ParvoHBoV"};
            File structureAlignment = new File("C:/brej/parvovirus/Parvovirus_aligned.dbn");
            File sequenceFile = new File("C:/brej/parvovirus/Parvovirus_aligned.fas");
            File outFile = new File("C:/brej/parvovirus/parvovirus_permutations_saved3.txt");
                ArrayList<File> referenceFiles = new ArrayList<>();
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Parvoviruses/ParvoAAV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Parvoviruses/ParvoHBoV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Parvoviruses/ParvoMLP.fas"));
           
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            ArrayList<MappedData> dataSources = new ArrayList<>();*/
            /*MappableData synDataParvoAAV_NS1 = new MappableData(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoAAV_NS1.fas"), new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoAAV_NS1.fas_gardout_finalout.fubar.csv"), 1, 1, true, "Syn rates ParvoAAV_NS1");
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataParvoAAV_NS1, 1000, false));
            MappableData synDataParvoAAV_VP1 = new MappableData(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoAAV_VP1.fas"), new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoAAV_VP1.fas_gardout_finalout.fubar.csv"), 1, 1, true, "Syn rates ParvoAAV_VP1");
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataParvoAAV_VP1, 1000, false));
            MappableData synDataParvoHBoV_NP1 = new MappableData(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoHBoV_NP1_trimed_edit.fas"), new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoHBoV_NP1_trimed.fas_gardout_finalout.fubar.csv"), 1, 1, true, "Syn rates ParvoHBoV_NP1");
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataParvoHBoV_NP1, 1000, false));
            MappableData synDataParvoHBoV_NS1 = new MappableData(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoHBoV_NS1.fas"), new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoHBoV_NS1.fas_gardout_finalout.fubar.csv"), 1, 1, true, "Syn rates ParvoHBoV_NS1");
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataParvoHBoV_NS1, 1000, false));
             MappableData synDataParvoMPV_NS1 = fnew MappableData(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoMPV_NS1.fas"), new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoMPV_NS1.fas_gardout_finalout.fubar.csv"), 1, 1, true, "Syn rates ParvoMPV_NS1");
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataParvoMPV_NS1, 1000, false));
            MappableData synDataParvoMPV_VP2 = new MappableData(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoMPV_VP2.fas"), new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Parvovirus/ParvoMPV_VP2.fas_gardout_finalout.fubar.csv"), 1, 1, true, "Syn rates ParvoMPV_VP2");
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataParvoMPV_VP2, 1000, false));
           */
            
          /* boolean circular = true;
           String [] categories = {"AnelloTTSuV1","AnelloTTSuV2","AnelloTTV"};
            File structureAlignment = new File("C:/brej/anellovirus/Anellovirus_aligned.dbn");
            File sequenceFile = new File("C:/brej/anellovirus/Anellovirus_aligned.fas");
            File outFile = new File("C:/brej/anellovirus/anellovirus_permutations_saved.txt");
            File referenceFile = sequenceFile;
          
            ArrayList<File> referenceFiles = new ArrayList<>();
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Anelloviruses/AnelloTTSuV1.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Anelloviruses/AnelloTTSuV2.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Anelloviruses/AnelloTTV.fas"));
        

            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            ArrayList<MappedData> dataSources = new ArrayList<>();
            MappableData synDataAnelloTTSuV1ORF1 = new MappableData(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Anellovirus/AnelloTTSuV1_ORF1.fas"), new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Anellovirus/AnelloTTSuV1_ORF1.fas_gardout_finalout.fubar.csv"), 1, 1, true, "Syn rates AnelloTTSuV1_ORF1");
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataAnelloTTSuV1ORF1, 1000, true));
             MappableData AnelloTTSuV2_ORF1 = new MappableData(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Anellovirus/AnelloTTSuV2_ORF1.fas"), new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Anellovirus/AnelloTTSuV2_ORF1.fas_gardout_finalout.fubar.csv"), 1, 1, true, "Syn rates AnelloTTSuV2_ORF1");
            dataSources.add(MappedData.getMappedData(sequenceFile, AnelloTTSuV2_ORF1, 1000, true));
             MappableData synDataAnelloTTVORF1 = new MappableData(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Anellovirus/AnelloTTV_ORF1.fas"), new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Fubar/Anellovirus/AnelloTTV_ORF1.fas_gardout_finalout.fubar.csv"), 1, 1, true, "Syn rates AnelloTTV_ORF1");
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataAnelloTTVORF1, 1000, true));
       */
           /* boolean circular = false;
             String [] categories = {"seq"};
            File structureAlignment = new File("C:/dev/thesis/hiv_full/darren/conserved/darren_hiv_full_aligned_muscle.dbn");
            File sequenceFile = new File("C:/dev/thesis/hiv_full/darren/conserved/darren_hiv_full_aligned_muscle.fas");
            File outFile = new File("C:/dev/thesis/hiv_full/darren/conserved/hiv_permutations.txt");
            File referenceFile = sequenceFile;
          
            ArrayList<File> referenceFiles = new ArrayList<>();
 
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            ArrayList<MappedData> dataSources = new ArrayList<>();*/
            
            /*boolean circular = false;
             String [] categories = {"seq"};
            File structureAlignment = new File("C:/dev/thesis/hiv_full/test/darren_hiv.dbn");
            File sequenceFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv.fas");
            File outFile = new File("C:/dev/thesis/hiv_full/test/darren_hiv_permutations.txt");
            File referenceFile = sequenceFile;
          
            ArrayList<File> referenceFiles = new ArrayList<>();
 
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            ArrayList<MappedData> dataSources = new ArrayList<>();*/
  
           /* boolean circular = true;
           String [] categories = {"CircoPCV","CircoCoCV", "CircoBFDV", "CircoDGCV"};
            File structureAlignment = new File("C:/brej/circovirus/Circovirus_aligned.dbn");
            File sequenceFile = new File("C:/brej/circovirus/Circovirus_aligned.fas");
            File outFile = new File("C:/brej/circovirus/circovirus_permutations_saved.txt");
            File referenceFile = sequenceFile;
            ArrayList<File> referenceFiles = new ArrayList<>();
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Circoviruses/CircoBFDV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Circoviruses/CircoCoCV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Circoviruses/CircoDGCV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Circoviruses/CircoPCV.fas"));
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            ArrayList<MappedData> dataSources = new ArrayList<>();*/
            
           /*boolean circular = true;
            String [] categories = {"GeminiMSV","GeminiPanSV", "GeminiTYDV-CpCV", "GeminiCpCDV", "GeminiWDV"};
            File structureAlignment = new File("C:/brej/gemini_mastrevirus/Gemini_Mastrevirus_aligned.dbn");
            File sequenceFile = new File("C:/brej/gemini_mastrevirus/Gemini_Mastrevirus_aligned.fas");
            File outFile = new File("C:/brej/gemini_mastrevirus/mastrevirus_permutations_saved.txt");            
            ArrayList<File> referenceFiles = new ArrayList<>();
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Geminiviruses/GeminiMSV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Geminiviruses/GeminiPanSV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Geminiviruses/GeminiTYDV-CpCV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Geminiviruses/GeminiCpCDV.fas"));
            referenceFiles.add(new File("C:/Users/Michael/Dropbox/BrejAndMichael/ssDNA_viruses_groups/Nasp alignments/Geminiviruses/GeminiWDV.fas"));
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            ArrayList<MappedData> dataSources = new ArrayList<>();
            */
            //String [] categories = {"msv"};
            //File structureAlignment = new File("C:/brej/msv/MSV_10seq.dbn");
           // File sequenceFile = new File("C:/brej/msv/MSV_10seq.fas");
            //File outFile = new File("C:/brej/msv/msv_permutations.txt");
            //ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);

            //String[] categories = {"hcv1", "hcv2"};
            /*boolean circular = false;
            String[] categories = {"hcv1", "hcv2", "hcv3", "hcv4", "hcv6"};
            File outFile = new File("C:/dev/thesis/hcv/hcv_permutations2.txt");
            //  String[] categories = {"hcv1a", "hcv1b", "hcv2a", "hcv2b", "hcv3", "hcv4", "hcv6"};
            File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
            File sequenceFile = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
            ArrayList<File> referenceFiles = new ArrayList<>();
            referenceFiles.add(new File("C:/dev/thesis/hcv/visualisation/H77_reference.fasta"));
            MappableData nucleotideData = new MappableData(sequenceFile, new File("C:/dev/thesis/hcv/conservation/rates.csv"), 0, 1, false, "Nucleotide rates");
            MappableData synDataHCV1 = new MappableData(new File("C:/dev/thesis/hcv/1/300/hcv1_polyprotein_300_aligned.fas"), new File("C:/dev/thesis/hcv/1/300/hcv1_polyprotein_300_aligned.csv"), 1, 1, true, "Syn rates hcv 1");
            MappableData synDataHCV1a = new MappableData(new File("C:/dev/thesis/hcv/1a/300/hcv1a_polyprotein_300_aligned.fas"), new File("C:/dev/thesis/hcv/1a/300/hcv1a_polyprotein_300_aligned.csv"), 1, 1, true, "Syn rates hcv 1a");
            MappableData synDataHCV1b = new MappableData(new File("C:/dev/thesis/hcv/1b/300/hcv1b_polyprotein_300_aligned.fas"), new File("C:/dev/thesis/hcv/1b/300/hcv1b_polyprotein_300_aligned.csv"), 1, 1, true, "Syn rates hcv 1b");
            MappableData synDataHCV2 = new MappableData(new File("C:/hcv/2_coding_alignment_100.fas"), new File("C:/hcv/2_coding_alignment_100.nex.csv"), 1, 1, true, "Syn rates hcv 2");
            MappableData synDataHCV3 = new MappableData(new File("C:/hcv/3_coding_alignment_48.fas_DNA.fasta"), new File("C:/hcv/3_coding_alignment_48.fas_DNA.nex.csv"), 1, 1, true, "Syn rates hcv 3");
            MappableData synDataHCV4 = new MappableData(new File("C:/hcv/4_coding_macse.fas_DNA.fasta"), new File("C:/hcv/4_coding_macse.fas_DNA.fasta.nex.csv"), 1, 1, true, "Syn rates hcv 4");
            MappableData synDataHCV6 = new MappableData(new File("C:/hcv/6_coding_alignment_77.fas"), new File("C:/hcv/6_coding_alignment_77.nex.csv"), 1, 1, true, "Syn rates hcv 6");
            ArrayList<MappedData> dataSources = new ArrayList<>();
            dataSources.add(MappedData.getMappedData(sequenceFile, nucleotideData, 1000, false));
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataHCV1, 1000, false));
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataHCV1a, 1000, false));
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataHCV1b, 1000, false));
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataHCV2, 1000, false));
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataHCV3, 1000, false));
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataHCV4, 1000, false));
            dataSources.add(MappedData.getMappedData(sequenceFile, synDataHCV6, 1000, false));
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            */
            
            /*boolean circular = false;
            String[] categories = {"dengue", "jev", "tbv", "westnile"};
            File outFile = new File("C:/dev/thesis/dengue_jev_tbv_westnile/dengue_jev_tbv_westnile_permutations.txt");
            //  String[] categories = {"hcv1a", "hcv1b", "hcv2a", "hcv2b", "hcv3", "hcv4", "hcv6"};
            File structureAlignment = new File("C:/dev/thesis/dengue_jev_tbv_westnile/dengue_jev_westnile_tbv_aligned.dbn");
            File sequenceFile = new File("C:/dev/thesis/dengue_jev_tbv_westnile/dengue_jev_westnile_tbv_aligned.fas");
            ArrayList<File> referenceFiles = new ArrayList<>();
            referenceFiles.add(new File("C:/dev/thesis/dengue_jev_tbv_westnile/dengue_refseq.fasta"));
            referenceFiles.add(new File("C:/dev/thesis/jev_tbv_westnile/visualisation/westnile_reference.fasta"));
            ArrayList<MappedData> dataSources = new ArrayList<>();
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            */
            /*
            //String[] categories = {"westnile", "hcv1a", "hcv1b", "hcv2a", "hcv2b","hcv3","hcv4", "hcv6"};
            //  File outFile = new File("C:/dev/thesis/hcv/hcv_permutations2.txt"); 
            //  String[] categories = {"hcv1a", "hcv1b", "hcv2a", "hcv2b", "hcv3", "hcv4", "hcv6"};
            //   File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"); 
            //   File sequenceFile = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"); 
            //  File referenceFile = new File("C:/dev/thesis/hcv/visualisation/H77_reference.fasta");
            //  MappableData nucleotideData = new MappableData(sequenceFile, new File("C:/dev/thesis/hcv/conservation/rates.csv"), 0, 1, false,"Nucleotide rates"); 
            //   MappableData synDataHCV1 = new MappableData(new File("C:/dev/thesis/hcv/1/300/hcv1_polyprotein_300_aligned.fas"), new File("C:/dev/thesis/hcv/1/300/hcv1_polyprotein_300_aligned.csv"), 1, 1, true, "Syn rates hcv 1"); 
            //  MappableData synDataHCV1a = new MappableData(new File("C:/dev/thesis/hcv/1a/300/hcv1a_polyprotein_300_aligned.fas"), new File("C:/dev/thesis/hcv/1a/300/hcv1a_polyprotein_300_aligned.csv"), 1, 1, true, "Syn rates hcv 1a"); 
            // MappableData synDataHCV1b = new  MappableData(new File("C:/dev/thesis/hcv/1b/300/hcv1b_polyprotein_300_aligned.fas"), new File("C:/dev/thesis/hcv/1b/300/hcv1b_polyprotein_300_aligned.csv"), 1, 1, true, "Syn rates hcv 1b"); 
            //   MappableData synDataHCV2 = new MappableData(new File("C:/hcv/2_coding_alignment_100.fas"), new File("C:/hcv/2_coding_alignment_100.nex.csv"), 1, 1, true, "Syn rates hcv 2"); 
            //   MappableData synDataHCV3 = new MappableData(new File("C:/hcv/3_coding_alignment_48.fas_DNA.fasta"), new File("C:/hcv/3_coding_alignment_48.fas_DNA.nex.csv"), 1, 1, true, "Syn rates hcv 3"); 
            //  MappableData synDataHCV4 = new MappableData(new File("C:/hcv/4_coding_macse.fas_DNA.fasta"), new File("C:/hcv/4_coding_macse.fas_DNA.fasta.nex.csv"), 1, 1, true, "Syn rates hcv 4"); 
            //   MappableData synDataHCV6 = new MappableData(new File("C:/hcv/6_coding_alignment_77.fas"), new File("C:/hcv/6_coding_alignment_77.nex.csv"), 1, 1, true, "Syn rates hcv 6"); 
            //   ArrayList<MappedData> dataSources = new ArrayList<>();
            // dataSources.add(MappedData.getMappedData(sequenceFile,nucleotideData, 1000, false));
            //   dataSources.add(MappedData.getMappedData(sequenceFile,synDataHCV1, 1000, false));
            //  dataSources.add(MappedData.getMappedData(sequenceFile, synDataHCV1a, 1000, false));
            //  dataSources.add(MappedData.getMappedData(sequenceFile,synDataHCV1b, 1000, false));
            //  dataSources.add(MappedData.getMappedData(sequenceFile,synDataHCV2, 1000, false));
            // dataSources.add(MappedData.getMappedData(sequenceFile, synDataHCV3, 1000, false));
            // dataSources.add(MappedData.getMappedData(sequenceFile,synDataHCV4, 1000, false));
            // dataSources.add(MappedData.getMappedData(sequenceFile, synDataHCV6, 1000, false));

            //ArrayList<MappedData> dataSources = new ArrayList<>();
            // File outFile = new File("C:/dev/thesis/hiv_full/hiv_not_siv_permutations2.txt");
            // String[] categories = {"hiv1b", "hiv1c", "hiv1d", "hiv1g", "hiv1o", "hiv2"};
            //File structureAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn");
            // File sequenceFile = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas");
            //File referenceFile = new File("C:/dev/thesis/hiv_full/hiv1-reference.fasta");
            //dataSources.add(hivMapping(sequenceFile));

            //   ArrayList<MappedData> dataSources = new ArrayList<>();
            // File outFile = new File("C:/dev/thesis/bvdv_csfv/bvdv_and_csfv_permutations.txt");
            // String[] categories = {"bvdv", "csfv"};
            //  File structureAlignment = new File("C:/dev/thesis/bvdv_csfv/bvdv_and_csfv.dbn");
            // File sequenceFile = new File("C:/dev/thesis/bvdv_csfv/bvdv_and_csfv.fas");
            // File referenceFile = new File("C:/dev/thesis/bvdv_csfv/bvdv_and_csfv.fas");
            //dataSources.add(hivMapping(sequenceFile));


            System.out.println(combinations(categories));
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);


            /*
             * ArrayList<SecondaryStructureData> structuralAlignment = new
             * ArrayList<SecondaryStructureData>();
             * structuralAlignment.addAll(structureData.subList(0, 25));
             * double[] mountainSumPlot = mountainSumPlot(structuralAlignment);
             * for (int i = 0; i < mountainSumPlot.length; i++) {
             * System.out.println(i + "\t" + mountainSumPlot[i]); }
             * System.exit(0);
             */

            ArrayList<Combination> combinations = combinations(categories);
            for (int i = 0; i < combinations.size(); i++) {
                Combination combination = combinations.get(i);
                 if (combination.combination.size() >=4) {
               // if (combination.combination.size() <= 2 || combination.combination.size() == categories.length) {
                    //if (combination.combination.size() == 4) {
                } else {
                    combinations.remove(i);
                    i--;
                }
            }

            /*
             * File outFile = new
             * File("C:/dev/thesis/hiv_full/hiv_full_permutations_75.txt");
             * String[] categories = {"hiv1b", "hiv1c", "hiv1d", "hiv1g",
             * "hiv1o", "hiv2", "siv"}; String[] categories_hiv1 = {"hiv1b",
             * "hiv1c", "hiv1d", "hiv1g", "hiv1o"}; String[]
             * categories_hiv1_hiv2 = {"hiv1b", "hiv1c", "hiv1d", "hiv1g",
             * "hiv1o", "hiv2"}; System.out.println(combinations(categories));
             * ArrayList<SecondaryStructureData> structureData =
             * FileImport.loadStructures(new
             * File("C:/dev/thesis/hiv_full/hiv_full_aligned.dbn"),
             * DataType.FileFormat.VIENNA_DOT_BRACKET);
             *
             * ArrayList<Combination> combinations = combinations(categories);
             * for (int i = 0; i < combinations.size(); i++) { Combination
             * combination = combinations.get(i); if
             * (combination.combination.size() <= 2 ||
             * combination.combination.size() == categories.length) { } else {
             * combinations.remove(i); i--; } } combinations.add(new
             * Combination(categories_hiv1)); combinations.add(new
             * Combination(categories_hiv1_hiv2));
             */

            /*
             * File outFile = new File("C:/dev/thesis/hcv/hcv_75_2.txt");
             * String[] categories = {"hcv1a", "hcv1b", "hcv2a", "hcv2b",
             * "hcv3", "hcv4", "hcv6"}; //String[] categories_hiv1 = {"hiv1b",
             * "hiv1c", "hiv1d", "hiv1g", "hiv1o"}; // String[]
             * categories_hiv1_hiv2 = {"hiv1b", "hiv1c", "hiv1d", "hiv1g",
             * "hiv1o", "hiv2"}; System.out.println(combinations(categories));
             * ArrayList<SecondaryStructureData> structureData =
             * FileImport.loadStructures(new
             * File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"),
             * DataType.FileFormat.VIENNA_DOT_BRACKET);
             *
             * ArrayList<Combination> combinations = combinations(categories);
             * for (int i = 0; i < combinations.size(); i++) { Combination
             * combination = combinations.get(i); if
             * (combination.combination.size() <= 2 ||
             * combination.combination.size() == categories.length) { } else {
             * combinations.remove(i); i--; } }
             */



        ArrayList<String> pairMustContain = new ArrayList<>();
         pairMustContain = new ArrayList<>();
            postAnalysis(outFile, outFile, categories, combinations, structureData, windowSize, pairMustContain, sequenceFile, referenceFiles, dataSources, circular);
           for (int i = 0; i < categories.length; i++) {
                pairMustContain = new ArrayList<>();
                pairMustContain.add(categories[i]);
                postAnalysis(outFile, outFile, categories, combinations, structureData, windowSize, pairMustContain, sequenceFile, referenceFiles, dataSources, circular);
            }
           
            System.exit(0);
            //ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(new File("C:/dev/thesis/dengue2/50/dengue2_all_50_aligned_partial_structurealign.fas.dbn"), DataType.FileFormat.VIENNA_DOT_BRACKET);
            //ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(new File("C:/dev/thesis/full-alignment.dbn"), DataType.FileFormat.VIENNA_DOT_BRACKET);

            ArrayList<StructureItem> structures = new ArrayList<>();
            for (SecondaryStructureData d : structureData) {
                StructureItem item = new StructureItem();
                item.pairedSites = d.pairedSites;
                item.sequence = d.sequence;
                item.title = d.title;
                String[] split = d.title.split("_");
                item.organism = split[split.length - 1];
                structures.add(item);
            }

            Random random = new Random(4809130618718489104L);

            HashSet<Pair<StructureItem, StructureItem>> pairwiseComparisons = new HashSet<>();
            BufferedWriter buffer = new BufferedWriter(new FileWriter(outFile));

            ArrayList<String> catList = new ArrayList<>();
            for (int i = 0; i < categories.length; i++) {
                catList.add(categories[i]);
            }
            double[] catCount = new double[categories.length];
            double[] catPerc = new double[categories.length];
            double catFrac = 1 / (double) catCount.length;
            double t = 0;

            StructureAlign sal = new StructureAlign();
            for (int a = 0; a < 10000000;) {
                int i = random.nextInt(structures.size());
                int j = random.nextInt(structures.size());

                StructureItem s1 = structures.get(i);
                StructureItem s2 = structures.get(j);
                if (i == j || pairwiseComparisons.contains(new Pair<>(s1, s2))) {
                    continue;
                }

                // System.out.println();
                //System.out.println(catList+"\t"+s1.organism);
                int cati = catList.indexOf(s1.organism);
                int catj = catList.indexOf(s2.organism);
                if(catList.size() == 1)
                {
                    catj=cati;
                    balance = false;
                }
                if (balance) {
                    if (catPerc[cati] > catFrac || catPerc[catj] > catFrac) { // balance for the fact that we have an uneven number of sequences from each category
                        continue;
                    }
                }


                if(balance)
                {
                    catCount[cati]++;
                    catCount[catj]++;
                    t += 2;
                    for (int c = 0; c < catPerc.length; c++) {
                        catPerc[c] = catCount[c] / t;
                    }
                    if (a % 10 == 0) {
                        for (int c = 0; c < catPerc.length; c++) {
                            System.out.println(catList.get(c) + "\t" + catPerc[c]);
                        }
                        System.out.println();
                    }
                }
                a++;

                //double[] pairwiseSimilarity = StructureAlign.slidingWeightedMountainSimilarity(s1.pairedSites, s2.pairedSites, windowSize, true);

                // double[] pairwiseSimilarity = StructureAlign.permutationTestSliding(s1.pairedSites, s2.pairedSites, windowSize, true, -1);
                double[] pairwiseSimilarity = null;
                if(circular)
                {
                    pairwiseSimilarity = sal.parallelizedPermutationTestSlidingCircular(s1.pairedSites, s2.pairedSites, s1.sequence, windowSize, true, permutations);
                }
                else
                {
                    pairwiseSimilarity = sal.parallelizedPermutationTestSliding(s1.pairedSites, s2.pairedSites, s1.sequence, windowSize, true, permutations);
                }
                String[] split1 = s1.title.split("_");
                String[] split2 = s2.title.split("_");
                String o1 = split1[split1.length - 1];
                String o2 = split2[split2.length - 1];
                buffer.write(i + "_" + j + "\t" + o1 + "_" + o2 + "\t");
                for (int p = 0; p < pairwiseSimilarity.length; p++) {
                    buffer.write(pairwiseSimilarity[p] + "\t");
                }
                buffer.newLine();
                pairwiseComparisons.add(new Pair<>(s1, s2));

            }
            buffer.close();

            /*
             * double[][] data = new double[structures.get(0).pairedSites.length
             * - windowSize][pairwiseComparisons.size()]; int c = 0; for (int i
             * = 0; i < structures.size(); i++) { StructureItem s1 =
             * structures.get(i); for (int j = i + 1; j < structures.size();
             * j++) { StructureItem s2 = structures.get(j); Pair<StructureItem,
             * StructureItem> key = new Pair<>(s1, s2); double[]
             * pairwiseSimilarity = pairwiseComparisons.get(key); for (int k =
             * 0; k < data.length; k++) {
             * //System.out.println(k+"\t"+c+"\t"+data.length+"\t"+pairwiseSimilarity.length);
             * data[k][c] = pairwiseSimilarity[k]; } c++; } }
             * System.out.println("pairs = " + c);
             *
             * for (int i = 0; i < data.length; i++) { Arrays.sort(data[i]);
             * System.out.println(i + "\t" + data[i][data[i].length / 4] + "\t"
             * + data[i][data[i].length / 2] + "\t" + data[i][data[i].length / 4
             * * 3]); }
             */

        } catch (ParserException ex) {
            Logger.getLogger(PairwiseStructureComparison.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PairwiseStructureComparison.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PairwiseStructureComparison.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public double[] getPercentGaps(ArrayList<SecondaryStructureData> structureData, int windowSize) {
        double[] array = new double[structureData.get(0).pairedSites.length];
        double[] sliding = new double[array.length - windowSize];
        double count = 0;
        for (SecondaryStructureData s : structureData) {
            for (int i = 0; i < s.pairedSites.length; i++) {
                if (s.sequence.charAt(i) == '-') {
                    array[i]++;
                }
            }
            count++;
        }
        for (int i = 0; i < array.length; i++) {
            array[i] /= count;
        }
        for (int i = 0; i < sliding.length; i++) {
            for (int j = i; j < i + windowSize; j++) {
                sliding[i] += array[j];
            }
            sliding[i] /= (double) windowSize;
        }
        return sliding;
    }

    public static class Combination {

        public ArrayList<String> combination;

        public boolean startsWith(String startsWith) {
            for (String s : combination) {
                if (startsWith.startsWith(s)) {
                    return true;
                }
            }
            return false;
        }

        public boolean startsWith(String s1, String s2) {
            return startsWith(s1) && startsWith(s2);
        }

        public Combination(String[] combination) {
            this.combination = new ArrayList<>();
            for (int i = 0; i < combination.length; i++) {
                this.combination.add(combination[i]);
            }
        }

        public boolean contains(String s1) {
            //return combination.contains(s1);
            return startsWith(s1);
        }

        public boolean contains(String s1, String s2) {
            //return combination.contains(s1) && combination.contains(s2);
            return startsWith(s1) && startsWith(s2);
        }

        @Override
        public String toString() {
            String ret = "";
            for (int i = 0; i < combination.size(); i++) {
                ret += combination.get(i);
                if (i != combination.size() - 1) {
                    ret += "_";
                }
            }
            return ret;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Combination other = (Combination) obj;
            if (!Objects.equals(this.combination, other.combination)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode(this.combination);
            return hash;
        }
    }

    public static String leftPad(String s, char padChar, int length) {
        String ret = s;
        for (int i = 0; i < length - s.length(); i++) {
            ret = padChar + ret;
        }
        return ret;
    }

    public static ArrayList<Combination> combinations(String[] s) {
        /*
         * String [] s = new String[t.length*2]; for(int i = 0 ; i < t.length ;
         * i++) { s[i] = t[i]; }
         *
         * for(int i = t.length ; i < s.length ; i++) { s[i] = t[i-t.length]; }
         */

        ArrayList<Combination> possibilities = new ArrayList<>();

        for (int i = 1; i < Math.pow(2, s.length); i++) {
            String p = leftPad(Integer.toBinaryString(i), '0', s.length);
            int c = 0;
            for (int j = 0; j < p.length(); j++) {
                if (p.charAt(j) == '1') {
                    c++;
                }
            }
            String[] q = new String[c];
            c = 0;
            for (int j = 0; j < p.length(); j++) {
                if (p.charAt(j) == '1') {
                    q[c] = s[j];
                    c++;
                }
            }
            Arrays.sort(q);
            if (!possibilities.contains(new Combination(q))) {
                possibilities.add(new Combination(q));
            }
        }
        return possibilities;
    }

    public void postAnalysis(File inFile, File outFile, String[] categories, ArrayList<Combination> combinations, ArrayList<SecondaryStructureData> structureData, int windowSize, ArrayList<String> pairMustContain, File sequenceFile, ArrayList<File> referenceFiles, ArrayList<MappedData> dataSources, boolean circular) {


        double gapPerc = 0.3;
        double pvalCutoff = 0.05;

        
        
        //Mapping alignmentToReference = ;
        ArrayList<Mapping> alignmentToReferenceMappings = new ArrayList<>();
        for(File referenceFile : referenceFiles)
        {
            alignmentToReferenceMappings.add(Mapping.createMapping(sequenceFile, referenceFile, 1000, false));
        }
        
        
        String mustHave = "";

        if (pairMustContain.size() > 0) {
            mustHave = "_pairmusthave";
            for (int i = 0; i < pairMustContain.size(); i++) {
                mustHave += "_" + pairMustContain.get(i);
            }
        }

        for (Combination combination : combinations) {
            if(!(combination.toString().equals("hiv1b_hiv1c_hiv1d_hiv1g") || combination.toString().equals("hiv1b_hiv1c_hiv1d_hiv1g_hiv1o") || combination.toString().equals("hiv1b_hiv1c_hiv1d_hiv1g_hiv1o_hiv2")))
            {
                continue;
            }
            
            ArrayList<SecondaryStructureData> structureData2 = (ArrayList<SecondaryStructureData>) structureData.clone();
            for (SecondaryStructureData d : structureData) {
                StructureItem item = new StructureItem();
                item.pairedSites = d.pairedSites;
                item.sequence = d.sequence;
                item.title = d.title;
                String[] split = d.title.split("_");
                item.organism = split[split.length - 1];
                
                if (!combination.contains((item.organism))) {

                    System.out.println("Removing " + d.title + "\t" + item.organism + "\t" + combination);
                    structureData2.remove(d);
                } else {
                    System.out.println("Not removing " + d.title + "\t" + item.organism + "\t" + combination);
                }
            }

            if (structureData2.size() == 0) {
                return;
            }

            double[] gaps = getPercentGaps(structureData2, windowSize);

            boolean mayContinue = true;
            for (int i = 0; i < pairMustContain.size(); i++) {
                if (!combination.contains(pairMustContain.get(i))) {
                    mayContinue = false;
                }
            }
            if (!mayContinue) {
                continue;
            }
           

            ArrayList<Integer> structures = new ArrayList<>();
            try {
                BufferedReader buffer = new BufferedReader(new FileReader(inFile));
                BufferedWriter writer2 = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave + "_" + pvalCutoff + "_structures.txt"));
                double totalPairs = 0;
                String textline = null;
                ArrayList<double[]> zscores = new ArrayList<>();
                ArrayList<double[]> simscores = new ArrayList<>();
                int dataLength = -1;
                long startTime = System.currentTimeMillis();
                while ((textline = buffer.readLine()) != null) {
                    String[] split = textline.split("(\\s)+");
                    //ArrayList<Double> values = new ArrayList<>();
                    dataLength = dataLength == -1 ? split.length - 2 : dataLength;
                    double[] values = new double[dataLength];
                    int x = Integer.parseInt(split[0].split("_")[0]);
                    int y = Integer.parseInt(split[0].split("_")[1]);

                    String o1 = split[1].split("_")[0];
                    String o2 = split[1].split("_")[1];

                    mayContinue = true;
                    for (int i = 0; i < pairMustContain.size(); i++) {
                        // exactly one of the pair must contain value i
                        //System.out.println(pairMustContain.get(i)+"\t"+o1+"\t"+pairMustContain.get(i)+o2);
                        if (!(o1.startsWith(pairMustContain.get(i)) ^ o2.startsWith(pairMustContain.get(i))))
                                {
                          //  pairMustContain.get(i).equals(o1) ^ pairMustContain.get(i).equals(o2))) {
                            mayContinue = false;
                            break;
                        }
                    }

                    if (!mayContinue) {
                        continue;
                    }

                    if (combination.contains(o1, o2)) {

                        for (int i = 2; i < split.length; i++) {
                            values[i - 2] = Double.parseDouble(split[i]);
                        }
                        if (dataLength == split.length - 2) // if last line too short because of file I/O do not add
                        {
                            zscores.add(values);
                        }

                        writer2.write(RNAFoldingTools.getDotBracketStringFromPairedSites(structureData.get(x).pairedSites));
                        writer2.newLine();
                        writer2.write(RNAFoldingTools.getDotBracketStringFromPairedSites(structureData.get(y).pairedSites));
                        writer2.newLine();

                        // System.out.println(combination);
                        // System.out.println(o1 + "\t" + x);
                        // System.out.println(o2 + "\t" + y);
                        structures.add(x);
                        structures.add(y);
                        
                        double [] pairwiseSim = StructureAlign.slidingWeightedMountainSimilarity(structureData.get(x).pairedSites, structureData.get(y).pairedSites, windowSize, circular);
                         double [] pairwiseSimOffset = new double[pairwiseSim.length+windowSize];
                         for(int i = 0 ; i < pairwiseSim.length ; i++)
                         {
                             pairwiseSimOffset[i+(windowSize/2)] = pairwiseSim[i];
                         }
                         simscores.add(pairwiseSimOffset);
                        

                        if (zscores.size() >= 10000) {
                            break;
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                double elapsedTime = (double) (endTime - startTime) / 1000.0;
                System.out.println("Elapsed time = " + zscores.size() + "\t" + elapsedTime);
                buffer.close();
                System.out.println(combination + "\t" + zscores.size());

                ArrayList<Double> medianPvals = new ArrayList<>();


                if (zscores.size() > 0) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave + "_" + pvalCutoff));
                    for (int i = 0; i < zscores.get(0).length; i++) {
                        ArrayList<Double> valuesAtPos = new ArrayList<>();
                        for (int j = 0; j < zscores.size(); j++) {
                            valuesAtPos.add(zscores.get(j)[i]);
                        }
                        
                        ArrayList<Double> simValuesAtPos = new ArrayList<>();
                        for (int j = 0; j < simscores.size(); j++) {
                            simValuesAtPos.add(simscores.get(j)[i]);
                        }

                        double medianPval = RankingAnalyses.getMedian(valuesAtPos);
                        double percentile25 = StatUtils.getInvCDF(RankingAnalyses.getPercentile(valuesAtPos, 0.25) / 2, true);
                        double percentile75 = StatUtils.getInvCDF(RankingAnalyses.getPercentile(valuesAtPos, 0.75) / 2, true);
                        double percentile90 = StatUtils.getInvCDF(RankingAnalyses.getPercentile(valuesAtPos, 0.90) / 2, true);
                        double zscore = StatUtils.getInvCDF(medianPval / 2, true);
                        double medianSimscore = RankingAnalyses.getMedian(simValuesAtPos);
                        medianPvals.add(medianPval);
                        writer.write(i + "\t" + gaps[i%gaps.length] + "\t" + medianPval + "\t" + zscore + "\t" + (gaps[i%gaps.length] >= gapPerc ? "1\t0\t0\t0\t0" : medianPval + "\t" + zscore + "\t" + percentile25 + "\t" + percentile75 + "\t" + percentile90) +"\t"+medianSimscore);
                        //writer.write(i + "\t" + gaps[i] + "\t" + medianPval + "\t" + zscore + "\t" + (gaps[i] >= 0.2 ? "1\t0" : medianPval + "\t" + zscore));

                        writer.newLine();

                        //System.out.println(i + "\t" + RankingAnalyses.getMedian(valuesAtPos));
                    }
                    writer.close();
                }

                int realLength = structureData.get(0).pairedSites.length;
                int arrayLength = realLength+windowSize;
                //boolean[] inWindow = new boolean[structureData.get(0).pairedSites.length];
                boolean[] inWindow = new boolean[arrayLength];
                for (int i = 0; i < medianPvals.size(); i++) {
                    if (gaps[i%gaps.length] < gapPerc && medianPvals.get(i) <= pvalCutoff) {
                        System.out.println("FAV\t"+i+"\t"+inWindow.length+"\t"+medianPvals.get(i));
                        for (int j = i; j < i + windowSize; j++) {
                            // OVERHEREA AAA
                            inWindow[j] = true;
                        }
                    }
                }
                for (int i = 0; i < medianPvals.size(); i++) {
                    if (gaps[i%gaps.length] >= gapPerc) {
                        writer2.write('-');
                    } else if (medianPvals.get(i) <= pvalCutoff) {
                        writer2.write('*');
                    } else {
                        writer2.write(' ');
                    }
                }
                writer2.newLine();

                for (int i = 0; i < inWindow.length; i++) {
                    if (inWindow[i]) {
                        writer2.write('*');
                    } else {
                        writer2.write(' ');
                    }
                }
                writer2.newLine();

                int start = -1;
                int end = -1;
                ArrayList<Substructure> substructures = new ArrayList<>();
                for (int i = 0; i < inWindow.length; i++) {
                    if (inWindow[i] && (i == 0 || !inWindow[i - 1])) {
                        start = i;
                    }

                    if (inWindow[i] && (i == inWindow.length - 1 || !inWindow[i + 1])) {
                        end = i;
                        int length = end - start + 1;
                        substructures.add(new Substructure(start, length));
                        //System.out.println(start+"-"+end);
                    }
                }


                //DecimalFormat df = new DecimalFormat("0.000");
                File substructureFile = new File(outFile.getAbsolutePath() + "_" + combination + mustHave + "_" + pvalCutoff + ".substructures");
                BufferedWriter substructureWriter = new BufferedWriter(new FileWriter(substructureFile));
                for (Substructure substructure : substructures) {
                    ArrayList<Double> pvalues = new ArrayList<>();
                    System.out.println("JXJA"+substructureFile+"\t"+substructure.start+"\t"+(substructure.start + substructure.length - windowSize + 1));
                    for (int i = substructure.start; i < substructure.start + substructure.length - windowSize + 1; i++) {
                        pvalues.add(medianPvals.get(i));
                        System.out.println(i+"\t"+medianPvals.get(i));
                    }
                    //1250
                    int l = Math.max(0, (1000 - substructure.length) / 2);
                    System.out.println("l=" + l + "\t" + substructure.start + "\t" + substructure.length);
                    substructure.startMatrix = Math.max(0, substructure.start - l);
                    int endPos = Math.min(structureData.get(0).pairedSites.length, substructure.startMatrix + substructure.length + 2 * l);
                    if(circular)
                    {
                        endPos = substructure.startMatrix + substructure.length + 2 * l;
                    }
                    substructure.matrixLength = endPos - substructure.startMatrix;

                    substructure.medianPvalue = RankingAnalyses.getMedian(pvalues);
                    System.out.println(pvalues);

                    substructure.matrix = new double[substructure.matrixLength][substructure.matrixLength];
                    for (int a = 0; a < structures.size(); a++) {
                        SecondaryStructureData structure = structureData.get(structures.get(a));
                        int length = structure.pairedSites.length;
                        //int length = circular ? (structure.pairedSites.length*3)/2 : structure.pairedSites.length;
                        for (int x = 0; x < length; x++) {
                            int y = structure.pairedSites[x%structure.pairedSites.length] - 1;
                           // System.out.println("HITA "+x+"\t"+y+"\t"+structure.pairedSites.length+"\t"+length);                            
                            /*if(x >= structure.pairedSites.length && y >= 0)
                            {
                                y += structure.pairedSites.length;
                            }*/
                            
                           // System.out.println("HITB "+x+"\t"+y+"\t"+structure.pairedSites.length+"\t"+length);
                            if (x - substructure.startMatrix >= 0 && x - substructure.startMatrix < substructure.matrixLength) {
                                if (y - substructure.startMatrix >= 0 && y - substructure.startMatrix < substructure.matrixLength) {

                                    substructure.matrix[x - substructure.startMatrix][y - substructure.startMatrix]++;
                                    //System.out.println(x - substructure.start+"\t"+(y - substructure.start)+"\t"+substructure.matrix[x - substructure.start][y - substructure.start]);
                                }
                            }
                        }
                        
                        if(circular)
                        {
                            for (int x2 = 0; x2 < length; x2++) {
                                int x = x2+length;
                                int y = structure.pairedSites[x%structure.pairedSites.length] - 1;
                            // System.out.println("HITB "+x+"\t"+y+"\t"+structure.pairedSites.length+"\t"+length);
                                if (x - substructure.startMatrix >= 0 && x - substructure.startMatrix < substructure.matrixLength) {
                                    if (y - substructure.startMatrix >= 0 && y - substructure.startMatrix < substructure.matrixLength) {

                                        substructure.matrix[x - substructure.startMatrix][y - substructure.startMatrix]++;
                                        //System.out.println(x - substructure.start+"\t"+(y - substructure.start)+"\t"+substructure.matrix[x - substructure.start][y - substructure.start]);
                                    }
                                }
                            }

                            for (int x2 = 0; x2 < length; x2++) {
                            int x = x2;
                            int y = structure.pairedSites[x%structure.pairedSites.length] - 1;
                            if(y != -1)
                            {
                                y += length;
                            }
                            // System.out.println("HITB "+x+"\t"+y+"\t"+structure.pairedSites.length+"\t"+length);
                            if (x - substructure.startMatrix >= 0 && x - substructure.startMatrix < substructure.matrixLength) {
                                if (y - substructure.startMatrix >= 0 && y - substructure.startMatrix < substructure.matrixLength) {

                                    substructure.matrix[x - substructure.startMatrix][y - substructure.startMatrix]++;
                                    //System.out.println(x - substructure.start+"\t"+(y - substructure.start)+"\t"+substructure.matrix[x - substructure.start][y - substructure.start]);
                                }
                            }
                        }
                    }
                    }
                    DecimalFormat df = new DecimalFormat("0.000");
                    double t = structures.size();
                    for (int x = 0; x < substructure.matrix.length; x++) {
                        for (int y = 0; y < substructure.matrix[0].length; y++) {
                            substructure.matrix[x][y] /= t;
                        }
                    }
                    System.out.println("Substructure start = " + substructure.startMatrix+"\t"+substructure.start);
                    System.out.println("Start posterior decoding " + substructure.matrix.length);
                    substructure.pairedSites = PosteriorDecodingTool.getPosteriorDecodingConsensusStructure(substructure.matrix);
                    if(circular)
                    {
                        // this may need to be  fixed
                         substructure.pairedSites = StructureAlign.getSubstructureCircular(substructure.pairedSites, substructure.start - substructure.startMatrix, substructure.length);
                    }
                    else
                    {
                          substructure.pairedSites = StructureAlign.getSubstructure(substructure.pairedSites, substructure.start - substructure.startMatrix, substructure.length);
                    }
                    System.out.println("End posterior decoding " + substructure.matrix.length);
                    System.out.println(substructure.toString());
                    substructureWriter.write(substructure.toString());
                    
                    for(int i = 0 ; i < alignmentToReferenceMappings.size() ; i++)
                    {
                        Mapping alignmentToReference = alignmentToReferenceMappings.get(i);
                        substructureWriter.write(referenceFiles.get(i).toString()+"\n");
                        int startMapped = alignmentToReference.aToBNearest(substructure.start%realLength) + 1;
                        int endMapped = alignmentToReference.aToBNearest((substructure.start + substructure.length-1)%realLength)+1;
                        substructureWriter.write((substructure.start%realLength+1) + "-" + ((substructure.start + substructure.length-1)%realLength+1)+ "\n");
                        substructureWriter.write(startMapped + "-" + endMapped + "\n\n");
                    }
                    substructureWriter.write("p-value=" + df.format(substructure.medianPvalue) + "\n");
                    substructureWriter.write("z-score=" + df.format(StatUtils.getInvCDF(substructure.medianPvalue / 2, true)) + "\n");
                    for (MappedData mappedData : dataSources) {
                        ArrayList<Double> values = new ArrayList<>();
                        ArrayList<Double> allValues = new ArrayList<>();
                        for (int i = substructure.start; i < substructure.start + substructure.length; i++) {
                            if (mappedData.used[i]) {
                                values.add(mappedData.values[i]);
                            }
                        }
                        for (int i = 0; i < mappedData.used.length; i++) {
                            if (mappedData.used[i]) {
                                allValues.add(mappedData.values[i]);
                            }
                        }

                        substructureWriter.write(mappedData.name + "\n");
                        substructureWriter.write(df.format(RankingAnalyses.getMedian(values)) + "\n");
                        substructureWriter.write(df.format(RankingAnalyses.getMedian(allValues)) + "\n");
                        MyMannWhitney mw = new MyMannWhitney(values, allValues);
                        substructureWriter.write(df.format(mw.getZ()) + "\n");
                    }
                    substructureWriter.newLine();
                }
                substructureWriter.close();

                BufferedWriter svgWriter = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave + "_" + pvalCutoff + ".svg"));
                svgWriter.write(getSVGRepresentationSubstructure(substructures, structureData.get(0).pairedSites.length, combination.toString() + mustHave));
                svgWriter.close();


                BufferedWriter matrixWriter = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave + "_" + pvalCutoff + ".matrix"));
                String structure = "";
                double[] pairingProbability = new double[structureData.get(0).pairedSites.length];
                for (int i = 0; i < structureData.get(0).pairedSites.length; i++) {
                    for (Substructure substructure : substructures) {
                        if (substructure.start == i) {
                            structure += RNAFoldingTools.getDotBracketStringFromPairedSites(substructure.pairedSites);

                            for (int j = i; j < i + substructure.length; j++) {
                                pairingProbability[j%pairingProbability.length] = substructure.pairingProbability[(j - i)%substructure.pairingProbability.length];
                            }
                            i += substructure.length;

                            for (int x = substructure.start - substructure.startMatrix; x < substructure.start - substructure.startMatrix + substructure.length; x++) {
                                for (int y = substructure.start - substructure.startMatrix; y < substructure.start - substructure.startMatrix + substructure.length; y++) {
                                    //pairingProbability[x - (substructure.start - substructure.startMatrix)] += substructure.matrix[x][y];
                                    if (substructure.matrix[x%substructure.matrixLength][y%substructure.matrixLength] != 0) {
                                        matrixWriter.write((x + substructure.startMatrix) + "," + (y + substructure.startMatrix) + "," + substructure.matrix[x%substructure.matrixLength][y%substructure.matrixLength]);
                                        matrixWriter.newLine();
                                    }
                                }
                            }


                            /*
                             * for (int x = 0; x < substructure.length; x++) {
                             * for (int y = 0; y < substructure.length; y++) {
                             * if (substructure.matrix[x][y] != 0) {
                             * matrixWriter.write((substructure.start + x) + ","
                             * + (substructure.start + y) + "," +
                             * substructure.matrix[x][y]);
                             * matrixWriter.newLine(); } } }
                             */
                        }

                    }
                    structure += "#";
                }

                matrixWriter.close();


                System.out.println(structure);
                writer2.write(structure);
                writer2.newLine();
                writer2.close();

                BufferedWriter csvWriter = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + combination + mustHave + "_" + pvalCutoff + ".csv"));
                csvWriter.write("Position,Probability\n");
                for (int i = 0; i < pairingProbability.length; i++) {
                    csvWriter.write((i + 1) + "," + pairingProbability[i] + "\n");
                }
                csvWriter.close();

                BufferedWriter allWriter = new BufferedWriter(new FileWriter(outFile.getAbsolutePath() + "_" + pvalCutoff + "_summary.txt", true));
                allWriter.write(">" + combination + mustHave);
                allWriter.newLine();
                allWriter.write(structure);
                allWriter.newLine();
                allWriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class Substructure {

        int start;
        int startMatrix;
        int length;
        int matrixLength;
        double[][] matrix;
        int[] pairedSites;
        double medianPvalue;
        double[] pairingProbability;
        DecimalFormat df = new DecimalFormat("0.000");

        public Substructure(int start, int length) {
            this.start = start;
            this.length = length;
        }

        public void calculatePairingProbability(double[][] matrix) {
            pairingProbability = new double[length];

            /*
             * for (int i = start - startMatrix; i < start - startMatrix +
             * length; i++) { for (int j = start - startMatrix; j < start -
             * startMatrix + length; j++) { pairingProbability[i - (start -
             * startMatrix)] += matrix[i][j]; } }
             */

            for (int i = 0; i < matrixLength; i++) {
                for (int j = 0; j < matrixLength; j++) {
                    if (i - (start - startMatrix) >= 0 && i - (start - startMatrix) < length) {
                        pairingProbability[i - (start - startMatrix)] += matrix[i][j];
                    }
                }
            }
        }

        @Override
        public String toString() {
            String ret = "";
            ret = start + " - " + (start + length) + " (" + medianPvalue + ")" + " : " + RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites);
            ret += "\n";
            calculatePairingProbability(matrix);
            String dbn = RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites);
            for (int i = 0; i < pairingProbability.length; i++) {
                int y = pairedSites[i];
                double p = 0;
                if (y != 0) {

                    p = matrix[i + (start - startMatrix)][y - 1 + (start - startMatrix)];
                }
                System.out.println(i + "\t" + dbn.charAt(i) + "\t" + df.format(p) + "\t" + df.format(pairingProbability[i]));
            }
            return ret;
        }
    }

    public static String getSVGRepresentationSubstructure(ArrayList<Substructure> substructures, int length, String label) {
        int panelWidth = 1000;
        int panelHeight = 11;


        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("<?xml version=\"1.0\" standalone=\"no\"?>");
        pw.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \n\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
        pw.println("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + panelWidth + "\" height=\"" + panelHeight + "\" style=\"fill:none;stroke-width:16\">");


        int fontSize = 9;

        pw.println("<text x=\"" + (-5) + "\" y=\"" + ((panelHeight / 2) + (fontSize / 2)) + "\" style=\"font-size:" + fontSize + "px;stroke:none;fill:black\" text-anchor=\"" + "end" + "\">");
        pw.println("<tspan>" + label + "</tspan>");
        pw.println("</text>");
        pw.println("<g>");
        pw.println("<rect x=\"" + (0) + "\" y=\"" + 0 + "\" width=\"" + (panelWidth) + "\" height=\"" + (panelHeight) + "\"  style=\"fill:#" + GraphicsUtils.getHexString(Color.white) + ";\"/>");
        for (Substructure substructure : substructures) {
            double x = ((double) substructure.start / (double) length) * panelWidth;
            double width = ((double) substructure.length / (double) length) * panelWidth;

            DataTransform transform = new DataTransform(0.0001, 0.5, TransformType.NORMSINV1);
            ColorGradient gradient = new ColorGradient(Color.darkGray, Color.white);

            System.out.println(">>>" + substructure.medianPvalue + "\t" + gradient.getColor(transform.transform((float) substructure.medianPvalue)) + "\t" + transform.transform((float) substructure.medianPvalue));

            if(x+width < panelWidth)
            {
                pw.println("<rect x=\"" + (x) + "\" y=\"" + 0 + "\" width=\"" + (width) + "\" height=\"" + (panelHeight) + "\"  style=\"fill:#" + GraphicsUtils.getHexString(gradient.getColor(transform.transform((float) substructure.medianPvalue))) + ";\"/>");
            }
            else
            {
                pw.println("<rect x=\"" + (x) + "\" y=\"" + 0 + "\" width=\"" + (panelWidth-x) + "\" height=\"" + (panelHeight) + "\"  style=\"fill:#" + GraphicsUtils.getHexString(gradient.getColor(transform.transform((float) substructure.medianPvalue))) + ";\"/>");
                pw.println("<rect x=\"" + (0) + "\" y=\"" + 0 + "\" width=\"" + (x+width-panelWidth) + "\" height=\"" + (panelHeight) + "\"  style=\"fill:#" + GraphicsUtils.getHexString(gradient.getColor(transform.transform((float) substructure.medianPvalue))) + ";\"/>");
            }
        }
        pw.println("</g>");
        /*
         * pw.println("<text x=\"" + (x + xoffset + regionWidth / 2) + "\" y=\""
         * + (rulerHeight + feature.row * blockHeight + blockHeight / 2 +
         * (fontSize / 2)) + "\" style=\"font-size:" + fontSize +
         * "px;stroke:none;fill:black\" text-anchor=\"" + "middle" + "\" >");
         * pw.println("<tspan>" + feature.name + "</tspan>");
         * pw.println("</text>");
         */

        pw.println("</svg>");
        pw.close();
        //System.out.println(sw.toString());
        return sw.toString();
    }

    public static double[] mountainSumPlot(ArrayList<SecondaryStructureData> structuralAlignment) {
        double[] sum = new double[structuralAlignment.get(0).pairedSites.length];

        for (SecondaryStructureData structure : structuralAlignment) {
            double[] mountain = MountainMetrics.getMountainVector(structure.pairedSites, false);

            for (int i = 0; i < mountain.length; i++) {
                sum[i] += mountain[i];
                //System.out.print(sum[i] + "\t");
            }
            // System.out.println();
        }

        for (int i = 0; i < sum.length; i++) {
            sum[i] /= (double) structuralAlignment.size();
        }

        return sum;
    }

    public static void main(String[] args) {
        new PairwiseStructureComparison().runComparison();
    }

    public class StructureItem {

        String organism = "";
        String dotBracketStructure;
        int[] pairedSites;
        String sequence;
        String title;

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StructureItem other = (StructureItem) obj;
            if (!Objects.equals(this.organism, other.organism)) {
                return false;
            }
            if (!Objects.equals(this.dotBracketStructure, other.dotBracketStructure)) {
                return false;
            }
            if (!Arrays.equals(this.pairedSites, other.pairedSites)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + Objects.hashCode(this.organism);
            hash = 53 * hash + Objects.hashCode(this.dotBracketStructure);
            hash = 53 * hash + Arrays.hashCode(this.pairedSites);
            return hash;
        }
    }
}
