/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.vienna.inverserna;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import nava.utils.RNAFoldingTools;
import nava.vienna.*;
import org.apache.batik.ext.awt.image.codec.tiff.TIFFDecodeParam;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Main {
    public static void batchEvaluation(ArrayList<MultiTargetAndCandidate> mtcs, Evaluator evaluator)
    {
         long startTime = System.nanoTime();
         evaluator.evaluate(mtcs);
         long endTime = System.nanoTime();
         double elapsedTimeSeconds = (endTime - startTime)/1E9;
         double averageTime = elapsedTimeSeconds/(double)mtcs.size();
         System.out.println("Average time(s) = " +averageTime);
    }
    
    public static void main(String [] args)
    {        
        try {
            Table table = new Table();
            ViennaRuntime viennaRuntime = new ViennaRuntime(new File("C:/Program Files (x86)/ViennaRNA Package/"), ViennaRuntime.OS.WINDOWS);
            RNAinverse rnainverse = new RNAinverse(viennaRuntime);
            IncaRNAtion2 incarnation = new IncaRNAtion2();
            int incarnationSampleSize = 20;
            double tempCelsius = 37;
            //int [] target = RNAFoldingTools.getPairedSitesFromDotBracketString(".....(((((.............))))).........(((((((.....)).))..)))...........((((..........))))....(((.(((.....(((......)))......((((((................)))))).))))))...");
            int [] target = RNAFoldingTools.getPairedSitesFromDotBracketString("(((.((((....((((((((((............(((.......)))))))))))))....)))))))((.......................................................))(((((....((((((((.........))))))))...))))).......(((.....)))......................");
            TargetStructure target1 = new TargetStructure("target1",target, 37);
            //TargetStructure target1 = new TargetStructure("target1",RNAFoldingTools.getPairedSitesFromDotBracketString(".((((((((((((....................)))))))))).))(((((((((((((..............)))))))))))))...."), 37);
            //TargetStructure target2 = new TargetStructure("target2",RNAFoldingTools.getPairedSitesFromDotBracketString(".((((((((((((....................)))))))))).))(((((((((((((..(((...)))...)))))))))))))...."), 34);
            ArrayList<TargetStructure> targetStructures = new ArrayList<>();
            targetStructures.add(target1);
            //targetStructures.add(target2);
            
            GCcontentEvaluator linEvaluator = new GCcontentEvaluator();
            RNAfoldEvaluator rnaFoldEvaluator = new RNAfoldEvaluator(viennaRuntime);
            AlternativeStructureEvaluator alternativeEvaluator = new AlternativeStructureEvaluator();
            UNAfoldEvaluator unafoldEvaluator = new UNAfoldEvaluator();
            
                
            ParetoOptimization pareto = new ParetoOptimization(0, 3);
            
            ScatterPlot plot = new ScatterPlot();
            
            for(long iter = 0 ; iter < 1000000 ; iter++)
            {
                plot.x = new ArrayList<>();
                plot.y = new ArrayList<>();
                List<String> sequences = incarnation.generateSample(target1.pairedSites, tempCelsius, 0.5, 20, incarnationSampleSize);
                sequences = sequences.subList(0, Math.min(sequences.size(), incarnationSampleSize));
     
                ArrayList<MultiTargetAndCandidate> mtcs = new ArrayList<>(sequences.size());
                for(String sequence : sequences)
                {
                   // mtcs.add(new MultiTargetAndCandidate(targetStructures, sequence));
                }
                
                /*
                System.out.println(linEvaluator);
                batchEvaluation(mtcs,linEvaluator);
                System.out.println(rnaFoldEvaluator);
                batchEvaluation(mtcs,rnaFoldEvaluator);
                System.out.println(alternativeEvaluator);
                batchEvaluation(mtcs,alternativeEvaluator);
                 System.out.println(unafoldEvaluator);
                batchEvaluation(mtcs,unafoldEvaluator);
                
                ArrayList<String> sortedCriteria = null;
                for(MultiTargetAndCandidate mtc : mtcs)
                {
                    pareto.addItem(mtc);

                    if(sortedCriteria == null)
                    {
                        sortedCriteria = mtc.getAlphabeticalCriteria();
                    }
                    for(String criterion : sortedCriteria)
                    {
                        table.add(criterion, mtc.getCriterion(criterion).value);
                    }

                    System.out.println(pareto.items.size());
                }*/
                
                
                ArrayList<String> sortedCriteria = null;
                /*for(String s : sequences)
                {
                    MultiTargetAndCandidate t = new MultiTargetAndCandidate(targetStructures, s);
                    t.addEvaluatedCriteria(linEvaluator.evaluate(t));
                    t.addEvaluatedCriteria(rnaFoldEvaluator.evaluate(t));
                    t.addEvaluatedCriteria(alternativeEvaluator.evaluate(t));
                    t.addEvaluatedCriteria(unafoldEvaluator.evaluate(t));
                    pareto.addItem(t);

                    if(sortedCriteria == null)
                    {
                        sortedCriteria = t.getAlphabeticalCriteria();
                    }
                    for(String criterion : sortedCriteria)
                    {
                        table.add(criterion, t.getCriterion(criterion).value);
                    }

                    System.out.println(pareto.items.size());
                }*/


                // now refine using sequences using rna inverse
                for(TargetStructure t : targetStructures)
                {
                    for(String s : sequences)
                    {
                        boolean useMFE = false;
                        boolean usePartitionFunction = false;
                        boolean applyConstraintMaskForStackingBases = true;     
                        ArrayList<Structure> sample = rnainverse.inverse(t.pairedSites, s, t.tempCelsius, 2, useMFE, usePartitionFunction, applyConstraintMaskForStackingBases);
                         
                        if(sample != null)
                        {
                            for(Structure structure : sample)
                            {
                                mtcs.add(new MultiTargetAndCandidate(targetStructures, structure.sequence));
                            }
                        }
                       /* if(sample != null)
                        {
                            for(Structure structure : sample)
                            {
                                String sequence = structure.sequence;
                                MultiTargetAndCandidate multiTarget = new MultiTargetAndCandidate(targetStructures, sequence);
                                multiTarget.addEvaluatedCriteria(linEvaluator.evaluate(multiTarget));
                                multiTarget.addEvaluatedCriteria(rnaFoldEvaluator.evaluate(multiTarget));
                                multiTarget.addEvaluatedCriteria(alternativeEvaluator.evaluate(multiTarget));
                                multiTarget.addEvaluatedCriteria(unafoldEvaluator.evaluate(multiTarget));
                                if(sortedCriteria == null)
                                {
                                    sortedCriteria = multiTarget.getAlphabeticalCriteria();
                                }
                                pareto.addItem(multiTarget);
                                for(String criterion : sortedCriteria)
                                {
                                    table.add(criterion, multiTarget.getCriterion(criterion).value);
                                }
                                System.out.println(pareto.items.size());
                            }
                        }*/
                    }
                }
                
                System.out.println(linEvaluator);
                batchEvaluation(mtcs,linEvaluator);
                System.out.println(rnaFoldEvaluator);
                batchEvaluation(mtcs,rnaFoldEvaluator);
                System.out.println(alternativeEvaluator);
                batchEvaluation(mtcs,alternativeEvaluator);
                System.out.println(unafoldEvaluator);
                batchEvaluation(mtcs,unafoldEvaluator);
                
                for(MultiTargetAndCandidate mtc : mtcs)
                {
                    pareto.addItem(mtc);

                    if(sortedCriteria == null)
                    {
                        sortedCriteria = mtc.getAlphabeticalCriteria();
                    }
                    for(String criterion : sortedCriteria)
                    {
                        table.add(criterion, mtc.getCriterion(criterion).value);
                    }

                    System.out.println(pareto.items.size());
                }

                pareto.items.get(0).printCriteria();
                table.writeTable(new File("inversernatable.txt"));
                System.out.println("Correlations:\n"+table.getPairwiseSpearmanCorrelations());


            //plot.xlab = "target1_gc_content_paired_sites";
                //plot.ylab = "target1_rnafold_ensemble_prob_37.0C";
                plot.xlab = "target1: rnafold ensemble prob @ 37.0C";
                plot.ylab = "target1: rnafold norm. ensemble defect @ 37.0C";
            // plot.ylab = "target2: rnafold ensemble prob @ 34.0C";

                ArrayList<String> paretoCriteria = new ArrayList<>();
                paretoCriteria.add(plot.xlab);
                paretoCriteria.add(plot.ylab);
                //pareto.setParetoCriteria(paretoCriteria);

                for(ParetoItem t : pareto.items )
                {
                    plot.x.add(t.getCriterion(plot.xlab).value);
                    plot.y.add(t.getCriterion(plot.ylab).value);
                }
            }
            JFrame frame = new JFrame();
            frame.setSize(640, 480);
            
            plot.repaint();
            frame.add(plot, BorderLayout.CENTER);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            
            
        } catch (Exception ex) {
            Logger.getLogger(Pareto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
