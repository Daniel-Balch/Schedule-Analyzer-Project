package edu.miamioh.team2;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class NonEditableModel extends DefaultTableModel {

    NonEditableModel(Vector<Vector<String>> data, Vector<String> columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}