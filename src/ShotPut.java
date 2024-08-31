import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ShotPut {

    public static double SCREEN_SIZE_MULTIPLIER;
    public static String SAVE_LOCATION;
    public static String SCREENSHOT_KEY;

    private static boolean isPaused = true;
    private static KeyAdapter keyAdapter;
    private static int screenshotCount = 0;
    private static JLabel countLabel;

    private static JFrame buildWindow() throws IOException {
        JFrame frame = new JFrame("ShotPut");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Determine window size and position
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //SCREEN_SIZE_MULTIPLIER = ((screenSize.getWidth()/1920) + (screenSize.getHeight()/1080)) / 2;
        SCREEN_SIZE_MULTIPLIER = 1;

        frame.setSize((int) (480 * SCREEN_SIZE_MULTIPLIER), (int) (512 * SCREEN_SIZE_MULTIPLIER));
        frame.setResizable(false);

        frame.setLocation(0, 0);

        Image iconImage = ImageIO.read(new File("res/icon.jpg"));
        frame.setIconImage(iconImage);

        return frame;
    }

    private static void setSaveLocation() {
        String saveLocation;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            saveLocation = System.getenv("USERPROFILE") + "\\Pictures\\ShotPut Captures";
        } else {
            saveLocation = Paths.get(System.getProperty("user.home")) + "/Pictures/ShotPut Captures";
        }

        SAVE_LOCATION = saveLocation;
    }

    private static void start() throws IOException {
        JFrame window = buildWindow();

        SCREENSHOT_KEY = "Back Slash"; // Initial screenshot key
        setSaveLocation();

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 5, 5, 5);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("ShotPut", JLabel.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 24));
        panel.add(titleLabel, gbc);

        // Logo
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel logoLabel = new JLabel(new ImageIcon("res/icon.jpg"));
        panel.add(logoLabel, gbc);

        // Screenshot key
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel screenshotKeyLabel = new JLabel("Screenshot key:");
        panel.add(screenshotKeyLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel keyPanel = new JPanel();
        JLabel keyLabel = new JLabel(SCREENSHOT_KEY);
        keyPanel.add(keyLabel);
        panel.add(keyPanel, gbc);

        gbc.gridx = 2;
        JButton changeKeyButton = new JButton("Change");
        panel.add(changeKeyButton, gbc);

        // Action listener for changing screenshot key button
        changeKeyButton.addActionListener(e -> setScreenshotKey(keyLabel, window));

        // Save location
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel saveToLabel = new JLabel("Output folder:");
        panel.add(saveToLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        JTextField savePathField = new JTextField(SAVE_LOCATION, 25);
        panel.add(savePathField, gbc);

        gbc.gridx = 2;
        JButton changePathButton = new JButton("Change");
        panel.add(changePathButton, gbc);

        // Action listener for change save path button press
        changePathButton.addActionListener(e -> {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            folderChooser.setDialogTitle("Select Output Folder");

            int userSelection = folderChooser.showOpenDialog(window);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFolder = folderChooser.getSelectedFile();
                SAVE_LOCATION = selectedFolder.getAbsolutePath();
                savePathField.setText(SAVE_LOCATION);
            }
        });

        // Pause/Play section
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel pausePlayPanel = new JPanel();
        JLabel pausePlayLabel = new JLabel("ShotPut is paused");
        JButton pausePlayButton = new JButton("▶");
        pausePlayButton.setPreferredSize(new Dimension(50, 50));
        pausePlayButton.addActionListener(e -> togglePausePlay(pausePlayLabel, pausePlayButton, window));
        pausePlayPanel.add(pausePlayLabel);
        pausePlayPanel.add(pausePlayButton);
        panel.add(pausePlayPanel, gbc);

        // Screenshot count
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        countLabel = new JLabel("Session screenshots: 0");
        countLabel.setFont(new Font(countLabel.getFont().getName(), Font.PLAIN, 12));
        panel.add(countLabel, gbc);

        window.getContentPane().add(panel);
        window.setVisible(true);
    }

    private static void togglePausePlay(JLabel pausePlayLabel, JButton pausePlayButton, JFrame window) {
        isPaused = !isPaused;
        if (isPaused) {
            pausePlayLabel.setText("ShotPut is paused");
            pausePlayButton.setText("▶");
            window.removeKeyListener(keyAdapter);
        } else {
            pausePlayLabel.setText("ShotPut is running");
            pausePlayButton.setText("| |");
            keyAdapter = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (KeyEvent.getKeyText(e.getKeyCode()).equals(SCREENSHOT_KEY)) {
                        screenshotCount++;
                        countLabel.setText("Session screenshots: " + screenshotCount);
                        System.out.println("Screenshot key pressed");
                    }
                }
            };
            window.addKeyListener(keyAdapter);
            window.requestFocus();
        }
    }

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

    public static void main(String[] args) throws IOException {
        start();
    }
}
