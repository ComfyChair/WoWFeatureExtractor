public interface LuaToXML {
    // returns true upon success, false upon failure
    default boolean exportToXML(){
        return true;
    };
}
