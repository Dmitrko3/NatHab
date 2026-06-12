package ecosystem.ui;

import ecosystem.core.Environment;
import ecosystem.entities.EntityDecorator;
import ecosystem.entities.PoisonedDecorator;
import ecosystem.entities.SpeedBoostDecorator;
import ecosystem.entities.AbstractEntity;

import javax.swing.*;
import java.awt.*;
/**
 * UI panel displaying entity details and controls for Decorator effects.
 */
public class EntityInfoPanel extends JPanel {

    private final JLabel titleLabel;
    private final JTextArea detailsArea;

    private final JButton poisonButton;
    private final JButton speedButton;
    private AbstractEntity currentSelectedEntity;
    private Environment environment;

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

        poisonButton = new JButton("החל רעל");
        speedButton = new JButton("החל האצה");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(poisonButton);
        buttonPanel.add(speedButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Build decorators using AbstractEntity directly
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

        clearEntity();
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
    }
    /** Clears panel information. */
    public void clearEntity() {
        this.currentSelectedEntity = null;
        poisonButton.setEnabled(false);
        speedButton.setEnabled(false);
        titleLabel.setText("No entity selected");
        detailsArea.setText("Click an entity on the map to view its details.");
    }
}