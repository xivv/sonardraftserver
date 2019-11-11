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

import java.awt.*;
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
    public static final String COMPPATH      = BASE + "comps\\";
    public static final String SCREENPATH    = BASE + "screenshots\\";

    public static final int        METHOD = Imgproc.TM_SQDIFF_NORMED;
    public static       Screensize resolution;

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

    public static <T> List<T> initialiseFolder ( String path, Class<T> clazz ) {

        File folder = new File ( path );
        List<T> result = new ArrayList<> ();
        Gson gson = new GsonBuilder ().create ();

        for ( File file : folder.listFiles () ) {

            if ( FilenameUtils.getExtension ( file.getName () ).equals ( "json" ) ) {

                try {
                    String content = Resources.toString ( file.toURI ().toURL (), StandardCharsets.UTF_8 );
                    T object = gson.fromJson ( content, TypeToken.of ( clazz ).getType () );

                    result.add ( object );
                } catch ( IOException e ) {
                    logger.log ( Level.SEVERE, "Couldnt initialise: " + clazz.getName () + " -> " + e.getMessage () );
                }

            }
        }
        return result;
    }

    public static void initialiseCharacters () {

        // We resize the images to match our resolution
        Tools.resizeImages ( Variables.CHARACTERPATH, 64 );
        Variables.characters.addAll ( Variables.<Character>initialiseFolder ( Variables.CHARACTERPATH, Character.class ) );
        Variables.characters.parallelStream ().forEach ( character -> {
            Mat mat = Imgcodecs.imread ( Variables.CHARACTERPATH + character.getName () + ".png" );
            character.setMat ( mat );
        } );

    }

    public static void initialiseComps () {
        Variables.comps.addAll ( Variables.<Comp>initialiseFolder ( Variables.COMPPATH, Comp.class ) );
    }

    public static boolean init () {

        BASE = System.getProperty ( "user.dir" ) + "\\";

        Dimension screensize = Toolkit.getDefaultToolkit ().getScreenSize ();

        if ( screensize.width >= 1920 && screensize.height >= 1080 ) {
            resolution = Screensize.x1920x1080;
        } else if ( screensize.width < 1920 && screensize.height < 1080 ) {
            resolution = Screensize.x1024x768;
            logger.log ( Level.SEVERE, "This resolution is not supported" );
            System.exit ( 0 );
        }

        // Get configured character combo properties
        initialiseCharacters ();
        initialiseComps ();
        // loadCounterData ();

        return true;
    }

}
