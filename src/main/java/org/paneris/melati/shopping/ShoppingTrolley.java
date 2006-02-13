/*
 * $Source$
 * $Revision$
 *
 * Copyright (C) 2000 Tim Joyce
 *
 * Part of Melati (http://melati.org), a framework for the rapid
 * development of clean, maintainable web applications.
 *
 * Melati is free software; Permission is granted to copy, distribute
 * and/or modify this software under the terms either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation; either version 2 of the License, or (at your option)
 *    any later version,
 *
 *    or
 *
 * b) any version of the Melati Software License, as published
 *    at http://melati.org
 *
 * You should have received a copy of the GNU General Public License and
 * the Melati Software License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA to obtain the
 * GNU General Public License and visit http://melati.org to obtain the
 * Melati Software License.
 *
 * Feel free to contact the Developers of Melati (http://melati.org),
 * if you would like to work out a different arrangement than the options
 * outlined here.  It is our intention to allow Melati to be used by as
 * wide an audience as possible.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Contact details for copyright holder:
 *
 *     Tim Joyce <timj@paneris.org>
 *     http://paneris.org/
 *     68 Sandbanks Rd, Poole, Dorset. BH14 8BY. UK
 */

package org.paneris.melati.shopping;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import org.melati.Melati;
import org.melati.MelatiUtil;
import org.melati.template.ServletTemplateContext;
import javax.servlet.http.HttpSession;
import java.text.NumberFormat;
import java.util.Locale;
import org.melati.util.InstantiationPropertyException;

 /**
 * <p> A Shopping Trolley stores information in the user's 
 * Shopping Trolley.</p>
 * <p> It does this by storing itself in the session.</p>
 * <p> For this reason, the constructors are private, and you will be expected
 * always to get the Shopping Trolley using getInstance();</p>
 * <p>
 * Usage example:
 * </p><p>
 * ShoppingTrolley trolley = ShoppingTrolley.getInstance(Melati melati);
 * context.put("trolley", trolley);
 * </p>
 **/

public abstract class ShoppingTrolley {

  private static String TROLLEY = 
      "org.paneris.melati.shopping.DefaultShoppingTrolley";
  protected Locale locale;
  protected String address;
  protected String name;
  protected String tel;
  protected String town;
  protected String county;
  protected String country;
  protected String postcode;
  protected String message;
  protected String email;
  protected boolean hasDetails = false;
  Vector orderedItems = new Vector();
  Hashtable items = new Hashtable();

  public final static Double VAT_PERCENT_TIMES_TEN = 175.0;
  public MelatiShoppingConfig config;
  public Melati melati;

 /**
  * Private Constructor to build an empty ShoppingTrolley
  **/
  protected void initialise(Melati melatiIn, MelatiShoppingConfig configIn) {
    this.config = configIn;
    this.melati = melatiIn;
  }

  /**
   * Public Constructor to build a trolley from some id.
   */
  public void initialise(Melati melatiIn, 
                         MelatiShoppingConfig configIn, Integer id)
   throws InstantiationPropertyException {
    initialise(melatiIn,configIn);
    load(id);
    HttpSession session = melati.getSession();
    session.setAttribute(name(),this);
  }

  /**
   * Remove any trolley from the session.
   */
  public void remove(Melati melatiIn) {
    HttpSession session = melatiIn.getSession();
    session.removeAttribute(name());
  }


  /**
   * Returns the single instance, creating one if it can't be found.
   */
  public static synchronized ShoppingTrolley 
      getInstance(Melati melati, MelatiShoppingConfig config)
   throws InstantiationPropertyException {
    HttpSession session = melati.getSession();
    ShoppingTrolley instance = (ShoppingTrolley) session.getAttribute(name());
    if (instance == null) {
      instance = newTrolley(config);
      instance.initialise(melati,config);
      session.putValue(name(),instance);
    }
    instance.configureRequest(melati);
    return instance;
  }

  public static synchronized ShoppingTrolley 
      newTrolley(MelatiShoppingConfig config)
      throws InstantiationPropertyException {
    return config.getShoppingTrolley();
  }

 /**
  * Get the Locale for this trolley.
  */
  public abstract Locale getLocale();


 /**
  * Set the Locale for this trolley.
  */
  public void setLocale(Locale locale){
    this.locale = locale;
  }

 /**
  * Confirm payment of this trolley.
  */
  public abstract void confirmPayment(Melati melatiIn);

 /**
  * Load a trolley from something persistent.
  */
  public abstract void load(Integer id) throws InstantiationPropertyException;

 /**
  * Save a trolley to something persistent.
  */
  public abstract void save();

 /**
  * This is done for each request, so anything special that needs to be done
  * can be put in here
  */
  public void configureRequest(Melati melatiIn) {
    this.melati = melatiIn;
  }

  /**
   *  Do something to force users to login.
   * You could perhaps throw an access poem exception in order to let the
   * servlet generate the login page.
   */
  public abstract void assertLogin(Melati melatiIn);

  /**
   * Set the user's detault details into this trolley.  
   * This is useful if users have already logged in, 
   * and we don't want them to reenter their details.
   */
  public abstract void setDefaultDetails(Melati melatiIn);


 /**
  * Return the name of the trolley (for storing in the session).
  */
  public static String name() {
    return TROLLEY;
  }

 /**
  * Get the items from the trolley.
  */
  public Enumeration getItems() {
    return orderedItems.elements();
  }

 /**
  * Have we got anything in the trolley.
  */
  public boolean isEmpty() {
    return items.isEmpty();
  }

 /**
  * Have we entered any personal information.
  */
  public boolean hasDetails() {
    return hasDetails;
  }

 /**
  * Get an item from the trolley.
  */
  public ShoppingTrolleyItem getItem(Integer id) {
    return (ShoppingTrolleyItem)items.get(id);
  }

 /**
  * Remove an item from the trolley.
  */
  public void removeItem(ShoppingTrolleyItem item) {
    items.remove(item.getId());
    orderedItems.removeElement(item);
  }

 /**
  * Add an item to the trolley.
  */
  public void addItem(ShoppingTrolleyItem item) {
    // don't add it if it's already there
    if (!items.containsKey(item.getId())) {
      orderedItems.add(item);
    }
    items.put(item.getId(),item);
  }

 /**
  * Create an item and put it in the Trolley.
  * 
  * @param id
  * @param description
  * @param price
  * @return a newly created item in the Trolley
  * @throws InstantiationPropertyException
  */
  public ShoppingTrolleyItem newItem(Integer id, String description, 
                                     Double price)
   throws InstantiationPropertyException {
    ShoppingTrolleyItem item = ShoppingTrolleyItem.newTrolleyItem(config);
    item.initialise(this, melati, id, description, price);
    addItem(item);
    return item;
  }

 /**
  * Calculate the value of the items in the trolley.
  */
  public double getValue() {
    double value = 0;
    for (Enumeration en = items.elements(); en.hasMoreElements();) {
      ShoppingTrolleyItem product = (ShoppingTrolleyItem) en.nextElement();
      value += product.getValue();
    }
    return value;
  }

 /**
  * Format the order value for display.
  * This value does not include discount or delivery, but does invlude VAT.
  */
  public String getValueDisplay() {
    return displayCurrency(getValue());
  }

 /**
  * Calculate the total value of this order.
  */
  public double getTotalValue() {
    return getValue() + 
           getTotalDeliveryValue() + 
           getDiscountValue() + 
           getVATValue();
  }

 /**
  * Format the total order value for display.
  * This value includes discount, delivery and VAT.
  */
  public String getTotalValueDisplay() {
    return displayCurrency(getTotalValue());
  }

 /**
  * Format the total order value in pence, typically ecomerce sites
  * accept the values in pence not pounds.
  */
  public String getTotalValuePence() {
    return (new Double(roundTo2dp(getTotalValue() * 100))).intValue() + "";
  }

 /**
  * Provide a mechanism for working out if
  * this order should include a delivery charge.
  */
  public abstract boolean hasDelivery();

 /**
  * You need to provide some mechanism for calculating the delivery
  * value for the order (item delivery values are calculated individually.
  */
  public abstract double getDeliveryValue();

 /**
  * The delivery charge for the order is the sum of the charges on the items
  * and an overall charge.
  */
  public double getTotalDeliveryValue() {
    double value = 0;
    if (hasDelivery()) {
      value = getDeliveryValue();
      for (Enumeration en = items.elements(); en.hasMoreElements();) {
        ShoppingTrolleyItem item = (ShoppingTrolleyItem)en.nextElement();
        value += item.getDeliveryValue();
      }
    }
    return value;
  }

 /**
  * Format the delivery value for display.
  */
  public String getDeliveryDisplay() {
    return displayCurrency(getTotalDeliveryValue());
  }

 /**
  * Provide a mechanism for working out if
  * this order should include a discount.
  */
  public abstract boolean hasDiscount();

 /**
  * If you want to apply a discount to this order, do it here.
  */
  public abstract double getDiscountRate();

 /**
  * Work out the value of the discout applied to this order
  * (returns a negative value).
  */
  public double getDiscountValue() {
    double value = 0;
    if (hasDiscount()) {
      value = 0 - roundTo2dp(getValue()*getDiscountRate());
    }
    return value;
  }

 /**
  * Display the discount (if present).
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

 /**
  * Format the discount value for display.
  */
  public String getDiscountValueDisplay() throws Exception {
    return displayCurrency(getDiscountValue());
  }

 /**
  * Provide a mechanism for working out if
  * this order should include VAT (default should be true).
  */
  public abstract boolean hasVAT();

 /**
  * Calculate the VAT value of the order.
  * Typically items are priced inclusive of VAT and orders
  * are therefore also inclusive of VAT.  If this order is
  * for someone who should not be charged VAT, we need to subtract VAT
  * from the order value.
  */
  public double getVATValue() {
    if (!hasVAT()) {
      return roundTo2dp((getValue() * 
                        (-1 * (1.0 - (1000.0/VAT_PERCENT_TIMES_TEN)))));
    } else {
      return 0;
    }
  }

 /**
  * Format the vat value for display.
  */
  public String getVATDisplay() {
    return displayCurrency(getVATValue());
  }

 /**
  * Set values from form fileds. 
  * @param melati
  */
  public void setFromForm(Melati melati) {
    ServletTemplateContext tc = melati.getServletTemplateContext();
    setName(MelatiUtil.getFormNulled(tc,"trolley_name"));
    setEmail(MelatiUtil.getFormNulled(tc,"trolley_email"));
    setTel(MelatiUtil.getFormNulled(tc,"trolley_tel"));
    setDeliveryAddress(MelatiUtil.getFormNulled(tc,"trolley_deliveryaddress"));
    setTown(MelatiUtil.getFormNulled(tc,"trolley_town"));
    setCounty(MelatiUtil.getFormNulled(tc,"trolley_county"));
    setCountry(MelatiUtil.getFormNulled(tc,"trolley_country"));
    setPostcode(MelatiUtil.getFormNulled(tc,"trolley_postcode"));
    setMessage(MelatiUtil.getFormNulled(tc,"trolley_message"));
    hasDetails = true;
  }

 /**
  * Set the address.
  */
  public void setDeliveryAddress(String a) {
    address = a;
  }
 /**
  * Get the address.
  */
  public String getDeliveryAddress() {
    return address;
  }

 /**
  * Set the name.
  */
  public void setName(String a) {
    name = a;
  }
 /**
  * Get the name.
  */
  public String getName() {
    return name;
  }

 /**
  * Set the email address.
  */
  public void setEmail(String a) {
    email = a;
  }
 /**
  * Get the email address.
  */
  public String getEmail() {
    return email;
  }

 /**
  * Set the postcode.
  */
  public void setPostcode(String a) {
    postcode = a;
  }
 /**
  * Get the postcode.
  */
  public String getPostcode() {
    return postcode;
  }

 /**
  * Set the telephone number.
  */
  public void setTel(String a) {
    tel = a;
  }
 /**
  * Get the telephone number.
  */
  public String getTel() {
    return tel;
  }

 /**
  * Set the town.
  */
  public void setTown(String a) {
    town = a;
  }
 /**
  * Get the town.
  */
  public String getTown() {
    return town;
  }

 /**
  * Set the county.
  */
  public void setCounty(String a) {
    county = a;
  }
 /**
  * Get the county.
  */
  public String getCounty() {
    return county;
  }

 /**
  * Set the country.
  */
  public void setCountry(String a) {
    country = a;
  }
 /**
  * Get the country.
  */
  public String getCountry() {
    return country;
  }

 /**
  * Set the delivery message.
  */
  public void setMessage(String a) {
    message = a;
  }
 /**
  * Get the delivery message.
  */
  public String getMessage() {
    return message;
  }

 /**
  * Format a number in the locale currency.
  */
  public String displayCurrency(double value) {
    return new String(NumberFormat.getCurrencyInstance(
                                       getLocale()).format(value));
  }

 /**
  * Format a number in the locale currency.
  */
  public String displayCurrency(Double value) {
    return displayCurrency(value.doubleValue());
  }

  public String baseURL() {
    return melati.getRequest().getServletPath() + "/" +
           melati.getPoemContext().getLogicalDatabase() + "/";
  }

  public String viewURL() {
    return baseURL() + "View/";
  }

  public String detailsURL() {
    return baseURL() + "Details/";
  }

  public String confirmURL() {
    return baseURL() + "Confirm/";
  }

  public String abandonURL() {
    return baseURL() + "Abandon/";
  }

  public String updateURL() {
    return baseURL() + "Update/";
  }

  public String paidURL() {
    return baseURL() + "Paid/";
  }

  public static double roundTo2dp(double num) {
    int a = Math.round(new Float(num * 100).floatValue());
    double b = new Double(a).doubleValue();
    return (b/100);
  }

}

