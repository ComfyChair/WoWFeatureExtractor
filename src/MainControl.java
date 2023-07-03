public class MainControl {
    //"eager" initialization, as the control class is needed right away
    private static final MainControl instance = new MainControl();

    private MainControl(){}

    public static MainControl getInstance() {
        return instance;
    }
    void installAddon(){

    }
    void selectFolder(){

    }
    void selectSession(){

    }
    void exportToXML(){

    }
}
