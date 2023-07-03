import java.util.ArrayList;

public interface LuaToXML {
    ArrayList<Feature> featureList = new ArrayList<>();
    // returns true upon success, false upon failure
    default boolean exportToXML(String inPath, String outPath){
        //TODO: populate featureList from Lua file
        // TODO: write XML from features
        return true;
    };
}
