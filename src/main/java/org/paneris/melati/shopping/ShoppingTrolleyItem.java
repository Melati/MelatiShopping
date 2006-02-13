/*
 * $Source$
 * $Revision$
 *
 * Copyright (C) 2000 Tim Joyce
 *
 * Part of Melati (http://melati.org/ ), a framework for the rapid
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

import java.util.Locale;
import java.text.NumberFormat;
import org.melati.util.InstantiationPropertyException;
import org.melati.Melati;

/**
 * Extend this to create your own ShoppingTrolleyItem.
 */
public abstract class ShoppingTrolleyItem  {

  protected Integer id;
  protected double quantity;
  protected double price;
  protected Locale locale;
  protected String description;
  // the shopping trolley to which this item belongs
  protected ShoppingTrolley trolley;
  public Melati melati;

  public static synchronized ShoppingTrolleyItem 
      newTrolleyItem(MelatiShoppingConfig config)
      throws InstantiationPropertyException {
    return config.getShoppingTrolleyItem();
  }

 /**
  * Public Constructor to build a trolley item from some id.
  **/
  public void initialise(ShoppingTrolley trolleyIn, Melati melatiIn,
                         Integer idIn, String descriptionIn, Double priceIn) {
    this.trolley = trolleyIn;
    this.id = idIn;
    this.melati = melatiIn;
    load(id);
    if (description != null) this.description = descriptionIn;
      // set something in the description!
    if (this.description == null) this.description = id +"";
    if (priceIn != null) this.price = priceIn.doubleValue();
  }


 /**
  * Load in information about this product given an id.
  * Perhaps this id represents a poem troid?
  */
  protected abstract void load(Integer idIn);

 /**
  * The id.
  */
  public Integer getId() {
    return id;
  }

 /**
  * The description.
  */
  public String getDescription() {
    return description;
  }

 /**
  * The quantity on the trolley.
  */
  public double getQuantity() {
    return quantity;
  }

 /**
  * Set the quantity on the trolley.
  */
  public void setQuantity(double q) {
    quantity = q;
  }

 /**
  * Get the quantity on the trolley formatted for display.
  * If it is an iteger, display it as such.
  */
  public String getQuantityDisplay() {
    try {
      return (new Double(quantity)).intValue() + "";
    } catch (NumberFormatException e) {
      return quantity + "";
    }
  }

 /**
  * The price of this item.
  */
  public double getPrice() {
    return price;
  }

 /**
  * Set the price of this item.
  */
  public void setPrice(double p){
    price = p;
  }

 /**
  * Display the price of this item.
  */
  public String getPriceDisplay(){
    return displayCurrency(getPrice());
  }

 /**
  * Work out the cost of delivery.
  */
  public abstract double getDeliveryValue();

 /**
  * Display the cost of delivery.
  */
  public String getDeliveryDisplay() {
    return displayCurrency(getDeliveryValue());
  }

 /**
  * Calculate the value (without delivery).
  */
  public double getValue() {
    return ShoppingTrolley.roundTo2dp(getPrice() * getQuantity());
  }

 /**
  * Display the item value.
  */
  public String getValueDisplay() {
    return displayCurrency(getValue());
  }

 /**
  * Calculate the value (without delivery).
  */
  public double getTotalValue() {
    return getValue() + getDeliveryValue();
  }

 /**
  * Display the item value.
  */
  public String getTotalValueDisplay() {
    return displayCurrency(getTotalValue());
  }

 /**
  * format a number in the locale currency.
  */
  public String displayCurrency(double value) {
    return new String(NumberFormat.getCurrencyInstance(trolley.getLocale())
                                  .format(value));
  }

}

