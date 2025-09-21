import java.awt.*;
//import java.awt.List;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class Main {
    JFrame frame;

    public Main() {}

    public static void main(String[] args) {
        autoReturnEBooks();
        new Main().start();
    }

    public void start() {
        frame = new JFrame("LOG IN");
        frame.setSize(1084, 700);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel backgroundLabel = new JLabel(new ImageIcon("panelimages/PCUMain2.jpg"));
        backgroundLabel.setBounds(-10, 0, 1084, 700);

        JLabel logo = new JLabel(new ImageIcon("panelimages/PCULogoSmall.png"));
        logo.setBounds(450, 50, 200, 300);

        JPanel centerPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 30, 30);
            }
        };
        centerPanel.setBounds(350, 100, 400, 500);
        centerPanel.setOpaque(false);

        JPanel whitePanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 50));
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        };

        // Welcome messages
        JLabel smile2 = new JLabel(new ImageIcon("panelimages/smile.png"));
        JLabel line1 = new JLabel("           SUCCESSFULLY LOGGED IN");
        JLabel line2 = new JLabel("                       WELCOME USER");

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(line1);
        textPanel.add(line2);
        textPanel.setBounds(50, 200, 100, 100);

        JPanel userl = new JPanel();
        userl.setLayout(new BorderLayout());
        userl.add(smile2, BorderLayout.CENTER);
        userl.add(textPanel, BorderLayout.AFTER_LAST_LINE);

        JLabel logol = new JLabel(new ImageIcon("panelimages/smallest.png"));
        JLabel line3 = new JLabel("           SUCCESSFULLY LOGGED IN");
        JLabel line4 = new JLabel("                       WELCOME ADMIN");

        JPanel textPanel1 = new JPanel();
        textPanel1.setLayout(new BoxLayout(textPanel1, BoxLayout.Y_AXIS));
        textPanel1.add(line3);
        textPanel1.add(line4);
        textPanel1.setBounds(50, 200, 100, 100);

        JPanel adminl = new JPanel();
        adminl.setLayout(new BorderLayout());
        adminl.add(logol, BorderLayout.CENTER);
        adminl.add(textPanel1, BorderLayout.AFTER_LAST_LINE);

        whitePanel.setBounds(0, 0, 1084, 700);
        whitePanel.setOpaque(false);

        Font font1 = new Font("Arial", Font.BOLD, 30);
        Font font2 = new Font("Arial", Font.BOLD, 15);

        JLabel loginLabel = new JLabel("PCU LIBRARY");
        loginLabel.setFont(font1);
        loginLabel.setBounds(450, 310, 2000, 40);
        loginLabel.setForeground(Color.white);

        JLabel loginLabel1 = new JLabel("LOG IN");
        loginLabel1.setFont(font1);
        loginLabel1.setBounds(500, 330, 200, 100);
        loginLabel1.setForeground(Color.white);

        JLabel username1 = new JLabel("USERNAME");
        username1.setFont(font2);
        username1.setBounds(355, 370, 500, 40);
        username1.setForeground(Color.white);

        JLabel password1 = new JLabel("PASSWORD");
        password1.setFont(font2);
        password1.setBounds(355, 450, 500, 40);
        password1.setForeground(Color.white);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(365, 420, 365, 25);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField.setForeground(Color.BLACK);
        usernameField.setBackground(Color.WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(365, 500, 365, 25);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setForeground(Color.BLACK);
        passwordField.setBackground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        passwordField.setEchoChar('•');

        ImageIcon eyeIcon = new ImageIcon("panelimages/eyebutton.png");
        JButton eyeButton = new JButton(eyeIcon);
        eyeButton.setBounds(690, 493, 40, 40);
        eyeButton.setContentAreaFilled(false);
        eyeButton.setBorderPainted(false);
        eyeButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == 8226) {
                passwordField.setEchoChar('\u0000');
            } else {
                passwordField.setEchoChar('•');
            }
        });

        JButton loginButton = new JButton("Log in");
        loginButton.setFont(font2);
        loginButton.setBounds(500, 550, 100, 40);
        loginButton.setFocusable(false);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ef) {
                String inputUsername = usernameField.getText().trim();
                String inputPassword = new String(passwordField.getPassword()).trim();
                String[] authResult = authenticateUser(inputUsername, inputPassword);
                if (authResult != null) {
                    String role = authResult[0];
                    String id = authResult[1];

                    frame.dispose(); // Close login window

                    if (role.equals("admin")) {
                        new AdminDashboard();
                        JOptionPane.showMessageDialog(null, adminl, "ADMIN", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        // Show welcome dialog
                        JOptionPane.showMessageDialog(null, userl, "USER", JOptionPane.PLAIN_MESSAGE);

                        // Then open User dashboard and notif
                        SwingUtilities.invokeLater(() -> {
                            User user = new User(id); // This will call notif() inside User constructor
                            user.checkRejectedRequests();
                        });
                    }
                    return;
                }

                JOptionPane.showMessageDialog(null, "Incorrect username or password!");
            }
        });

        frame.add(loginLabel);
        frame.add(loginButton);
        frame.add(eyeButton);
        frame.add(password1);
        frame.add(passwordField);
        frame.add(username1);
        frame.add(loginLabel1);
        frame.add(usernameField);
        frame.add(logo);
        frame.add(centerPanel);
        frame.add(whitePanel);
        frame.add(backgroundLabel);
        frame.setVisible(true);
    }

    private String[] authenticateUser(String inputUsername, String inputPassword) {
        try {
            // Check admin file
            File adminFile = new File("admins.txt");
            Scanner adminScanner = new Scanner(adminFile);

            while (adminScanner.hasNextLine()) {
                String line = adminScanner.nextLine().trim();
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String fileUsername = parts[0].trim();
                    String filePassword = parts[1].trim();
                    if (inputUsername.equalsIgnoreCase(fileUsername) && inputPassword.equals(filePassword)) {
                        adminScanner.close();
                        return new String[]{"admin", fileUsername};
                    }
                }
            }
            adminScanner.close();

            // Check user file
            File userFile = new File("users.txt");
            Scanner userScanner = new Scanner(userFile);

            while (userScanner.hasNextLine()) {
                String line = userScanner.nextLine().trim();
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String fileUsername = parts[0].trim();
                    String filePassword = parts[1].trim();
                    String fileUserId = parts[2].trim();
                    if (inputUsername.equalsIgnoreCase(fileUsername) && inputPassword.equals(filePassword)) {
                        userScanner.close();
                        return new String[]{"user", fileUserId};
                    }
                }
            }
            userScanner.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Error accessing user database",
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null; // Login failed
    }

    public static void autoReturnEBooks() {
        File borrowFile = new File("borrow.txt");
        File booksFile = new File("books.txt");
        File autoReturnFile = new File("autoreturn.txt");
        File historyFile = new File("history.txt");
        File eBook1DayFile = new File("eBook1day.txt");

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        Date now = new Date();

        try {
            List<String> borrowLines = Files.readAllLines(borrowFile.toPath());
            List<String> updatedBorrowLines = new ArrayList<>();
            List<String> booksLines = Files.readAllLines(booksFile.toPath());
            List<String> autoReturnEntries = new ArrayList<>();
            List<String> historyEntries = new ArrayList<>();
            List<String> oneDayWarningEntries = new ArrayList<>();
            List<String> existingOneDayWarnings = new ArrayList<>();
            if (eBook1DayFile.exists()) {
                existingOneDayWarnings = Files.readAllLines(eBook1DayFile.toPath());
            }

            for (String borrowLine : borrowLines) {
                String[] parts = borrowLine.split(",", 6);
                if (parts.length < 6) continue;

                String bookId = parts[0];
                String userId = parts[1];
                String bookName = parts[2];
                String userName = parts[3];
                Date borrowedDate = format.parse(parts[4]);
                String status = parts[5];

                // Get book type from books.txt
                String bookType = "Physical Book";
                for (String bookLine : booksLines) {
                    String[] bookParts = bookLine.split(",", -1);
                    if (bookParts.length >= 6 && bookParts[0].equals(bookId)) {
                        bookType = bookParts[4];
                        break;
                    }
                }

                if (bookType.equals("eBook")) {
                    Calendar due = Calendar.getInstance();
                    due.setTime(borrowedDate);
                    due.add(Calendar.DAY_OF_MONTH, 7); // eBook due date = borrowed + 7 days

                    // Check for 1-day-before due warning
                    Calendar oneDayBeforeDue = (Calendar) due.clone();
                    oneDayBeforeDue.add(Calendar.DAY_OF_MONTH, -1);
                    if (isSameDay(now, oneDayBeforeDue.getTime())) {
                        String warningEntry = String.join(",", userId, bookId, bookName, format.format(due.getTime()));
                        oneDayWarningEntries.add(warningEntry);
                    }

                    // Auto-return if overdue
                    if (!now.before(due.getTime())) {
                        String autoEntry = String.join(",", userId, bookId, bookName);
                        autoReturnEntries.add(autoEntry);

                        String historyEntry = String.join(",", bookId, userId, bookName, userName, parts[4], format.format(now));
                        historyEntries.add(historyEntry);

                        // Mark book as Available
                        for (int i = 0; i < booksLines.size(); i++) {
                            String[] bookParts = booksLines.get(i).split(",", -1);
                            if (bookParts.length >= 6 && bookParts[0].equals(bookId)) {
                                bookParts[5] = "Available";
                                booksLines.set(i, String.join(",", bookParts));
                                break;
                            }
                        }

                        continue; // don't re-add to borrow.txt
                    }
                }

                updatedBorrowLines.add(borrowLine); // keep line if not due or physical
            }

            // Write updated files
            Files.write(borrowFile.toPath(), updatedBorrowLines);
            Files.write(booksFile.toPath(), booksLines);

            if (!autoReturnEntries.isEmpty()) {
                Files.write(autoReturnFile.toPath(), autoReturnEntries, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            }

            if (!historyEntries.isEmpty()) {
                Files.write(historyFile.toPath(), historyEntries, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            }
            if (!oneDayWarningEntries.isEmpty()) {
                Files.write(eBook1DayFile.toPath(), oneDayWarningEntries, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            }
            // Remove 1-day warning entries for books that were just auto-returned
            if (!autoReturnEntries.isEmpty()) {
                Set<String> returnedBookKeys = autoReturnEntries.stream()
                        .map(entry -> {
                            String[] parts = entry.split(",", 3);
                            return parts[0] + "," + parts[1]; // userId,bookId
                        })
                        .collect(Collectors.toSet());

                List<String> filteredWarnings = existingOneDayWarnings.stream()
                        .filter(line -> {
                            String[] parts = line.split(",", 4);
                            if (parts.length < 2) return true;
                            String key = parts[0] + "," + parts[1]; // userId,bookId
                            return !returnedBookKeys.contains(key);
                        })
                        .collect(Collectors.toList());

                Files.write(eBook1DayFile.toPath(), filteredWarnings, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility to compare dates by year + day-of-year only
    private static boolean isSameDay(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
