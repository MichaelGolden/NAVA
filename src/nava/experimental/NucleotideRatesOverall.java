/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.CsvReader;
import nava.data.io.FileImport;
import nava.data.types.DataType;
import nava.data.types.SecondaryStructureData;
import nava.ranking.MyMannWhitney;
import nava.ranking.RankingAnalyses;
import nava.structurevis.data.DataTransform;
import nava.structurevis.data.NHistogram;
import nava.structurevis.data.NHistogramPanel;
import nava.structurevis.data.NucleotideComposition;
import nava.utils.Mapping;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class NucleotideRatesOverall {
    
    public static class SynonymousTestData
    {
        File synonymousCSV;
        File synonymousAlignment;
        File structureAlignment;
        File sequenceAlignment;
        
        public SynonymousTestData(File synonymousCSV, File synonymousAlignment, File structureAlignment, File sequenceAlignment)
        {            
            this.synonymousCSV = synonymousCSV;
            this.synonymousAlignment = synonymousAlignment;
            this.structureAlignment = structureAlignment;
            this.sequenceAlignment = sequenceAlignment;
        }
    }
    
    public static void runTest(SynonymousTestData testData, String keyword) throws Exception
    {
        ArrayList<SecondaryStructureData> structureData2 = FileImport.loadStructures(testData.structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
        ArrayList<SecondaryStructureData> structureData = new ArrayList<SecondaryStructureData>();
        for(int i = 0 ; i < structureData2.size() ; i++)
        {
            if(keyword == null || structureData2.get(i).title.contains(keyword))
            {
                structureData.add(structureData2.get(i));
            }
        }
        
        System.out.println("structures "+structureData.size());
        if(structureData.size() == 0)
        {
            return;
        }
        //double [] pairedPercent = getPairedPercentage(structureData);
        
    double [] pairedPercent = getPairedPercentagePhylogeneticallyWeighted(structureData);
        
      /* Mapping mapping = Mapping.createMapping(testData.synonymousAlignment, testData.sequenceAlignment, 1000,false);
        ArrayList<String> values = CsvReader.getColumn(testData.synonymousCSV, 1);
        values.remove(0);
         System.out.println(values);*/
        ArrayList<Double> pairedValuesSiteAll = new ArrayList<>();
        ArrayList<Double> unpairedValuesSiteAll = new ArrayList<>();
        ArrayList<Double> all = new ArrayList<Double>();
        
        //MappedData data = MappingComparison.getHIVMappedData(testData.sequenceAlignment);
     MappedData data = MappedData.getMappedData(testData.sequenceAlignment, new MappableData(testData.synonymousAlignment, testData.synonymousCSV,1,1, false, "Nuc"), 1000, false);
    
        for(int i = 0 ; i < data.values.length ; i++)
        {
           // System.out.println(data.values[i]+"\t"+data.used[i]+"\t"+data.codon[i]);
            if(data.used[i])
            {
                if( pairedPercent[i] > 0.5)
                {
                    pairedValuesSiteAll.add(data.values[i]);
                }
                else
                {
                    unpairedValuesSiteAll.add(data.values[i]);
                }
                
                all.add(data.values[i]);
            }
        }
        MyMannWhitney siteAll = new MyMannWhitney(pairedValuesSiteAll, unpairedValuesSiteAll);
      
        DecimalFormat df = new DecimalFormat("0.000");
        DecimalFormat df2 = new DecimalFormat("0.00E0");
        System.out.println(">>>"+keyword);
        System.out.println("All\t"+pairedValuesSiteAll.size()+"\t"+unpairedValuesSiteAll.size()+"\t"+df.format(RankingAnalyses.getMedian(pairedValuesSiteAll))+"\t"+df.format(RankingAnalyses.getMedian(unpairedValuesSiteAll)) +"\t"+df.format(siteAll.getZ())+"\t"+df2.format(RankingAnalyses.NormalZ(siteAll.getZ())));
       
        double min = Math.min(Collections.min(pairedValuesSiteAll), Collections.min(unpairedValuesSiteAll));
        double max = Math.min(Collections.max(pairedValuesSiteAll), Collections.max(unpairedValuesSiteAll));
        NHistogramPanel panel = new NHistogramPanel();
        min = 0;
        max = 3;
        NHistogram nhist = new NHistogram(min, max, 10, new DataTransform(min, max, DataTransform.TransformType.LINEAR));
        nhist.addClass("Paired sites", Color.red, pairedValuesSiteAll);
        nhist.addClass("Unpaired sites", Color.blue, unpairedValuesSiteAll);
        nhist.calculate();
        panel.setNHistogram(nhist);
        panel.saveAsSVG(new File(keyword+"_histogram.svg"));
        
    }
    
    public static void main(String [] args)
    {
        try {
            
            SynonymousTestData hiv1bData = new SynonymousTestData(new File("C:/dev/thesis/hiv_full/1b/100/hiv1b_all_100_aligned.csv"),new File("C:/dev/thesis/hiv_full/1b/100/hiv1b_all_100_aligned.fas"),new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn"), new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas"));
            runTest(hiv1bData, "hiv1b");
            
             SynonymousTestData hiv1cData = new SynonymousTestData(new File("C:/dev/thesis/hiv_full/1c/100/hiv1c_all_100_aligned.csv"),new File("C:/dev/thesis/hiv_full/1c/100/hiv1c_all_100_aligned.fas"),new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn"), new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas"));
            runTest(hiv1cData, "hiv1c");
            
              SynonymousTestData hiv1dData = new SynonymousTestData(new File("C:/dev/thesis/hiv_full/1d/100/hiv1d_all_100_aligned.csv"),new File("C:/dev/thesis/hiv_full/1d/100/hiv1d_all_100_aligned.fas"),new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn"), new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas"));
            runTest(hiv1dData, "hiv1d");
            
             SynonymousTestData hiv1gData = new SynonymousTestData(new File("C:/dev/thesis/hiv_full/1g/100/hiv1g_all_100_aligned.csv"),new File("C:/dev/thesis/hiv_full/1g/100/hiv1g_all_100_aligned.fas"),new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn"), new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas"));
            runTest(hiv1gData, "hiv1g");
            
               SynonymousTestData hiv1oData = new SynonymousTestData(new File("C:/dev/thesis/hiv_full/1o/100/hiv1o_all_100_aligned.csv"),new File("C:/dev/thesis/hiv_full/1o/100/hiv1o_all_100_aligned.fas"),new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn"), new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas"));
            runTest(hiv1oData, "hiv1o");
            
               SynonymousTestData hiv2Data = new SynonymousTestData(new File("C:/dev/thesis/hiv_full/2/100/hiv2_all_100_aligned.csv"),new File("C:/dev/thesis/hiv_full/2/100/hiv2_all_100_aligned.fas"),new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn"), new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas"));
            runTest(hiv2Data, "hiv2");
            
            SynonymousTestData hiv1Data = new SynonymousTestData(new File("C:/dev/thesis/hiv_full/hiv1/100/hiv1_all_100_aligned.csv"),new File("C:/dev/thesis/hiv_full/hiv1/100/hiv1_all_100_aligned.fas"),new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn"), new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas"));
            runTest(hiv1Data, "hiv1");
       
           SynonymousTestData dengueData = new SynonymousTestData(new File("C:/dev/thesis/dengue/100/dengue_all_100_aligned_full.csv"),new File("C:/dev/thesis/dengue/100/dengue_all_100_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
           runTest(dengueData, "dengue");
            
            SynonymousTestData dengueData1 = new SynonymousTestData(new File("C:/dev/thesis/dengue1/100/dengue1_all_100_aligned.csv"),new File("C:/dev/thesis/dengue1/100/dengue1_all_100_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
            runTest(dengueData1, "dengue1");
          
            SynonymousTestData dengueData2 = new SynonymousTestData(new File("C:/dev/thesis/dengue2/100/dengue2_all_100_aligned.csv"),new File("C:/dev/thesis/dengue2/100/dengue2_all_100_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
          runTest(dengueData2, "dengue2");
           
            SynonymousTestData dengueData3 = new SynonymousTestData(new File("C:/dev/thesis/dengue3/100/dengue3_all_100_aligned.csv"),new File("C:/dev/thesis/dengue3/100/dengue3_all_100_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
         runTest(dengueData3, "dengue3");
           
             SynonymousTestData dengueData4 = new SynonymousTestData(new File("C:/dev/thesis/dengue4/100/dengue4_all_100_aligned.csv"),new File("C:/dev/thesis/dengue4/100/dengue4_all_100_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
           runTest(dengueData4, "dengue4");
           
           
            SynonymousTestData hcv1Data = new SynonymousTestData(new File("C:/dev/thesis/hcv/1/100/hcv1_all_100_aligned.csv"),new File("C:/dev/thesis/hcv/1/100/hcv1_all_100_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcv1Data, "hcv1");
            
            SynonymousTestData hcv1aData = new SynonymousTestData(new File("C:/dev/thesis/hcv/1a/100/hcv1a_all_100_aligned.csv"),new File("C:/dev/thesis/hcv/1a/100/hcv1a_all_100_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcv1aData, "hcv1a");
            
            SynonymousTestData hcv1bData = new SynonymousTestData(new File("C:/dev/thesis/hcv/1b/100/hcv1b_all_100_aligned.csv"),new File("C:/dev/thesis/hcv/1b/100/hcv1b_all_100_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcv1bData, "hcv1b");
             

            SynonymousTestData hcv2Data = new SynonymousTestData(new File("C:/dev/thesis/hcv/2/100/hcv2_all_100_aligned_edit.csv"),new File("C:/dev/thesis/hcv/2/100/hcv2_all_100_aligned_edit.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcv2Data, "hcv2");
            
            SynonymousTestData hcv2aData = new SynonymousTestData(new File("C:/dev/thesis/hcv/2a/100/hcv2a_all_100_aligned.csv"),new File("C:/dev/thesis/hcv/2a/100/hcv2a_all_100_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcv2aData, "hcv2a");
            
            SynonymousTestData hcv2bData = new SynonymousTestData(new File("C:/dev/thesis/hcv/2b/100/hcv2b_all_100_aligned.csv"),new File("C:/dev/thesis/hcv/2b/100/hcv2b_all_100_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
           runTest(hcv2bData, "hcv2b");
            
             SynonymousTestData hcv3Data = new SynonymousTestData(new File("C:/dev/thesis/hcv/3/100/hcv3_all_100_aligned.csv"),new File("C:/dev/thesis/hcv/3/100/hcv3_all_100_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcv3Data, "hcv3");
            
            SynonymousTestData hcv4Data = new SynonymousTestData(new File("C:/dev/thesis/hcv/4/100/hcv4_all_100_aligned.csv"),new File("C:/dev/thesis/hcv/4/100/hcv4_all_100_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcv4Data, "hcv4");
            
             SynonymousTestData hcv6Data = new SynonymousTestData(new File("C:/dev/thesis/hcv/6/100/hcv6_all_100_aligned.csv"),new File("C:/dev/thesis/hcv/6/100/hcv6_all_100_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcv6Data, "hcv6");
           
           
            
            SynonymousTestData jevData = new SynonymousTestData(new File("C:/dev/thesis/jev/100/jev_all_100_aligned.csv"),new File("C:/dev/thesis/jev/100/jev_all_100_aligned.fas"),new File("C:/dev/thesis/jev/jev-alignment.dbn"), new File("C:/dev/thesis/jev/jev-alignment.fas"));
            runTest(jevData, "jev");
            
             SynonymousTestData bvdvData = new SynonymousTestData(new File("C:/dev/thesis/bvdv/100/all_100_aligned.csv"),new File("C:/dev/thesis/bvdv/100/all_100_aligned.fas"),new File("C:/dev/thesis/bvdv/bvdv_all_aligned.dbn"), new File("C:/dev/thesis/bvdv/bvdv_all_aligned.fas"));
             runTest(bvdvData, "bvdv");
            
              SynonymousTestData westnileData = new SynonymousTestData(new File("C:/dev/thesis/westnile/100/westnile_all_100_aligned.csv"),new File("C:/dev/thesis/westnile/100/westnile_all_100_aligned.fas"),new File("C:/dev/thesis/westnile/westnile_all_200_aligned.dbn"), new File("C:/dev/thesis/westnile/westnile_all_200_aligned.fas"));
             runTest(westnileData, "westnile");
           
           
             SynonymousTestData tbvData = new SynonymousTestData(new File("C:/dev/thesis/tbv/100/tbv_all_100_aligned.csv"),new File("C:/dev/thesis/tbv/100/tbv_all_100_aligned.fas"),new File("C:/dev/thesis/tbv/all_tbv_aligned.dbn"), new File("C:/dev/thesis/tbv/all_tbv_aligned.fas"));
            runTest(tbvData, "tbv");
            
            SynonymousTestData csfvData = new SynonymousTestData(new File("C:/dev/thesis/csfv/100/all_100_aligned.csv"),new File("C:/dev/thesis/csfv/100/all_100_aligned.fas"),new File("C:/dev/thesis/csfv/csfv_all_aligned.dbn"), new File("C:/dev/thesis/csfv/csfv_all_aligned.fas"));
             runTest(csfvData, "csfv");
            
                   //MappableData nucleotideData = new MappableData(sequenceFile, new File("C:/dev/thesis/hcv/conservation/rates.csv"), 0, 1, false, "Nucleotide rates");
         
          //SynonymousTestData hcvData1 = new SynonymousTestData(new File("C:/dev/thesis/hcv/conservation/rates.csv"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
         //   runTest(hcvData1,null);
            
          //    SynonymousTestData dengueData = new SynonymousTestData(new File("C:/dev/thesis/dengue/300/site rates.csv"),new File("C:/dev/thesis/dengue/300/dengue_all_300_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
            //runTest(dengueData, null);
            /*// SynonymousTestData hivData = new SynonymousTestData(new File("C:/dev/thesis/jev/300/jev_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/jev/300/jev_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn"), new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas"));
           //runTest(hivData, null);
          // System.exit(0);
              SynonymousTestData dengueData = new SynonymousTestData(new File("C:/dev/thesis/dengue/300/dengue_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/dengue/300/dengue_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
            runTest(dengueData, null);
             SynonymousTestData dengueData1 = new SynonymousTestData(new File("C:/dev/thesis/dengue1/300/dengue1_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/dengue1/300/dengue1_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
            runTest(dengueData1, "dengue1");
            SynonymousTestData dengueData2 = new SynonymousTestData(new File("C:/dev/thesis/dengue2/300/dengue2_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/dengue2/300/dengue2_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
            runTest(dengueData2, "dengue2");
            SynonymousTestData dengueData3 = new SynonymousTestData(new File("C:/dev/thesis/dengue3/300/dengue3_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/dengue3/300/dengue3_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
            runTest(dengueData3, "dengue3");
             SynonymousTestData dengueData4 = new SynonymousTestData(new File("C:/dev/thesis/dengue4/250/dengue4_polyprotein_250_aligned.csv"),new File("C:/dev/thesis/dengue4/250/dengue4_polyprotein_250_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
            runTest(dengueData4, "dengue4");
               SynonymousTestData bvdvData = new SynonymousTestData(new File("C:/dev/thesis/bvdv/300/bvdv_polyprotein_300_aligned_trim.csv"),new File("C:/dev/thesis/bvdv/300/polyprotein_300_aligned_trim.fas"),new File("C:/dev/thesis/bvdv/bvdv_all_aligned.dbn"), new File("C:/dev/thesis/bvdv/bvdv_all_aligned.fas"));
            runTest(bvdvData, null);
             SynonymousTestData csfvData = new SynonymousTestData(new File("C:/dev/thesis/csfv/300/csfv_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/csfv/300/polyprotein_300_aligned.fas"),new File("C:/dev/thesis/csfv/csfv_all_aligned.dbn"), new File("C:/dev/thesis/csfv/csfv_all_aligned.fas"));
            runTest(csfvData, null);
            SynonymousTestData jevData = new SynonymousTestData(new File("C:/dev/thesis/jev/300/jev_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/jev/300/jev_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/jev/jev-alignment.dbn"), new File("C:/dev/thesis/jev/jev-alignment.fas"));
            runTest(jevData, null);
          
           // SynonymousTestData dengueData4 = new SynonymousTestData(new File("C:/dev/thesis/dengue4/300/dengue4_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/dengue4/300/dengue4_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/dengue/dengue-alignment.dbn"), new File("C:/dev/thesis/dengue/dengue-alignment.fas"));
           // runTest(dengueData4, "dengue4");
            SynonymousTestData westnileData = new SynonymousTestData(new File("C:/dev/thesis/westnile/300/westnile_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/westnile/300/westnile_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/westnile/westnile_all_200_aligned.dbn"), new File("C:/dev/thesis/westnile/westnile_all_200_aligned.fas"));
            runTest(westnileData, null);
            SynonymousTestData tbvData = new SynonymousTestData(new File("C:/dev/thesis/tbv/300/tbv_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/tbv/300/tbv_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/tbv/all_tbv_aligned.dbn"), new File("C:/dev/thesis/tbv/all_tbv_aligned.fas"));
            runTest(tbvData, null);
            SynonymousTestData hcvData1 = new SynonymousTestData(new File("C:/dev/thesis/hcv/1/300/hcv1_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/hcv/1/300/hcv1_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcvData1, "hcv1");
            SynonymousTestData hcvData1a = new SynonymousTestData(new File("C:/dev/thesis/hcv/1a/300/hcv1a_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/hcv/1a/300/hcv1a_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcvData1a, "hcv1a");
            SynonymousTestData hcvData1b = new SynonymousTestData(new File("C:/dev/thesis/hcv/1b/300/hcv1b_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/hcv/1b/300/hcv1b_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcvData1b, "hcv1b");
            SynonymousTestData hcvData2 = new SynonymousTestData(new File("C:/dev/thesis/hcv/2/300/hcv2_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/hcv/2/300/hcv2_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcvData2, "hcv2");
            SynonymousTestData hcvData6 = new SynonymousTestData(new File("C:/dev/thesis/hcv/6/300/hcv6_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/hcv/6/300/hcv6_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn"), new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas"));
            runTest(hcvData6, "hcv6");
            * */
          
            
        } catch (Exception ex) {
            Logger.getLogger(SynonymousRatesOverall.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static double [][] getPairedMatrix(ArrayList<SecondaryStructureData> structureData)
    {
        int length = structureData.get(0).pairedSites.length;
        double [][] pairedMatrix = new double[length][length];
        for(SecondaryStructureData s : structureData)
        {
            for(int i = 0 ; i < s.pairedSites.length ; i++)
            {
                if(s.pairedSites[i] != 0)
                {
                    pairedMatrix[i][s.pairedSites[i]-1]++;
                }
            }
        }
        return pairedMatrix;
    }
    
    public static double [] getPairedPercentage(ArrayList<SecondaryStructureData> structureData)
    {
        int length = structureData.get(0).pairedSites.length;
        double [] pairedMatrix = new double[length];
        double [] t = new double[length];
        for(SecondaryStructureData s : structureData)
        {
            for(int i = 0 ; i < s.pairedSites.length ; i++)
            {
                if(s.pairedSites[i] != 0)
                {
                    pairedMatrix[i]++;
                }
                
                if(s.sequence.charAt(i) != '-')
                {
                    t[i]++;
                }                
            }
        }
        
        for(int i = 0 ; i < t.length ; i++)
        {
            pairedMatrix[i] /= t[i];
        }
        
        return pairedMatrix;
    }
    
     public static double [] getPairedPercentagePhylogeneticallyWeighted(ArrayList<SecondaryStructureData> structureData)
    {
        ArrayList<String> sequences = new ArrayList<>();
        for(SecondaryStructureData s : structureData)
        {
            sequences.add(s.sequence);
        }        
        ///double [][] distances = NucleotideComposition.getDistanceMatrix(sequences);
        //double average = 
        double [] weights = NucleotideComposition.getWeights(sequences);
       /* for(int i = 0 ; i < weights.length ; i++)
        {
            System.out.println(i+"\t"+weights[i]);
        }*/
        
        int length = structureData.get(0).pairedSites.length;
        double [] pairedMatrix = new double[length];
        double [] t = new double[length];
        for(int k = 0; k < structureData.size() ; k++)
        {
            SecondaryStructureData s = structureData.get(k);
            for(int i = 0 ; i < s.pairedSites.length ; i++)
            {
                if(s.pairedSites[i] != 0)
                {
                    //pairedMatrix[i]++;
                    pairedMatrix[i] += weights[k];
                }
                
                if(s.sequence.charAt(i) != '-')
                {
                    //t[i]++;
                    t[i] += weights[k];
                }                
            }
        }
        
        for(int i = 0 ; i < t.length ; i++)
        {
            pairedMatrix[i] /= t[i];
        }
        
        /*
        for(int i = 0 ; i < pairedMatrix.length ; i++)
        {
            System.out.println("Paired\t"+pairedMatrix[i]);
        }*/
        
        return pairedMatrix;
    }
    
    
}
