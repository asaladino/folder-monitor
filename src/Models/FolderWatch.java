package Models;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Adam
 */
public class FolderWatch {

    /**
     * List of folders to monitor.
     */
    private ArrayList<FolderOptions> folderList = new ArrayList<FolderOptions>();
    /**
     * The location of config file that was loaded.
     */
    private String configFile;

    /**
     * The location of config file that was loaded.
     * @return the config file
     */
    public String getConfigFile() {
        return configFile;
    }

    /**
     * The location of config file that was loaded.
     * @param configFile location
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    /**
     * List of folders to monitor.
     * @return list of folders to monitor
     */
    public ArrayList<FolderOptions> getFolderList() {
        return folderList;
    }

    /**
     * Start watching for files
     */
    public void watchFolders() {
        for (int i = 0; i < folderList.size(); i++) {
            folderList.get(i).watchForChanges();
        }
    }

    /**
     * Cancel all the folders being monitored and clear the list.
     */
    public void cancelAndClearMonitoringFolders() {
        for (int i = 0; i < folderList.size(); i++) {
            folderList.get(i).cancelMonitoringFolders();
        }
        folderList.clear();
    }

    /**
     * Load the config file of folders to watch.
     * @param file config file.
     */
    public void loadConfig(String file) {
        configFile = file;
        File xmlFile = new File(file);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            Logger.getLogger(FolderWatch.class).info("Loading config file...");
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("folder_options");
            cancelAndClearMonitoringFolders();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node node = nList.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    FolderOptions fo = new FolderOptions();
                    fo.setArguments(getTagValue("arguments", element));
                    fo.setName(getTagValue("name", element));
                    fo.setCommandToRun(getTagValue("commandToRun", element));
                    try {
                        fo.setDeleteAfterCommand(Boolean.parseBoolean(getTagValue("deleteAfterCommand", element)));
                    } catch(Exception e) {
                        fo.setDeleteAfterCommand(false);
                    }
                    try {
                        fo.setFolderToWatch(new File(getTagValue("folderToWatch", element)));
                    } catch(Exception e) {
                        fo.setFolderToWatch(null);
                    }
                    try {
                        fo.setSecondsBetweenCheck(Integer.parseInt(getTagValue("secondsBetweenCheck", element)));
                    } catch(Exception e) {
                        fo.setSecondsBetweenCheck(60);
                    }
                    fo.setFileTypes(getTagValue("fileType", element));
                    folderList.add(fo);
                }
            }
            Logger.getLogger(FolderWatch.class).info("Finished loading config file...");
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FolderWatch.class).error(ex);
        } catch (SAXException ex) {
            Logger.getLogger(FolderWatch.class).error(ex);
        } catch (IOException ex) {
            Logger.getLogger(FolderWatch.class).error("Could not open file: " + file);
        }
    }

    /**
     * Write the XML config file to the file that was opened.
     */
    public void writeConfigFile() {
        writeConfigFile(configFile);
    }

    /**
     * Write the list of folder options to an XML file
     * @param file to write too.
     */
    public void writeConfigFile(String file) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element folders = doc.createElement("folders");
            doc.appendChild(folders);

            for (int i = 0; i < folderList.size(); i++) {
                Element folderOptions = doc.createElement("folder_options");
                folders.appendChild(folderOptions);

                Element name = doc.createElement("name");
                name.appendChild(doc.createTextNode(folderList.get(i).getName()));
                folderOptions.appendChild(name);

                Element commandToRun = doc.createElement("commandToRun");
                commandToRun.appendChild(doc.createTextNode(folderList.get(i).getCommandToRun()));
                folderOptions.appendChild(commandToRun);


                Element deleteAfterCommand = doc.createElement("deleteAfterCommand");
                deleteAfterCommand.appendChild(doc.createTextNode(folderList.get(i).isDeleteAfterCommand().toString()));
                folderOptions.appendChild(deleteAfterCommand);

                Element folderToWatch = doc.createElement("folderToWatch");
                folderToWatch.appendChild(doc.createTextNode(folderList.get(i).getFolderToWatch().getAbsolutePath()));
                folderOptions.appendChild(folderToWatch);

                Element secondsBetweenCheck = doc.createElement("secondsBetweenCheck");
                secondsBetweenCheck.appendChild(doc.createTextNode(folderList.get(i).getSecondsBetweenCheck() + ""));
                folderOptions.appendChild(secondsBetweenCheck);

                Element fileType = doc.createElement("fileType");
                fileType.appendChild(doc.createTextNode(folderList.get(i).getFileTypes()));
                folderOptions.appendChild(fileType);

                Element arguments = doc.createElement("arguments");
                arguments.appendChild(doc.createTextNode(folderList.get(i).getArgumentsString()));
                folderOptions.appendChild(arguments);
            }

            // Write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            File output = new File(file);
            StreamResult result = new StreamResult(new File(file));
            transformer.transform(source, result);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FolderWatch.class).error(ex);
        } catch (TransformerException ex) {
            Logger.getLogger(FolderWatch.class).error(ex);
        }
    }

    /**
     * Get the value of a tag
     * @param tag name that has a value
     * @param element that holds the tag
     * @return the value of the tag.
     */
    private static String getTagValue(String tag, Element element) {
        try {
            NodeList nlList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node nValue = (Node) nlList.item(0);
            return nValue.getNodeValue();
        } catch(NullPointerException e) {
            return null;
        }
    }
}
