/*     */ package com.hp.ilo2.intgapp;
/*     */ 
/*     */ import com.hp.ilo2.remcons.URLDialog;
/*     */ import com.hp.ilo2.remcons.remcons;
/*     */ import com.hp.ilo2.remcons.telnet;
/*     */ import com.hp.ilo2.virtdevs.MediaAccess;
/*     */ import com.hp.ilo2.virtdevs.VErrorDialog;
/*     */ import com.hp.ilo2.virtdevs.VFileDialog;
/*     */ import com.hp.ilo2.virtdevs.virtdevs;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.util.Arrays;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JApplet;
/*     */ import javax.swing.JCheckBoxMenuItem;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuBar;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JScrollPane;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class intgapp
/*     */   extends JApplet
/*     */   implements Runnable, ActionListener, ItemListener
/*     */ {
/*     */   public virtdevs virtdevsObj;
/*     */   public remcons remconsObj;
/*     */   public locinfo locinfoObj;
/*     */   public jsonparser jsonObj;
/*     */   public String optional_features;
/*     */   public JFrame dispFrame;
/*     */   public JPanel mainPanel;
/*     */   JMenuBar mainMenuBar;
/*     */   JMenu psMenu;
/*     */   JMenu vdMenu;
/*     */   JMenu kbMenu;
/*     */   JMenu kbCAFMenu;
/*     */   JMenu kbAFMenu;
/*     */   JMenu kbLangMenu;
/*     */   JMenu hlpMenu;
/*     */   int vdmenuIndx;
/*     */   int fdMenuItems;
/*     */   int cdMenuItems;
/*     */   private MediaAccess ma;
/*     */   JCheckBoxMenuItem[] vdMenuItems;
/*     */   public JMenuItem vdMenuItemCrImage;
/*     */   JMenuItem momPress;
/*     */   JMenuItem pressHold;
/*  64 */   public int blade = 0; JMenuItem powerCycle; JMenuItem sysReset; JMenuItem ctlAltDel; JMenuItem numLock; JMenuItem capsLock; JMenuItem ctlAltBack; JMenuItem hotKeys; JMenuItem aboutJirc; JMenuItem[] ctlAltFn; JMenuItem[] AltFn; JCheckBoxMenuItem[] localKbdLayout; JPanel dispStatusBar; JMenuItem mdebug1; JMenuItem mdebug2; JMenuItem mdebug3; JScrollPane scroller; public String enc_key; public String rc_port; public String vm_key; public String vm_port; public String server_name; public String ilo_fqdn; public String enclosure;
/*  65 */   public int bay = 0;
/*  66 */   public byte[] enc_key_val = new byte[16];
/*     */   
/*     */   String rcErrMessage;
/*     */   
/*     */   public int dwidth;
/*     */   public int dheight;
/*     */   public boolean exit = false;
/*     */   public boolean fdSelected = false;
/*     */   public boolean cdSelected = false;
/*     */   public boolean in_enclosure = false;
/*  76 */   private int REMCONS_MAX_FN_KEYS = 12;
/*  77 */   private int REMCONS_MAX_KBD_LAYOUT = 17;
/*     */ 
/*     */   
/*     */   public String getLocalString(int paramInt) {
/*  81 */     String str = "";
/*     */     try {
/*  83 */       str = this.locinfoObj.getLocString(paramInt);
/*     */     } catch (Exception exception) {
/*     */       
/*  86 */       System.out.println("remcons:getLocalString" + exception.getMessage());
/*     */     } 
/*  88 */     return str;
/*     */   }
/*     */ 
/*     */   
/*     */   public intgapp() {
/*  93 */     this.virtdevsObj = new virtdevs(this);
/*  94 */     this.remconsObj = new remcons(this);
/*  95 */     this.locinfoObj = new locinfo(this);
/*  96 */     this.jsonObj = new jsonparser(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void init() {
/* 101 */     boolean bool = true;
/* 102 */     String str = null;
/*     */     
/* 104 */     System.out.println("Started Retrieving parameters from ILO..");
/* 105 */     str = this.jsonObj.getJSONRequest("rc_info");
/*     */     
/* 107 */     ApplyRcInfoParameters(str);
/* 108 */     System.out.println("Completed Retrieving parameters from ILO");
/* 109 */     bool = this.locinfoObj.initLocStrings();
/*     */     
/* 111 */     this.virtdevsObj.init();
/* 112 */     this.remconsObj.init();
/*     */     
/* 114 */     ui_init();
/*     */     
/* 116 */     if (null == str) {
/* 117 */       System.out.println("Failed to retrive parameters from ILO");
/* 118 */       new VErrorDialog(this.dispFrame, getLocalString(8212), this.rcErrMessage, true);
/* 119 */       this.dispFrame.setVisible(false);
/*     */     }
/* 121 */     else if (false == bool) {
/* 122 */       new VErrorDialog(this.dispFrame, getLocalString(8212), this.locinfoObj.rcErrMessage, true);
/* 123 */       this.dispFrame.setVisible(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void start() {
/*     */     try {
/* 131 */       this.virtdevsObj.start();
/* 132 */       this.remconsObj.start();
/*     */       
/* 134 */       this.dispFrame.getContentPane().add(this.scroller, "Center");
/* 135 */       this.dispFrame.getContentPane().add(this.dispStatusBar, "South");
/* 136 */       this.scroller.validate();
/* 137 */       this.dispStatusBar.validate();
/* 138 */       this.dispFrame.validate();
/*     */ 
/*     */       
/* 141 */       System.out.println("Set Inital focus for session..");
/* 142 */       this.remconsObj.session.requestFocus();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 161 */       run();
/*     */     } catch (Exception exception) {
/*     */       
/* 164 */       System.out.println("FAILURE: exception starting applet");
/* 165 */       exception.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/* 172 */     this.exit = true;
/* 173 */     this.virtdevsObj.stop();
/* 174 */     this.remconsObj.remconsUnInstallKeyboardHook();
/* 175 */     this.remconsObj.stop();
/*     */   }
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 180 */     System.out.println("Destroying subsustems");
/* 181 */     this.exit = true;
/* 182 */     this.remconsObj.remconsUnInstallKeyboardHook();
/* 183 */     this.virtdevsObj.destroy();
/* 184 */     this.remconsObj.destroy();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void run() {
/* 195 */     byte b = 0;
/* 196 */     boolean bool = false;
/*     */     while (true) {
/*     */       try {
/* 199 */         b = 0;
/* 200 */         byte b1 = 0;
/* 201 */         this.ma = new MediaAccess();
/* 202 */         String[] arrayOfString = this.ma.devices();
/* 203 */         for (byte b2 = 0; arrayOfString != null && b2 < arrayOfString.length; b2++) {
/* 204 */           int i = this.ma.devtype(arrayOfString[b2]);
/* 205 */           if (i == 2 || i == 5) {
/* 206 */             b1++;
/*     */           }
/*     */         } 
/*     */         
/* 210 */         if (b1 > this.vdmenuIndx - 4) {
/* 211 */           ClassLoader classLoader = getClass().getClassLoader();
/* 212 */           for (byte b3 = 0; arrayOfString != null && b3 < arrayOfString.length; b3++) {
/* 213 */             bool = false;
/* 214 */             int i = this.ma.devtype(arrayOfString[b3]);
/* 215 */             for (byte b4 = 0; b4 < this.vdmenuIndx - 4; b4++) {
/* 216 */               if (arrayOfString[b3].equals(this.vdMenu.getItem(b4).getText())) {
/* 217 */                 bool = true;
/* 218 */                 b++;
/*     */               } 
/*     */             } 
/* 221 */             if (!bool) {
/* 222 */               if (i == 2) {
/* 223 */                 System.out.println("Device attached: " + arrayOfString[b3]);
/* 224 */                 this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(arrayOfString[b3]);
/* 225 */                 this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + arrayOfString[b3]);
/* 226 */                 this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/* 227 */                 if (arrayOfString[b3].equals("A:") || arrayOfString[b3].equals("B:")) {
/* 228 */                   this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/FloppyDisk.png"))));
/*     */                 } else {
/* 230 */                   this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/usb.png"))));
/*     */                 } 
/* 232 */                 this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx], b);
/* 233 */                 this.vdMenu.updateUI();
/* 234 */                 this.vdmenuIndx++;
/*     */                 break;
/*     */               } 
/* 237 */               if (i == 5) {
/* 238 */                 System.out.println("CDROM Hot plug device auto-update no supported at this time");
/*     */               }
/*     */             }
/*     */           
/*     */           } 
/* 243 */         } else if (b1 < this.vdmenuIndx - 4) {
/* 244 */           for (byte b3 = 0; b3 < this.vdmenuIndx - 4; b3++) {
/* 245 */             bool = false;
/* 246 */             for (byte b4 = 0; arrayOfString != null && b4 < arrayOfString.length; b4++) {
/* 247 */               int i = this.ma.devtype(arrayOfString[b4]);
/* 248 */               if ((i == 2 || i == 5) && 
/* 249 */                 this.vdMenu.getItem(b3).getText().equals(arrayOfString[b4])) {
/* 250 */                 bool = true;
/*     */               }
/*     */             } 
/*     */             
/* 254 */             if (!bool) {
/* 255 */               System.out.println("Device removed: " + this.vdMenu.getItem(b3).getText());
/* 256 */               this.vdMenu.remove(b3);
/* 257 */               this.vdMenu.updateUI();
/* 258 */               this.vdmenuIndx--;
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/* 263 */         this.ma = null;
/* 264 */         this.remconsObj.session.set_status(3, "");
/* 265 */         this.remconsObj.sleepAtLeast(5000L);
/* 266 */         if (this.exit) {
/*     */           break;
/*     */         }
/*     */       } catch (InterruptedException interruptedException) {
/*     */         
/* 271 */         System.out.println("Exception on intgapp");
/*     */       } 
/*     */     } 
/* 274 */     System.out.println("Intgapp stopped running");
/*     */   }
/*     */ 
/*     */   
/*     */   public void paintComponent(Graphics paramGraphics) {
/* 279 */     paintComponents(paramGraphics);
/* 280 */     paramGraphics.drawString("Remote Console JApplet Loaded", 10, 50);
/*     */   }
/*     */ 
/*     */   
/*     */   public void ui_init() {
/* 285 */     System.out.println("Message from ui_init55");
/* 286 */     this.dispFrame = new JFrame("JavaApplet IRC Window");
/* 287 */     this.dispFrame.getContentPane().setLayout(new BorderLayout());
/* 288 */     this.dispFrame.addWindowListener(new WindowCloser(this));
/* 289 */     this.mainMenuBar = new JMenuBar();
/* 290 */     this.dispStatusBar = new JPanel(new BorderLayout());
/* 291 */     this.dispStatusBar.add(((telnet)this.remconsObj.session).status_box, "West");
/* 292 */     this.dispStatusBar.add(this.remconsObj.pwrStatusPanel, "East");
/*     */ 
/*     */     
/* 295 */     String str = this.jsonObj.getJSONRequest("session_info");
/*     */     
/* 297 */     JPopupMenu.setDefaultLightWeightPopupEnabled(false);
/* 298 */     this.dispFrame.setJMenuBar(this.mainMenuBar);
/* 299 */     makePsMenu(this.mainMenuBar, this.jsonObj.getJSONNumber(str, "reset_priv"));
/* 300 */     makeVdMenu(this.mainMenuBar, this.jsonObj.getJSONNumber(str, "virtual_media_priv"));
/* 301 */     makeKbMenu(this.mainMenuBar);
/* 302 */     makeHlpMenu(this.mainMenuBar);
/*     */ 
/*     */     
/* 305 */     this.scroller = new JScrollPane((Component)this.remconsObj.session, 20, 30);
/* 306 */     this.scroller.setVisible(true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 313 */       String str1 = getLocalString(4132) + " " + this.server_name + " " + getLocalString(4133) + " " + this.ilo_fqdn;
/*     */       
/* 315 */       if (this.blade == 1 && this.in_enclosure == true) {
/* 316 */         str1 = str1 + " " + getLocalString(4134) + " " + this.enclosure + " " + getLocalString(4135) + " " + this.bay;
/*     */       }
/*     */       
/* 319 */       this.dispFrame.setTitle(str1);
/*     */     } catch (Exception exception) {
/*     */       
/* 322 */       this.dispFrame.setTitle(getLocalString(4132) + " " + getCodeBase().getHost());
/* 323 */       System.out.println("IRC title not available");
/*     */     } 
/* 325 */     int i = (Toolkit.getDefaultToolkit().getScreenSize()).width;
/* 326 */     int j = (Toolkit.getDefaultToolkit().getScreenSize()).height;
/*     */     
/* 328 */     boolean bool1 = (i < 1054) ? i : true;
/* 329 */     boolean bool2 = (j < 874) ? (j - 30) : true;
/* 330 */     boolean bool3 = (i > 1054) ? ((i - 1054) / 2) : false;
/* 331 */     boolean bool4 = (j > 874) ? ((j - 874) / 2) : false;
/*     */     
/* 333 */     this.dispFrame.setSize(bool1, bool2);
/* 334 */     this.dispFrame.setLocation(bool3, bool4);
/* 335 */     System.out.println("check dimensions " + bool1 + " " + bool2 + " " + bool3 + " " + bool4);
/* 336 */     this.dispFrame.setVisible(true);
/*     */     
/*     */     try {
/* 339 */       Insets insets = this.dispFrame.getInsets();
/* 340 */       ClassLoader classLoader = getClass().getClassLoader();
/* 341 */       this.dispFrame.setIconImage(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/hp_logo.png")));
/* 342 */       Image image = this.dispFrame.getIconImage();
/* 343 */       if (image == null) {
/* 344 */         System.out.println("Dimage is null");
/*     */       }
/*     */     } catch (Exception exception) {
/*     */       
/* 348 */       System.out.println("JIRC icon not available");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void makeHlpMenu(JMenuBar paramJMenuBar) {
/* 358 */     this.hlpMenu = new JMenu(getLocalString(4136));
/* 359 */     this.aboutJirc = new JMenuItem(getLocalString(4137));
/* 360 */     this.aboutJirc.addActionListener(this);
/* 361 */     this.hlpMenu.add(this.aboutJirc);
/* 362 */     paramJMenuBar.add(this.hlpMenu);
/*     */   }
/*     */   
/*     */   protected void makeVdMenu(JMenuBar paramJMenuBar, int paramInt) {
/* 366 */     this.vdMenu = new JMenu(getLocalString(4098));
/* 367 */     if (paramInt == 1) {
/* 368 */       paramJMenuBar.add(this.vdMenu);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateVdMenu() {
/* 375 */     this.ma = new MediaAccess();
/* 376 */     ClassLoader classLoader = getClass().getClassLoader();
/*     */     
/* 378 */     String str1 = this.jsonObj.getJSONRequest("vm_status");
/*     */     
/* 380 */     String str2 = this.jsonObj.getJSONArray(str1, "options", 0);
/*     */     
/* 382 */     String str3 = this.jsonObj.getJSONArray(str1, "options", 1);
/*     */ 
/*     */     
/* 385 */     String[] arrayOfString = this.ma.devices();
/* 386 */     this.vdmenuIndx = 0;
/* 387 */     if (arrayOfString != null) {
/* 388 */       this.vdMenuItems = new JCheckBoxMenuItem[arrayOfString.length + 5];
/* 389 */       for (byte b = 0; b < arrayOfString.length; b++) {
/* 390 */         int i = this.ma.devtype(arrayOfString[b]);
/* 391 */         if (i == 5) {
/* 392 */           this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(arrayOfString[b]);
/* 393 */           this.vdMenuItems[this.vdmenuIndx].setActionCommand("cd" + arrayOfString[b]);
/* 394 */           this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/* 395 */           this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/CD_Drive.png"))));
/* 396 */           this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/* 397 */           this.vdmenuIndx++;
/*     */         }
/* 399 */         else if (i == 2) {
/* 400 */           this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(arrayOfString[b]);
/* 401 */           this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + arrayOfString[b]);
/* 402 */           this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/* 403 */           if (arrayOfString[b].equals("A:") || arrayOfString[b].equals("B:")) {
/* 404 */             this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/FloppyDisk.png"))));
/*     */           } else {
/* 406 */             this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/usb.png"))));
/*     */           } 
/* 408 */           this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/* 409 */           this.vdmenuIndx++;
/*     */         } 
/*     */       } 
/*     */     } else {
/* 413 */       this.vdMenuItems = new JCheckBoxMenuItem[5];
/* 414 */       System.out.println("Media Access not available...");
/*     */     } 
/* 416 */     this.ma = null;
/*     */ 
/*     */     
/* 419 */     this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4130) + " " + getLocalString(4106));
/* 420 */     this.vdMenuItems[this.vdmenuIndx].setActionCommand("fd" + getLocalString(12567));
/* 421 */     this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/* 422 */     this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Image_File.png"))));
/* 423 */     this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/* 424 */     this.vdmenuIndx++;
/*     */     
/* 426 */     this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4131) + getLocalString(4106));
/* 427 */     this.vdMenuItems[this.vdmenuIndx].setActionCommand("FLOPPY");
/* 428 */     this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Network.png"))));
/* 429 */     this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/* 430 */     this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/* 431 */     this.vdmenuIndx++;
/* 432 */     if (this.jsonObj.getJSONNumber(str2, "vm_url_connected") == 1 && this.jsonObj.getJSONNumber(str2, "vm_connected") == 1) {
/* 433 */       this.fdSelected = true;
/* 434 */       lockFdMenu(false, "URL Removable Media");
/*     */     } 
/*     */ 
/*     */     
/* 438 */     this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4130) + " " + getLocalString(4107));
/* 439 */     this.vdMenuItems[this.vdmenuIndx].setActionCommand("cd" + getLocalString(12567));
/* 440 */     this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/* 441 */     this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Image_File.png"))));
/* 442 */     this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/* 443 */     this.vdmenuIndx++;
/*     */     
/* 445 */     this.vdMenuItems[this.vdmenuIndx] = new JCheckBoxMenuItem(getLocalString(4131) + getLocalString(4107));
/* 446 */     this.vdMenuItems[this.vdmenuIndx].setActionCommand("CDROM");
/* 447 */     this.vdMenuItems[this.vdmenuIndx].setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Network.png"))));
/* 448 */     this.vdMenu.add(this.vdMenuItems[this.vdmenuIndx]);
/* 449 */     this.vdMenuItems[this.vdmenuIndx].addItemListener(this);
/* 450 */     this.vdmenuIndx++;
/* 451 */     if (this.jsonObj.getJSONNumber(str3, "vm_url_connected") == 1 && this.jsonObj.getJSONNumber(str3, "vm_connected") == 1) {
/* 452 */       this.cdSelected = true;
/* 453 */       lockCdMenu(false, "URL CD/DVD-ROM");
/*     */     } 
/*     */ 
/*     */     
/* 457 */     this.vdMenu.addSeparator();
/* 458 */     this.vdMenuItemCrImage = new JMenuItem(getLocalString(4109));
/* 459 */     this.vdMenuItemCrImage.setActionCommand("CreateDiskImage");
/* 460 */     this.vdMenuItemCrImage.addActionListener(this);
/* 461 */     this.vdMenu.add(this.vdMenuItemCrImage);
/*     */   }
/*     */ 
/*     */   
/*     */   public void lockCdMenu(boolean paramBoolean, String paramString) {
/* 466 */     byte b = 0;
/*     */     
/* 468 */     for (b = 0; b < this.vdmenuIndx; b++) {
/* 469 */       this.vdMenu.getItem(b).removeItemListener(this);
/*     */ 
/*     */       
/* 472 */       if (this.vdMenu.getItem(b).getActionCommand().startsWith("cd") || this.vdMenu.getItem(b).getActionCommand().equals("CDROM"))
/*     */       {
/* 474 */         if (paramString.equals(this.vdMenu.getItem(b).getText())) {
/*     */ 
/*     */           
/* 477 */           this.vdMenu.getItem(b).setSelected(!paramBoolean);
/*     */         } else {
/*     */           
/* 480 */           this.vdMenu.getItem(b).setSelected(false);
/* 481 */           this.vdMenu.getItem(b).setEnabled(paramBoolean);
/*     */         } 
/*     */       }
/* 484 */       this.vdMenu.getItem(b).addItemListener(this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void lockFdMenu(boolean paramBoolean, String paramString) {
/* 490 */     byte b = 0;
/*     */     
/* 492 */     for (b = 0; b < this.vdmenuIndx; b++) {
/* 493 */       this.vdMenu.getItem(b).removeItemListener(this);
/*     */       
/* 495 */       if (this.vdMenu.getItem(b).getActionCommand().startsWith("fd") || this.vdMenu.getItem(b).getActionCommand().equals("FLOPPY"))
/*     */       {
/* 497 */         if (paramString.equals(this.vdMenu.getItem(b).getText())) {
/*     */ 
/*     */           
/* 500 */           this.vdMenu.getItem(b).setSelected(!paramBoolean);
/*     */         } else {
/*     */           
/* 503 */           this.vdMenu.getItem(b).setSelected(false);
/* 504 */           this.vdMenu.getItem(b).setEnabled(paramBoolean);
/*     */         } 
/*     */       }
/* 507 */       this.vdMenu.getItem(b).addItemListener(this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void makePsMenu(JMenuBar paramJMenuBar, int paramInt) {
/* 513 */     ClassLoader classLoader = getClass().getClassLoader();
/*     */     
/* 515 */     this.psMenu = new JMenu(getLocalString(4097));
/*     */     
/* 517 */     this.momPress = new JMenuItem(getLocalString(4100));
/* 518 */     this.momPress.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/press.png"))));
/* 519 */     this.momPress.setActionCommand("psMomPress");
/* 520 */     this.momPress.addActionListener(this);
/* 521 */     this.psMenu.add(this.momPress);
/*     */     
/* 523 */     this.pressHold = new JMenuItem(getLocalString(4101));
/* 524 */     this.pressHold.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/hold.png"))));
/* 525 */     this.pressHold.setActionCommand("psPressHold");
/* 526 */     this.pressHold.addActionListener(this);
/*     */     
/* 528 */     this.powerCycle = new JMenuItem(getLocalString(4102));
/* 529 */     this.powerCycle.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/coldboot.png"))));
/* 530 */     this.powerCycle.setActionCommand("psPowerCycle");
/* 531 */     this.powerCycle.addActionListener(this);
/*     */     
/* 533 */     this.sysReset = new JMenuItem(getLocalString(4103));
/* 534 */     this.sysReset.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/reset.png"))));
/* 535 */     this.sysReset.setActionCommand("psSysReset");
/* 536 */     this.sysReset.addActionListener(this);
/*     */     
/* 538 */     if (paramInt == 1) {
/* 539 */       paramJMenuBar.add(this.psMenu);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void updatePsMenu(int paramInt) {
/* 545 */     if (0 == paramInt) {
/* 546 */       this.psMenu.remove(this.pressHold);
/* 547 */       this.psMenu.remove(this.powerCycle);
/* 548 */       this.psMenu.remove(this.sysReset);
/*     */     }
/*     */     else {
/*     */       
/* 552 */       this.psMenu.remove(this.pressHold);
/* 553 */       this.psMenu.remove(this.powerCycle);
/* 554 */       this.psMenu.remove(this.sysReset);
/*     */       
/* 556 */       this.psMenu.add(this.pressHold);
/* 557 */       this.psMenu.add(this.powerCycle);
/* 558 */       this.psMenu.add(this.sysReset);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void makeKbMenu(JMenuBar paramJMenuBar) {
/* 565 */     ClassLoader classLoader = getClass().getClassLoader();
/* 566 */     this.kbMenu = new JMenu(getLocalString(4099));
/* 567 */     this.kbCAFMenu = new JMenu("CTRL-ALT-Fn");
/* 568 */     this.kbAFMenu = new JMenu("ALT-Fn");
/* 569 */     this.kbLangMenu = new JMenu(getLocalString(4110));
/*     */     
/* 571 */     this.ctlAltDel = new JMenuItem(getLocalString(4104));
/* 572 */     this.ctlAltDel.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
/* 573 */     this.ctlAltDel.setActionCommand("kbCtlAltDel");
/* 574 */     this.ctlAltDel.addActionListener(this);
/* 575 */     this.kbMenu.add(this.ctlAltDel);
/*     */     
/* 577 */     this.numLock = new JMenuItem(getLocalString(4105));
/* 578 */     this.numLock.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
/* 579 */     this.numLock.setActionCommand("kbNumLock");
/* 580 */     this.numLock.addActionListener(this);
/* 581 */     this.kbMenu.add(this.numLock);
/*     */     
/* 583 */     this.capsLock = new JMenuItem(getLocalString(4128));
/* 584 */     this.capsLock.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
/* 585 */     this.capsLock.setActionCommand("kbCapsLock");
/* 586 */     this.capsLock.addActionListener(this);
/* 587 */     this.kbMenu.add(this.capsLock);
/*     */     
/* 589 */     this.ctlAltBack = new JMenuItem("CTRL-ALT-BACKSPACE");
/* 590 */     this.ctlAltBack.setIcon(new ImageIcon(getImage(classLoader.getResource("com/hp/ilo2/remcons/images/Keyboard.png"))));
/* 591 */     this.ctlAltBack.setActionCommand("kbCtlAltBack");
/* 592 */     this.ctlAltBack.addActionListener(this);
/*     */ 
/*     */     
/* 595 */     this.ctlAltFn = new JMenuItem[this.REMCONS_MAX_FN_KEYS]; byte b;
/* 596 */     for (b = 0; b < this.REMCONS_MAX_FN_KEYS; b++) {
/* 597 */       this.ctlAltFn[b] = new JMenuItem("CTRL-ALT-F" + (b + 1));
/* 598 */       this.ctlAltFn[b].setActionCommand("kbCtrlAltFn" + b);
/*     */ 
/*     */       
/* 601 */       this.ctlAltFn[b].addActionListener(this);
/* 602 */       this.kbCAFMenu.add(this.ctlAltFn[b]);
/*     */     } 
/* 604 */     this.AltFn = new JMenuItem[this.REMCONS_MAX_FN_KEYS];
/* 605 */     for (b = 0; b < this.REMCONS_MAX_FN_KEYS; b++) {
/* 606 */       this.AltFn[b] = new JMenuItem("ALT-F" + (b + 1));
/* 607 */       this.AltFn[b].setActionCommand("kbAltFn" + b);
/* 608 */       this.AltFn[b].addActionListener(this);
/* 609 */       this.kbAFMenu.add(this.AltFn[b]);
/*     */     } 
/*     */     
/* 612 */     this.localKbdLayout = new JCheckBoxMenuItem[this.REMCONS_MAX_KBD_LAYOUT];
/* 613 */     for (b = 0; b < this.REMCONS_MAX_KBD_LAYOUT; b++) {
/* 614 */       this.localKbdLayout[b] = new JCheckBoxMenuItem(getLocalString(4111 + b));
/* 615 */       this.localKbdLayout[b].setActionCommand("localKbdLayout" + b);
/* 616 */       this.localKbdLayout[b].addItemListener(this);
/* 617 */       this.kbLangMenu.add(this.localKbdLayout[b]);
/*     */     } 
/* 619 */     this.localKbdLayout[0].setSelected(true);
/*     */     
/* 621 */     String str = System.getProperty("os.name").toLowerCase();
/* 622 */     if (!str.startsWith("windows")) {
/* 623 */       this.kbMenu.add(this.ctlAltBack);
/* 624 */       this.kbMenu.add(this.kbCAFMenu);
/* 625 */       this.kbMenu.add(this.kbAFMenu);
/* 626 */       this.kbMenu.add(this.kbLangMenu);
/*     */     } 
/*     */     
/* 629 */     this.kbMenu.addSeparator();
/* 630 */     this.hotKeys = new JMenuItem(getLocalString(4129));
/* 631 */     this.hotKeys.addActionListener(this);
/* 632 */     this.kbMenu.add(this.hotKeys);
/*     */     
/* 634 */     paramJMenuBar.add(this.kbMenu);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 642 */     if (paramActionEvent.getSource() == this.momPress) {
/* 643 */       this.remconsObj.session.sendMomPress();
/*     */     }
/* 645 */     else if (paramActionEvent.getSource() == this.pressHold) {
/* 646 */       this.remconsObj.session.sendPressHold();
/*     */     }
/* 648 */     else if (paramActionEvent.getSource() == this.powerCycle) {
/* 649 */       this.remconsObj.session.sendPowerCycle();
/*     */     }
/* 651 */     else if (paramActionEvent.getSource() == this.sysReset) {
/* 652 */       this.remconsObj.session.sendSystemReset();
/*     */ 
/*     */     
/*     */     }
/* 656 */     else if (paramActionEvent.getSource() == this.ctlAltDel) {
/* 657 */       this.remconsObj.session.send_ctrl_alt_del();
/*     */     }
/* 659 */     else if (paramActionEvent.getSource() == this.numLock) {
/* 660 */       this.remconsObj.session.send_num_lock();
/*     */     }
/* 662 */     else if (paramActionEvent.getSource() == this.capsLock) {
/* 663 */       this.remconsObj.session.send_caps_lock();
/*     */     }
/* 665 */     else if (paramActionEvent.getSource() == this.ctlAltBack) {
/* 666 */       this.remconsObj.session.send_ctrl_alt_back();
/*     */     }
/* 668 */     else if (paramActionEvent.getSource() == this.hotKeys) {
/* 669 */       this.remconsObj.viewHotKeys();
/*     */ 
/*     */     
/*     */     }
/* 673 */     else if (paramActionEvent.getSource() == this.vdMenuItemCrImage) {
/* 674 */       this.virtdevsObj.createImage();
/*     */     
/*     */     }
/* 677 */     else if (paramActionEvent.getSource() == this.aboutJirc) {
/* 678 */       this.remconsObj.viewAboutJirc();
/*     */     }
/*     */     else {
/*     */       
/* 682 */       for (byte b = 0; b < this.REMCONS_MAX_FN_KEYS; b++) {
/* 683 */         if (paramActionEvent.getSource() == this.ctlAltFn[b]) {
/* 684 */           this.remconsObj.session.send_ctrl_alt_fn(b);
/*     */           break;
/*     */         } 
/* 687 */         if (paramActionEvent.getSource() == this.AltFn[b]) {
/* 688 */           this.remconsObj.session.send_alt_fn(b);
/*     */           break;
/*     */         } 
/*     */       } 
/* 692 */       if (b >= this.REMCONS_MAX_FN_KEYS) {
/* 693 */         System.out.println("Unhandled ActionItem" + paramActionEvent.getActionCommand());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void itemStateChanged(ItemEvent paramItemEvent) {
/* 701 */     boolean bool = false;
/* 702 */     JCheckBoxMenuItem jCheckBoxMenuItem = null;
/* 703 */     String str1 = null;
/* 704 */     String str2 = null;
/* 705 */     int i = paramItemEvent.getStateChange();
/*     */     
/*     */     byte b;
/* 708 */     for (b = 0; b < this.REMCONS_MAX_KBD_LAYOUT; b++) {
/* 709 */       if (this.localKbdLayout[b] == paramItemEvent.getSource() && i == 1) {
/* 710 */         System.out.println(b);
/* 711 */         this.localKbdLayout[b].setSelected(true);
/* 712 */         kbdLayoutMenuHandler(b);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 718 */     for (b = 0; b < this.vdmenuIndx; b++) {
/* 719 */       if (this.vdMenuItems[b] == paramItemEvent.getSource()) {
/* 720 */         jCheckBoxMenuItem = this.vdMenuItems[b];
/* 721 */         str1 = jCheckBoxMenuItem.getActionCommand();
/*     */         
/* 723 */         str2 = jCheckBoxMenuItem.getLabel();
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*     */     
/* 729 */     if (jCheckBoxMenuItem == null || str1 == null) {
/* 730 */       System.out.println("Unhandled item event");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 735 */     if (str1.equals("fd" + getLocalString(12567))) {
/* 736 */       String str = null;
/* 737 */       if (i == 2) {
/* 738 */         bool = this.virtdevsObj.do_floppy(str2);
/* 739 */         lockFdMenu(true, str2);
/*     */       }
/* 741 */       else if (i == 1) {
/*     */         
/* 743 */         this.dispFrame.setVisible(false);
/* 744 */         VFileDialog vFileDialog = new VFileDialog(getLocalString(8261), "*.img");
/* 745 */         str = vFileDialog.getString();
/* 746 */         this.dispFrame.setVisible(true);
/*     */         
/* 748 */         if (str != null) {
/* 749 */           if (this.virtdevsObj.fdThread != null)
/* 750 */             this.virtdevsObj.change_disk(this.virtdevsObj.fdConnection, str); 
/* 751 */           System.out.println("Image file: " + str);
/* 752 */           bool = this.virtdevsObj.do_floppy(str);
/* 753 */           if (!bool) {
/*     */             
/* 755 */             lockFdMenu(true, str2);
/*     */           } else {
/* 757 */             lockFdMenu(false, str2);
/*     */           } 
/*     */         } else {
/* 760 */           lockFdMenu(true, str2);
/*     */         } 
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 767 */     if (str1.equals("cd" + getLocalString(12567))) {
/* 768 */       String str = null;
/* 769 */       if (i == 2) {
/* 770 */         bool = this.virtdevsObj.do_cdrom(str2);
/* 771 */         lockCdMenu(true, str2);
/*     */       }
/* 773 */       else if (i == 1) {
/*     */         
/* 775 */         this.dispFrame.setVisible(false);
/* 776 */         VFileDialog vFileDialog = new VFileDialog(getLocalString(8261), "*.iso");
/* 777 */         str = vFileDialog.getString();
/* 778 */         this.dispFrame.setVisible(true);
/*     */         
/* 780 */         if (str != null) {
/* 781 */           if (this.virtdevsObj.cdThread != null)
/* 782 */             this.virtdevsObj.change_disk(this.virtdevsObj.cdConnection, str); 
/* 783 */           System.out.println("Image file: " + str);
/* 784 */           bool = this.virtdevsObj.do_cdrom(str);
/* 785 */           if (!bool) {
/*     */             
/* 787 */             lockCdMenu(true, str2);
/*     */           } else {
/* 789 */             lockCdMenu(false, str2);
/*     */           } 
/*     */         } else {
/* 792 */           lockCdMenu(true, str2);
/*     */         } 
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 799 */     if (str1.startsWith("cd")) {
/* 800 */       bool = this.virtdevsObj.do_cdrom(str2);
/* 801 */       if (bool)
/*     */       {
/* 803 */         lockCdMenu((i != 1), str2);
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 809 */     if (str1.startsWith("fd")) {
/* 810 */       bool = this.virtdevsObj.do_floppy(str2);
/* 811 */       if (bool)
/*     */       {
/* 813 */         lockFdMenu((i != 1), str2);
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 819 */     if (str1.equals("FLOPPY") || str1.equals("CDROM")) {
/*     */ 
/*     */       
/* 822 */       String str = "";
/* 823 */       boolean bool1 = false;
/*     */       
/* 825 */       if (i == 2) {
/* 826 */         String str3 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + str1 + "\", \"command\":\"EJECT\", \"session_key\":\"" + getParameter("RCINFO1") + "\"}";
/* 827 */         str = this.jsonObj.postJSONRequest("vm_status", str3);
/* 828 */         this.remconsObj.session.set_status(3, "Unmounted URL");
/*     */       }
/* 830 */       else if (i == 1) {
/* 831 */         this.remconsObj.setDialogIsOpen(true);
/* 832 */         URLDialog uRLDialog = new URLDialog(this.remconsObj);
/* 833 */         String str3 = uRLDialog.getUserInput();
/*     */         
/* 835 */         if (str3.compareTo("userhitcancel") == 0 || str3.compareTo("userhitclose") == 0) {
/* 836 */           str3 = null;
/*     */         }
/*     */         
/* 839 */         if (str3 != null) {
/* 840 */           str3 = str3.replaceAll("[\000-\037]", "");
/* 841 */           System.out.println("url:  " + str3);
/*     */         } 
/*     */         
/* 844 */         this.remconsObj.setDialogIsOpen(false);
/* 845 */         if (str3 != null) {
/* 846 */           String str4 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + str1 + "\", \"command\":\"INSERT\", \"url\":\"" + str3 + "\", \"session_key\":\"" + getParameter("RCINFO1") + "\"}";
/*     */           
/* 848 */           str = this.jsonObj.postJSONRequest("vm_status", str4);
/* 849 */           if (str == "Success") {
/* 850 */             str4 = "{\"method\":\"set_virtual_media_options\", \"device\":\"" + str1 + "\", \"boot_option\":\"CONNECT\", \"command\":\"SET\", \"url\":\"" + str3 + "\", \"session_key\":\"" + getParameter("RCINFO1") + "\"}";
/*     */             
/* 852 */             str = this.jsonObj.postJSONRequest("vm_status", str4);
/*     */           } 
/*     */           
/* 855 */           if (str == "SCSI_ERR_NO_LICENSE") {
/* 856 */             String str5 = "<html>" + getLocalString(8213) + " " + getLocalString(8214) + " " + getLocalString(8237) + "<br><br>" + getLocalString(8238) + "</html>";
/*     */             
/* 858 */             new VErrorDialog(this.dispFrame, getLocalString(8236), str5, true);
/*     */           }
/* 860 */           else if (str != "Success") {
/* 861 */             new VErrorDialog(this.dispFrame, getLocalString(8212), getLocalString(8292), true);
/*     */           } else {
/* 863 */             bool1 = true;
/* 864 */             this.remconsObj.session.set_status(3, getLocalString(12581));
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 869 */       if (str1.equals("FLOPPY")) {
/* 870 */         lockFdMenu(!bool1, str2);
/*     */       }
/* 872 */       else if (str1.equals("CDROM")) {
/*     */         
/* 874 */         lockCdMenu(!bool1, str2);
/*     */       } 
/*     */       return;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void kbdLayoutMenuHandler(int paramInt) {
/* 886 */     for (byte b = 0; b < this.REMCONS_MAX_KBD_LAYOUT; b++) {
/* 887 */       if (b != paramInt) {
/* 888 */         this.localKbdLayout[b].setSelected(false);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 893 */     this.remconsObj.setLocalKbdLayout(paramInt);
/*     */   } class WindowCloser extends WindowAdapter { private final intgapp this$0;
/*     */     WindowCloser(intgapp this$0) {
/* 896 */       this.this$0 = this$0;
/*     */     }
/*     */ 
/*     */     
/*     */     public void windowClosing(WindowEvent param1WindowEvent) {
/* 901 */       this.this$0.stop();
/* 902 */       this.this$0.exit = true;
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ApplyRcInfoParameters(String paramString) {
/* 910 */     this.enc_key = this.rc_port = this.vm_key = this.vm_port = null;
/* 911 */     Arrays.fill(this.enc_key_val, (byte)0);
/*     */ 
/*     */     
/* 914 */     paramString = paramString.trim();
/* 915 */     paramString = paramString.substring(1, paramString.length() - 1);
/* 916 */     String[] arrayOfString = paramString.split(",");
/*     */     
/* 918 */     for (byte b = 0; b < arrayOfString.length; b++) {
/* 919 */       String[] arrayOfString1 = arrayOfString[b].split(":");
/* 920 */       if (arrayOfString1.length != 2) {
/* 921 */         System.out.println("Error in ApplyRcInfoParameters");
/*     */         
/*     */         return;
/*     */       } 
/* 925 */       String str1 = arrayOfString1[0].trim();
/* 926 */       str1 = str1.substring(1, str1.length() - 1);
/*     */       
/* 928 */       String str2 = arrayOfString1[1].trim();
/* 929 */       if (str2.charAt(0) == '"') {
/* 930 */         str2 = str2.substring(1, str2.length() - 1);
/*     */       }
/*     */       
/* 933 */       if (str1.compareToIgnoreCase("enc_key") == 0) {
/*     */ 
/*     */         
/* 936 */         this.enc_key = str2;
/* 937 */         for (byte b1 = 0; b1 < this.enc_key_val.length; b1++) {
/* 938 */           String str = this.enc_key.substring(b1 * 2, b1 * 2 + 2);
/*     */           try {
/* 940 */             this.enc_key_val[b1] = (byte)Integer.parseInt(str, 16);
/*     */           }
/*     */           catch (NumberFormatException numberFormatException) {
/*     */             
/* 944 */             System.out.println("Failed to Parse enc_key");
/*     */           }
/*     */         
/*     */         }
/*     */       
/* 949 */       } else if (str1.compareToIgnoreCase("rc_port") == 0) {
/* 950 */         System.out.println("rc_port:" + str2);
/* 951 */         this.rc_port = str2;
/*     */       }
/* 953 */       else if (str1.compareToIgnoreCase("vm_key") == 0) {
/*     */         
/* 955 */         this.vm_key = str2;
/*     */       }
/* 957 */       else if (str1.compareToIgnoreCase("vm_port") == 0) {
/* 958 */         System.out.println("vm_port:" + str2);
/* 959 */         this.vm_port = str2;
/*     */       }
/* 961 */       else if (str1.equalsIgnoreCase("optional_features")) {
/* 962 */         System.out.println("optional_features:" + str2);
/* 963 */         this.optional_features = str2;
/*     */       }
/* 965 */       else if (str1.compareToIgnoreCase("server_name") == 0) {
/* 966 */         System.out.println("server_name:" + str2);
/* 967 */         this.server_name = str2;
/*     */       }
/* 969 */       else if (str1.compareToIgnoreCase("ilo_fqdn") == 0) {
/* 970 */         System.out.println("ilo_fqdn:" + str2);
/* 971 */         this.ilo_fqdn = str2;
/*     */       }
/* 973 */       else if (str1.compareToIgnoreCase("blade") == 0) {
/* 974 */         this.blade = Integer.parseInt(str2);
/* 975 */         System.out.println("blade:" + this.blade);
/*     */       }
/* 977 */       else if (this.blade == 1 && str1.compareToIgnoreCase("enclosure") == 0) {
/* 978 */         if (!str2.equals("null")) {
/* 979 */           this.in_enclosure = true;
/* 980 */           System.out.println("enclosure:" + str2);
/* 981 */           this.enclosure = str2;
/*     */         }
/*     */       
/* 984 */       } else if (this.blade == 1 && str1.compareToIgnoreCase("bay") == 0) {
/* 985 */         this.bay = Integer.parseInt(str2);
/* 986 */         System.out.println("bay:" + this.bay);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void moveUItoInit(boolean paramBoolean) {
/* 993 */     System.out.println("Disable Menus\n");
/* 994 */     this.psMenu.setEnabled(paramBoolean);
/* 995 */     this.vdMenu.setEnabled(paramBoolean);
/* 996 */     this.kbMenu.setEnabled(paramBoolean);
/*     */   }
/*     */ }


/* Location:              C:\Users\MARTIN\Downloads\intgapp3_231.jar!\com\hp\ilo2\intgapp\intgapp.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */