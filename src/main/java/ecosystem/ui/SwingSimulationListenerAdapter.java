package ecosystem.ui;

import ecosystem.core.Environment;
import ecosystem.core.SimulationListener;

import javax.swing.SwingUtilities;

/**
 * Adapter that ensures a SimulationListener is invoked on the Swing EDT.
 * The model can safely call this adapter from any thread.
 */
public class SwingSimulationListenerAdapter implements SimulationListener {

    private final SimulationListener delegate;

    public SwingSimulationListenerAdapter(SimulationListener delegate) {
        if (delegate == null) throw new IllegalArgumentException("delegate");
        this.delegate = delegate;
    }

    @Override
    public boolean onSimulationUpdated(Environment environment) {
        // If we're already on the EDT, invoke directly and return the delegate result.
        if (SwingUtilities.isEventDispatchThread()) {
            try {
                return delegate.onSimulationUpdated(environment);
            } catch (Exception ex) {
                // Swallow exceptions from delegate; return false to indicate failure
                return false;
            }
        }

        // Not on EDT: schedule the delegate call to run on EDT asynchronously.
        // We cannot get a true success/failure result here without blocking (invokeAndWait),
        // so return true to indicate the update was accepted for dispatch.
        SwingUtilities.invokeLater(() -> {
            try {
                delegate.onSimulationUpdated(environment);
            } catch (Exception ignored) {
                // Keep UI resilient; log if you want.
            }
        });
        return true;
    }
}