package FolderMonitor;

import Models.FolderOptions;
import Models.FolderWatch;
import Dialogs.AboutDialog;
import Dialogs.EditOptionJDialog;
import Dialogs.PreferencesJDialog;
import Models.Options;
import Utilities.DocumentationHelper;
import Utilities.OSXAdapter;
import Utilities.Utilities;
import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 * TODO: Finish help documentation integration
 * DONE: Fix bug where no file is loaded and you can't save.
 * TODO: Add options for starting all watches, stopping all watches from
 *       the tool bar and the system tray
 * TODO: Add option to each FolderOption to start on load.
 * TODO: Finish code commenting.
 * TODO: Move utilities to separate project
 * TODO: Finish command line interface with apache commons library
 *       https://commons.apache.org/cli/
 * TODO: Do unit testing on all methods
 * 2.0 release track =========================================================
 * TODO: Decease memory foot print
 * TODO: Add FolderOption templates
 * TODO: Add desktop notifications
 * TODO: Add scripting interface?
 * TODO: Add UI for viewing log files
 * @author Adam
 */
public class MonitorFrame extends JFrame {

    /**
     * This watches all the folders.
     */
    private FolderWatch folderWatch = new FolderWatch();
    private EditOptionJDialog editOptionJDialog = new EditOptionJDialog(this, true);
    private AboutDialog aboutJDialog = new AboutDialog(this, true);
    private PreferencesJDialog preferencesJDialog = new PreferencesJDialog(this, true);
    private Options options = Options.getInstance();
    private DefaultTableModel model;
//    private DocumentationHelper documentationHelper = new DocumentationHelper();
    private boolean saveRequired;

    public MonitorFrame(FolderWatch folderWatch) {
        this.folderWatch = folderWatch;
        initComponents();
        initFrame();
    }

    private void initFrame() {
        loadTableModel();
        loadTableData();
        loadOSDefaults();
        loadHelpDocuments();
        loadSystemTrayMenu();
        loadOptions();
        setDialogTitle();
        Utilities.centerComponentOnScreen(this);
    }

    public JFrame getThis() {
        return this;
    }

    public void setDialogTitle() {
        String title = Main.PROGRAM_NAME;
        if (folderWatch.getConfigFile() == null || folderWatch.getConfigFile().isEmpty()) {
            title += " - [New Folder Monitor]";
        } else {
            title += " - [" + new File(folderWatch.getConfigFile()).getName() + "]";
        }
        if (saveRequired) {
            title += " ***";
        }
        setTitle(title);

    }

    /**
     * Load system specific options
     */
    private void loadOSDefaults() {
        if (Utilities.MAC_OS_X) {
            jMenuItemExit.setVisible(false);
            jMenuItemAbout.setVisible(false);
            jMenuEdit.setVisible(false);
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[]) null));
                OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
                OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[]) null));
//                OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadImageFile", new Class[] { String.class }));
            } catch (Exception e) {
                Logger.getLogger(MonitorFrame.class).error("Error while loading the OSXAdapter");
            }
        }
    }

    /**
     * Load table model to be used.
     */
    private void loadTableModel() {
        model = new DefaultTableModel();
        String col[] = {"Running", "Name", "Folder", "File Types", "Interval"};
        model.addColumn(col[0]);
        model.addColumn(col[1]);
        model.addColumn(col[2]);
        model.addColumn(col[3]);
        jTable1.setModel(model);
        jTable1.addMouseListener(new EditFolderMouseAdapter());
    }

    /**
     * loaded the table data into the model
     */
    private void loadTableData() {
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        for (int i = 0; i < folderWatch.getFolderList().size(); i++) {
            model.addRow(new Object[]{
                        folderWatch.getFolderList().get(i).getYesNoStatus(),
                        folderWatch.getFolderList().get(i).getName(),
                        folderWatch.getFolderList().get(i).getFolderToWatch(),
                        folderWatch.getFolderList().get(i).getFileTypes(),
                        folderWatch.getFolderList().get(i).getSecondsBetweenCheck() + " sec(s)"
                    });
        }
        jTable1.repaint();
    }

    private void loadSystemTrayMenu() {
        if (!SystemTray.isSupported()) {
            Logger.getLogger(MonitorFrame.class).error("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(Utilities.getImageIconSmall("icon").getImage());
        final SystemTray tray = SystemTray.getSystemTray();

        MenuItem watchAllItem = new MenuItem("Monitor All");
        watchAllItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                watchAllFolders();
            }
        });

        MenuItem stopWatchingAllItem = new MenuItem("Stop Monitoring Everything");
        watchAllItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                stopWatchingAll();
            }
        });

        MenuItem preferenecesItem = new MenuItem("Preferences");
        preferenecesItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                preferences();
            }
        });

        MenuItem showFrameItem = new MenuItem("Show Monitor");
        showFrameItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Utilities.toFront(getThis());
            }
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                quit();
            }
        });

        popup.add(watchAllItem);
        popup.add(stopWatchingAllItem);
        popup.addSeparator();
        popup.add(preferenecesItem);
        popup.add(showFrameItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            Logger.getLogger(MonitorFrame.class).error("TrayIcon could not be added.");
        }
    }

    private void loadOptions() {
        setVisible(!options.isStartToSystemTray());
        if (folderWatch.getConfigFile() != null && !folderWatch.getConfigFile().isEmpty()) {
            options.addRecentlyOpened(folderWatch.getConfigFile());
        }
        if (options.isStartWithPreviousWatchList()
                && (folderWatch.getConfigFile() == null || folderWatch.getConfigFile().isEmpty())
                && !options.getRecentlyOpened().isEmpty()) {
            folderWatch.loadConfig(options.getRecentlyOpened().get(0));
            loadTableData();
        }
        loadRecentMenuItems();
    }

    private void loadRecentMenuItems() {
        jMenuRecentlyOpened.removeAll();
        for (String file : options.getRecentlyOpened()) {
            String currentFile = "";
            if (file.equals(folderWatch.getConfigFile())) {
                currentFile = " [open]";
            }
            JMenuItem openItem = new JMenuItem(new File(file).getName() + currentFile);
            openItem.setToolTipText(file);
            openItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    loadConfig(((JMenuItem) ae.getSource()).getToolTipText());
                }
            });
            jMenuRecentlyOpened.add(openItem);
        }
    }

    private void loadConfig(String file) {
        if (folderWatch.getConfigFile() == null
                || folderWatch.getConfigFile().isEmpty()
                || !folderWatch.getConfigFile().equals(file)) {
            folderWatch.loadConfig(file);
            options.addRecentlyOpened(file);
            options.save();
            loadTableData();
            loadRecentMenuItems();
        }
    }

    private void loadHelpDocuments() {
//        documentationHelper.setHelpSetContext(jMenuItemHelp, "intro");
//        jMenuItemHelp.addActionListener(documentationHelper.getHelpSetListener());
//        documentationHelper.setHelpSetContext(jButtonHelp, "intro");
//        jButtonHelp.addActionListener(documentationHelper.getHelpSetListener());
    }

    /**
     * Add a new folder to monitor.
     * @param name of the folder
     * @param command to run on the folder
     * @param folder location
     * @param fileTypes to look for
     * @param delete should the file be deleted after the command is run
     * @param seconds time between checks
     * @param array of parameters
     */
    public void addFolder(String name, String command, String folder, String fileTypes, boolean delete, int seconds, ArrayList<String> arguments) {
        FolderOptions folderOptions = new FolderOptions();
        folderOptions.setName(name);
        folderOptions.setCommandToRun(command);
        folderOptions.setFolderToWatch(new File(folder));
        folderOptions.setFileTypes(fileTypes);
        folderOptions.setDeleteAfterCommand(delete);
        folderOptions.setSecondsBetweenCheck(seconds);
        folderOptions.setArguments(arguments);
        folderWatch.getFolderList().add(folderOptions);
        saveRequired = true;
        loadTableData();
        setDialogTitle();
    }

    /**
     * Update an existing folders options.
     * @param index of the folder in the list
     * @param name of the folder
     * @param command to run on the folder
     * @param folder location
     * @param fileTypes to look for
     * @param delete should the file be deleted after the command is run
     * @param seconds time between checks
     * @param array of parameters
     */
    public void updateFolder(int index, String name, String command, String folder, String fileTypes, boolean delete, int seconds, ArrayList<String> arguments) {
        folderWatch.getFolderList().get(index).setName(name);
        folderWatch.getFolderList().get(index).setCommandToRun(command);
        folderWatch.getFolderList().get(index).setFolderToWatch(new File(folder));
        folderWatch.getFolderList().get(index).setFileTypes(fileTypes);
        folderWatch.getFolderList().get(index).setDeleteAfterCommand(delete);
        folderWatch.getFolderList().get(index).setSecondsBetweenCheck(seconds);
        folderWatch.getFolderList().get(index).setArguments(arguments);
        saveRequired = true;
        loadTableData();
        setDialogTitle();
    }

    private class EditFolderMouseAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                editSelectedFolder();
            }
        }
    }

    public void editSelectedFolder() {
        if (jTable1.getSelectedRowCount() == 1) {
            editOptionJDialog.setFolderWatchIndex(jTable1.getSelectedRow());
            editOptionJDialog.setOptionFields(folderWatch.getFolderList().get(jTable1.getSelectedRow()));
            Utilities.centerComponentAboveFrame(editOptionJDialog, this);
            editOptionJDialog.setTitle("Edit Folder...");
            editOptionJDialog.setVisible(true);
        }
    }

    public void stopWatchingAll() {
        for (int i = 0; i < folderWatch.getFolderList().size(); i++) {
            folderWatch.getFolderList().get(i).cancelMonitoringFolders();
        }
        loadTableData();
    }

    public void watchAllFolders() {
        for (int i = 0; i < folderWatch.getFolderList().size(); i++) {
            folderWatch.getFolderList().get(i).watchForChanges();
        }
        loadTableData();
    }

    public void quit() {
        Logger.getLogger(MonitorFrame.class).debug("Quiting");
        if (saveRequired) {
            if (doYouWantToSaveChanges() == 0) {
                save();
            }
        }
        System.exit(0);
    }

    private int doYouWantToSaveChanges() {
        Object[] quitOptions = {"Yes, Save Changes", "No, Discard Changes"};
        return JOptionPane.showOptionDialog(this,
                "Do you want to save your changes?",
                "Save Changes?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                quitOptions,
                quitOptions[0]);
    }

    public void about() {
        Utilities.centerComponentAboveFrame(aboutJDialog, this);
        aboutJDialog.setVisible(true);
    }

    public void save() {
        if (folderWatch.getConfigFile() == null || folderWatch.getConfigFile().isEmpty()) {
            File file = Utilities.getFileSaveOnly(this, "Save Config File...", null);
            if (file != null) {
                folderWatch.setConfigFile(file.getAbsolutePath());
                doSave();
            }
        } else {
            doSave();
        }
    }

    public void doSave() {
        if (folderWatch.getConfigFile() != null && !folderWatch.getConfigFile().isEmpty()) {
            folderWatch.writeConfigFile();
            saveRequired = false;
            setDialogTitle();
        }
    }

    public void open() {
        File file = Utilities.getFileOpenOnly(this, "Open Config File...", null);
        if (file != null) {
            loadConfig(file.getAbsolutePath());
            setDialogTitle();
        }
    }

    public void preferences() {
        Utilities.centerComponentAboveFrame(preferencesJDialog, this);
        preferencesJDialog.setVisible(true);
    }

    public void newFolderWatcher() {
        if (saveRequired) {
            if (doYouWantToSaveChanges() == 0) {
                save();
            }
        }
        File file = Utilities.getFileSaveOnly(this, "New Config File...", null);
        if (file != null) {
            folderWatch.cancelAndClearMonitoringFolders();
            folderWatch = new FolderWatch();
            folderWatch.setConfigFile(file.getAbsolutePath());
            loadTableData();
            doSave();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jButtonNew = new javax.swing.JButton();
        jButtonOpen = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonHelp = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonNewFolder = new javax.swing.JButton();
        jButtonEditFolder = new javax.swing.JButton();
        jButtonDeleteFolder = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jButtonStopWatching = new javax.swing.JButton();
        jButtonWatchFolder = new javax.swing.JButton();
        jButtonStopWatchingAll = new javax.swing.JButton();
        jButtonWatchAllFolders = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new SwingBeans.MonitorJTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNew = new javax.swing.JMenuItem();
        jMenuItemOpenList = new javax.swing.JMenuItem();
        jMenuRecentlyOpened = new javax.swing.JMenu();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemPreferences = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemAbout = new javax.swing.JMenuItem();
        jMenuItemHelp = new javax.swing.JMenuItem();

        setIconImage(Utilities.getApplicationIcon());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowIconified(java.awt.event.WindowEvent evt) {
                formWindowIconified(evt);
            }
        });

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setMaximumSize(new java.awt.Dimension(300, 32));
        jToolBar3.setMinimumSize(new java.awt.Dimension(128, 32));
        jToolBar3.setPreferredSize(new java.awt.Dimension(128, 32));
        jToolBar3.setSize(new java.awt.Dimension(128, 32));

        jButtonNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/page_add_16x16.png"))); // NOI18N
        jButtonNew.setToolTipText("New Monitor...");
        jButtonNew.setBorderPainted(false);
        jButtonNew.setFocusable(false);
        jButtonNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNew.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonNew.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonNew.setPreferredSize(new java.awt.Dimension(28, 28));
        jButtonNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewActionPerformed(evt);
            }
        });
        jToolBar3.add(jButtonNew);

        jButtonOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/folder_page_16x16.png"))); // NOI18N
        jButtonOpen.setToolTipText("Open Existing Monitor...");
        jButtonOpen.setBorderPainted(false);
        jButtonOpen.setFocusable(false);
        jButtonOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonOpen.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonOpen.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonOpen.setPreferredSize(new java.awt.Dimension(28, 28));
        jButtonOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });
        jToolBar3.add(jButtonOpen);

        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk_16x16.png"))); // NOI18N
        jButtonSave.setToolTipText("Save Current Monitor");
        jButtonSave.setBorderPainted(false);
        jButtonSave.setFocusable(false);
        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSave.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonSave.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonSave.setPreferredSize(new java.awt.Dimension(28, 28));
        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jToolBar3.add(jButtonSave);

        jButtonHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/help_16x16.png"))); // NOI18N
        jButtonHelp.setToolTipText("Help Contents");
        jButtonHelp.setBorderPainted(false);
        jButtonHelp.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonHelp.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonHelp.setPreferredSize(new java.awt.Dimension(28, 28));
        jToolBar3.add(jButtonHelp);

        jPanel1.add(jToolBar3);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(96, 32));
        jToolBar1.setMinimumSize(new java.awt.Dimension(96, 32));
        jToolBar1.setPreferredSize(new java.awt.Dimension(96, 32));

        jButtonNewFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/add_16x16.png"))); // NOI18N
        jButtonNewFolder.setToolTipText("Monitor New Folder...");
        jButtonNewFolder.setBorderPainted(false);
        jButtonNewFolder.setFocusable(false);
        jButtonNewFolder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNewFolder.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonNewFolder.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonNewFolder.setPreferredSize(new java.awt.Dimension(28, 28));
        jButtonNewFolder.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNewFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewFolderActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonNewFolder);

        jButtonEditFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/information_16x16.png"))); // NOI18N
        jButtonEditFolder.setToolTipText("Edit Monitor Folder...");
        jButtonEditFolder.setBorderPainted(false);
        jButtonEditFolder.setFocusable(false);
        jButtonEditFolder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonEditFolder.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonEditFolder.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonEditFolder.setPreferredSize(new java.awt.Dimension(28, 28));
        jButtonEditFolder.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonEditFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditFolderActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonEditFolder);

        jButtonDeleteFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/delete_16x16.png"))); // NOI18N
        jButtonDeleteFolder.setToolTipText("Delete Folder");
        jButtonDeleteFolder.setBorderPainted(false);
        jButtonDeleteFolder.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonDeleteFolder.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonDeleteFolder.setPreferredSize(new java.awt.Dimension(28, 28));
        jButtonDeleteFolder.setSize(new java.awt.Dimension(28, 28));
        jButtonDeleteFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteFolderActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonDeleteFolder);

        jPanel1.add(jToolBar1);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setMaximumSize(new java.awt.Dimension(11200, 32));
        jToolBar2.setMinimumSize(new java.awt.Dimension(32, 32));
        jToolBar2.setPreferredSize(new java.awt.Dimension(128, 32));
        jToolBar2.setSize(new java.awt.Dimension(128, 32));

        jButtonStopWatching.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control_stop_16x16.png"))); // NOI18N
        jButtonStopWatching.setToolTipText("Stop Monitoring Selected");
        jButtonStopWatching.setBorderPainted(false);
        jButtonStopWatching.setFocusable(false);
        jButtonStopWatching.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStopWatching.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonStopWatching.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonStopWatching.setPreferredSize(new java.awt.Dimension(28, 28));
        jButtonStopWatching.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStopWatching.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopWatchingActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonStopWatching);

        jButtonWatchFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control_play_16x16.png"))); // NOI18N
        jButtonWatchFolder.setToolTipText("Start Monitoring Selected");
        jButtonWatchFolder.setBorderPainted(false);
        jButtonWatchFolder.setFocusable(false);
        jButtonWatchFolder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonWatchFolder.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonWatchFolder.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonWatchFolder.setPreferredSize(new java.awt.Dimension(28, 28));
        jButtonWatchFolder.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonWatchFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonWatchFolderActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonWatchFolder);

        jButtonStopWatchingAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control_pause_16x16.png"))); // NOI18N
        jButtonStopWatchingAll.setToolTipText("Stop All Monitoring");
        jButtonStopWatchingAll.setBorderPainted(false);
        jButtonStopWatchingAll.setFocusable(false);
        jButtonStopWatchingAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStopWatchingAll.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonStopWatchingAll.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonStopWatchingAll.setPreferredSize(new java.awt.Dimension(28, 28));
        jButtonStopWatchingAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStopWatchingAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopWatchingAllActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonStopWatchingAll);

        jButtonWatchAllFolders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control_fastforward_16x16.png"))); // NOI18N
        jButtonWatchAllFolders.setToolTipText("Start Monitoring All Folders");
        jButtonWatchAllFolders.setBorderPainted(false);
        jButtonWatchAllFolders.setFocusable(false);
        jButtonWatchAllFolders.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonWatchAllFolders.setMaximumSize(new java.awt.Dimension(28, 28));
        jButtonWatchAllFolders.setMinimumSize(new java.awt.Dimension(28, 28));
        jButtonWatchAllFolders.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonWatchAllFolders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonWatchAllFoldersActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonWatchAllFolders);

        jPanel1.add(jToolBar2);

        jScrollPane1.setBorder(null);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jMenuFile.setMnemonic('f');
        jMenuFile.setText("File");

        jMenuItemNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/page_add_16x16.png"))); // NOI18N
        jMenuItemNew.setMnemonic('n');
        jMenuItemNew.setText("New...");
        jMenuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNew);

        jMenuItemOpenList.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpenList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/folder_page_16x16.png"))); // NOI18N
        jMenuItemOpenList.setMnemonic('o');
        jMenuItemOpenList.setText("Open...");
        jMenuItemOpenList.setToolTipText("Open Watch List");
        jMenuItemOpenList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenListActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpenList);

        jMenuRecentlyOpened.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/empty_16x16.png"))); // NOI18N
        jMenuRecentlyOpened.setText("Recently Opened");
        jMenuFile.add(jMenuRecentlyOpened);

        jMenuItemSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.SHIFT_MASK));
        jMenuItemSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/empty_16x16.png"))); // NOI18N
        jMenuItemSaveAs.setMnemonic('a');
        jMenuItemSaveAs.setText("Save As...");
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk_16x16.png"))); // NOI18N
        jMenuItemSave.setMnemonic('s');
        jMenuItemSave.setText("Save");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);

        jMenuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/empty_16x16.png"))); // NOI18N
        jMenuItemExit.setMnemonic('x');
        jMenuItemExit.setText("Exit");
        jMenuItemExit.setToolTipText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar1.add(jMenuFile);

        jMenuEdit.setMnemonic('e');
        jMenuEdit.setText("Edit");

        jMenuItemPreferences.setMnemonic('p');
        jMenuItemPreferences.setText("Preferences");
        jMenuItemPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPreferencesActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemPreferences);

        jMenuBar1.add(jMenuEdit);

        jMenuHelp.setMnemonic('h');
        jMenuHelp.setText("Help");

        jMenuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/empty_16x16.png"))); // NOI18N
        jMenuItemAbout.setText("About...");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        jMenuItemHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/help_16x16.png"))); // NOI18N
        jMenuItemHelp.setText("Help Contents...");
        jMenuHelp.add(jMenuItemHelp);

        jMenuBar1.add(jMenuHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jMenuItemOpenListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenListActionPerformed
        open();
    }//GEN-LAST:event_jMenuItemOpenListActionPerformed
    private void jButtonEditFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditFolderActionPerformed
        editSelectedFolder();
    }//GEN-LAST:event_jButtonEditFolderActionPerformed
    private void jButtonNewFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewFolderActionPerformed
        editOptionJDialog.clearOptionFields();
        editOptionJDialog.setFolderWatchIndex(-1);
        Utilities.centerComponentAboveFrame(editOptionJDialog, this);
        editOptionJDialog.setTitle("Add Folder...");
        editOptionJDialog.setVisible(true);
    }//GEN-LAST:event_jButtonNewFolderActionPerformed
    private void jButtonDeleteFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteFolderActionPerformed
        if (jTable1.getSelectedRowCount() == 1) {
            folderWatch.getFolderList().remove(jTable1.getSelectedRow());
        }
        loadTableData();
    }//GEN-LAST:event_jButtonDeleteFolderActionPerformed
    private void jButtonWatchFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWatchFolderActionPerformed
        if (jTable1.getSelectedRowCount() > 0) {
            int[] selectedRows = jTable1.getSelectedRows();
            for (int i = 0; i < selectedRows.length; i++) {
                folderWatch.getFolderList().get(selectedRows[i]).watchForChanges();
            }
        }
        loadTableData();
    }//GEN-LAST:event_jButtonWatchFolderActionPerformed
    private void jButtonStopWatchingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopWatchingActionPerformed
        if (jTable1.getSelectedRowCount() > 0) {
            int[] selectedRows = jTable1.getSelectedRows();
            for (int i = 0; i < selectedRows.length; i++) {
                folderWatch.getFolderList().get(selectedRows[i]).cancelMonitoringFolders();
            }
        }
        loadTableData();
    }//GEN-LAST:event_jButtonStopWatchingActionPerformed
    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
        File file = Utilities.getFileOpenOnly(this, "Save Config File...", null);
        if (file != null) {
            folderWatch.writeConfigFile(file.getAbsolutePath());
        }
        saveRequired = false;
        setDialogTitle();
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed
    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        save();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed
    private void jMenuItemNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewActionPerformed
        newFolderWatcher();
    }//GEN-LAST:event_jMenuItemNewActionPerformed
    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        about();
    }//GEN-LAST:event_jMenuItemAboutActionPerformed
    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        save();
    }//GEN-LAST:event_jButtonSaveActionPerformed
    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        open();
    }//GEN-LAST:event_jButtonOpenActionPerformed
    private void jButtonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewActionPerformed
        newFolderWatcher();
    }//GEN-LAST:event_jButtonNewActionPerformed
    private void jMenuItemPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPreferencesActionPerformed
        preferences();
    }//GEN-LAST:event_jMenuItemPreferencesActionPerformed
    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        quit();
    }//GEN-LAST:event_jMenuItemExitActionPerformed
private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
    if (options.isMinimizeToSystemTray() && !Utilities.MAC_OS_X) {
        setVisible(false);
    }
}//GEN-LAST:event_formWindowIconified
private void jButtonStopWatchingAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopWatchingAllActionPerformed
    stopWatchingAll();
}//GEN-LAST:event_jButtonStopWatchingAllActionPerformed
private void jButtonWatchAllFoldersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWatchAllFoldersActionPerformed
    watchAllFolders();
}//GEN-LAST:event_jButtonWatchAllFoldersActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDeleteFolder;
    private javax.swing.JButton jButtonEditFolder;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonNew;
    private javax.swing.JButton jButtonNewFolder;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonStopWatching;
    private javax.swing.JButton jButtonStopWatchingAll;
    private javax.swing.JButton jButtonWatchAllFolders;
    private javax.swing.JButton jButtonWatchFolder;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemHelp;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemOpenList;
    private javax.swing.JMenuItem jMenuItemPreferences;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenu jMenuRecentlyOpened;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private SwingBeans.MonitorJTable jTable1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    // End of variables declaration//GEN-END:variables
}
