import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class AdminBooksPanel extends JPanel {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfBookID, tfBookName, tfAuthor, tfGenre, searchField;
    private JLabel imageLabel = new JLabel();
    private boolean imageSelected = false;
    private String imageFilename = "";
    private JComboBox comboBox;

    public AdminBooksPanel(JFrame frame) {
        setLayout(null); // No layout manager
        setBackground(Color.white);

        // Text fields
        tfBookID = new JTextField(); tfBookID.setBounds(120, 10, 200, 30);
        tfBookID.setFont(new Font("Arial", Font.PLAIN, 16)); tfBookID.setForeground(Color.BLACK);
        tfBookID.setBackground(Color.white); tfBookID.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tfBookName = new JTextField(); tfBookName.setBounds(120, 50, 200, 30);
        tfBookName.setFont(new Font("Arial", Font.PLAIN, 16)); tfBookName.setForeground(Color.BLACK);
        tfBookName.setBackground(Color.white); tfBookName.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tfAuthor = new JTextField(); tfAuthor.setBounds(120, 90, 200, 30);
        tfAuthor.setFont(new Font("Arial", Font.PLAIN, 16)); tfAuthor.setForeground(Color.BLACK);
        tfAuthor.setBackground(Color.WHITE); tfAuthor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tfGenre = new JTextField();       tfGenre.setBounds(450, 10, 200, 30);
        tfGenre.setFont(new Font("Arial", Font.PLAIN, 16)); tfGenre.setForeground(Color.BLACK);
        tfGenre.setBackground(Color.WHITE); tfGenre.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Text field labels
        JLabel lblBookID = new JLabel("Book ID:"); lblBookID.setBounds(46, 10, 100, 25);
        lblBookID.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblBookName = new JLabel("Book Name:"); lblBookName.setBounds(20, 50, 100, 25);
        lblBookName.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblAuthor = new JLabel("Author:"); lblAuthor.setBounds(55, 90, 100, 25);
        lblAuthor.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblGenre = new JLabel("Genre:");    lblGenre.setBounds(390, 10, 100, 25);
        lblGenre.setFont(new Font("Arial", Font.BOLD, 16));

        // Book Cover Panel
        JPanel imagePanel = new JPanel();
        imagePanel.setBounds(675, 10, 150, 225);
        imagePanel.setBorder(BorderFactory.createTitledBorder("Book Cover"));
        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Search Bar
        JLabel searchLabel = new JLabel("Search Book ID, Book Name, or Author:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 16));
        searchLabel.setBounds(20, 185, 350, 20);
        add(searchLabel);
        searchField = new JTextField();
        searchField.setBounds(20, 210, 305, 30);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setForeground(Color.BLACK); searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Search bar filter function
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void search(String query) {
                query = query.toLowerCase();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0, 1, 2));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                search(searchField.getText());
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                search(searchField.getText());
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                search(searchField.getText());
            }
        });

        // Buttons
        JButton btnInsert = new JButton("Insert"); btnInsert.setBounds(850, 10, 100, 30);
        btnInsert.setFocusable(false);  btnInsert.setFont(new Font("Arial", Font.BOLD, 16));
        JButton btnUpdate = new JButton("Update"); btnUpdate.setBounds(850, 50, 100, 30);
        btnUpdate.setFocusable(false);  btnUpdate.setFont(new Font("Arial", Font.BOLD, 16));
        JButton btnDelete = new JButton("Delete"); btnDelete.setBounds(970, 10, 100, 30);
        btnDelete.setFocusable(false);  btnDelete.setFont(new Font("Arial", Font.BOLD, 16));
        JButton btnClear = new JButton("Clear");   btnClear.setBounds(970, 50, 100, 30);
        btnClear.setFocusable(false);  btnClear.setFont(new Font("Arial", Font.BOLD, 16));
        JButton btnImage = new JButton("Image"); btnImage.setBounds(905, 90, 100, 30);
        btnImage.setFocusable(false); btnImage.setFont(new Font("Arial", Font.BOLD, 16));

        //ComboBox for Book Type
        String[] booktype = {"Select Book Type", "Physical Book", "eBook"};
        comboBox = new JComboBox(booktype);
        comboBox.setBounds(450, 50, 160, 40);

        // Adding all components
        add(lblBookID);  add(tfBookID);
        add(lblBookName); add(tfBookName);
        add(lblAuthor); add(tfAuthor);
        add(lblGenre);    add(tfGenre);
        add(btnInsert);   add(btnUpdate);
        add(btnDelete);   add(btnClear);
        add(imagePanel);  add(searchField);
        add(btnImage);    add(comboBox);

        // Table
        model = new DefaultTableModel(new Object[]{"Book ID", "Book Name", "Author", "Genre", "Book Type", "Status", "Image"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 250, 1520, 330); // Set bounds for table
        add(scrollPane); // Add table to panel

        // Hiding the "Image" column
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        // Load existing books
        loadBooksFromFile();

        // Button listeners
        btnInsert.addActionListener(e -> insertBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear.addActionListener(e -> clearFields());
        btnImage.addActionListener(e -> insertImage());

        // Table listener for clicking a cell
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int modelRow = table.convertRowIndexToModel(row);
                    tfBookID.setText(model.getValueAt(modelRow, 0).toString());
                    tfBookName.setText(model.getValueAt(modelRow, 1).toString());
                    tfAuthor.setText(model.getValueAt(modelRow, 2).toString());
                    tfGenre.setText(model.getValueAt(modelRow, 3).toString());
                    comboBox.setSelectedItem(model.getValueAt(modelRow, 4).toString());

                    String filename = model.getValueAt(modelRow, 6).toString();
                    File imageFile = new File("bookimages", filename);

                    try {
                        Image img = ImageIO.read(imageFile);
                        Image scaledImg = img.getScaledInstance(
                                imageLabel.getWidth(),
                                imageLabel.getHeight(),
                                Image.SCALE_SMOOTH
                        );
                        imageLabel.setIcon(new ImageIcon(scaledImg));
                        imageLabel.setText(""); // Clear any previous text
                    } catch (Exception ex) {
                        imageLabel.setIcon(null);
                        imageLabel.setText("No book cover found.");
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
                    }
                }
            }
        });

        // Set preferred size
        setPreferredSize(new Dimension(600, 450));
    }
    private void insertBook() {
        if (fieldsEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!imageSelected) {
            JOptionPane.showMessageDialog(this, "Please select a book cover image.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate Book ID is an integer
        String bookIdText = tfBookID.getText().trim();
        try {
            Integer.parseInt(bookIdText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Book ID must be an integer.", "Invalid ID", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate Book ID
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equals(bookIdText)) {
                JOptionPane.showMessageDialog(this, "This Book ID is already taken.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Check for duplicate Book Name + Author combination
        String newBookName = tfBookName.getText().trim();
        String newAuthor = tfAuthor.getText().trim();
        for (int i = 0; i < model.getRowCount(); i++) {
            String existingName = model.getValueAt(i, 1).toString().trim();
            String existingAuthor = model.getValueAt(i, 2).toString().trim();
            if (existingName.equalsIgnoreCase(newBookName) && existingAuthor.equalsIgnoreCase(newAuthor)) {
                JOptionPane.showMessageDialog(this, "This book already exists (same name and author).", "Duplicate Book", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Add book
        model.addRow(new Object[]{
                bookIdText, newBookName, newAuthor, tfGenre.getText(),
                comboBox.getSelectedItem().toString(), "Available", imageFilename
        });

        saveBooksToFile();
        clearFields();
        imageLabel.setIcon(null); // Clear the image display
        imageSelected = false;    // Reset the image selection flag
    }

    private void updateBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check for empty fields
        if (fieldsEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate Book ID is an integer
        String newBookId = tfBookID.getText().trim();
        try {
            Integer.parseInt(newBookId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Book ID must be an integer.", "Invalid ID", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Only check for duplicate Book ID if it's changed
        String currentBookId = model.getValueAt(selectedRow, 0).toString();
        if (!newBookId.equals(currentBookId)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (i != selectedRow && model.getValueAt(i, 0).toString().equals(newBookId)) {
                    JOptionPane.showMessageDialog(this, "This Book ID is already taken.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        // Check for duplicate Book Name + Author combo
        String newBookName = tfBookName.getText().trim();
        String newAuthor = tfAuthor.getText().trim();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i == selectedRow) continue;
            String existingName = model.getValueAt(i, 1).toString().trim();
            String existingAuthor = model.getValueAt(i, 2).toString().trim();
            if (existingName.equalsIgnoreCase(newBookName) && existingAuthor.equalsIgnoreCase(newAuthor)) {
                JOptionPane.showMessageDialog(this, "This book already exists (same name and author).", "Duplicate Book", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // ✅ Keep existing image filename from hidden column
        String imageFilename = model.getValueAt(selectedRow, 6).toString();

        // Update the book details
        model.setValueAt(newBookId, selectedRow, 0);
        model.setValueAt(newBookName, selectedRow, 1);
        model.setValueAt(newAuthor, selectedRow, 2);
        model.setValueAt(tfGenre.getText(), selectedRow, 3);
        model.setValueAt(comboBox.getSelectedItem().toString(), selectedRow, 4);
        model.setValueAt(imageFilename, selectedRow, 6); // 🔒 preserve cover image

        saveBooksToFile();
    }

    private void deleteBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(selectedRow);
            saveBooksToFile();
        }
    }
    private void clearFields() {
        tfBookID.setText("");
        tfBookName.setText("");
        tfAuthor.setText("");
        tfGenre.setText("");
        comboBox.setSelectedIndex(0);
        searchField.setText("");
        table.clearSelection();
        imageLabel.setIcon(null);
        imageSelected = false;
    }
    private boolean fieldsEmpty() {
        return tfBookID.getText().isEmpty() || tfBookName.getText().isEmpty() ||
                tfAuthor.getText().isEmpty() || tfGenre.getText().isEmpty() ||
                comboBox.getSelectedItem() == null || comboBox.getSelectedItem().toString().equals("Select Book Type");
    }
    private void loadBooksFromFile() {
        model.setRowCount(0); // Clear existing data
        try {
            File file = new File("books.txt");
            if (!file.exists()) file.createNewFile();

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] data = line.split(",", -1); // Include empty strings if any
                    if (data.length == 7) {
                        model.addRow(data);
                    }
                }
            }
            scanner.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        private void saveBooksToFile() {
            try (FileWriter writer = new FileWriter("books.txt")) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        line.append(model.getValueAt(i, j));
                        if (j < model.getColumnCount() - 1) {
                            line.append(",");
                        }
                    }
                    writer.write(line.toString() + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private void insertImage(){
            JFileChooser fileChooser = new JFileChooser(new File("bookimages"));
            fileChooser.setDialogTitle("Choose a Book Cover");
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    Image img = ImageIO.read(selectedFile);
                    Image scaledImage = img.getScaledInstance(
                            imageLabel.getWidth(),
                            imageLabel.getHeight(),
                            Image.SCALE_SMOOTH
                    );
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                    imageLabel.setText(""); // Clear any previous "No book cover found." text

                    imageSelected = true;
                    imageFilename = selectedFile.getName(); // get the image's file name
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Failed to load image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}