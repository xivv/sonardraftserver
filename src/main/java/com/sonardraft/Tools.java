package com.sonardraft;

import com.sonardraft.db.Character;
import com.sonardraft.db.*;
import com.sonardraft.db.enums.Role;
import com.sun.jna.platform.WindowUtils;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Tools {

    private static final Logger logger = Logger.getLogger ( Tools.class.getName () );

    private Tools () {

    }

    public static       boolean clientRunning   = false;
    public static final boolean programmRunning = true;

    public static Draft getDraft () {

        List<Mat> screenshots = Tools.takeScreenshots ( true );
        Draft draft = new Draft ();

        int counter = 0;

        for ( Mat mat : screenshots ) {

            Character character = TemplateRecognition.featureMatchingSimple ( mat );

            if ( counter > 4 ) {
                draft.getRed ().getPicks ().add ( character );
            } else if ( counter < 5 ) {
                draft.getBlue ().getPicks ().add ( character );
            }

            counter++;
        }

        return draft;
    }

    public static void filterAvailableCharacters ( Draft draft, List<Character> available ) {
        for ( Character character : draft.getBlue ().getBanns () ) {
            available.remove ( character );
        }

        for ( Character character : draft.getRed ().getBanns () ) {
            available.remove ( character );
        }

        for ( Character character : draft.getBlue ().getPicks () ) {
            available.remove ( character );
        }

        for ( Character character : draft.getRed ().getPicks () ) {
            available.remove ( character );
        }
    }

    public static void calculateCombosAndCounters ( Team team, Team enemyTeam ) {
        for ( Character character : team.getPicks () ) {
            for ( Character possibleCombo : character.getCombos () ) {

                // If character is available and add the prio
                Character foundCombo = findByName ( team.getCombos (), possibleCombo.getName () );

                if ( foundCombo != null ) {
                    foundCombo.setPriority ( foundCombo.getPriority () + possibleCombo.getPriorityBonus () );

                    if ( foundCombo.getPriority () > 100 ) {
                        foundCombo.setPriority ( 100 );
                    }
                }
            }

            for ( Character possibleCounter : character.getCounter () ) {
                setCounterPriority ( possibleCounter, enemyTeam.getCombos () );
            }
        }
    }

    public static void setCounterPriority ( Character character, List<Character> available ) {

        // If character is available and add the prio
        Character foundCounter = findByName ( available, character.getName () );

        if ( foundCounter != null ) {
            foundCounter.setPriority ( foundCounter.getPriority () + character.getPriorityBonus () );

            if ( foundCounter.getPriority () > 100 ) {
                foundCounter.setPriority ( 100 );
            }
        }
    }

    public static void filterSystemTemplates ( Draft draft ) {

        draft.getBlue ().getCombos ().remove ( findByName ( draft.getBlue ().getCombos (), "None" ) );
        draft.getBlue ().getCombos ().remove ( findByName ( draft.getBlue ().getCombos (), "Picking" ) );

        draft.getRed ().getCombos ().remove ( findByName ( draft.getRed ().getCombos (), "None" ) );
        draft.getRed ().getCombos ().remove ( findByName ( draft.getRed ().getCombos (), "Picking" ) );
    }

    public static Draft getPriorityDraft ( Draft draft ) {

        List<Character> available = new ArrayList<> ();
        available.addAll ( Variables.characters );

        // Remove banned and picked
        filterAvailableCharacters ( draft, available );

        // Calculate
        draft.getBlue ().getCombos ().addAll ( cloneList ( available ) );
        draft.getRed ().getCombos ().addAll ( cloneList ( available ) );

        // Check for combos and counters
        calculateCombosAndCounters ( draft.getBlue (), draft.getRed () );
        calculateCombosAndCounters ( draft.getRed (), draft.getBlue () );

        // Filter None and Picking
        filterSystemTemplates ( draft );

        // Filter combos for roles

        draft.getBlue ().setOpenRoles ( remainingRoles ( filterPickedRoles ( draft.getBlue ().getPicks () ) ) );
        draft.getRed ().setOpenRoles ( remainingRoles ( filterPickedRoles ( draft.getRed ().getPicks () ) ) );
        draft.getBlue ().setCombos ( filterRoles ( draft.getBlue ().getCombos (), filterPickedRoles ( draft.getBlue ().getPicks () ) ) );
        draft.getRed ().setCombos ( filterRoles ( draft.getRed ().getCombos (), filterPickedRoles ( draft.getRed ().getPicks () ) ) );

        // Set comps available
        draft.setComps ( getCompsAvailable ( draft, Variables.comps ) );
        // Order by priority
        Collections.sort ( draft.getBlue ().getCombos (), ( o1, o2 ) -> o2.getPriority ().compareTo ( o1.getPriority () ) );
        Collections.sort ( draft.getRed ().getCombos (), ( o1, o2 ) -> o2.getPriority ().compareTo ( o1.getPriority () ) );

        return draft;
    }

    private static List<Role> filterPickedRoles ( List<Character> picks ) {

        List<Role> remainingRoles = new ArrayList<> ();
        EnumMap<Role, Integer> availableRoles = new EnumMap<> ( Role.class );
        getAvailableRoles ( availableRoles, picks );

        // If the remaining roles equal the characters which means that all roles have
        // to be taken
        if ( availableRoles.size () == picks.size () ) {
            remainingRoles.addAll ( availableRoles.keySet () );
        } else {
            int iterations = 0;
            while ( iterations < 10 ) {
                for ( Character character : picks ) {
                    if ( !character.getRoles ().isEmpty () && character.getRoles ().size () == 1 || getSameRoles ( character, picks )
                                    .size () == picks.size () - 1 ) {
                        remainingRoles.addAll ( character.getRoles () );
                        removeRoles ( character.getRoles (), picks );
                    }
                }
                iterations++;
            }

        }

        return remainingRoles;

    }

    private static List<Character> getSameRoles ( Character character, List<Character> characters ) {

        List<Character> foundCharacters = new ArrayList<> ();

        for ( Character c : characters ) {
            if ( c.getRoles ().size () == character.getRoles ().size () && character.getRoles ().containsAll ( c.getRoles () ) ) {
                foundCharacters.add ( c );
            }
        }

        return foundCharacters;
    }

    private static void getAvailableRoles ( EnumMap<Role, Integer> availableRoles, List<Character> characters ) {

        for ( Character character : characters ) {

            for ( Role role : character.getRoles () ) {

                if ( !availableRoles.containsKey ( role ) ) {
                    availableRoles.put ( role, 0 );
                }

                availableRoles.put ( role, availableRoles.get ( role ) + 1 );
            }
        }

    }

    private static void removeRoles ( List<Role> roles, List<Character> characters ) {

        for ( Character character : characters ) {
            character.getRoles ().removeAll ( roles );
        }
    }

    private static List<Role> remainingRoles ( List<Role> roles ) {
        List<Role> remainingRoles = new ArrayList<> ( EnumSet.allOf ( Role.class ) );
        remainingRoles.removeAll ( roles );
        return remainingRoles;
    }

    private static boolean isCharacterAvailableOrPicked ( Character character, Team team ) {

        boolean champAlreadyPicked = team.getPicks ().stream ().filter ( c1 -> c1.getName ().equals ( character.getName () ) ).findAny ()
                                         .orElse ( null ) != null;

        boolean champAvailable = team.getCombos ().stream ().filter ( c1 -> c1.getName ().equals ( character.getName () ) ).findAny ()
                                     .orElse ( null ) != null;

        return champAlreadyPicked || champAvailable;

    }

    private static List<Comp> getCompsAvailable ( Draft draft, List<Comp> comps ) {

        List<Comp> availableComps = new ArrayList<> ();

        compLoop:
        for ( Comp comp : comps ) {

            characterLoop:
            for ( CompCharacter character : comp.getPicks () ) {

                // If we didnt pick the champ yet and it is not available for us
                if ( !isCharacterAvailableOrPicked ( character, draft.getBlue () ) ) {

                    for ( Character alternative : character.getAlternatives () ) {
                        if ( isCharacterAvailableOrPicked ( alternative, draft.getBlue () ) ) {
                            continue characterLoop;
                        }
                    }

                    continue compLoop;
                }

            }

            availableComps.add ( comp );

            comp.getBanns ().stream ().forEach ( character -> {
                setCounterPriority ( character, draft.getRed ().getCombos () );
            } );

        }
        return availableComps;
    }

    private static List<Character> filterRoles ( List<Character> combos, List<Role> roles ) {

        List<Character> filtered = new ArrayList<> ();

        for ( Role role : remainingRoles ( roles ) ) {
            filtered.addAll ( combos.stream ()
                                    .filter ( character -> character.getRoles ().contains ( role ) && !filtered.contains ( character ) )
                                    .collect ( Collectors.toList () ) );
        }

        return filtered;
    }

    public static List<Mat> takeScreenshots ( boolean saveResult ) {

        List<Mat> result = new ArrayList<> ();

        if ( isClientRunning () ) {

            if ( saveResult ) {
                clearFolder ( new File ( Variables.SCREENPATH ) );
            }

            WindowUtils.getAllWindows ( true ).forEach ( desktopWindow -> {

                if ( desktopWindow.getTitle ().equals ( "League of Legends" ) ) {

                    // CHAR 1
                    // 50x100 - 70x70 - 100
                    try {
                        Robot robot = new Robot ();
                        for ( var a = 0; a < 2; a++ ) {

                            Point base = new Point ();

                            if ( a == 0 ) {
                                base = new Point ( desktopWindow.getLocAndSize ().x + 10, desktopWindow.getLocAndSize ().y + 100 );
                            } else if ( a == 1 ) {
                                base = new Point ( desktopWindow.getLocAndSize ().x + 1195, desktopWindow.getLocAndSize ().y + 100 );
                            }

                            for ( var i = 0; i < 5; i++ ) {

                                Rectangle rect = new Rectangle ( base.x, base.y + i * 80, 110 - ( a == 1 ? 40 : 0 ), 70 );

                                BufferedImage image = robot.createScreenCapture ( rect );

                                // Convert image to mat so we dont need to save it for performance
                                result.add ( BufferedImage2Mat ( image ) );

                                if ( saveResult ) {
                                    saveBufferedImage ( image, Variables.SCREENPATH + a + i + ".png" );
                                }
                            }
                        }
                    } catch ( Exception e ) {
                        logger.log ( Level.SEVERE, "Couldnt initialise robot for finding the client:" + e.getMessage () );
                    }
                }
            } );
        }
        return result;
    }

    public static void resizeImages ( String path, Integer size ) {

        File folder = new File ( path );

        for ( File file : folder.listFiles () )
            if ( !file.isDirectory () && FilenameUtils.isExtension ( file.getName (), Variables.IMAGEFORMATS ) ) {

                BufferedImage image = loadBufferedImage ( file.getAbsolutePath () );

                if ( image != null ) {
                    if ( image.getHeight () != size ) {
                        BufferedImage resizedImage = resize ( image, size, size );
                        saveBufferedImage ( resizedImage, file.getAbsolutePath () );
                    }
                } else {
                    logger.log ( Level.SEVERE, "Character image " + file.getName () + " not found" );
                }
            }

    }

    public static void clearFolder ( File folder ) {
        for ( File file : folder.listFiles () )
            if ( !file.isDirectory () ) {
                try {
                    Files.delete ( file.toPath () );
                } catch ( IOException e ) {
                    logger.log ( Level.SEVERE, "Couldnt clear folder:" + e.getMessage () );
                }
            }
    }

    public static BufferedImage toBufferedImage ( Image image ) {

        if ( image instanceof BufferedImage ) {
            return ( BufferedImage ) image;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage ( image.getWidth ( null ), image.getHeight ( null ), BufferedImage.TYPE_INT_ARGB );

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics ();
        bGr.drawImage ( image, 0, 0, null );
        bGr.dispose ();

        // Return the buffered image
        return bimage;

    }

    private static BufferedImage resize ( BufferedImage img, int height, int width ) {
        Image tmp = img.getScaledInstance ( width, height, Image.SCALE_SMOOTH );
        BufferedImage resized = new BufferedImage ( width, height, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = resized.createGraphics ();
        g2d.drawImage ( tmp, 0, 0, null );
        g2d.dispose ();
        return resized;
    }

    public static BufferedImage loadBufferedImage ( String path ) {

        try {
            File bufferedImage = new File ( path );
            return ImageIO.read ( bufferedImage );
        } catch ( IOException e ) {
            logger.log ( Level.SEVERE, "Couldnt load image:" + e.getMessage () );
        }
        return null;
    }

    public static void saveBufferedImage ( BufferedImage image, String path ) {

        try ( AsynchronousFileChannel asyncFile = AsynchronousFileChannel
                        .open ( Paths.get ( path ), StandardOpenOption.WRITE, StandardOpenOption.CREATE ) ) {
            asyncFile.write ( convertImageData ( image ), 0 );
        } catch ( IOException e ) {
            logger.log ( Level.SEVERE, "Couldnt save image:" + e.getMessage () );
        }

    }

    public static boolean isClientRunning () {
        try {
            Runtime runtime = Runtime.getRuntime ();
            String[] cmds = { "cmd", "/c", "tasklist" };
            Process proc = runtime.exec ( cmds );
            InputStream inputstream = proc.getInputStream ();
            InputStreamReader inputstreamreader = new InputStreamReader ( inputstream );
            BufferedReader bufferedreader = new BufferedReader ( inputstreamreader );
            String line = "";

            StringBuilder bld = new StringBuilder ();

            while ( ( line = bufferedreader.readLine () ) != null ) {
                bld.append ( line );
            }

            return bld.toString ().contains ( "LeagueClient.exe" );
        } catch ( Exception e ) {
            logger.log ( Level.SEVERE, "Couldnt read process list:" + e.getMessage () );
            return false;
        }
    }

    public static Mat BufferedImage2Mat ( BufferedImage image ) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream ();
        ImageIO.write ( image, "png", byteArrayOutputStream );
        byteArrayOutputStream.flush ();
        return Imgcodecs.imdecode ( new MatOfByte ( byteArrayOutputStream.toByteArray () ), Imgcodecs.IMREAD_UNCHANGED );
    }

    public static ByteBuffer convertImageData ( BufferedImage bi ) {
        ByteArrayOutputStream out = new ByteArrayOutputStream ();
        try {
            ImageIO.write ( bi, "png", out );
            return ByteBuffer.wrap ( out.toByteArray () );
        } catch ( IOException e ) {
            logger.log ( Level.SEVERE, "Couldnt convert image to bytebuffer:" + e.getMessage () );
        }
        return null;
    }

    public static Character findByName ( Collection<Character> list, String name ) {
        return list.stream ().filter ( character -> name.equals ( character.getName () ) ).findFirst ().orElse ( null );
    }

    public static List<Character> cloneList ( List<Character> characters ) {

        List<Character> clone = new ArrayList<> ();

        for ( Character character : characters ) {
            clone.add ( new Character ( character ) );
        }

        return clone;
    }
}
