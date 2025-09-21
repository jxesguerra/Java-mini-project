import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class HistoryPanel extends JPanel {
    private JPanel mainPanel;
    private String currentUserID;

    private static final String HISTORY_FILE = "history.txt";
    private static final String BOOK_FILE = "books.txt";

    public HistoryPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Create a wrapper panel to add top padding
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(245, 245, 245));
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        add(wrapperPanel, BorderLayout.CENTER);
    }

    public void setUserID(String userID) {
        this.currentUserID = userID;
        loadUserHistory();
    }

    private void loadUserHistory() {
        mainPanel.removeAll();
        Map<String, List<String[]>> booksByReturnDate = new TreeMap<>(Collections.reverseOrder());

        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 6 && data[1].equals(currentUserID)) {
                    String returnDate = data[5];
                    booksByReturnDate.computeIfAbsent(returnDate, k -> new ArrayList<>()).add(data);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading history: " + e.getMessage());
        }

        Map<String, String> bookImages = loadBookImages();

        if (booksByReturnDate.isEmpty()) {
            JLabel emptyLabel = new JLabel("No return history found", JLabel.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(Box.createVerticalGlue());
            mainPanel.add(emptyLabel);
            mainPanel.add(Box.createVerticalGlue());
        } else {
            for (Map.Entry<String, List<String[]>> entry : booksByReturnDate.entrySet()) {
                JLabel dateLabel = new JLabel("Returned on: " + entry.getKey(), JLabel.LEFT);
                dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                dateLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
                mainPanel.add(dateLabel);

                // Add separator line
                JSeparator separator = new JSeparator();
                separator.setForeground(new Color(220, 220, 220));
                mainPanel.add(separator);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

                for (String[] bookData : entry.getValue()) {
                    String bookName = bookData[2];
                    String bookType = findBookType(bookData[2]); // bookData[2] is bookName
                    String returnDate = bookData[5];
                    String imagePath = bookImages.get(bookName);
                    JPanel bookEntry = createBookEntry(bookName, bookType, returnDate, imagePath);
                    mainPanel.add(bookEntry);
                    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }
    private Map<String, String> loadBookImages() {
        Map<String, String> map = new HashMap<>();
        String imageFolderPath = "bookimages";

        try (BufferedReader reader = new BufferedReader(new FileReader(BOOK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    String bookName = data[1].trim();
                    String imagePath = imageFolderPath + File.separator + data[6].trim();
                    map.put(bookName, imagePath);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading books.txt: " + e.getMessage());
        }
        return map;
    }
    private JPanel createBookEntry(String bookName, String type, String returnDate, String imagePath) {
        JPanel entryPanel = new JPanel(new BorderLayout(15, 0));
        entryPanel.setBackground(Color.WHITE);
        entryPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        // Image panel
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setPreferredSize(new Dimension(80, 100));

        JLabel imageLabel;
        if (imagePath != null && new File(imagePath).exists()) {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(img));
        } else {
            imageLabel = new JLabel("No Image", SwingConstants.CENTER);
            imageLabel.setPreferredSize(new Dimension(80, 100));
            imageLabel.setOpaque(true);
            imageLabel.setBackground(new Color(240, 240, 240));
            imageLabel.setForeground(Color.GRAY);
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        }
        imagePanel.add(imageLabel);
        entryPanel.add(imagePanel, BorderLayout.WEST);

        // Text panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        // Book name
        JLabel nameLabel = new JLabel(bookName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        textPanel.add(nameLabel);

        // Type
        JLabel typeLabel = new JLabel("Type: " + type);
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeLabel.setForeground(new Color(100, 100, 100));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(typeLabel);

        // Return date
        JLabel dateLabel = new JLabel("Returned: " + returnDate);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(100, 100, 100));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(dateLabel);

        entryPanel.add(textPanel, BorderLayout.CENTER);

        return entryPanel;
    }
    public static void addReturnRecord(String bookID, String userID, String bookName,
                                       String bookType, String borrowDate, String returnDate) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            writer.write(String.join(",", bookID, userID, bookName, bookType, borrowDate, returnDate));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String findBookType(String bookName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOK_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 5 && data[1].trim().equals(bookName.trim())) {
                    return data[4].trim(); // This is the Book Type
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading books.txt: " + e.getMessage());
        }
        return "Unknown";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("User Return History");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            HistoryPanel panel = new HistoryPanel();
            panel.setUserID("1001");
            frame.setContentPane(panel);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}