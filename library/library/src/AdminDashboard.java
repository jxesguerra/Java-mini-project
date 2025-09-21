import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class AdminDashboard {
    JFrame frame;
    JLayeredPane layeredPane;
    JPanel contentPanel;
    int screenWidth, screenHeight;

    public AdminDashboard() {
        initializeFrame();
        addLogo();  // Updated method
        addWhiteOverlayPanel();
        addBorrowRequestPanel();
        addSmallLogo();
        setupTopRibbon();
        setupContentPanel();
        //processEbookDueReturns();
        frame.setVisible(true);
    }
    private void initializeFrame() {
        frame = new JFrame("Admin Dashboard");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;

        layeredPane = new JLayeredPane();
        frame.add(layeredPane);
    }
    private void addLogo() {
        try {
            BufferedImage image = ImageIO.read(new File("panelimages/PCUMain.jpg"));

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            int maxWidth = screenWidth;
            int maxHeight = screenHeight - 60; // below the ribbon

            // Scale to fill the screen width
            float scale = (float) maxWidth / imageWidth;

            int scaledWidth = maxWidth;
            int scaledHeight = (int) (imageHeight * scale);

            Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            JLabel logo = new JLabel(new ImageIcon(scaledImage));

            // Align to bottom of the space (crop from top if needed)
            int yOffset = 60 - (scaledHeight - maxHeight); // shift image up

            logo.setBounds(0, yOffset, scaledWidth, scaledHeight);
            layeredPane.add(logo, Integer.valueOf(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setupTopRibbon() {
        JPanel ribbon = new JPanel();
        ribbon.setBackground(Color.BLUE);
        ribbon.setBounds(0, 0, screenWidth, 60);
        ribbon.setLayout(null);
        layeredPane.add(ribbon, Integer.valueOf(1));

        JLabel label = new JLabel("       ADMIN DASHBOARD", SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(Color.WHITE);
        label.setBounds(20, 10, 400, 40);
        ribbon.add(label);

        int buttonWidth = 150;
        int buttonHeight = 41;
        int gap = 20;
        ImageIcon addbook = new ImageIcon("panelimages/addbock.png");
        ImageIcon borro = new ImageIcon("panelimages/bo.png");
        ImageIcon log = new ImageIcon("panelimages/log.png");
        ImageIcon user = new ImageIcon("panelimages/add.png");
        JButton userButton = new JButton("Users",user);
        JButton bookButton = new JButton("Books",addbook);
        JButton borrowButton = new JButton("Borrowed",borro);
        JButton logoutButton = new JButton("Logout",log);

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        for (JButton button : new JButton[]{userButton, bookButton, borrowButton, logoutButton}) {

            button.setFont(buttonFont);
            button.setFocusable(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            ribbon.add(button);
            ;
        }

        int xStart = screenWidth - (3 * buttonWidth + 3 * gap);
        userButton.setBounds(xStart - (buttonWidth + gap), 15, buttonWidth, buttonHeight);
        bookButton.setBounds(xStart, 15, buttonWidth, buttonHeight);
        borrowButton.setBounds(xStart + buttonWidth + gap, 15, buttonWidth + 10, buttonHeight);
        logoutButton.setBounds(xStart + 2 * (buttonWidth + gap), 15, buttonWidth, buttonHeight);

        bookButton.addActionListener(e -> showBookPanel());
        userButton.addActionListener(e -> showUserPanel());
        borrowButton.addActionListener(e -> showBorrowPanel());
        logoutButton.addActionListener(e -> LogoutPanel());
    }
    private void addSmallLogo() {
        JLabel smallLogo = new JLabel(new ImageIcon("panelimages/PCULogoTiny.png"));
        smallLogo.setBounds(5, -3, 65, 65);
        layeredPane.add(smallLogo, Integer.valueOf(2));
    }
    private void setupContentPanel() {
        contentPanel = new JPanel(null);
        contentPanel.setBounds(0, 60, screenWidth, screenHeight - 60);
        contentPanel.setOpaque(true);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setVisible(false);
        layeredPane.add(contentPanel, Integer.valueOf(2));
    }
    private void showBookPanel() {
        contentPanel.removeAll();
        AdminBooksPanel booksPanel = new AdminBooksPanel(frame);
        booksPanel.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(booksPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        contentPanel.setVisible(true);
    }
    private void showUserPanel() {
        contentPanel.removeAll();
        AdminUsersPanel usersPanel = new AdminUsersPanel(frame);
        usersPanel.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(usersPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        contentPanel.setVisible(true);
    }
    private void showBorrowPanel() {
        contentPanel.removeAll();
        AdminBorrowPanel borrowPanel = new AdminBorrowPanel(frame);
        borrowPanel.setBounds(0, 0, contentPanel.getWidth(), contentPanel.getHeight());
        contentPanel.add(borrowPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        contentPanel.setVisible(true);
    }
    private void LogoutPanel() {
        frame.dispose(); // Close the dashboard window first

        // Show logout message before opening login page
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

        // Open login page AFTER showing the logout message
        new Main().start();
    }

    private void addWhiteOverlayPanel() {
        JPanel whitePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 50));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        whitePanel.setBounds(0, 60, screenWidth, screenHeight - 60);
        whitePanel.setOpaque(false);
        layeredPane.add(whitePanel, Integer.valueOf(1));
    }
    private void addBorrowRequestPanel() {
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));
        requestPanel.setBackground(new Color(255, 255, 255, 200));
        requestPanel.setBounds(20, screenHeight - 300, 460, 100);

        JScrollPane scrollPane = new JScrollPane(requestPanel);
        scrollPane.setBounds(20, screenHeight - 300, 460, 200);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        boolean hasRequests = false;

        // Map to store panels by bookId
        Map<String, List<JPanel>> bookPanelsMap = new HashMap<>();

        try {
            java.util.List<String> users = java.nio.file.Files.readAllLines(new File("users.txt").toPath());
            java.util.List<String> initialBooks = java.nio.file.Files.readAllLines(new File("books.txt").toPath());
            java.util.List<String> requests = java.nio.file.Files.readAllLines(new File("requests.txt").toPath());

            for (String requestLine : requests) {
                String[] requestParts = requestLine.split(",");
                if (requestParts.length >= 9) {
                    hasRequests = true;

                    String userId = requestParts[0];
                    String bookId = requestParts[1];
                    String dateBorrowed = requestParts[8];

                    String username = "UnknownUser";
                    for (String userLine : users) {
                        String[] userParts = userLine.split(",");
                        if (userParts.length >= 3 && userParts[2].equals(userId)) {
                            username = userParts[0];
                            break;
                        }
                    }

                    String bookName = "UnknownBook";
                    String bookType = "Unknown";
                    for (String bookLine : initialBooks) {
                        String[] bookParts = bookLine.split(",");
                        if (bookParts.length >= 5 && bookParts[0].equals(bookId)) {
                            bookName = bookParts[1];
                            bookType = bookParts[4];
                            break;
                        }
                    }

                    String message = username + " wants to borrow " + bookName + ". (" + dateBorrowed + ")";
                    JPanel singleRequestPanel = new JPanel(new BorderLayout());
                    singleRequestPanel.setBackground(new Color(240, 240, 240));
                    singleRequestPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    JLabel msgLabel = new JLabel(message);
                    JButton confirmButton = new JButton("Confirm");
                    confirmButton.setPreferredSize(new Dimension(100, 25));
                    confirmButton.setFocusable(false);
                    JButton rejectButton = new JButton("Reject");
                    rejectButton.setPreferredSize(new Dimension(100, 25));
                    rejectButton.setFocusable(false);

                    String finalUsername = username;
                    String finalBookName = bookName;
                    String finalBookType = bookType;

                    confirmButton.addActionListener(e -> {
                        // Remove all panels for the same bookId
                        List<JPanel> panelsToRemove = bookPanelsMap.get(bookId);
                        if (panelsToRemove != null) {
                            for (JPanel panel : panelsToRemove) {
                                requestPanel.remove(panel);
                            }
                            requestPanel.revalidate();
                            requestPanel.repaint();
                        }

                        try {
                            File requestFile = new File("requests.txt");
                            List<String> allRequests = new ArrayList<>(Files.readAllLines(requestFile.toPath()));
                            List<String> updatedRequests = new ArrayList<>();
                            List<String> rejectedEntries = new ArrayList<>();

                            for (String line : allRequests) {
                                String[] parts = line.split(",");
                                if (parts.length >= 9) {
                                    String rUserId = parts[0];
                                    String rBookId = parts[1];

                                    if (rBookId.equals(bookId)) {
                                        if (!rUserId.equals(userId)) {
                                            rejectedEntries.add(rUserId + "," + rBookId + "," + finalBookName);
                                        }
                                        // Remove all requests for this book
                                    } else {
                                        updatedRequests.add(line); // Keep unrelated requests
                                    }
                                }
                            }
                            // Overwrite requests.txt with updated list
                            Files.write(requestFile.toPath(), updatedRequests);

                            // Append rejected ones
                            File rejectedFile = new File("rejected.txt");
                            if (!rejectedFile.exists()) rejectedFile.createNewFile();
                            Files.write(rejectedFile.toPath(), rejectedEntries, StandardOpenOption.APPEND);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        // Write to borrow.txt
                        try {
                            String fullName = "Unknown";
                            for (String userLine : users) {
                                String[] userParts = userLine.split(",");
                                if (userParts.length >= 4 && userParts[2].equals(userId)) {
                                    fullName = userParts[3];
                                    break;
                                }
                            }

                            String borrowDate = new java.util.Date().toString();
                            String returnDate;
                            if (finalBookType.equalsIgnoreCase("Physical Book")) {
                                returnDate = "Not Yet Returned";
                            } else {
                                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                                Date parsedDate = sdf.parse(borrowDate);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(parsedDate);
                                cal.add(Calendar.DAY_OF_MONTH, 7);
                                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd yyyy");
                                returnDate = outputFormat.format(cal.getTime());
                            }

                            String borrowEntry = bookId + "," + userId + "," + finalBookName + "," + fullName + "," + borrowDate + "," + returnDate;

                            File borrowFile = new File("borrow.txt");
                            Files.write(borrowFile.toPath(), Collections.singletonList(borrowEntry),
                                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        // Mark book as unavailable
                        try {
                            List<String> books = Files.readAllLines(new File("books.txt").toPath());
                            File booksFile = new File("books.txt");
                            List<String> updatedBooks = new ArrayList<>();

                            for (String bookLine : books) {
                                String[] bookParts = bookLine.split(",");
                                if (bookParts.length >= 7 && bookParts[0].equals(bookId)) {
                                    bookParts[5] = "Unavailable";
                                    updatedBooks.add(String.join(",", bookParts));
                                } else {
                                    updatedBooks.add(bookLine);
                                }
                            }

                            Files.write(booksFile.toPath(), updatedBooks);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        JLabel logol = new JLabel(new ImageIcon("panelimages/co.png"));
                        JLabel line3 = new JLabel("      Request confirmed for " + finalUsername);
                        JPanel textPanel1 = new JPanel();
                        textPanel1.setLayout(new BoxLayout(textPanel1, BoxLayout.Y_AXIS));
                        textPanel1.add(line3);
                        textPanel1.setBounds(50, 200, 100, 100);

                        JPanel adminl = new JPanel();
                        adminl.setLayout(new BorderLayout());
                        adminl.add(logol, BorderLayout.CENTER);
                        adminl.add(textPanel1, BorderLayout.AFTER_LAST_LINE);

                        JOptionPane.showMessageDialog(null, adminl, "Confirm", JOptionPane.PLAIN_MESSAGE);

                        if (requestPanel.getComponentCount() == 0) {
                            scrollPane.setVisible(false);
                        }
                    });

                    rejectButton.addActionListener(e -> {
                        // Remove the request from the requests.txt file
                        try {
                            File requestFile = new File("requests.txt");
                            List<String> allRequests = new ArrayList<>(Files.readAllLines(requestFile.toPath()));
                            List<String> updatedRequests = new ArrayList<>();
                            List<String> rejectedEntries = new ArrayList<>();

                            // Process all requests and remove the rejected one
                            for (String line : allRequests) {
                                String[] parts = line.split(",");
                                if (parts.length >= 9) {
                                    String rUserId = parts[0];
                                    String rBookId = parts[1];

                                    // Check if the current line matches the request to be rejected
                                    if (rBookId.equals(bookId) && rUserId.equals(userId)) {
                                        // Add this rejected entry to the list
                                        rejectedEntries.add(rUserId + "," + rBookId + "," + finalBookName);
                                    } else {
                                        // Keep other requests that are not rejected
                                        updatedRequests.add(line);
                                    }
                                }
                            }
                            // Overwrite the requests.txt file with the updated list (no rejected request)
                            Files.write(requestFile.toPath(), updatedRequests);

                            // Append the rejected entry to rejected.txt
                            File rejectedFile = new File("rejected.txt");
                            if (!rejectedFile.exists()) rejectedFile.createNewFile();
                            Files.write(rejectedFile.toPath(), rejectedEntries, StandardOpenOption.APPEND);

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        // Remove the request panel from the UI
                        requestPanel.remove(singleRequestPanel);
                        requestPanel.revalidate();
                        requestPanel.repaint();

                        // If no requests are left, hide the scroll pane
                        if (requestPanel.getComponentCount() == 0) {
                            scrollPane.setVisible(false);
                        }

                        // Show a pop-up message to confirm rejection
                        JOptionPane.showMessageDialog(null, "Borrow request successfully rejected.", "Rejected", JOptionPane.INFORMATION_MESSAGE);
                    });

                    JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
                    buttonPanel.setOpaque(false);
                    buttonPanel.setPreferredSize(new Dimension(100, 60));

                    confirmButton.setPreferredSize(null);
                    rejectButton.setPreferredSize(null);

                    buttonPanel.add(confirmButton);
                    buttonPanel.add(rejectButton);

                    singleRequestPanel.add(msgLabel, BorderLayout.CENTER);
                    singleRequestPanel.add(buttonPanel, BorderLayout.EAST);

                    requestPanel.add(singleRequestPanel);

                    // Track the panel by bookId
                    bookPanelsMap.computeIfAbsent(bookId, k -> new ArrayList<>()).add(singleRequestPanel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (hasRequests) {
            layeredPane.add(scrollPane, Integer.valueOf(2));
        }
    }

    /*private void processEbookDueReturns() {
        File borrowFile = new File("borrow.txt");
        File booksFile = new File("books.txt");
        File historyFile = new File("history.txt");

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        Date now = new Date();

        try {
            List<String> borrowLines = Files.readAllLines(borrowFile.toPath());
            List<String> updatedBorrowLines = new ArrayList<>();
            List<String> booksLines = Files.readAllLines(booksFile.toPath());

            for (String borrowLine : borrowLines) {
                String[] parts = borrowLine.split(",", 6);
                if (parts.length < 6) continue;

                String bookId = parts[0];
                String userId = parts[1];
                String bookName = parts[2];
                String userName = parts[3];
                Date borrowedDate = format.parse(parts[4]);
                String statusOrReturn = parts[5];

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
                    due.add(Calendar.DAY_OF_MONTH, 7);

                    if (!now.before(due.getTime())) {
                        // Add to history.txt
                        String historyEntry = String.join(",", bookId, userId, bookName, userName, parts[4], format.format(now));
                        Files.write(historyFile.toPath(), Collections.singletonList(historyEntry), StandardOpenOption.APPEND, StandardOpenOption.CREATE);

                        // Set book to Available in books.txt
                        for (int i = 0; i < booksLines.size(); i++) {
                            String[] bookParts = booksLines.get(i).split(",", -1);
                            if (bookParts.length >= 6 && bookParts[0].equals(bookId)) {
                                bookParts[5] = "Available";
                                booksLines.set(i, String.join(",", bookParts));
                                break;
                            }
                        }
                        continue; // Do NOT add this line back to updatedBorrowLines
                    }
                }
                updatedBorrowLines.add(borrowLine); // Keep line if not due yet or physical
            }
            // Write back updated borrow list and books
            Files.write(borrowFile.toPath(), updatedBorrowLines);
            Files.write(booksFile.toPath(), booksLines);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    public static void main(String[] args) {
        String adminId = "";
        SwingUtilities.invokeLater(() -> new AdminDashboard());
    }
}