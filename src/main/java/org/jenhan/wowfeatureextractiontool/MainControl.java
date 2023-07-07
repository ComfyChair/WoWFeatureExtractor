package org.jenhan.wowfeatureextractiontool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.prefs.*;

public class MainControl {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String ADDON_NAME = "FeatureRecordingTool";
    private static final File ADDON_ZIP =
            new File("src/main/resources/org/jenhan/wowfeatureextractiontool/WoWAddon.zip");
    // preferences
    private static final String ADDON_DIR_PREF = "addon_dir_pref";
    private static final String SAVED_VAR_DIR_PREF = "saved_vars_dir_pref";
    private static final String INPUT_FILE_PREF = "input_file_pref";
    private static final String OUTPUT_DIR_PREF = "output_dir_pref";
    public ButtonType selectButtonType;
    // paths
    private File addonDir;
    private File inputFile;
    private File outputFile;
    // session stuff
    private SessionManager sessionManager;

    private static ObservableList<SessionInfo> sessionInfos;

    // unzips the addon files into the specified folder,
    // receives installation directory from GUI (called on button click)
    @FXML
    void installAddon() {
        Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
        File destinationDir = Gui.promptForFolder("Select installation directory", prefs.get(ADDON_DIR_PREF, null));
        if (isValidDirectory(destinationDir)) {
            this.addonDir = destinationDir;
            prefs.put(ADDON_DIR_PREF, addonDir.getPath());
            // unzip addon files
            unzipAddon(destinationDir);
            locateSavedVariablesDir();
        }
    }

    // checks for valid installation directory
    private boolean isValidDirectory(File destinationDir) {
        if (destinationDir == null) return false; // user canceled selection, do nothing
        log.fine("Selected dir: " + destinationDir);
        // check for writing privileges
        if (!destinationDir.canWrite()) {
            Gui.errorMessage("No writing access to this directory, choose another one");
            return false;
        }
        // check for expected directory name
        if (!destinationDir.getName().endsWith("AddOns")) {
            boolean confirmation = Gui.confirm("Are you sure you want to install in this directory?" +
                    " It does not appear to be a WoW Addon folder: " + destinationDir);
            if (!confirmation) {
                System.out.println("Don't install");
                return false;
            }
        }
        return true;
    }

    // unzips addon files to installation directory
    private static void unzipAddon(File destinationDir) {
        try (ZipFile zipFile = new ZipFile(ADDON_ZIP.getAbsolutePath())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(destinationDir, entry.getName());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    OutputStream outputStream = new FileOutputStream(entryDestination);
                    zipFile.getInputStream(entry).transferTo(outputStream);
                }
            }
            log.info("Unzipped addon files");
        } catch (IOException e) {
            Gui.errorMessage("Error while unzipping addon files");
        }
    }

    // deducts the SavedVariables directory from the installation path
    private void locateSavedVariablesDir() {
        // from Addons directory, move up to "_retail_" directory
        Path addonPath = addonDir.toPath().toAbsolutePath();
        if (addonPath.getNameCount() < 2) { // can we move two directories up?
            Gui.notice("Can't locate your SavedVariables folder. Please select the input file manually.");
        } else {
            Path accountPath = getWTFAccountDir(addonPath);
            if (accountPath != null) {
                // Account-wide Folders are in UPPERCASE -> look for them
                List<Path> accountsFound = getUppercaseFolders(accountPath);
                if (accountsFound.size() == 1) { // one account on this installation
                    Path savedVarsDir = accountsFound.get(0).resolve("SavedVariables");
                    Gui.notice("Saved vars directory located: " + savedVarsDir);
                    Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
                    prefs.put(SAVED_VAR_DIR_PREF, savedVarsDir.toString());
                    String inputFilePath = savedVarsDir + File.separator + ADDON_NAME + ".lua";
                    inputFile = new File(inputFilePath);
                    prefs.put(INPUT_FILE_PREF, inputFilePath);
                } else {
                    Gui.notice("You seem to have multiple WoW accounts. Please select the input file manually.");
                }
            }
        }
    }

    // moves from Addons folder to ../../WTF/Account folder
    private static Path getWTFAccountDir(Path addonPath) {
        // ../.. first, move two directories up
        Path twoUp = addonPath.getRoot()
                .resolve(addonPath.subpath(0, addonPath.getNameCount() - 2));
        // then, move down to ./WTF/Account
        Path accountPath = twoUp.resolve("WTF").resolve("Account");
        if (accountPath.toFile().exists()) {
            return accountPath;
        } else {
            Gui.notice("Can't locate your SavedVariables folder. Please select the input file manually.");
            return null;
        }
    }

    // looks for folders that are named after account names (in uppercase) in WTF/Account folder
    private static List<Path> getUppercaseFolders(Path accountPath) {
        List<Path> accountsFound = new ArrayList<>();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**[A-Z]");
        try {
            Files.walkFileTree(accountPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path,
                                                         BasicFileAttributes attrs) {
                    if (matcher.matches(path)) {
                        log.info("Found match:" + path);
                        accountsFound.add(path);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            Gui.errorMessage("Something went wrong while looking for the SavedVariables folder");
        }
        return accountsFound;
    }

    // receives input file path from GUI (called on button click)
    @FXML
    void selectFile() {
        Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
        File selectedFile = Gui.promptForFile("Select SavedVariables file (usually: FeatureRecordingTool.lua)",
                prefs.get(INPUT_FILE_PREF, null));
        if (selectedFile != null) {
            if (selectedFile.exists()) {
                if (selectedFile.canRead()) {
                    this.inputFile = selectedFile;
                    prefs.put(INPUT_FILE_PREF, selectedFile.getPath());
                } else {
                    Gui.errorMessage("Error: Could not read the file: " + selectedFile.getPath());
                }
            }
        }
    }

    // exports the .lua file to .xml format (called on button click)
    @FXML
    void exportToXML() {
        //TODO: Allow specifying a file name
        boolean hasOutputDirectory = promptForOutputDirectory();
        if (!hasOutputDirectory) return; // user canceled
        boolean hasInputFile = inputFile != null;
        if (!hasInputFile) {
            hasInputFile = promptForInputFile();
        }
        if (!hasInputFile) return; // user canceled
        sessionManager = SessionManager.getInstance();
        List<Integer> sessionIDs = getSessionID(sessionManager);
        if (sessionIDs.size() > 0){
            sessionManager.exportToXML(inputFile, outputFile, sessionIDs);
            Gui.success("Converted  " + sessionIDs.size() + " sessions to xml");
        } else {
            Gui.notice("no session selected");
        }
    }

    private List<Integer> getSessionID(SessionManager sessionManager) {
        sessionInfos = FXCollections.observableList(sessionManager.getSessionList(inputFile));
        System.out.println("Got session infos");
        List<Integer> sessionIDs = new ArrayList<>();
        if (sessionInfos.isEmpty()) { // no session recorded
            Gui.errorMessage("There was no recording found in the input file");
        } else {
            System.out.println("There are " + sessionInfos.size() + " recorded sessions in this file.");
            if (sessionInfos.size() == 1) { // only one session
                sessionIDs.add(0);
            }
            if (sessionInfos.size() > 1) { // if more than 1 session, session selection is required
                System.out.println("Prompting for session");
                sessionIDs.addAll(Gui.promptForSession());
            }
        }
        System.out.println("Returning " + sessionIDs.size() + " session id(s): " + Arrays.toString(sessionIDs.toArray()));
        return sessionIDs;
    }

    private boolean promptForInputFile() {
        Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
        inputFile = Gui.promptForFile("Please select the input file", prefs.get(OUTPUT_DIR_PREF, null));
        if (inputFile == null) {
            Gui.errorMessage("There is no valid input file");
            return false;
        }
        if (!inputFile.canRead()) {
            Gui.errorMessage("Can not read file: " + inputFile.getAbsolutePath());
            return false;
        }
        return true;
    }

    private boolean promptForOutputDirectory() {
        Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
        File selectedDir = Gui.promptForFolder("Select export directory", prefs.get(OUTPUT_DIR_PREF, null));
        if (selectedDir == null) {
            return false;
        }
        prefs.put(OUTPUT_DIR_PREF, selectedDir.getPath());
        Path outPathComplete = selectedDir.toPath().resolve("out.xml");
        outputFile = outPathComplete.toFile();
        log.info("Output file will be saved to: " + outputFile.getAbsolutePath());
        return true;
    }

    static ObservableList<SessionInfo> getSessionInfos() {
        return sessionInfos;
    }
}
