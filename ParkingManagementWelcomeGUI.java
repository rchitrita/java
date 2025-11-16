import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.sql.Timestamp;

public class ParkingManagementWelcomeGUI {
    public static void main(String[] args) {
        new WelcomeScreen();
    }
}

class WelcomeScreen {
    JFrame frame;

    public WelcomeScreen() {
        frame = new JFrame("Welcome - Car Parking Management System");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        ImageIcon backgroundIcon = new ImageIcon("front.jpg");
        JLabel background = new JLabel(backgroundIcon);
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        frame.setContentPane(background);

        JLabel title = new JLabel("Welcome to Car Parking Management System", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.BLACK);
        title.setBorder(BorderFactory.createEmptyBorder(70, 10, 20, 10));

        JButton continueButton = new JButton("Continue");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setFont(new Font("Arial", Font.BOLD, 20));
        continueButton.setBackground(new Color(0, 102, 204));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);

        continueButton.addActionListener(e -> {
            frame.dispose();
            new TollGateScreen();
        });

        background.add(Box.createRigidArea(new Dimension(0, 100)));
        background.add(title);
        background.add(Box.createRigidArea(new Dimension(0, 200)));
        background.add(continueButton);

        frame.setVisible(true);
    }
}

class TollGateScreen {
    JFrame tollFrame;

    public TollGateScreen() {
        tollFrame = new JFrame("Toll Gate");
        tollFrame.setSize(960, 600);
        tollFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tollFrame.setLocationRelativeTo(null);

        TollGatePage panel = new TollGatePage(tollFrame);
        tollFrame.add(panel);
        tollFrame.setVisible(true);
    }
}

class TollGatePage extends JPanel {
    Timer timer;
    int vehicleX = 0;
    boolean gateOpen = false;
    boolean gateOpening = false;
    Image backgroundImage;
    JFrame parentFrame;
    String selectedVehicleType;
    String vehicleNumber;
    Random random = new Random();

    public TollGatePage(JFrame frame) {
        this.parentFrame = frame;
        backgroundImage = new ImageIcon("se.jpg").getImage();
        setLayout(null);

        selectedVehicleType = random.nextBoolean() ? "Car" : "Bike";

        timer = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!gateOpening && vehicleX + 50 >= 370) {
                    gateOpening = true;
                    timer.stop();

                    SwingUtilities.invokeLater(() -> {
                        vehicleNumber = generateRandomVehicleNumber();
                        saveVehicleInfo(vehicleNumber, selectedVehicleType);

                        new Timer(1000, evt2 -> {
                            gateOpen = true;
                            timer.start();
                            ((Timer) evt2.getSource()).stop();
                        }).start();
                    });
                }

                if (gateOpen || vehicleX + 50 < 370) {
                    vehicleX += 4;
                }

                if (vehicleX > getWidth()) {
                    timer.stop();
                    parentFrame.dispose();
                    new ParkingLotScreen(vehicleNumber, selectedVehicleType);
                }

                repaint();
            }
        });
        timer.start();
    }

    private String generateRandomVehicleNumber() {
        String states = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String randomState = "" + states.charAt(random.nextInt(26)) + states.charAt(random.nextInt(26));
        int district = random.nextInt(90) + 10;
        String randomAlpha = "" + states.charAt(random.nextInt(26)) + states.charAt(random.nextInt(26));
        int number = random.nextInt(9000) + 1000;
        return randomState + district + randomAlpha + number;
    }

    private void saveVehicleInfo(String vehicleNumber, String vehicleType) {
        String url = "jdbc:mysql://localhost:3306/parking";
        String user = "root";
        String password = "chithu";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            String sql = "INSERT INTO vehicles (vehicle_number, vehicle_type, in_time, status) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vehicleNumber);
            pstmt.setString(2, vehicleType);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, "Entered");
            pstmt.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.GRAY);
        g.fillRect(0, 330, getWidth(), 70);
        g.setColor(Color.WHITE);
        for (int i = 0; i < getWidth(); i += 40) {
            g.fillRect(i, 365, 20, 5);
        }
        g.setColor(Color.DARK_GRAY);
        g.fillRect(370, 300, 100, 10);
        g.setColor(Color.GREEN);
        if (!gateOpen) {
            g.fillRect(415, 270, 10, 30);
        } else {
            g.fillRect(415, 240, 10, 5);
        }
        if ("Car".equals(selectedVehicleType)) {
            drawCar(g, vehicleX, 340);
        } else {
            drawBike(g, vehicleX, 360);
        }
    }

    private void drawCar(Graphics g, int x, int y) {
        g.setColor(new Color(0, 102, 204));
        g.fillRoundRect(x, y + 30, 120, 30, 20, 20);
        g.setColor(new Color(51, 204, 255));
        Polygon roof = new Polygon();
        roof.addPoint(x + 20, y + 30);
        roof.addPoint(x + 40, y + 10);
        roof.addPoint(x + 80, y + 10);
        roof.addPoint(x + 100, y + 30);
        g.fillPolygon(roof);
        g.setColor(Color.BLACK);
        g.fillOval(x + 15, y + 55, 20, 20);
        g.fillOval(x + 85, y + 55, 20, 20);
    }

    private void drawBike(Graphics g, int x, int y) {
        g.setColor(Color.ORANGE);
        g.fillRect(x, y, 60, 20);
        g.setColor(Color.BLACK);
        g.fillOval(x - 5, y + 15, 20, 20);
        g.fillOval(x + 45, y + 15, 20, 20);
    }
}

class ParkingLotScreen {
    JFrame parkingFrame;

    public ParkingLotScreen(String vehicleNumber, String vehicleType) {
        if (vehicleType.equals("Car")) {
            parkingFrame = new JFrame("Car Parking Lot");
            CarParkingLot carLot = new CarParkingLot(vehicleNumber);
            setupParkingFrame(parkingFrame, carLot);
        } else {
            parkingFrame = new JFrame("Bike Parking Lot");
            BikeParkingLot bikeLot = new BikeParkingLot(vehicleNumber);
            setupParkingFrame(parkingFrame, bikeLot);
        }
    }

    private void setupParkingFrame(JFrame frame, JPanel panel) {
        frame.add(panel);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(0, 102, 204));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            frame.dispose();
            new TollGateScreen();
        });

        panel.add(backButton);
        backButton.setBounds(10, 10, 100, 40);

        JButton exitButton = new JButton("Exit Vehicle");
        exitButton.setFont(new Font("Arial", Font.BOLD, 16));
        exitButton.setBackground(new Color(204, 0, 0));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        if (panel instanceof CarParkingLot) {
            exitButton.addActionListener(e -> ((CarParkingLot) panel).exitVehicle());
        } else if (panel instanceof BikeParkingLot) {
            exitButton.addActionListener(e -> ((BikeParkingLot) panel).exitVehicle());
        }

        panel.add(exitButton);
        exitButton.setBounds(120, 10, 150, 40);

        frame.setSize(1000, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class CarParkingLot extends JPanel {
    static final int TOTAL_SLOTS = 10;
    static boolean[] occupiedSlots = new boolean[TOTAL_SLOTS];
    static ArrayList<String> parkedVehicles = new ArrayList<>();
    String incomingVehicleNumber;
    int assignedSlot = -1;

    public CarParkingLot(String vehicleNumber) {
        this.incomingVehicleNumber = vehicleNumber;
        assignSlot();
        repaint();
    }

    private void assignSlot() {
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            if (!occupiedSlots[i]) {
                occupiedSlots[i] = true;
                parkedVehicles.add(incomingVehicleNumber);
                assignedSlot = i;
                break;
            }
        }
        if (assignedSlot == -1) {
            JOptionPane.showMessageDialog(this, "Car Parking Full!");
        }
    }

    public void exitVehicle() {
        String slotInput = JOptionPane.showInputDialog(this, "Enter Car Slot Number to Exit (1 to " + TOTAL_SLOTS + "):");
        try {
            int slotNumber = Integer.parseInt(slotInput) - 1;
            if (slotNumber >= 0 && slotNumber < TOTAL_SLOTS && occupiedSlots[slotNumber]) {
                occupiedSlots[slotNumber] = false;
                parkedVehicles.remove(slotNumber);
                JOptionPane.showMessageDialog(this, "Car exited from Slot " + (slotNumber + 1));
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Slot Number or Slot is Empty");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid slot number.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(200, 230, 255));
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        int slotWidth = 100, slotHeight = 150, gap = 20, startX = 50, startY = 100;
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            int x = startX + (i % 5) * (slotWidth + gap);
            int y = startY + (i / 5) * (slotHeight + gap);
            g.setColor(occupiedSlots[i] ? Color.RED : Color.WHITE);
            g.fillRect(x, y, slotWidth, slotHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, slotWidth, slotHeight);
            g.drawString("Car Slot " + (i + 1), x + 20, y + 75);
        }
    }
}

class BikeParkingLot extends JPanel {
    static final int TOTAL_SLOTS = 10;
    static boolean[] occupiedSlots = new boolean[TOTAL_SLOTS];
    static ArrayList<String> parkedVehicles = new ArrayList<>();
    String incomingVehicleNumber;
    int assignedSlot = -1;

    public BikeParkingLot(String vehicleNumber) {
        this.incomingVehicleNumber = vehicleNumber;
        assignSlot();
        repaint();
    }

    private void assignSlot() {
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            if (!occupiedSlots[i]) {
                occupiedSlots[i] = true;
                parkedVehicles.add(incomingVehicleNumber);
                assignedSlot = i;
                break;
            }
        }
        if (assignedSlot == -1) {
            JOptionPane.showMessageDialog(this, "Bike Parking Full!");
        }
    }

    public void exitVehicle() {
        String slotInput = JOptionPane.showInputDialog(this, "Enter Bike Slot Number to Exit (1 to " + TOTAL_SLOTS + "):");
        try {
            int slotNumber = Integer.parseInt(slotInput) - 1;
            if (slotNumber >= 0 && slotNumber < TOTAL_SLOTS && occupiedSlots[slotNumber]) {
                occupiedSlots[slotNumber] = false;
                parkedVehicles.remove(slotNumber);
                JOptionPane.showMessageDialog(this, "Bike exited from Slot " + (slotNumber + 1));
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Slot Number or Slot is Empty");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid slot number.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(255, 240, 200));
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3));
        int slotWidth = 80, slotHeight = 100, gap = 20, startX = 50, startY = 100;
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            int x = startX + (i % 5) * (slotWidth + gap);
            int y = startY + (i / 5) * (slotHeight + gap);
            g.setColor(occupiedSlots[i] ? Color.RED : Color.WHITE);
            g.fillRect(x, y, slotWidth, slotHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, slotWidth, slotHeight);
            g.drawString("Bike Slot " + (i + 1), x + 10, y + 50);
        }
    }
}
