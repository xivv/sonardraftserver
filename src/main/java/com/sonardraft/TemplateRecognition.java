package com.sonardraft;

import com.sonardraft.db.Character;
import org.opencv.core.*;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.File;

public class TemplateRecognition {

    private TemplateRecognition () {

    }

    public static Character featureMatchingSimple ( Mat source ) {

        Character bestCharacter = null;
        Double bestValue = 1d;

        for ( Character character : Variables.characters ) {

            Mat template = character.getMat ();

            Mat resultMatrix = new Mat ();
            int result_cols = source.cols () - template.cols () + 1;
            int result_rows = source.rows () - template.rows () + 1;
            resultMatrix.create ( result_rows, result_cols, CvType.CV_32FC1 );

            Imgproc.matchTemplate ( source, template, resultMatrix, Variables.METHOD );

            MinMaxLocResult mmr = Core.minMaxLoc ( resultMatrix );

            if ( bestValue > mmr.minVal ) {
                bestCharacter = character;
                bestValue = mmr.minVal;
            }

        }

        return bestCharacter != null ? bestCharacter : new Character ( "None" );
    }

    public static Character featureMatchingSimple ( String path ) {

        Mat source = Imgcodecs.imread ( new File ( path ).getAbsolutePath () );
        return featureMatchingSimple ( source );
    }

    public static BufferedImage createResultImage ( Point matchLoc, Mat screenshot, Mat template ) {

        Imgproc.rectangle ( screenshot, matchLoc, new Point ( matchLoc.x + template.cols (), matchLoc.y + template.rows () ),
                            new Scalar ( 0, 255, 0 ), 2, 8, 0 );

        return Tools.toBufferedImage ( HighGui.toBufferedImage ( screenshot ) );
    }
}
