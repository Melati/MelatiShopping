package org.paneris.melati.shopping;

import java.util.Properties;
import org.melati.util.*;
import org.melati.playing.neutral.ConfigException;
import java.io.*;


public class MelatiShoppingConfig {

  private Properties configuration = null;
  private String ShoppingTrolleyProp = null;
  private String ShoppingTrolleyItemProp = null;
  private static Class clazz;
  static {
    try {
      clazz = Class.forName("org.paneris.melati.shopping.ShoppingTrolley");
    }
    catch (ClassNotFoundException e) {
      throw new MelatiBugMelatiException("Out of date Class.forName", e);
    }
  }

  public MelatiShoppingConfig() throws MelatiException {

    // Load org.paneris.melati.shopping.ShoppingTrolley.properties, or set blank configuration
    String pref = clazz.getName() + ".";
    ShoppingTrolleyProp = pref + "trolley";
    ShoppingTrolleyItemProp = pref + "item";

    try {
      configuration =
          PropertiesUtils.fromResource(clazz, pref + "properties");
    }
    catch (FileNotFoundException e) {
      System.err.println("Could not find properties file: " +e.toString());
      configuration = new Properties();
    }
    catch (IOException e) {
      throw new ShoppingConfigException(e.toString());
    }

  }
  
  public org.paneris.melati.shopping.ShoppingTrolley getShoppingTrolley() 
    throws InstantiationPropertyException {
      return (org.paneris.melati.shopping.ShoppingTrolley)PropertiesUtils.instanceOfNamedClass(
	    configuration, ShoppingTrolleyProp, "org.paneris.melati.shopping.ShoppingTrolley",
	    "org.paneris.melati.shopping.DefaultShoppingTrolley");
  }
  
  public org.paneris.melati.shopping.ShoppingTrolleyItem getShoppingTrolleyItem()
    throws InstantiationPropertyException {
      return (org.paneris.melati.shopping.ShoppingTrolleyItem)PropertiesUtils.instanceOfNamedClass(
	    configuration, ShoppingTrolleyItemProp, "org.paneris.melati.shopping.ShoppingTrolleyItem",
	    "org.paneris.melati.shopping.DefaultShoppingTrolleyItem");
  }

}
