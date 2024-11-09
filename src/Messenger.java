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
import com.ericsson.hosasdk.api.hui.*;
import com.ericsson.hosasdk.api.ui.*;
import com.ericsson.hosasdk.api.*;

public class Messenger extends IpAppHosaUIManagerAdapter
    implements IpAppHosaUIManager
{
    private static final String SERVICE_TYPE_NAME = "SP_HOSA_USER_LOCATION";
    private IpHosaUIManager itsService;
    private FWproxy itsFramework;
    private int itsServiceAssignmentId;
    private Feature theFeature;

    public Messenger(Feature aFeature, FWproxy aFramework)
    {
        theFeature = aFeature;
        itsFramework = aFramework;
    }

    public void start()
    {
        itsService = (IpHosaUIManager) itsFramework.obtainSCF(IpHosaUIManager.class,
            "SP_HOSA_USER_INTERACTION");
        sendSMS("0", "0", "test");
        activateServiceNumber();
    }

    public void stop()
    {
        try
        {
            deactivateServiceNumber();
            // itsFramework.releaseSCF(itsService);
        }
        finally  
        {	// even if disposing SCS resources fails, 
            // we still want to dispose of our resources
            IpAppHosaUIManagerMgr.dispose(this);
        }
    }

    private void activateServiceNumber()
    {
        TpAddressRange origin = createE164Range("*");
        TpAddressRange destination = createE164Range("666");
        String serviceCode = "guru";
        TpUIEventCriteria criteria = new TpUIEventCriteria(origin,
            destination, serviceCode);
        itsServiceAssignmentId = itsService.createNotification(this,
            criteria);
    }

    private void deactivateServiceNumber()
    {
        itsService.destroyNotification(itsServiceAssignmentId);
    }

    public IpAppUI reportNotification(TpUIIdentifier aUI, TpUIEventInfo anInfo, int i)
    {
        TpAddress origin = anInfo.OriginatingAddress;
        TpAddress destination = anInfo.DestinationAddress;
        System.out.println("Received SMS from "
            + anInfo.OriginatingAddress.AddrString);
        sendSMS(destination, origin, theFeature.getQuote());
        return null;
    }

    public void sendSMS(String anOriginatingAddress, String aDestinationAddress, String aMessage)
    {
        sendSMS(makeTpAddress(anOriginatingAddress),
            makeTpAddress(aDestinationAddress), aMessage);
    }

    public static TpAddressRange createE164Range(String aNumberRange)
    {
        return new TpAddressRange(TpAddressPlan.P_ADDRESS_PLAN_E164,
            aNumberRange, // address
            "",  // name
            ""); // subaddress
    }

    private TpAddress makeTpAddress(String anE164Number)
    {
        return new TpAddress(TpAddressPlan.P_ADDRESS_PLAN_E164,
            anE164Number, "",
            TpAddressPresentation.P_ADDRESS_PRESENTATION_ALLOWED,
            TpAddressScreening.P_ADDRESS_SCREENING_USER_VERIFIED_PASSED,
            "");
    }

    public void sendSMS(TpAddress anOriginatingAddress, TpAddress aDestinationAddress, String aMessage)
    {
        System.out.println("Sending SMS to "
            + aDestinationAddress.AddrString + ": " + aMessage);
        // To send the message, the destination must be a
        // TpHosaTerminatingAddressList, instead of one address,
        // so create a list with one number
        TpHosaTerminatingAddressList recipients = new TpHosaTerminatingAddressList();
        recipients.ToAddressList = new TpAddress[1];
        recipients.ToAddressList[0] = aDestinationAddress;

        String subject = ""; // subject is not used with SMS

        // Reformat the incoming message string message to a TpHosaMessage
        TpHosaMessage message = new TpHosaMessage();
        message.Text(aMessage);

        // Send it as SMS
        TpHosaUIMessageDeliveryType deliveryType = TpHosaUIMessageDeliveryType.P_HUI_SMS;

        String billing = "for free";
        int response = 1;
        boolean deliverynotification = false;

        // Create a dummy delivery time (send immidiately)
        TpHosaDeliveryTime deliveryTime = new TpHosaDeliveryTime();
        deliveryTime.Dummy((short) 0);

        String validityTime = "2010-01-01 00:00:00.000";

        // Send message
        itsService.hosaSendMessageReq(this, anOriginatingAddress,
            recipients, subject, message, deliveryType, billing,
            response, deliverynotification, deliveryTime, validityTime);
    }

    /**
     * Called remotely <br>
     * Sending the message was a succes
     */
    public void hosaSendMessageRes(int anAssignmentID,
        TpHosaSendMessageReport[] aResponseList)
    {}

    public void notImplemented()
    {
        new UnsupportedOperationException("An unexpected callback method was invoked").printStackTrace();
    }
}
