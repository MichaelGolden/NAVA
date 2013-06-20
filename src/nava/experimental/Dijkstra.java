/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.experimental;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import nava.utils.RNAFoldingTools;

class Vertex implements Comparable<Vertex> {

    public final String name;
    public final int index;
    public Edge[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;

    public Vertex(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String toString() {
        return name;
    }

    public int compareTo(Vertex other) {
        return Double.compare(minDistance, other.minDistance);
    }
}

class Edge {

    public final Vertex target;
    public final double weight;

    public Edge(Vertex argTarget, double argWeight) {
        target = argTarget;
        weight = argWeight;
    }
}

public class Dijkstra {

    public static void computePaths(Vertex source) {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies) {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);
                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous) {
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }

    public static Vertex[] getPairedSitesGraph(int[] pairedSites) {
        Vertex[] vertices = new Vertex[pairedSites.length];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vertex(i + "", i);
        }

        for (int i = 0; i < vertices.length; i++) {
            ArrayList<Edge> edges = new ArrayList<>();
            if (i > 0) {
                edges.add(new Edge(vertices[i - 1], 1));
            }
            if (i + 1 < pairedSites.length) {
                edges.add(new Edge(vertices[i + 1], 1));
            }
            if (pairedSites[i] != 0) {
                edges.add(new Edge(vertices[pairedSites[i] - 1], 1));
            }
            vertices[i].adjacencies = new Edge[edges.size()];
            for (int j = 0; j < edges.size(); j++) {
                vertices[i].adjacencies[j] = edges.get(j);
            }

        }

        return vertices;
    }
    
    public static short [][] getDistanceMatrix(int [] pairedSites)
    {
        short [][] distanceMatrix = new short[pairedSites.length][pairedSites.length];
        Vertex[] vertices = Dijkstra.getPairedSitesGraph(pairedSites);
        
        
        for (int i = 0; i < pairedSites.length; i++) {
            for(Vertex v : vertices)
            {
                v.minDistance = Double.POSITIVE_INFINITY;
                v.previous = null;
            }
            
            computePaths(vertices[i]);
            for (Vertex v : vertices) {
                distanceMatrix[i][v.index] =  (short) v.minDistance;
            }
        }
        
        return distanceMatrix;        
    }
    
    public static double getDistance(int [] pairedSites, int i, int j)
    {
          Vertex[] vertices = Dijkstra.getPairedSitesGraph(pairedSites);
          computePaths(vertices[i]);
          return vertices[j].minDistance;
    }

    public static void main(String[] args) {
        //String dbs = "(((............)))";
        //012345678901234567
        String dbs = "...(((((((((((((........(((...((((((......))))))..))).)))))).)))))))(((.(((((((((((.....))))))))))).)))..........((((((.....))))))........((......)).((((((......(((((((((.....)))))))))......)))))).(((((..((((((((.(((..(((((((...))))))).)))))))))..))..))))).....((((((((((((..........))))...)))))))).((....)).....((((((.((((((.....))))))....))))))..((((....)))).....(((((((((((((((......(((...(((......))).)))..))))))))))..))))).((.((..(((((((......(((.(((.(((....)))))).)))..))))))).)).))...(((((...(((.(((((..((((((((((((.((..((....(((((((.(((....((.........)).))).((((((((((((.(((..((((((..(((((.((..((.((....(((...((..(((((((((..(((..((((.((...((((......))))......))....))))....))).(((((((...(((((.((.........)).))))).))))))).(.((((((((((((((....)).)).))).(((((((.((((((((.....)))).((((.((.(((...........))).))..))))..)))).)))))))((((((....((((((.(((((.((..((((((....((.((((((((((((...((((((.........))))))..))((((((.((((.((((((.((......)))))))).((((((.((.(((.((.(((((.((.(((.((((..((.((..((((((..((.((((....))))..((((((((((.(((....((((.......)))).....))).))))..(((((........)))))))))))))...)))))).......))...)).)))).)))....)).)).)))........)))))...)).)))))).((((..((((...)))).)))).))))...))))))..((((((((((......)))))))))).....)))))))))).))..))))))..))..))))).)).)))))))))).)))).))).)..)))))))))))..))).....)).)).)).)).))).)))))).)))..........)))))))).)))))))))))...))..)).((.((..(((..........)))..)).)).((((((((................)))))))).))))))))))))...))))).))).)))))..((((...((((((((((((.(((((((...(((((.((.((((....))))..)).)))))....)))))))...(((.((.(((.......))).)))))..(((((.(((((..((((..((((((((...(((.......)))(..(((((((.....)))))))..).(((((...(((...((((((((.((.....(((.(..((((((((((...((......((.(((.....))).)).......)).....))))))......))))).)))..)).))))))))..))).))))).))))).)))..))))..))...)))......))))).((((((.(((((.((((.......(((((((.((.......)).(((((.............)))))...(((((.......)))))))))))).......)))).)))))...(((((((.((((.......)))).)))))))))))))...((((............))))...)))))))).((.((((((((((((((..........)))))...))))))))).))...))))...))))....(((..(((...((.(((((((...(((..((((((........((..(((((.((......)).))))).))..)))))).))).)))))))..)).)))))).((((((((..((..(((.((((((.((.(((((.(((.((((((.(((.((..((((...((((((.......(((...(((((((.....((((((((..(((..((.((((((.((((....))))((...(((.((((((...((((((((((((..(((((......(((...((((.......))))....)))....))))).....)))))))))))).(((((((((((((.((((.((((((.((((.....((((.......((((.(((...(((((((((.((....)).(((....))).(((((....)))))...((..((((...(((..(((((((((.((..(((.((.((((......)))).))..))).)).....))))))))).)))((((.(((....(((.((((((...)))))).)))..((.....))...))).))))(((((.....((..((........))..)).....))))))))).)).((((...((((((((....(((.(((((.....(((((((.(((....(((((((.........)))))))......)))(((((((((......(((((((((...((((..........(((((.....((((((((.......((((....((((((((((.....(((.((((.....(((((((((......(((...((((((((.((((...(((.....))).))))))))))))..)))..))))..(((((((..(((..((((.((((((((((((.(((....)))...)))......))))))))).))))..)))...((..(((((...(((....)))(((.(((......))).)))....((.((((....)))).)).))))).....))...((((((((((.(((....))).)))))))))))))))))..))))).....)))))))((((((....................(((.((((.....))))....)))))))))..))))))))))....)))).((((..(((((.......)))))......)))).(((.((((((....)))))))))....))))))))..))))).........))))......)))))..))))...)).((((((((.((((((.(((((((((((.((.....)).)))))..((.(((((((((((((..((....))..((((((((((((((...(((((((((...((((..(((((...(((......)))...))))).((((..((.......))..))))(((((((..((((((((((((......)))..))))))....))).....)))))))...))))....)))))))))..((.((((((...((((((...............))))))...)))))).)).)))))..))).)))))).(((((((((.........)))...)))))).(((.(((((((((..(((...((((((...((((((((((..(((((((.((.((.(((((..(((...(((..((((((((..(((((((((.((((.((.(((((((((((((.((((((((.((..(((((................)))))..))((((((.((((((((((.((..((((..((((((..(((((((.((......)))))))))......))))))((((.(((((.(((..(((...........)))))))))))...)))).(((((((((((.((((.((((((..((((.((((((....((((....(((((((((.(((((((((.((..(((((.(((.......))).))))).(((.......)))......)).))).))))))))))))))).))))..))))))))))..))))))...)))).)))).....)))))))((((((((((((((((((..(((.(((..(((...))).(((.((((((.((((((.........)))))).)))).((((((...(((((((.((....))))))))))))))).)).)))((((.......))))....(((.((((((..((((((..(((((((...((((..(((.((..((((.((..(((..(((...........)))..)))..))...))))..)).)))........)))).....))))).........((..((((...))))....)).((((((..(((((((..(((((((.(((...))).)))).(((((((..........(((((((((((...(((.(((((......((((((((((........(((.........))).....))))))...)))).))))).)))..)).))))))))).....))))))))))..)))))))..)))))).))..))))))...)))))).))).((((((((((((((.((((....))))..(((.............))).(((..((((((.((....(((((((..((((.....)))).((((.....(((((.((......))..(((((((((.((((((.(((....(((.....(((((((..............(((((...(((((((((((.....((((((.....((((.(((.(((..((....)).)))...)))...))))...(((((((.((((((.((.....(((((((((...(((((((.((.(((.((((..((.(((((...((.....))((((((.(((((.((.......(((......))).(((((...(((.......)))...((((((((.......(((.....(((...(((.(((.....((((....))))..))).))).....))).)))...))))))))(((((.((((....(((((((((((...........))))..(((((.......))))).)))))))..))))))))).....((((((......(((((((((((((.(((.(((....((((((..(((..............)))..))))))......)))((..((((..........))))...))..(((.(((((((((.((((.((((((...)).))))..))))((...(((((((((((..((.((((........)))).))((((........)))).))).)))))))).....))((((((.......)).))))......)))))..)))).))).((((((((...((((..((.(((..((((...)))).....))).....))....))))..))))...))))..))).(((....))))))))))))))))..))))))))))).(((((....(((..(((((...((......))..((.(((((..((...((((.((....)).))))..)).))))).)).(((((((((.....))))))))).(((....((((((((((..(((..((....))..((((((((......((((.....((((((.......))))))......))))...)))))))).))))))))))))).......)))..))).))...)))((((((((((.......))))))..((((((....))))))...)))).)))))(((..(((.((..(((.......(((.(((.......))).)))..))))).))).)))..)).))))).)).))))..)))))...))..((((....)))))))).)))...))..))).((.....))....))))..))))))))).....))..)))))).(((..........)))(((....))).))))))).(((((((((.(((....))).))).))))))..(((((.....)))))..))))))...)))).....((..((((.((((.(((((((..(((............))).)))))...............))...)))).))))...))..)).)))))...(((...........)))))))).............)))))))))).....))).)))))).)))...))))))....)))))...))))..))))))).))))))))....))).....)))))...))))))))).))).)))..)))))))..)))))...)))))))))).)).))...))))))))))))))((((((((((....(((((.....)))))(((((..(((((.....((..(((((((((((((........((((..((.((((((((((.((.((.(((((((((((..(((((((((..((((((.((.((((((...((....((.((((...((.(((..((((((((((.((((((((((....))))))))))..))).(((((((((((.(((......)))...))))))))))))))))))...((((((.((.((((((.....))))))....))))))))....))).)).)))).....)).....))..)))))).)).)))))).))).))))((((((.......))))))..)).))))))))))).))...)).....)))))...))))).))))))........))))..)))))))))....))........))))).)))))...)))))))).))..))))))))..)))))))))).....((..(((((....)))))..))..))).....)).))))))))))))).))))..((((.(((((.(((.....))).)))))(((..((........)).))).(((((...(((.....((((((((((((((.....(((((((....((((((.......((((....)))).........))))))........))))))).......))))))....)))))))).)))....)))))..))))...((((.(((.(((..(((((.((((.((((...........)))).))))....((((..(((((......(((((....(((......(((........)))....))).)))))....)))))..)))).))))).(((((((.(((.(((((((((((((.....))))....(((((((...........((((((((.....((((....(((..((((((........))))))..)))))))...(((.((....((.(((((.((.(((((.((..((((((((((..((((((....))))))...))))))))))....)).....)))))..))))))).)).....)).))).(((.(((....((((.((.......((((((......(((((.....((((((((.((((((((..(((((........((((...((((....))))......)))).......)))))((((.............))))...(((((((((((((...(((.(((((...((((.....((((.(((...((......)).))).)))).)))).((.(((((((..((((...........)))))))).))).)))))))..((((......))))((....))((...............)).)))..))))))).(((((((((......((((.(((..(((((((((((.(((...(((.....)))....))))))))))))))..(((((....)).)))....((((((((((..((((((....(((((.(((.((((((..(((((..((.((((((((......(((((..(((((((...)))))..))..)))))....)))))))).))...(((((...........(((((.(((((..((((..........((((.((.......(((((((((..((((((((((.......)))))))...(((((...((((.((((..(((........((((.(((.((..(((((((.(((((((((..(((...)))..)))..)))))).)))....))))((((.....(((.(((((((((........)))))...))))........)))....))))..))))).)))).......))))))).....)))))))))...((......((((((...(((((.......(((.((((....)))).)))......)))))......(((((.....)))))))))))......))..((((...)))).))).))))))))).....)).)))).........))))....(((((((((......)))))))))..))))).)))))......)))))...))))))))))).((.....))((((.....)))).)))...(((.(((.(((.(((....))).))).........))).))).((..(((((...((((((((((...((((((((.............))))).(((((.((.........)).)))))....))).....))))...))))))....)))))..))...)))))...)))))).((((((((....)).))))))..(((((.((..(((((...((((((...(((.....(((.....)))...))).)))))).))))).)).)))))...)))))))))).(((((.(((...((((((...)))))).((..........)).((.(((((....(((((...((((.....)))).)))))...)))))...)).)))...)))))..))).)))).))))))))).))))))))))))))...(((.(((............))).)))...))))))))...)))))......))))))......)).)))).))).)))..((((......)))))))))))))))))))..))))))))).))).))))))).((..((.....))......)).((((...((((..........))))......)))).))).))))))).((((.........(((((..........)))))..............)))).))))..)))..)))))))).))))...)))))..(((.(((((((((.(((..((((((......(((..(((((((....((...((.(((.....))).)).))......)))))))..)))..))))))..)))...))))))))))))((........)).)).))))))))))..(((......)))....(((......)))(((....(((((((.........((((.(((((..((...))..)))))))))...))))))).))).))))))...)))..))))))))).))).)))))).....))))))).))...)))))))))))).))))))))....))))))).((.(((...(((((((((.....))))))))).))).))...))))))).))))))))))))))))((((...(((.......(((((((..((((((.(((.((((..................))))...))).))))))..)).))))).....))).)))).)))))))))))))...))).))))...))))((((((..(((((((((((.((.(((.....((....))....)))..)).)))))).)))))((((((((((((.(((.(((((((((.((.....)).)))..))))))..)))....)))))))))))).(((...))).))))))...))))..))))))))))...)))).((..((((((....((((((.....(((.((((((((((((.....))).)))))))))...)))........))))))....))))))..)).((((..((((...(((....((((((.(((((.(((.....(((((((.((.....(((((..((((....)))).)))))...))...)))))))..(((....)))...((.(((........((..(((((..(((........))))))))((((......))))))..))).))..(((((.....(((.((((...........(((((.....(((.(((((((....))))))))))))))))))))))....))))).)))...))))).))))))....))).)))).))))))).))))))...((......))....)))))).)))..)))))))).))..)))...))))))))..)))))))....)))..))))))...))))....))...))).))))))...))).))))).........((((........))))...))..)))))).))).))))))))))((((((((((((((((.......)))......)))))))))))))..(((((....))))).";
        int[] pairedSites = RNAFoldingTools.getPairedSitesFromDotBracketString(dbs);


        short [][] distanceMatrix = Dijkstra.getDistanceMatrix(pairedSites);
        
        /*
        Vertex[] vertices = Dijkstra.getPairedSitesGraph(pairedSites);
        
        
        for (int i = 0; i < pairedSites.length; i++) {
            for(Vertex v : vertices)
            {
                v.minDistance = Double.POSITIVE_INFINITY;
                v.previous = null;
            }
            
            computePaths(vertices[i]);
            System.out.println(i + "\t");
            // System.out.println("Distance to\t" + 0 + "\t" + vertices[0].minDistance);
            for (Vertex v : vertices) {
                //System.out.println(v);
                // System.out.println("Distance to " + v + ": " + v.minDistance);
                //List<Vertex> path = getShortestPathTo(v);
                // System.out.println("Path: " + path);
            }
        }*/

        /*
         *
         * Vertex v0 = new Vertex("Redvile"); Vertex v1 = new
         * Vertex("Blueville"); Vertex v2 = new Vertex("Greenville"); Vertex v3
         * = new Vertex("Orangeville"); Vertex v4 = new Vertex("Purpleville");
         *
         *
         * v0.adjacencies = new Edge[]{ new Edge(v1, 5), new Edge(v2, 10), new
         * Edge(v3, 8) }; v1.adjacencies = new Edge[]{ new Edge(v0, 5), new
         * Edge(v2, 3), new Edge(v4, 7) }; v2.adjacencies = new Edge[]{ new
         * Edge(v0, 10), new Edge(v1, 3) }; v3.adjacencies = new Edge[]{ new
         * Edge(v0, 8), new Edge(v4, 2) }; v4.adjacencies = new Edge[]{ new
         * Edge(v1, 7), new Edge(v3, 2) }; Vertex[] vertices = { v0, v1, v2, v3,
         * v4 }; computePaths(v0); for (Vertex v : vertices) {
         * System.out.println("Distance to " + v + ": " + v.minDistance);
         * List<Vertex> path = getShortestPathTo(v); System.out.println("Path: "
         * + path); }
         */
    }
}
