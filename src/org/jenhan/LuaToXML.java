package org.jenhan;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.logging.Logger;

public interface LuaToXML {
    Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);



    // org.jenhan.Main interface function
    // reads lua session, writes xml file
    // returns true upon success, false upon failure
    default boolean exportToXML(File inputFile, File outputFile) {
        boolean success = false;
        LineNumberReader luaReader = prepareInput(inputFile);
        PrintWriter xmlWriter = prepareOutput(outputFile);

        //TODO: implement the actual conversion

        return success;
    }

    // Utility methods
    static boolean isAssignment(String line){
        String[] split = line.split("=");
        return split.length == 2;
    }

    static String getLuaFieldValue(String line) {
        if (isAssignment(line)){
            String[] split = line.split("=");
            // right hand side of line = value
            String valueSide = split[1].trim();
            // substring to omit trailing comma
            valueSide = valueSide.substring(0, valueSide.length()-1);
            // remove quotation marks
            if (valueSide.startsWith("\"")){
                valueSide = valueSide.substring(1, valueSide.length()-1);
            }
            return valueSide;
        } else {
            throw new InvalidParameterException("something went wrong while manipulating lua file strings");
        }
    }

    static String getLuaFieldKey(String line) {
        if (isAssignment(line)){
            String[] split = line.split("=");
            // left hand side of line = key
            String keySide = split[0].trim();
            // substring to omit quotation marks and brackets
            return keySide.substring(2, keySide.length()-2);
        } else {
            throw new InvalidParameterException("something went wrong while manipulating lua file strings");
        }
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
