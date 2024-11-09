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

import com.ericsson.hosasdk.utility.framework.FWproxy;
import com.ericsson.hosasdk.api.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Feature
{
    private FWproxy itsFramework;
    private Messenger itsMessenger;
    private Properties itsProperties;
    private int itsNextQuote;

    public Feature(FWproxy aFramework, Properties p)
    {
        itsFramework = aFramework;
        itsProperties = p;
        itsMessenger = new Messenger(this, itsFramework);
    }

    public String getDescription()
    {
        return "";
    }

    public void start()
    {
        itsMessenger.start();
    }

    public String getQuote()
    {
        String quote = itsProperties.getProperty("quote."
            + itsNextQuote);
        if (quote == null)
        {
            quote = itsProperties.getProperty("quote."
                + (itsNextQuote = 0));
        }
        if (quote == null)
        {
            quote = "";
        }
        itsNextQuote++;
        return quote;
    }

    public void stop()
    {
        itsMessenger.stop();
    }
}
