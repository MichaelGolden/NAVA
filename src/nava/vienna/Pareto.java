/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.structure.BenchmarkMetrics;
import nava.structure.MountainMetrics;
import nava.utils.RNAFoldingTools;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Pareto {
    
    ArrayList<ParetoItem> items = new ArrayList<>();
    
    
    
    public boolean dominatedByAtLeastOne(ParetoItem item)
    {
        for(ParetoItem other : items)
        {
            if(other.dominatesAll(item) && !item.equals(other))
            {
                return true;
            }
        }
        return false;
    }
    
    public void removeAllNonDominant()
    {
        for(int i = 0 ; i < items.size() ; i++)
        {
            if(dominatedByAtLeastOne(items.get(i)))
            {
                items.remove(i);
                i--;
            }
        }
    }
    
    public void addItem(ParetoItem item)
    {
        items.add(item);
        removeAllNonDominant();
    }
    
    
    static String [] stackingPairs = {"AAUU","ACGU","AGCU","AGUU","AUAU","AUGU","CAUG","CGUG","CUAG","CUGG","GAUC","GCGC","GGCC","GGUC","GUAC","GUGC","GAUU","GCGU","GGCU","GGUU","GUAU","GUGU","UAUA","UCGA","UGCA","UGUA","UUAA","UUGA","UAUG","UCGG","UGCG","UGUG","UUAG","UUGG","CCGG","CGCG"};
    static double [] stackingEnergies = {-0.9,-2.2,-2.1,-0.6,-1.1,-1.4,-2.1,-1.4,-2.1,-2.1,-2.4,-3.4,-3.3,-1.5,-2.2,-2.5,-1.3,-2.5,-2.1,-0.5,-1.4,1.3,-1.3,-2.4,-2.1,-1.0,-0.9,-1.3,-1.0,-1.5,-1.4,0.3,-0.6,-0.5,-3.3,-2.4};

    public static double alternativeHelixEnergyScore(int [] pairedSites, String sequence, int minHelixLength, boolean ratio)
    {        
        
        ArrayList<Helix> helices = listHelices(sequence, minHelixLength,0.03);
        double alternative = 0;
        double structure = 0;
        for(Helix helix : helices)
        {
            String previousPair = null;
            for(int len = 0 ; len < helix.length  ; len++)
            {
                int i = helix.start + len;
                int j = helix.end - len;
                String currentPair = ""+sequence.charAt(i)+sequence.charAt(j);
                if(pairedSites[i] != j + 1 && len > 1)
                {
                    String stack = (previousPair+currentPair).toUpperCase().replaceAll("T", "U");
                    for(int k = 0 ; k < stackingPairs.length ; k++)
                    {
                        if(stackingPairs[k].equals(stack))
                        {
                            alternative += stackingEnergies[k];
                        }
                    }
                }
                else
                {
                  /*  String stack = (previousPair+currentPair).toUpperCase().replaceAll("T", "U");
                    for(int k = 0 ; k < stackingPairs.length ; k++)
                    {
                        if(stackingPairs[k].equals(stack))
                        {
                            structure += stackingEnergies[k];
                        }
                    }*/
                }
                previousPair = currentPair;
            }
        }
        Random random = new Random(937502570252725058L);
        int iter = 2000;
        for(int i = 0 ; i < iter ; i++)
        {
            String stack = "";
            for(int j = 0 ; j < 4 ; j++)
            {
                stack += sequence.charAt(random.nextInt(pairedSites.length));
            }
            for(int k = 0 ; k < stackingPairs.length ; k++)
            {
                if(stackingPairs[k].equals(stack))
                {
                    structure += stackingEnergies[k];
                }
            }
        }
        
        if(ratio)
        {
            return alternative/(structure/((double)iter));
        }
        else
        {
            return alternative;
        }
    }
    
    
    public void evaluate(ViennaRuntime viennaRuntime, Structure s, int [] target) throws Exception
    {
        double ensemble_freq = s.calculateEnsembleFrequency(viennaRuntime);
               
        ParetoItem<Structure> item = new ParetoItem<>();
        item.object = s;
     
        item.setParameter("ensemble_freq_37C", ParetoParameter.ParetoRanking.HIGHER_IS_BETTER, ensemble_freq);
        item.setParameter("ensemble_freq_"+25+"C", ParetoParameter.ParetoRanking.HIGHER_IS_BETTER, Structure.calculateEnsembleFrequency(viennaRuntime, s.sequence, target, 25.0));                            
        item.setParameter("gc_content", ParetoParameter.ParetoRanking.LOWER_IS_BETTER, Utils.calculateGC(s.sequence));
        item.setParameter("gc_content_paired", ParetoParameter.ParetoRanking.LOWER_IS_BETTER, Utils.calculatedBasePairedGC(s));
        int [] pairedSites = new RNAfold(viennaRuntime).fold(s.sequence, s.tempCelsius, false).mfeStructure.pairedSites;
        item.setParameter("mountain_sim_37C", ParetoParameter.ParetoRanking.HIGHER_IS_BETTER,  1 - MountainMetrics.calculateNormalizedWeightedMountainDistance(pairedSites, target));
        item.setParameter("sensitivity_37C", ParetoParameter.ParetoRanking.HIGHER_IS_BETTER, BenchmarkMetrics.calculateSensitivity(target, pairedSites));
        addItem(item);
        for(ParetoItem<Structure> paretoItem : items)
        {
            System.out.println(paretoItem.formattedString()+"\t"+simpleAlternativeStructureScore(target, paretoItem.object.sequence)+"\t"+simpleAlternativeStructureScore2(target, paretoItem.object.sequence)+"\t"+simpleAlternativeHelixScore(target,paretoItem.object.sequence,2)+"\t"+simpleAlternativeHelixScore(target,paretoItem.object.sequence,3)+"\t"+alternativeHelixEnergyScore(target,paretoItem.object.sequence,3,false)+"\t"+alternativeHelixEnergyScore(target,paretoItem.object.sequence,3,true)+"\t"+paretoItem.object.freeEnergy);
        }
        //System.out.println(simpleAlternativeStructureScore(target, s.sequence)+"\t"+simpleAlternativeStructureScore2(target, s.sequence)+"\t"+simpleAlternativeHelixScore(target,s.sequence,2)+"\t"+simpleAlternativeHelixScore(target,s.sequence,3)+"\t"+alternativeHelixEnergyScore(target,s.sequence,3,false)+"\t"+alternativeHelixEnergyScore(target,s.sequence,3,true)+"\t"+s.freeEnergy+"\t"+ensemble_freq);
       System.out.println("-------------------------------------");
    }
    
    public static class Helix
    {
        int start;
        int end;
        int length;
        String sequence;

        @Override
        public String toString() {
            return "Helix{" + "start=" + start + ", end=" + end + ", length=" + length + ", sequence=" + sequence + '}';
        }

       
        
    }
    
    public static ArrayList<Helix> listHelices(String sequence, int minHelixLength, double minPairingProb)
    {
        
        ArrayList<Helix> helices = new ArrayList<>();

        for(int i = 0 ; i < sequence.length() ; i++)
        {
            for(int j = i+1 ; j < sequence.length() ; j++)
            {   
                if(PairingProbabilities.get(sequence.charAt(i), sequence.charAt(j)) > minPairingProb && (i == 0 || j == sequence.length()-1 || PairingProbabilities.get(sequence.charAt(i-1), sequence.charAt(j+1)) < minPairingProb))
                {
                    Helix helix = new Helix();
                    helix.start = i;
                    helix.end = j;
                    for(int length = 0 ; length < sequence.length() ; length++)
                    {
                        if(i+length < sequence.length() && i+length < j - length && PairingProbabilities.get(sequence.charAt(i+length), sequence.charAt(j-length)) > minPairingProb)
                        {
                            
                        }
                        else
                        {
                            helix.length = length;
                            if(helix.start+3 < helix.end)
                            {
                                helix.sequence = sequence.substring(helix.start, helix.end+1);
                          
                               // System.out.println(i+length);
                                //System.out.println(j - length);
                                //System.out.println(PairingProbabilities.get(sequence.charAt(i+length), sequence.charAt(j-length)));
                               // System.out.println(PairingProbabilities.get(sequence.charAt(i+length), sequence.charAt(j-length))+"\t"+helix);
                                if(helix.length >= minHelixLength)
                                {
                                    helices.add(helix);
                                }
                            }
                            break;
                        }
                    }
                }
                        
            }
        }
        
        return helices;
    }
    
    public static double simpleAlternativeStructureScore(int [] pairedSites, String sequence)
    {
        double score = 0;
        for(int i = 0 ; i < pairedSites.length ; i++)
        {
            for(int j = 0 ; j < pairedSites.length ; j++)
            {
                if(pairedSites[i] != j + 1)
                {
                    double p = PairingProbabilities.get(sequence.charAt(i), sequence.charAt(j));
                    if(p > 0.05)
                    {
                        score += p;
                    }
                }
            }
        }
        return score;
    }    
    
    public static double simpleAlternativeStructureScore2(int [] pairedSites, String sequence)
    {
        double score = 0;
        for(int i = 0 ; i < pairedSites.length ; i++)
        {
            for(int j = 0 ; j < pairedSites.length ; j++)
            {
                if(pairedSites[i] != j + 1)
                {
                    double p = PairingProbabilities.get(sequence.charAt(i), sequence.charAt(j));
                    if(p > 0.05)
                    {
                        score += 1;
                    }
                }
            }
        }
        return score;
    }
    
     public static double simpleAlternativeHelixScore(int [] pairedSites, String sequence, int minHelixLength)
    {
        ArrayList<Helix> helices = listHelices(sequence, minHelixLength,0.1);
        double score = 0;
        for(Helix helix : helices)
        {
            for(int len = 0 ; len < helix.length  ; len++)
            {
                int i = helix.start + len;
                int j = helix.end - len;
                if(pairedSites[i] != j + 1)
                {
                    //score += PairingProbabilities.get(sequence.charAt(i), sequence.charAt(j));
                    score += 1;
                }
            }
        }
        return score;
    }
    
    public static void main(String [] args)
    {
        Table table = new Table();
        
        Pareto pareto = new Pareto();
        
        try {
            ViennaRuntime viennaRuntime = new ViennaRuntime(new File("C:/Program Files (x86)/ViennaRNA Package/"), ViennaRuntime.OS.WINDOWS);
            
            /*RNAfold rnafold = new RNAfold(viennaRuntime);
            System.out.println(rnafold.fold("CCGGGUCCGCCUCCUUGGCGGGCAAAAAAAAAAAAAAACCCGGCGAUUAGCCGGGCCGG", 37, true));
            RNAfoldResult result = rnafold.fold("GGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCCGGGAAAAACCC", 37, true);
            Structure mfeStructure = result.getMFEstructure();
            
            RNAeval rnaeval = new RNAeval(viennaRuntime);
             System.out.println(mfeStructure.pairedSites.length);
            */
            RNAinverse rnainverse = new RNAinverse(viennaRuntime);
            
            //rnainverse.inverse(RNAFoldingTools.getPairedSitesFromDotBracketString("(((((((.....)))))))"), 37, 10);
            IncaRNAtion incarnation = new IncaRNAtion();
            
          // int [] target = RNAFoldingTools.getPairedSitesFromDotBracketString(".....(((((.............))))).........(((((((.....)).))..)))...........((((..........))))....(((.(((.....(((......)))......((((((................)))))).))))))...");
           int [] target = RNAFoldingTools.getPairedSitesFromDotBracketString(".((((((((((((....................)))))))))).))(((((((((((((..............)))))))))))))....");
            //int [] target = RNAFoldingTools.getPairedSitesFromDotBracketString("(((.((((....((((((((((............(((.......)))))))))))))....)))))))((.......................................................))(((((....((((((((.........))))))))...))))).......(((.....)))......................");
            System.out.println("target_length="+target.length);
            boolean useMFE = false;
            boolean usePartitionFunction = true;
            boolean applyConstraintMaskForStackingBases = true;
            //double gcContent = 0.5;
            int count = 0;
            int tempCelsius = 37;
            int incarnationSampleSize = 25;
            int inverseRNArepeats = 1;
            
            Random random = new Random(4820528022082502580L);
 
            int sampleSize = 0;
            while(true)
            {
                double [] gcContentArray = {0.5};
                //double [] gcContentArray = {0.05, 0.1, 0.15, 0.2,0.25, 0.3,0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65,0.7,0.75,0.8,0.85,0.9,0.95};
                for(double gcContent : gcContentArray)
                {
                    ArrayList<Structure> firstSample = incarnation.generateSample(target, 37, gcContent, 20.0, incarnationSampleSize);
                    while(firstSample.size() > incarnationSampleSize)
                    {
                        firstSample.remove(random.nextInt(firstSample.size()));
                    }
                    
                    long startTime = System.currentTimeMillis();
                    for(Structure s : firstSample)
                    { 
                        try {
                            sampleSize++;
                            pareto.evaluate(viennaRuntime, s, target);
                        } catch (Exception ex) {
                            Logger.getLogger(Pareto.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    for(Structure s2 : firstSample)
                    {    
                    ArrayList<Structure> sample = rnainverse.inverse(target, s2.sequence, 37, inverseRNArepeats, useMFE, usePartitionFunction, applyConstraintMaskForStackingBases);
                        //ArrayList<Structure> sample = rnainverse.inverse(target, null, 37, 5, useMFE, usePartitionFunction, applyConstraintMaskForStackingBases);
                        if(sample != null)
                        {
                            for(Structure s : sample)
                            {
                                 double ensemble_freq = s.calculateEnsembleFrequency(viennaRuntime);
                                 //System.out.println(ensemble_freq+"\t"+simpleAlternativeStructureScore(target, s.sequence)+"\t"+simpleAlternativeStructureScore2(target, s.sequence)+"\t"+simpleAlternativeHelixScore(target,s.sequence,2)+"\t"+simpleAlternativeHelixScore(target,s.sequence,3)+"\t"+alternativeHelixEnergyScore(target,s.sequence,3,false)+"\t"+alternativeHelixEnergyScore(target,s.sequence,3,true)+"\t"+s.freeEnergy+"\t");
                                 table.add("ensemble_freq_"+tempCelsius+"C", ensemble_freq);
                                 table.add("alt base prob sum", simpleAlternativeStructureScore(target, s2.sequence));
                                 table.add("alt base pair count", simpleAlternativeStructureScore2(target, s2.sequence));
                                 table.add("alt helix 2", simpleAlternativeHelixScore(target,s2.sequence,2));
                                 table.add("alt helix 3", simpleAlternativeHelixScore(target,s2.sequence,3));
                                 table.add("stack helix 3", alternativeHelixEnergyScore(target,s2.sequence,3,false));
                                 table.add("norm stack helix 3", alternativeHelixEnergyScore(target,s2.sequence,3,true));
                                 table.add("free energy", s2.freeEnergy);
                                 table.add("ratio", alternativeHelixEnergyScore(target,s2.sequence,3,false)/s2.freeEnergy);
                                 table.writeTable(new File("table_out.txt"));
                                // System.out.println(table.getPairwiseCorrelations());
                                try {                        
                                    count++;
                                    long elapsedTime = System.currentTimeMillis()-startTime;
                                    double rate = count / (elapsedTime/1000.0);
                                    //System.out.print(rate+"\t"+elapsedTime+"\t");
                                    pareto.evaluate(viennaRuntime, s, target);
                                } catch (Exception ex) {
                                    Logger.getLogger(RNAfold.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Pareto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
