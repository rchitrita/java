import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TollGatePage extends JPanel {
    ParkingSystem frame;
    static String vehicleNumber;
    static String vehicleType;
    Timer timer;
    int x = 0;
    boolean gateOpen = false;

    public TollGatePage(ParkingSystem frame) {
        this.frame = frame;
        setLayout(null);

        timer = new Timer(20, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                x += 4;
                if (x >= 340 && x <= 350) {
                    gateOpen = true;
                }
                if (x > 700) {
                    timer.stop();
                    frame.showPage("ParkingLotPage");
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(Color.GRAY);
        g.fillRect(0, 240, getWidth(), 100);

        // Road stripes
        g.setColor(Color.WHITE);
        for (int i = 0; i < getWidth(); i += 50) {
            g.fillRect(i, 285, 30, 5);
        }

        // Toll Gate
        g.setColor(Color.DARK_GRAY);
        g.fillRect(350, 220, 100, 10); // Toll Bar

        if (!gateOpen) {
            g.setColor(Color.GREEN);
            g.fillRect(390, 200, 10, 30);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(390, 170, 10, 5);
        }

        // Draw vehicle
        if (vehicleType.equalsIgnoreCase("Car")) {
            g.setColor(Color.BLUE);
            g.fillRect(x, 270, 50, 30);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, 275, 40, 20);
        }
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            x = 0;
            gateOpen = false;
            timer.start();
        } else {
            timer.stop();
        }
    }
}