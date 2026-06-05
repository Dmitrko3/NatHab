package ecosystem.entities;

import ecosystem.core.Environment;
import ecosystem.core.Position;
import ecosystem.interfaces.Actable;

import java.util.Random;


/**
 * An entity that is alive and changes each turn.
 *
 * <p>Each turn, it gets older and loses energy.
 * If its energy reaches 0, it dies.
 *
 * <p>Subclasses can add their own behavior after this basic action.
 */
public abstract class LivingEntity extends AbstractEntity implements Actable, Runnable {

    // lifecycle & state
    protected int    age;
    protected double energy;
    protected double maxEnergy;

    // thread control
    private volatile boolean isThreadActive = false;
    private volatile Environment runEnvironment;
    private transient Thread entityThread;
    private static final Random THREAD_RANDOM = new Random();

    protected LivingEntity(Position position, char symbol,
                           double initialEnergy, double maxEnergy) {
        super(position, symbol);
        this.age       = 0;
        this.energy    = initialEnergy;
        this.maxEnergy = maxEnergy;
    }

    // -------------------------------------------------------------------------
    // Actable
    // -------------------------------------------------------------------------
    @Override
    public boolean act(Environment environment) {
        try{
            age++;

        energy -= 2.0;
        if (energy <= 0) {
            alive = false;
            // subclass may stop its thread in response to death by calling setThreadActive(false)
        }
        return true;
        } catch (Exception ex) {
            System.err.println("Entity " + this + " act() error: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Runnable: entity execution loop
    // -------------------------------------------------------------------------
    @Override
    public void run() {
        while (isThreadActive) {
            try {
                int delay = 500 + THREAD_RANDOM.nextInt(1001); // 500..1500 ms
                Thread.sleep(delay);

                try {
                    // perform one tick of behavior
                    act(runEnvironment);
                } catch (Exception ex) {
                    System.err.println("Entity " + this + " act() failed: " + ex.getMessage());
                    ex.printStackTrace();
                }

            } catch (InterruptedException ie) {
                // Respect interruption as a request to stop
                Thread.currentThread().interrupt();
                setThreadActive(false);
                break;
            } catch (Exception ex) {
                // Log unexpected thread-level failures and continue (do not silently die)
                System.err.println("Entity " + this + " thread failure: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**     * Start this entity's internal thread. Must provide a non-null Environment.     * Returns true if thread was started, false otherwise.     */
    public boolean startThread(Environment environment) {
        if (environment == null) return false;
        // prevent starting when already active
        if (!setThreadActive(true)) return false;

        this.runEnvironment = environment;
        entityThread = new Thread(this, getClass().getSimpleName() + "-thread");
        entityThread.setDaemon(true);
        entityThread.start();
        return true;
    }

    /**     * Stop the internal thread. Returns true if stop request was issued.     */
    public boolean stopThread() {
        boolean ok = setThreadActive(false);
        if (entityThread != null) {
            entityThread.interrupt();
        }
        return ok;
    }

    /**     * Protected setter for the thread-active flag.     * Validates that the thread is not being improperly restarted.     *     * @return true if the flag was set successfully, false otherwise     */
    protected boolean setThreadActive(boolean active) {
        synchronized (this) {
            // disallow restarting a currently active thread
            if (active && isThreadActive) return false;
            isThreadActive = active;
            return true;
        }
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------
    public int    getAge()       { return age; }
    public double getEnergy()    { return energy; }
    public double getMaxEnergy() { return maxEnergy; }

    /**     * Sets energy, clamping to [0, maxEnergy].     */
    public void setEnergy(double energy) {
        this.energy = Math.max(0, Math.min(maxEnergy, energy));
        if (this.energy <= 0) this.alive = false;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------
    @Override
    public String toString() {
        return String.format("%s %s energy=%.1f age=%d alive=%b",
                getClass().getSimpleName(), position, energy, age, alive);
    }
}