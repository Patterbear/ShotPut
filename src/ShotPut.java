import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class ShotPut {

    public static double SCREEN_SIZE_MULTIPLIER;
    public static String SAVE_LOCATION;
    public static String SCREENSHOT_KEY;

    private static boolean isPaused = true;
    private static KeyAdapter keyAdapter;
    private static int screenshotCount = 0;
    private static JLabel countLabel;

    // Build window function
    private static JFrame buildWindow() {
        JFrame frame = new JFrame("ShotPut");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_SIZE_MULTIPLIER = ((screenSize.getWidth() / 1920) + (screenSize.getHeight() / 1080)) / 2;

        frame.setSize((int) (520 * SCREEN_SIZE_MULTIPLIER), (int) (520 * SCREEN_SIZE_MULTIPLIER));
        frame.setResizable(false);
        frame.setLocation(0, 0);

        Image iconImage = new ImageIcon(Objects.requireNonNull(ShotPut.class.getResource("/icon.jpg"))).getImage();
        frame.setIconImage(iconImage);

        return frame;
    }

    // Set save location function (default)
    private static void setSaveLocation() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            SAVE_LOCATION = System.getenv("USERPROFILE") + "\\Pictures\\ShotPut Captures";
        } else {
            SAVE_LOCATION = Paths.get(System.getProperty("user.home")).resolve("Pictures/ShotPut Captures").toString();
        }
    }

    // Start method
    private static void start() {
        JFrame window = buildWindow();

        SCREENSHOT_KEY = "Back Slash";
        setSaveLocation();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets((int) (10 * SCREEN_SIZE_MULTIPLIER), (int) (10 * SCREEN_SIZE_MULTIPLIER), (int) (10 * SCREEN_SIZE_MULTIPLIER), (int) (10 * SCREEN_SIZE_MULTIPLIER));

        // Title
        Font titleFont = new Font("Arial", Font.BOLD, (int) (24 * SCREEN_SIZE_MULTIPLIER));
        JLabel titleLabel = new JLabel("ShotPut", JLabel.CENTER);
        titleLabel.setFont(titleFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Logo
        ImageIcon logoIcon = scaleImageIcon();
        JLabel logoLabel = new JLabel(logoIcon);

        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(logoLabel, gbc);

        // Screenshot key
        Font labelFont = new Font("Arial", Font.PLAIN, (int) (16 * SCREEN_SIZE_MULTIPLIER));
        JLabel screenshotKeyLabel = new JLabel("Screenshot key:");
        screenshotKeyLabel.setFont(labelFont);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(screenshotKeyLabel, gbc);

        JPanel keyPanel = new JPanel();
        JLabel keyLabel = new JLabel(SCREENSHOT_KEY);
        keyLabel.setFont(labelFont);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        keyPanel.add(keyLabel);
        panel.add(keyPanel, gbc);

        // Change key button
        JButton changeKeyButton = new JButton("Change");
        changeKeyButton.setPreferredSize(new Dimension((int) (120 * SCREEN_SIZE_MULTIPLIER), (int) (40 * SCREEN_SIZE_MULTIPLIER)));
        changeKeyButton.setFont(labelFont);
        changeKeyButton.addActionListener(e -> setScreenshotKey(keyLabel, window));

        gbc.gridx = 2;
        panel.add(changeKeyButton, gbc);

        // Output folder
        JLabel saveToLabel = new JLabel("Output folder:");
        saveToLabel.setFont(labelFont);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(saveToLabel, gbc);

        JTextField savePathField = new JTextField(SAVE_LOCATION, 25);
        savePathField.setFont(labelFont);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        panel.add(savePathField, gbc);

        // Change path button
        JButton changePathButton = new JButton("Change");
        changePathButton.setPreferredSize(new Dimension((int) (120 * SCREEN_SIZE_MULTIPLIER), (int) (40 * SCREEN_SIZE_MULTIPLIER)));
        changePathButton.setFont(labelFont);
        changePathButton.addActionListener(e -> selectOutputFolder(window, savePathField));

        gbc.gridx = 2;
        panel.add(changePathButton, gbc);

        // Play/pause button
        JButton pausePlayButton = new JButton("Start");
        pausePlayButton.setPreferredSize(new Dimension((int) (120 * SCREEN_SIZE_MULTIPLIER), (int) (40 * SCREEN_SIZE_MULTIPLIER)));
        Font pausePlayButtonFont = new Font("Arial", Font.BOLD, (int) (18 * SCREEN_SIZE_MULTIPLIER));
        pausePlayButton.setFont(pausePlayButtonFont);
        pausePlayButton.addActionListener(e -> togglePausePlay(pausePlayButton, window));

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(pausePlayButton, gbc);

        // Session screenshot count
        countLabel = new JLabel("Screenshots taken: 0");
        countLabel.setFont(new Font("Arial", Font.PLAIN, (int) (16 * SCREEN_SIZE_MULTIPLIER)));

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(countLabel, gbc);

        // Add panel
        window.getContentPane().add(panel);
        window.setVisible(true);
    }

    // Pause/start function
    private static void togglePausePlay(JButton pausePlayButton, JFrame window) {
        isPaused = !isPaused;
        if (isPaused) {
            pausePlayButton.setText("Start");
            window.removeKeyListener(keyAdapter);
        } else {
            pausePlayButton.setText("Stop");
            keyAdapter = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (KeyEvent.getKeyText(e.getKeyCode()).equals(SCREENSHOT_KEY)) {
                        screenshotCount++;
                        countLabel.setText("Screenshots taken: " + screenshotCount);
                        try {
                            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                            BufferedImage screenFullImage = new Robot().createScreenCapture(screenRect);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            String timestamp = sdf.format(new Date());
                            Path filePath = Paths.get(SAVE_LOCATION, "screenshot_" + timestamp + ".png");
                            Files.createDirectories(filePath.getParent());
                            ImageIO.write(screenFullImage, "png", filePath.toFile());
                        } catch (Exception ex) {
                            System.out.println("Screenshot failed.");
                        }
                    }
                }
            };
            window.addKeyListener(keyAdapter);
            window.requestFocus();
        }
    }

    // Set screenshot key function
    private static void setScreenshotKey(JLabel keyLabel, JFrame window) {
        keyLabel.setText("press key to set");
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                SCREENSHOT_KEY = KeyEvent.getKeyText(e.getKeyCode());
                keyLabel.setText(SCREENSHOT_KEY);
                window.removeKeyListener(this);
            }
        };

        window.addKeyListener(keyAdapter);
        window.requestFocus();
    }

    // Select output folder function
    private static void selectOutputFolder(JFrame window, JTextField savePathField) {
        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderChooser.setDialogTitle("Select Output Folder");

        int userSelection = folderChooser.showOpenDialog(window);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folderChooser.getSelectedFile();
            SAVE_LOCATION = selectedFolder.getAbsolutePath();
            savePathField.setText(SAVE_LOCATION);

            Path path = Paths.get(SAVE_LOCATION);
            if (Files.notExists(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException ex) {
                    System.out.println("Cannot create folder.");
                }
            }
        }
    }

    // Scale logo function
    private static ImageIcon scaleImageIcon() {
        Image img = new ImageIcon(Objects.requireNonNull(ShotPut.class.getResource("/icon.jpg"))).getImage();
        int width = (int) (img.getWidth(null) * SCREEN_SIZE_MULTIPLIER);
        int height = (int) (img.getHeight(null) * SCREEN_SIZE_MULTIPLIER);
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // Main method
    public static void main(String[] args) {
        start();
    }
}
