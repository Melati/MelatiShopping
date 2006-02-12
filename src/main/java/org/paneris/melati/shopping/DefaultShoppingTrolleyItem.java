package org.paneris.melati.shopping;

public class DefaultShoppingTrolleyItem extends ShoppingTrolleyItem {

 /**
  * Load in information about this product given an id.  
  * Perhaps this id represents a Poem troid?
  */
  protected void load(Integer idIn){};
    
 /**
  * Work out the cost of delivery
  */
  public double getDeliveryValue() {
    return 0;
  }
                             
}

