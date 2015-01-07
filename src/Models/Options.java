package Models;

import java.util.ArrayList;
import java.util.prefs.Preferences;

/**
 * Store the user options using java's preferences class
 * @author adamsaladino
 */
public class Options {

    /**
     * Singleton instance for accessing user options
     */
    private static Options instance;
    /**
     * Node that will store user preferences
     */
    private static String MAIN_NODE = "folder_monitor";
    /**
     * Recently opened list.
     */
    private ArrayList<String> recentlyOpened = new ArrayList<String>();
    /**
     * Max recently opened list
     */
    private int maxRecentlyOpened = 5;
    /**
     * When the minimize button is clicked does the
     * minimize to the task bar or system tray
     */
    private boolean minimizeToSystemTray = true;
    /**
     * Start the app to system tray
     */
    private boolean startToSystemTray = false;
    /**
     * Start the app with the previously opened 
     * watch list.
     */
    private boolean startWithPreviousWatchList = true;
    /**
     * Last opened file location.
     */
    private String lastOpenedLocation = "";

    private Options() {
    }

    /**
     * Singleton
     * @return 
     */
    public static synchronized Options getInstance() {
        if (instance == null) {
            instance = new Options();
            instance.load();
        }
        return instance;
    }

    /**
     * Load the options from java's special place
     */
    public void load() {
        Preferences prefs = Preferences.userRoot().node(MAIN_NODE);
        setMaxRecentlyOpened(prefs.getInt("max_recently_opened", getMaxRecentlyOpened()));
        for (int i = 0; i <= getMaxRecentlyOpened(); i++) {
            String item = prefs.get("recently_opened_" + i, "");
            if (!item.isEmpty()) {
                getRecentlyOpened().add(prefs.get("recently_opened_" + i, ""));
            }
        }
        setMinimizeToSystemTray(prefs.getBoolean("minimize_to_system_tray", isMinimizeToSystemTray()));
        setLastOpenedLocation(prefs.get("last_opend_location", getLastOpenedLocation()));
        setStartToSystemTray(prefs.getBoolean("start_to_system_tray", isStartToSystemTray()));
        setStartWithPreviousWatchList(prefs.getBoolean("start_with_previous_watch_list", isStartWithPreviousWatchList()));
    }

    /**
     * Save the options to java's special place
     */
    public void save() {
        Preferences prefs = Preferences.userRoot().node(MAIN_NODE);
        prefs.putInt("max_recently_opened", getMaxRecentlyOpened());
        for (int i = 0; i <= getMaxRecentlyOpened(); i++) {
            if (i >= getRecentlyOpened().size()) {
                break;
            }
            prefs.put("recently_opened_" + i, getRecentlyOpened().get(i));
        }
        prefs.putBoolean("minimize_to_system_tray", isMinimizeToSystemTray());
        prefs.put("last_opend_location", getLastOpenedLocation());
        prefs.putBoolean("start_to_system_tray", isStartToSystemTray());
        prefs.putBoolean("start_with_previous_watch_list", isStartWithPreviousWatchList());
    }

    /**
     * Add to the list of recently opened files. If the file is already in the list
     * then don't add it
     * @param file String to add
     */
    public void addRecentlyOpened(String file) {
        if (!getRecentlyOpened().contains(file)) {
            getRecentlyOpened().add(0, file);
        }
    }

    /**
     * Recently opened list.
     * @return the recentlyOpened
     */
    public ArrayList<String> getRecentlyOpened() {
        return recentlyOpened;
    }

    /**
     * Recently opened list.
     * @param recentlyOpened the recentlyOpened to set
     */
    public void setRecentlyOpened(ArrayList<String> recentlyOpened) {
        this.recentlyOpened = recentlyOpened;
    }

    /**
     * Max recently opened list
     * @return the maxRecentlyOpened
     */
    public int getMaxRecentlyOpened() {
        return maxRecentlyOpened;
    }

    /**
     * Max recently opened list
     * @param maxRecentlyOpened the maxRecentlyOpened to set
     */
    public void setMaxRecentlyOpened(int maxRecentlyOpened) {
        this.maxRecentlyOpened = maxRecentlyOpened;
    }

    /**
     * When the minimize button is clicked does the
     * minimize to the task bar or system tray
     * @return the minimizeToSystemTray
     */
    public boolean isMinimizeToSystemTray() {
        return minimizeToSystemTray;
    }

    /**
     * When the minimize button is clicked does the
     * minimize to the task bar or system tray
     * @param minimizeToSystemTray the minimizeToSystemTray to set
     */
    public void setMinimizeToSystemTray(boolean minimizeToSystemTray) {
        this.minimizeToSystemTray = minimizeToSystemTray;
    }

    /**
     * Last opened file location.
     * @return the lastOpenedLocation
     */
    public String getLastOpenedLocation() {
        return lastOpenedLocation;
    }

    /**
     * Last opened file location.
     * @param lastOpenedLocation the lastOpenedLocation to set
     */
    public void setLastOpenedLocation(String lastOpenedLocation) {
        this.lastOpenedLocation = lastOpenedLocation;
    }

    /**
     * Start the app to system tray
     * @return the startToSystemTray
     */
    public boolean isStartToSystemTray() {
        return startToSystemTray;
    }

    /**
     * Start the app to system tray
     * @param startToSystemTray the startToSystemTray to set
     */
    public void setStartToSystemTray(boolean startToSystemTray) {
        this.startToSystemTray = startToSystemTray;
    }

    /**
     * Start the app with the previously opened
     * watch list.
     * @return the startWithPreviousWatchList
     */
    public boolean isStartWithPreviousWatchList() {
        return startWithPreviousWatchList;
    }

    /**
     * Start the app with the previously opened
     * watch list.
     * @param startWithPreviousWatchList the startWithPreviousWatchList to set
     */
    public void setStartWithPreviousWatchList(boolean startWithPreviousWatchList) {
        this.startWithPreviousWatchList = startWithPreviousWatchList;
    }
}
