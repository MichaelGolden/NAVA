/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.CsvReader;
import nava.data.io.FileImport;
import nava.data.types.DataType;
import nava.data.types.SecondaryStructureData;
import nava.ranking.MyMannWhitney;
import nava.ranking.RankingAnalyses;
import nava.structurevis.data.NucleotideComposition;
import nava.utils.Mapping;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SynonymousRatesOverall {
    
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
        
      /*  Mapping mapping = Mapping.createMapping(testData.synonymousAlignment, testData.sequenceAlignment, 1000,false);
        ArrayList<String> values = CsvReader.getColumn(testData.synonymousCSV, 1);
        values.remove(0);
         System.out.println(values);*/
         
        ArrayList<Double> pairedValuesSite1 = new ArrayList<>();
        ArrayList<Double> unpairedValuesSite1 = new ArrayList<>();
        ArrayList<Double> pairedValuesSite2 = new ArrayList<>();
        ArrayList<Double> unpairedValuesSite2 = new ArrayList<>();
        ArrayList<Double> pairedValuesSite3 = new ArrayList<>();
        ArrayList<Double> unpairedValuesSite3 = new ArrayList<>();
        ArrayList<Double> pairedValuesSiteAll = new ArrayList<>();
        ArrayList<Double> unpairedValuesSiteAll = new ArrayList<>();
        ArrayList<Double> pairedValuesSiteAtLeast2 = new ArrayList<>();
        ArrayList<Double> unpairedValuesSiteAtLeast2 = new ArrayList<>();
        ArrayList<Double> all = new ArrayList<Double>();
        
        
      int column = 2;
      //MappedData data = MappingComparison.getHIVMappedData(testData.sequenceAlignment, column);
      MappedData data = MappedData.getMappedData(testData.sequenceAlignment, new MappableData(testData.synonymousAlignment, testData.synonymousCSV,column,1, true, "Syn"), 1000, false);
      /*for(int i = 0 ; i < data.used.length ; i++)
       {
           if(i >= 225 && i <= 525)
           {
               
           }
           else
           {
               data.used[i]=false;
               data.codon[i]=0;
               data.values[i]=Double.NaN;
           }
       }*/
        for(int i = 0 ; i < data.values.length ; i++)
        {
            //System.out.println(data.values[i]+"\t"+data.used[i]+"\t"+data.codon[i]);
            if(data.codon[i] == 1)
            {
                if(pairedPercent[i] > 0.5)
                {
                    pairedValuesSite1.add(data.values[i]);
                }
                else
                {
                    unpairedValuesSite1.add(data.values[i]);
                }
            }
            else
            if(data.codon[i] == 2)
            {
                if(pairedPercent[i] > 0.5)
                {
                    pairedValuesSite2.add(data.values[i]);
                }
                else
                {
                    unpairedValuesSite2.add(data.values[i]);
                }
            }
            else
            if(data.codon[i] == 3)
            {
                if(pairedPercent[i] > 0.5)
                {
                    pairedValuesSite3.add(data.values[i]);
                }
                else
                {
                    unpairedValuesSite3.add(data.values[i]);
                }
            }
            
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
        
          for(int i = 0 ; i < data.values.length ; i++)
        {           
            if(data.codon[i] == 1)
            {
                int pairedCount = 0;
                if(data.used[i] && pairedPercent[i] > 0.5)
                {
                    pairedCount++;
                }
                
                i++;
                if(data.used[i] && pairedPercent[i] > 0.5)
                {
                    pairedCount++;
                }
                
                i++;
                if(data.used[i] && pairedPercent[i] > 0.5)
                {
                    pairedCount++;
                }
                
                if(pairedCount >= 2)
                {
                    pairedValuesSiteAtLeast2.add(data.values[i-2]);
                }
                else
                if(data.used[i-2])
                {
                    unpairedValuesSiteAtLeast2.add(data.values[i-2]);
                }
            }
        }
        
        MyMannWhitney site1 = new MyMannWhitney(pairedValuesSite1, unpairedValuesSite1);
        MyMannWhitney site2 = new MyMannWhitney(pairedValuesSite2, unpairedValuesSite2);
        MyMannWhitney site3 = new MyMannWhitney(pairedValuesSite3, unpairedValuesSite3);
        MyMannWhitney siteAll = new MyMannWhitney(pairedValuesSiteAll, unpairedValuesSiteAll);
        MyMannWhitney siteAtLeast2 = new MyMannWhitney(pairedValuesSiteAtLeast2, unpairedValuesSiteAtLeast2);

        DecimalFormat df = new DecimalFormat("0.000");
        DecimalFormat df2 = new DecimalFormat("0.00E0");
        System.out.println(testData.synonymousCSV+"\t"+keyword);
        System.out.println("1\t"+pairedValuesSite1.size()+"\t"+unpairedValuesSite1.size()+"\t"+df.format(RankingAnalyses.getMedian(pairedValuesSite1))+"\t"+df.format(RankingAnalyses.getMedian(unpairedValuesSite1)) +"\t"+df.format(site1.getZ())+"\t"+df2.format(RankingAnalyses.NormalZ(site1.getZ())));
        System.out.println("2\t"+pairedValuesSite2.size()+"\t"+unpairedValuesSite2.size()+"\t"+df.format(RankingAnalyses.getMedian(pairedValuesSite2))+"\t"+df.format(RankingAnalyses.getMedian(unpairedValuesSite2)) +"\t"+df.format(site2.getZ())+"\t"+df2.format(RankingAnalyses.NormalZ(site2.getZ())));
        System.out.println("3\t"+pairedValuesSite3.size()+"\t"+unpairedValuesSite3.size()+"\t"+df.format(RankingAnalyses.getMedian(pairedValuesSite3))+"\t"+df.format(RankingAnalyses.getMedian(unpairedValuesSite3)) +"\t"+df.format(site3.getZ())+"\t"+df2.format(RankingAnalyses.NormalZ(site3.getZ())));
        System.out.println("All\t"+pairedValuesSiteAll.size()+"\t"+unpairedValuesSiteAll.size()+"\t"+df.format(RankingAnalyses.getMedian(pairedValuesSiteAll))+"\t"+df.format(RankingAnalyses.getMedian(unpairedValuesSiteAll)) +"\t"+df.format(siteAll.getZ())+"\t"+df2.format(RankingAnalyses.NormalZ(siteAll.getZ())));
        System.out.println("At2\t"+pairedValuesSiteAtLeast2.size()+"\t"+unpairedValuesSiteAtLeast2.size()+"\t"+df.format(RankingAnalyses.getMedian(pairedValuesSiteAtLeast2))+"\t"+df.format(RankingAnalyses.getMedian(unpairedValuesSiteAtLeast2)) +"\t"+df.format(siteAtLeast2.getZ())+"\t"+df2.format(RankingAnalyses.NormalZ(siteAtLeast2.getZ())));
        System.out.println(RankingAnalyses.getPercentile(all, 0.25)+"\t"+RankingAnalyses.getPercentile(all, 0.75));
    }
    
    public static void main(String [] args)
    {
        try {
           /* SynonymousTestData hivData = new SynonymousTestData(new File("C:/dev/thesis/jev/300/jev_polyprotein_300_aligned.csv"),new File("C:/dev/thesis/jev/300/jev_polyprotein_300_aligned.fas"),new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn"), new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas"));
          runTest(hivData, null);
           System.exit(0);*/
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
            //System.exit(0);
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
