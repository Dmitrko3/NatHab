/*Dmitry Kornilov-322220609
  Igal Chertok -322832163
 * */
package ecosystem;

import ecosystem.controller.SimulationController;
import ecosystem.engine.Environment;
import ecosystem.engine.Position;
import ecosystem.engine.SimulationEngine;
import ecosystem.entities.animals.Deer;
import ecosystem.entities.animals.Lion;
import ecosystem.entities.animals.Rabbit;
import ecosystem.entities.plants.Flower;
import ecosystem.entities.plants.Tree;
import ecosystem.entities.resources.Rock;
import ecosystem.entities.resources.Water;
import ecosystem.network.NetworkManager;
import ecosystem.ui.MainFrame;

import javax.swing.SwingUtilities;

/**
 * Entry point — builds an initial world and runs the simulation for a fixed
 * number of ticks.
 *
 * <p>Grid legend printed before the first tick:
 * <pre>
 *   L = Lion     D = Deer      R = Rabbit
 *   T = Tree     F = Flower
 *   # = Rock     ~ = Water     . = Empty cell
 * </pre>
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Environment env = new Environment();

            // ---- Static resources ----
            env.addEntity(new Rock(new Position(3, 3)));
            env.addEntity(new Rock(new Position(4, 3)));
            env.addEntity(new Rock(new Position(5, 3)));
            env.addEntity(new Rock(new Position(10, 10)));
            env.addEntity(new Rock(new Position(11, 10)));

            env.addEntity(new Water(new Position(8,  8)));
            env.addEntity(new Water(new Position(9,  8)));
            env.addEntity(new Water(new Position(8,  9)));

            // ---- Plants ----
            env.addEntity(new Tree(new Position(2,  2)));
            env.addEntity(new Tree(new Position(15, 15)));
            env.addEntity(new Tree(new Position(1,  12)));

            env.addEntity(new Flower(new Position(6,  6)));
            env.addEntity(new Flower(new Position(12, 4)));
            env.addEntity(new Flower(new Position(7,  14)));
            env.addEntity(new Flower(new Position(17, 7)));

            // ---- Herbivores ----
            env.addEntity(new Deer(new Position(5,  5)));
            env.addEntity(new Deer(new Position(14, 14)));

            env.addEntity(new Rabbit(new Position(6,  7)));
            env.addEntity(new Rabbit(new Position(11, 11)));
            env.addEntity(new Rabbit(new Position(3,  12)));
            env.addEntity(new Rabbit(new Position(16, 3)));

            // ---- Carnivore ----
            env.addEntity(new Lion(new Position(10, 5)));
            env.addEntity(new Lion(new Position(11, 7)));

            SimulationEngine engine = new SimulationEngine(env);
            SimulationController controller = new SimulationController(engine, env);
            MainFrame frame = new MainFrame(controller, env);

            engine.addSimulationListener(new ecosystem.ui.SwingSimulationListenerAdapter(frame));
            engine.addSimulationListener(new ecosystem.ui.SwingSimulationListenerAdapter(frame.getSimulationPanel()));

            frame.showWindow();
        });
    }
}