package ecosystem.ui;

import ecosystem.controller.SimulationController;
import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.entities.AbstractEntity;
import ecosystem.entities.LivingEntity;
import ecosystem.entities.animals.Deer;
import ecosystem.entities.animals.Lion;
import ecosystem.entities.animals.Rabbit;
import ecosystem.entities.plants.Flower;
import ecosystem.entities.plants.Tree;
import ecosystem.entities.resources.Rock;
import ecosystem.entities.resources.Water;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/**
 * Dialog window for adding a new entity to the simulation.
 *
 * Includes validation for entity type, coordinates, and starting energy.
 */
public class AddEntityDialog extends JDialog {

    private final SimulationController controller;

    private JComboBox<String> entityTypeBox;
    private JTextField xField;
    private JTextField yField;
    private JTextField energyField;

    private JButton addButton;
    private JButton cancelButton;

    public AddEntityDialog(JFrame owner, SimulationController controller) {
        super(owner, "Add Entity", true);
        this.controller = controller;

        initializeDialog();
        initializeComponents();
        layoutComponents();
        wireButtonActions();

        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void initializeComponents() {
        entityTypeBox = new JComboBox<>(new String[] {
                "Lion",
                "Deer",
                "Rabbit",
                "Tree",
                "Flower",
                "Rock",
                "Water"
        });

        xField = new JTextField(10);
        yField = new JTextField(10);
        energyField = new JTextField(10);

        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");
    }

    private void layoutComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        inputPanel.add(new JLabel("Entity Type:"));
        inputPanel.add(entityTypeBox);

        inputPanel.add(new JLabel("X Coordinate:"));
        inputPanel.add(xField);

        inputPanel.add(new JLabel("Y Coordinate:"));
        inputPanel.add(yField);

        inputPanel.add(new JLabel("Starting Energy:"));
        inputPanel.add(energyField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void wireButtonActions() {
        addButton.addActionListener(event -> handleAddEntity());
        cancelButton.addActionListener(event -> dispose());
    }

    private void handleAddEntity() {
        try {
            String entityType = (String) entityTypeBox.getSelectedItem();

            int x = Integer.parseInt(xField.getText().trim());
            int y = Integer.parseInt(yField.getText().trim());

            if (!isValidCoordinate(x, y)) {
                showValidationError("Coordinates must be inside the grid.\n"
                        + "X must be between 0 and " + (Environment.WIDTH - 1) + ".\n"
                        + "Y must be between 0 and " + (Environment.HEIGHT - 1) + ".");
                return;
            }

            Position position = new Position(x, y);
            AbstractEntity entity = createEntity(entityType, position);

            applyStartingEnergyIfNeeded(entity);

            controller.addNewEntity(entity);
            dispose();

        } catch (NumberFormatException exception) {
            showValidationError("Please enter valid numbers for coordinates and energy.");
        } catch (IllegalArgumentException exception) {
            showValidationError(exception.getMessage());
        } catch (Exception exception) {
            showValidationError("Could not add entity: " + exception.getMessage());
        }
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < Environment.WIDTH
                && y >= 0 && y < Environment.HEIGHT;
    }

    private AbstractEntity createEntity(String entityType, Position position) {
        if ("Lion".equals(entityType)) {
            return new Lion(position);
        }
        if ("Deer".equals(entityType)) {
            return new Deer(position);
        }
        if ("Rabbit".equals(entityType)) {
            return new Rabbit(position);
        }
        if ("Tree".equals(entityType)) {
            return new Tree(position);
        }
        if ("Flower".equals(entityType)) {
            return new Flower(position);
        }
        if ("Rock".equals(entityType)) {
            return new Rock(position);
        }
        if ("Water".equals(entityType)) {
            return new Water(position);
        }

        throw new IllegalArgumentException("Unknown entity type selected.");
    }

    private void applyStartingEnergyIfNeeded(AbstractEntity entity) {
        String energyText = energyField.getText().trim();

        if (energyText.isEmpty()) {
            return;
        }

        double startingEnergy = Double.parseDouble(energyText);

        if (startingEnergy < 0) {
            throw new IllegalArgumentException("Starting energy cannot be negative.");
        }

        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setEnergy(startingEnergy);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "This entity does not use energy. The energy value will be ignored.",
                    "Energy Ignored",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
