package eu.uberdust.model;

/**
 * Created with IntelliJ IDEA.
 * User: amaxilatis
 * Date: 12/1/12
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Uberdust {
    private static Uberdust instance = null;
    private String uberdustURL;

    public static Uberdust getInstance() {
        synchronized (Uberdust.class) {
            if (instance == null) {
                instance = new Uberdust();
            }
            return instance;
        }
    }

    public void setUberdustURL(final String uberdustURL) {
        this.uberdustURL = uberdustURL.replaceAll("http://", "");

    }

    public String getUberdustURL() {
        return uberdustURL;
    }
}
