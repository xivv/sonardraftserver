package com.sonardraft;

import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sonardraft.db.Character;
import com.sonardraft.db.Comp;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Variables {

    /**
     * Konfiguration
     */

    private static final Logger logger = Logger.getLogger ( Variables.class.getName () );

    public static final String   CHARACTERURL = "https://na.leagueoflegends.com/en/game-info/champions/";
    public static final String[] IMAGEFORMATS = { "png" };

    public static       String BASE          = "";
    public static final String CHARACTERPATH = BASE + "characters\\";
    public static final String RESULTPATH    = BASE + "result\\";
    public static final String SCREENPATH    = BASE + "screenshots\\";

    public static final int        METHOD = Imgproc.TM_SQDIFF_NORMED;
    public static       Screensize resolution;

    /*
     * Quality
     */
    protected static final int[]   HISTOGRAMMSIZE     = { 64, 64, 64 };
    protected static final float[] HISTOGRAMMRANGE    = { 0, 128, 0, 128, 0, 128 };
    protected static final int[]   HISTOGRAMMCHANNELS = { 0, 1, 2 };

    /*
     * Statics
     */
    public static List<Character> characters = new ArrayList<> ();
    public static List<Comp>      comps      = new ArrayList<> ();

    private Variables () {
    }

    public static void main ( String[] args ) throws IOException {

        String baseURL = "http://championcounter.com/";
        //String baseURL = "http://lolcounter.com/champions/";
        String url = baseURL + "Zed";

        Document doc = Jsoup.connect ( url ).get ();

    }

    private static void loadCounterData () {

        try {

            BASE = System.getProperty ( "user.dir" ) + "\\";
            String baseURL = "http://lolcounter.com/champions/";

            for ( Character character : characters ) {

                String url = baseURL + character.getName ();

                List<Character> counters = new ArrayList<> ();
                character.setCounter ( counters );
                character.setMat ( null );
                character.setPriorityBonus ( null );

                String freshConfiguration = new GsonBuilder ().setPrettyPrinting ().create ().toJson ( character );
                try ( FileOutputStream outputStream = new FileOutputStream ( Variables.CHARACTERPATH + character.getName () + ".json" ) ) {
                    byte[] strToBytes = freshConfiguration.getBytes ();
                    outputStream.write ( strToBytes );
                }
            }

        } catch ( Exception e ) {
            logger.log ( Level.SEVERE, "Couldnt load counter data:" + e.getMessage () );
        }
    }

    public static void createFreshConfiguration () throws IOException {

        BASE = System.getProperty ( "user.dir" ) + "\\";
        for ( File character : new File ( Variables.CHARACTERPATH ).listFiles () ) {

            if ( !character.isDirectory () ) {
                Variables.characters.add ( new Character ( FilenameUtils.getBaseName ( character.getName () ) ) );
            }
        }

        String freshConfiguration = new Gson ().toJson ( characters );
        try ( FileOutputStream outputStream = new FileOutputStream ( BASE + "priorities.original.json" ) ) {
            byte[] strToBytes = freshConfiguration.getBytes ();
            outputStream.write ( strToBytes );
        }
    }

    public static void initialiseCharacters () {

        // We resize the images to match our resolution
        Tools.resizeImages ( Variables.CHARACTERPATH, 64 );

        // We iterate trough all configured characters
        File characterFolder = new File ( Variables.CHARACTERPATH );

        for ( File file : characterFolder.listFiles () ) {

            if ( FilenameUtils.getExtension ( file.getName () ).equals ( "json" ) ) {

                // Load the character configuration
                String priorityProperties = null;
                try {
                    priorityProperties = Resources.toString ( file.toURI ().toURL (), StandardCharsets.UTF_8 );
                } catch ( IOException e ) {
                    logger.log ( Level.SEVERE, "Couldnt initialise characters:" + e.getMessage () );
                }
                Character character = new Gson ().fromJson ( priorityProperties, new TypeToken<Character> () {

                }.getType () );

                // Load the characters image/mat

                Mat mat = Imgcodecs.imread ( Variables.CHARACTERPATH + FilenameUtils.getBaseName ( file.getName () ) + ".png" );
                character.setMat ( mat );
                Variables.characters.add ( character );
            }
        }

    }

    public static void initialiseComps () {

        File compFile = new File ( BASE + "comps.json" );
        try {
            String compString = Resources.toString ( compFile.toURI ().toURL (), StandardCharsets.UTF_8 );
            comps = new Gson ().fromJson ( compString, new TypeToken<List<Comp>> () {

            }.getType () );
        } catch ( IOException e ) {
            logger.log ( Level.SEVERE, "Couldnt initialise comps:" + e.getMessage () );
        }

    }

    public static boolean init () {

        BASE = System.getProperty ( "user.dir" ) + "\\";

        //		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        //
        //		if (screensize.width == 1920 && screensize.height == 1080) {
        //			resolution = Screensize.x1920x1080;
        //		} else if (screensize.width == 1024 && screensize.height == 768) {
        //			resolution = Screensize.x1024x768;
        //		}

        // Get configured character combo properties
        initialiseCharacters ();
        initialiseComps ();
        // loadCounterData ();

        return true;
    }

}
