package org.jenhan.wowfeatureextractiontool;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Session Management Singleton **/
class SessionManager {
    private static final String OUTFILE_EXTENSION = ".xml";
    private static SessionManager instance;
    private List<Session> sessionList;

    /** private constructor **/
    private SessionManager() {
    }

    /** @return the one and only instance **/
    static SessionManager getInstance() {
        // "lazy" initialization: delay initialization until needed
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /** @return the list of all the sessions in the given file
     * @param luaFile the input file **/
    List<Session> getSessionList(File luaFile) {
        LuaReader luaReader = new LuaReader();
        sessionList = luaReader.readFile(luaFile);
        return sessionList;
    }

    /** initiates export of the selected session to xml
     * @param outFile outputFile
     * @param sessionIDs List if session ids = position in internal session list
     * @return List of converted files **/
    List<File> exportToXML(File outFile, List<Integer> sessionIDs) {
        List<File> outList = new ArrayList<>();
        if (sessionIDs.size() == 1) { // single session, no additional identifier for output
            exportSingleSession(sessionIDs.get(0), outFile, outList);
        } else { // multiple session, append session id to file name
            Path outPath = outFile.getParentFile().toPath();
            String outName = outFile.getName();
            String outTruncName = outName.substring(0, outName.length()-4) + "_";
            for (Integer sessionID : sessionIDs
            ) {
                String newOutName = outTruncName + sessionID + ".xml";
                File thisOutFile  = outPath.resolve(new File(newOutName).toPath()).toFile();
                System.out.println("Outfile: " + newOutName);
                exportSingleSession(sessionID, thisOutFile, outList);
                System.out.println("Outpath: " + thisOutFile.getAbsolutePath());
            }
        }
        return outList;
    }

    private void exportSingleSession(int sessionID, File outFile, List<File> outList) {
        boolean success = sessionList.get(sessionID).exportToXML(outFile);
        if (success){
            outList.add(outFile);
        }
    }

    /** extends the path with a given file name
     * @param path the path
     * @param fileName the file name **/
    private File extendPath(Path path, String fileName) {
        return path.resolve(fileName).toFile();
    }

}
