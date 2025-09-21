import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class AdminBorrowPanel extends JPanel {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfUserID, tfBookID, searchField;
    private JButton btnBorrow, btnReturn, btnClear;
    private JLabel imageLabel = new JLabel();
    private JLabel picLabel = new JLabel();
    int screenWidth, screenHeight;

    private static final String BORROWED_FILE = "borrow.txt";
    private static final String USERS_FILE = "users.txt";
    private static final String BOOKS_FILE = "books.txt";
    private static final String HISTORY_FILE = "history.txt";

    private List<String[]> usersData;
    private List<String[]> booksData;

    public AdminBorrowPanel(JFrame frame) {
        this.frame = frame;
        tfUserID = new JTextField();
        tfUserID.setBounds(120, 20, 200, 30);
        tfUserID.setFont(new Font("Arial", Font.PLAIN, 16));
        tfUserID.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        tfBookID = new JTextField();
        tfBookID.setBounds(120, 60, 200, 30);
        tfBookID.setFont(new Font("Arial", Font.PLAIN, 16));
        tfBookID.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel lblUserID = new JLabel("User ID:");
        lblUserID.setBounds(50, 20, 100, 25);
        lblUserID.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblBookID = new JLabel("Book ID:");
        lblBookID.setBounds(50, 60, 100, 25);
        lblBookID.setFont(new Font("Arial", Font.BOLD, 16));

        btnBorrow = new JButton("Borrow");
        btnBorrow.setBounds(350, 20, 100, 30);
        btnBorrow.setFont(new Font("Arial", Font.BOLD, 16));
        btnBorrow.setFocusable(false);

        btnReturn = new JButton("Return");
        btnReturn.setBounds(350, 60, 100, 30);
        btnReturn.setFont(new Font("Arial", Font.BOLD, 16));
        btnReturn.setFocusable(false);

        btnClear = new JButton("Clear");
        btnClear.setBounds(460, 20, 100, 30);
        btnClear.setFont(new Font("Arial", Font.BOLD, 16));
        btnClear.setFocusable(false);

        JPanel imagePanel = new JPanel();
        imagePanel.setBounds(580, 10, 150, 225);
        imagePanel.setBorder(BorderFactory.createTitledBorder("Book Cover"));
        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        JPanel imagePanel2 = new JPanel();
        imagePanel2.setBounds(750, 10, 200, 200);
        imagePanel2.setBorder(BorderFactory.createTitledBorder("Profile Picture"));
        imagePanel2.setLayout(new BorderLayout());
        imagePanel2.add(picLabel, BorderLayout.CENTER);

        JLabel searchLabel = new JLabel("Search Book ID, User ID, Book Name, or Username:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 16));
        searchLabel.setBounds(20, 185, 400, 20);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(20, 210, 395, 30);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void search(String query) {
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0, 1, 2, 3));
            }
            public void insertUpdate(DocumentEvent e) { search(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { search(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { search(searchField.getText()); }
        });

        add(lblUserID); add(tfUserID);
        add(lblBookID); add(tfBookID);
        add(btnBorrow); add(btnReturn); add(btnClear);
        add(imagePanel); add(imagePanel2);
        add(searchField); add(searchLabel);

        model = new DefaultTableModel(new Object[]{"Book ID", "User ID", "Book Name", "Name of User", "Borrowed Date", "Return Date"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 250, 1520, 330);
        add(scrollPane);

        btnBorrow.addActionListener(e -> borrowBook());
        btnReturn.addActionListener(e -> returnBook());
        btnClear.addActionListener(e -> clearFields());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String bookID = table.getValueAt(row, 0).toString();
                    String userID = table.getValueAt(row, 1).toString();

                    // Set values to text fields
                    tfBookID.setText(bookID);
                    tfUserID.setText(userID);

                    // Load book image
                    for (String[] book : booksData) {
                        if (book[0].equals(bookID)) {
                            loadImageIntoLabel(imageLabel, book[6]);
                            break;
                        }
                    }
                    // Load user image
                    for (String[] user : usersData) {
                        if (user[2].equals(userID)) {
                            loadImageIntoLabel(picLabel, user[6]);
                            break;
                        }
                    }
                }
            }
        });

        loadUserData();
        loadBookData();
        loadBorrowedBooks();
        setLayout(null);
    }
    private void loadUserData() {
        usersData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                usersData.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBookData() {
        booksData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                booksData.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBorrowedBooks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BORROWED_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", 6);
                if (data.length == 6) {
                    model.addRow(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidUser(String userID) {
        return usersData.stream().anyMatch(u -> u[2].equals(userID));
    }

    private boolean isValidBook(String bookID) {
        return booksData.stream().anyMatch(b -> b[0].equals(bookID));
    }

    private void borrowBook() {
        String userID = tfUserID.getText().trim();
        String bookID = tfBookID.getText().trim();

        if (userID.isEmpty() || bookID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both User ID and Book ID.");
            return;
        }
        if (!isValidUser(userID) || !isValidBook(bookID)) {
            JOptionPane.showMessageDialog(this, "Invalid User ID or Book ID.");
            return;
        }

        for (String[] book : booksData) {
            if (book[0].equals(bookID) && !book[5].equalsIgnoreCase("Available")) {
                JOptionPane.showMessageDialog(this, "Book is already borrowed.");
                return;
            }
        }

        String userName = usersData.stream().filter(u -> u[2].equals(userID)).map(u -> u[3]).findFirst().orElse("Unknown");
        String bookName = booksData.stream().filter(b -> b[0].equals(bookID)).map(b -> b[1]).findFirst().orElse("Unknown");
        String bookType = booksData.stream().filter(b -> b[0].equals(bookID)).map(b -> b[4]).findFirst().orElse("Physical Book");
        String borrowDate = new Date().toString();
        String returnDate;

        if (bookType.equalsIgnoreCase("eBook")) {
            // For eBooks: Set return date 7 days after now
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, 7);
            returnDate = cal.getTime().toString();
        } else {
            //  For Physical Books: "Not Returned Yet"
            returnDate = "Not Returned Yet";
        }

        // Update table model
        model.addRow(new Object[]{bookID, userID, bookName, userName, borrowDate, returnDate});

        // Append to borrow.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BORROWED_FILE, true))) {
            writer.write(bookID + "," + userID + "," + bookName + "," + userName + "," + borrowDate + "," + returnDate + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update books.txt, mark as Unavailable
        try {
            List<String> updatedBooks = new ArrayList<>();
            for (String[] book : booksData) {
                if (book[0].equals(bookID)) {
                    book[5] = "Unavailable";
                }
                updatedBooks.add(String.join(",", book));
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
                for (String line : updatedBooks) writer.write(line + "\n");
            }
            loadBookData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, "Book borrowed successfully.");
    }

    private void returnBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a book to return.");
            return;
        }

        String bookID = model.getValueAt(selectedRow, 0).toString();
        String userID = model.getValueAt(selectedRow, 1).toString();
        String bookName = model.getValueAt(selectedRow, 2).toString();
        String userName = model.getValueAt(selectedRow, 3).toString();
        String borrowDate = model.getValueAt(selectedRow, 4).toString();
        String returnDate = new Date().toString();

        try {
            List<String> updatedBooks = new ArrayList<>();
            for (String[] book : booksData) {
                if (book[0].equals(bookID)) {
                    book[5] = "Available";
                }
                updatedBooks.add(String.join(",", book));
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
                for (String line : updatedBooks) writer.write(line + "\n");
            }
            loadBookData();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add to history.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            writer.write(bookID + "," + userID + "," + bookName + "," + userName + "," + borrowDate + "," + returnDate + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add to returnedbook.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("returnedbook.txt", true))) {
            writer.write(userID + "," + bookID + "," + bookName + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove from borrow.txt
        try {
            List<String> remaining = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(BORROWED_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (!(parts[0].equals(bookID) && parts[1].equals(userID))) {
                        remaining.add(line);
                    }
                }
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(BORROWED_FILE))) {
                for (String line : remaining) writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.removeRow(selectedRow);
        JOptionPane.showMessageDialog(this, "Book returned and moved to history.");
    }

    private void clearFields() {
        tfUserID.setText("");
        tfBookID.setText("");
        searchField.setText("");
        table.clearSelection();
        imageLabel.setIcon(null);
        picLabel.setIcon(null);
    }
    private void loadImageIntoLabel(JLabel label, String imageFileName) {
        try {
            String path = (label == imageLabel ? "bookimages/" : "profilepics/") + imageFileName;
            Image img = ImageIO.read(new File(path));
            Image scaledImg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImg));
        } catch (IOException e) {
            label.setIcon(null);
            System.err.println("Image not found: " + imageFileName);
        }
    }
}
