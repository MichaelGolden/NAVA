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
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import nava.data.types.*;
import nava.structurevis.data.DataSource1D;
import nava.structurevis.data.DataTransform;
import nava.structurevis.data.Histogram;
import nava.structurevis.data.MappingSource;
import nava.ui.MainFrame;
import nava.ui.ProjectModel;
import nava.utils.ColorGradient;
import nava.utils.Utils;

/**
 *
 * @author Michael Golden <michaelgolden0@gmail.com>
 */
public class Data1DPanel extends javax.swing.JPanel implements KeyListener, ItemListener {

    DefaultComboBoxModel<Tabular> dataSourceComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<TabularField> dataFieldComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<TabularField> positionComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<Alignment> mappingSourceComboBoxModel = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<DataTransform.TransformType> transformComboBoxModel;
    DataLegend dataLegend = new DataLegend();
    ProjectModel projectModel;
    DefaultFormatter numberFormatter;
    TabularField selectedField = null;
    TabularField dataLoadedForField = null;
    DataTransform selectedTransform = null;
    DataSource1D dataSource1D = null;
    DataPreviewTable previewTable = new DataPreviewTable();
    

    public Data1DPanel(ProjectModel projectModel) {
        initComponents();

        this.projectModel = projectModel;

        this.dataSourceComboBox.setModel(dataSourceComboBoxModel);
        this.dataSourceComboBox.addItemListener(this);

        this.dataFieldComboBox.setModel(dataFieldComboBoxModel);
        this.dataFieldComboBox.addItemListener(this);

        this.positionComboBox.setModel(positionComboBoxModel);
        this.positionComboBox.addItemListener(this);

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
        this.fromFieldRadioButton.addItemListener(this);
        this.zeroPositionRadioButton.addItemListener(this);
        this.onePositionRadioButton.addItemListener(this);
        this.headerCheckButton.addItemListener(this);
        this.codonCheckButton.addItemListener(this);

        populateDataSourceComboBox(Collections.list(projectModel.dataSources.elements()));
        populateMappingSourceComboBox(Collections.list(projectModel.dataSources.elements()));
        previewPanel.add(previewTable, BorderLayout.CENTER);
    }

    public void populateDataSourceComboBox(List<DataSource> dataSources) {
        dataSourceComboBoxModel.removeAllElements();
        for (int i = 0; i < dataSources.size(); i++) {
            if (dataSources.get(i) instanceof Tabular) {
                dataSourceComboBoxModel.addElement((Tabular) dataSources.get(i));
            }
        }
    }

    public void populateDataFieldComboBox() {
        Tabular table = (Tabular) dataSourceComboBox.getSelectedItem();

        dataFieldComboBoxModel.removeAllElements();
        positionComboBoxModel.removeAllElements();
        ArrayList<TabularField> fields = table.getChildren();
        for (TabularField f : fields) {
            dataFieldComboBoxModel.addElement(f);
            positionComboBoxModel.addElement(f);
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
        if (selectedField != null) {
            if (!selectedField.equals(dataLoadedForField)) {
                values = (ArrayList<Double>) selectedField.getObject(MainFrame.dataSourceCache).getNumericValues();
                dataLoadedForField = selectedField;
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
        // (TabularField field, String title, TabularField positionField, boolean naturalPositions, boolean oneOffset, boolean codonPositions, double min, double max, boolean excludeValuesOutOfRange, DataTransform transform, ColorGradient colorGradient) {
        if(selectedField != null && positionComboBox.getSelectedItem() != null)
        {
            MappingSource mappingSource = new MappingSource((Alignment) mappingSourceComboBox.getSelectedItem());
       
            dataSource1D = DataSource1D.getDataSource1D((Tabular) dataSourceComboBox.getSelectedItem(), selectedField, dataTitleField.getText(), (TabularField) positionComboBox.getSelectedItem(), naturalRadioButton.isSelected(), onePositionRadioButton.isSelected(), headerCheckButton.isSelected() ? 1 : 0, codonCheckButton.isSelected(), (Double) dataMinField.getValue(), (Double) dataMaxField.getValue(), missingDataRadioButton.isSelected(), selectedTransform, dataLegend.colorGradient, mappingSource);
            dataSource1D.loadData();
            previewTable.tableDataModel.setDataSource1D(dataSource1D);
        }
    }
    
    public void setDataSource1D(DataSource1D dataSource1D)
    {
        this.dataSourceComboBoxModel.setSelectedItem(dataSource1D.dataTable);
        this.dataFieldComboBoxModel.setSelectedItem(dataSource1D.dataField);
        this.dataTitleField.setText(dataSource1D.title);
        this.positionComboBoxModel.setSelectedItem(dataSource1D.positionField);
        this.naturalRadioButton.setSelected(dataSource1D.naturalPositions);        
        this.onePositionRadioButton.setSelected(dataSource1D.oneOffset);
        this.headerCheckButton.setSelected(dataSource1D.dataOffset == 1);
        this.codonCheckButton.setSelected(dataSource1D.codonPositions);
        this.dataMinField.setValue(dataSource1D.minValue);
        this.dataMaxField.setValue(dataSource1D.maxValue);
        this.fromFieldRadioButton.setSelected(dataSource1D.positionField != null);
        this.missingDataRadioButton.setSelected(dataSource1D.excludeValuesOutOfRange);
        this.clampedRadioButton.setSelected(!dataSource1D.excludeValuesOutOfRange);
        this.transformComboBoxModel.setSelectedItem(dataSource1D.dataTransform.type);
        this.dataLegend.setLegend(dataSource1D.title, dataSource1D.dataTransform, dataSource1D.colorGradient, dataSource1D.defaultColorGradient);
        this.mappingSourceComboBoxModel.setSelectedItem(dataSource1D.mappingSequence == null ? null : dataSource1D.mappingSource.alignmentSource);        
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
        jPanel1 = new javax.swing.JPanel();
        fromFieldRadioButton = new javax.swing.JRadioButton();
        naturalRadioButton = new javax.swing.JRadioButton();
        codonCheckButton = new javax.swing.JCheckBox();
        positionComboBox = new javax.swing.JComboBox();
        zeroPositionRadioButton = new javax.swing.JRadioButton();
        onePositionRadioButton = new javax.swing.JRadioButton();
        firstPositionLabel = new javax.swing.JLabel();
        headerCheckButton = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dataSourceComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        dataFieldComboBox = new javax.swing.JComboBox();
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
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        mappingSourceComboBox = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        transformComboBox = new javax.swing.JComboBox();
        dataLegendPanel = new javax.swing.JPanel();
        previewPanel = new javax.swing.JPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("2. Specify how the values are positioned"));

        positionGroup.add(fromFieldRadioButton);
        fromFieldRadioButton.setText("From field");
        fromFieldRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromFieldRadioButtonActionPerformed(evt);
            }
        });

        positionGroup.add(naturalRadioButton);
        naturalRadioButton.setSelected(true);
        naturalRadioButton.setText("Natural (1, 2, 3, ...)");

        codonCheckButton.setText("Positions are codon positions");

        positionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        firstPositionGroup.add(zeroPositionRadioButton);
        zeroPositionRadioButton.setSelected(true);
        zeroPositionRadioButton.setText("Zero");
        zeroPositionRadioButton.setEnabled(false);

        firstPositionGroup.add(onePositionRadioButton);
        onePositionRadioButton.setText("One");
        onePositionRadioButton.setEnabled(false);

        firstPositionLabel.setText("Numbering starts at");
        firstPositionLabel.setEnabled(false);

        headerCheckButton.setSelected(true);
        headerCheckButton.setText("First line is header");
        headerCheckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                headerCheckButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(naturalRadioButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(fromFieldRadioButton)
                                .addGap(0, 56, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(firstPositionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(zeroPositionRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(onePositionRadioButton))
                            .addComponent(positionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(codonCheckButton)
                            .addComponent(headerCheckButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(naturalRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(positionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fromFieldRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstPositionLabel)
                    .addComponent(zeroPositionRadioButton)
                    .addComponent(onePositionRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(headerCheckButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codonCheckButton)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("1. Select a data field"));

        jLabel1.setText("Data table");

        dataSourceComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Data field");

        dataFieldComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Title");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataSourceComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dataFieldComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dataTitleField))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(dataSourceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dataFieldComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dataTitleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(" 4. Specify the numeric range"));

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
                                    .addComponent(dataMinField, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
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
                        .addComponent(clampedRadioButton)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(dataMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(restMinMaxButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(missingDataRadioButton)
                    .addComponent(clampedRadioButton)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("3. Map the data values to the structure"));

        jLabel4.setText("Mapping source");

        mappingSourceComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jScrollPane1.setBorder(null);
        jScrollPane1.setOpaque(false);

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("You should choose a mapping source (a sequence or an alignment) where the nucleotide positions (column positions) correspond exactly to the data values. This allows the data values to be automatically mapped against the structure.");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setBorder(null);
        jTextArea1.setOpaque(false);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mappingSourceComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(mappingSourceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("5. Choose how the data values are displayed"));

        jLabel5.setText("Select a transform");

        transformComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        dataLegendPanel.setLayout(new java.awt.BorderLayout());

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
                        .addComponent(transformComboBox, 0, 153, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(transformComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataLegendPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        previewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Data preview"));
        previewPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void dataMinFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataMinFieldActionPerformed
        this.update();
    }//GEN-LAST:event_dataMinFieldActionPerformed

    private void dataMaxFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dataMaxFieldActionPerformed
        this.update();
    }//GEN-LAST:event_dataMaxFieldActionPerformed

    private void restMinMaxButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restMinMaxButtonActionPerformed
        if (selectedField != null) {
            this.dataMinField.setValue(selectedField.getMinimum());
            this.dataMaxField.setValue(selectedField.getMaximum());
            this.update();
        }
    }//GEN-LAST:event_restMinMaxButtonActionPerformed

    private void clampedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clampedRadioButtonActionPerformed
        this.update();
    }//GEN-LAST:event_clampedRadioButtonActionPerformed

    private void missingDataRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_missingDataRadioButtonActionPerformed
        this.update();
    }//GEN-LAST:event_missingDataRadioButtonActionPerformed

    private void headerCheckButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_headerCheckButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_headerCheckButtonActionPerformed

    private void fromFieldRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromFieldRadioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fromFieldRadioButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton clampedRadioButton;
    private javax.swing.JCheckBox codonCheckButton;
    private javax.swing.JComboBox dataFieldComboBox;
    private javax.swing.JPanel dataLegendPanel;
    private javax.swing.JFormattedTextField dataMaxField;
    private javax.swing.JFormattedTextField dataMinField;
    private javax.swing.JComboBox dataSourceComboBox;
    private javax.swing.JTextField dataTitleField;
    private javax.swing.ButtonGroup firstPositionGroup;
    private javax.swing.JLabel firstPositionLabel;
    private javax.swing.JRadioButton fromFieldRadioButton;
    private javax.swing.JCheckBox headerCheckButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JComboBox mappingSourceComboBox;
    private javax.swing.JRadioButton missingDataRadioButton;
    private javax.swing.JRadioButton naturalRadioButton;
    private javax.swing.JRadioButton onePositionRadioButton;
    private javax.swing.JComboBox positionComboBox;
    private javax.swing.ButtonGroup positionGroup;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JButton restMinMaxButton;
    private javax.swing.JComboBox transformComboBox;
    private javax.swing.ButtonGroup valueGroup;
    private javax.swing.JRadioButton zeroPositionRadioButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(this.dataSourceComboBox)) {
            populateDataFieldComboBox();
        } else if (e.getSource().equals(this.dataFieldComboBox)) {
            TabularField field = (TabularField) dataFieldComboBox.getSelectedItem();
            if (field != null) {
                this.dataTitleField.setText(field.getTitle());
                this.dataMinField.setValue(field.getMinimum());
                this.dataMaxField.setValue(field.getMaximum());
                this.selectedField = field;
            }
        } else if (e.getSource().equals(this.naturalRadioButton) || e.getSource().equals(this.fromFieldRadioButton)) {
            boolean enable = false;
            if (fromFieldRadioButton.isSelected()) {
                enable = true;
            }

            this.firstPositionLabel.setEnabled(enable);
            this.zeroPositionRadioButton.setEnabled(enable);
            this.onePositionRadioButton.setEnabled(enable);
        }
        update();
    }
    
    public DataSource1D getDataSource1D()
    {
        return dataSource1D;
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
