package org.paneris.melati.shopping;

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
import java.util.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.net.URLEncoder;


/** 
 * The Melati Shopping Trolley Interface is designed to provide a flexible adstraction
 * of the basic processes of allowing customers to buy goods on a website. By
 * default, it is not integrated with POEM, and so makes no assumptions about
 * the underlying database implementaion.
 *
 * Please read the readme.txt file for full instructions.
 *
 * The Trolley is a servlet that handles the user's interaction with the Shopping
 * Trolley.
 *
 * @see org.paneris.melati.shopping.ShoppingTrolley
 * @see org.paneris.melati.shopping.ShoppingTrolleyItem
 * @see org.paneris.melati.shopping.DefaultShoppingTrolley
 * @see org.paneris.melati.shopping.DefaultShoppingTrolleyItem
 *
 */

public class Trolley extends TemplateServlet {

  MelatiShoppingConfig config;
   
  /**
   * Inititialise the Shopping Trolley Engine.  This will load a file called
   * org.paneris.melati.shopping.ShoppingTrolley.properties in order to 
   * find the classes that implement this shopping implementation
   *
   * @param conf - the Servlet's config parameters
   * @see org.paneris.melati.shopping.MelatiShoppingConfig
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

  /**
   * Main entry point for this servlet
   *
   * @param melati - the melati for this request
   * @param context - the Template Context for this request
   *
   * @return - the name of the template to be returned to the user
   *
   * @throws InvalidUsageException - if this request has an invalid form
   */
  protected String doTemplateRequest(Melati melati, TemplateContext context)
      throws Exception {

    if (config==null) 
       throw new ShoppingConfigException("Shopping Trolley not Configured");
    // at any pint, the user can be forced to login, by simply appending "?login"
    // to the url
    if (MelatiUtil.getFormNulled(context,"login") != null) assertLogin(melati);
    ShoppingContext shoppingContext = (ShoppingContext)melati.getContext();
    if (shoppingContext.method.equals("Load")) 
      return Load(melati, shoppingContext.stid);
    if (shoppingContext.method.equals("View")) return View(melati);
    if (shoppingContext.method.equals("Update")) return Update(melati);
    if (shoppingContext.method.equals("Add")) 
      return Add(melati, shoppingContext.stid, shoppingContext.quantity);
    if (shoppingContext.method.equals("MultipleAdd")) 
      return MultipleAdd(melati);
    if (shoppingContext.method.equals("Remove")) 
      return Remove(melati, shoppingContext.stid);
    if (shoppingContext.method.equals("Set")) 
      return Set(melati, shoppingContext.stid, shoppingContext.quantity);
    if (shoppingContext.method.equals("Details")) return Details(melati);
    if (shoppingContext.method.equals("Confirm")) return Confirm(melati);
    if (shoppingContext.method.equals("Paid")) return Paid(melati);
    if (shoppingContext.method.equals("Abandon")) return Abandon(melati);
    throw new InvalidUsageException(this, shoppingContext);
  }

  /** 
   * load the trolley from something persistent
   *
   * @param melati - the melati for this request
   * @param id - an id that can be used to identify the trolley to be loaded
   *
   * @return - "Trolley" - the page where users manipulate their 
   *           Shopping Trolley
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String Load(Melati melati, Integer id)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.newTrolley(config);
    trolley.initialise(melati,config,id);
    melati.getTemplateContext().put("trolley", trolley);
    return shoppingTemplate(melati, "Trolley");
  }


  /** 
   * load the trolley from something persistent
   *
   * @param melati - the melati for this request
   *
   * @return - "Trolley" - the page where users manipulate their 
   *           Shopping Trolley
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String Save(Melati melati)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    trolley.save();
    melati.getTemplateContext().put("trolley", trolley);
    return shoppingTemplate(melati, "Trolley");
  }

  /** 
   * view the trolley
   *
   * @param melati - the melati for this request
   *
   * @return - "Trolley" - the page where users manipulate their 
   *           Shopping Trolley
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String View(Melati melati)
   throws InstantiationPropertyException {
    melati.getTemplateContext().put("trolley", ShoppingTrolley.getInstance(melati,config));
    return shoppingTemplate(melati, "Trolley");
  }

  /** 
   * update the entire trolley, changing quantities
   * and removing items.  the POSTed form is analysed for fields with names of 
   * the form:
   *
   * trolleyitem_<item id>_quantity - the new quantity of the item (if set)
   * trolleyitem_<item id>_deleted - remove this item from the trolley (if set)
   *
   * items will also be deleted if the quantity is set to 0
   *
   * @param melati - the melati for this request
   *
   * @return - "Trolley" - the page where users manipulate their 
   *           Shopping Trolley
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String Update(Melati melati) throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    for (Enumeration c = trolley.getItems(); c.hasMoreElements();) {
      ShoppingTrolleyItem item = (ShoppingTrolleyItem)c.nextElement();
      String formName = "trolleyitem_" + item.getId();
      String formQuantity = formName + "_quantity";
      String formDeleted = formName + "_deleted";
      String deleted = 
             MelatiUtil.getFormNulled(melati.getTemplateContext(),formDeleted);
      String quantity = 
             MelatiUtil.getFormNulled(melati.getTemplateContext(),formQuantity);
      System.err.println(deleted + " " + quantity);
      if (deleted != null || quantity == null || quantity.equals("0")) {
        trolley.removeItem(item);
      } else {
        item.setQuantity(new Double(quantity).doubleValue());
      }
    }
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley");
  }

  /** 
   * add multiple items to the trolley, or add to the quantities already in the 
   * trolley.  the POSTed form is analysed for fields with names of 
   * the form:
   *
   * product_<item id> - the id of the item to be added
   * quantity_<item id> - the quantity to add
   *
   * if no quantity is set, a single item will be added
   *
   * @param melati - the melati for this request
   *
   * @return - "Trolley" - the page where users manipulate their 
   *           Shopping Trolley
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String MultipleAdd(Melati melati)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    for (Enumeration e = melati.getRequest().getParameterNames(); 
                     e.hasMoreElements();) {
      String name = (String)e.nextElement();
      if (name.length() > 8) {
        String p = name.substring(0,7);
        if (p.equals("product")) {
          String id = name.substring(8);
          Integer idInt = new Integer(id);
          ShoppingTrolleyItem item = trolley.getItem(idInt);
          String quantityName = "quantity_" + id;
          String priceName = "price_" + id;
          String descriptionName = "description_" + id;
          double quantity = 1;
          Double price = null;
          String quantitySring = 
                 MelatiUtil.getFormNulled
                 (melati.getTemplateContext(), quantityName);
          String priceString = 
                 MelatiUtil.getFormNulled
                 (melati.getTemplateContext(), priceName);
          String description = 
                 MelatiUtil.getFormNulled
                 (melati.getTemplateContext(), descriptionName);
          if (quantitySring != null) 
            quantity = (new Double(quantitySring)).doubleValue();
          if (priceString != null) price = new Double(priceString);
          if (item == null) {
            item = newItem(trolley,idInt,price,description);
          }
          item.setQuantity(item.getQuantity() + quantity);
        }
      }
    }
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley");
  }

  /** 
   * add a single item to the trolley, or add to the quantity already in the 
   * trolley.  the product is specified on the pathinfo which should be of the
   * form:
   *
   * /<logicaldatabase>/<id>/<quantity>/Add/
   *
   * if no quantity is set, a single item will be added.  The form parmaeters
   * will be parsed to see if they contain "price" and/or "description" fields.
   * if they are present, they will be used to set up the item
   *
   * @param melati - the melati for this request
   * @param id - the id of the item to be added
   *
   * @return - "Trolley" - the page where users manipulate their 
   *           Shopping Trolley
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String Add(Melati melati, Integer id, double quantity)
   throws InstantiationPropertyException {
     System.err.println("Adding");
    // the quantity is defaulted to 1, so if you don't set it you will get one
    if (quantity == 0) quantity = 1;
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    ShoppingTrolleyItem item = trolley.getItem(id);
    if (item == null) {
      Double price = null;
      String priceString = 
             MelatiUtil.getFormNulled(melati.getTemplateContext(), "price");
      if (priceString != null) price = new Double(priceString);
      item = newItem(trolley,id,price,
          MelatiUtil.getFormNulled(melati.getTemplateContext(), "description"));
    }    
    item.setQuantity(item.getQuantity() + quantity);
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley");
  }

  /** 
   * remove a single item from the trolley, the product is specified on the 
   * pathinfo which should be of the form:
   *
   * /<logicaldatabase>/<id>/Remove/
   *
   * @param melati - the melati for this request
   *
   * @return - "Trolley" - the page where users manipulate their 
   *           Shopping Trolley
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String Remove(Melati melati, Integer id)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    ShoppingTrolleyItem item = trolley.getItem(id);
    trolley.removeItem(item);
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley");
  }

  /** 
   * set the quantity of an item in the trolley, the product and new quantity is
   * specified on the pathinfo which should be of the form:
   *
   * /<logicaldatabase>/<id>/<quantity>/Set/
   *
   * @param melati - the melati for this request
   * @param id - the id of the item to be removed
   *
   * @return - "Trolley" - the page where users manipulate their 
   *           Shopping Trolley
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String Set(Melati melati, Integer id, double quantity)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    ShoppingTrolleyItem item = trolley.getItem(id);
    if (item == null) item = newItem(trolley,id, null, null);
    item.setQuantity(quantity);
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Trolley");
  }
  
  /** 
   * return the page where the user enters their details
   *
   * @param melati - the melati for this request
   *
   * @return - "Details" - the page where users enter their details
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String Details(Melati melati)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    trolley.setDefaultDetails(melati);
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Details");
  }
  
  /** 
   * update the user's information and return the
   * confirmation page
   *
   * @param melati - the melati for this request
   *
   * @return - "Confirm" - the page where users confirm their order
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String Confirm(Melati melati)
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    if (MelatiUtil.getFormNulled(melati.getTemplateContext(),"submittoken") != null) 
      trolley.setFromForm(melati);
    trolley.save();
    melati.getTemplateContext().put("trolley",trolley);
    return shoppingTemplate(melati, "Confirm");
  }
  
  /** 
   * complete the user's shopping experience, and remove their Shopping Trolley
   * from the Session
   *
   * If you need to do something (like send an email) following confirmation of 
   * payment, define the method in <Your>ShoppingTrolley.java:
   * 
   *  public void confirmPayment(Melati melati) {}
   *
   * Because the callback request (typically) comes from the Payment Server, you
   * will not have the user's shoping trolley (Session) available to them.  You 
   * will therefor need to get whatever information you require from something 
   * persistent.
   *
   * The alternative is to get the Payment Server to generate the emails (or 
   * whatever) for you.  Most Payment Servers offer this facility. 
   *
   * @param melati - the melati for this request
   *
   * @return - "Paid" - a message thanking the user for their order
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String Paid(Melati melati) 
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    trolley.confirmPayment(melati);
    // and get rid of it
    trolley.remove(melati);
    return shoppingTemplate(melati, "Paid");
  }
  
  /** 
   * abandon a trolley 
   *
   * @param melati - the melati for this request
   *
   * @return - "Trolley" - the initial trolley page
   *
   * @throws InstantiationPropertyException - if we cannot construct the trolley
   */
  protected String Abandon(Melati melati) 
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    // and get rid of it
    trolley.remove(melati);
    return shoppingTemplate(melati, "Trolley");
  }
  
  /** 
   * force a user to login
   *
   * @param melati - the melati for this request
   */
  protected void assertLogin(Melati melati) 
   throws InstantiationPropertyException {
    ShoppingTrolley trolley = ShoppingTrolley.getInstance(melati,config);
    // deligate to your trolley
    trolley.assertLogin(melati);
  }

  /** 
   * create the full name of the tempalte to be returned
   *
   * @param melati - the melati for this request (not used)
   * @param name - the name of the template
   *
   * @return - the full path to the template
   */
  protected final String shoppingTemplate(Melati melati, String name) {
    return "shopping/" + name;
  }

  /** 
   * create a new item and add it to the ShoppingTrolley
   *
   * if a price is passed
   *
   * @param trolley - the trolley to add the item to 
   * @param melati - the melati for this request
   * @param id - the id of the item to be added
   *
   * @return - the new shopping trolley item
   */
  private ShoppingTrolleyItem newItem(ShoppingTrolley trolley, Integer id,
                                      Double price, String description)
   throws InstantiationPropertyException {
    return trolley.newItem(id, description, price);
  }

  /** 
   * a ShoppingContext contains additional information from parsing the pathInfo
   */
  public class ShoppingContext extends MelatiContext {
    // shopping trolley id
    Integer stid;
    double quantity;
  }

  /** 
   * override the building of the MelatiContext in order to glean the additional
   * information required for the Shopping Trolley system
   *
   * @param melati - the melati for this request
   *
   * @return - the ShoppingContext with as many bits set up as possible
   *
   * @throws PathInfoException - if we don't understand the PathInfo
   */
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

}
