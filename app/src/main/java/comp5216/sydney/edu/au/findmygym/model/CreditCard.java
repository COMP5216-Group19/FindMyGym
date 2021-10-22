package comp5216.sydney.edu.au.findmygym.model;

import android.graphics.Bitmap;

import java.util.Calendar;

public class CreditCard
{
	private String cardNumber;
	private String cardName;
	private String expiryDate;
	
	public CreditCard(String cardNumber, String cardName, String expiryDate)
	{
		this.cardNumber = cardNumber;
		this.cardName = cardName;
		this.expiryDate = expiryDate;
	}
	
	
	public String getCardNumber()
	{
		return String.valueOf(cardNumber);
	}
	
	public void setCardNumber(String cardNumber)
	{
		this.cardNumber = cardNumber;
	}
	
	public String getCardName()
	{
		return cardName;
	}
	
	public void setCardName(String cardName)
	{
		this.cardName = cardName;
	}
	
	public String getExpiryDate()
	{
		return expiryDate;
	}
	
	
	public void setExpiryDate(String expiryDate)
	{
		this.expiryDate = expiryDate;
	}
}
