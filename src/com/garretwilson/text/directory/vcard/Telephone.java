package com.garretwilson.text.directory.vcard;

import java.util.*;
import com.garretwilson.itu.*;

/**An object representing the "TEL" type of a vCard <code>text/directory</code>
	profile as defined in <a href="http://www.ietf.org/rfc/rfc2426.txt">RFC 2426</a>,
	"vCard MIME Directory Profile".
@author Garret Wilson
*/
public class Telephone extends TelephoneNumber
{

	/**Indicates no telephone type is specified.*/
	public final static int NO_TELEPHONE_TYPE=0;
	/**A telephone number associated with a residence.*/
	public final static int HOME_TELEPHONE_TYPE=1<<0;
	/**A telephone number that has voice messaging support.*/
	public final static int MESSAGE_TELEPHONE_TYPE=1<<1;
	/**A telephone number associated with a place of work.*/
	public final static int WORK_TELEPHONE_TYPE=1<<2;
	/**A preferred-use telephone number.*/
	public final static int PREFERRED_TELEPHONE_TYPE=1<<3;
	/**A voice telephone number.*/
	public final static int VOICE_TELEPHONE_TYPE=1<<4;
	/**A facsimile telephone number.*/
	public final static int FAX_TELEPHONE_TYPE=1<<5;
	/**A cellular telephone number.*/
	public final static int CELL_TELEPHONE_TYPE=1<<6;
	/**A video conferencing telephone number.*/
	public final static int VIDEO_TELEPHONE_TYPE=1<<7;
	/**A paging device telephone number.*/
	public final static int PAGER_TELEPHONE_TYPE=1<<8;
	/**A bulletin board system telephone number.*/
	public final static int BBS_TELEPHONE_TYPE=1<<9;
	/**A modem-connected telephone number.*/
	public final static int MODEM_TELEPHONE_TYPE=1<<10;
	/**A car-phone telephone number.*/
	public final static int CAR_TELEPHONE_TYPE=1<<11;
	/**An ISDN service telephone number.*/
	public final static int ISDN_TELEPHONE_TYPE=1<<12;
	/**A personal communication services telephone number.*/
	public final static int PCS_TELEPHONE_TYPE=1<<13;

	/**The default telephone type.*/
	public final static int DEFAULT_TELEPHONE_TYPE=VOICE_TELEPHONE_TYPE;

	/**The intended use.*/
	private int telephoneType;

		/**@return The intended use; defaults to
			<code>VOICE_TELEPHONE_TYPE</code>.
		*/
		public int getTelephoneType() {return telephoneType;}

		/**Sets the intended use.
		@param telephoneType The intended use, one or more of the
			<code>XXX_TELEPHONE_TYPE</code> constants ORed together.
		*/
		public void setTelephoneType(final int telephoneType) {this.telephoneType=telephoneType;}

	/**The locale that represents the language of the text, or <code>null</code>
		if no language is indicated.
	*/
	private Locale locale;

		/**@return The locale that represents the language of the text, or
			<code>null</code> if no language is indicated.
		*/
		public Locale getLocale() {return locale;}

		/**Sets the language used by the text.
		@param locale The locale that represents the language of the text, or
			<code>null</code> if no language should be indicated.
		*/
		public void setLocale(final Locale locale) {this.locale=locale;}

	/**Telephone number constructor with default telephone type of <code>VOICE_TELEPHONE_TYPE</code>.
	@param telephoneNumber The telephone number from which values should be used
		for initialization.
	@exception TelephoneNumberSyntaxException Thrown if the values violate ITU-T
		E.164.
	*/
	public Telephone(final TelephoneNumber telephoneNumber) throws TelephoneNumberSyntaxException
	{
		this(telephoneNumber, DEFAULT_TELEPHONE_TYPE);	//construct a telephone with the default telephone type
	}

	/**Telephone number and type constructor.
	@param telephoneNumber The telephone number from which values should be used
		for initialization.
	@param telephoneType The intended use, one or more of the
		<code>XXX_TELEPHONE_TYPE</code> constants ORed together.
	@exception TelephoneNumberSyntaxException Thrown if the values violate ITU-T
		E.164.
	*/
	public Telephone(final TelephoneNumber telephoneNumber, final int telephoneType) throws TelephoneNumberSyntaxException
	{
		this(telephoneNumber.getCountryCode(), telephoneNumber.getNationalDestinationCode(), telephoneNumber.getSubscriberNumber(), telephoneType);	//construct the class from the components of the given telephone number
	}

	/**Full constructor with default telephone type of <code>VOICE_TELEPHONE_TYPE</code>.
	@param cc The country code for geographic areas.
	@param ndc The national destination code
	@param sn The subscriber number.
	@exception TelephoneNumberSyntaxException Thrown if the values violate ITU-T
		E.164.
	*/
	public Telephone(final String cc, final String ndc, final String sn) throws TelephoneNumberSyntaxException
	{
		this(cc, ndc, sn, DEFAULT_TELEPHONE_TYPE);	//construct a telephone with the default telephone type
	}

	/**Full constructor from separate telephone number components.
	@param cc The country code for geographic areas.
	@param ndc The national destination code
	@param sn The subscriber number.
	@param telephoneType The intended use, one or more of the
		<code>XXX_TELEPHONE_TYPE</code> constants ORed together.
	@exception TelephoneNumberSyntaxException Thrown if the values violate ITU-T
		E.164.
	*/
	public Telephone(final String cc, final String ndc, final String sn, final int telephoneType) throws TelephoneNumberSyntaxException
	{
		this(cc, ndc, sn, telephoneType, null);	//construct a telephone with no locale	
	}

	/**Full constructor from separate telephone number components.
	@param cc The country code for geographic areas.
	@param ndc The national destination code
	@param sn The subscriber number.
	@param telephoneType The intended use, one or more of the
		<code>XXX_TELEPHONE_TYPE</code> constants ORed together.
	@param locale The locale that represents the language of the text, or
		<code>null</code> if no language should be indicated.
	@exception TelephoneNumberSyntaxException Thrown if the values violate ITU-T
		E.164.
	*/
	public Telephone(final String cc, final String ndc, final String sn, final int telephoneType, final Locale locale) throws TelephoneNumberSyntaxException
	{
		super(cc, ndc, sn);	//construct the parent class
		setTelephoneType(telephoneType);	//set the telephone type
		setLocale(locale);	//set the locale
	}

	/**The string for separating the components of the string representation of
		the telephone type.
	*/
	protected final static String TELEPHONE_TYPE_SEPARATOR=", ";

	/**@return A string to represent the telephone type.*/
	public String getTelephoneTypeString()
	{
		return getTelephoneTypeString(getTelephoneType());	//return a string for our telephone type
	}

	/**Constructs a string to represent the given telephone type.
	@param telephoneType The intended use, one or more of the
		<code>XXX_TELEPHONE_TYPE</code> constants ORed together.
	 */
	public static String getTelephoneTypeString(final int telephoneType)	//G***i18n
	{
		final StringBuffer stringBuffer=new StringBuffer();
		if((telephoneType&PREFERRED_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("preferred");
		}		
		if((telephoneType&WORK_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("work");
		}		
		if((telephoneType&HOME_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("home");
		}		
		if((telephoneType&CELL_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("mobile");
		}		
		if((telephoneType&VOICE_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("voice");
		}		
		if((telephoneType&FAX_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("fax");
		}		
		if((telephoneType&PAGER_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("pager");
		}		
		if((telephoneType&MODEM_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("modem");
		}		
		if((telephoneType&MESSAGE_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("message");
		}		
		if((telephoneType&CAR_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("car");
		}		
		if((telephoneType&VIDEO_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("video");
		}		
		if((telephoneType&ISDN_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("ISDN");
		}		
		if((telephoneType&BBS_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("BBS");
		}		
		if((telephoneType&PCS_TELEPHONE_TYPE)!=0)
		{
			if(stringBuffer.length()>0)
				stringBuffer.append(TELEPHONE_TYPE_SEPARATOR);
			stringBuffer.append("PCS");
		}
		return stringBuffer.toString();		
	}

	/**@return A string representation of the address.*/
	public String toString()
	{
		return super.toString()+" ("+getTelephoneTypeString()+")";	//return the telephone type appended to the telephone number 
	}
}
