package org.paneris.melati.shopping;

import org.paneris.jammyjoes.servlet.*;
import org.paneris.jammyjoes.model.*;
import org.melati.*;
import org.melati.util.*;
import org.melati.template.webmacro.*;
import org.melati.template.*;
import org.melati.servlet.*;
import org.melati.poem.*;
import org.webmacro.*;
import org.webmacro.util.*;
import org.webmacro.servlet.*;
import org.webmacro.engine.*;
import org.webmacro.resource.*;
import org.webmacro.broker.*;
import java.util.*;
import javax.servlet.*;
import java.net.URLEncoder;


public class Trolley extends WebmacroMelatiServlet {

  MelatiShoppingConfig config;
   
  /** 
   * Inititialise the Shopping Trolley Engine
   * and the template engine
   * @param ServletConfig
   */
  public void init( ServletConfig conf ) throws ServletException
  {
     super.init( conf );

     try {
       config = new MelatiShoppingConfig();
     } catch (MelatiException e) {
       throw new ServletException(e.toString());
     }

  }

  protected String handle(Melati melati, WebContext context)
      throws Exception {

    if (config==null) 
       throw new ShoppingConfigException("Shopping Trolley not Configured");
    ShoppingContext shoppingContext = (ShoppingContext)melati.getContext();
    if (shoppingContext.method.equals("Load")) 
      return Load(melati, shoppingContext.stid);
    if (shoppingContext.method.equals("View")) return View(melati);
    if (shoppingContext.method.equals("Update")) return Update(melati);
    if (shoppingContext.method.equals("Add")) 
      return Add(melati, shoppingContext.stid, shoppingContext.quantity);
    if (shoppingContext.method.equals("Remove")) 
      return Remove(melati, shoppingContext.stid);
    if (shoppingContext.method.equals("Set")) 
      return Set(melati, shoppingContext.stid, shoppingContext.quantity);
    if (shoppingContext.method.equals("Details")) return Details(melati);
    if (shoppingContext.method.equals("Confirm")) return Confirm(melati);
    throw new InvalidUsageException(this, shoppingContext);
  }

  /* load the trolley from something persistent
  */
  protected String Load(Melati melati, Integer id)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.newTrolley(config);
    trolley.initialise(melati,config,id);
    melati.getTemplateContext().put("trolley", trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* save the trolley to something persistent
  */
  protected String Save(Melati melati)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    trolley.save();
    melati.getTemplateContext().put("trolley", trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* simply view the trolley
  */
  protected String View(Melati melati)
   throws InstantiationPropertyException {
    melati.getTemplateContext().put("trolley", ShoppingTrolley.getInstance(melati,config));
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* update the entire trolley, changing quantities
     and removing items
  */
  protected String Update(Melati melati)
   throws InstantiationPropertyException {
     System.err.println("Updating");
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    for (Enumeration c = trolley.getItems(); c.hasMoreElements();) {
      ShoppingTrolleyItem item = (ShoppingTrolleyItem)c.nextElement();
      String formName = "trolleyitem_" + item.getId();
      String formQuantity = formName + "_quantity";
      String formDeleted = formName + "_deleted";
      String deleted = getFormNulled(melati,formDeleted);
      String quantity = getFormNulled(melati,formQuantity);
      System.err.println(deleted + " " + quantity);
      if (deleted != null || quantity == null || quantity.equals("0")) {
        trolley.removeItem(item);
      } else {
        item.setQuantity(new Double(quantity).doubleValue());
      }
    }
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* add an item to the trolley, or add to the
     quantity already in the trolley
  */
  protected String Add(Melati melati, Integer id, double quantity)
   throws InstantiationPropertyException {
     System.err.println("Adding");
    // the quantity is defaulted to 1, so if you don't set it you will get one
    if (quantity == 0) quantity = 1;
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    ShoppingTrolleyItem item = trolley.getItem(id);
    if (item == null) item = newItem(trolley,melati,id);
    item.setQuantity(item.getQuantity() + quantity);
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* remove an item from the trolley
  */
  protected String Remove(Melati melati, Integer id)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    ShoppingTrolleyItem item = trolley.getItem(id);
    trolley.removeItem(item);
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* set the quantity of an item in the trolley
  */
  protected String Set(Melati melati, Integer id, double quantity)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    ShoppingTrolleyItem item = trolley.getItem(id);
    if (item == null) item = newItem(trolley,melati,id);
    item.setQuantity(quantity);
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }
  
  /* return the page where the user enters their details
  */
  protected String Details(Melati melati)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Details.wm");
  }
  
  /* update the user's information and return the
     confirmation page
  */
  protected String Confirm(Melati melati)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    if (getFormNulled(melati,"submit") != null) trolley.setFromForm(melati);
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Confirm.wm");
  }

  protected final String shoppingTemplate(Melati melati, String name)
   throws InstantiationPropertyException {
    return "shopping/" + name;
  }

  private ShoppingTrolleyItem newItem(ShoppingTrolley trolley, Melati melati, Integer id)
   throws InstantiationPropertyException {
    Double price = null;
    String priceString = getFormNulled(melati, "price");
    if (priceString != null) price = new Double(priceString);
    return trolley.newItem(id, getFormNulled(melati, "description"), price);
  }

  public class ShoppingContext extends MelatiContext {
    // shopping trolley id
    Integer stid;
    double quantity;
  }

  protected MelatiContext melatiContext(Melati melati)
                          throws PathInfoException {
    ShoppingContext it = new ShoppingContext();
    String[] parts = melati.getPathInfoParts();
    if (parts.length < 2) 
      throw new PathInfoException(
          "The servlet expects to see pathinfo in the form " +
          "/db/method/ or /db/troid/method/ or /db/troid/quantity/method/");
    it.logicalDatabase = parts[0];
    if (parts.length == 2) it.method = parts[1];
    try {
      if (parts.length == 3) {
        it.stid = new Integer(parts[1]);
        it.method = parts[2];
      }
      if (parts.length > 3) {
        it.stid = new Integer(parts[1]);
        it.quantity = (new Double(parts[2])).doubleValue();
        it.method = parts[3];
      }
    } catch (NumberFormatException e) {
      throw new PathInfoException(
          "The servlet expects to see pathinfo in the form " +
          "/db/method/ or /db/troid/method/ or /db/troid/quantity/method/ " +
          "where the troid is an integer and the quantity is a number");
    }
    return it;
  }

  public String getFormNulled(Melati melati, String field) {
    String val = melati.getTemplateContext().getForm(field);
    if (val == null) return null;
    return val.equals("")?null:val;
  }

}
