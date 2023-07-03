import java.io.*;
import java.util.ArrayList;
import java.util.List;

public interface LuaToXML {
    // Main interface function
    // reads lua session, writes xml file
    // returns true upon success, false upon failure
    default boolean exportToXML(File inputFile, File outputFile) {
        LineNumberReader luaReader = prepareInput(inputFile);
        PrintWriter xmlWriter = prepareOutput(outputFile);
        // TODO: the actual conversion goes here
        // TODO: should not happen, but handle it anymays
        return xmlWriter != null;
    }

    // Utility for SessionManager class
    static List<Session> readSessionInfo(File luaFile) {
        LineNumberReader luaReader = prepareInput(luaFile);
        List<Session> sessionList = new ArrayList<>();
        //TODO: read the lua file, find sessions, populate sessionList
        return sessionList;
    }

    // direct file access functions with exception handling

    static private LineNumberReader prepareInput(File inputFile) {
        LineNumberReader luaReader;
        try {
            luaReader = new LineNumberReader(new FileReader(inputFile));
        } catch (FileNotFoundException e) {
            // TODO: handle input exception with user feedback dialog
            throw new RuntimeException("Could not read file", e);
        }
        return luaReader;
    }

    private static PrintWriter prepareOutput(File outputFile) {
        PrintWriter xmlWriter;
        try {
            xmlWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
        } catch (IOException e) {
            // TODO: handle output exception with user feedback dialog
            throw new RuntimeException("Could not write file", e);
        }
        return xmlWriter;
    }

}
