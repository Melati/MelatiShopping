package org.paneris.melati.shopping;

import java.util.Locale;
import java.text.NumberFormat;
import org.melati.util.*;
import org.melati.*;

public abstract class ShoppingTrolleyItem  {

  public Melati melati;
  Integer id;
  double quantity;
  double price;
  Locale locale;
  String description;

  public static synchronized ShoppingTrolleyItem newTrolleyItem(MelatiShoppingConfig config)
   throws InstantiationPropertyException {
    System.err.println("new item");
    return config.getShoppingTrolleyItem();
  }
  
  /**
   * public Constructor to build a trolley item from some id
  **/
  public void initialise(Locale l, Integer id) {
    this.id = id;
    this.description = id +"";
    locale = l;
    load(id);
  }
  
  /**
   * public Constructor to build a trolley item from some id
  **/
  public void initialise(Melati melati, Locale locale, Integer id, String description, Double price) {
    this.id = id;
    this.melati = melati;
    load(id);
    if (description != null) this.description = description;
      // set something in the description!
    if (this.description == null) this.description = id +"";
    if (price != null) this.price = price.doubleValue();
    this.locale = locale;
  }

  
  /* load in information about this product given an id.  
     perhaps this id represents a poem troid?
  */
  protected abstract void load(Integer id);
    
  /* the id
  */
  public Integer getId() {
    return id;
  }

  /* the description
  */
  public String getDescription() {
    return description;
  }

  /* the quantity on the trolley
  */
  public double getQuantity() {
    return quantity;
  }

  /* set the quantity on the trolley
  */
  public void setQuantity(double q) {
    quantity = q;
  }

  /* get the quantity on the trolley formatted for display
  *  if it is an iteger, display it as such
  */
  public String getQuantityDisplay() {
    try {
      return (new Double(quantity)).intValue() + "";
    } catch (NumberFormatException e) {
      return quantity + "";
    }
  }
  
  /* the price of this item
  */
  public double getPrice() {
    return price;
  }

  /* set the price of this item
  */
  public void setPrice(double p){
    price = p;
  }

  /* display the price of this item
  */
  public String getPriceDisplay(){
    return displayCurrency(price);
  }

  /* work out the cost of delivery
  */
  public abstract double getDeliveryValue();
                             
  /* display the cost of delivery
  */
  public String getDeliveryDisplay() { 
    return displayCurrency(getDeliveryValue());
  }

  /* calculate the value (without delivery)
  */
  public double getValue() {
    return getPrice() * getQuantity();
  }

  /* display the item value
  */
  public String getValueDisplay() {
    return displayCurrency(getValue());
  }

  /* calculate the value (without delivery)
  */
  public double getTotalValue() {
    return getPrice() * getQuantity() + getDeliveryValue();
  }

  /* display the item value
  */
  public String getTotalValueDisplay() {
    return displayCurrency(getTotalValue());
  }

  /* format a number in the locale currency
  */
  private String displayCurrency(double value) {
    return new String(NumberFormat.getCurrencyInstance(locale).format(value));
  } 

}

