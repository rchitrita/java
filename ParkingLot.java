import javax.swing.*;
import java.awt.*;

public class ParkingLot extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set background
        setBackground(new Color(180, 180, 180));

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3)); // Line thickness

        int slotWidth = 100;
        int slotHeight = 150;
        int gap = 20;
        int startX = 50;
        int startY = 50;

        // Draw only empty parking slots (no cars)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 7; col++) {
                int x = startX + col * (slotWidth + gap);
                int y = startY + row * (slotHeight + gap);

                // Draw parking slot rectangle
                g2d.setColor(Color.WHITE);
                g2d.drawRect(x, y, slotWidth, slotHeight);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Parking Lot View");
        ParkingLot parkingLot = new ParkingLot();
        frame.add(parkingLot);
        frame.setSize(1000, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
