package org.paneris.melati.shopping;

import org.melati.Melati;
import java.util.Locale;

/**
* <p> A Shopping Trolley stores information in the user's Shopping Trolley.<p>
* <p> It does this by storing itself in the session.</p>
* <p> For this reason, the constructors are private, and you will be expected 
* always to get the Shopping Trolley using getInstance();</p>
*
* usage example:
*
* ShoppingTrolley trolley = ShoppingTrolley.getInstance(Melati melati);
* context.put("trolley", trolley);
*
**/

public class DefaultShoppingTrolley extends ShoppingTrolley {
  
 /** 
  * Set the Locale for this trolley.
  */
  public Locale getLocale(){
    return Locale.UK;
  }

 /**
  * Load a trolley from something persistent.
  */
  public void load(Integer id) {
  }

 /**
  * Save a trolley to something persistent.
  */
  public void save() {
  }
  
  /** 
   * Set the user's detault details into this trolley.  
   * This is useful if users have already logged in, 
   * and we don't want them to reenter their details.
   */
  public void setDefaultDetails(Melati melati) {
  }
  
 /**
  * Do something to force users to login.
  * You should throw an access poem exception in order to 
  * generate the login page.
  */
  public void assertLogin(Melati melatiIn) {
  }

 /**
  * Provide a mechanism for working out if 
  * this order should include a delivery charge.
  */
  public boolean hasDelivery(){
    return false;
  }

 /**
  * You need to provide some mechanism for calculating the delivery
  * value for the order (item delivery values are calculated individually.
  */
  public double getDeliveryValue() {
    return 0;
  }
  

 /** 
  * Provide a mechanism for working out if 
  * this order should include a discount.
  */
  public boolean hasDiscount() {
    return false;
  }

 /** 
  * If you want to apply a discount to this order, do it here.
  */
  public double getDiscountRate() {
    return 0;
  }

 /** 
  * Provide a mechanism for working out whether 
  * this order should include VAT (default should be true).
  */
  public boolean hasVAT() {
    return true;
  }

 /** 
  * A call back point for the payment server.
  */
  public void confirmPayment(Melati melatiIn) {
    return;
  }

}

