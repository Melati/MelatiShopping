package org.paneris.melati.shopping;

import org.paneris.melati.shopping.ShoppingTrolleyItem;

public class DefaultShoppingTrolleyItem extends ShoppingTrolleyItem {

  /* load in information about this product given an id.  
     perhaps this id represents a poem troid?
  */
  protected void load(Integer id){};
    
  /* work out the cost of delivery
  */
  public double getDeliveryValue() {
    return 0;
  }
                             
}

