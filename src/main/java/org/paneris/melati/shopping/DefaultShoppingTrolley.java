package org.paneris.melati.shopping;

import org.paneris.melati.shopping.ShoppingTrolley;
import org.melati.Melati;
import java.util.Locale;

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

public class DefaultShoppingTrolley extends ShoppingTrolley {
  
  /* set the Locale for this trolley
  */
  public Locale getLocale(){
    return Locale.UK;
  }

  /* load a trolley from something persistent
  */
  public void load(Integer id) {
  }

  /* save a trolley to something persistent
  */
  public void save() {
  }
  
  /* set the user's detault details into this trolley.  this is useful
   * if users have already logged in, and we don't want them to reenter their 
   * details
   */
  public void setDefaultDetails(Melati melati) {
  }
  
  /* do something to force users to login
   * you should throw an access poem exception in order to generate the login page
   */
  public void assertLogin(Melati melati) {
  }

  /* provide a mechanism for working out if 
     this order should include a delivery charge
  */
  public boolean hasDelivery(){
    return false;
  }

  /* you need to provide some mechanism for calculating the delivery
     value for the order (item delivery values are calculated individually
  */
  public double getDeliveryValue() {
    return 0;
  }
  

  /* provide a mechanism for working out if 
     this order should include a discount
  */
  public boolean hasDiscount() {
    return false;
  }

  /* if you want to apply a discount to this order, do it here
  */
  public double getDiscountRate() {
    return 0;
  }

  /* provide a mechanism for working out if 
     this order should include VAT (default should be true)
  */
  public boolean hasVAT() {
    return true;
  }

  public void confirmPayment(Melati melati) {
    return;
  }

}

