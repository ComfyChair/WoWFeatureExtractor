package org.jenhan;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface LuaToXML {
    Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // org.jenhan.Main interface function
    // reads lua session, writes xml file
    // returns true upon success, false upon failure
    default boolean exportToXML(File inputFile, File outputFile) {
        LineNumberReader luaReader = prepareInput(inputFile);
        PrintWriter xmlWriter = prepareOutput(outputFile);
        // TODO: the actual conversion goes here
        return xmlWriter != null;
    }

    // direct file access functions with exception handling
    static LineNumberReader getNumberReader(File luaFile) {
        return prepareInput(luaFile);
    }

    private static LineNumberReader prepareInput(File inputFile) {
        LineNumberReader luaReader = null;
        try {
            luaReader = new LineNumberReader(new FileReader(inputFile));
        } catch (FileNotFoundException e) {
            // TODO: handle input exception with user feedback dialog
            log.severe("Could not read file");
        }
        return luaReader;
    }

    private static PrintWriter prepareOutput(File outputFile) {
        PrintWriter xmlWriter = null;
        try {
            xmlWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
        } catch (IOException e) {
            // TODO: handle output exception with user feedback dialog
            log.severe("output error");
        }
        return xmlWriter;
    }

}
