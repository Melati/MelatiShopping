package org.paneris.melati.shopping;

import org.melati.servlet.MelatiContext;

  /** 
   * A ShoppingContext contains additional information from parsing the pathInfo.
   */
  public class ShoppingContext extends MelatiContext {
    // shopping trolley id
    public Integer stid;
    public double quantity;
  }

