package org.jenhan.wowfeatureextractiontool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Main controller class **/
public class MainControl {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String ADDON_NAME = "FeatureRecordingTool";
    private static final File ADDON_ZIP =
            new File("src/main/resources/org/jenhan/wowfeatureextractiontool/WoWAddon.zip");
    private String installFolderExpected;
    // preferences
    private static final String ADDON_DIR_PREF = "addon_dir_pref";
    private static final String SAVED_VAR_DIR_PREF = "saved_vars_dir_pref";
    private static final String INPUT_FILE_PREF = "input_file_pref";
    private static final String OUTPUT_DIR_PREF = "output_dir_pref";
    // initialize empty observable list for testing
    private static ObservableList<Session> sessionList = FXCollections.observableList(new ArrayList<>());
    // paths
    private File addonDir;
    private File inputFile;
    private File outputFile;

    /** unzips addon files to installation directory **/
    private static void unzipAddon(File destinationDir) {
        try (ZipFile zipFile = new ZipFile(ADDON_ZIP.getAbsolutePath())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            boolean confirmOverwrite = false;
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(destinationDir, entry.getName());
                if (entry.isDirectory()) {
                    boolean created = entryDestination.mkdirs();
                    if (!created && !confirmOverwrite){
                        confirmOverwrite = Gui.confirmationDialog("Overwrite existing installation in "
                                + destinationDir + "?");
                        if (!confirmOverwrite) {
                            log.info("Aborted installing addon");
                            return;
                        }
                    }
                } else {
                    OutputStream outputStream = new FileOutputStream(entryDestination);
                    zipFile.getInputStream(entry).transferTo(outputStream);
                }
            }
            log.info("Unzipped addon files");
        } catch (IOException e) {
            handleError("Error while unzipping addon files", e);
        }
    }

    /** moves from Addons folder to ../../WTF/Account folder **/
    private static Path getWTFAccountDir(Path addonPath) {
        // ../.. first, move two directories up
        Path twoUp = addonPath.getRoot()
                .resolve(addonPath.subpath(0, addonPath.getNameCount() - 2));
        // then, move down to ./WTF/Account
        Path accountPath = twoUp.resolve("WTF").resolve("Account");
        if (accountPath.toFile().exists()) {
            return accountPath;
        } else {
            handleUserfeedback(Alert.AlertType.INFORMATION,
                    "Can't locate your SavedVariables folder. Please select the input file manually.", "");
            return null;
        }
    }

    /** looks for folders that are named after account names (in uppercase) in WTF/Account folder **/
    private static List<Path> detectAccountFolders(Path accountPath) {
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
            handleError("Something went wrong while looking for the SavedVariables folder", e);
        }
        return accountsFound;
    }

    static boolean confirmationDialog(String message) {
        if (Gui.getPrimaryStage() != null){
            return Gui.confirmationDialog(message);
        } else {
            return true; // no confirmation request without GUI, as in test cases
        }
    }

    /** unzips the addon files into the specified folder **/
    @FXML
    void installAddon() {
        Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
        String separator = FileSystems.getDefault().getSeparator();
        installFolderExpected = "Interface" + separator + "AddOns";
        File destinationDir = Gui.promptForFolder(
                "Select installation directory", prefs.get(ADDON_DIR_PREF, null));
        if (isValidInstallDirectory(destinationDir)) {
            this.addonDir = destinationDir;
            prefs.put(ADDON_DIR_PREF, addonDir.getPath());
            unzipAddon(destinationDir);
            locateSavedVariablesDir();
        } else {
            handleUserfeedback(Alert.AlertType.ERROR, "Sorry, this is an invalid installation directory", "");
        }
    }

    /** checks if a selected directory is valid for addon installation
     * @return true for a valid destination directory **/
    private boolean isValidInstallDirectory(File destinationDir) {
        if (!isValidDirectory(destinationDir)) return false;
        boolean confirmation = true;
        if (!destinationDir.getName().endsWith(installFolderExpected)) { // check for expected directory name
            confirmation = Gui.confirmationDialog("Are you sure you want to install in this directory?" +
                    " It does not appear to be a WoW Addon folder: " + destinationDir);
            if (!confirmation) {
                log.info("Aborted installing addon");
            }
        }
        return confirmation;
    }

    /** checks for writing access to a directory or canceled selection
     * @return true for a valid destination directory **/
    private static boolean isValidDirectory(File destinationDir) {
        if (destinationDir == null) return false;
        log.fine("Selected dir: " + destinationDir);
        // check for writing privileges
        if (!destinationDir.canWrite()) {
            handleUserfeedback(Alert.AlertType.ERROR,
                    "No writing access to this directory, choose another one", "Access Error");
            return false;
        }
        return true;
    }

    /** deducts the SavedVariables directory from the installation path **/
    private void locateSavedVariablesDir() {
        // from Addons directory, move up to "_retail_" directory
        Path addonPath = addonDir.toPath().toAbsolutePath();
        if (addonPath.getNameCount() < 2) { // can we move two directories up?
            handleUserfeedback(Alert.AlertType.INFORMATION,
                    "Can't locate your SavedVariables folder. Please select the input file manually.", "");
        } else {
            Path accountPath = getWTFAccountDir(addonPath);
            if (accountPath != null) {
                // Account-wide Folders are in UPPERCASE -> look for them
                List<Path> accountsFound = detectAccountFolders(accountPath);
                if (accountsFound.size() == 1) { // one account on this installation
                    Path savedVarsDir = accountsFound.get(0).resolve("SavedVariables");
                    handleUserfeedback(Alert.AlertType.INFORMATION, ("Saved vars directory located: " + savedVarsDir), "");
                    Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
                    prefs.put(SAVED_VAR_DIR_PREF, savedVarsDir.toString());
                    String inputFilePath = savedVarsDir + File.separator + ADDON_NAME + ".lua";
                    inputFile = new File(inputFilePath);
                    prefs.put(INPUT_FILE_PREF, inputFilePath);
                } else {
                    handleUserfeedback(Alert.AlertType.INFORMATION,
                            "You seem to have multiple WoW accounts. Please select the input file manually.", "");
                }
            }
        }
    }

    /** receives input file path from GUI (called on button click) **/
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
                    handleUserfeedback(Alert.AlertType.ERROR,
                            "Error: Could not read the file: " + selectedFile.getPath(), "Error");
                }
            }
        }
    }

    /** exports the .lua file to .xml format; called from GUI on button click **/
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
        // get going
        SessionManager sessionManager = SessionManager.getInstance();
        List<Integer> sessionIDs = selectSessions(sessionManager);
        if (sessionIDs.size() > 0) {
            List<File> outList = sessionManager.exportToXML(outputFile, sessionIDs);
            handleUserfeedback(Alert.AlertType.INFORMATION,
                    "Exported  " + outList.size() + " sessions to xml", "Sessions converted");
        }
    }

    /** retrieves session list from session manager, prompts for session selection if more than one session is returned
     * @param sessionManager the SessionManager
     * @return list of selected session IDs **/
    private List<Integer> selectSessions(SessionManager sessionManager) {
        sessionList = FXCollections.observableList(sessionManager.getSessionList(inputFile));
        List<Integer> sessionIDs = new ArrayList<>();
        if (sessionList.isEmpty()) { // no session recorded
            handleUserfeedback(Alert.AlertType.ERROR, "There was no recording found in the input file", "");
        } else {
            if (sessionList.size() == 1) { // only one session
                sessionIDs.add(0);
            }
            if (sessionList.size() > 1) { // if more than 1 session, session selection is required
                sessionIDs.addAll(Gui.promptForSession());
            }
        }
        return sessionIDs;
    }

    /** initiates user prompt for the input file location
     * @return true if a valid file was selected**/
    private boolean promptForInputFile() {
        Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
        inputFile = Gui.promptForFile("Please select the input file", prefs.get(OUTPUT_DIR_PREF, null));
        if (inputFile == null) {
            handleUserfeedback(Alert.AlertType.ERROR, "There is no valid input file", "");
            return false;
        }
        if (!inputFile.canRead()) {
            handleUserfeedback(Alert.AlertType.ERROR, "Can not read file: " + inputFile.getAbsolutePath(), "");
            return false;
        }
        return true;
    }

    /** initiates user prompt for the output directory location
     * @return true if a valid directory was selected **/
    private boolean promptForOutputDirectory() {
        Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
        File selectedDir = Gui.promptForFolder("Select export directory", prefs.get(OUTPUT_DIR_PREF, null));
        if (!isValidDirectory(selectedDir)) return false;
        prefs.put(OUTPUT_DIR_PREF, selectedDir.getPath());
        outputFile = selectedDir.toPath().toFile();
        log.info("Output file will be saved to: " + outputFile.getAbsolutePath() + ".xml");
        return true;
    }

    /** central error handler with Exception
     * @param message the error message
     * @param e the exception **/
    static void handleError(String message, Exception e) {
        if (Gui.getPrimaryStage() != null){
            Gui.feedbackDialog(Alert.AlertType.ERROR, message, "");
        } else {
            System.err.println(message);
        }
        System.err.println(e.getMessage());
        e.printStackTrace();
    }

    /** central error handler without Exception
     * @param message the error message **/
    static void handleUserfeedback(Alert.AlertType alertType, String message, String title) {
        if (Gui.getPrimaryStage() != null){
            Gui.feedbackDialog(alertType, message, title);
        } else {
            System.out.println(alertType + " - " +  title + ": " + message);
        }
    }

    /** getter for session info, necessary for GUI display
     * @return list of sessions to populate the session selection table **/
    static ObservableList<Session> sessionList() {
        return sessionList;
    }

}
