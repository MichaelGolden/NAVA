/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.ui.structurevis;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava.bio.BioException;
import org.biojava.bio.symbol.Location;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

/**
 *
 * @author Michael
 */
public class AnnotationData {
    
   public static Color[] featureColours = {
        new Color(255, 190, 190),
        new Color(190, 255, 255),
        new Color(190, 190, 255),
        new Color(255, 190, 255),
        new Color(200, 255, 190),
        new Color(255, 255, 190)};

    public int sequenceLength;
    public ArrayList<Feature> features = new ArrayList<>();

    @Override
    public String toString() {
        return features.toString();
    }
    
    /**
     * Automatically assign a different colour to each feature.
     */
    public void assignColors()
    {
        for(int i = 0 ; i < features.size() ; i++)
        {
            Feature f = features.get(i);
            for(int j = 0 ; j < f.blocks.size() ; j++)
            {
                f.blocks.get(j).color = featureColours[i % featureColours.length];
            }
        }
    }

    /**
     * Returns an AnnotationData object, containing a natural stacking of
     * sequence features, i.e. places the features on different rows so that
     * they are non-overlapping.
     *
     * @param annotationData
     * @return
     */
    public static AnnotationData stackFeatures(AnnotationData annotationData) {
        AnnotationData ret = new AnnotationData();
        ret.sequenceLength = annotationData.sequenceLength;
        ret.features.addAll(annotationData.features);
        Collections.sort(ret.features);
        Collections.reverse(ret.features);


        ArrayList<Feature> addedFeatures = new ArrayList<>();
        int maxRow = 0;
        for (int row = 0; row < ret.features.size(); row++) {
            Feature currentFeature = ret.features.get(row);
            int currentRow = 0;
            for (currentRow = 0; currentRow <= maxRow + 1; currentRow++) {
                currentFeature.row = currentRow;
                if (isOverlap(currentFeature, addedFeatures)) {
                } else {
                    break;
                }
            }
            maxRow = Math.max(maxRow, currentRow);
            currentFeature.row = currentRow;
            addedFeatures.add(currentFeature);
        }
        ret.features = addedFeatures;
        return ret;
    }

    /**
     * Tests whether a given feature overlaps any features in a provided list of
     * features.
     *
     * @param f
     * @param otherFeatures
     * @return
     */
    public static boolean isOverlap(Feature f, ArrayList<Feature> otherFeatures) {
        for (int i = 0; i < otherFeatures.size(); i++) {
            if (Feature.isOverlap(f, otherFeatures.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a list of features, return a list of those features which are visible.
     *
     * @param annotationData
     * @return
     */
    public static AnnotationData getVisible(AnnotationData annotationData) {
        AnnotationData ret = new AnnotationData();
        ret.sequenceLength = annotationData.sequenceLength;
        for (int i = 0; i < annotationData.features.size(); i++) {
            if (annotationData.features.get(i).visible) {
                ret.features.add(annotationData.features.get(i));
            }
        }
        return ret;
    }

    /**
     * Given a GenBank file, return an AnnotationData object containing the
     * annotations.
     *
     * @param genBankFile
     * @return
     * @throws BioException
     */
    public static AnnotationData readAnnotations(File genBankFile) throws BioException, IOException {
        AnnotationData annotationData = new AnnotationData();
        annotationData.sequenceLength = 0;

        BufferedReader br = new BufferedReader(new FileReader(genBankFile));
        Namespace ns = RichObjectFactory.getDefaultNamespace();
        RichSequenceIterator seqs = RichSequence.IOTools.readGenbankDNA(br, ns);

        while (seqs.hasNext()) {
            RichSequence rs = seqs.nextRichSequence();
            Iterator<org.biojava.bio.seq.Feature> it = rs.features();
            while (it.hasNext()) {
                org.biojava.bio.seq.Feature ft = it.next();

                Feature feature = new Feature();
                feature.min = ft.getLocation().getMin();
                feature.max = ft.getLocation().getMax();
                if (ft.getType().equalsIgnoreCase("source")) {
                    annotationData.sequenceLength = Math.max(annotationData.sequenceLength, ft.getLocation().getMax());
                }

                if (ft.getAnnotation().containsProperty("gene")) {
                    feature.name = ft.getAnnotation().getProperty("gene").toString();
                } else if (ft.getAnnotation().containsProperty("product")) {
                    feature.name = ft.getAnnotation().getProperty("product").toString();
                } else {
                    feature.name = ft.getType();
                    feature.visible = false;
                }

                Iterator<Location> blocks = ft.getLocation().blockIterator();
                while (blocks.hasNext()) {
                    Location lt = blocks.next();
                    Block block = new Block(feature, lt.getMin(), lt.getMax());
                    feature.blocks.add(block);
                }

                annotationData.features.add(feature);
            }
        }
        br.close();

        return annotationData;
    }

    public static void main(String[] args) throws IOException {
        try {
            System.out.println(readAnnotations(new File("examples/annotations/refseq.gb")));
            System.out.println(stackFeatures(readAnnotations(new File("examples/annotations/refseq.gb"))));
        } catch (BioException ex) {
            Logger.getLogger(AnnotationData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}