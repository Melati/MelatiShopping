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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.melati.util.MelatiBugMelatiException;
import org.melati.util.PropertiesUtils;
import org.melati.util.InstantiationPropertyException;


/** 
 * The MelatiShoppingConfig provides configuration information for the Melati 
 * Shopping Interface 
 */

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

/** 
 * Construct a MelatiShoppingConfig object.  if the propeties file is not found
 * default properties will be used
 *
 * @throws ShoppingConfigException - if there is an error loading the file
 */
  public MelatiShoppingConfig() throws ShoppingConfigException {

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

/** 
 * get a ShoppingTrolley Object as defined in the properties file 
 * if no definition is found, a default DefaultShoppingTrolley will be returned
 *
 * @return - a shopping trolley object as defined in the properties file 
 *
 * @throws InstantiationPropertyException - if we cannot instantiate the object
 */
  public org.paneris.melati.shopping.ShoppingTrolley getShoppingTrolley() 
      throws InstantiationPropertyException {
    return (org.paneris.melati.shopping.ShoppingTrolley)
             PropertiesUtils.instanceOfNamedClass(
                 configuration, ShoppingTrolleyProp, 
                 "org.paneris.melati.shopping.ShoppingTrolley",
                 "org.paneris.melati.shopping.DefaultShoppingTrolley");
  }
  

/** 
 * get a ShoppingTrolleyItem Object as defined in the properties file 
 * if no definition is found, a default DefaultShoppingTrolleyItem will be 
 * returned
 *
 * @return - a shopping trolley item object as defined in the properties file 
 *
 * @throws InstantiationPropertyException - if we cannot instantiate the object
 */
  public org.paneris.melati.shopping.ShoppingTrolleyItem getShoppingTrolleyItem()
      throws InstantiationPropertyException {
    return (org.paneris.melati.shopping.ShoppingTrolleyItem)
               PropertiesUtils.instanceOfNamedClass(
               configuration, ShoppingTrolleyItemProp, 
               "org.paneris.melati.shopping.ShoppingTrolleyItem",
               "org.paneris.melati.shopping.DefaultShoppingTrolleyItem");
  }

}
