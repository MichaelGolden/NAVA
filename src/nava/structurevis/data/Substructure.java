package nava.structurevis.data;

import java.io.Serializable;
import nava.utils.RNAFoldingTools;

/**
 * Class that represents a substructure
 *
 * @author Michael Golden
 */
public class Substructure implements Serializable {
    private static final long serialVersionUID = -9100660823526593325L;

    public int[] pairedSites;
    public String name = "";
    /**
     * String representing the nucleotide sequence of this structure.
     */
    public String sequence = "";
    public int startPosition = 0;
    public int length;
    public int index;

    public Substructure(int length) {
        this.length = length;
        this.pairedSites = new int[length];
    }
    
    public Substructure(int startPosition, int [] pairedSites)
    {
        this.startPosition = startPosition;
        this.length = pairedSites.length;
        this.pairedSites = pairedSites;
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
        final Substructure other = (Substructure) obj;
        if (this.startPosition != other.startPosition) {
            return false;
        }
        if (this.length != other.length) {
            return false;
        }
        return true;
    }
}
