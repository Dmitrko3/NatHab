package ecosystem.ui;

import ecosystem.controller.*;
import ecosystem.core.*;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * Main application window for the ecosystem simulation GUI.
 * This frame acts as the master container for all sub-panels,
 * such as the simulation grid, controls, stats, and entity selection.
 */
public class MainFrame extends JFrame implements SimulationListener {

    private final SimulationController controller;
    private Environment environment;
    private SimulationGridPanel simulationPanel;
    private ControlPanel controlPanel;
    private EntityInfoPanel infoPanel;

    public MainFrame(SimulationController controller, Environment environment) {
        this.controller = controller;
        this.environment = environment;

        initializeFrame();
        initializePanels();
        layoutPanels();
    }

    /**
     * Sets up the basic JFrame settings.
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
        simulationPanel = new SimulationGridPanel(environment, infoPanel);
        controlPanel = new ControlPanel(controller);
    }

    /**
     * Places all sub-panels into the main BorderLayout.
     */
    private void layoutPanels() {
        add(simulationPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(infoPanel, BorderLayout.EAST);
    }

    /**
     * Opens the main application window.
     */
    public void showWindow() {
        setVisible(true);
    }

    /**
     * Called by SimulationEngine after each tick.
     * Updates the GUI from the latest model state and repaints the view.
     */
    @Override
    public void onSimulationUpdated(Environment environment) {
        this.environment = environment;

        SwingUtilities.invokeLater(() -> {
            simulationPanel.repaint();
            infoPanel.repaint();
            repaint();
        });
    }

    public SimulationController getController() {
        return controller;
    }

    public SimulationGridPanel getSimulationPanel() {
        return simulationPanel;
    }

    public EntityInfoPanel getInfoPanel() {
        return infoPanel;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }
}