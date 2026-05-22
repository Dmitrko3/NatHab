package ecosystem.ui;

import ecosystem.entities.*;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Font;

/**
 * Side panel that displays full details about the currently selected entity.
 */
public class EntityInfoPanel extends JPanel {

    private final JLabel titleLabel;
    private final JTextArea detailsArea;

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

        clearEntity();
    }

    /**
     * Displays details for the selected entity.
     *
     * @param entity selected entity
     */
    public void displayEntity(AbstractEntity entity) {
        if (entity == null) {
            clearEntity();
            return;
        }

        titleLabel.setText(entity.getClass().getSimpleName());

        // Base info
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(entity.getClass().getSimpleName()).append("\n");
        sb.append("Symbol: ").append(entity.getSymbol()).append("\n");
        sb.append("Position: ").append(entity.getPosition()).append("\n");
        sb.append("Alive: ").append(entity.isAlive()).append("\n");

        // If it's a living entity (animals/plants), show energy and age
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            sb.append(String.format("Energy: %.1f / %.1f%n", living.getEnergy(), living.getMaxEnergy()));
            sb.append("Age: ").append(living.getAge()).append("\n");
        }

        detailsArea.setText(sb.toString());
    }

    /**
     * Clears the panel when no entity is selected.
     */
    public void clearEntity() {
        titleLabel.setText("No entity selected");
        detailsArea.setText("Click an entity on the map to view its details.");
    }
}