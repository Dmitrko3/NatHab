package ecosystem.ui;

import ecosystem.controller.SimulationController;
import ecosystem.engine.Environment;
import ecosystem.entities.decorators.EntityDecorator;
import ecosystem.entities.decorators.PoisonedDecorator;
import ecosystem.entities.decorators.SpeedBoostDecorator;
import ecosystem.entities.base.AbstractEntity;

import javax.swing.*;
import java.awt.*;

/**
 * UI panel displaying entity details and controls for Decorator effects and sending entities.
 */
public class EntityInfoPanel extends JPanel {

    private final JLabel titleLabel;
    private final JTextArea detailsArea;

    private final JButton poisonButton;
    private final JButton speedButton;

    private AbstractEntity currentSelectedEntity;
    private Environment environment;

    private final JTextField ipField;
    private final JButton sendButton;
    private SimulationController controller;

    public EntityInfoPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Selected Entity"));

        titleLabel = new JLabel("No entity selected");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        add(titleLabel, BorderLayout.NORTH);
        add(detailsArea, BorderLayout.CENTER);

        poisonButton = new JButton("Start Poison");
        speedButton = new JButton("Start Speed");

        // Prepare send controls
        ipField = new JTextField(12);
        ipField.setToolTipText("Target IP address (e.g. 192.168.1.42)");
        sendButton = new JButton("Send to Portal");

        // Build the combined bottom panel (decorators on left, send on right)
        JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sendPanel.add(new JLabel("Target IP:"));
        sendPanel.add(ipField);
        sendPanel.add(sendButton);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel decoratorsPanel = new JPanel();
        decoratorsPanel.add(poisonButton);
        decoratorsPanel.add(speedButton);
        buttonPanel.add(decoratorsPanel, BorderLayout.WEST);
        buttonPanel.add(sendPanel, BorderLayout.EAST);

        add(buttonPanel, BorderLayout.SOUTH);

        // Register decorator listeners (single registration)
        poisonButton.addActionListener(e -> {
            if (currentSelectedEntity != null) {
                applyDecorator(new PoisonedDecorator(currentSelectedEntity));
            }
        });
        speedButton.addActionListener(e -> {
            if (currentSelectedEntity != null) {
                applyDecorator(new SpeedBoostDecorator(currentSelectedEntity));
            }
        });

        // Send button action: delegate to controller (do not block EDT)
        sendButton.addActionListener(e -> {
            if (currentSelectedEntity == null) return;
            if (controller == null) {
                JOptionPane.showMessageDialog(this, "No controller available", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String ip = ipField.getText();
            if (ip == null || ip.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a target IP address", "Input required", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Disable send button briefly to avoid double-clicks; controller will enqueue removal
            sendButton.setEnabled(false);

            // Delegate to controller (controller should perform network I/O off the EDT)
            controller.sendEntityToPortal(ip.trim(), currentSelectedEntity);

            // Optimistically clear selection in UI (controller will remove entity on engine thread)
            clearEntity();
        });

        clearEntity();
    }

    public void setController(SimulationController controller) {
        this.controller = controller;
    }

    /** @param env The simulation environment. */
    public void setEnvironment(Environment env) {
        this.environment = env;
    }

    /** @param decorator The effect to apply. */
    private void applyDecorator(EntityDecorator decorator) {
        if (currentSelectedEntity != null && environment != null && decorator != null) {
            environment.removeEntity(currentSelectedEntity);
            environment.addEntity(decorator);
            displayEntity(decorator); // Refresh UI
        }
    }

    /** @param entity Entity to display. */
    public void displayEntity(AbstractEntity entity) {
        if (entity == null) {
            clearEntity();
            return;
        }

        this.currentSelectedEntity = entity;

        // Determine whether the entity uses energy by checking max energy > 0
        boolean usesEnergy = entity.getMaxEnergy() > 0.0;
        poisonButton.setEnabled(usesEnergy);
        speedButton.setEnabled(true);

        titleLabel.setText(entity.getClass().getSimpleName());
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(entity.getClass().getSimpleName()).append("\n");
        sb.append("Symbol: ").append(entity.getSymbol()).append("\n");
        sb.append("Position: ").append(entity.getPosition()).append("\n");
        sb.append("Alive: ").append(entity.isAlive()).append("\n");

        if (usesEnergy) {
            sb.append(String.format("Energy: %.1f / %.1f%n", entity.getEnergy(), entity.getMaxEnergy()));
            sb.append("Age: ").append(entity.getAge()).append("\n");
        }

        detailsArea.setText(sb.toString());
        sendButton.setEnabled(true);
    }

    /** Clears panel information. */
    public void clearEntity() {
        this.currentSelectedEntity = null;
        poisonButton.setEnabled(false);
        speedButton.setEnabled(false);
        titleLabel.setText("No entity selected");
        detailsArea.setText("Click an entity on the map to view its details.");
        sendButton.setEnabled(false);
    }

}