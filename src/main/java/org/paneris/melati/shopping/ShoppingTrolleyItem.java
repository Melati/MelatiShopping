package org.paneris.melati.shopping;

import java.util.Locale;
import java.text.NumberFormat;
import org.melati.util.InstantiationPropertyException;
import org.melati.Melati;

public abstract class ShoppingTrolleyItem  {

  protected Integer id;
  protected double quantity;
  protected double price;
  protected Locale locale;
  protected String description;
  // the shopping trolley to which this item belongs
  protected ShoppingTrolley trolley;
  public Melati melati;

  public static synchronized ShoppingTrolleyItem newTrolleyItem(MelatiShoppingConfig config)
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

