package ecosystem.ui;

import ecosystem.core.Environment;
import ecosystem.entities.EntityDecorator;
import ecosystem.entities.PoisonedDecorator;
import ecosystem.entities.SpeedBoostDecorator;
import ecosystem.entities.*;

import javax.swing.*;
import java.awt.*;

public class EntityInfoPanel extends JPanel {

    private final JLabel titleLabel;
    private final JTextArea detailsArea;

    // New Fields for Decorators
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

        // Decorator Buttons Initialization
        poisonButton = new JButton("החל רעל");
        speedButton = new JButton("החל האצה");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(poisonButton);
        buttonPanel.add(speedButton);
        add(buttonPanel, BorderLayout.SOUTH);

        poisonButton.addActionListener(e -> applyDecorator(new PoisonedDecorator(currentSelectedEntity)));
        speedButton.addActionListener(e -> applyDecorator(new SpeedBoostDecorator(currentSelectedEntity)));

        clearEntity();
    }

    public void setEnvironment(Environment env) {
        this.environment = env;
    }

    private void applyDecorator(EntityDecorator decorator) {
        if (currentSelectedEntity != null && environment != null) {
            environment.removeEntity(currentSelectedEntity);
            environment.addEntity(decorator);
            displayEntity(decorator); // Refresh UI
        }
    }

    public void displayEntity(AbstractEntity entity) {
        if (entity == null) {
            clearEntity();
            return;
        }

        this.currentSelectedEntity = entity;
        poisonButton.setEnabled(true);
        speedButton.setEnabled(true);

        titleLabel.setText(entity.getClass().getSimpleName());
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(entity.getClass().getSimpleName()).append("\n");
        sb.append("Symbol: ").append(entity.getSymbol()).append("\n");
        sb.append("Position: ").append(entity.getPosition()).append("\n");
        sb.append("Alive: ").append(entity.isAlive()).append("\n");

        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            sb.append(String.format("Energy: %.1f / %.1f%n", living.getEnergy(), living.getMaxEnergy()));
            sb.append("Age: ").append(living.getAge()).append("\n");
        }

        detailsArea.setText(sb.toString());
    }

    public void clearEntity() {
        this.currentSelectedEntity = null;
        poisonButton.setEnabled(false);
        speedButton.setEnabled(false);
        titleLabel.setText("No entity selected");
        detailsArea.setText("Click an entity on the map to view its details.");
    }
}