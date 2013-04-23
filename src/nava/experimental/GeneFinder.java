/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import nava.data.io.IO;
import nava.tasks.applications.Application;
import nava.utils.GeneticCode;
import org.biojava.bio.BioException;
import org.biojava.bio.symbol.Location;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class GeneFinder {

    static class Dataset {

        String dir;
        File genbankFile;
        String organism;
        boolean allowXs = false;

        public Dataset(String dir, File genbankFile, String organism, boolean allowXs) {
            this.dir = dir;
            this.genbankFile = genbankFile;
            this.organism = organism;
            this.allowXs = allowXs;
        }
    }

    public static void saveGenbankFileAsFasta(Dataset dataset, File outFile) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataset.genbankFile));
            Namespace ns = RichObjectFactory.getDefaultNamespace();
            RichSequenceIterator seqs = RichSequence.IOTools.readGenbankDNA(br, ns);

            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            // get a list of genes
            long count = 0;
            while (seqs.hasNext()) {
                count++;
                RichSequence rs = seqs.nextRichSequence();
                writer.write(">" + rs.getName() + "_" + dataset.organism + "\n");
                writer.write(rs.seqString() + "\n");
            }
            br.close();
            writer.close();
            if (count < 100) {
                GeneFinder.MAFFTalignment(outFile, new File(outFile.getAbsolutePath() + "_aligned"));
            }
        } catch (NoSuchElementException ex) {
            Logger.getLogger(GeneFinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BioException ex) {
            Logger.getLogger(GeneFinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void resaveGenbankFile(File inFile, File outFile) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(inFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

            String textline = null;

            boolean delete = false;
            while ((textline = buffer.readLine()) != null) {
                if (textline.startsWith("REFERENCE")) {
                    delete = true;
                } else if (delete && (textline.charAt(0) == ' ' || textline.charAt(0) == '\t')) {
                } else {
                    writer.write(textline);
                    writer.newLine();
                    delete = false;
                }
            }
            buffer.close();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(GeneFinder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) throws Exception {
        int k = 6;

        ArrayList<String> forbiddenKeywords = new ArrayList<>();
        forbiddenKeywords.add("modified");
        forbiddenKeywords.add("attenuated");
        forbiddenKeywords.add("patent");

        // int n = 100;
        //int[] sizes = {50,100, 200, 20, 10, 300, 400};
        int[] sizes = {300, 250, 200, 50, 100, 20, 10};
        //int[] sizes = {25, 20, 50, 100, 200, 10};
        //int[] sizes = {25};
        for (int n : sizes) {
            double cutoff = 0.5;

            ArrayList<Dataset> datasets = new ArrayList<>();
            //resaveGenbankFile(new File("C:/dev/thesis/hiv_full/1a/complete.gb"),new File("C:/dev/thesis/hiv_full/1a/complete2.gb"));
            //resaveGenbankFile(new File("C:/dev/thesis/hiv_full/1b/complete.gb"),new File("C:/dev/thesis/hiv_full/1b/complete2.gb"));
            // resaveGenbankFile(new File("C:/dev/thesis/hiv_full/1c/complete.gb"),new File("C:/dev/thesis/hiv_full/1c/complete2.gb"));
            // resaveGenbankFile(new File("C:/dev/thesis/hiv_full/1d/complete.gb"),new File("C:/dev/thesis/hiv_full/1d/complete2.gb"));
            //  resaveGenbankFile(new File("C:/dev/thesis/hiv_full/1g/complete.gb"),new File("C:/dev/thesis/hiv_full/1g/complete2.gb"));
            //  resaveGenbankFile(new File("C:/dev/thesis/hiv_full/1o/complete.gb"),new File("C:/dev/thesis/hiv_full/1o/complete2.gb"));
            // resaveGenbankFile(new File("C:/dev/thesis/hiv_full/2/complete.gb"),new File("C:/dev/thesis/hiv_full/2/complete2.gb"));
            //resaveGenbankFile(new File("C:/dev/thesis/hiv_full/siv/complete.gb"),new File("C:/dev/thesis/hiv_full/siv/complete2.gb"));
            //datasets.add(new Dataset("C:/dev/thesis/hiv_full/hiv1/", new File("C:/dev/thesis/hiv_full/hiv1/complete.gb"), "hiv1", false));
            //datasets.add(new Dataset("C:/dev/thesis/hiv_full/1a/", new File("C:/dev/thesis/hiv_full/1a/complete2.gb"), "hiv1a", true));
            //datasets.add(new Dataset("C:/dev/thesis/hiv_full/1b/", new File("C:/dev/thesis/hiv_full/1b/complete2.gb"), "hiv1b", false));
            //datasets.add(new Dataset("C:/dev/thesis/hiv_full/1c/", new File("C:/dev/thesis/hiv_full/1c/complete2.gb"), "hiv1c", false));
            //datasets.add(new Dataset("C:/dev/thesis/hiv_full/1d/", new File("C:/dev/thesis/hiv_full/1d/complete2.gb"), "hiv1d", true));
            //datasets.add(new Dataset("C:/dev/thesis/hiv_full/1g/", new File("C:/dev/thesis/hiv_full/1g/complete2.gb"), "hiv1g", true));
            //datasets.add(new Dataset("C:/dev/thesis/hiv_full/1o/", new File("C:/dev/thesis/hiv_full/1o/complete2.gb"), "hiv1o", true));
            //datasets.add(new Dataset("C:/dev/thesis/hiv_full/2/", new File("C:/dev/thesis/hiv_full/2/complete2.gb"), "hiv2", true));
            //datasets.add(new Dataset("C:/dev/thesis/hiv_full/siv/", new File("C:/dev/thesis/hiv_full/siv/complete2.gb"), "siv", false));
            datasets.add(new Dataset("C:/dev/thesis/hcv/1/", new File("C:/dev/thesis/hcv/1/complete_a_b.gb"), "hcv1", false));
            datasets.add(new Dataset("C:/dev/thesis/hcv/1a/", new File("C:/dev/thesis/hcv/1a/complete.gb"), "hcv1a", false));
            datasets.add(new Dataset("C:/dev/thesis/hcv/1b/", new File("C:/dev/thesis/hcv/1b/complete.gb"), "hcv1b", false));
            datasets.add(new Dataset("C:/dev/thesis/hcv/2/", new File("C:/dev/thesis/hcv/2/complete.gb"), "hcv2", false));
            datasets.add(new Dataset("C:/dev/thesis/hcv/2a/", new File("C:/dev/thesis/hcv/2a/complete.gb"), "hcv2a", true));
            datasets.add(new Dataset("C:/dev/thesis/hcv/2b/", new File("C:/dev/thesis/hcv/2b/complete.gb"), "hcv2b", true));
            datasets.add(new Dataset("C:/dev/thesis/hcv/3/", new File("C:/dev/thesis/hcv/3/complete.gb"), "hcv3", true));
            datasets.add(new Dataset("C:/dev/thesis/hcv/4/", new File("C:/dev/thesis/hcv/4/complete.gb"), "hcv4", true));
            datasets.add(new Dataset("C:/dev/thesis/hcv/6/", new File("C:/dev/thesis/hcv/6/complete.gb"), "hcv6", true));
           // datasets.add(new Dataset("C:/dev/thesis/westnile/", new File("C:/dev/thesis/westnile/complete.gb"), "westnile", false));
            //datasets.add(new Dataset("C:/dev/thesis/norovirus/", new File("C:/dev/thesis/norovirus/complete.gb"), "norovirus", false));
             

            //  datasets.add(new Dataset("C:/dev/thesis/siv/", new File("C:/dev/thesis/siv/complete.gb"), "siv", true));
            // datasets.add(new Dataset("C:/dev/thesis/hiv/", new File("C:/dev/thesis/hiv/complete.gb"), "hiv", false));
            //datasets.add(new Dataset("C:/dev/thesis/dengue/", new File("C:/dev/thesis/dengue/complete.gb"), "dengue", false));
            //datasets.add(new Dataset("C:/dev/thesis/dengue1/", new File("C:/dev/thesis/dengue1/complete.gb"), "dengue1", false));
            //datasets.add(new Dataset("C:/dev/thesis/dengue2/", new File("C:/dev/thesis/dengue2/complete.gb"), "dengue2", false));
            // datasets.add(new Dataset("C:/dev/thesis/dengue3/", new File("C:/dev/thesis/dengue3/complete.gb"), "dengue3", false));
            //datasets.add(new Dataset("C:/dev/thesis/dengue4/", new File("C:/dev/thesis/dengue4/complete.gb"), "dengue4", false));
            // datasets.add(new Dataset("C:/dev/thesis/csfv/", new File("C:/dev/thesis/csfv/complete.gb"), "csfv", true));        
            //datasets.add(new Dataset("C:/dev/thesis/gb/", new File("C:/dev/thesis/gb/complete.gb"), "gb", true));
           // datasets.add(new Dataset("C:/dev/thesis/jev/", new File("C:/dev/thesis/jev/complete.gb"), "jev", false));
            // datasets.add(new Dataset("C:/dev/thesis/bvdv/", new File("C:/dev/thesis/bvdv/complete.gb"), "bvdv", true));

            //  boolean allowXs = true;
            // String dir = "C:/dev/thesis/bvdv/";
            // File genbankFile = new File("C:/dev/thesis/bvdv/complete.gb");
            //String organism = "bvdv";
            for (Dataset dataset : datasets) {
                //saveGenbankFileAsFasta(dataset,new File(dataset.dir+File.separator+"complete.fas"));
               /*
                 * if( 1!= 2) { continue; }
                 */
                System.out.println(dataset.organism);
                boolean allowXs = dataset.allowXs;
                String dir = dataset.dir + n + File.separator;
                new File(dir).mkdir();
                File genbankFile = dataset.genbankFile;
                String organism = dataset.organism;

                BufferedReader br = new BufferedReader(new FileReader(genbankFile));
                Namespace ns = RichObjectFactory.getDefaultNamespace();
                RichSequenceIterator seqs = RichSequence.IOTools.readGenbankDNA(br, ns);

                HashMap<String, Integer> geneCount = new HashMap<String, Integer>();
                String keyword = "CDS";
                String keywordGene = "gene";

                // get a list of genes
                long count = 0;
                while (seqs.hasNext()) {
                    count++;
                    RichSequence rs = seqs.nextRichSequence();
                    String description = rs.getDescription().toLowerCase();
                    for (String forbidden : forbiddenKeywords) {
                        description.contains(forbidden.toLowerCase());
                        continue;
                    }

                    Iterator<org.biojava.bio.seq.Feature> it = rs.features();

                    boolean first = true;
                    while (it.hasNext()) {
                        org.biojava.bio.seq.Feature ft = it.next();

                        if (ft.getTypeTerm().getName().equalsIgnoreCase(keyword)) {
                            String sequence = ft.getSequence().seqString().toUpperCase();
                            if (first) {
                                //sequences.add(new KmerSequence(rs.getName(), GeneFinder.getKmerArray(sequence, 5)));
                                first = false;
                            }

                            if (ft.getAnnotation().containsProperty(keywordGene)) {
                                String geneName = ft.getAnnotation().getProperty(keywordGene).toString().toLowerCase();
                                geneCount.put(geneName, (geneCount.containsKey(geneName) ? geneCount.get(geneName) : 0) + 1);

                            } else if (ft.getLocation().getMax() - ft.getLocation().getMin() > 2000) {

                                geneCount.put("polyprotein", (geneCount.containsKey("polyprotein") ? geneCount.get("polyprotein") : 0) + 1);
                            }
                        }
                    }
                }
                br.close();

                Set<Entry<String, Integer>> geneSet = geneCount.entrySet();
                Iterator<Entry<String, Integer>> geneIterator = geneSet.iterator();
                ArrayList<String> genes = new ArrayList<>();
                while (geneIterator.hasNext()) {
                    Entry<String, Integer> entry = geneIterator.next();
                    double geneNum = entry.getValue();
                    double p = geneNum / ((double) count);
                    if (p > cutoff) {
                        System.out.println(entry.getKey() + "\t" + p + "\t" + geneNum);
                        genes.add(entry.getKey());
                    }
                }

                if (genes.size() > 1) {
                    genes.remove("polyprotein");
                }

                System.out.println(count);

                //HashMap<String, String> sequenceGeneMap = new HashMap<>();
                // find the set of sequences that contains all the genes
                HashSet<String> minimumSet = new HashSet<>();
                ArrayList<KmerSequence> sequences = new ArrayList<>();
                br = new BufferedReader(new FileReader(genbankFile));
                ns = RichObjectFactory.getDefaultNamespace();
                seqs = RichSequence.IOTools.readGenbankDNA(br, ns);
                while (seqs.hasNext()) {
                    RichSequence rs = seqs.nextRichSequence();
                    Iterator<org.biojava.bio.seq.Feature> it = rs.features();

                    String description = rs.getDescription().toLowerCase();
                    for (String forbidden : forbiddenKeywords) {
                        description.contains(forbidden.toLowerCase());
                        continue;
                    }

                    boolean[] hasGene = new boolean[genes.size()];
                    String sequence = null;
                    while (it.hasNext()) {
                        org.biojava.bio.seq.Feature ft = it.next();

                        if (ft.getTypeTerm().getName().equalsIgnoreCase(keyword)) {
                            if (ft.getAnnotation().containsProperty(keywordGene)) {
                                String geneName = ft.getAnnotation().getProperty(keywordGene).toString().toLowerCase();
                                //geneCount.put(geneName, (geneCount.containsKey(geneName) ? geneCount.get(geneName) : 0) + 1);
                                int index = genes.indexOf(geneName);
                                if (index >= 0) {
                                    Iterator<Location> blocks = ft.getLocation().blockIterator();

                                    boolean setTrue = true;
                                    while (blocks.hasNext()) {
                                        Location lt = blocks.next();
                                        int start = lt.getMin() - 1;
                                        int length = lt.getMax() - lt.getMin() + 1;

                                        String geneseq = ft.getSequence().seqString().toUpperCase().substring(start, start + length);
                                        String aminoAcid = GeneticCode.translateNucleotideSequence(geneseq);
                                        int stopCodonPos = aminoAcid.indexOf("*");
                                        if ((aminoAcid.contains("X") && !allowXs) || stopCodonPos >= 0 && stopCodonPos != aminoAcid.length() - 1) // if stop codon not in last position
                                        {
                                            // do not add gene, because contains stop codon in middle
                                            setTrue = false;
                                        }
                                    }

                                    hasGene[index] = setTrue;
                                    sequence = ft.getSequence().seqString();
                                }
                            } else if (ft.getLocation().getMax() - ft.getLocation().getMin() > 2000) {
                                String geneName = "polyprotein";
                                //geneCount.put(geneName, (geneCount.containsKey(geneName) ? geneCount.get(geneName) : 0) + 1);
                                int index = genes.indexOf(geneName);
                                if (index >= 0) {
                                    Iterator<Location> blocks = ft.getLocation().blockIterator();

                                    boolean setTrue = true;
                                    while (blocks.hasNext()) {
                                        Location lt = blocks.next();
                                        int start = lt.getMin() - 1;
                                        int length = lt.getMax() - lt.getMin() + 1;

                                        String geneseq = ft.getSequence().seqString().toUpperCase().substring(start, start + length);
                                        String aminoAcid = GeneticCode.translateNucleotideSequence(geneseq);
                                        int stopCodonPos = aminoAcid.indexOf("*");
                                        if ((aminoAcid.contains("X") && !allowXs) || stopCodonPos >= 0 && stopCodonPos != aminoAcid.length() - 1) // if stop codon not in last position
                                        {
                                            // do not add gene, because contains stop codon in middle
                                            setTrue = false;
                                        }
                                    }

                                    hasGene[index] = setTrue;
                                    sequence = ft.getSequence().seqString();
                                }
                            }
                        }
                    }

                    boolean hasAll = true;
                    for (int i = 0; i < hasGene.length; i++) {
                        if (!hasGene[i]) {
                            hasAll = false;
                            break;
                        }
                    }

                    if (hasAll && sequence != null) {
                        minimumSet.add(rs.getName());
                        KmerSequence seq = new KmerSequence(rs.getName(), getKmerArray(sequence.toUpperCase(), k));
                        seq.seq = sequence.toUpperCase();
                        sequences.add(seq);
                    }
                }

                System.out.println("Has all = " + minimumSet.size());

                if (sequences.size() < 2) {
                    continue;
                }
                ArrayList<KmerSequence> selection = GeneFinder.getDiverseSelection(sequences, n);
                HashSet<String> selectionSet = new HashSet<>();
                for (KmerSequence seq : selection) {
                    selectionSet.add(seq.id);
                }

                File allSeqFile = new File(dir + organism + "_all_" + n + ".fas");
                File allSeqAlignedFile = new File(dir + organism + "_all_" + n + "_aligned.fas");
                try {
                    BufferedWriter buffer = new BufferedWriter(new FileWriter(allSeqFile));
                    for (KmerSequence seq : selection) {
                        buffer.write(">" + seq.id + "_" + organism);
                        buffer.newLine();
                        buffer.write(seq.seq);
                        buffer.newLine();
                    }
                    buffer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                for (String gene : genes) {
                    new File(dir + organism + "_" + gene + "_" + n + ".fas").delete();
                    new File(dir + organism + "_" + gene + "_" + n + "_nooverlap" + ".fas").delete();
                }


                System.out.println("Set size = " + minimumSet.size());
                br = new BufferedReader(new FileReader(genbankFile));
                ns = RichObjectFactory.getDefaultNamespace();
                seqs = RichSequence.IOTools.readGenbankDNA(br, ns);
                HashSet<String> completed = new HashSet<>();
                while (seqs.hasNext()) {
                    RichSequence rs = seqs.nextRichSequence();
                    Iterator<org.biojava.bio.seq.Feature> it = rs.features();

                    String description = rs.getDescription().toLowerCase();
                    for (String forbidden : forbiddenKeywords) {
                        description.contains(forbidden.toLowerCase());
                        continue;
                    }

                    ArrayList<Gene> seqGenes = new ArrayList<Gene>();
                    if (selectionSet.contains(rs.getName())) {
                        while (it.hasNext()) {
                            org.biojava.bio.seq.Feature ft = it.next();
                            if (ft.getTypeTerm().getName().equalsIgnoreCase(keyword)) {
                                if (ft.getAnnotation().containsProperty(keywordGene)) {
                                    String geneName = ft.getAnnotation().getProperty(keywordGene).toString().toLowerCase();
                                    int index = genes.indexOf(geneName);
                                    if (index >= 0) {
                                        try {

                                            Iterator<Location> blocks = ft.getLocation().blockIterator();
                                            int geneno = 0;

                                            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + organism + "_" + geneName + "_" + n + ".fas", true));
                                            writer.write(">" + rs.getName() + "_" + geneName + "_" + organism + "\n");
                                            while (blocks.hasNext()) {
                                                Location lt = blocks.next();
                                                int start = lt.getMin() - 1;
                                                int length = lt.getMax() - lt.getMin() + 1;


                                                String seq = ft.getSequence().seqString().toUpperCase().substring(start, start + length);
                                                String lastCodon = GeneticCode.translateNucleotideSequence(seq.substring(seq.length() - 3, seq.length()));
                                                if (lastCodon.equals("*")) {
                                                    seq = seq.substring(0, seq.length() - 3);
                                                }


                                                Gene gene = new Gene();
                                                gene.geneName = geneName;
                                                gene.start = start;
                                                gene.length = seq.length() - 3;
                                                gene.fullSeq = ft.getSequence().seqString();
                                                seqGenes.add(gene);

                                                writer.write(seq);
                                                //  BufferedWriter writer = new BufferedWriter(new FileWriter(dir + geneName + "_" + geneno + ".fas", true));
                                                //writer.write(">" + rs.getName() + "_" + geneName + "\n");
                                                //writer.write(ft.getSequence().seqString().toUpperCase().substring(start, start + length));
                                                //  writer.newLine();
                                                // writer.close();
                                                geneno++;
                                            }
                                            writer.newLine();
                                            writer.close();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                } else if (ft.getLocation().getMax() - ft.getLocation().getMin() > 2000) {
                                    if (completed.contains(rs.getName())) // prevent adding the same record twice if in file twice for some reason
                                    {
                                        continue;
                                    } else {
                                        completed.add(rs.getName());
                                    }
                                    String geneName = "polyprotein";
                                    int index = genes.indexOf(geneName);
                                    if (index >= 0) {
                                        try {

                                            Iterator<Location> blocks = ft.getLocation().blockIterator();
                                            int geneno = 0;

                                            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + organism + "_" + geneName + "_" + n + ".fas", true));
                                            writer.write(">" + rs.getName() + "_" + geneName + "_" + organism + "\n");
                                            while (blocks.hasNext()) {
                                                Location lt = blocks.next();
                                                int start = lt.getMin() - 1;
                                                int length = lt.getMax() - lt.getMin() + 1;

                                                String seq = ft.getSequence().seqString().toUpperCase().substring(start, start + length);
                                                String lastCodon = GeneticCode.translateNucleotideSequence(seq.substring(seq.length() - 3, seq.length()));
                                                if (lastCodon.equals("*")) {
                                                    seq = seq.substring(0, seq.length() - 3);
                                                }
                                                writer.write(seq);
                                                //  BufferedWriter writer = new BufferedWriter(new FileWriter(dir + geneName + "_" + geneno + ".fas", true));
                                                //writer.write(">" + rs.getName() + "_" + geneName + "\n");
                                                //writer.write(ft.getSequence().seqString().toUpperCase().substring(start, start + length));
                                                //  writer.newLine();
                                                // writer.close();
                                                geneno++;
                                            }
                                            writer.newLine();
                                            writer.close();
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }



                    //System.out.println(seqGenes);
                    ArrayList<Gene> noOverlap = GeneFinder.removeCodingOverlaps(seqGenes);
                    for (Gene gene : noOverlap) {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(dir + organism + "_" + gene.geneName + "_" + n + "_nooverlap" + ".fas", true));
                        writer.write(">" + rs.getName() + "_" + gene.geneName + "_" + organism + "\n");
                        writer.write(gene.subSeq + "\n");
                        writer.close();
                    }
                }



                br.close();

                for (String gene : genes) {
                    File inFastaFile = new File(dir + organism + "_" + gene + "_" + n + ".fas");
                    File outFastaFile = new File(dir + organism + "_" + gene + "_" + n + "_aligned.fas");
                    codonMAFFTalignment(inFastaFile, outFastaFile);
                }

                // align non-overlapping
                for (String gene : genes) {
                    File inFastaFile = new File(dir + organism + "_" + gene + "_" + n + "_nooverlap" + ".fas");
                    if (inFastaFile.exists()) {
                        File outFastaFile = new File(dir + organism + "_" + gene + "_" + n + "_nooverlap" + "_aligned.fas");
                        codonMAFFTalignment(inFastaFile, outFastaFile);
                    }
                }

                MAFFTalignment(allSeqFile, allSeqAlignedFile);


                //process = Runtime.getRuntime().exec(cmd, null, tempDir);
            }
        }
    }
    static String MAFFT_EXECUTABLE = "bin/mafft-6.952-win64/mafft-win/mafft.bat";

    public static class Gene {

        String sequenceName;
        String geneName;
        String organism;
        String fullSeq;
        int start;
        int length;
        String subSeq;

        @Override
        protected Object clone() {
            Gene gene = new Gene();
            gene.sequenceName = sequenceName;
            gene.geneName = geneName;
            gene.organism = organism;
            gene.fullSeq = fullSeq;
            gene.start = start;
            gene.length = length;
            gene.subSeq = subSeq;
            return gene;
        }

        @Override
        public String toString() {
            return "Gene{" + "sequenceName=" + sequenceName + ", geneName=" + geneName + ", organism=" + organism + ", start=" + start + ", length=" + length + '}';
        }
    }

    public static ArrayList<Gene> removeCodingOverlaps(ArrayList<Gene> genes) {

        ArrayList<Gene> newGenes = new ArrayList<>();

        if (genes.size() > 0) {
            int[] count = new int[genes.get(0).fullSeq.length()];
            Gene[] geneArray = new Gene[count.length];

            for (Gene gene : genes) {
                for (int i = gene.start; i < gene.start + gene.length; i++) {
                    count[i]++;
                    geneArray[i] = gene;
                }
            }

            int prev = -1;
            int startPos = -1;
            for (int i = 0; i < count.length; i++) {
                Gene gene = geneArray[i];
                Gene nextGene = null;
                if (i < count.length - 1) {
                    if (count[i + 1] == 1) {
                        nextGene = geneArray[i + 1];
                    }
                }

                if (prev != 1 && count[i] == 1) {
                    startPos = i;
                }

                if (count[i] == 1 && !Objects.equals(gene, nextGene)) {
                    Gene newGene = (Gene) gene.clone();
                    newGene.start = startPos;
                    int end = i;

                    int frame = gene.start % 3;
                    while (newGene.start % 3 != frame) {
                        newGene.start++;
                    }

                    while ((end + 1) % 3 != frame) {
                        end--;
                    }


                    newGene.length = end - newGene.start + 1;

                    if (newGene.start >= 0 && newGene.start < end && newGene.start + newGene.length <= gene.fullSeq.length()) {
                        // System.out.println(gene.geneName);
                        // System.out.println("a" + gene.start);
                        // System.out.println("e" + startPos);
                        //System.out.println("b" + i);
                        //System.out.println("c" + newGene.start);
                        //System.out.println("d" + end);
                        newGene.subSeq = gene.fullSeq.substring(newGene.start, newGene.start + newGene.length);
                        newGenes.add(newGene);
                    } else {
                        //   System.out.println("Error" + "\t" + newGene.start + "\t" + (newGene.start + newGene.length) + "\t" + gene.fullSeq.length());
                    }
                }

                prev = count[i];
            }
        }


        //ArrayList<Gem
        HashMap<String, Gene> geneMap = new HashMap<>();
        for (Gene newGene : newGenes) {
            Gene finalGene = new Gene();
            if (geneMap.containsKey(newGene.geneName)) {
                finalGene = geneMap.get(newGene.geneName);
            } else {
                finalGene.geneName = newGene.geneName;
                finalGene.subSeq = "";
            }

            finalGene.subSeq += newGene.subSeq;
            geneMap.put(finalGene.geneName, finalGene);
        }
        ArrayList<Gene> finalGenes = new ArrayList<>();
        finalGenes.addAll(geneMap.values());
        /*
         * HashMap<String, Integer> mapCount = new HashMap<>(); for(Gene newGene
         * : newGenes) {
         * mapCount.put(newGene.geneName,(mapCount.containsKey(newGene.geneName)
         * ? mapCount.get(newGene.geneName) : 0)+1);
         *
         * }
         */

        return finalGenes;
    }

    public static void MAFFTalignment(File inFastaFile, File outFastaFile) {
        try {
            String cmd = new File(MAFFT_EXECUTABLE).getAbsolutePath() + " --retree 2 --maxiterate 1000 " + inFastaFile.getAbsolutePath() + " > " + outFastaFile.getAbsolutePath();
            Process p = Runtime.getRuntime().exec(cmd);
            Application.nullOutput(p.getInputStream());
            Application.nullOutput(p.getErrorStream());;
            int code = p.waitFor();
            if (code == 0) {
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(GeneFinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GeneFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void codonMAFFTalignment(File inFastaFile, File outFastaFile) {
        try {
            File tempProteinInFile = new File(inFastaFile.getParentFile() + File.separator + "protein_" + inFastaFile.getName());
            File tempProteinAlignedFile = new File(inFastaFile.getParentFile() + File.separator + "protein_aligned_" + inFastaFile.getName());
            GeneticCode.saveAsProteinAlignment(inFastaFile, tempProteinInFile);
            String cmd = new File(MAFFT_EXECUTABLE).getAbsolutePath() + " --retree 2 --maxiterate 1000 " + tempProteinInFile.getAbsolutePath() + " > " + tempProteinAlignedFile.getAbsolutePath();
            Process p = Runtime.getRuntime().exec(cmd);
            Application.nullOutput(p.getInputStream());
            Application.nullOutput(p.getErrorStream());;
            int code = p.waitFor();
            if (code == 0) {
                ArrayList<String> codonSequences = new ArrayList<>();
                ArrayList<String> codonSequenceNames = new ArrayList<>();
                IO.loadFastaSequences(inFastaFile, codonSequences, codonSequenceNames);
                ArrayList<String> proteinSequences = new ArrayList<>();
                ArrayList<String> proteinSequenceNames = new ArrayList<>();
                IO.loadFastaSequences(tempProteinAlignedFile, proteinSequences, proteinSequenceNames);
                for (int i = 0; i < proteinSequences.size(); i++) {
                    String proteinSeq = proteinSequences.get(i).toUpperCase();
                    String codonSeq = codonSequences.get(i).toUpperCase();
                    String seq = "";
                    int k = 0;
                    for (int j = 0; j < proteinSeq.length(); j++) {
                        if (proteinSeq.charAt(j) == '-') {
                            seq += "---";
                        } else {
                            //System.out.println(j+"\t"+k+"\t"+(k*3)+"\t"+codonSeq.length()+"\t"+proteinSeq.length()+"\t"+proteinSeq.charAt(j));

                            seq += codonSeq.substring(k * 3, Math.min(k * 3 + 3, codonSeq.length()));
                            k++;
                        }
                    }
                    proteinSequences.set(i, seq);
                }
                IO.saveToFASTAfile(proteinSequences, proteinSequenceNames, outFastaFile);
                tempProteinInFile.delete();
                tempProteinAlignedFile.delete();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(GeneFinder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GeneFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<KmerSequence> getDiverseSelection(ArrayList<KmerSequence> sequences, int selection) {
        HashMap<String, Integer> idIndexMap = new HashMap<String, Integer>();
        for (int i = 0; i < sequences.size(); i++) {
            idIndexMap.put(sequences.get(i).id, i);
        }
        double[][] distanceMatrix = new double[sequences.size()][sequences.size()];

        ArrayList<KmerSequence> masterList = (ArrayList<KmerSequence>) sequences.clone();
        Random random = new Random(3410381490644177142L);
        ArrayList<KmerSequence> selectedSequences = new ArrayList<>();
        selectedSequences.add(masterList.remove(random.nextInt(masterList.size())));
        int n = 500;
        while (selectedSequences.size() < selection && masterList.size() > 0) {
            double maxMinD = 0;
            double minMinD = Double.MAX_VALUE;
            int furthestSeqIndex = 0;
            for (int i = 0; i < n; i++) {
                int randomIndex = random.nextInt(masterList.size());
                KmerSequence randomSeq = masterList.get(randomIndex);
                int x = idIndexMap.get(randomSeq.id);
                double minD = Double.MAX_VALUE;
                //System.out.println("B " + randomSeq.seq.substring(0, 100));
                //.out.println("B " + randomSeq);
                for (KmerSequence seq : selectedSequences) {
                    int y = idIndexMap.get(seq.id);
                    if (distanceMatrix[x][y] == 0) {
                        distanceMatrix[x][y] = distance(seq.kmer, randomSeq.kmer);
                        distanceMatrix[y][x] = distanceMatrix[x][y];
                    }

                    minD = Math.min(minD, distanceMatrix[x][y]);
                }


                if (minD >= maxMinD) {
                    maxMinD = minD;
                    furthestSeqIndex = randomIndex;
                }
                minMinD = Math.min(minMinD, minD);
            }

            //System.out.println(selectedSequences.size() + "\t" + minMinD + "\t" + maxMinD);
            selectedSequences.add(masterList.remove(furthestSeqIndex));
        }

        return selectedSequences;
    }

    public static double getSelectionScore(double[][] matrix, boolean[] selection) {
        double score = 0;
        for (int i = 0; i < selection.length; i++) {
            if (selection[i]) {
                for (int j = 0; j < selection.length; j++) {
                    if (selection[j]) {
                        score += matrix[i][j];
                    }
                }
            }
        }
        return score;
    }

    public static boolean[] greedy(int select, double[][] distanceMatrix, int numSequences) {
        boolean[] selection = new boolean[numSequences];

        double[] scores = new double[numSequences];
        for (int i = 0; i < scores.length; i++) {
            for (int j = 0; j < scores.length; j++) {
                scores[i] += distanceMatrix[i][j];
            }
        }

        for (int k = 0; k < select; k++) {
            int maxIndex = -1;
            for (int i = 0; i < selection.length; i++) {
                if ((!selection[i] && maxIndex == -1) || (!selection[i] && scores[i] >= scores[maxIndex])) {
                    maxIndex = i;
                }
            }
            selection[maxIndex] = true;
        }

        int c = 0;
        for (int i = 0; i < selection.length; i++) {
            if (selection[i]) {
                c++;
            }
        }

        return selection;
    }

    public static float[] getKmerArray(String sequence, int k) {
        int[] arr = new int[(int) Math.pow(5, k)];
        double count = 0;
        for (int i = 0; i < sequence.length() - k + 1; i++) {
            arr[getIndex(sequence.substring(i, i + k))]++;
            count++;
        }
        float[] arr2 = new float[arr.length];
        for (int i = 0; i < arr.length; i++) {
            arr2[i] = (float) ((double) arr[i] / (double) count);
        }
        return arr2;
    }

    public static int getIndex(String kmer) {
        String s = kmer.replace("A", "0").replace("C", "1").replace("G", "2").replace("T", "3").replace("U", "3").replaceAll("[^0-9]", "4");
        return Integer.parseInt(s, 5);
    }

    public static double[][] getDistanceMatrix(ArrayList<KmerSequence> sequences) {
        double[][] matrix = new double[sequences.size()][sequences.size()];
        for (int i = 0; i < sequences.size(); i++) {
            if (i % 10 == 0) {
                System.out.println(i);
            }
            for (int j = 0; j < sequences.size(); j++) {
                matrix[i][j] = GeneFinder.distance(sequences.get(i).kmer, sequences.get(j).kmer);
            }
        }
        return matrix;
    }

    public static double distance(float[] a1, float[] a2) {
        double distance = 0;
        for (int i = 0; i < a1.length; i++) {
            distance += Math.pow(a1[i] - a2[i], 2);
        }
        return Math.sqrt(distance);
    }

    public static class KmerSequence {

        public String id;
        //String sequence;
        public float[] kmer;
        public String seq;

        public KmerSequence(String id, float[] kmer) {
            this.id = id;
            this.kmer = kmer;
        }

        @Override
        public String toString() {
            String ret = id + ": ";
            for (int i = 0; i < Math.min(kmer.length, 100); i++) {
                ret += kmer[i] + ", ";
            }
            return ret;
        }
    }
}
