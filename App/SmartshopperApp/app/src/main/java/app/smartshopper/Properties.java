package app.smartshopper;

/**
 * This class contains of constants that are important for the whole project (e.g. username).
 *
 * Created by hauke on 01.06.16.
 */
public class Properties {
    private static final Properties INSTANCE = new Properties();

    private String _username;

    private Properties(){
        _username = "Max Mustermann";
    }

    public static Properties getInstance(){
        return INSTANCE;
    }

    public String getUserName(){
        return _username;
    }
}
