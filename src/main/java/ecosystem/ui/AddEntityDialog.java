package ecosystem.ui;

import ecosystem.controller.*;
import ecosystem.core.*;
import ecosystem.entities.*;
import ecosystem.entities.animals.*;
import ecosystem.entities.plants.*;
import ecosystem.entities.resources.*;
import ecosystem.entities.EntityFactory;
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
 */
public class AddEntityDialog extends JDialog {

    private final SimulationController controller;

    private JComboBox<String> entityTypeBox;
    private JTextField xField;
    private JTextField yField;
    private JTextField energyField;

    private JButton addButton;
    private JButton cancelButton;

    /**
     * Creates a dialog for adding a new entity.
     *
     * @param owner the parent window
     * @param controller the simulation controller
     */
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

    /**
     * Configures basic dialog properties.
     *
     * @return true upon successful setup
     */
    private boolean initializeDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        return true;
    }

    /**
     * Creates the input fields and buttons.
     */
    private void initializeComponents() {
        // Use Factory to fetch dynamic available types, satisfying Open/Closed Principle
        entityTypeBox = new JComboBox<>(EntityFactory.getSupportedTypes());
        xField = new JTextField(10);
        yField = new JTextField(10);
        energyField = new JTextField(10);
        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");
    }
    /**
     * Arranges the UI components in the dialog.
     */
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

    /**
     * Connects the buttons to their actions.
     *
     * @return true upon successful connection
     */
    private boolean wireButtonActions() {
        addButton.addActionListener(event -> handleAddEntity());
        cancelButton.addActionListener(event -> dispose());
        return true;
    }

    /**
     * Validates inputs and attempts to add the new entity.
     *
     * @return true if the entity was added successfully, false otherwise
     */
    private boolean handleAddEntity() {
        try {
            String entityType = (String) entityTypeBox.getSelectedItem();

            int x = Integer.parseInt(xField.getText().trim());
            int y = Integer.parseInt(yField.getText().trim());

            if (!isValidCoordinate(x, y)) {
                showValidationError("Coordinates must be inside the grid.\n"
                        + "X must be between 0 and " + (Environment.WIDTH - 1) + ".\n"
                        + "Y must be between 0 and " + (Environment.HEIGHT - 1) + ".");
                return false;
            }

            Position position = new Position(x, y);
            AbstractEntity entity = createEntity(entityType, position);

            applyStartingEnergyIfNeeded(entity);

            boolean added = controller.addNewEntity(entity);
            if (added) {
                dispose();
                return true;
            } else {
                showValidationError("Could not add entity at the specified position.");
                return false;
            }

        } catch (NumberFormatException exception) {
            showValidationError("Please enter valid numbers for coordinates and energy.");
            return false;
        } catch (IllegalArgumentException exception) {
            showValidationError(exception.getMessage());
            return false;
        } catch (Exception exception) {
            showValidationError("Could not add entity: " + exception.getMessage());
            return false;
        }
    }

    /**
     * Checks if the coordinates are within the environment bounds.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if valid, false otherwise
     */
    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < Environment.WIDTH
                && y >= 0 && y < Environment.HEIGHT;
    }

    /**
     * Creates an entity based on the selected type.
     *
     * @param entityType the entity type name
     * @param position the starting coordinates
     * @return the newly created entity
     */
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

    /**
     * Sets the starting energy for living entities if provided.
     *
     * @param entity the entity to modify
     */
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

    /**
     * Displays an error message popup.
     *
     * @param message the error message to display
     * @return always returns false
     */
    private boolean showValidationError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
        );
        return false;
    }
}