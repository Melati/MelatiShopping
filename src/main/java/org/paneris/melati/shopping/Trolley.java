package org.paneris.melati.shopping;

import org.paneris.jammyjoes.servlet.*;
import org.paneris.jammyjoes.model.*;
import org.melati.*;
import org.melati.util.*;
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


public class Trolley extends MelatiServlet {

  String db;
  String method;
  Integer id;
  double quantity;
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

  protected Template handle(WebContext context, Melati melati)
      throws Exception {

    if (config==null) throw new ShoppingConfigException("Shopping Trolley not Configured");
    // work out what to do
    parsePathInfo(melati);

    if (method.equals("Load")) return Load(melati, id);
    if (method.equals("View")) return View(melati);
    if (method.equals("Update")) return Update(melati);
    if (method.equals("Add")) return Add(melati, id, quantity);
    if (method.equals("Remove")) return Remove(melati, id);
    if (method.equals("Set")) return Set(melati, id, quantity);
    if (method.equals("Details")) return Details(melati);
    if (method.equals("Confirm")) return Confirm(melati);
    throw new InvalidUsageException(this, melati.getContext());
  }

  /* load the trolley from something persistent
  */
  protected Template Load(Melati melati, Integer id)
   throws NotFoundException, InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.newTrolley(config);
    trolley.initialise(melati,config,id);
    melati.getWebContext().put("trolley", trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* save the trolley to something persistent
  */
  protected Template Save(Melati melati)
   throws NotFoundException, InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    trolley.save();
    melati.getWebContext().put("trolley", trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* simply view the trolley
  */
  protected Template View(Melati melati)
   throws NotFoundException, InstantiationPropertyException {
    melati.getWebContext().put("trolley", ShoppingTrolley.getInstance(melati,config));
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* update the entire trolley, changing quantities
     and removing items
  */
  protected Template Update(Melati melati)
   throws NotFoundException, InstantiationPropertyException {
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
    melati.getWebContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* add an item to the trolley, or add to the
     quantity already in the trolley
  */
  protected Template Add(Melati melati, Integer id, double quantity)
   throws NotFoundException, InstantiationPropertyException {
     System.err.println("Adding");
    // the quantity is defaulted to 1, so if you don't set it you will get one
    if (quantity == 0) quantity = 1;
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    ShoppingTrolleyItem item = trolley.getItem(id);
    if (item == null) item = newItem(trolley,melati,id);
    item.setQuantity(item.getQuantity() + quantity);
    melati.getWebContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* remove an item from the trolley
  */
  protected Template Remove(Melati melati, Integer id)
   throws NotFoundException, InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    ShoppingTrolleyItem item = trolley.getItem(id);
    trolley.removeItem(item);
    melati.getWebContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }

  /* set the quantity of an item in the trolley
  */
  protected Template Set(Melati melati, Integer id, double quantity)
   throws NotFoundException, InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    ShoppingTrolleyItem item = trolley.getItem(id);
    if (item == null) item = newItem(trolley,melati,id);
    item.setQuantity(quantity);
    melati.getWebContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley.wm");
  }
  
  /* return the page where the user enters their details
  */
  protected Template Details(Melati melati)
   throws NotFoundException, InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    melati.getWebContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Details.wm");
  }
  
  /* update the user's information and return the
     confirmation page
  */
  protected Template Confirm(Melati melati)
   throws NotFoundException, InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    if (getFormNulled(melati,"submit") != null) trolley.setFromForm(melati);
    melati.getWebContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Confirm.wm");
  }

  protected final Template shoppingTemplate(Melati melati, String name)
   throws NotFoundException, InstantiationPropertyException {
    return getTemplate("shopping/" + name);
  }

  private ShoppingTrolleyItem newItem(ShoppingTrolley trolley, Melati melati, Integer id)
   throws InstantiationPropertyException {
    Double price = null;
    String priceString = getFormNulled(melati, "price");
    if (priceString != null) price = new Double(priceString);
    return trolley.newItem(id, getFormNulled(melati, "description"), price);
  }

  /* sort out what to do
  */
  protected void parsePathInfo(Melati melati) throws PathInfoException {
    String pathInfo = melati.getWebContext().getRequest().getPathInfo();
    System.err.println(pathInfo);
    pathInfo = pathInfo.substring(1);
    if (pathInfo.endsWith("/")) pathInfo = pathInfo.substring(0,pathInfo.length()-1);
    String[] parts = StringUtils.split(pathInfo, '/');
    if (parts.length < 2) 
      throw new PathInfoException(
          "The servlet expects to see pathinfo in the form " +
          "/db/method/ or /db/troid/method/ or /db/troid/quantity/method/");
    db = parts[0];
    try {
      if (parts.length == 2) method = parts[1];
      if (parts.length == 3) {
         id = new Integer(parts[1]);
         method = parts[2];
      }
      if (parts.length == 4) {
         id = new Integer(parts[1]);
         quantity = (new Double(parts[2])).doubleValue();
         method = parts[3];
      }
    } catch (NumberFormatException e) {
      throw new PathInfoException(
          "The servlet expects to see pathinfo in the form " +
          "/db/method/ or /db/troid/method/ or /db/troid/quantity/method/ " +
          "where the troid is an integer and the quantity is a number");
    }
  }

  public String getFormNulled(Melati melati, String field) {
    String val = melati.getWebContext().getForm(field);
    if (val == null) return null;
    return val.equals("")?null:val;
  }
  
  protected MelatiContext melatiContext(WebContext context)
      throws MelatiException {
      MelatiContext it = new MelatiContext();
      it.logicalDatabase = "jammyjoes";
      return it;
  }

}
