       README
       ======

       Melati Shopping Trolley Interface
       ---------------------------------

       (c) Paneris 2001
       Author: Tim Joyce (timj@paneris.org)


Introduction
------------

This file describes the Melati (http://www.melati.org) shopping trolley 
interface developed by Paneris (http://www.paneris.org) and how to install and
set it up. It assumes you have installed Melati and have created your own POEM 
database/melati application to which you wish to add the board system.

The shopping trolley system is a java package to be used with Melati. Melati is
a java servlet based framework for developing HTTP applications using
POEM. POEM provides an OO wrapper around (currently) an SQL database.
Together Melati and POEM provide a RAD toolkit for web sites.

The Melati Shopping Trolley Interface is designed to provide a flexible adstraction 
of the basic processes of allowing customers to buy goods on a website. By 
default, it is not integrated with POEM, and so makes no assumptions about 
the underlying database implementaion. It does provide the following:

o- Ability to add / remove / update items in a shopping trolley (stored in 
    the users session)
o- Collection of User details
o- Calculation of VAT and delivery charges
o- Connection to a 3rd party site for payment

Because exact implementations of shopping trolleys are notoriously varied, this 
interface seeks to provide as much flexibility as possible.  You can customise 
your implementation in 2 ways:

1) Writing implementations of 2 abstract classes: 
    - org.paneris.melati.shopping.ShoppingTrolley and 
    - org.paneris.melati.shopping.ShoppingTrolleyItem
    This allows you to decide how to calculate devliery charges etc.

2) Writing tempaltes:
    - Trolley.wm
    - Details.wm
    - Confirm.wm 
    - Order.wm
    - Menu.wm  
  
The basic process as experienced by the user is fairly typical of any 
online Shopping experience:

1) Load up the trolley
2) Enter customer details
3) Confirm order
4) Make payment




INSTALLATION
------------

0) Preinstallation System Requirements
--------------------------------------

Please install and test Melati: http://www.melati.org



1) Download and unzip Melatishopping
------------------------------------

Download the latest Melatishopping snapshot from http://www.melati.org
and unzip or untar it. eg: 

Unix users type: 
  cd /usr/local/
  tar -xzf melatishopping_0.50.tar.gz
  for convenience, create a soft link: ln -s melati_0.50 melati 

Windows users should use WinZip


2) Arrange for the Melati Shopping classes and properties files to be accessible 
to your servlet runner.

For Apache Jserv, this can be done either in the jserv.properties file, by adding a lines

wrapper.classpath=/usr/local/melatishopping/src
wrapper.classpath=/usr/local/melatishopping/lib/melati.jar

or in your <zone>.properties file by adding

/usr/local/melatishopping/src,/usr/local/melatishopping/lib/melatishopping.jar

to the line beginning repositories=
        

3) Write your ShoppingTrolley and ShoppingTrolleyItem classes
-------------------------------------------------------------

These should be based on DefaultShoppingTrolley.java and 
DefaultShoppingTrolleyItem.java.  You will (at least) have to define the following
abstract methods.  If you don't want to make use of a paticular feature, simply
"return".

ShoppingTrolley:

  /* set the Locale for this trolley
  */
  public Locale getLocale(){}

  /* load a trolley from something persistent
  */
  public void load(Integer id) {}

  /* save a trolley to something persistent
  */
  public void save() {}

  /* provide a mechanism for working out if 
     this order should include a delivery charge
  */
  public boolean hasDelivery(){}

  /* you need to provide some mechanism for calculating the delivery
     value for the order (item delivery values are calculated individually
  */
  public double getDeliveryValue() {}
  

  /* provide a mechanism for working out if 
     this order should include a discount
  */
  public boolean hasDiscount() {}

  /* if you want to apply a discount to this order, do it here
  */
  public double getDiscountRate() {}

  /* provide a mechanism for working out if 
     this order should include VAT (default should be true)
  */
  public boolean hasVAT() {}

  /* if you want something to happen (like sending an email) when the user
     has returned from paying, do it here */
  public void confirmPayment(Melati melati) {}



ShoppingTrolleyItem

  /* load in information about this product given an id.  
     perhaps this id represents a poem troid?
  */
  protected void load(Integer id){};
    
  /* work out the cost of delivery for this item
  */
  public double getDeliveryValue() {}



4) Specify your classes in org.paneris.melati.shopping.ShoppingTrolley.properties
---------------------------------------------------------------------------------

For example, you need to specify the 2 classes you created above:

# What class implements the shopping trolley
org.paneris.melati.shopping.ShoppingTrolley.trolley = <Your>ShoppingTrolley

# What class implements the shopping trolley items
org.paneris.melati.shopping.ShoppingTrolley.item = <Your>ShoppingTrolleyItem



5)  Modify your Templates
-------------------------

The interface to the Shopping Trolley is defined in the following templates:

Trolley.wm - The initial page where you can add/remove/edit trolley items
Details.wm - Collection of user details
Confirm.wm - Confirmation of order
Order.wm - The trolley rendered as text
Menu.wm - The menu



6) Link to the Payment Server
-----------------------------

There are many companies offering secure payment services.  My current personal
favorite is http://www.securetrading.com.  You will need to edit Confirm.wm to 
supply the correct details to your chosen server.



7) Link back from the Payment Server
------------------------------------

If you need to do something (like send an email) following confirmation of 
payment, define the method in <Your>ShoppingTrolley.java:

  public void confirmPayment(Melati melati) {}

This will return the template Paid.wm.

Because the callback request (typically) comes from the Payment Server, you
will not have the user's shoping trolley (Session) available to them.  You 
will therefor need to get whatever information you require from something 
persistent.

The alternative is to get the Payment Server to generate the emails (or 
whatever) for you.  Most Payment Servers offer this facility. 



8) Link into Your Application
-----------------------------

You can add items to your shopping trolley using the following URL:
http://<host name>/<servlet zone>/org.paneris.melati.shopping.Trolley/<database name>/<product id>/Add

This will load the product with the supplied Troid, using the method:

 load(Integer id){};    in org.paneris.melati.shopping.shoppingTrolleyItem  


Alternatively, supply the price and description as part of the request:

?price=8.99&description=Caterpillar+May

This has obvious security considerations (users can change the price).  
This mechanism is best used for prototyping or for systems when orders are checked
manually before payment is taken (telephone ordering etc).


9) Examples
-----------

Included with this distribution are 2 examples:

1) JammyJoes Toyshop (http://www.jammyjoes.com).  Source files in lib/jammyjoes.zip
1) Bibliomania  (http://www.bibliomania.com).  Source files in lib/bibliomania.zip

--EOF--


