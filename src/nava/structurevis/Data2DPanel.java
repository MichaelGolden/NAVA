/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nava.structurevis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import nava.data.types.Alignment;
import nava.data.types.DataSource;
import nava.data.types.Matrix;
import nava.data.types.TabularField;
import nava.structurevis.data.DataOverlay2D.MatrixRegion;
import nava.structurevis.data.*;
import nava.ui.MainFrame;
import nava.ui.ProjectModel;
import nava.utils.ColorGradient;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Data2DPanel extends javax.swing.JPanel implements KeyListener, ItemListener {

    DefaultComboBoxModel<Matrix> dataMatrixComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<TabularField> positionComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<Alignment> mappingSourceComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<DataTransform.TransformType> transformComboBoxModel;
    DataLegend dataLegend = new DataLegend();
    ProjectModel projectModel;
    DefaultFormatter numberFormatter;
    Matrix selectedMatrix = null;
    PersistentSparseMatrix matrixData = null;
    Matrix dataLoadedForMatrix = null;
    DataTransform selectedTransform = null;
    //DataSource1D dataSource1D = null;
    DataOverlay2D dataSource2D = null;
    DataPreviewTable previewTable = new DataPreviewTable();

    public Data2DPanel(ProjectModel projectModel) {
        initComponents();

        this.projectModel = projectModel;

        this.dataMatrixComboBox.setModel(dataMatrixComboBoxModel);
        this.dataMatrixComboBox.addItemListener(this);

        this.transformComboBoxModel = new DefaultComboBoxModel<>(DataTransform.TransformType.values());
        this.transformComboBox.setModel(transformComboBoxModel);
        this.transformComboBox.addItemListener(this);

        this.mappingSourceComboBox.setModel(mappingSourceComboBoxModel);
        this.mappingSourceComboBox.addItemListener(this);

        InputVerifier verifier = new InputVerifier() {

            public boolean verify(JComponent comp) {
                JTextField textField = (JTextField) comp;
                return Utils.isNumeric(textField.getText());
            }
        };
        this.dataMinField.setInputVerifier(verifier);
        this.dataMaxField.setInputVerifier(verifier);

        numberFormatter = new DefaultFormatter() {

            DecimalFormat df = new DecimalFormat("0.0####");
            DecimalFormat df2 = new DecimalFormat("0.00E0#");

            @Override
            public String valueToString(Object o) throws ParseException {
                if (o != null) {
                    double value = ((Double) o).doubleValue();
                    if (value == 0) {
                        return df.format(value);
                    } else if (value < 10000 && value > 0.0001) {
                        return df.format(value);
                    } else {
                        return df2.format(value);
                    }
                }
                return "";
            }

            @Override
            public Double stringToValue(String s)
                    throws ParseException {
                if (Utils.isNumeric(s)) {
                    return Double.parseDouble(s);
                } else {
                    return new Double(0);
                }
            }
        };

        DefaultFormatterFactory f1 = new DefaultFormatterFactory(numberFormatter, numberFormatter, numberFormatter, numberFormatter);;
        this.dataMinField.setFormatterFactory(f1);
        DefaultFormatterFactory f2 = new DefaultFormatterFactory(numberFormatter, numberFormatter, numberFormatter, numberFormatter);;
        this.dataMaxField.setFormatterFactory(f2);

        this.dataMinField.addKeyListener(this);
        this.dataMaxField.addKeyListener(this);

        this.dataLegendPanel.add(dataLegend, BorderLayout.CENTER);
        dataLegend.setLegend("Example", new DataTransform(0, 1, DataTransform.TransformType.LINEAR), new ColorGradient(Color.white, Color.red), new ColorGradient(Color.white, Color.red));

        this.naturalRadioButton.addItemListener(this);
        this.codonCheckButton.addItemListener(this);

        populateDataMatrixComboBox(projectModel.dataSources.getArrayListShallowCopy());
        populateMappingSourceComboBox(projectModel.dataSources.getArrayListShallowCopy());
        
        
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setReshowDelay(0);
        
        this.matrixHelpLabel.setToolTipText(Utils.plainTextToHtml(Utils.wrapText(MainFrame.resources.getString("matrixHelpText"), 60)));
        this.mappingHelpLabel.setToolTipText(Utils.plainTextToHtml(Utils.wrapText(MainFrame.resources.getString("mappingSourceHelpText"), 60)));
        this.transformHelpLabel.setToolTipText(Utils.plainTextToHtml(Utils.wrapText(MainFrame.resources.getString("transformHelpText"), 60)));
        this.rangeHelpLabel.setToolTipText(Utils.plainTextToHtml(Utils.wrapText(MainFrame.resources.getString("rangeHelpText"), 60)));
        this.positionHelpLabel.setToolTipText(Utils.plainTextToHtml(Utils.wrapText(MainFrame.resources.getString("positionHelpText"), 60)));        
    }

    public void populateDataMatrixComboBox(List<DataSource> dataSources) {
        dataMatrixComboBoxModel.removeAllElements();
        for (int i = 0; i < dataSources.size(); i++) {
            if (dataSources.get(i) instanceof Matrix) {
                dataMatrixComboBoxModel.addElement((Matrix) dataSources.get(i));
            }
        }
    }

    public void populateMappingSourceComboBox(List<DataSource> dataSources) {
        mappingSourceComboBoxModel.removeAllElements();
        for (int i = 0; i < dataSources.size(); i++) {
            if (dataSources.get(i) instanceof Alignment) {
                mappingSourceComboBoxModel.addElement((Alignment) dataSources.get(i));
            }
        }
    }
    ArrayList<Double> values = new ArrayList<>();

    public void updateLegend() {
        if (selectedMatrix != null) {
            if (!selectedMatrix.equals(dataLoadedForMatrix)) {
                try
                {
                    values = (ArrayList<Double>) selectedMatrix.getObject(MainFrame.dataSourceCache).getSample(10000);
                }
                catch(Exception ex)
                {
                    values = new ArrayList<Double>();
                }
                dataLoadedForMatrix = selectedMatrix;
            }

            ArrayList<Double> transformedValues = new ArrayList<>(values.size());
            if (selectedTransform != null) {
                transformedValues = Histogram.getTransformedValues((Double) dataMinField.getValue(), (Double) dataMaxField.getValue(), missingDataRadioButton.isSelected(), selectedTransform, values);
                dataLegend.setDataTransform(selectedTransform);
            }

            dataLegend.setHistogram(Histogram.getHistogram(0, 1, transformedValues, 8, 30));
        }
    }

    public void update() {
        this.selectedTransform = new DataTransform(dataMinField.getValue() == null ? 0 : (Double) dataMinField.getValue(), dataMaxField.getValue() == null ? 0 : (Double) dataMaxField.getValue(), (DataTransform.TransformType) transformComboBoxModel.getSelectedItem());
        this.updateLegend();

        MappingSource mappingSource = new MappingSource((Alignment) mappingSourceComboBox.getSelectedItem());
        MatrixRegion matrixRegion = MatrixRegion.FULL;
        if(useUpperMatrixRadioButton.isSelected())
        {
            matrixRegion = MatrixRegion.UPPER_TRIANGLE;
        }
        else
        if(useLowerMatrixRadioButton.isSelected())
        {
             matrixRegion = MatrixRegion.LOWER_TRIANGLE;
        }
        
        if(dataMatrixComboBox.getSelectedItem() != null)
        {
            dataSource2D = DataOverlay2D.getDataOverlay2D((Matrix) dataMatrixComboBox.getSelectedItem(), dataTitleField.getText(), naturalRadioButton.isSelected(), true, codonCheckButton.isSelected(), (Double) dataMinField.getValue(), (Double) dataMaxField.getValue(), missingDataRadioButton.isSelected(), selectedTransform, dataLegend.colorGradient, mappingSource, matrixRegion);
            dataSource2D.loadData();
        }
    }

    public void setDataSource2D(DataOverlay2D dataSource2D) {
        this.dataMatrixComboBoxModel.setSelectedItem(dataSource2D.dataMatrix);
        this.dataTitleField.setText(dataSource2D.title);
        this.naturalRadioButton.setSelected(dataSource2D.naturalPositions);
        this.codonCheckButton.setSelected(dataSource2D.codonPositions);
        this.dataMinField.setValue(dataSource2D.minValue);
        this.dataMaxField.setValue(dataSource2D.maxValue);
        this.missingDataRadioButton.setSelected(dataSource2D.excludeValuesOutOfRange);
        this.clampedRadioButton.setSelected(!dataSource2D.excludeValuesOutOfRange);
        this.transformComboBoxModel.setSelectedItem(dataSource2D.dataTransform.type);
        this.dataLegend.setLegend(dataSource2D.title, dataSource2D.dataTransform, dataSource2D.colorGradient, dataSource2D.defaultColorGradient);
        this.mappingSourceComboBoxModel.setSelectedItem(dataSource2D.mappingSequence == null ? null : dataSource2D.mappingSource.alignmentSource);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        positionGroup = new javax.swing.ButtonGroup();
        valueGroup = new javax.swing.ButtonGroup();
        firstPositionGroup = new javax.swing.ButtonGroup();
        matrixGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        naturalRadioButton = new javax.swing.JRadioButton();
        codonCheckButton = new javax.swing.JCheckBox();
        positionHelpLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dataMatrixComboBox = new javax.swing.JComboBox();
        dataTitleField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        missingDataRadioButton = new javax.swing.JRadioButton();
        clampedRadioButton = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        dataMaxField = new javax.swing.JFormattedTextField();
        dataMinField = new javax.swing.JFormattedTextField();
        restMinMaxButton = new javax.swing.JButton();
        rangeHelpLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        mappingSourceComboBox = new javax.swing.JComboBox();
        mappingHelpLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        transformComboBox = new javax.swing.JComboBox();
        dataLegendPanel = new javax.swing.JPanel();
        transformHelpLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        useEntireMatrixRadioButton = new javax.swing.JRadioButton();
        useUpperMatrixRadioButton = new javax.swing.JRadioButton();
        useLowerMatrixRadioButton = new javax.swing.JRadioButton();
        matrixHelpLabel = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("2. Specify how the values are positioned"));

        positionGroup.add(naturalRadioButton);
        naturalRadioButton.setSelected(true);
        naturalRadioButton.setText("Natural (1, 2, 3, ...)");

        codonCheckButton.setText("Positions are codon positions");

        positionHelpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        positionHelpLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/question-24x24.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(naturalRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(codonCheckButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(positionHelpLabel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(naturalRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(codonCheckButton)
                        .addGap(0, 10, Short.MAX_VALUE))
                    .addComponent(positionHelpLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("1. Select a data field"));

        jLabel1.setText("Data matrix");

        dataMatrixComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Title");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(dataMatrixComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(54, 54, 54)
                        .addComponent(dataTitleField)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(dataMatrixComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(dataTitleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("5. Specify the numeric range"));

        jLabel6.setText("Minimum value");

        jLabel7.setText("Maximum value");

        jLabel8.setText("How should values out of this range be treated? ");

        valueGroup.add(missingDataRadioButton);
        missingDataRadioButton.setSelected(true);
        missingDataRadioButton.setText("As missing data");
        missingDataRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                missingDataRadioButtonActionPerformed(evt);
            }
        });

        valueGroup.add(clampedRadioButton);
        clampedRadioButton.setText("Clamped");
        clampedRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clampedRadioButtonActionPerformed(evt);
            }
        });

        jLabel9.setText("e.g. 0.01 or 1e-2");

        dataMaxField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataMaxFieldActionPerformed(evt);
            }
        });

        dataMinField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataMinFieldActionPerformed(evt);
            }
        });

        restMinMaxButton.setText("Reset");
        restMinMaxButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restMinMaxButtonActionPerformed(evt);
            }
        });

        rangeHelpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rangeHelpLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/question-24x24.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dataMinField, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                                    .addComponent(dataMaxField))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(restMinMaxButton)))
                            .addComponent(jLabel8)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(missingDataRadioButton)
                        .addGap(18, 18, 18)
                        .addComponent(clampedRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rangeHelpLabel)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(dataMinField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(dataMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(restMinMaxButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(missingDataRadioButton)
                            .addComponent(clampedRadioButton)))
                    .addComponent(rangeHelpLabel)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("4. Map the data values to the structure"));

        jLabel4.setText("Mapping source");

        mappingSourceComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        mappingHelpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mappingHelpLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/question-24x24.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mappingSourceComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mappingHelpLabel)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(mappingSourceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(mappingHelpLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("6. Choose how the data values are displayed"));

        jLabel5.setText("Select a transform");

        transformComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        dataLegendPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dataLegendPanel.setLayout(new java.awt.BorderLayout());

        transformHelpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        transformHelpLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/question-24x24.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataLegendPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(transformComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(transformHelpLabel)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(transformComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(transformHelpLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataLegendPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("3. Select which parts of the matrix to use."));

        matrixGroup.add(useEntireMatrixRadioButton);
        useEntireMatrixRadioButton.setSelected(true);
        useEntireMatrixRadioButton.setText("Use entire matrix");

        matrixGroup.add(useUpperMatrixRadioButton);
        useUpperMatrixRadioButton.setText("Use only upper triangle");
        useUpperMatrixRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useUpperMatrixRadioButtonActionPerformed(evt);
            }
        });

        matrixGroup.add(useLowerMatrixRadioButton);
        useLowerMatrixRadioButton.setText("Use only lower triangle");

        matrixHelpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        matrixHelpLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/icons/question-24x24.png"))); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(useLowerMatrixRadioButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(useEntireMatrixRadioButton)
                            .addComponent(useUpperMatrixRadioButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(matrixHelpLabel)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(matrixHelpLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(useEntireMatrixRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(useUpperMatrixRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(useLowerMatrixRadioButton)))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel6.getAccessibleContext().setAccessibleName("3. Select which parts of the matrix to use");
    }// </editor-fold>//GEN-END:initComponents

    private void dataMinFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataMinFieldActionPerformed
        this.update();
    }//GEN-LAST:event_dataMinFieldActionPerformed

    private void dataMaxFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataMaxFieldActionPerformed
        this.update();
    }//GEN-LAST:event_dataMaxFieldActionPerformed

    private void restMinMaxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restMinMaxButtonActionPerformed
        if (selectedMatrix != null) {
            this.dataMinField.setValue(matrixData.getMinValue());
            this.dataMaxField.setValue(matrixData.getMaxValue());
            this.update();
        }
    }//GEN-LAST:event_restMinMaxButtonActionPerformed

    private void clampedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clampedRadioButtonActionPerformed
        this.update();
    }//GEN-LAST:event_clampedRadioButtonActionPerformed

    private void missingDataRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_missingDataRadioButtonActionPerformed
        this.update();
    }//GEN-LAST:event_missingDataRadioButtonActionPerformed

    private void useUpperMatrixRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useUpperMatrixRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_useUpperMatrixRadioButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton clampedRadioButton;
    private javax.swing.JCheckBox codonCheckButton;
    private javax.swing.JPanel dataLegendPanel;
    private javax.swing.JComboBox dataMatrixComboBox;
    private javax.swing.JFormattedTextField dataMaxField;
    private javax.swing.JFormattedTextField dataMinField;
    private javax.swing.JTextField dataTitleField;
    private javax.swing.ButtonGroup firstPositionGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel mappingHelpLabel;
    private javax.swing.JComboBox mappingSourceComboBox;
    private javax.swing.ButtonGroup matrixGroup;
    private javax.swing.JLabel matrixHelpLabel;
    private javax.swing.JRadioButton missingDataRadioButton;
    private javax.swing.JRadioButton naturalRadioButton;
    private javax.swing.ButtonGroup positionGroup;
    private javax.swing.JLabel positionHelpLabel;
    private javax.swing.JLabel rangeHelpLabel;
    private javax.swing.JButton restMinMaxButton;
    private javax.swing.JComboBox transformComboBox;
    private javax.swing.JLabel transformHelpLabel;
    private javax.swing.JRadioButton useEntireMatrixRadioButton;
    private javax.swing.JRadioButton useLowerMatrixRadioButton;
    private javax.swing.JRadioButton useUpperMatrixRadioButton;
    private javax.swing.ButtonGroup valueGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(this.dataMatrixComboBox)) {
            selectedMatrix = (Matrix) this.dataMatrixComboBox.getSelectedItem();
            matrixData = selectedMatrix.getObject(MainFrame.dataSourceCache);
            this.dataTitleField.setText(selectedMatrix.title);
            this.dataMinField.setValue(matrixData.getMinValue());
            this.dataMaxField.setValue(matrixData.getMaxValue());
        } else if (e.getSource().equals(this.naturalRadioButton)) {
            boolean enable = false;
        }
        update();
    }

    public DataOverlay2D getDataSource2D() {
        return dataSource2D;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        update();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        update();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        update();
    }
}
