import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class AdminUsersPanel extends JPanel {
    private JLabel imageLabel = new JLabel();
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfId, tfName, tfUsername, tfPassword, tfEmail, tfRole, searchField;
    private boolean imageSelected = false;
    private String imageFilename = "";


    public AdminUsersPanel(JFrame frame) {
        setLayout(null); // No layout manager
        setBackground(Color.white);

        // Form panel components
        tfUsername = new JTextField(); tfUsername.setBounds(110, 10, 200, 30);
        tfUsername.setFont(new Font("Arial", Font.PLAIN, 14)); tfUsername.setForeground(Color.BLACK);
        tfUsername.setBackground(Color.white); tfUsername.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tfPassword = new JTextField(); tfPassword.setBounds(110, 50, 200, 30);
        tfPassword.setFont(new Font("Arial", Font.PLAIN, 14)); tfPassword.setForeground(Color.BLACK);
        tfPassword.setBackground(Color.WHITE); tfPassword.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tfId = new JTextField();       tfId.setBounds(110, 90, 200, 30);
        tfId.setFont(new Font("Arial", Font.PLAIN, 14)); tfId.setForeground(Color.BLACK);
        tfId.setBackground(Color.WHITE); tfId.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tfName = new JTextField();     tfName.setBounds(400, 10, 200, 30);
        tfName.setFont(new Font("Arial", Font.PLAIN, 14)); tfName.setForeground(Color.BLACK);
        tfName.setBackground(Color.WHITE); tfName.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tfEmail = new JTextField();    tfEmail.setBounds(400, 50, 200, 30);
        tfEmail.setFont(new Font("Arial", Font.PLAIN, 14)); tfEmail.setForeground(Color.BLACK);
        tfEmail.setBackground(Color.WHITE); tfEmail.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tfRole = new JTextField();     tfRole.setBounds(400, 90, 200, 30);
        tfRole.setFont(new Font("Arial", Font.PLAIN, 14)); tfRole.setForeground(Color.BLACK);
        tfRole.setBackground(Color.WHITE); tfRole.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel lblUsername = new JLabel("Username:"); lblUsername.setBounds(20, 10, 100, 25);
        lblUsername.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblPassword = new JLabel("Password:"); lblPassword.setBounds(20, 50, 100, 25);
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblId = new JLabel("User ID:");    lblId.setBounds(40, 90, 100, 25);
        lblId.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblName = new JLabel("Name:");         lblName.setBounds(340, 10, 100, 25);
        lblName.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblEmail = new JLabel("Email:");        lblEmail.setBounds(340, 50, 100, 25);
        lblEmail.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel lblRole = new JLabel("Role:");          lblRole.setBounds(346, 90, 100, 25);
        lblRole.setFont(new Font("Arial", Font.BOLD, 16));

        // Profile Picture Panel
        JPanel imagePanel = new JPanel();
        imagePanel.setBounds(630, 10, 200, 200);
        imagePanel.setBorder(BorderFactory.createTitledBorder("Profile Picture"));
        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // Search Bar
        JLabel searchLabel = new JLabel("Search User ID, Name, Username, or Email:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 16));
        searchLabel.setBounds(20, 160, 370, 20);
        add(searchLabel);
        searchField = new JTextField();
        searchField.setBounds(20, 185, 350, 30);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setForeground(Color.BLACK); searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(searchField);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void search(String query) {
                query = query.toLowerCase();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0, 1, 2, 4)); // Columns: ID, Name, Username, Email
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

        // Add all form components
        add(lblUsername); add(tfUsername);
        add(lblPassword); add(tfPassword);
        add(lblId);       add(tfId);
        add(lblName);     add(tfName);
        add(lblEmail);    add(tfEmail);
        add(lblRole);     add(tfRole);
        add(btnInsert);   add(btnUpdate);
        add(btnDelete);   add(btnClear);
        add(imagePanel);  add(btnImage);

        // Table
        model = new DefaultTableModel(new Object[]{"User ID", "Name", "Username", "Password", "Email", "Role", "Image"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 230, 1520, 350); // Set bounds for table
        add(scrollPane); // Add table to panel

        // Hiding the "Image" column
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        // Load existing users
        loadUsersFromFile();

        // Button listeners
        btnInsert.addActionListener(e -> insertUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearFields());
        btnImage.addActionListener(e -> insertImage());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int modelRow = table.convertRowIndexToModel(row);
                    tfId.setText(model.getValueAt(modelRow, 0).toString());
                    tfName.setText(model.getValueAt(modelRow, 1).toString());
                    tfUsername.setText(model.getValueAt(modelRow, 2).toString());
                    tfPassword.setText(model.getValueAt(modelRow, 3).toString());
                    tfEmail.setText(model.getValueAt(modelRow, 4).toString());
                    tfRole.setText(model.getValueAt(modelRow, 5).toString());

                    String filename = model.getValueAt(modelRow, 6).toString();
                    File imageFile = new File("profilepics", filename);

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
                        imageLabel.setText("No profile pic found.");
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
                    }
                }
            }
        });

        // Set preferred size so it can be seen properly
        setPreferredSize(new Dimension(600, 450));
    }

    private void insertUser() {
        if (fieldsEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!imageSelected) {
            JOptionPane.showMessageDialog(this, "Please select a profile picture.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ensure User ID is an integer
        String idText = tfId.getText().trim();
        try {
            Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "User ID must be an integer.", "Invalid ID", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Max username length check
        if (tfUsername.getText().trim().length() > 16) {
            JOptionPane.showMessageDialog(this, "Username should not exceed 16 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate Username and User ID
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 2).toString().equals(tfUsername.getText())) {
                JOptionPane.showMessageDialog(this, "This username is already taken.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (model.getValueAt(i, 0).toString().equals(idText)) {
                JOptionPane.showMessageDialog(this, "This User ID is already taken.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (model.getValueAt(i, 4).toString().equalsIgnoreCase(tfEmail.getText())) {
                JOptionPane.showMessageDialog(this, "This email is already registered.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (model.getValueAt(i, 1).toString().equalsIgnoreCase(tfName.getText()) &&
                    model.getValueAt(i, 4).toString().equalsIgnoreCase(tfEmail.getText())) {
                JOptionPane.showMessageDialog(this, "A user with this name and email already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (tfPassword.getText().length() < 8) {
            JOptionPane.showMessageDialog(this, "Password should be at least 8 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tfPassword.getText().length() > 16) {
            JOptionPane.showMessageDialog(this, "Password should not exceed 16 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        model.addRow(new Object[]{
                tfId.getText(), tfName.getText(), tfUsername.getText(),
                tfPassword.getText(), tfEmail.getText(), tfRole.getText(), imageFilename
        });

        saveUsersToFile();
        clearFields();
    }

    private void updateUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fieldsEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idText = tfId.getText().trim();
        try {
            Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "User ID must be an integer.", "Invalid ID", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newUsername = tfUsername.getText().trim();
        String newEmail = tfEmail.getText().trim();

        // Max username length check
        if (newUsername.length() > 16) {
            JOptionPane.showMessageDialog(this, "Username should not exceed 16 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate Username (excluding current row)
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i != selectedRow && model.getValueAt(i, 2).toString().equalsIgnoreCase(newUsername)) {
                JOptionPane.showMessageDialog(this, "This username is already taken.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Check for duplicate User ID (excluding current row)
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i != selectedRow && model.getValueAt(i, 0).toString().equals(idText)) {
                JOptionPane.showMessageDialog(this, "This User ID is already taken.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Check for duplicate Email (excluding current row)
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i != selectedRow && model.getValueAt(i, 4).toString().equalsIgnoreCase(newEmail)) {
                JOptionPane.showMessageDialog(this, "This email is already registered.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (tfPassword.getText().length() < 8) {
            JOptionPane.showMessageDialog(this, "Password should be at least 8 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tfPassword.getText().length() > 16) {
            JOptionPane.showMessageDialog(this, "Password should not exceed 16 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        model.setValueAt(tfId.getText(), selectedRow, 0);
        model.setValueAt(tfName.getText(), selectedRow, 1);
        model.setValueAt(tfUsername.getText(), selectedRow, 2);
        model.setValueAt(tfPassword.getText(), selectedRow, 3);
        model.setValueAt(tfEmail.getText(), selectedRow, 4);
        model.setValueAt(tfRole.getText(), selectedRow, 5);

        saveUsersToFile();
    }

    private void deleteUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a User to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(selectedRow);
            saveUsersToFile();
        }
    }
    private void clearFields() {
        tfId.setText("");
        tfName.setText("");
        tfUsername.setText("");
        tfPassword.setText("");
        tfEmail.setText("");
        tfRole.setText("");
        searchField.setText("");
        table.clearSelection();
        imageLabel.setIcon(null);
        imageSelected = false;
    }

    private boolean fieldsEmpty() {
        return tfUsername.getText().isEmpty() || tfPassword.getText().isEmpty() || tfId.getText().isEmpty()
                || tfName.getText().isEmpty() || tfEmail.getText().isEmpty() || tfRole.getText().isEmpty();
    }

    private void loadUsersFromFile() {
        model.setRowCount(0);
        try {
            File file = new File("users.txt");
            if (!file.exists()) file.createNewFile();

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] data = line.split(",", -1); // Preserve empty values
                    if (data.length == 7) {
                        // Reordering to match model: {Student ID, Name, Username, Password, Email, Role}
                        model.addRow(new Object[]{data[2], data[3], data[0], data[1], data[4], data[5], data[6]});
                    }
                }
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveUsersToFile() {
        try (FileWriter writer = new FileWriter("users.txt")) {
            for (int i = 0; i < model.getRowCount(); i++) {
                StringBuilder line = new StringBuilder();
                line.append(model.getValueAt(i, 2)).append(","); // Username
                line.append(model.getValueAt(i, 3)).append(","); // Password
                line.append(model.getValueAt(i, 0)).append(","); // Student ID
                line.append(model.getValueAt(i, 1)).append(","); // Name
                line.append(model.getValueAt(i, 4)).append(","); // Email
                line.append(model.getValueAt(i, 5)).append(","); // Role
                line.append(model.getValueAt(i, 6));             // Image (no trailing comma)
                writer.write(line.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void insertImage(){
        JFileChooser fileChooser = new JFileChooser(new File("profilepics"));
        fileChooser.setDialogTitle("Choose a Profile Picture");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Image img = ImageIO.read(selectedFile);
                Image scaledImage = img.getScaledInstance(
                        imageLabel.getWidth(),
                        imageLabel.getHeight(),
                        Image.SCALE_SMOOTH
                );
                imageLabel.setIcon(new ImageIcon(scaledImage));
                imageLabel.setText(""); // Clear any previous "No profile pics found." text

                imageSelected = true;
                imageFilename = selectedFile.getName(); // get the image's file name
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}