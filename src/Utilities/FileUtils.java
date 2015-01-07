package Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Tools for handling common file and folder tasks
 * @author Adam
 */
public class FileUtils {

    /**
     * Will zip up a folder into a zip file.
     * @param srcFolder absolute path to the folder to be zipped.
     * @param destZipFile absolute path to the destination zip file.
     * @throws Exception encase there is a file output problem
     */
    static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
    }

    /**
     * Loops through all the folders and adds the file based on type.
     * @param path to the location in the zip file.
     * @param srcFolder absolute path to the folder to be zipped.
     * @param zip absolute path to the destination zip file.s
     * @throws Exception encase there are some write errors.
     */
    static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
        File folder = new File(srcFolder);
        for (String fileName : folder.list()) {
            if (path.equals("")) {
                addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
            } else {
                addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
            }
        }
    }

    /**
     * Does the writing of the file or folder to the zip file. If the file is a folder
     * then it gets sent back to addFolderToZip
     * @param path to the location in the zip file.
     * @param srcFolder absolute path to the folder to be zipped.
     * @param zip absolute path to the destination zip file.s
     * @throws Exception encase there are some write errors.
     */
    static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {
        File folder = new File(srcFile);
        if (folder.isDirectory()) {
            addFolderToZip(path, srcFile, zip);
        } else {
            byte[] buf = new byte[1024];
            int len;
            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
            in.close();
        }
    }

    /**
     * Will recursively loop through a directory and deleted it and the contents.
     * Note, this method will not work if the directory is still being used by a
     * FileOutStream or another program.
     * @param file absolute path to the folder to delete.
     * @throws IOException encase there is a file access problem.
     */
    public static void deleteDir(File file) throws IOException {
        if (file.isDirectory()) {
            //directory is empty, then delete it
            if (file.list().length == 0) {
                file.delete();
            } else {
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    deleteDir(fileDelete);
                }
                //check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else {
            // if file, then delete it
            file.delete();
        }
    }

    /**
     * Returns just the file name, no extention.
     * @param fileName name of the file.
     * @return file name less extension.
     */
    public static String getFileNameWithoutExt(String fileName) {
        File file = new File(fileName);
        int index = file.getName().lastIndexOf('.');
        if (index > 0 && index <= file.getName().length() - 2) {
            return file.getName().substring(0, index);
        }
        return "";
    }

    /**
     * Get the file extention from a file
     * @param file that has the extention
     * @return the extention without the dot.
     */
    public static String getFileExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf(".");
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * Returns the name of a project from piq file name
     * @param fileName name or full path to file
     * @return the project name
     */
    public static String getProjectNameFromPiqFile(String fileName) {
        return getFileNameWithoutExt(fileName).replaceAll("all_", "");
    }

    /**
     * Copies a file from one location to another.
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            //if directory not exists, create it
            if (!dest.exists()) {
                dest.mkdir();
                System.out.println("Directory copied from " + src + "  to " + dest);
            }
            //list all the directory contents
            String files[] = src.list();
            for (String file : files) {
                //construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                //recursive copy
                copyFolder(srcFile, destFile);
            }
        } else {
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            //copy the file content in bytes
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
            System.out.println("File copied from " + src + " to " + dest);
        }
    }
}
