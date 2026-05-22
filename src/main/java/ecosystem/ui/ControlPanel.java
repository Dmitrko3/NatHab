package ecosystem.ui;

import ecosystem.controller.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.FlowLayout;
import java.awt.Window;

/**
 * Bottom panel containing the main simulation control buttons.
 */
public class ControlPanel extends JPanel {

    private final SimulationController controller;

    private final JButton tickButton;
    private final JButton runButton;
    private final JButton stopButton;
    private final JButton resetButton;
    private final JButton addEntityButton;

    public ControlPanel(SimulationController controller) {
        this.controller = controller;

        setBorder(BorderFactory.createTitledBorder("Controls"));
        setLayout(new FlowLayout(FlowLayout.CENTER));

        tickButton = new JButton("Tick");
        runButton = new JButton("Run");
        stopButton = new JButton("Stop");
        resetButton = new JButton("Reset");
        addEntityButton = new JButton("Add Entity");

        add(tickButton);
        add(runButton);
        add(stopButton);
        add(resetButton);
        add(addEntityButton);

        wireButtonActions();
    }


    /**
     * Connects button clicks to controller actions.
     */
    private void wireButtonActions() {
        tickButton.addActionListener(event -> controller.executeSingleTick());

        runButton.addActionListener(event -> controller.startContinuousRun(200));

        stopButton.addActionListener(event -> controller.stopContinuousRun());

        resetButton.addActionListener(event -> {
            // Reset the simulation to an empty state
            controller.resetSimulation();
        });

        addEntityButton.addActionListener(event -> {
            Window window = SwingUtilities.getWindowAncestor(this);

            if (window instanceof javax.swing.JFrame) {
                AddEntityDialog dialog = new AddEntityDialog(
                        (javax.swing.JFrame) window,
                        controller
                );
                dialog.setVisible(true);
            }
        });
    }

    public JButton getTickButton() {
        return tickButton;
    }

    public JButton getRunButton() {
        return runButton;
    }

    public JButton getStopButton() {
        return stopButton;
    }

    public JButton getResetButton() {
        return resetButton;
    }

    public JButton getAddEntityButton() {
        return addEntityButton;
    }
}