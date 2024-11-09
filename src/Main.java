/*
 * **************************************************************************
 * *                                                                        *
 * * Ericsson hereby grants to the user a royalty-free, irrevocable,        *
 * * worldwide, nonexclusive, paid-up license to copy, display, perform,    *
 * * prepare and have prepared derivative works based upon the source code  *
 * * in this sample application, and distribute the sample source code and  *
 * * derivative works thereof and to grant others the foregoing rights.     *
 * *                                                                        *
 * * ERICSSON DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,        *
 * * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS.       *
 * * IN NO EVENT SHALL ERICSSON BE LIABLE FOR ANY SPECIAL, INDIRECT OR      *
 * * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS    *
 * * OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE  *
 * * OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE *
 * * OR PERFORMANCE OF THIS SOFTWARE.                                       *
 * *                                                                        *
 * **************************************************************************
 */

import com.ericsson.hosasdk.api.*;
import com.ericsson.hosasdk.utility.framework.FWproxy;
import com.ericsson.hosasdk.utility.log.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main
{
    private Feature theFeature;
    private GUI theGUI;
    private boolean theIsStarted, theIsStopped;
    private FWproxy itsFramework;

    public static void main(String[] args)
        throws Exception
    {
        new Main("config/config.ini");
    }

    private Main(String aConfigFileName)
        throws IOException
    {
        SimpleTracer.SINGLETON.PRINT_STACKTRACES = false;
        HOSAMonitor.addListener(SimpleTracer.SINGLETON);

        Properties p = new Properties();
        p.load(new FileInputStream(aConfigFileName));
        itsFramework = new FWproxy(p);
        theFeature = new Feature(itsFramework, p);

        String s = "<HTML>" 
            + "<font size=\"4\"><p align=center>Users can request a message of wisdom by sending an SMS to service number 666.</p>"
            + "<p align=center>The Guru returns an SMS with food for thought.</p></font>"
            + "</HTML>";

        initGUI(s);
        theIsStopped = true;
        theGUI.updateState();
    }

    public boolean isStarted()
    {
        return theIsStarted;
    }

    public boolean isStopped()
    {
        return theIsStopped;
    }

    public void start()
    {
        theIsStarted = theIsStopped = false;
        theGUI.updateState();
        try
        {
            theFeature.start();
            theIsStarted = true;
        }
        catch (RuntimeException e)
        {
            theIsStopped = true;
            System.err.println(ObjectWriter.print(e));
            // throw e;
        }
        finally 
        {
            theGUI.updateState();
        }
    }

    public void stop()
    {
        theIsStarted = theIsStopped = false;
        theGUI.updateState();
        try
        {
            theFeature.stop();
        }
        finally 
        {
            theIsStopped = true;
            theGUI.updateState();
        }
    }

    void initGUI(String aDescription)
    {
        JFrame f = new JFrame();
        f.setTitle("Guru");
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(theGUI = new GUI(this, aDescription),
            BorderLayout.CENTER);
        f.pack();
        f.setLocation(100, 100);
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
			System.exit(0);
            }
        });
    }
}

