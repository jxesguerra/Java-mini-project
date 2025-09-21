import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import javax.swing.table.TableRowSorter;

public class User extends Component {
    JFrame frame;
    JPanel whitePanel, bluePanel;
    JPanel historyPanel, borrowpanel, accountpanel;
    private JTable table;
    private DefaultTableModel model;
    JComboBox filter1;
    private DefaultTableModel requestModel;
    private JTable requestTable;
    private String currentUserId;
    private JLabel profileImageLabel;

    // Account panel components
    private JTextField tfUsername, tfId, tfName, tfEmail, tfRole;
    public User(String userId) {
        this.currentUserId = userId;
        initializeUI();
        checkRejectedRequests();
        //notif();
        checkReturnedBooks();
        checkAutoReturnedEBooks();
        checkEBook1DayWarnings();
    }
    private void initializeUI() {
        ImageIcon borrow = new ImageIcon("panelimages/BORROW.jpg");
        ImageIcon acc = new ImageIcon("panelimages/ACC.jpg");
        ImageIcon history = new ImageIcon("panelimages/PENDING.jpg");
        ImageIcon home = new ImageIcon("panelimages/home.png");

        Font font2 = new Font("Arial", Font.BOLD, 15);
        Font font1 = new Font("Arial", Font.BOLD, 20);

        frame = new JFrame("User Dashboard - " + currentUserId);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        JPanel ribbon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.decode("#0096FF"));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        ribbon.setBounds(0, 0, width, 80);
        ribbon.setLayout(null);

        ImageIcon search = new ImageIcon("panelimages/search.png");
        JLabel icon = new JLabel(search);
        icon.setBounds(15, 5, 70, 70);

        JTextField searchbar = new JTextField("Search");
        searchbar.setBounds(90, 15, 400, 50);
        searchbar.setFont(new Font("Arial", Font.PLAIN, 20));
        searchbar.setForeground(Color.GRAY);
        searchbar.setBackground(Color.WHITE);
        searchbar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        searchbar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (searchbar.getText().isEmpty()) {
                    searchbar.setForeground(Color.GRAY);
                    searchbar.setText("Search");
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                if (searchbar.getText().equals("Search")) {
                    searchbar.setText("");
                    searchbar.setForeground(Color.BLACK);
                    searchbar.setFocusable(true);
                }
            }
        });
        bluePanel = new JPanel();
        bluePanel.setBounds(0, 55, width, 340);
        bluePanel.setBackground(Color.blue);
        bluePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        bluePanel.setVisible(true);
        bluePanel.setLayout(null);

        whitePanel = new JPanel();
        whitePanel.setBounds(0, 80, width, height - 80);
        whitePanel.setBackground(Color.white);
        whitePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        whitePanel.setVisible(true);
        whitePanel.setLayout(null);

        // Initialize request model and table
        requestModel = new DefaultTableModel(new Object[]{"Name of Book", "GENRE", "Author", "Date of REQUEST"}, 0);
        requestTable = new JTable(requestModel);

        // Pass requestModel and requestTable to UserNewBook
        new UserNewBook(whitePanel, requestModel, requestTable, currentUserId);

        historyPanel = new JPanel();
        historyPanel.setBounds(0, 0, width, height);
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setVisible(false);

        borrowpanel = new JPanel();
        borrowpanel.setBounds(0, 0, width, height);
        borrowpanel.setBackground(Color.WHITE);
        borrowpanel.setVisible(false);

        accountpanel = createAccountPanel();
        accountpanel.setBounds(0, 80, width, height - 80);
        accountpanel.setBackground(Color.WHITE);
        accountpanel.setVisible(false);

        Font font3 = new Font("Arial", 1, 30);
        JLabel newbooks = new JLabel("New Books");
        newbooks.setForeground(Color.black);
        newbooks.setFont(font3);
        newbooks.setBounds(0, -75, 300, 200);

        JButton homeButton = new JButton("Home", home);
        homeButton.setBounds(620, 3, 300, 70);
        homeButton.setFont(font1);
        homeButton.setContentAreaFilled(false);
        homeButton.setBorderPainted(false);
        homeButton.setForeground(Color.BLACK);

        JButton historyButton = new JButton("Return History", history);
        historyButton.setFont(font1);
        historyButton.setBounds(840, 3, 300, 70);
        historyButton.setContentAreaFilled(false);
        historyButton.setBorderPainted(false);
        historyButton.setForeground(Color.BLACK);

        JButton borrowButton = new JButton("Borrow", borrow);
        borrowButton.setFont(font1);
        borrowButton.setBounds(1060, 3, 300, 70);
        borrowButton.setContentAreaFilled(false);
        borrowButton.setBorderPainted(false);
        borrowButton.setForeground(Color.BLACK);

        JButton accountButton = new JButton("Account", acc);
        accountButton.setFont(font1);
        accountButton.setBounds(1250, 3, 300, 70);
        accountButton.setContentAreaFilled(false);
        accountButton.setBorderPainted(false);
        accountButton.setForeground(Color.BLACK);

        homeButton.addActionListener(e -> {
            whitePanel.setVisible(true);
            historyPanel.setVisible(false);
            borrowpanel.setVisible(false);
            accountpanel.setVisible(false);
        });
        historyButton.addActionListener(e -> {
            whitePanel.setVisible(false);
            historyPanel.setVisible(true);
            borrowpanel.setVisible(false);
            accountpanel.setVisible(false);
            historypanel();
        });

        borrowButton.addActionListener(e -> {
            whitePanel.setVisible(false);
            historyPanel.setVisible(false);
            borrowpanel.setVisible(true);
            accountpanel.setVisible(false);
            initializeBorrowPanel();
        });

        accountButton.addActionListener(e -> {
            if (!accountpanel.isVisible()) {
                if (!loadUserData(currentUserId)) {
                    JOptionPane.showMessageDialog(frame, "Failed to load user data.");
                    return;
                }
            }
            whitePanel.setVisible(false);
            historyPanel.setVisible(false);
            borrowpanel.setVisible(false);
            accountpanel.setVisible(true);
        });

        JLabel list = new JLabel("List of Books");
        list.setFont(font1);
        list.setBounds(10,400, 200,50);

        model = new DefaultTableModel(new Object[]{"Book ID", "Book Name", "Author", "Genre", "Book Type", "Status", "Image"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(0, 450, width , 230);
        whitePanel.add(scrollPane);
        ImageIcon filter = new ImageIcon("panelimages/fil.png");
        JLabel d = new JLabel(filter);
        d.setBounds(1300, 400, 40,50);

        String F [] = {"All","Physical Books","eBook"};
        filter1 = new JComboBox(F);
        whitePanel.add(filter1);
        filter1.setBounds(1350,412,150,20);

        filter1.addActionListener(e -> {
            String selected = filter1.getSelectedItem().toString();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);

            if (selected.equals("Physical Books")) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)Physical", 4));
            } else if (selected.equals("eBook")) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)e.?book", 4));
            } else {
                sorter.setRowFilter(null);
            }
        });

        searchbar.getDocument().addDocumentListener(new DocumentListener() {
            private void search(String query) {
                query = query.toLowerCase();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0, 1, 2));
            }
            public void insertUpdate(DocumentEvent e) {
                search(searchbar.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                search(searchbar.getText());
            }
            public void changedUpdate(DocumentEvent e) {
                search(searchbar.getText());
            }
        });

        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        loadBooksFromFile();

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String id = table.getValueAt(row, 0).toString();
                    String name = table.getValueAt(row, 1).toString();
                    String author = table.getValueAt(row, 2).toString();
                    String genre = table.getValueAt(row, 3).toString();
                    String type = table.getValueAt(row, 4).toString();
                    String status = table.getValueAt(row, 5).toString();
                    String filename = table.getValueAt(row, 6).toString();

                    ImageIcon icon;
                    try {
                        File imageFile = new File("bookimages", filename);
                        if (imageFile.exists()) {
                            Image img = ImageIO.read(imageFile);
                            Image scaled = img.getScaledInstance(220, 280, Image.SCALE_SMOOTH);
                            icon = new ImageIcon(scaled);
                        } else {
                            icon = new ImageIcon(new BufferedImage(220, 280, BufferedImage.TYPE_INT_RGB));
                        }
                    } catch (IOException ex) {
                        icon = new ImageIcon(new BufferedImage(220, 280, BufferedImage.TYPE_INT_RGB));
                    }

                    JFrame description = new JFrame("Book Details");
                    description.setSize(550, 450);
                    description.setLayout(null);

                    JButton Borrow = new JButton("Borrow");
                    Borrow.setBounds(410,320,100,40);
                    Borrow.setFocusable(false);

                    // Add action listener to handle borrowing
                    Borrow.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            boolean isAvailable = false;
                            boolean isEBook = false;
                            String bookName = "";
                            String userFullName = "";
                            String borrowDate = new Date().toString();

                            // ✅ Check if user already borrowed this book
                            try {
                                File borrowFile = new File("borrow.txt");
                                if (borrowFile.exists()) {
                                    List<String> borrowedLines = java.nio.file.Files.readAllLines(borrowFile.toPath());
                                    for (String line : borrowedLines) {
                                        String[] parts = line.split(",", -1);
                                        if (parts.length >= 2 && parts[0].equals(id) && parts[1].equals(currentUserId)) {
                                            JOptionPane.showMessageDialog(description,
                                                    "You already have this book borrowed.",
                                                    "Already Borrowed",
                                                    JOptionPane.WARNING_MESSAGE);
                                            return;
                                        }
                                    }
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(description,
                                        "Error checking borrowed books.",
                                        "Error",
                                        JOptionPane.WARNING_MESSAGE);
                                return;
                            }

                            // ✅ Read book details from books.txt
                            try {
                                File file = new File("books.txt");
                                List<String> lines = java.nio.file.Files.readAllLines(file.toPath());

                                for (int i = 0; i < lines.size(); i++) {
                                    String[] parts = lines.get(i).split(",", -1);
                                    if (parts.length >= 6 && parts[0].equals(id)) {
                                        bookName = parts[1];
                                        isEBook = parts[4].equalsIgnoreCase("eBook");
                                        if (parts[5].equalsIgnoreCase("Available")) {
                                            isAvailable = true;

                                            if (isEBook) {
                                                // ✅ For eBooks: Skip requests and write directly to borrow.txt
                                                userFullName = getUserFullName(currentUserId);

                                                // Calculate return date (7 days after borrowDate)
                                                Calendar cal = Calendar.getInstance();
                                                cal.setTime(new Date());
                                                cal.add(Calendar.DAY_OF_MONTH, 7);
                                                String returnDate = cal.getTime().toString();

                                                // Write to borrow.txt
                                                try (BufferedWriter writer = new BufferedWriter(new FileWriter("borrow.txt", true))) {
                                                    writer.write(id + "," + currentUserId + "," + bookName + "," + userFullName + "," + borrowDate + "," + returnDate + "\n");
                                                }

                                                // Update books.txt (mark as Unavailable)
                                                parts[5] = "Unavailable";
                                                lines.set(i, String.join(",", parts));
                                                Files.write(file.toPath(), lines);

                                                // ✅ Update the JTable in real time
                                                model.setValueAt("Unavailable", row, 5);

                                                JLabel logol = new JLabel(new ImageIcon("panelimages/co.png"));
                                                JLabel message = new JLabel("Book successfully borrowed.");

                                                JPanel panel = new JPanel(new BorderLayout(10, 10));
                                                panel.add(logol, BorderLayout.WEST);
                                                panel.add(message, BorderLayout.CENTER);

                                                JOptionPane.showMessageDialog(description, panel, "Success", JOptionPane.PLAIN_MESSAGE);

                                                description.dispose();
                                                updateRequestTable();
                                                return;
                                            }
                                        }
                                        break;
                                    }
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(description,
                                        "Error reading book availability.",
                                        "Error",
                                        JOptionPane.WARNING_MESSAGE);
                                return;
                            }

                            if (isAvailable) {
                                // ✅ Check if user already requested this book
                                try {
                                    File requestFile = new File("requests.txt");
                                    if (requestFile.exists()) {
                                        List<String> requestLines = java.nio.file.Files.readAllLines(requestFile.toPath());
                                        for (String line : requestLines) {
                                            String[] parts = line.split(",", -1);
                                            if (parts.length >= 2 && parts[0].equals(currentUserId) && parts[1].equals(id)) {
                                                JOptionPane.showMessageDialog(description,
                                                        "You already requested this book. Please wait for approval.",
                                                        "Request Pending",
                                                        JOptionPane.WARNING_MESSAGE);
                                                return; // Exit early
                                            }
                                        }
                                    }
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(description,
                                            "Error checking existing requests.",
                                            "Error",
                                            JOptionPane.WARNING_MESSAGE);
                                    return;
                                }

                                // ✅ Proceed with normal request (physical books)
                                if (addToBookRequests(currentUserId, id)) {
                                    JLabel logol = new JLabel(new ImageIcon("panelimages/wait.png"));
                                    JLabel line3 = new JLabel("      Book request submitted for approval ");
                                    JPanel textPanel1 = new JPanel();
                                    textPanel1.setLayout(new BoxLayout(textPanel1, BoxLayout.Y_AXIS));
                                    textPanel1.add(line3);
                                    textPanel1.setBounds(50, 200, 100, 100);

                                    JPanel adminl = new JPanel();
                                    adminl.setLayout(new BorderLayout());
                                    adminl.add(logol, BorderLayout.CENTER);
                                    adminl.add(textPanel1, BorderLayout.AFTER_LAST_LINE);
                                    JOptionPane.showMessageDialog(null, adminl, "Request Sent", JOptionPane.PLAIN_MESSAGE);

                                    description.dispose();
                                    updateRequestTable();
                                } else {
                                    JOptionPane.showMessageDialog(description,
                                            "Failed to submit request",
                                            "Error",
                                            JOptionPane.WARNING_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(description,
                                        "This book is unavailable (borrowed by someone else).",
                                        "Not Available",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                        }

                        private String getUserFullName(String userId) {
                            try {
                                List<String> lines = Files.readAllLines(Paths.get("users.txt"));
                                for (String line : lines) {
                                    String[] parts = line.split(",", -1);
                                    if (parts.length >= 4 && parts[2].equals(userId)) {
                                        return parts[3]; // Full name
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return "Unknown User";
                        }
                    });


                    JLabel bookCover = new JLabel(icon);
                    bookCover.setBounds(30, 30, 220, 280);
                    bookCover.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

                    Font font = new Font("Arial", Font.BOLD, 16);
                    JLabel titleLabel = new JLabel("Title: " + name);
                    titleLabel.setFont(font);
                    titleLabel.setBounds(270, 40, 250, 25);

                    JLabel authorLabel = new JLabel("Author: " + author);
                    authorLabel.setFont(font);
                    authorLabel.setBounds(270, 70, 250, 25);

                    JLabel genreLabel = new JLabel("Genre: " + genre);
                    genreLabel.setFont(font);
                    genreLabel.setBounds(270, 100, 250, 25);

                    JLabel typeLabel = new JLabel("Type: " + type);
                    typeLabel.setFont(font);
                    typeLabel.setBounds(270, 130, 250, 25);

                    JLabel statusLabel = new JLabel("Status: " + status);
                    statusLabel.setFont(font);
                    statusLabel.setBounds(270, 160, 250, 25);

                    description.add(bookCover);
                    description.add(titleLabel);
                    description.add(authorLabel);
                    description.add(genreLabel);
                    description.add(typeLabel);
                    description.add(statusLabel);
                    description.add(Borrow);

                    description.setResizable(false);
                    description.setLocationRelativeTo(null);
                    description.setVisible(true);

                }
            }
        });
        ribbon.add(homeButton);
        ribbon.add(accountButton);
        ribbon.add(borrowButton);
        ribbon.add(historyButton);
        whitePanel.add(d);
        whitePanel.add(list);
        whitePanel.add(bluePanel);
        whitePanel.add(newbooks);
        frame.add(ribbon);
        ribbon.add(icon);
        ribbon.add(searchbar);
        frame.add(whitePanel);
        frame.add(historyPanel);
        frame.add(borrowpanel);
        frame.add(accountpanel);
        frame.setVisible(true);
    }

    private boolean addToBookRequests(String userId, String bookId) {
        try {
            String bookDetails = getBookDetails(bookId);
            if (bookDetails == null) return false;

            FileWriter writer = new FileWriter("requests.txt", true);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            writer.write(userId + "," + bookDetails + "," + sdf.format(new Date()) + "\n");
            writer.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private String getBookDetails(String bookId) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("books.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",", -1);
            if (parts.length >= 7 && parts[0].equals(bookId)) {
                reader.close();
                return line;
            }
        }
        reader.close();
        return null;
    }

    private void updateRequestTable() {
        requestModel.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader("requests.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] requestData = line.split(",", -1);
                if (requestData.length >= 5 && requestData[0].equals(currentUserId)) {
                    String bookName = requestData[2];
                    String genre = requestData[4];
                    String author = requestData[3];
                    String requestDate = requestData[requestData.length - 1];
                    requestModel.addRow(new Object[]{bookName, genre, author, requestDate});
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading request data:\n" + e.getMessage(),
                    "File Read Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel createAccountPanel() {
        JPanel accountPanel = new JPanel(new GridBagLayout());
        accountPanel.setBackground(new Color(245, 247, 250));

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        Dimension cardSize = new Dimension(700, 650);
        cardPanel.setPreferredSize(cardSize);
        cardPanel.setMinimumSize(cardSize);
        cardPanel.setMaximumSize(cardSize);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI Semibold", Font.PLAIN, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color accentColor = new Color(0, 120, 215);

        // Header
        JLabel headerLabel = new JLabel("Account Details");
        headerLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        headerLabel.setForeground(accentColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(headerLabel, gbc);

        // Profile Image
        profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(120, 120));
        profileImageLabel.setMinimumSize(new Dimension(120, 120));
        profileImageLabel.setMaximumSize(new Dimension(120, 120));


        // profile image
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(profileImageLabel, gbc);

        // Reset for fields
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 12, 12, 12);

        tfUsername = new JTextField(20);
        tfId = new JTextField(20);
        tfName = new JTextField(20);
        tfEmail = new JTextField(20);
        tfRole = new JTextField(20);

        JTextField[] fields = {tfUsername, tfId, tfName, tfEmail, tfRole};
        String[] labels = {"Username:", "User ID:", "Full Name:", "Email Address:", "Role:"};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 2;

            JLabel label = new JLabel(labels[i]);
            label.setFont(labelFont);
            label.setForeground(new Color(80, 80, 80));
            cardPanel.add(label, gbc);

            gbc.gridx = 1;
            fields[i].setEditable(false);
            fields[i].setFont(fieldFont);
            fields[i].setBackground(new Color(250, 250, 250));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            cardPanel.add(fields[i], gbc);
        }

        // Sign Out Button
        JButton logoutBtn = new JButton("Sign Out");
        logoutBtn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(accentColor);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)
        ));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutBtn.setBackground(Color.WHITE);
            }
        });
        logoutBtn.addActionListener(e -> logout());

        gbc.gridx = 0;
        gbc.gridy = labels.length + 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);
        cardPanel.add(logoutBtn, gbc);

        accountPanel.add(cardPanel);
        return accountPanel;
    }


    private void initializeBorrowPanel() {
        borrowpanel.removeAll();
        borrowpanel.setLayout(new GridBagLayout());
        borrowpanel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel requestLabel = new JLabel("BOOK REQUEST");
        requestLabel.setFont(new Font("Arial", Font.BOLD, 20));
        requestLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        requestLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        styleTable(requestTable);
        JScrollPane requestScrollPane = new JScrollPane(requestTable);
        requestScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        requestScrollPane.setPreferredSize(new Dimension(1100, 250));

        JLabel borrowLabel = new JLabel("CURRENTLY BORROWED");
        borrowLabel.setFont(new Font("Arial", Font.BOLD, 20));
        borrowLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        borrowLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        String[] borrowColumns = {"Name of Book", "Book Type", "Date Borrowed", "Return Due"};
        DefaultTableModel borrowModel = new DefaultTableModel(borrowColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable borrowTable = new JTable(borrowModel);
        styleTable(borrowTable);
        JScrollPane borrowScrollPane = new JScrollPane(borrowTable);
        borrowScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        borrowScrollPane.setPreferredSize(new Dimension(1100, 300));

        contentPanel.add(requestLabel);
        contentPanel.add(requestScrollPane);
        contentPanel.add(borrowLabel);
        contentPanel.add(borrowScrollPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        borrowpanel.add(contentPanel, gbc);

        loadRequestData();
        loadBorrowedData(borrowModel);

        borrowpanel.revalidate();
        borrowpanel.repaint();
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
    }

    private void loadRequestData() {
        requestModel.setRowCount(0);
        try {
            BufferedReader reader = new BufferedReader(new FileReader("requests.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] requestData = line.split(",", -1);
                if (requestData.length >= 5 && requestData[0].equals(currentUserId)) {
                    String bookName = requestData[2];
                    String genre = requestData[4];
                    String author = requestData[3];
                    String requestDate = requestData[requestData.length - 1];
                    requestModel.addRow(new Object[]{bookName, genre, author, requestDate});
                }
            }
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading request data:\n" + e.getMessage(),
                    "File Read Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void loadBorrowedData(DefaultTableModel model) {
        model.setRowCount(0);

        // Step 1: Load book types from books.txt
        Map<String, String> bookTypeMap = new HashMap<>();
        try (BufferedReader bookReader = new BufferedReader(new FileReader("books.txt"))) {
            String bookLine;
            while ((bookLine = bookReader.readLine()) != null) {
                String[] bookParts = bookLine.split(",", -1);
                if (bookParts.length >= 5) {
                    String bookId = bookParts[0];
                    String bookType = bookParts[4];  // index 4 = Book Type
                    bookTypeMap.put(bookId, bookType);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Step 2: Load borrowed data from borrow.txt
        try (BufferedReader reader = new BufferedReader(new FileReader("borrow.txt"))) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 6 && parts[1].equals(currentUserId)) {  // check user ID

                    String bookId = parts[0];
                    String bookTitle = parts[2];
                    String bookType = bookTypeMap.getOrDefault(bookId, "Unknown");
                    Date borrowedDate = inputFormat.parse(parts[4]);
                    String dateBorrowed = displayFormat.format(borrowedDate);

                    String returnDue;
                    if (bookType.equalsIgnoreCase("Physical Book")) {
                        returnDue = "Not Yet Returned";
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(borrowedDate);
                        calendar.add(Calendar.DAY_OF_MONTH, 7);
                        returnDue = displayFormat.format(calendar.getTime());
                    }

                    model.addRow(new Object[]{bookTitle, bookType, dateBorrowed, returnDue});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean loadUserData(String userIdToFind) {
        if (userIdToFind == null || userIdToFind.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid user ID");
            clearAccountFields1();
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    String id = parts[2].trim();
                    if (id.equalsIgnoreCase(userIdToFind)) {
                        tfUsername.setText(parts[0].trim());
                        tfId.setText(parts[2].trim());
                        tfName.setText(parts[3].trim());
                        tfEmail.setText(parts[4].trim());
                        tfRole.setText(parts[5].trim());

                        String imageFilename = parts[6].trim();
                        updateProfileImage(imageFilename);
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearAccountFields1();
        return false;
    }

    private void updateProfileImage(String imageFilename) {
        if (imageFilename == null || imageFilename.isEmpty()) {
            profileImageLabel.setIcon(null);
            profileImageLabel.setText("No Image");
            return;
        }

        File imageFile = new File("profilepics/" + imageFilename);

        if (imageFile.exists()) {
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            Image image = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            profileImageLabel.setIcon(new ImageIcon(image));
            profileImageLabel.setText(null);  // Clear text if set before
        } else {
            System.err.println("Image file not found: " + imageFile.getAbsolutePath());
            profileImageLabel.setIcon(null);
            profileImageLabel.setText("No Image");
        }
    }

    private void clearAccountFields1() {
        tfUsername.setText("");
        tfId.setText("");
        tfName.setText("");
        tfEmail.setText("");
        tfRole.setText("");
        profileImageLabel.setIcon(null);
    }

    private void historypanel() {
        historyPanel.removeAll();
        historyPanel.setLayout(new BorderLayout());

        HistoryPanel newHistoryPanel = new HistoryPanel();
        newHistoryPanel.setUserID(currentUserId);
        historyPanel.add(newHistoryPanel, BorderLayout.CENTER);

        historyPanel.revalidate();
        historyPanel.repaint();
    }

    private void clearAccountFields() {
        tfUsername.setText("");
        tfId.setText("");
        tfName.setText("");
        tfEmail.setText("");
        tfRole.setText("");
    }

    private void logout() {
        frame.dispose(); // Close the dashboard window

        // Show logout message before starting new login window
        JLabel logol = new JLabel(new ImageIcon("panelimages/log.png"));
        JLabel line3 = new JLabel("                Successfully Log out");
        JPanel textPanel1 = new JPanel();
        textPanel1.setLayout(new BoxLayout(textPanel1, BoxLayout.Y_AXIS));
        textPanel1.add(line3);
        textPanel1.setBounds(50, 200, 100, 100);

        JPanel adminl = new JPanel();
        adminl.setLayout(new BorderLayout());
        adminl.add(logol, BorderLayout.CENTER);
        adminl.add(textPanel1, BorderLayout.AFTER_LAST_LINE);

        JOptionPane.showMessageDialog(null, adminl, "Log out", JOptionPane.PLAIN_MESSAGE);

        // Only one call to start the login page
        new Main().start();
    }

    private void loadBooksFromFile() {
        model.setRowCount(0);
        try {
            File file = new File("books.txt");
            if (!file.exists()) file.createNewFile();

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] data = line.split(",", -1);
                    if (data.length == 7) {
                        model.addRow(data);
                    }
                }
            }
            scanner.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    // Method to check rejected requests
    public void checkRejectedRequests() {
        try {
            // Read all lines from rejected.txt
            List<String> rejectedLines = Files.readAllLines(Paths.get("rejected.txt"));

            for (String line : rejectedLines) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String userId = parts[0];
                    String bookName = parts[2];

                    // If the current User's ID matches the rejected request
                    if (userId.equals(currentUserId)) {
                        // Show a pop-up message
                        int response = JOptionPane.showOptionDialog(
                                null,
                                "Your request to borrow '" + bookName + "' has been rejected.",
                                "Request Rejected",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                new Object[]{"Ok"},
                                "Ok"
                        );

                        // If the user clicks "Ok", remove the rejected entry from rejected.txt
                        if (response == JOptionPane.OK_OPTION) {
                            removeRejectedRequest(line);
                        }
                        break; // Exit the loop after the first match
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to remove the rejected request from rejected.txt
    private void removeRejectedRequest(String rejectedRequestLine) {
        try {
            // Read all lines from rejected.txt
            List<String> rejectedLines = new ArrayList<>(Files.readAllLines(Paths.get("rejected.txt")));

            // Remove the specific rejected line
            rejectedLines.remove(rejectedRequestLine);

            // Overwrite rejected.txt with the updated list of rejected requests
            Files.write(Paths.get("rejected.txt"), rejectedLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private void notif() {
        try {
            File borrowFile = new File("borrow.txt");
            File booksFile = new File("books.txt");

            Map<String, String[]> bookData = new HashMap<>();
            List<String> updatedBooksLines = new ArrayList<>();
            Scanner bookScanner = new Scanner(booksFile);

            // Read books.txt and store lines in memory
            while (bookScanner.hasNextLine()) {
                String line = bookScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    bookData.put(parts[0].trim(), parts); // BookID → full row
                }
                updatedBooksLines.add(line); // Preserve original order for rewriting
            }
            bookScanner.close();

            Scanner borrowScanner = new Scanner(borrowFile);
            List<String> remainingBorrowLines = new ArrayList<>();

            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            Date now = new Date();

            while (borrowScanner.hasNextLine()) {
                String line = borrowScanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length >= 6) {
                    String bookId = parts[0].trim();
                    String userIdInFile = parts[1].trim();
                    String bookTitle = parts[2].trim();
                    String dueDateStr = parts[5].trim();

                    String[] bookInfo = bookData.get(bookId);
                    if (bookInfo != null && bookInfo[4].trim().equalsIgnoreCase("eBook")) {
                        if (userIdInFile.equals(this.currentUserId) && !dueDateStr.equalsIgnoreCase("Not Returned Yet")) {
                            Date dueDate = sdf.parse(dueDateStr);
                            long diffInMillis = dueDate.getTime() - now.getTime();
                            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

                            if (diffInDays <= 1 && diffInDays >= 0) {
                                JOptionPane.showMessageDialog(null,
                                        "📢 Reminder: Your eBook \"" + bookTitle + "\" will expire in 1 day.",
                                        "eBook Expiry Notice", JOptionPane.WARNING_MESSAGE);
                                remainingBorrowLines.add(line); // Keep it for now
                            } else if (now.after(dueDate)) {
                                JOptionPane.showMessageDialog(null,
                                        "❌ Your eBook \"" + bookTitle + "\" has expired and will be returned.",
                                        "eBook Expired", JOptionPane.ERROR_MESSAGE);

                                // Update the status in books.txt (Available again)
                                bookInfo[5] = "Available";
                            } else {
                                remainingBorrowLines.add(line); // Not expired
                            }
                        } else {
                            remainingBorrowLines.add(line); // Not for this user or not returned yet
                        }
                    } else {
                        remainingBorrowLines.add(line); // Not an eBook
                    }
                } else {
                    remainingBorrowLines.add(line); // Malformed line, keep it just in case
                }
            }
            borrowScanner.close();

            // Rewrite books.txt with updated statuses
            try (PrintWriter bookWriter = new PrintWriter(booksFile)) {
                for (String[] parts : bookData.values()) {
                    bookWriter.println(String.join(",", parts));
                }
            }

            // Rewrite borrow.txt without expired eBooks
            try (PrintWriter borrowWriter = new PrintWriter(borrowFile)) {
                for (String borrowLine : remainingBorrowLines) {
                    borrowWriter.println(borrowLine);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error reading or updating borrow/books data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }*/

    private void checkReturnedBooks() {
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(Paths.get("returnedbook.txt")));
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(currentUserId)) {
                    String bookName = parts[2];
                    int result = JOptionPane.showConfirmDialog(null,
                            "The book you borrowed \"" + bookName + "\" has been returned.",
                            "Book Returned",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);

                    // Only remove the line if user clicks OK
                    if (result != JOptionPane.OK_OPTION) {
                        updatedLines.add(line); // user didn’t confirm, keep the line
                    }
                } else {
                    updatedLines.add(line); // not for this user
                }
            }
            // Overwrite returnedbook.txt with updated lines
            Files.write(Paths.get("returnedbook.txt"), updatedLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void checkAutoReturnedEBooks() {
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(Paths.get("autoreturn.txt")));
            List<String> updatedLines = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(this.currentUserId)) {
                    String bookName = parts[2];
                    int result = JOptionPane.showConfirmDialog(null,
                            "The eBook you borrowed \"" + bookName + "\" has been returned to the system.",
                            "Auto-Return Notification",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);

                    // Only remove the line if user clicks OK
                    if (result != JOptionPane.OK_OPTION) {
                        updatedLines.add(line); // user didn’t confirm, keep the line
                    }
                } else {
                    updatedLines.add(line); // not for this user
                }
            }

            // Overwrite autoreturn.txt with updated lines
            Files.write(Paths.get("autoreturn.txt"), updatedLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void checkEBook1DayWarnings() {
        File file = new File("eBook1day.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length >= 4 && parts[0].equals(currentUserId)) {
                    String bookName = parts[2];
                    String dueDate = parts[3];
                    JOptionPane.showMessageDialog(this,
                            "Reminder: Your borrowed eBook '" + bookName +
                                    "' will expire tomorrow (" + dueDate + ").",
                            "Upcoming eBook Expiry", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading eBook1day.txt: " + e.getMessage());
        }
    }


}