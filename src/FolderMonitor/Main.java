package FolderMonitor;

import Models.FolderWatch;
import java.awt.EventQueue;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author Adam
 */
public class Main {

    /**
     * The program names
     */
    public static final String PROGRAM_NAME = "Folder Monitor";

    /**
     * Set OSX specific properties.
     */
    static {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", PROGRAM_NAME);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                PropertyConfigurator.configure("logs/log4j.properties");
                FolderWatch folderWatch = new FolderWatch();
                if (args.length <= 0) {
                    Logger.getLogger(Main.class).info("Config file not listed.");
                } else {
                    folderWatch.loadConfig(args[0]);
                }
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    Logger.getLogger(Main.class).debug("Could not load system look and feel.");
                }
                loadMonitorFrame(folderWatch);
            }
        });
    }

    /**
     * Load the main monitoring frame
     * @param folderWatch pass the class that holds all the folders to watch
     */
    public static void loadMonitorFrame(final FolderWatch folderWatch) {
        Logger.getLogger(Main.class).debug("Loading GUI frame.");
        MonitorFrame monitorFrame = new MonitorFrame(folderWatch);
    }
}
