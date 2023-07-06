package org.jenhan.wowfeatureextractiontool;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.prefs.*;

public class MainControl {
    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String ADDON_NAME = "FeatureRecordingTool";
    private static final File ADDON_ZIP = new File("src/main/resources/org/jenhan/wowfeatureextractiontool/WoWAddon.zip");
    // preferences
    private static final String ADDON_DIR_PREF = "addon_dir_pref";
    private static final String SAVED_VAR_DIR_PREF = "saved_vars_dir_pref";
    private static final String OUTPUT_DIR_PREF = "output_dir_pref";
    // paths
    private File addonDir;
    private File savedVarsDir;
    private File inputFile;
    private File outputFile;
    // session stuff
    private SessionManager sessionManager;
    List<Session.SessionInfo> sessionInfos;

    // unzips the addon files into the specified folder
    // receives installation directory from GUI
    @FXML
    public void installAddon(ActionEvent actionEvent) {
        Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
        File destinationDir = Gui.promptForFolder("Select installation directory", prefs.get(ADDON_DIR_PREF, null));
        if (destinationDir != null) {
            log.fine("Selected dir: " + destinationDir);
            // check for writing privileges
            if (!destinationDir.canWrite()){
                Gui.errorMessage("No writing access to this directory, choose another one");
                return;
            }
            if (!destinationDir.getName().endsWith("AddOns")){
                boolean confirmation = Gui.confirm("Are you sure you want to install in this directory?" +
                        " It does not appear to be a WoW Addon folder: " + destinationDir);
                if (!confirmation) return;
            }
            System.out.println("Carrying on");
            this.addonDir = destinationDir;
            prefs.put(ADDON_DIR_PREF, addonDir.getPath());
            // unzip addon files
            try (ZipFile zipFile = new ZipFile(ADDON_ZIP.getAbsolutePath());) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    File entryDestination = new File(destinationDir,  entry.getName());
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
            deductSavedVariablesDir();
        }
    }

    private void deductSavedVariablesDir() {
        // form Addons directory, move up to "_retail_" directory
        Path addonPath = addonDir.toPath().toAbsolutePath();
        System.out.println("Addon path: " + addonPath + ", name cont: " + addonPath.getNameCount());
        if (addonPath.getNameCount() < 4){
            // TODO: can't move up, handle error or stay silent?
            System.out.println("Can't move up the file tree from: " + addonPath);
        } else {
            // ../.. move two up
            Path twoUp = addonPath.getRoot()
                    .resolve(addonPath.subpath(0, addonPath.getNameCount()-2));
            System.out.println("Two up: " + twoUp);
            Path accountPath = twoUp.resolve("WTF").resolve("Account");
            System.out.println("Account path: " + accountPath);
            // TODO: deduct account folder (the ones in CAPS - can be more than one if users share one computer)
            // look for uppercase folder
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**[A-Z]");
            List<Path> accountsFound = new ArrayList<>();
            try {
                Files.walkFileTree(accountPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path path,
                                                     BasicFileAttributes attrs) throws IOException {
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
            if (accountsFound.size() == 1){ // in case of multiple accounts, don't bother, let the user decide later on which one to choose
                savedVarsDir = accountsFound.get(0).resolve("SavedVariables").toFile();
                System.out.println("Saved vars dir deducted: " + savedVarsDir);
                Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
                prefs.put(SAVED_VAR_DIR_PREF, savedVarsDir.getPath());
            }
        }
    }

    // receives installation file location from GUI
    void selectSavedVarFile(File inputFile) {
        if (inputFile.exists()){
            if (inputFile.isDirectory()){
                String fileName = inputFile + "/" + ADDON_NAME + ".lua";
                inputFile = new File(fileName);
                if (!inputFile.exists()){
                    Gui.errorMessage("Error: There is no " + ADDON_NAME + ".lua file in the selected folder!");
                    return;
                } // else, go on
            }
            if (inputFile.canRead()){
                System.out.println("Input file: " + inputFile);
                this.inputFile =  inputFile;
            } else {
                Gui.errorMessage("Error: Could not read the file: " + inputFile.getAbsolutePath());
            }
        }

    }

    void selectSession(int sessionID) { // receive session selection from GUI
        sessionManager = SessionManager.getInstance();
        sessionManager.exportToXML(inputFile, outputFile, sessionID);
    }

    void exportToXML(File outPath) {
        String outPathComplete = outPath.getName() + "/out.xml";
        outputFile = new File(outPathComplete);
        log.info("Output file will be saved to: " + outputFile.getAbsolutePath());
        if (inputFile == null) {
            inputFile = Gui.promptForFile("Please select the input file");
            if(inputFile == null){
                Gui.errorMessage("There is no valid input file");
                return;
            }
            if(!inputFile.canRead()){
                Gui.errorMessage("Can not read file: " + inputFile.getAbsolutePath());
                return;
            }
        }
            sessionManager = SessionManager.getInstance();
            sessionInfos = sessionManager.getSessionList(inputFile);
            if (sessionInfos.isEmpty()) { // no session recorded
                Gui.errorMessage("There was no recording found in the input file");
            } else if (sessionInfos.size() > 1) { // if more than 1 session, session selection is required
                Gui.promptForSession();
            } else { // only 1 session -> export without further ado
                sessionManager.exportToXML(inputFile, outputFile, 0);
            }

    }



    @FXML
    public void onSelectFileClick(ActionEvent actionEvent) {
        File selectedFile = Gui.promptForFile("Select SavedVariables file (usually: FeatureRecordingTool.lua)");
        if (selectedFile != null) {
            selectSavedVarFile(selectedFile);
        }
    }

    @FXML
    public void onExportToXmlClick(ActionEvent actionEvent) {
        Preferences prefs = Preferences.userNodeForPackage(MainControl.class);
        File selectedDir = Gui.promptForFolder("Select export directory", prefs.get(OUTPUT_DIR_PREF, null));
        if (selectedDir != null) {
            prefs.put(OUTPUT_DIR_PREF, selectedDir.getPath());
            exportToXML(selectedDir);
        }
    }


}