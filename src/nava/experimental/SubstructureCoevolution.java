/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.FileImport;
import nava.data.types.DataType;
import nava.data.types.SecondaryStructureData;
import nava.experimental.CoevolutionParser.CoevolutionElement;
import nava.ranking.MyMannWhitney;
import nava.ranking.RankingAnalyses;
import nava.ranking.StatUtils;
import nava.utils.Mapping;
import nava.utils.RNAFoldingTools;
import org.apache.commons.math3.stat.inference.ChiSquareTest;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class SubstructureCoevolution {
    public static class Substructure
    {
        int start;
        int end;
        int length;
        
        public Substructure(String s)
        {
            String [] split = s.split("(\\s)+-(\\s)+");
            this.start = Integer.parseInt(split[0]);
            this.end = Integer.parseInt(split[1]);
            this.length = this.end - this.start;
        }
        
        public Substructure(int start, int end)
        {
            this.start = start;
            this.end = end;
            this.length = this.end - this.start;
        }

        @Override
        public String toString() {
            return "Substructure{"+start + "-" + end + '}';
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
    
    public static int [] getConsensusStructure(double [][] pairedMatrix, int start, int length)
    {
        double [][] submatrix = new double[length][length];
        for(int i = start ; i < start+length ; i++)
        {
            for(int j = start ; j < start+length ; j++)
            {
                submatrix[i-start][j-start] = pairedMatrix[i][j];
            }
        }
        
        int [] pairedSites = PosteriorDecodingTool.getPosteriorDecodingConsensusStructure(submatrix);
        int [] out = new int[pairedMatrix.length];
        for(int i = 0 ; i < pairedSites.length ; i++)
        {
            if(pairedSites[i] != 0)
            {
                out[i+start] = pairedSites[i] + start;
            }
            
        }
         
        return out;                
    }
    
    public static double [][] getExpectedValues(long [][] counts)
    {
        double [][] expected = new double[counts.length][counts[0].length];
        double tableSum = 0;
        
        for(int i = 0 ; i < counts.length ; i++)
        {
            for(int j = 0 ; j < counts[0].length ; j++)
            {
                tableSum += counts[i][j];
            }
        }
        
        for(int i = 0 ; i < counts.length ; i++)
        {
            for(int j = 0 ; j < counts[0].length ; j++)
            {
                double rowsum = 0;
                for(int x = 0 ; x < counts[0].length ; x++)
                {
                    rowsum += counts[i][x];
                }
                
                double colsum = 0;
                 for(int x = 0 ; x < counts.length ; x++)
                {
                    colsum += counts[x][j];
                }
                 
                 expected[i][j] = (rowsum*colsum)/tableSum;
            }
        }
        
        return expected;
    }
    
    public static void printTable(long [][] counts, String [] colLabels, String [] rowLabels)
    {
        for(int i = 0 ; i < counts.length ; i++)
        {
            System.out.print(colLabels[i]+"\t");
        }
        System.out.println();
        
        for(int i = 0 ; i < counts.length ; i++)
        {
            for(int j = 0 ; j < counts.length ; j++)
            {
                if(j == 0)
                {
                    System.out.print(rowLabels[i]+"\t");
                }
                System.out.print(counts[i][j]+"\t");
            }  
            System.out.println();
        }
    }
    
    static DecimalFormat df = new DecimalFormat("0.0");
        static DecimalFormat df2 = new DecimalFormat("0.00E0");
      public static void printTable(double [][] counts, String [] colLabels, String [] rowLabels)
    {
        
        for(int i = 0 ; i < counts.length ; i++)
        {
            System.out.print(colLabels[i]+"\t");
        }
        System.out.println();
        
        for(int i = 0 ; i < counts.length ; i++)
        {
            for(int j = 0 ; j < counts.length ; j++)
            {
                if(j == 0)
                {
                    System.out.print(rowLabels[i]+"\t");
                }
                System.out.print(df.format(counts[i][j])+"\t");
            }  
            System.out.println();
        }
    }
      
    public static String getList(long [][] counts, String [] colLabels, String [] rowLabels)
    {
        StringBuffer sb = new StringBuffer("");
        
        sb.append("IsPaired,IsCoevolving\n");
        for(int i = 0 ; i < counts.length ; i++)
        {
            for(int j = 0 ; j < counts.length ; j++)
            {
                for(int k = 0 ; k < counts[i][j] ; k++)
                {
                    sb.append(rowLabels[i]+","+colLabels[j]+"\n");
                }
                
            }  
        }
        return sb.toString();
    }
    
    public static void main(String [] args) throws Exception
    {
         DecimalFormat df = new DecimalFormat("0.000");
        DecimalFormat df2 = new DecimalFormat("0.00E0");
        try {
            
         /* File coevolutionFile = new File("C:/dev/thesis/hcv/coevolution/hcv-formation.clm");
            File coevolutionAlignment = new File("C:/dev/thesis/hcv/coevolution/HCV_mafft_aligned_250_resave.fas");
            File structureAlignment = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.dbn");
            File sequenceFile = new File("C:/dev/thesis/hcv/hcv_genotypes2_aligned.fas");
           
            ArrayList<Substructure> substructures = new ArrayList<>();
            substructures.add( new Substructure("114 - 190"));
            substructures.add( new Substructure("356 - 950"));
            substructures.add( new Substructure("8132 - 8377"));
            substructures.add( new Substructure("8432 - 8509"));
            substructures.add( new Substructure("8571 - 8668"));
            substructures.add( new Substructure("8948 - 9086"));
            substructures.add( new Substructure("9382 - 9796")); // 9796
             substructures.add( new Substructure("0 - 100000"));*/
           /*File coevolutionFile = new File("C:/dev/thesis/hiv_full/coevolution/hiv-formation.clm");
            File coevolutionAlignment = new File("C:/dev/thesis/hiv_full/coevolution/hiv2010-500-seperated.fas.fas");
            File structureAlignment = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.dbn");
            File sequenceFile = new File("C:/dev/thesis/hiv_full/hiv_not_siv_full_aligned.fas");
            
            ArrayList<Substructure> substructures = new ArrayList<>();
            substructures.add( new Substructure("1746 - 1834"));
            substructures.add( new Substructure("2013 - 2092"));
            substructures.add( new Substructure("2013 - 2088"));
            substructures.add( new Substructure("2496 - 2647")); // 1576-1695
            substructures.add( new Substructure("3610 - 3763")); // 1576-1695
            substructures.add( new Substructure("7593 - 7734")); // 5884-6023
            substructures.add( new Substructure("9493 - 9840")); // 7289-7609
             substructures.add( new Substructure("0 - 100000"));*/
          
          /*File coevolutionFile = new File("C:/dev/thesis/jev/coevolution/jev-formation.clm");
            File coevolutionAlignment = new File("C:/dev/thesis/jev/coevolution/jev_200_seperated.fas");
            File structureAlignment =  new File("C:/dev/thesis/jev/jev-alignment.dbn");
            File sequenceFile = new File("C:/dev/thesis/jev/jev-alignment.fas");
            ArrayList<Substructure> substructures = new ArrayList<>();
            substructures.add( new Substructure("1746 - 1834"));
            substructures.add( new Substructure("2013 - 2092"));
            substructures.add( new Substructure("2013 - 2088"));
            substructures.add( new Substructure("2496 - 2647")); // 1576-1695
            substructures.add( new Substructure("7593 - 7734")); // 5884-6023
            substructures.add( new Substructure("9493 - 9840")); // 7289-7609
             substructures.add( new Substructure("0 - 100000"));*/
            
            
          File coevolutionFile = new File("C:/dev/thesis/dengue/coevolution/dengue-formation.clm");
            File coevolutionAlignment = new File("C:/dev/thesis/dengue/coevolution/dengue_all_300_aligned_curated.fas");
            File structureAlignment =  new File("C:/dev/thesis/dengue/dengue-alignment.dbn");
            File sequenceFile = new File("C:/dev/thesis/dengue/dengue-alignment.fas");
            ArrayList<Substructure> substructures = new ArrayList<>();
             substructures.add( new Substructure("225 - 525"));
            substructures.add( new Substructure("2151 - 2229"));
            substructures.add( new Substructure("3176 - 3268"));
            substructures.add( new Substructure("5208 - 5284"));
            substructures.add( new Substructure("10937 - 11012"));
             substructures.add( new Substructure("0 - 100000"));
    
            ArrayList<SecondaryStructureData> structureData = FileImport.loadStructures(structureAlignment, DataType.FileFormat.VIENNA_DOT_BRACKET);
            double [] [] pairedMatrix = getPairedMatrix(structureData);
            double t = structureData.size();
            for (int x = 0; x < pairedMatrix.length; x++) {
                for (int y = 0; y <pairedMatrix.length; y++) {
                    pairedMatrix[x][y] /= t;
                }
            }
            
           int UNPAIRED = 0, PAIRED = 1;
           int NOTCOEVOLVING = 0, COEVOLVING = 1;
            //double significanceCutOffCoevolution = 0.05;
           double p2 = 0.25;
           double significanceCutOffCoevolution = 0.05;
           ChiSquareTest chiPairingTest = new ChiSquareTest();
           
            
            int [] all = new int[pairedMatrix.length];
             for(Substructure s : substructures)
            {
                int l = Math.max(0, (1250 - s.length) / 2);
                int startPos = Math.max(0, s.start - l);
                int endPos = Math.min(structureData.get(0).pairedSites.length, startPos + s.length + 2 * l);
                int matrixLength = endPos - startPos;
                int [] consensus = null;
                if(matrixLength < 3000)
                {
                    consensus = getConsensusStructure(pairedMatrix, startPos, matrixLength);             
                    for(int i = 0 ; i < consensus.length ; i++)
                    {
                        if(consensus[i] != 0 && i >= s.start && i <= s.end &&  consensus[i]-1 >= s.start &&  consensus[i]-1 <= s.end)
                        {
                            all[i] = consensus[i];
                        }
                    }
                }
            }
            
            ArrayList<CoevolutionElement> values = CoevolutionParser.readValues(coevolutionFile);
            Mapping mapping = Mapping.createMapping(coevolutionAlignment, sequenceFile, 1000,false);
           
            int total2 = 1;
            double total = total2;
            double [] percs = new double[total2];
            for(int i = 0 ; i < total ; i++)
            {
                percs[i] = (i/total);
            }
            percs[0] = p2;
            for(double p : percs)
            {
                long [][] counts = new long[2][2];
                for(CoevolutionElement elem : values)
                {
                    int x = mapping.aToB(elem.i);
                    int y = mapping.aToB(elem.j);
                    if(x >= 0 && y >= 0)
                    {
                        int ispaired = pairedMatrix[x][y] > p ? 1 : 0;
                        int coevolving = elem.pval < significanceCutOffCoevolution ? 1 : 0;

                        counts[coevolving][ispaired]++;
                    }
                }
                String [] colLabels = {"Unpaired", "Paired"};
                String [] rowLabels = {"Not coevolving", "Coevolving"};
                printTable(counts, colLabels, rowLabels);
                try
                {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("chitest.csv"));
                    writer.write(getList(counts,colLabels,rowLabels));
                    writer.close();                    
                }
                catch(IOException ex)
                {
                    ex.printStackTrace();
                }
                //System.out.println(chiPairingTest.chiSquare(counts));
                //System.out.println(chiPairingTest.chiSquareTest(counts));
                double [][] expected = getExpectedValues(counts);
               printTable(expected, colLabels, rowLabels);

                double percPairedCoevolvingObs = (double)counts[1][1]/(double)(counts[0][1]+counts[1][1]);
                double percPairedCoevolvingExp = expected[1][1]/(expected[0][1]+expected[1][1]);
               // System.out.println("% paired and coevolving (obs): "+percPairedCoevolvingObs);
               // System.out.println("% paired and coevolving (exp): "+percPairedCoevolvingExp);
                System.out.println("factor\t"+p+"\t"+(percPairedCoevolvingObs/percPairedCoevolvingExp)+"\t"+df.format(chiPairingTest.chiSquare(counts))+"\t"+df2.format(chiPairingTest.chiSquareTest(counts)));
            }
            
            for(Substructure s : substructures)
            {
                int l = Math.max(0, (1250 - s.length) / 2);
                int startPos = Math.max(0, s.start - l);
                int endPos = Math.min(structureData.get(0).pairedSites.length, startPos + s.length + 2 * l);
                int matrixLength = endPos - startPos;
                int [] consensus = null;
                if(matrixLength < 3000)
                {
                    consensus = getConsensusStructure(pairedMatrix, startPos, matrixLength);                    
                    System.out.println(RNAFoldingTools.getDotBracketStringFromPairedSites(consensus));
                }
                
                ArrayList<CoevolutionElement> mappedvalues = new ArrayList<CoevolutionElement>();

                ArrayList<Double> pairedValues = new ArrayList<>();
                ArrayList<Double> unpairedValues = new ArrayList<>();
                for(CoevolutionElement elem : values)
                {
                    int x = mapping.aToB(elem.i);
                    int y = mapping.aToB(elem.j);
                    if(x >= 0 && y >= 0)
                    {
                        CoevolutionElement mappedElement = new CoevolutionElement(x,y,elem.pval);
                        mappedvalues.add(mappedElement);
                    // System.out.println(elem+"\t"+mappedElement+"\t"+pairedMatrix[x][y]);
                        //if(pairedMatrix[x][y]>0 && x >= s.start && x <= s.end && y >= s.start && y <= s.end)
                        
                       /* if(s.start == 0 && all[x] == y +1 && x >= s.start && x <= s.end && y >= s.start && y <= s.end)
                        {
                            pairedValues.add(elem.pval);
                        }
                        else
                        if(s.start != 0 && consensus != null && consensus[x] == y +1 && x >= s.start && x <= s.end && y >= s.start && y <= s.end)
                        {
                            pairedValues.add(elem.pval);                        
                        }
                        else
                        if(s.start != 0 && consensus == null && pairedMatrix[x][y]>0 && x >= s.start && x <= s.end && y >= s.start && y <= s.end)
                        {
                             pairedValues.add(elem.pval);   
                        }
                        else
                        if(pairedMatrix[x][y]==0)
                        {
                            unpairedValues.add(elem.pval);
                        }*/
                        
                        double cutoff = 0.0;
                        if(pairedMatrix[x][y]>cutoff&& x >= s.start && x <= s.end && y >= s.start && y <= s.end)
                        {
                             pairedValues.add(elem.pval);   
                        }
                        /*else
                            if(pairedMatrix[x][y]==0&& x >= s.start && x <= s.end && y >= s.start && y <= s.end)
                        {
                             unpairedValues.add(elem.pval);   
                        }*/
                        else
                            if(pairedMatrix[x][y]<=cutoff)
                        {
                             unpairedValues.add(elem.pval);   
                        }
                        /*if(pairedMatrix[x][y]>0)
                        //if(all[x] == y +1)
                        {
                            boolean in_substructure = false;
                            for(int i = 0 ; i < substructures.size()-1 ; i++)
                            {
                                Substructure s2 = substructures.get(i);
                                if(x >= s2.start && x <= s2.end && y >= s2.start && y <= s2.end)
                                {
                                    in_substructure = true;
                                }
                            }
                            in_substructure = false;
                            if(!in_substructure)
                            {
                                unpairedValues.add(elem.pval);
                            }
                        }*/
                        
                    }
                }


                System.out.println(s);
                System.out.println(pairedValues.size() +"\t"+unpairedValues.size()+"\t"+ df.format(RankingAnalyses.getMedian(pairedValues))+"\t"+ df.format(RankingAnalyses.getMedian(unpairedValues)));
                    MyMannWhitney mw = new MyMannWhitney(pairedValues, unpairedValues);
                    System.out.println(df.format(mw.getZ())+"\t"+df2.format(RankingAnalyses.NormalZ(mw.getZ())));
            }
            
        } catch (IOException ex) {
            Logger.getLogger(SubstructureCoevolution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
