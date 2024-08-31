import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ShotPut {

    public static double SCREEN_SIZE_MULTIPLIER ;
    public static String SAVE_LOCATION;

    public static char SCREENSHOT_KEY;

    private static JFrame buildWindow() throws IOException {
        JFrame frame = new JFrame("ShotPut");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Determine window size and position
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //SCREEN_SIZE_MULTIPLIER = ((screenSize.getWidth()/1920) + (screenSize.getHeight()/1080)) / 2;
        SCREEN_SIZE_MULTIPLIER = 1;

        frame.setSize((int)(480 * SCREEN_SIZE_MULTIPLIER), (int)(480 * SCREEN_SIZE_MULTIPLIER));

        frame.setLocation(0, 0);

        Image iconImage = ImageIO.read(new File("res/icon.jpg"));
        frame.setIconImage(iconImage);

        return frame;
    }

    private static void setSaveLocation() {
        String saveLocation;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            saveLocation = System.getenv("USERPROFILE") + "\\Pictures";
        } else {
           saveLocation = Paths.get(System.getProperty("user.home")) + "\\Pictures";
        }

        SAVE_LOCATION = saveLocation;
    }

    private static void start() throws IOException {
        JFrame window = buildWindow();

        SCREENSHOT_KEY = '\\';
        setSaveLocation();

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // Span across all columns
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("ShotPut", JLabel.CENTER);
        panel.add(titleLabel, gbc);

        // Logo
        gbc.gridy = 1;
        gbc.gridwidth = 3; // Span across all columns
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel logoLabel = new JLabel(new ImageIcon("res/icon.jpg")); // Change path accordingly
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
        keyPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        keyPanel.add(new JLabel("\\"));
        panel.add(keyPanel, gbc);

        gbc.gridx = 2;
        JButton changeKeyButton = new JButton("Change");
        panel.add(changeKeyButton, gbc);

        // Save location
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel saveToLabel = new JLabel("Save screenshots to:");
        panel.add(saveToLabel, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        JLabel savePathLabel = new JLabel(SAVE_LOCATION);
        panel.add(savePathLabel, gbc);

        gbc.gridx = 2;
        JButton changePathButton = new JButton("Change");
        panel.add(changePathButton, gbc);

        window.getContentPane().add(panel);

        window.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        start();
    }

}
