package minow.pwr.UI;

import minow.pwr.CenyTowarow;
import minow.pwr.CombineApiData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserInterface {

    private JFrame frame;
    private JTable table;
    private JComboBox<String> productFilter;
    private JTextField yearInput;
    private DefaultTableModel tableModel;
    private CombineApiData apiData;

    public UserInterface() {
        apiData = new CombineApiData();

        // Initialize frame
        frame = new JFrame("Średnie ceny produktów żywnościowych dla zadanego obszaru i roku");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Initialize table without the "Year" column
        tableModel = new DefaultTableModel(new String[]{"Cena (PLN)", "Obszar", " Produkt"}, 0);
        table = new JTable(tableModel);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());

        productFilter = new JComboBox<>();
        productFilter.addItem("All Products");
        productFilter.addActionListener(e -> filterTable());

        yearInput = new JTextField(6);
        yearInput.setText("2023");

        JButton fetchButton = new JButton("Fetch Data");
        fetchButton.addActionListener(e -> fetchData());

        controlPanel.add(new JLabel("Year:"));
        controlPanel.add(yearInput);
        controlPanel.add(fetchButton);
        controlPanel.add(new JLabel("Product:"));
        controlPanel.add(productFilter);

        // Add components to frame
        frame.add(controlPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        frame.setSize(800, 600);
        frame.setVisible(true);

        // Initial data fetch
        fetchData();
    }

    private void fetchData() {
        try {
            int year = Integer.parseInt(yearInput.getText());
            List<CenyTowarow> data = apiData.fetchDataForYear(year);

            // Populate table and product filter
            populateTable(data);
            populateProductFilter(data);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error fetching data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<CenyTowarow> data) {
        tableModel.setRowCount(0); // Clear existing rows
        for (CenyTowarow item : data) {
            // Exclude year from the row data
            tableModel.addRow(new Object[]{item.getValue(), item.getPosition1Name(), item.getPosition2Name()});
        }
    }

    private void populateProductFilter(List<CenyTowarow> data) {
        productFilter.removeAllItems();
        productFilter.addItem("All Products");
        for (String product : apiData.getUniqueProducts()) {
            productFilter.addItem(product);
        }
    }

    private void filterTable() {
        String selectedProduct = (String) productFilter.getSelectedItem();
        if (selectedProduct == null || selectedProduct.equals("All Products")) {
            table.clearSelection();
        } else {
            for (int i = 0; i < table.getRowCount(); i++) {
                String product = (String) table.getValueAt(i, 2); // Column index for Position2
                if (!selectedProduct.equals(product)) {
                    table.removeRowSelectionInterval(i, i);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserInterface::new);
    }
}