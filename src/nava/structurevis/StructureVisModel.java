/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import nava.structurevis.data.*;
import nava.structurevis.navigator.DataOverlayTreeModel;
import nava.ui.ProjectController;
import nava.utils.Mapping;
import nava.utils.Pair;
import nava.utils.SafeListModel;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class StructureVisModel implements Serializable {
    public SafeListModel<StructureSource> structureSources = new SafeListModel<>();
    public SafeListModel<DataOverlay1D> structureVisDataOverlays1D = new SafeListModel<>();
    public SafeListModel<DataOverlay2D> structureVisDataOverlays2D = new SafeListModel<>();
    public SafeListModel<AnnotationSource> annotationSources = new SafeListModel<>();
    public SafeListModel<NucleotideComposition> nucleotideSources = new SafeListModel<>();
    
    public DataOverlayTreeModel overlayNavigatorTreeModel;
    
   // protected transient EventListenerList listeners = new EventListenerList();
    
    public SubstructureModel substructureModel = null;
    Hashtable<Pair<MappingSource, MappingSource>, Mapping> mappings = new Hashtable<>();
    //public File structureVisModelFile = null;
    
    public void saveStructureVisModel(File outFile) {      
        ArrayList<TreeModelListener> treeListenersList = new ArrayList<>();
        TreeModelListener[] overlayTreeListeners = overlayNavigatorTreeModel.getTreeModelListeners();
        for (int i = 0; i < overlayTreeListeners.length; i++) {
            treeListenersList.add(overlayTreeListeners[i]);
            overlayNavigatorTreeModel.removeTreeModelListener(overlayTreeListeners[i]);
        }
        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(this);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // re-add the listeners, this is only necessary if the application stays open
        
        System.out.println("StructureVis project saved");
    }
    
     public static StructureVisModel loadProject(File inFile, StructureVisController structureVisController) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(inFile));

        StructureVisModel ret = (StructureVisModel) in.readObject();
        ret.initialise(structureVisController);
        in.close();
        

        System.out.println("StructureVis project loaded");
        return ret;
    }
     
     public String getStructureVisModelPathString(StructureVisController structureVisController)
     {
         return  structureVisController.getWorkingDirectory()+File.separator+"structurevis.model";
     }
     
    
     public void initialise(StructureVisController structureVisController)
     {         
        substructureModel.initialise(structureVisController);
        if(overlayNavigatorTreeModel == null)
        {            
            overlayNavigatorTreeModel = new DataOverlayTreeModel(new DefaultMutableTreeNode(),structureVisController);
        }
        structureVisController.addView(overlayNavigatorTreeModel);  
        structureVisDataOverlays1D.addListDataListener(structureVisController);
        structureVisDataOverlays2D.addListDataListener(structureVisController);
        nucleotideSources.addListDataListener(structureVisController);
        structureSources.addListDataListener(structureVisController);
     }
}
