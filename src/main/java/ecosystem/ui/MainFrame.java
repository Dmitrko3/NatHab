package ecosystem.ui;

import ecosystem.controller.*;
import ecosystem.engine.*;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * Main application window for the ecosystem simulation GUI.
 */
public class MainFrame extends JFrame implements SimulationListener {

    private final SimulationController controller;
    private Environment environment;
    private SimulationGridPanel simulationPanel;
    private ControlPanel controlPanel;
    private EntityInfoPanel infoPanel;

    /**
     * Creates the main window.
     *
     * @param controller the simulation controller
     * @param environment the ecosystem environment
     */
    public MainFrame(SimulationController controller, Environment environment) {
        this.controller = controller;
        this.environment = environment;

        initializeFrame();
        initializePanels();
        layoutPanels();
    }

    /**
     * Sets up the basic window settings.
     */
    private void initializeFrame() {
        setTitle("Ecosystem Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    /**
     * Creates the main GUI panels.
     */
    private void initializePanels() {
        infoPanel = new EntityInfoPanel();
        infoPanel.setEnvironment(environment); // Added line
        simulationPanel = new SimulationGridPanel(environment, infoPanel);
        controlPanel = new ControlPanel(controller);
    }

    /**
     * Places panels into the window layout.
     */
    private void layoutPanels() {
        add(simulationPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(infoPanel, BorderLayout.EAST);
    }

    /**
     * Opens the window.
     */
    public void showWindow() {
        setVisible(true);
    }

    /**
     * Updates the GUI after each simulation step.
     *
     * @param environment the updated environment
     * @return true when handled successfully
     */
    @Override
    public boolean onSimulationUpdated(Environment environment) {
        this.environment = environment;
        infoPanel.setEnvironment(environment); // Added line
        SwingUtilities.invokeLater(() -> {
            simulationPanel.repaint();
            infoPanel.repaint();
            repaint();
        });
        return true;
    }



    /**
     * Gets the controller.
     *
     * @return the simulation controller
     */
    public SimulationController getController() {
        return controller;
    }

    /**
     * Gets the grid panel.
     *
     * @return the simulation grid panel
     */
    public SimulationGridPanel getSimulationPanel() {
        return simulationPanel;
    }

    /**
     * Gets the info panel.
     *
     * @return the entity info panel
     */
    public EntityInfoPanel getInfoPanel() {
        return infoPanel;
    }

    /**
     * Gets the control panel.
     *
     * @return the simulation control panel
     */
    public ControlPanel getControlPanel() {
        return controlPanel;
    }
}