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
  }

}

