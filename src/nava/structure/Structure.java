package nava.structure;

import java.io.File;
import nava.utils.RNAFoldingTools;

/**
 * Class that represents a secondary structure and metadata.
 *
 * @author Michael Golden
 */
public class Structure implements Comparable<Structure> {

    /**
     * 2 x genomeLength array of paired nucleotide positions. pairedSites[0][i] = nucleotidePosition of nucleotide i, pairedSites[1][i] = nucleotide position of pairing partner of i or 0 zero if unpaired.
     * If pairedSites[a][b] =< -1, then nucleotide position = genomeLength + pairedSites[a][b] + 1
     */
    public int[] pairedSites;
    public String name = "";
    /**
     * String representing the nucleotide sequence of this structure.
     */
    public String sequence = "";
    public int startPosition = 0;
    public int length;
    public int index;

    public Structure(int length) {
        this.length = length;
        this.pairedSites = new int[length];
    }
    
    public Structure(int [] pairedSites, String name)
    {
        this.length = pairedSites.length;
        this.pairedSites = pairedSites;
        this.name = name;
    }
     public Structure(int [] pairedSites, int startPos, String name)
    {
        this.length = pairedSites.length;
        this.startPosition = startPos;
        this.pairedSites = pairedSites;
        this.name = name;
    }

    public String getDotBracketString() {
       return RNAFoldingTools.getDotBracketStringFromPairedSites(pairedSites);
    }

    /**
     *
     * @return the start position of the structure in the parent genome (one-offset)
     */
    public int getStartPosition ()
    {
        return startPosition;
    }

    /**
     * @return the end position (inclusive) of the structure in the parent genome.
     */
    public int getEndPosition ()
    {
        return startPosition+length;
    }

    public int getLength ()
    {
        return length;
    }

    @Override
    public String toString ()
    {
        return name + " ["+getStartPosition()+"-"+getEndPosition()+"]:"+getDotBracketString();
    }

    public int [][] allShortestPaths ()
    {
        int [] [] distance = new int[length][length];
        return distance;
    }
    
    

    /*public static void main(String [] args)
    {
        try {
            Structure s = StructureParser.parseNaspCtFile(new File("D:/NASP/BFDV/BFDV_10Seq.ct"));
            for (int i = 0; i < s.length; i++)
            {
                System.out.println(s.pairedSites[0][i]+"\t"+s.pairedSites[1][i]);
            }
            System.out.println(s.getDotBracketString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Structure other = (Structure) obj;
        if (this.startPosition != other.startPosition) {
            return false;
        }
        if (this.length != other.length) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public int compareTo(Structure o) {
        if(this.length < o.length)
        {
            return -1;
        }
        else
        if(this.length > o.length)
        {
            return 1;
        }
        return 0;
    }
}
