package org.paneris.melati.shopping;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import org.melati.Melati;
import javax.servlet.http.HttpSession;
import java.text.NumberFormat;
import java.util.Locale;
import org.melati.util.*;

/**
* <p> A Shopping Trolley stored information in the user's Shopping Trolley.<p>
* <p> It does this by storing itself in the session.</p>
* <p> For this reason, the constructors are private, and you will be expected to
* always get the Shopping Trolley using getInstance();</p>
*
* usage example:
*
* ShoppingTrolley trolley = ShoppingTrolley.getInstance(Melati melati);
* context.put("trolley", trolley);
*
**/

public abstract class ShoppingTrolley {

  private static String TROLLEY = "org.paneris.melati.shopping.DefaultShoppingTrolley";
  protected Locale locale;
  protected String address;
  protected String name;
  protected String tel;
  protected String town;
  protected String country;
  protected String postcode;
  protected String message;
  protected String email;
  protected boolean hasDetails = false;
  Vector orderedItems = new Vector();
  Hashtable items = new Hashtable();
  MelatiShoppingConfig config;
  protected Melati melati;

  /**
   * private Constructor to build an empty ShoppingTrolley
  **/
  protected void initialise(Melati melati, MelatiShoppingConfig config) {
    locale = getLocale();
    this.config = config;
    this.melati = melati;
  }

  /**
   * public Constructor to build a trolley from some id
  **/
  public void initialise(Melati melati, MelatiShoppingConfig config, Integer id) 
   throws InstantiationPropertyException {
    load(id);
    initialise(melati,config);
    HttpSession session = melati.getSession();
    session.putValue(name(),this);
    this.melati = melati;
  }
  
  /** 
   * remove any trolley from the session
   */
  public void remove(Melati melati) {
    HttpSession session = melati.getSession();
    session.removeValue(name());
  }
    

  /**
   * Returns the single instance, creating one if it can't be found.
   */
  public static synchronized ShoppingTrolley getInstance(Melati melati, MelatiShoppingConfig config) 
   throws InstantiationPropertyException {
    HttpSession session = melati.getSession();
    ShoppingTrolley instance = (ShoppingTrolley) session.getValue(name());
    if (instance == null) {
      instance = newTrolley(config);
      instance.initialise(melati,config);
      session.putValue(name(),instance);
    }
    return instance;
  }

  public static synchronized ShoppingTrolley newTrolley(MelatiShoppingConfig config) 
   throws InstantiationPropertyException {
    return config.getShoppingTrolley();
  }

  /* set the Locale for this trolley
  */
  public abstract Locale getLocale();

  /* confirm payment of this trolley
  */
  public abstract void confirmPayment(Melati melati);

  /* load a trolley from something persistent
  */
  public abstract void load(Integer id) throws InstantiationPropertyException;

  /* save a trolley to something persistent
  */
  public abstract void save();

  /* return the name of the trolley (for storing in the session
  */
  public static String name() {
    return TROLLEY;
  }

  /* get the items from the trolley
  */
  public Enumeration getItems() {
    return orderedItems.elements();
  }
  
  /* have we got anything in the trolley
  */
  public boolean isEmpty() {
    return items.isEmpty();
  }
  
  /* have we entered any personal information
  */
  public boolean hasDetails() {
    return hasDetails;
  }

  /* get an item from the trolley
  */
  public ShoppingTrolleyItem getItem(Integer id) {
    return (ShoppingTrolleyItem)items.get(id);
  }

  /* remove an item from the trolley
  */
  public void removeItem(ShoppingTrolleyItem item) {
    items.remove(item.getId());
    orderedItems.removeElement(item); 
  }

  /* add an item to the trolley
  */
  public void addItem(ShoppingTrolleyItem item) {
    items.put(item.getId(),item);
    orderedItems.add(item); 
  }
  
  public ShoppingTrolleyItem newItem(Integer id, String description, Double price) 
   throws InstantiationPropertyException {
    ShoppingTrolleyItem item = ShoppingTrolleyItem.newTrolleyItem(config);
    item.initialise(this, melati, locale, id, description, price);
    addItem(item);
    return item;
  }

  /* calculate the value of the items in the trolley
  */
  public double getValue() {
    double value = 0;
    for (Enumeration enum = items.elements(); enum.hasMoreElements();) {
      ShoppingTrolleyItem product = (ShoppingTrolleyItem) enum.nextElement();
      value += product.getValue();
    }
    return value;
  }

  /* format the order value for display
     this value does not include discount or delivery, but does invlude VAT
  */
  public String getValueDisplay() {
    return displayCurrency(getValue());
  }

  /* calculate the total value of this order
  */
  public double getTotalValue() {
    return getValue() + getTotalDeliveryValue() + getDiscountValue() + getVATValue();
  }

  /* format the total order value for display
     this value includes discount, delivery and VAT
  */
  public String getTotalValueDisplay() {
    return displayCurrency(getTotalValue());
  }

  /* format the total order value in pence, typically ecomerce sites
     accept the values in pence not pounds
  */
  public String getTotalValuePence() {
    return (new Double(getTotalValue() * 100)).intValue() + "";
  }

  /* provide a mechanism for working out if 
     this order should include a delivery charge
  */
  public abstract boolean hasDelivery();

  /* you need to provide some mechanism for calculating the delivery
     value for the order (item delivery values are calculated individually
  */
  public abstract double getDeliveryValue();

  /* the delivery charge for the order is the sum of the charges on the items
     and an overall charge
  */
  public double getTotalDeliveryValue() {
    double value = 0;
    if (hasDelivery()) {
      value = getDeliveryValue();
      for (Enumeration enum = items.elements(); enum.hasMoreElements();) {
        ShoppingTrolleyItem item = (ShoppingTrolleyItem) enum.nextElement();
        value += item.getDeliveryValue();
      }
    }
    return value;
  }

  /* format the devliery value for display
  */
  public String getDeliveryDisplay() {
    return displayCurrency(getTotalDeliveryValue());
  }

  /* provide a mechanism for working out if 
     this order should include a discount
  */
  public abstract boolean hasDiscount();

  /* if you want to apply a discount to this order, do it here
  */
  public abstract double getDiscountRate();

  /* work out the value of the discout applied to this order
     (returns a negative value)
  */
  public double getDiscountValue() {
    double value = 0;
    if (hasDiscount()) {
      value = 0 - getValue()*getDiscountRate();
    }
    return value;
  }
  
  /* display the discount (if present)
  */
  public String getDiscountRateDisplay() {
    if (hasDiscount()) {
      try {
        return (new Double(getDiscountRate())).intValue() + "%";
      } catch (NumberFormatException e) {
        return getDiscountRate() + "%";
      }
    } else {
      return "";
    }
  }

  /* format the discount value for display
  */
  public String getDiscountValueDisplay() throws Exception {
    return displayCurrency(getDiscountValue());
  }

  /* provide a mechanism for working out if 
     this order should include VAT (default should be true)
  */
  public abstract boolean hasVAT();

  /* calculate the VAT value of the order
     typically items are priced inclusive of VAT and orders 
     are therefor also inclusive of VAT.  If this order is
     for someone who should not be charged VAT, we need to subtract VAT
     from the order value
  */
  public double getVATValue() {
    if (!hasVAT()) {
      return getValue() * -0.14894;
    } else {
      return 0;
    }
  }

  /* format the vat value for display
  */
  public String getVATDisplay() {
    return displayCurrency(getVATValue());
  }

  public void setFromForm(Melati melati) {
    setName(getFormNulled(melati,"trolley_name"));
    setEmail(getFormNulled(melati,"trolley_email"));
    setTel(getFormNulled(melati,"trolley_tel"));
    setDeliveryAddress(getFormNulled(melati,"trolley_deliveryaddress"));
    setTown(getFormNulled(melati,"trolley_town"));
    setCountry(getFormNulled(melati,"trolley_country"));
    setPostcode(getFormNulled(melati,"trolley_postcode"));
    setMessage(getFormNulled(melati,"trolley_message"));
    hasDetails = true;
  }

  /* set the address
  */
  public void setDeliveryAddress(String a) {
    address = a;
  }
  /* get the address
  */
  public String getDeliveryAddress() {
    return address;
  }

  /* set the name
  */
  public void setName(String a) {
    name = a;
  }
  /* get the name
  */
  public String getName() {
    return name;
  }

  /* set the email address
  */
  public void setEmail(String a) {
    email = a;
  }
  /* get the email address
  */
  public String getEmail() {
    return email;
  }

  /* set the postcode
  */
  public void setPostcode(String a) {
    postcode = a;
  }
  /* get the postcode
  */
  public String getPostcode() {
    return postcode;
  }

  /* set the telephone number
  */
  public void setTel(String a) {
    tel = a;
  }
  /* get the telephone number
  */
  public String getTel() {
    return tel;
  }

  /* set the town
  */
  public void setTown(String a) {
    town = a;
  }
  /* get the town
  */
  public String getTown() {
    return town;
  }

  /* set the country
  */
  public void setCountry(String a) {
    country = a;
  }
  /* get the country
  */
  public String getCountry() {
    return country;
  }

  /* set the delivery message 
  */
  public void setMessage(String a) {
    message = a;
  }
  /* get the delivery message 
  */
  public String getMessage() {
    return message;
  }

  /* format a number in the locale currency
  */
  private String displayCurrency(double value) {
    return new String(NumberFormat.getCurrencyInstance(locale).format(value));
  }
  
  public String getFormNulled(Melati melati, String field) {
    String val = melati.getTemplateContext().getForm(field);
    if (val == null) return null;
    return val.equals("")?null:val;
  }


}

