/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class cmd
/*     */   implements Runnable
/*     */ {
/*     */   protected Thread receiver;
/*     */   protected Socket s;
/*     */   protected DataInputStream in;
/*     */   protected DataOutputStream out;
/*  42 */   protected String login = "";
/*     */ 
/*     */   
/*  45 */   protected String host = "";
/*     */   
/*     */   public static final int TELNET_PORT = 23;
/*     */   
/*  49 */   protected int port = 23;
/*     */ 
/*     */   
/*  52 */   protected int connected = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   remcons cmdHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void transmit(String paramString) {
/*  65 */     System.out.println("in cmd::transmit");
/*  66 */     if (this.out == null) {
/*     */       return;
/*     */     }
/*  69 */     if (paramString.length() != 0) {
/*  70 */       byte[] arrayOfByte = new byte[paramString.length()];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  79 */       for (byte b = 0; b < paramString.length(); b++) {
/*  80 */         arrayOfByte[b] = (byte)paramString.charAt(b);
/*     */       }
/*     */       
/*     */       try {
/*  84 */         this.out.write(arrayOfByte, 0, arrayOfByte.length);
/*     */       } catch (IOException iOException) {
/*     */         
/*  87 */         System.out.println("telnet.transmit() IOException: " + iOException);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void transmitb(byte[] paramArrayOfbyte, int paramInt) {
/*     */     try {
/*  97 */       this.out.write(paramArrayOfbyte, 0, paramInt);
/*     */     } catch (IOException iOException) {
/*     */       
/* 100 */       System.out.println("cmd.transmitb() IOException: " + iOException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendBool(boolean paramBoolean) {
/* 106 */     byte[] arrayOfByte = new byte[4];
/* 107 */     if (paramBoolean == true) {
/* 108 */       arrayOfByte[0] = 4;
/*     */     } else {
/*     */       
/* 111 */       arrayOfByte[0] = 3;
/*     */     } 
/* 113 */     arrayOfByte[1] = 0;
/* 114 */     arrayOfByte[2] = 0;
/* 115 */     arrayOfByte[3] = 0;
/* 116 */     transmitb(arrayOfByte, arrayOfByte.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 126 */     byte[] arrayOfByte1 = new byte[12];
/* 127 */     byte[] arrayOfByte2 = new byte[1];
/* 128 */     byte[] arrayOfByte3 = new byte[4];
/* 129 */     byte[] arrayOfByte4 = new byte[128];
/*     */ 
/*     */ 
/*     */     
/* 133 */     int i = 0;
/*     */     
/* 135 */     short s2 = 0;
/* 136 */     short s1 = 0;
/*     */     
/*     */     try {
/*     */       while (true) {
/*     */         String str1, str2, str3, str4, str5;
/* 141 */         byte b = 0; int j = b;
/* 142 */         while (b < 12) {
/* 143 */           j = this.in.read(arrayOfByte2, 0, 1);
/* 144 */           if (j == 1) {
/* 145 */             arrayOfByte1[b++] = arrayOfByte2[0];
/*     */           }
/*     */         } 
/* 148 */         byte b1 = arrayOfByte1[0];
/* 149 */         byte b2 = arrayOfByte1[4];
/* 150 */         s1 = (short)arrayOfByte1[8];
/* 151 */         s2 = (short)arrayOfByte1[10];
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
/* 163 */         switch (b1) {
/*     */           case 2:
/* 165 */             System.out.println("Received Post complete notification\n");
/* 166 */             this.cmdHandler.session.post_complete = true;
/* 167 */             this.cmdHandler.session.set_status(4, "");
/*     */             break;
/*     */           
/*     */           case 3:
/* 171 */             if (b2 != 1)
/* 172 */               System.out.println("Invalid size for cmd: " + b1 + " size:" + b2); 
/* 173 */             this.in.read(arrayOfByte2, 0, 1);
/* 174 */             this.cmdHandler.setPwrStatusPower(arrayOfByte2[0]);
/*     */             break;
/*     */           case 4:
/* 177 */             if (b2 != 1)
/* 178 */               System.out.println("Invalid size for cmd: " + b1 + " size:" + b2); 
/* 179 */             this.in.read(arrayOfByte2, 0, 1);
/* 180 */             this.cmdHandler.setPwrStatusHealth(arrayOfByte2[0]);
/*     */             break;
/*     */           case 5:
/* 183 */             if (!this.cmdHandler.session.post_complete) {
/* 184 */               StringBuffer stringBuffer = new StringBuffer(16);
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 189 */               j = this.in.read(arrayOfByte4, 0, 2);
/* 190 */               String str7 = Integer.toHexString(0xFF & arrayOfByte4[1]).toUpperCase();
/* 191 */               String str8 = Integer.toHexString(0xFF & arrayOfByte4[0]).toUpperCase();
/* 192 */               String str6 = stringBuffer.append(this.cmdHandler.getLocalString(12582)).append(str7).append(str8).toString();
/*     */               
/* 194 */               this.cmdHandler.session.set_status(4, str6);
/*     */             } 
/*     */             break;
/*     */           case 6:
/* 198 */             System.out.println("Seized command notification\n");
/*     */ 
/*     */             
/* 201 */             j = this.in.read(arrayOfByte4, 0, 128);
/* 202 */             str1 = "UNKNOWN";
/* 203 */             str2 = "UNKNOWN";
/* 204 */             System.out.println("Data rcvd for acquire " + arrayOfByte4 + "rd count " + j);
/* 205 */             if (j > 0) {
/* 206 */               String str = new String(arrayOfByte4);
/* 207 */               System.out.println("Pakcet " + str);
/* 208 */               str1 = str.substring(0, 63).trim();
/* 209 */               str2 = str.substring(64, 127).trim();
/* 210 */               if (str1.length() <= 0) {
/* 211 */                 str1 = "UNKNOWN";
/*     */               }
/* 213 */               if (str2.length() <= 0) {
/* 214 */                 str2 = "UNKNOWN";
/*     */               }
/*     */             } else {
/*     */               
/* 218 */               System.out.println("Invalid acquire info");
/*     */             } 
/* 220 */             i = this.cmdHandler.seize_dialog(str1, str2, s2);
/* 221 */             if (i == 0) {
/* 222 */               sendBool(true);
/* 223 */               this.cmdHandler.seize_confirmed();
/*     */               break;
/*     */             } 
/* 226 */             sendBool(false);
/*     */             break;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           case 7:
/* 237 */             this.in.read(arrayOfByte3, 0, 4);
/* 238 */             this.cmdHandler.ack(arrayOfByte3[0], arrayOfByte3[1], arrayOfByte3[2], arrayOfByte3[3]);
/*     */             break;
/*     */           
/*     */           case 8:
/* 242 */             System.out.println("Playback not supported now.\n");
/*     */             break;
/*     */           
/*     */           case 9:
/* 246 */             System.out.println("Share command notification\n");
/*     */ 
/*     */             
/* 249 */             j = this.in.read(arrayOfByte4, 0, 128);
/* 250 */             str3 = "UNKNOWN";
/* 251 */             str4 = "UNKNOWN";
/*     */ 
/*     */             
/* 254 */             if (j > 0) {
/* 255 */               String str = new String(arrayOfByte4);
/* 256 */               System.out.println("Pakcet " + str);
/* 257 */               str3 = str.substring(0, 63).trim();
/* 258 */               str4 = str.substring(64, 127).trim();
/* 259 */               if (str3.length() <= 0) {
/* 260 */                 str3 = "UNKNOWN";
/*     */               }
/* 262 */               if (str4.length() <= 0) {
/* 263 */                 str4 = "UNKNOWN";
/*     */               }
/*     */             } else {
/*     */               
/* 267 */               System.out.println("Invalid acquire info");
/*     */             } 
/* 269 */             sendBool(false);
/*     */             
/* 271 */             this.cmdHandler.shared(str4, str3);
/*     */             break;
/*     */           
/*     */           case 10:
/* 275 */             System.out.println("Firmware upgrade in progress notification\n");
/* 276 */             this.cmdHandler.firmwareUpgrade();
/*     */             break;
/*     */           case 11:
/* 279 */             System.out.println("Un authorized action performed\n");
/* 280 */             str5 = "";
/* 281 */             switch (s2) {
/*     */               case 2:
/* 283 */                 str5 = " for remote console";
/*     */                 break;
/*     */               case 3:
/* 286 */                 str5 = " for virtual media";
/*     */                 break;
/*     */               
/*     */               case 4:
/* 290 */                 str5 = " for virtual power switch operations";
/*     */                 break;
/*     */               default:
/* 293 */                 str5 = "{0x" + s2 + "}";
/*     */                 break;
/*     */             } 
/* 296 */             this.cmdHandler.unAuthorized(str5);
/*     */             break;
/*     */           
/*     */           case 13:
/* 300 */             this.in.read(arrayOfByte2, 0, 1);
/* 301 */             System.out.println("VM notification from firmware\n");
/*     */             break;
/*     */ 
/*     */           
/*     */           case 14:
/* 306 */             System.out.println("Unlicensed notification from firmware\n");
/* 307 */             this.cmdHandler.UnlicensedShutdown();
/*     */             break;
/*     */           case 15:
/* 310 */             System.out.println("Reset notification from firmware\n");
/* 311 */             this.cmdHandler.resetShutdown();
/*     */             break;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 319 */         b1 = 0;
/*     */       } 
/*     */     } catch (Exception exception) {
/*     */       
/* 323 */       System.out.println("CMD exception: " + exception.toString());
/*     */       return;
/*     */     } 
/*     */   }
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
/*     */   public boolean connectCmd(remcons paramremcons, String paramString, int paramInt) {
/*     */     try {
/* 340 */       this.cmdHandler = paramremcons;
/*     */ 
/*     */ 
/*     */       
/* 344 */       byte[] arrayOfByte1 = new byte[32];
/* 345 */       byte[] arrayOfByte2 = new byte[2];
/*     */ 
/*     */       
/* 348 */       this.s = new Socket(paramString, paramInt);
/*     */       try {
/* 350 */         this.s.setSoLinger(true, 0);
/*     */       } catch (SocketException socketException) {
/*     */         
/* 353 */         System.out.println("connectCmd linger SocketException: " + socketException);
/*     */       } 
/*     */       
/* 356 */       this.in = new DataInputStream(this.s.getInputStream());
/* 357 */       this.out = new DataOutputStream(this.s.getOutputStream());
/*     */       
/* 359 */       byte b = this.in.readByte();
/* 360 */       if (b == 80) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 365 */         arrayOfByte2[0] = 2;
/* 366 */         arrayOfByte2[1] = 32;
/* 367 */         arrayOfByte1 = paramremcons.ParentApp.getParameter("RCINFO1").getBytes();
/*     */ 
/*     */         
/* 370 */         if (paramremcons.ParentApp.optional_features.indexOf("ENCRYPT_KEY") != -1) {
/*     */           
/* 372 */           for (byte b1 = 0; b1 < arrayOfByte1.length; b1++)
/*     */           {
/* 374 */             arrayOfByte1[b1] = (byte)(arrayOfByte1[b1] ^ (byte)paramremcons.ParentApp.enc_key.charAt(b1 % paramremcons.ParentApp.enc_key.length()));
/*     */           }
/* 376 */           if (paramremcons.ParentApp.optional_features.indexOf("ENCRYPT_VMKEY") != -1) {
/*     */             
/* 378 */             arrayOfByte2[1] = (byte)(arrayOfByte2[1] | 0x40);
/*     */           }
/*     */           else {
/*     */             
/* 382 */             arrayOfByte2[1] = (byte)(arrayOfByte2[1] | 0x80);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 387 */         byte[] arrayOfByte = new byte[arrayOfByte2.length + arrayOfByte1.length];
/* 388 */         System.arraycopy(arrayOfByte2, 0, arrayOfByte, 0, arrayOfByte2.length);
/* 389 */         System.arraycopy(arrayOfByte1, 0, arrayOfByte, arrayOfByte2.length, arrayOfByte1.length);
/*     */ 
/*     */         
/* 392 */         String str = new String(arrayOfByte);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 401 */         transmit(str);
/* 402 */         b = this.in.readByte();
/* 403 */         if (b == 82) {
/*     */           
/* 405 */           this.receiver = new Thread(this);
/* 406 */           this.receiver.setName("cmd_rcvr");
/* 407 */           this.receiver.start();
/*     */         }
/*     */         else {
/*     */           
/* 411 */           System.out.println("login failed. read data" + b);
/*     */         } 
/*     */       } else {
/*     */         
/* 415 */         System.out.println("Socket connection failure... ");
/*     */       }
/*     */     
/*     */     } catch (SocketException socketException) {
/*     */       
/* 420 */       System.out.println("telnet.connect() SocketException: " + socketException);
/* 421 */       this.s = null;
/* 422 */       this.in = null;
/* 423 */       this.out = null;
/* 424 */       this.receiver = null;
/* 425 */       this.connected = 0;
/*     */     } catch (UnknownHostException unknownHostException) {
/*     */       
/* 428 */       System.out.println("telnet.connect() UnknownHostException: " + unknownHostException);
/* 429 */       this.s = null;
/* 430 */       this.in = null;
/* 431 */       this.out = null;
/* 432 */       this.receiver = null;
/* 433 */       this.connected = 0;
/*     */     } catch (IOException iOException) {
/*     */       
/* 436 */       System.out.println("telnet.connect() IOException: " + iOException);
/* 437 */       this.s = null;
/* 438 */       this.in = null;
/* 439 */       this.out = null;
/* 440 */       this.receiver = null;
/* 441 */       this.connected = 0;
/*     */     } 
/*     */     
/* 444 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void disconnectCmd() {
/* 452 */     if (this.receiver != null && this.receiver.isAlive()) {
/* 453 */       this.receiver.stop();
/*     */     }
/* 455 */     this.receiver = null;
/*     */     
/* 457 */     if (this.s != null) {
/*     */       try {
/* 459 */         System.out.println("Closing socket");
/* 460 */         this.s.close();
/*     */       } catch (IOException iOException) {
/*     */         
/* 463 */         System.out.println("telnet.disconnect() IOException: " + iOException);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 468 */     if (this.cmdHandler != null) {
/* 469 */       this.cmdHandler.setPwrStatusHealth(3);
/* 470 */       this.cmdHandler.setPwrStatusPower(0);
/* 471 */       this.cmdHandler = null;
/*     */     } 
/* 473 */     this.s = null;
/* 474 */     this.in = null;
/* 475 */     this.out = null;
/*     */   }
/*     */ }


/* Location:              C:\Users\MARTIN\Downloads\intgapp3_231.jar!\com\hp\ilo2\remcons\cmd.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */