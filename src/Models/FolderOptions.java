package Models;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Logger;

/**
 * @author Adam
 */
public class FolderOptions {

    /**
     * Name of options
     */
    private String name = "";
    /**
     * Command to run on folder
     */
    private String commandToRun = "";
    /**
     * Should the file be deleted after run?
     */
    private boolean deleteAfterCommand;
    /**
     * A list of arguments to be run with the command
     */
    private ArrayList<String> arguments = new ArrayList<String>();
    /**
     * Folder to watch
     */
    private File folderToWatch;
    /**
     * Number of seconds between check
     */
    private int secondsBetweenCheck = 60;
    /**
     * Instance of the time to check for changes
     */
    private Timer timer = new Timer();
    /**
     * Track all the processes that are running
     */
    private Map<String, DefaultExecutor> commandsRunning = new LinkedHashMap<String, DefaultExecutor>();
    /**
     * Filter the directory listing of files.
     */
    private FileFilters fileFilters = new FileFilters();
    /**
     * Store the file types to look for.
     */
    private String fileTypes = "";
    /**
     * Monitor if the folder is currently being monitored.
     */
    private boolean folderBeingMonitored;
    /**
     * Track if the timer has been created
     */
    private boolean timerCreated;
    /**
     * Token for where to insert the filename
     */
    public static final String FILENAME = "${file}";

    /**
     * Start the timer for watching changes to a folder.
     */
    public void watchForChanges() {
        if (!timerCreated) {
            timer.schedule(new RemindTask(), 0, getSecondsBetweenCheck() * 1000);
            timerCreated = true;
        }
        folderBeingMonitored = true;
    }

    /**
     * Stops the timer
     */
    public void cancelMonitoringFolders() {
        folderBeingMonitored = false;
    }

    /**
     * Name of options
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Name of options
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get a yes / no status if the folder is being monitored
     * @return Yes if it is being monitored.
     */
    public String getYesNoStatus() {
        if (folderBeingMonitored) {
            return "Yes";
        }
        return "No";
    }

    /**
     * The re-occurring task to run.
     */
    private class RemindTask extends TimerTask {

        @Override
        public void run() {
            if (folderBeingMonitored) {
                runCommands();
            }
        }
    }

    /**
     * Checks to see if any commands are current still running. If not, get all
     * the files in the directory and run the command on them.
     */
    private synchronized void runCommands() {
        if (doCleanupOnCommandsFinished()) {
            Logger.getLogger(FolderOptions.class).debug("No commands currently running...");
            for (File file : folderToWatch.listFiles(fileFilters)) {
                if(file != null && file.exists()) {
                    runComandOnFile(file);
                }
            }
        }
    }

    /**
     * Runs the specified command on a file.
     * @param file to run the command on.
     */
    private void runComandOnFile(File file) {
        try {
            CommandLine cmdLine = new CommandLine(getCommandToRun());

            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            PumpStreamHandler psh = new PumpStreamHandler(stdout);

            for (String argument : arguments) {
                cmdLine.addArgument(argument);
            }
            Map<String, File> fileMap = new HashMap<String, File>();
            fileMap.put("file", file);
            cmdLine.setSubstitutionMap(fileMap);
            Logger.getLogger(FolderOptions.class).info("[Running command] " + cmdLine.toString());

            DefaultExecutor executor = new DefaultExecutor();
            ExecuteWatchdog watchdog = new ExecuteWatchdog(6000);
            executor.setWatchdog(watchdog);
            executor.setStreamHandler(psh);
            executor.execute(cmdLine);
            Logger.getLogger(FolderOptions.class).info(stdout.toString());
            commandsRunning.put(file.getAbsolutePath(), executor);
        } catch (Exception ex) {
            Logger.getLogger(FolderOptions.class).error(ex.toString());
        }
    }

    /**
     * Cleans up any commands that have been running by...
     * - removing any commands that have finished.
     * - deleting any files if needed.
     * @return true if all the commands have finished.
     */
    private boolean doCleanupOnCommandsFinished() {
        ArrayList<String> deleteProcesses = new ArrayList<String>();
        for (Map.Entry<String, DefaultExecutor> e : commandsRunning.entrySet()) {
            if (!e.getValue().getWatchdog().isWatching()) {
                deleteProcesses.add(e.getKey());
            }
        }
        for (String deleteProcess : deleteProcesses) {
            Logger.getLogger(FolderOptions.class).info("[Command Finished] " + getCommandToRun(deleteProcess));
            if (deleteAfterCommand) {
                Logger.getLogger(FolderOptions.class).info("[Removing File] " + deleteProcess);
                new File(deleteProcess).delete();
            }
            commandsRunning.remove(deleteProcess);
        }
        return commandsRunning.isEmpty();
    }

    /**
     * Create a file filter to look for only the certain types in a directory.
     */
    public class FileFilters implements FileFilter {

        private String[] okFileExtensions = new String[]{"*"};

        /**
         * Check to see if the file has a particular extension
         * @param file to check
         * @return if the file does have the extension return true
         */
        public boolean accept(File file) {
            for (String extension : okFileExtensions) {
                if (file.getName().toLowerCase().endsWith(extension)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Set the extentions to test on the file
         * @param newExtentions array of extentions
         */
        public void setFileExtensions(String[] newExtentions) {
            okFileExtensions = newExtentions;
        }
    }

    /**
     * File type to look for.
     * @param fileType the fileType to set
     */
    public void setFileTypes(String fileType) {
        this.fileTypes = fileType;
        fileFilters.setFileExtensions(fileType.split(","));
    }

    /**
     * Get the file types to look for.
     * @return the file types.
     */
    public String getFileTypes() {
        return fileTypes;
    }

    /**
     * Command to run on folder
     * @param file to run the command on
     * @return the commandToRun
     */
    public String getCommandToRun(File file) {
        return commandToRun.replace(FILENAME, file.getAbsolutePath());
    }

    /**
     * Command to run on folder
     * @param file to run the command on
     * @return the commandToRun
     */
    public String getCommandToRun(String file) {
        return commandToRun.replace(FILENAME, file);
    }

    /**
     * Get the argument with the file name replaced in it
     * @param file string to replace in the argument
     * @param argument to file the file in
     * @return the full argument
     */
    public String getArgument(File file, String argument) {
        return argument.replace(FILENAME, file.getAbsolutePath());
    }

    /**
     * Get the command string to run
     * @return the command string.
     */
    public String getCommandToRun() {
        return commandToRun;
    }

    /**
     * Command to run on folder
     * @param commandToRun the commandToRun to set
     */
    public void setCommandToRun(String commandToRun) {
        this.commandToRun = commandToRun;
    }

    /**
     * Should the file be deleted after run?
     * @return the deleteAfterCommand
     */
    public Boolean isDeleteAfterCommand() {
        return deleteAfterCommand;
    }

    /**
     * Should the file be deleted after run?
     * @param deleteAfterCommand the deleteAfterCommand to set
     */
    public void setDeleteAfterCommand(boolean deleteAfterCommand) {
        this.deleteAfterCommand = deleteAfterCommand;
    }

    /**
     * Number of seconds between check
     * @return the secondsBetweenCheck
     */
    public int getSecondsBetweenCheck() {
        return secondsBetweenCheck;
    }

    /**
     * Number of seconds between check
     * @param secondsBetweenCheck the secondsBetweenCheck to set
     */
    public void setSecondsBetweenCheck(int secondsBetweenCheck) {
        this.secondsBetweenCheck = secondsBetweenCheck;
    }

    /**
     * Folder to watch
     * @return the folderToWatch
     */
    public File getFolderToWatch() {
        return folderToWatch;
    }

    /**
     * Folder to watch
     * @param folderToWatch the folderToWatch to set
     */
    public void setFolderToWatch(File folderToWatch) {
        this.folderToWatch = folderToWatch;
    }

    /**
     * A list of arguments to be run with the command
     * @return the arguments
     */
    public ArrayList<String> getArguments() {
        return arguments;
    }

    /**
     * A list of arguments to be run with the command
     * @param arguments the arguments to set
     */
    public void setArguments(ArrayList<String> arguments) {
        this.arguments = arguments;
    }

    /**
     * Get the argument list in string format
     * @return the list as a string
     */
    public String getArgumentsString() {
        String list = "";
        for (String arg : arguments) {
            list += arg + "%ARG%";
        }
        return list;
    }

    /**
     * Split a string of arguments into the array
     * @param arguments 
     */
    public void setArguments(String arguments) {
        try {
            this.arguments = new ArrayList(Arrays.asList(arguments.split("%ARG%")));
        } catch(Exception e) {
            this.arguments = new ArrayList();
        }
    }
}
