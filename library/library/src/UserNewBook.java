import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;


public class UserNewBook {
    private JPanel whitePanel;
    private String currentUserId;
    private DefaultTableModel requestModel;
    private JTable requestTable;

    public UserNewBook(JPanel whitePanel, DefaultTableModel requestModel, JTable requestTable, String userId) {
        this.whitePanel = whitePanel;
        this.requestModel = requestModel;
        this.requestTable = requestTable;
        this.currentUserId = userId;
        try {
            loadBooks();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading books:\n" + e.getMessage(),
                    "File Read Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadBooks() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("books.txt"));
        String line;

        int gap = 10;
        int x = 40;
        int y = 75;

        LinkedList<JButton> fifoRow = new LinkedList<>();

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",", -1);
            if (data.length == 7) {
                String id = data[0];
                String name = data[1];
                String author = data[2];
                String genre = data[3];
                String type = data[4];
                String status = data[5];
                String filename = data[6];

                ImageIcon icon = loadBookImage(filename);

                JButton bookButton = createBookButton(name, icon);
                setupButtonPosition(bookButton, x, y, fifoRow);

                String finalName = name;
                String finalAuthor = author;
                String finalGenre = genre;
                String finalType = type;
                String finalStatus = status;
                ImageIcon finalIcon = icon;

                bookButton.addActionListener(e -> showBookDetails(
                        finalName, finalAuthor, finalGenre, finalType, finalStatus, finalIcon, id));

                whitePanel.add(bookButton);
                fifoRow.addLast(bookButton);
                whitePanel.revalidate();
                whitePanel.repaint();

                x += 230 + gap;
            }
        }
        reader.close();
    }

    private ImageIcon loadBookImage(String filename) throws IOException {
        File imageFile = new File("bookimages", filename);
        if (imageFile.exists()) {
            Image img = ImageIO.read(imageFile);
            Image scaled = img.getScaledInstance(220, 280, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return new ImageIcon(new BufferedImage(220, 280, BufferedImage.TYPE_INT_RGB));
    }

    private JButton createBookButton(String name, ImageIcon icon) {
        JButton button = new JButton(name, icon);
        button.setPreferredSize(new Dimension(230, 300));
        button.setToolTipText(name);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        return button;
    }

    private void setupButtonPosition(JButton button, int x, int y, LinkedList<JButton> fifoRow) {
        if (fifoRow.size() == 6) {
            JButton oldButton = fifoRow.removeFirst();
            whitePanel.remove(oldButton);
            x = 40;
            for (JButton b : fifoRow) {
                b.setBounds(x, y, 230, 300);
                x += 230 + 10;
            }
        }
        button.setBounds(x, y, 230, 300);
    }

    private void showBookDetails(String name, String author, String genre, String type,
                                 String status, ImageIcon icon, String bookId) {
        JFrame description = new JFrame("Book Details");
        description.setSize(550, 450);
        description.setLayout(null);

        JButton borrowButton = new JButton("Borrow");
        borrowButton.setBounds(410, 320, 100, 40);
        borrowButton.addActionListener(new BorrowButtonListener(bookId, status, description));
        borrowButton.setFocusable(false);

        JLabel bookCover = new JLabel(icon);
        bookCover.setBounds(30, 30, 220, 280);
        bookCover.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        Font font = new Font("ARIAL", Font.BOLD, 16);
        addDetailLabel(description, "Title: " + name, 270, 40, font);
        addDetailLabel(description, "Author: " + author, 270, 70, font);
        addDetailLabel(description, "Genre: " + genre, 270, 100, font);
        addDetailLabel(description, "Type: " + type, 270, 130, font);
        addDetailLabel(description, "Status: " + status, 270, 160, font);

        description.add(borrowButton);
        description.add(bookCover);
        description.setResizable(false);
        description.setLocationRelativeTo(null);
        description.setVisible(true);
    }

    private void addDetailLabel(JFrame frame, String text, int x, int y, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setBounds(x, y, 250, 25);
        frame.add(label);
    }

    private class BorrowButtonListener implements ActionListener {
        private String bookId;
        private String status;
        private JFrame parentFrame;

        public BorrowButtonListener(String bookId, String status, JFrame parentFrame) {
            this.bookId = bookId;
            this.status = status;
            this.parentFrame = parentFrame;
        }

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
                        if (parts.length >= 2 && parts[0].equals(bookId) && parts[1].equals(currentUserId)) {
                            JOptionPane.showMessageDialog(parentFrame,
                                    "You already have this book borrowed.",
                                    "Already Borrowed",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame,
                        "Error checking borrowed books.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ✅ Read book info from books.txt
            try {
                File file = new File("books.txt");
                List<String> lines = java.nio.file.Files.readAllLines(file.toPath());

                for (int i = 0; i < lines.size(); i++) {
                    String[] parts = lines.get(i).split(",", -1);
                    if (parts.length >= 6 && parts[0].equals(bookId)) {
                        bookName = parts[1];
                        isEBook = parts[4].equalsIgnoreCase("eBook");

                        if (parts[5].equalsIgnoreCase("Available")) {
                            isAvailable = true;

                            if (isEBook) {
                                userFullName = getUserFullName(currentUserId);

                                // Set return date to 7 days from now
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(new Date());
                                cal.add(Calendar.DAY_OF_MONTH, 7);
                                String returnDate = cal.getTime().toString();

                                // Write to borrow.txt
                                try (BufferedWriter writer = new BufferedWriter(new FileWriter("borrow.txt", true))) {
                                    writer.write(bookId + "," + currentUserId + "," + bookName + "," + userFullName + "," + borrowDate + "," + returnDate + "\n");
                                }

                                // Update books.txt
                                parts[5] = "Unavailable";
                                lines.set(i, String.join(",", parts));
                                Files.write(file.toPath(), lines);

                                JLabel logol = new JLabel(new ImageIcon("panelimages/co.png"));
                                JLabel message = new JLabel("Book successfully borrowed.");

                                JPanel panel = new JPanel(new BorderLayout(10, 10));
                                panel.add(logol, BorderLayout.WEST);
                                panel.add(message, BorderLayout.CENTER);

                                JOptionPane.showMessageDialog(parentFrame, panel, "Success", JOptionPane.PLAIN_MESSAGE);

                                parentFrame.dispose();
                                updateRequestTable();
                                return;
                            }
                        }
                        break;
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame,
                        "Error reading book availability.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ✅ Handle request (for physical books only)
            if (isAvailable) {
                // Check if already requested
                try {
                    File requestFile = new File("requests.txt");
                    if (requestFile.exists()) {
                        List<String> requestLines = java.nio.file.Files.readAllLines(requestFile.toPath());
                        for (String line : requestLines) {
                            String[] parts = line.split(",", -1);
                            if (parts.length >= 2 && parts[0].equals(currentUserId) && parts[1].equals(bookId)) {
                                JOptionPane.showMessageDialog(parentFrame,
                                        "You already requested this book. Please wait for approval.",
                                        "Request Pending",
                                        JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame,
                            "Error checking existing requests.",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Add to requests.txt
                if (addToBookRequests(currentUserId, bookId)) {
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

                    parentFrame.dispose();
                    updateRequestTable();
                } else {
                    JOptionPane.showMessageDialog(parentFrame,
                            "Failed to submit request",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame,
                        "This book is unavailable (borrowed by someone else).",
                        "Not Available",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        // ✅ Helper method to get user full name from users.txt
        private String getUserFullName(String userId) {
            try {
                List<String> lines = Files.readAllLines(Paths.get("users.txt"));
                for (String line : lines) {
                    String[] parts = line.split(",", -1);
                    if (parts.length >= 4 && parts[2].equals(userId)) {
                        return parts[3];
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Unknown User";
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
                        "File Read Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
}