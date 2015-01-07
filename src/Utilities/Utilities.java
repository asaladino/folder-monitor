package Utilities;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.log4j.Logger;

public class Utilities {

    /**
     * Location of the images
     */
    public static final String IMAGE_LOCATION = "/Icons/";
    /**
     * Application image icon
     */
    public static final String APPLICATION_ICON = "icon";
    public static final String EMPTY_ICON = "empty";
    public static final String DEFAULT_IMAGE_TYPE = ".png";
    public static final String ICON_SMALL = "_16x16";
    public static final String ICON_MEDIUM = "_32x32";
    public static final String ICON_LARGE = "_64x64";
    public static final String ICON_FULL = "_512x512";
    public static boolean MAC_OS_X = System.getProperty("os.name").toLowerCase().startsWith("mac os x");

    /**
     * Center a component relative to another component
     * @param component to center
     * @param parentComponent relative component to center from
     */
    public static void centerComponentAboveFrame(Component component, Component parentComponent) {
        Rectangle parentBounds = parentComponent.getBounds();
        Point componentPosition = new Point(
                ((parentBounds.width - component.getWidth()) / 2) + parentBounds.x,
                ((parentBounds.height - component.getHeight()) / 2) + parentBounds.y);
        if (componentPosition.x < 0) {
            componentPosition.x = 0;
        }
        if (componentPosition.y < 0) {
            componentPosition.y = 0;
        }
        component.setLocation(componentPosition);
    }

    /**
     * Center a given component on the screen
     * @param component to center
     */
    public static void centerComponentOnScreen(Component component) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Point p = new Point(((d.width - component.getWidth()) / 2), ((d.height - component.getHeight()) / 2));
        if (p.x < 0) {
            p.x = 0;
        }
        if (p.y < 0) {
            p.y = 0;
        }
        component.setLocation(p);
    }

    /**
     * Maximizes a JFrame to the screen.
     * @param frame to maximize
     */
    public static void maximizeJFrameToScreen(JFrame frame) {
        frame.setMaximizedBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        frame.setPreferredSize(frame.getMaximizedBounds().getSize());
    }

    /**
     * Brings the JFrame to the front.
     * @param frame to bring forward
     */
    public static void toFront(JFrame frame) {
        frame.setVisible(true);
        int state = frame.getExtendedState();
        state &= ~JFrame.ICONIFIED;
        frame.setExtendedState(state);
        frame.setAlwaysOnTop(true);
        frame.toFront();
        frame.requestFocus();
        frame.setAlwaysOnTop(false);
    }

    /**
     * Get the image for the icon
     * @return the image
     */
    public static Image getApplicationIcon() {
        return getImageIcon(APPLICATION_ICON + ICON_MEDIUM).getImage();
    }

    /**
     * Get the small version of the icon
     * @param imageName filename of image
     * @return an image icon
     */
    public static ImageIcon getImageIconSmall(String imageName) {
        return createImage(IMAGE_LOCATION + imageName + ICON_SMALL + DEFAULT_IMAGE_TYPE);
    }

    /**
     * Retrieve an image from the image location.
     * @param imageName filename of image
     * @return an image icon
     */
    public static ImageIcon getImageIcon(String imageName) {
        return createImage(IMAGE_LOCATION + imageName + DEFAULT_IMAGE_TYPE);
    }

    /**
     * Retrieve and image icon give the full path to the resource
     * @param path full path to the image icon
     * @return null if the icon is not found.
     */
    public static ImageIcon createImage(String path) {
        URL imgURL = Utilities.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            Logger.getLogger(Utilities.class.getName()).error("Image not found: " + path);
            return new ImageIcon(IMAGE_LOCATION + EMPTY_ICON + DEFAULT_IMAGE_TYPE);
        }
    }

    /**
     * Sets the default title for a JDialog
     * @param dialog references to the dialog
     * @param title portion of the frame
     */
    public static void setTitleForFrame(JDialog dialog, String title) {
        dialog.setTitle(title);
        dialog.setName(title);
    }

    /**
     * Adds escape event that will hide the dialog when escape is pressed.
     * This should be return from the over-ridden protected JRootPane createRootPane() method
     * @param dialog to attach event too
     * @return the pane and the event.
     */
    public static JRootPane enableEscapeHideEventForJDialogs(final JDialog dialog) {
        JRootPane rootDialogPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        Action actionListener = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                dialog.setVisible(false);
            }
        };
        InputMap inputMap = rootDialogPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootDialogPane.getActionMap().put("ESCAPE", actionListener);
        return rootDialogPane;
    }

    /**
     * Displays a file save dialog that only displays directories.
     * @param frame the parent for the file dialog
     * @param title of the save dialog
     * @return the selected directory.
     */
    public static File getFileSaveDirectoryOnly(Component frame, String title) {
        return getFileFromDialog(frame, title, JFileChooser.SAVE_DIALOG, false, true, null);
    }

    /**
     * Displays a file open dialog for only selected extentions
     * @param frame the parent for the file dialog
     * @param title of the save dialog
     * @param fileTypes an array of file types. See ProteoIQConstants
     * @return the selected file.
     */
    public static File getFileOpenOnly(Component frame, String title, String fileTypes[]) {
        return getFileFromDialog(frame, title, JFileChooser.OPEN_DIALOG, true, false, setFileTypesFromArray(fileTypes));
    }

    /**
     * Display a file save dialog for only selected extentions
     * @param frame the parent for the file dialog
     * @param title of the save dialog
     * @param fileTypes an array of file types. See ProteoIQConstants
     * @return the selected file.
     */
    public static File getFileSaveOnly(Component frame, String title, String fileTypes[]) {
        return getFileFromDialog(frame, title, JFileChooser.SAVE_DIALOG, true, false, setFileTypesFromArray(fileTypes));
    }

    /**
     * Get a map of files and filter when selecting multiple files.
     * @param frame the parent for the file dialog
     * @param title of the save dialog
     * @param fileTypes an array of file types. See ProteoIQConstants
     * @return map of files and filter type or null if canceled.
     */
    public static Map getFilesOpenFilesAndDirectories(Component frame, String title, String fileTypes[]) {
        return getFilesFromDialog(frame, title, true, true, setFileTypesFromArray(fileTypes));
    }

    /**
     * Generic method for creating a file dialog box.
     * @param frame the parent for the file dialog
     * @param title of the save dialog
     * @param type of file dialog box, open / save
     * @param getFiles true if you are trying to retrieve files
     * @param getDirectories true if you are trying to retrieve a directory
     * @param fileTypes an array of file types. See ProteoIQConstants
     * @return the selected file or directory.
     */
    public static File getFileFromDialog(Component frame, String title, int type, boolean getFiles, boolean getDirectories, Map fileTypes) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        if (type == JFileChooser.SAVE_DIALOG) {
            chooser.setApproveButtonText("Save");
            chooser.setApproveButtonMnemonic("S".charAt(0));
        }
        if (type == JFileChooser.OPEN_DIALOG) {
            chooser.setApproveButtonText("Open");
            chooser.setApproveButtonMnemonic("O".charAt(0));
        }
        if (getFiles && !getDirectories) {
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        if (getDirectories && !getFiles) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        if (getFiles && getDirectories) {
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }
        if (fileTypes != null && !fileTypes.isEmpty()) {
            chooser.setAcceptAllFileFilterUsed(false);
            loadFileChooserExtentions(chooser, fileTypes);
        }
        chooser.setMultiSelectionEnabled(false);
        int c = -1;
        if (type == JFileChooser.OPEN_DIALOG) {
            c = chooser.showOpenDialog(frame);
        }
        if (type == JFileChooser.SAVE_DIALOG) {
            c = chooser.showSaveDialog(frame);
        }
        if (c == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (type == JFileChooser.SAVE_DIALOG && FileUtils.getFileExtension(file) == null && fileTypes != null) {
                try {
                    return new File(file.getAbsolutePath() + "." + ((ExtentionFilter) chooser.getFileFilter()).extention);
                } catch (ClassCastException ex) {
                    Logger.getLogger(Utilities.class.getName()).error(ex.getMessage());
                }
            }
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    /**
     * Get a map of files and filter type after a bunch of files are selected.
     * @param frame the parent for the file dialog
     * @param title of the save dialog
     * @param getFiles true if you are trying to retrieve files
     * @param getDirectories true if you are trying to retrieve a directory
     * @param fileTypes an array of file types. See ProteoIQConstants
     * @return map of files and filter type or null if canceled.
     */
    public static Map getFilesFromDialog(Component frame, String title, boolean getFiles, boolean getDirectories, Map fileTypes) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setApproveButtonText("Open");
        chooser.setApproveButtonMnemonic("O".charAt(0));
        if (getFiles && !getDirectories) {
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        if (getDirectories && !getFiles) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        if (getFiles && getDirectories) {
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }
        if (fileTypes != null && !fileTypes.isEmpty()) {
            chooser.setAcceptAllFileFilterUsed(false);
            loadFileChooserExtentions(chooser, fileTypes);
        }
        chooser.setMultiSelectionEnabled(true);
        int c = chooser.showOpenDialog(frame);
        Map selections = new HashMap();
        if (c == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFiles().length > 0) {
                selections.put("files", chooser.getSelectedFiles());
                selections.put("filter", chooser.getFileFilter());
            }
        }
        return selections;
    }

    /**
     * Will load the file extentions as filters into the file chooser dialog
     * @param fd is the file chooser in question
     */
    public static void loadFileChooserExtentions(JFileChooser fd, Map fileTypes) {
        Iterator iterTypes = fileTypes.entrySet().iterator();
        while (iterTypes.hasNext()) {
            Map.Entry pairs = (Map.Entry) iterTypes.next();
            fd.addChoosableFileFilter(new ExtentionFilter((String) pairs.getKey(), (String) pairs.getValue()));
        }
    }

    /**
     * Create a file filter for the file chooser dialog
     */
    public static class ExtentionFilter extends FileFilter {

        public String description;
        public String extention;

        public ExtentionFilter(String description, String extention) {
            this.description = description;
            this.extention = extention;
        }

        @Override
        public boolean accept(File file) {
            String filename = file.getName();
            return filename.endsWith(this.extention) || file.isDirectory();
        }

        @Override
        public String getDescription() {
            return this.description;
        }
    }

    /**
     * Populate the file list from an array of strings found in ProteoIQConstants
     * @param fileTypes the array of file types
     */
    public static Map setFileTypesFromArray(String[] fileTypes) {
        Map tempFileTypes = new LinkedHashMap();
        int begin = 0;
        int end = 0;
        if (fileTypes != null) {
            for (int i = fileTypes.length - 1; i >= 0; i--) {
                begin = fileTypes[i].indexOf("(") + 2;
                end = fileTypes[i].indexOf(")");
                tempFileTypes.put(fileTypes[i], fileTypes[i].substring(begin, end).trim());
            }
        }
        return tempFileTypes;
    }

    public static String stripLeadingAndTrailingQuotes(String str) {
        if (str.startsWith("\"")) {
            str = str.substring(1, str.length());
        }
        if (str.endsWith("\"")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public static JTable autoResizeColWidth(JTable table, DefaultTableModel model) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setModel(model);

        int margin = 5;

        for (int i = 0; i < table.getColumnCount(); i++) {
            int vColIndex = i;
            DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn col = colModel.getColumn(vColIndex);
            int width = 0;

            // Get width of column header
            TableCellRenderer renderer = col.getHeaderRenderer();

            if (renderer == null) {
                renderer = table.getTableHeader().getDefaultRenderer();
            }

            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);

            width = comp.getPreferredSize().width;

            // Get maximum width of column data
            for (int r = 0; r < table.getRowCount(); r++) {
                renderer = table.getCellRenderer(r, vColIndex);
                comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false,
                        r, vColIndex);
                width = Math.max(width, comp.getPreferredSize().width);
            }

            // Add margin
            width += 2 * margin;

            // Set the width
            col.setPreferredWidth(width);
        }

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(
                SwingConstants.LEFT);

        // table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);

        return table;
    }
}
