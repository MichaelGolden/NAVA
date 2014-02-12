/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import nava.vienna.*;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class AlternativeStructureEvaluator extends Evaluator {
    
    public AlternativeStructureEvaluator(EvaluationTypeRegister typeRegister)
    {
        super(typeRegister);               
    }

    @Override
    public List<EvaluationValue> evaluate(TargetAndCandidate tc) {
        ArrayList<EvaluationValue> evaluatedCriteria = new ArrayList<>();
        evaluatedCriteria.add(new EvaluationValue(alternativeHelixEnergyScore(tc.target.pairedSites,tc.sequence,3,false), typeRegister.getType("simple alt. helix 3 free energy", tc.target, EvaluationType.ParetoRanking.NONE, true,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY)));
        evaluatedCriteria.add(new EvaluationValue(alternativeHelixEnergyScore(tc.target.pairedSites,tc.sequence,3,true), typeRegister.getType("simple alt. helix 3 norm. free energy", tc.target, EvaluationType.ParetoRanking.NONE, true,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY)));
        evaluatedCriteria.add(new EvaluationValue(simpleAlternativeHelixScore(tc.target.pairedSites,tc.sequence,3), typeRegister.getType("simple alt. helix score 3", tc.target, EvaluationType.ParetoRanking.NONE, true,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY)));     
        return evaluatedCriteria;
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
                    score += 1;
                }
            }
        }
        return score;
    }
}
