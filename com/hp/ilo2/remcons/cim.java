/*      */ package com.hp.ilo2.remcons;
/*      */ 
/*      */ import com.hp.ilo2.virtdevs.VErrorDialog;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Image;
/*      */ import java.awt.Point;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.awt.image.MemoryImageSource;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Method;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class cim
/*      */   extends telnet
/*      */   implements MouseSyncListener
/*      */ {
/*      */   private static final int CMD_MOUSE_MOVE = 208;
/*      */   private static final int CMD_BUTTON_PRESS = 209;
/*      */   private static final int CMD_BUTTON_RELEASE = 210;
/*      */   private static final int CMD_BUTTON_CLICK = 211;
/*      */   private static final int CMD_BYTE = 212;
/*      */   private static final int CMD_SET_MODE = 213;
/*      */   private static final char MOUSE_USBABS = '\001';
/*      */   private static final char MOUSE_USBREL = '\002';
/*      */   static final int CMD_ENCRYPT = 192;
/*      */   public static final int MOUSE_BUTTON_LEFT = 4;
/*      */   public static final int MOUSE_BUTTON_CENTER = 2;
/*      */   public static final int MOUSE_BUTTON_RIGHT = 1;
/*   55 */   private char prev_char = ' ';
/*      */ 
/*      */   
/*      */   private boolean disable_kbd = false;
/*      */ 
/*      */   
/*      */   private boolean altlock = false;
/*      */ 
/*      */   
/*      */   private static final int block_width = 16;
/*      */ 
/*      */   
/*      */   private static final int block_height = 16;
/*      */ 
/*      */   
/*   70 */   public int[] color_remap_table = new int[32768];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   76 */   private int scale_x = 1;
/*   77 */   private int scale_y = 1;
/*      */ 
/*      */   
/*   80 */   private int screen_x = 1;
/*   81 */   private int screen_y = 1;
/*   82 */   private int mouse_protocol = 0;
/*      */   
/*   84 */   protected MouseSync mouse_sync = new MouseSync(this);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean UI_dirty = false;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean sending_encrypt_command = false;
/*      */ 
/*      */ 
/*      */   
/*   97 */   public byte[] encrypt_key = new byte[16];
/*      */   private RC4 RC4encrypter;
/*      */   private Aes Aes128encrypter;
/*      */   private Aes Aes256encrypter;
/*  101 */   private int key_index = 0;
/*      */ 
/*      */   
/*  104 */   private int bitsPerColor = 5;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  109 */   public Point mousePrevPosn = new Point(0, 0);
/*      */   
/*  111 */   private byte mouseBtnState = 0;
/*      */   
/*      */   private static final int RESET = 0;
/*      */   
/*      */   private static final int START = 1;
/*      */   
/*      */   private static final int PIXELS = 2;
/*      */   
/*      */   private static final int PIXLRU1 = 3;
/*      */   
/*      */   private static final int PIXLRU0 = 4;
/*      */   
/*      */   private static final int PIXCODE1 = 5;
/*      */   
/*      */   private static final int PIXCODE2 = 6;
/*      */   private static final int PIXCODE3 = 7;
/*      */   private static final int PIXGREY = 8;
/*      */   private static final int PIXRGBR = 9;
/*      */   private static final int PIXRPT = 10;
/*      */   private static final int PIXRPT1 = 11;
/*      */   private static final int PIXRPTSTD1 = 12;
/*      */   private static final int PIXRPTSTD2 = 13;
/*      */   private static final int PIXRPTNSTD = 14;
/*      */   private static final int CMD = 15;
/*      */   private static final int CMD0 = 16;
/*      */   private static final int MOVEXY0 = 17;
/*      */   private static final int EXTCMD = 18;
/*      */   private static final int CMDX = 19;
/*      */   private static final int MOVESHORTX = 20;
/*      */   private static final int MOVELONGX = 21;
/*      */   private static final int BLKRPT = 22;
/*      */   private static final int EXTCMD1 = 23;
/*      */   private static final int FIRMWARE = 24;
/*      */   private static final int EXTCMD2 = 25;
/*      */   private static final int MODE0 = 26;
/*      */   private static final int TIMEOUT = 27;
/*      */   private static final int BLKRPT1 = 28;
/*      */   private static final int BLKRPTSTD = 29;
/*      */   private static final int BLKRPTNSTD = 30;
/*      */   private static final int PIXFAN = 31;
/*      */   private static final int PIXCODE4 = 32;
/*      */   private static final int PIXDUP = 33;
/*      */   private static final int BLKDUP = 34;
/*      */   private static final int PIXCODE = 35;
/*      */   private static final int PIXSPEC = 36;
/*      */   private static final int EXIT = 37;
/*      */   private static final int LATCHED = 38;
/*      */   private static final int MOVEXY1 = 39;
/*      */   private static final int MODE1 = 40;
/*      */   private static final int PIXRGBG = 41;
/*      */   private static final int PIXRGBB = 42;
/*      */   private static final int HUNT = 43;
/*      */   private static final int PRINT0 = 44;
/*      */   private static final int PRINT1 = 45;
/*      */   private static final int CORP = 46;
/*      */   private static final int MODE2 = 47;
/*      */   private static final int SIZE_OF_ALL = 48;
/*  168 */   private static int[] bits_to_read = new int[] { 0, 1, 1, 1, 1, 1, 2, 3, 5, 5, 1, 1, 3, 3, 8, 1, 1, 7, 1, 1, 3, 7, 1, 1, 8, 1, 7, 0, 1, 3, 7, 1, 4, 0, 0, 0, 1, 0, 1, 7, 7, 5, 5, 1, 8, 8, 1, 4 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  178 */   private static int[] next_0 = new int[] { 1, 2, 31, 2, 2, 10, 10, 10, 10, 41, 2, 33, 2, 2, 2, 16, 19, 39, 22, 20, 1, 1, 34, 25, 46, 26, 40, 1, 29, 1, 1, 36, 10, 2, 1, 35, 8, 37, 38, 1, 47, 42, 10, 43, 45, 45, 1, 1 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  188 */   private static int[] next_1 = new int[] { 1, 15, 3, 11, 11, 10, 10, 10, 10, 41, 11, 12, 2, 2, 2, 17, 18, 39, 23, 21, 1, 1, 28, 24, 46, 27, 40, 1, 30, 1, 1, 35, 10, 2, 1, 35, 9, 37, 38, 1, 47, 42, 10, 0, 45, 45, 24, 1 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  199 */   private static int dvc_cc_active = 0;
/*  200 */   private static int[] dvc_cc_color = new int[17];
/*  201 */   private static int[] dvc_cc_usage = new int[17];
/*  202 */   private static int[] dvc_cc_block = new int[17];
/*      */ 
/*      */   
/*  205 */   private static int[] dvc_lru_lengths = new int[] { 0, 0, 0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4 };
/*      */   
/*  207 */   private static int[] dvc_getmask = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255 };
/*  208 */   private static int[] dvc_reversal = new int[256];
/*  209 */   private static int[] dvc_left = new int[256];
/*  210 */   private static int[] dvc_right = new int[256];
/*      */   
/*      */   private static int dvc_pixel_count;
/*      */   
/*      */   private static int dvc_size_x;
/*      */   
/*      */   private static int dvc_size_y;
/*      */   private static int dvc_y_clipped;
/*      */   private static int dvc_lastx;
/*  219 */   private static int dvc_ib_acc = 0; private static int dvc_lasty; private static int dvc_newx; private static int dvc_newy; private static int dvc_color;
/*      */   private static int dvc_last_color;
/*  221 */   private static int dvc_ib_bcnt = 0;
/*      */   
/*  223 */   private static int dvc_zero_count = 0;
/*      */ 
/*      */   
/*  226 */   private static int dvc_decoder_state = 0;
/*      */   
/*  228 */   private static int dvc_next_state = 0;
/*      */   
/*  230 */   private static int dvc_pixcode = 38;
/*      */   
/*  232 */   private static int dvc_code = 0;
/*  233 */   private static int[] block = new int[256];
/*      */   private static int dvc_red;
/*      */   private static int dvc_green;
/*      */   private static int dvc_blue;
/*      */   private static int fatal_count;
/*  238 */   private static int printchan = 0;
/*  239 */   private static String printstring = "";
/*  240 */   private static long count_bytes = 0L;
/*  241 */   private static int[] cmd_p_buff = new int[256];
/*  242 */   private static int cmd_p_count = 0;
/*  243 */   private static int cmd_last = 0;
/*      */   
/*  245 */   private static int framerate = 30;
/*      */   
/*      */   private static boolean debug_msgs = false;
/*      */   
/*  249 */   private static char last_bits = Character.MIN_VALUE;
/*  250 */   private static char last_bits2 = Character.MIN_VALUE;
/*  251 */   private static char last_bits3 = Character.MIN_VALUE;
/*  252 */   private static char last_bits4 = Character.MIN_VALUE;
/*  253 */   private static char last_bits5 = Character.MIN_VALUE;
/*  254 */   private static char last_bits6 = Character.MIN_VALUE;
/*  255 */   private static char last_bits7 = Character.MIN_VALUE;
/*  256 */   private static int last_len = 0;
/*  257 */   private static int last_len1 = 0;
/*  258 */   private static int last_len2 = 0;
/*  259 */   private static int last_len3 = 0;
/*  260 */   private static int last_len4 = 0;
/*  261 */   private static int last_len5 = 0;
/*  262 */   private static int last_len6 = 0;
/*  263 */   private static int last_len7 = 0;
/*  264 */   private static int last_len8 = 0;
/*  265 */   private static int last_len9 = 0;
/*  266 */   private static int last_len10 = 0;
/*  267 */   private static int last_len11 = 0;
/*  268 */   private static int last_len12 = 0;
/*  269 */   private static int last_len13 = 0;
/*  270 */   private static int last_len14 = 0;
/*  271 */   private static int last_len15 = 0;
/*  272 */   private static int last_len16 = 0;
/*  273 */   private static int last_len17 = 0;
/*  274 */   private static int last_len18 = 0;
/*  275 */   private static int last_len19 = 0;
/*  276 */   private static int last_len20 = 0;
/*  277 */   private static int last_len21 = 0;
/*  278 */   private static char dvc_new_bits = Character.MIN_VALUE;
/*  279 */   private static int debug_lastx = 0;
/*  280 */   private static int debug_lasty = 0;
/*  281 */   private static int debug_show_block = 0;
/*  282 */   private static long timeout_count = 0L;
/*  283 */   private static long dvc_counter_block = 0L;
/*  284 */   private static long dvc_counter_bits = 0L;
/*      */   private static boolean show_bitsblk_count = false;
/*  286 */   private static long show_slices = 0L;
/*      */ 
/*      */   
/*      */   private static boolean dvc_process_inhibit = false;
/*      */   
/*      */   private static boolean video_detected = true;
/*      */   
/*      */   private boolean ignore_next_key = false;
/*      */   
/*  295 */   private int blockHeight = 16;
/*  296 */   private int blockWidth = 16; private boolean unsupportedVideoModeWarned = false;
/*      */   private static final int B = -16777216;
/*      */   private static final int W = -8355712;
/*      */   
/*      */   public String getLocalString(int paramInt) {
/*  301 */     String str = "";
/*      */     try {
/*  303 */       str = this.remconsObj.ParentApp.locinfoObj.getLocString(paramInt);
/*      */     } catch (Exception exception) {
/*      */       
/*  306 */       System.out.println("cim:getLocalString" + exception.getMessage());
/*      */     } 
/*  308 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public cim(remcons paramremcons) {
/*  316 */     super(paramremcons);
/*  317 */     dvc_reversal[255] = 0;
/*  318 */     this.current_cursor = Cursor.getDefaultCursor();
/*  319 */     this.screen.addMouseListener(this.mouse_sync);
/*  320 */     this.screen.addMouseMotionListener(this.mouse_sync);
/*  321 */     this.screen.addMouseWheelListener(this.mouse_sync);
/*  322 */     this.mouse_sync.setListener(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setup_encryption(byte[] paramArrayOfbyte, int paramInt) {
/*  331 */     System.arraycopy(paramArrayOfbyte, 0, this.encrypt_key, 0, 16);
/*      */     
/*  333 */     this.RC4encrypter = new RC4(paramArrayOfbyte);
/*  334 */     this.Aes128encrypter = new Aes(0, paramArrayOfbyte);
/*  335 */     this.Aes256encrypter = new Aes(0, paramArrayOfbyte);
/*  336 */     this.key_index = paramInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void reinit_vars() {
/*  344 */     super.reinit_vars();
/*      */     
/*  346 */     dvc_code = 0;
/*  347 */     dvc_ib_acc = 0;
/*  348 */     dvc_ib_bcnt = 0;
/*  349 */     dvc_counter_bits = 0L;
/*      */     
/*  351 */     this.prev_char = ' ';
/*  352 */     this.disable_kbd = false;
/*  353 */     this.altlock = false;
/*      */     
/*  355 */     dvc_reversal[255] = 0;
/*      */     
/*  357 */     this.scale_x = 1;
/*  358 */     this.scale_y = 1;
/*      */     
/*  360 */     this.mouse_sync.restart();
/*      */     
/*  362 */     dvc_process_inhibit = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void enable_debug() {
/*  370 */     debug_msgs = true;
/*  371 */     super.enable_debug();
/*  372 */     this.mouse_sync.enableDebug();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void disable_debug() {
/*  380 */     debug_msgs = false;
/*  381 */     super.disable_debug();
/*  382 */     this.mouse_sync.disableDebug();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sync_start() {
/*  390 */     this.mouse_sync.sync();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serverMove(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  399 */     if (paramInt1 < -128) {
/*  400 */       paramInt1 = -128;
/*      */     }
/*  402 */     else if (paramInt1 > 127) {
/*  403 */       paramInt1 = 127;
/*      */     } 
/*  405 */     if (paramInt2 < -128) {
/*  406 */       paramInt2 = -128;
/*      */     }
/*  408 */     else if (paramInt2 > 127) {
/*  409 */       paramInt2 = 127;
/*      */     } 
/*  411 */     this.UI_dirty = true;
/*      */     
/*  413 */     if (this.screen_x > 0 && this.screen_y > 0) {
/*  414 */       paramInt3 = 3000 * paramInt3 / this.screen_x;
/*  415 */       paramInt4 = 3000 * paramInt4 / this.screen_y;
/*      */     } else {
/*      */       
/*  418 */       paramInt3 = 3000 * paramInt3 / 1;
/*  419 */       paramInt4 = 3000 * paramInt4 / 1;
/*      */     } 
/*      */ 
/*      */     
/*  423 */     byte[] arrayOfByte = new byte[10];
/*      */     
/*  425 */     arrayOfByte[0] = 2;
/*  426 */     arrayOfByte[1] = 0;
/*  427 */     arrayOfByte[2] = (byte)(paramInt3 & 0xFF);
/*  428 */     arrayOfByte[3] = (byte)(paramInt3 >> 8);
/*  429 */     arrayOfByte[4] = (byte)(paramInt4 & 0xFF);
/*  430 */     arrayOfByte[5] = (byte)(paramInt4 >> 8);
/*  431 */     arrayOfByte[6] = 0;
/*  432 */     arrayOfByte[7] = 0;
/*      */ 
/*      */     
/*  435 */     arrayOfByte[8] = this.mouseBtnState;
/*  436 */     arrayOfByte[9] = 0;
/*      */     
/*  438 */     String str = new String(arrayOfByte);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  443 */     transmit(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void mouse_mode_change(boolean paramBoolean) {
/*  462 */     boolean bool = paramBoolean ? true : true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void mouseEntered(MouseEvent paramMouseEvent) {
/*  470 */     this.UI_dirty = true;
/*      */     
/*  472 */     super.mouseEntered(paramMouseEvent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serverPress(int paramInt) {
/*  480 */     this.UI_dirty = true;
/*  481 */     send_mouse_press(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serverRelease(int paramInt) {
/*  490 */     this.UI_dirty = true;
/*  491 */     send_mouse_release(paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void serverClick(int paramInt1, int paramInt2) {
/*  500 */     this.UI_dirty = true;
/*  501 */     send_mouse_click(paramInt1, paramInt2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  507 */     this.mouseBtnState = mouseButtonState(paramInt1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void mouseExited(MouseEvent paramMouseEvent) {
/*  515 */     super.mouseExited(paramMouseEvent);
/*  516 */     setCursor(Cursor.getDefaultCursor());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void disable_keyboard() {
/*  524 */     this.disable_kbd = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void enable_keyboard() {
/*  532 */     this.disable_kbd = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void disable_altlock() {
/*  566 */     this.altlock = false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void enable_altlock() {
/*  578 */     this.altlock = true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, remcons paramremcons) {
/*  596 */     char[] arrayOfChar = { 'ÿ', 'À' };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  608 */     super.connect(paramString1, paramString2, paramInt1, paramInt2, paramInt3, paramremcons);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void transmit(String paramString) {
/*  622 */     if (this.out == null || paramString == null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  627 */     if (paramString.length() != 0) {
/*  628 */       byte[] arrayOfByte = new byte[paramString.length()];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  637 */       for (byte b = 0; b < paramString.length(); b++) {
/*  638 */         arrayOfByte[b] = (byte)paramString.charAt(b);
/*      */ 
/*      */         
/*  641 */         if (this.dvc_encryption) {
/*  642 */           char c; switch (this.cipher) {
/*      */             case 1:
/*  644 */               c = (char)(this.RC4encrypter.randomValue() & 0xFF);
/*  645 */               arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */               break;
/*      */             case 2:
/*  648 */               c = (char)(this.Aes128encrypter.randomValue() & 0xFF);
/*  649 */               arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */               break;
/*      */             case 3:
/*  652 */               c = (char)(this.Aes256encrypter.randomValue() & 0xFF);
/*  653 */               arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */               break;
/*      */             default:
/*  656 */               c = Character.MIN_VALUE;
/*  657 */               System.out.println("Unknown encryption"); break;
/*      */           } 
/*  659 */           arrayOfByte[b] = (byte)(arrayOfByte[b] & 0xFF);
/*      */         } 
/*      */       } 
/*      */       try {
/*  663 */         this.out.write(arrayOfByte, 0, arrayOfByte.length);
/*      */       } catch (IOException iOException) {
/*      */         
/*  666 */         System.out.println("telnet.transmit() IOException: " + iOException);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void transmitb(byte[] paramArrayOfbyte, int paramInt) {
/*  678 */     byte[] arrayOfByte = new byte[paramInt];
/*  679 */     System.arraycopy(paramArrayOfbyte, 0, arrayOfByte, 0, paramInt);
/*      */     
/*  681 */     for (byte b = 0; b < paramInt; b++) {
/*      */ 
/*      */       
/*  684 */       if (this.dvc_encryption) {
/*  685 */         char c; switch (this.cipher) {
/*      */           case 1:
/*  687 */             c = (char)(this.RC4encrypter.randomValue() & 0xFF);
/*  688 */             arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */             break;
/*      */           case 2:
/*  691 */             c = (char)(this.Aes128encrypter.randomValue() & 0xFF);
/*  692 */             arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */             break;
/*      */           case 3:
/*  695 */             c = (char)(this.Aes256encrypter.randomValue() & 0xFF);
/*  696 */             arrayOfByte[b] = (byte)(arrayOfByte[b] ^ c);
/*      */             break;
/*      */           default:
/*  699 */             c = Character.MIN_VALUE;
/*  700 */             System.out.println("Unknown encryption"); break;
/*      */         } 
/*  702 */         arrayOfByte[b] = (byte)(arrayOfByte[b] & 0xFF);
/*      */       } 
/*      */     } 
/*      */     
/*      */     try {
/*  707 */       if (null != this.out) {
/*  708 */         this.out.write(arrayOfByte, 0, paramInt);
/*      */       }
/*      */     } catch (IOException iOException) {
/*      */       
/*  712 */       System.out.println("telnet.transmitb() IOException: " + iOException);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String translate_key(KeyEvent paramKeyEvent) {
/*  725 */     String str = "";
/*  726 */     char c = paramKeyEvent.getKeyChar();
/*  727 */     byte b = 0;
/*  728 */     boolean bool = true;
/*      */ 
/*      */ 
/*      */     
/*  732 */     if (this.disable_kbd) {
/*  733 */       return "";
/*      */     }
/*      */     
/*  736 */     if (this.ignore_next_key) {
/*  737 */       this.ignore_next_key = false;
/*  738 */       return "";
/*      */     } 
/*      */     
/*  741 */     this.UI_dirty = true;
/*  742 */     if (paramKeyEvent.isShiftDown()) {
/*  743 */       b = 1;
/*      */     }
/*  745 */     else if (paramKeyEvent.isControlDown()) {
/*  746 */       b = 2;
/*      */     }
/*  748 */     else if (this.altlock || paramKeyEvent.isAltDown()) {
/*  749 */       b = 3;
/*  750 */       if (paramKeyEvent.isAltDown()) {
/*  751 */         paramKeyEvent.consume();
/*      */       }
/*      */     } 
/*      */     
/*  755 */     switch (c) {
/*      */       
/*      */       case '\033':
/*  758 */         bool = false;
/*      */         break;
/*      */ 
/*      */       
/*      */       case '\n':
/*      */       case '\r':
/*  764 */         switch (b) {
/*      */           case 0:
/*  766 */             str = "\r";
/*      */             break;
/*      */           
/*      */           case 1:
/*  770 */             str = "\033[3\r";
/*      */             break;
/*      */           
/*      */           case 2:
/*  774 */             str = "\n";
/*      */             break;
/*      */           
/*      */           case 3:
/*  778 */             str = "\033[1\r";
/*      */             break;
/*      */         } 
/*  781 */         bool = false;
/*      */         break;
/*      */ 
/*      */       
/*      */       case '\b':
/*  786 */         switch (b) {
/*      */           case 0:
/*  788 */             str = "\b";
/*      */             break;
/*      */           
/*      */           case 1:
/*  792 */             str = "\033[3\b";
/*      */             break;
/*      */           
/*      */           case 2:
/*  796 */             str = "";
/*      */             break;
/*      */           
/*      */           case 3:
/*  800 */             str = "\033[1\b";
/*      */             break;
/*      */         } 
/*  803 */         bool = false;
/*      */         break;
/*      */       
/*      */       default:
/*  807 */         str = super.translate_key(paramKeyEvent);
/*      */         break;
/*      */     } 
/*      */     
/*  811 */     if (bool == true && str.length() != 0 && b == 3) {
/*  812 */       str = "\033[1" + str;
/*      */     }
/*  814 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String translate_special_key(KeyEvent paramKeyEvent) {
/*  828 */     String str = "";
/*  829 */     boolean bool = true;
/*  830 */     byte b = 0;
/*      */     
/*  832 */     if (this.disable_kbd) {
/*  833 */       return "";
/*      */     }
/*      */ 
/*      */     
/*  837 */     this.UI_dirty = true;
/*  838 */     if (paramKeyEvent.isShiftDown()) {
/*  839 */       b = 1;
/*      */     }
/*  841 */     else if (paramKeyEvent.isControlDown()) {
/*  842 */       b = 2;
/*      */     }
/*  844 */     else if (this.altlock || paramKeyEvent.isAltDown()) {
/*  845 */       b = 3;
/*      */     } 
/*      */     
/*  848 */     switch (paramKeyEvent.getKeyCode()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 27:
/*  880 */         str = "\033";
/*      */         break;
/*      */       
/*      */       case 9:
/*  884 */         paramKeyEvent.consume();
/*  885 */         str = "\t";
/*      */         break;
/*      */       
/*      */       case 127:
/*  889 */         if (paramKeyEvent.isControlDown() && (this.altlock || paramKeyEvent.isAltDown())) {
/*      */           
/*  891 */           send_ctrl_alt_del();
/*  892 */           return "";
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  897 */         if (System.getProperty("java.version", "0").compareTo("1.4.2") < 0) {
/*  898 */           str = "";
/*      */         }
/*      */         break;
/*      */       
/*      */       case 36:
/*  903 */         str = "\033[H";
/*      */         break;
/*      */       
/*      */       case 35:
/*  907 */         str = "\033[F";
/*      */         break;
/*      */       
/*      */       case 33:
/*  911 */         str = "\033[I";
/*      */         break;
/*      */       
/*      */       case 34:
/*  915 */         str = "\033[G";
/*      */         break;
/*      */       
/*      */       case 155:
/*  919 */         str = "\033[L";
/*      */         break;
/*      */       
/*      */       case 38:
/*  923 */         str = "\033[A";
/*      */         break;
/*      */       
/*      */       case 40:
/*  927 */         str = "\033[B";
/*      */         break;
/*      */       
/*      */       case 37:
/*  931 */         str = "\033[D";
/*      */         break;
/*      */       
/*      */       case 39:
/*  935 */         str = "\033[C";
/*      */         break;
/*      */       
/*      */       case 112:
/*  939 */         switch (b) {
/*      */           case 0:
/*  941 */             str = "\033[M";
/*      */             break;
/*      */           
/*      */           case 1:
/*  945 */             str = "\033[Y";
/*      */             break;
/*      */           
/*      */           case 2:
/*  949 */             str = "\033[k";
/*      */             break;
/*      */           
/*      */           case 3:
/*  953 */             str = "\033[w";
/*      */             break;
/*      */         } 
/*  956 */         paramKeyEvent.consume();
/*  957 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 113:
/*  961 */         switch (b) {
/*      */           case 0:
/*  963 */             str = "\033[N";
/*      */             break;
/*      */           
/*      */           case 1:
/*  967 */             str = "\033[Z";
/*      */             break;
/*      */           
/*      */           case 2:
/*  971 */             str = "\033[l";
/*      */             break;
/*      */           
/*      */           case 3:
/*  975 */             str = "\033[x";
/*      */             break;
/*      */         } 
/*  978 */         paramKeyEvent.consume();
/*  979 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 114:
/*  983 */         switch (b) {
/*      */           case 0:
/*  985 */             str = "\033[O";
/*      */             break;
/*      */           
/*      */           case 1:
/*  989 */             str = "\033[a";
/*      */             break;
/*      */           
/*      */           case 2:
/*  993 */             str = "\033[m";
/*      */             break;
/*      */           
/*      */           case 3:
/*  997 */             str = "\033[y";
/*      */             break;
/*      */         } 
/* 1000 */         paramKeyEvent.consume();
/* 1001 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 115:
/* 1005 */         switch (b) {
/*      */           case 0:
/* 1007 */             str = "\033[P";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1011 */             str = "\033[b";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1015 */             str = "\033[n";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1019 */             str = "\033[z";
/*      */             break;
/*      */         } 
/* 1022 */         paramKeyEvent.consume();
/* 1023 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 116:
/* 1027 */         switch (b) {
/*      */           case 0:
/* 1029 */             str = "\033[Q";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1033 */             str = "\033[c";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1037 */             str = "\033[o";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1041 */             str = "\033[@";
/*      */             break;
/*      */         } 
/* 1044 */         paramKeyEvent.consume();
/* 1045 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 117:
/* 1049 */         switch (b) {
/*      */           case 0:
/* 1051 */             str = "\033[R";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1055 */             str = "\033[d";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1059 */             str = "\033[p";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1063 */             str = "\033[[";
/*      */             break;
/*      */         } 
/* 1066 */         paramKeyEvent.consume();
/* 1067 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 118:
/* 1071 */         switch (b) {
/*      */           case 0:
/* 1073 */             str = "\033[S";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1077 */             str = "\033[e";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1081 */             str = "\033[q";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1085 */             str = "\033[\\";
/*      */             break;
/*      */         } 
/* 1088 */         paramKeyEvent.consume();
/* 1089 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 119:
/* 1093 */         switch (b) {
/*      */           case 0:
/* 1095 */             str = "\033[T";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1099 */             str = "\033[f";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1103 */             str = "\033[r";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1107 */             str = "\033[]";
/*      */             break;
/*      */         } 
/* 1110 */         paramKeyEvent.consume();
/* 1111 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 120:
/* 1115 */         switch (b) {
/*      */           case 0:
/* 1117 */             str = "\033[U";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1121 */             str = "\033[g";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1125 */             str = "\033[s";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1129 */             str = "\033[^";
/*      */             break;
/*      */         } 
/* 1132 */         paramKeyEvent.consume();
/* 1133 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 121:
/* 1137 */         switch (b) {
/*      */           case 0:
/* 1139 */             str = "\033[V";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1143 */             str = "\033[h";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1147 */             str = "\033[t";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1151 */             str = "\033[_";
/*      */             break;
/*      */         } 
/* 1154 */         paramKeyEvent.consume();
/* 1155 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 122:
/* 1159 */         switch (b) {
/*      */           case 0:
/* 1161 */             str = "\033[W";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1165 */             str = "\033[i";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1169 */             str = "\033[u";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1173 */             str = "\033[`";
/*      */             break;
/*      */         } 
/* 1176 */         paramKeyEvent.consume();
/* 1177 */         bool = false;
/*      */         break;
/*      */       
/*      */       case 123:
/* 1181 */         switch (b) {
/*      */           case 0:
/* 1183 */             str = "\033[X";
/*      */             break;
/*      */           
/*      */           case 1:
/* 1187 */             str = "\033[j";
/*      */             break;
/*      */           
/*      */           case 2:
/* 1191 */             str = "\033[v";
/*      */             break;
/*      */           
/*      */           case 3:
/* 1195 */             str = "\033['";
/*      */             break;
/*      */         } 
/* 1198 */         paramKeyEvent.consume();
/* 1199 */         bool = false;
/*      */         break;
/*      */       
/*      */       default:
/* 1203 */         bool = false;
/* 1204 */         str = super.translate_special_key(paramKeyEvent);
/*      */         break;
/*      */     } 
/*      */     
/* 1208 */     if (str.length() != 0 && 
/* 1209 */       bool == true) {
/* 1210 */       switch (b) {
/*      */         case 1:
/* 1212 */           str = "\033[3" + str;
/*      */           break;
/*      */         
/*      */         case 2:
/* 1216 */           str = "\033[2" + str;
/*      */           break;
/*      */         
/*      */         case 3:
/* 1220 */           str = "\033[1" + str;
/*      */           break;
/*      */       } 
/*      */ 
/*      */     
/*      */     }
/* 1226 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected String translate_special_key_release(KeyEvent paramKeyEvent) {
/* 1250 */     String str = "";
/* 1251 */     int i = 0;
/*      */ 
/*      */     
/* 1254 */     if (paramKeyEvent.isShiftDown()) {
/* 1255 */       i = 1;
/*      */     }
/*      */     
/* 1258 */     if (this.altlock || paramKeyEvent.isAltDown()) {
/* 1259 */       i += true;
/*      */     }
/*      */     
/* 1262 */     if (paramKeyEvent.isControlDown()) {
/* 1263 */       i += true;
/*      */     }
/*      */     
/* 1266 */     switch (paramKeyEvent.getKeyCode()) {
/*      */       case 243:
/*      */       case 244:
/*      */       case 263:
/* 1270 */         i += 128;
/*      */         break;
/*      */       case 29:
/* 1273 */         i += 136;
/*      */         break;
/*      */       case 28:
/*      */       case 256:
/*      */       case 257:
/* 1278 */         i += 144;
/*      */         break;
/*      */       case 241:
/*      */       case 242:
/*      */       case 245:
/* 1283 */         i += 152;
/*      */         break;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1295 */     if (i > 127) {
/* 1296 */       str = "" + (char)i;
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/* 1302 */       str = "";
/*      */     } 
/*      */     
/* 1305 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_ctrl_alt_del() {
/* 1316 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */     
/* 1318 */     arrayOfByte[2] = 5;
/* 1319 */     arrayOfByte[4] = 76;
/* 1320 */     String str1 = new String(arrayOfByte);
/* 1321 */     transmit(str1);
/*      */     
/*      */     try {
/* 1324 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1327 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1330 */     arrayOfByte[4] = 0;
/* 1331 */     String str2 = new String(arrayOfByte);
/* 1332 */     transmit(str2);
/*      */     
/*      */     try {
/* 1335 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1338 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1341 */     arrayOfByte[2] = 0;
/* 1342 */     String str3 = new String(arrayOfByte);
/* 1343 */     transmit(str3);
/*      */     
/* 1345 */     requestFocus();
/*      */   }
/*      */ 
/*      */   
/*      */   public void send_num_lock() {
/* 1350 */     System.out.println("sending num lock");
/* 1351 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */     
/* 1353 */     arrayOfByte[4] = 83;
/* 1354 */     String str1 = new String(arrayOfByte);
/* 1355 */     transmit(str1);
/*      */     
/*      */     try {
/* 1358 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1361 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1364 */     arrayOfByte[4] = 0;
/* 1365 */     String str2 = new String(arrayOfByte);
/* 1366 */     transmit(str2);
/*      */   }
/*      */ 
/*      */   
/*      */   public void send_caps_lock() {
/* 1371 */     System.out.println("sending caps lock");
/* 1372 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */     
/* 1374 */     arrayOfByte[4] = 57;
/* 1375 */     String str1 = new String(arrayOfByte);
/* 1376 */     transmit(str1);
/*      */     
/*      */     try {
/* 1379 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1382 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1385 */     arrayOfByte[4] = 0;
/* 1386 */     String str2 = new String(arrayOfByte);
/* 1387 */     transmit(str2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_ctrl_alt_back() {
/* 1396 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */     
/* 1398 */     arrayOfByte[2] = 5;
/* 1399 */     arrayOfByte[4] = 42;
/* 1400 */     String str1 = new String(arrayOfByte);
/* 1401 */     transmit(str1);
/*      */     
/*      */     try {
/* 1404 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1407 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1410 */     arrayOfByte[4] = 0;
/* 1411 */     String str2 = new String(arrayOfByte);
/* 1412 */     transmit(str2);
/*      */     
/*      */     try {
/* 1415 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1418 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1421 */     arrayOfByte[2] = 0;
/* 1422 */     String str3 = new String(arrayOfByte);
/* 1423 */     transmit(str3);
/*      */     
/* 1425 */     requestFocus();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_ctrl_alt_fn(int paramInt) {
/* 1434 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/* 1435 */     byte b = 0;
/*      */ 
/*      */ 
/*      */     
/* 1439 */     switch (paramInt + 1) {
/*      */       case 1:
/* 1441 */         b = 58;
/*      */         break;
/*      */       case 2:
/* 1444 */         b = 59;
/*      */         break;
/*      */       case 3:
/* 1447 */         b = 60;
/*      */         break;
/*      */       case 4:
/* 1450 */         b = 61;
/*      */         break;
/*      */       case 5:
/* 1453 */         b = 62;
/*      */         break;
/*      */       case 6:
/* 1456 */         b = 63;
/*      */         break;
/*      */       case 7:
/* 1459 */         b = 64;
/*      */         break;
/*      */       case 8:
/* 1462 */         b = 65;
/*      */         break;
/*      */       case 9:
/* 1465 */         b = 66;
/*      */         break;
/*      */       case 10:
/* 1468 */         b = 67;
/*      */         break;
/*      */       case 11:
/* 1471 */         b = 68;
/*      */         break;
/*      */       case 12:
/* 1474 */         b = 69;
/*      */         break;
/*      */       default:
/* 1477 */         b = 64;
/*      */         break;
/*      */     } 
/*      */     
/* 1481 */     arrayOfByte[2] = 5;
/* 1482 */     arrayOfByte[4] = b;
/*      */     
/* 1484 */     String str1 = new String(arrayOfByte);
/* 1485 */     transmit(str1);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1491 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1494 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1497 */     arrayOfByte[4] = 0;
/* 1498 */     String str2 = new String(arrayOfByte);
/* 1499 */     transmit(str2);
/*      */     
/*      */     try {
/* 1502 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1505 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1508 */     arrayOfByte[2] = 0;
/* 1509 */     String str3 = new String(arrayOfByte);
/* 1510 */     transmit(str3);
/*      */     
/* 1512 */     requestFocus();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_alt_fn(int paramInt) {
/* 1521 */     byte[] arrayOfByte = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/* 1522 */     byte b = 0;
/*      */ 
/*      */ 
/*      */     
/* 1526 */     switch (paramInt + 1) {
/*      */       case 1:
/* 1528 */         b = 58;
/*      */         break;
/*      */       case 2:
/* 1531 */         b = 59;
/*      */         break;
/*      */       case 3:
/* 1534 */         b = 60;
/*      */         break;
/*      */       case 4:
/* 1537 */         b = 61;
/*      */         break;
/*      */       case 5:
/* 1540 */         b = 62;
/*      */         break;
/*      */       case 6:
/* 1543 */         b = 63;
/*      */         break;
/*      */       case 7:
/* 1546 */         b = 64;
/*      */         break;
/*      */       case 8:
/* 1549 */         b = 65;
/*      */         break;
/*      */       case 9:
/* 1552 */         b = 66;
/*      */         break;
/*      */       case 10:
/* 1555 */         b = 67;
/*      */         break;
/*      */       case 11:
/* 1558 */         b = 68;
/*      */         break;
/*      */       case 12:
/* 1561 */         b = 69;
/*      */         break;
/*      */       default:
/* 1564 */         b = 64;
/*      */         break;
/*      */     } 
/*      */     
/* 1568 */     arrayOfByte[2] = 4;
/* 1569 */     arrayOfByte[4] = b;
/*      */     
/* 1571 */     String str1 = new String(arrayOfByte);
/* 1572 */     transmit(str1);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 1578 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1581 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1584 */     arrayOfByte[4] = 0;
/* 1585 */     String str2 = new String(arrayOfByte);
/* 1586 */     transmit(str2);
/*      */     
/*      */     try {
/* 1589 */       Thread.currentThread(); Thread.sleep(250L);
/*      */     } catch (InterruptedException interruptedException) {
/*      */       
/* 1592 */       System.out.println("Thread interrupted..");
/*      */     } 
/*      */     
/* 1595 */     arrayOfByte[2] = 0;
/* 1596 */     String str3 = new String(arrayOfByte);
/* 1597 */     transmit(str3);
/*      */     
/* 1599 */     requestFocus();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendMomPress() {
/* 1606 */     this.post_complete = false;
/* 1607 */     byte[] arrayOfByte = new byte[4];
/*      */     
/* 1609 */     arrayOfByte[0] = 0;
/* 1610 */     arrayOfByte[1] = 0;
/* 1611 */     arrayOfByte[2] = 0;
/* 1612 */     arrayOfByte[3] = 0;
/*      */     
/* 1614 */     String str = new String(arrayOfByte);
/* 1615 */     transmit(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendPressHold() {
/* 1623 */     this.post_complete = false;
/* 1624 */     byte[] arrayOfByte = new byte[4];
/*      */     
/* 1626 */     arrayOfByte[0] = 0;
/* 1627 */     arrayOfByte[1] = 0;
/* 1628 */     arrayOfByte[2] = 1;
/* 1629 */     arrayOfByte[3] = 0;
/*      */     
/* 1631 */     String str = new String(arrayOfByte);
/* 1632 */     transmit(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendPowerCycle() {
/* 1640 */     this.post_complete = false;
/* 1641 */     byte[] arrayOfByte = new byte[4];
/*      */     
/* 1643 */     arrayOfByte[0] = 0;
/* 1644 */     arrayOfByte[1] = 0;
/* 1645 */     arrayOfByte[2] = 2;
/* 1646 */     arrayOfByte[3] = 0;
/*      */     
/* 1648 */     String str = new String(arrayOfByte);
/* 1649 */     transmit(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendSystemReset() {
/* 1657 */     this.post_complete = false;
/* 1658 */     byte[] arrayOfByte = new byte[4];
/*      */     
/* 1660 */     arrayOfByte[0] = 0;
/* 1661 */     arrayOfByte[1] = 0;
/* 1662 */     arrayOfByte[2] = 3;
/* 1663 */     arrayOfByte[3] = 0;
/*      */     
/* 1665 */     String str = new String(arrayOfByte);
/* 1666 */     transmit(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_mouse_press(int paramInt) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_mouse_release(int paramInt) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_mouse_click(int paramInt1, int paramInt2) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_mouse_byte(int paramInt) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void refresh_screen() {
/* 1714 */     byte[] arrayOfByte = new byte[2];
/*      */     
/* 1716 */     arrayOfByte[0] = 5;
/* 1717 */     arrayOfByte[1] = 0;
/* 1718 */     String str = new String(arrayOfByte);
/*      */     
/* 1720 */     transmit(str);
/*      */     
/* 1722 */     requestFocus();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void send_keep_alive_msg() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String byteToHex(byte paramByte) {
/* 1744 */     StringBuffer stringBuffer = new StringBuffer();
/* 1745 */     stringBuffer.append(toHexChar(paramByte >>> 4 & 0xF));
/* 1746 */     stringBuffer.append(toHexChar(paramByte & 0xF));
/* 1747 */     return stringBuffer.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static String intToHex(int paramInt) {
/* 1753 */     byte b = (byte)paramInt;
/* 1754 */     return byteToHex(b);
/*      */   }
/*      */ 
/*      */   
/*      */   public static String intToHex4(int paramInt) {
/* 1759 */     StringBuffer stringBuffer = new StringBuffer();
/* 1760 */     stringBuffer.append(byteToHex((byte)(paramInt / 256)));
/* 1761 */     stringBuffer.append(byteToHex((byte)(paramInt & 0xFF)));
/* 1762 */     return stringBuffer.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static String charToHex(char paramChar) {
/* 1768 */     byte b = (byte)paramChar;
/* 1769 */     return byteToHex(b);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char toHexChar(int paramInt) {
/* 1781 */     if (0 <= paramInt && paramInt <= 9) {
/* 1782 */       return (char)(48 + paramInt);
/*      */     }
/*      */     
/* 1785 */     return (char)(65 + paramInt - 10);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized void set_framerate(int paramInt) {
/* 1791 */     framerate = paramInt;
/* 1792 */     this.screen.set_framerate(paramInt);
/* 1793 */     set_status(3, "" + framerate);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void show_error(String paramString) {
/* 1834 */     System.out.println("dvc:" + paramString + ": state " + dvc_decoder_state + " code " + dvc_code);
/* 1835 */     System.out.println("dvc:error at byte count " + count_bytes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void cache_reset() {
/* 1862 */     dvc_cc_active = 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int cache_lru(int paramInt) {
/* 1873 */     int j = dvc_cc_active;
/* 1874 */     int i = 0;
/* 1875 */     boolean bool = false;
/*      */     
/*      */     byte b;
/*      */     
/* 1879 */     for (b = 0; b < j; b++) {
/* 1880 */       if (paramInt == dvc_cc_color[b]) {
/*      */ 
/*      */         
/* 1883 */         i = b;
/*      */         
/* 1885 */         bool = true;
/*      */         break;
/*      */       } 
/* 1888 */       if (dvc_cc_usage[b] == j - 1) {
/* 1889 */         i = b;
/*      */       }
/*      */     } 
/*      */     
/* 1893 */     int k = dvc_cc_usage[i];
/*      */     
/* 1895 */     if (!bool) {
/*      */       
/* 1897 */       if (j < 17) {
/*      */         
/* 1899 */         i = j;
/*      */         
/* 1901 */         k = j;
/*      */         
/* 1903 */         dvc_cc_active = ++j;
/*      */         
/* 1905 */         if (dvc_cc_active < 2) {
/*      */ 
/*      */ 
/*      */           
/* 1909 */           dvc_pixcode = 38;
/*      */         }
/* 1911 */         else if (dvc_cc_active == 2) {
/* 1912 */           dvc_pixcode = 4;
/* 1913 */         } else if (dvc_cc_active == 3) {
/* 1914 */           dvc_pixcode = 5;
/* 1915 */         } else if (dvc_cc_active < 6) {
/* 1916 */           dvc_pixcode = 6;
/* 1917 */         } else if (dvc_cc_active < 10) {
/* 1918 */           dvc_pixcode = 7;
/*      */         } else {
/* 1920 */           dvc_pixcode = 32;
/* 1921 */         }  next_1[31] = dvc_pixcode;
/*      */       } 
/*      */ 
/*      */       
/* 1925 */       dvc_cc_color[i] = paramInt;
/*      */     } 
/*      */     
/* 1928 */     dvc_cc_block[i] = 1;
/*      */ 
/*      */     
/* 1931 */     for (b = 0; b < j; b++) {
/* 1932 */       if (dvc_cc_usage[b] < k) {
/* 1933 */         dvc_cc_usage[b] = dvc_cc_usage[b] + 1;
/*      */       }
/*      */     } 
/* 1936 */     dvc_cc_usage[i] = 0;
/* 1937 */     return bool;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int cache_find(int paramInt) {
/* 1947 */     int i = dvc_cc_active;
/*      */     
/* 1949 */     for (byte b = 0; b < i; b++) {
/* 1950 */       if (paramInt == dvc_cc_usage[b]) {
/*      */         
/* 1952 */         int j = dvc_cc_color[b];
/* 1953 */         byte b1 = b;
/*      */         
/* 1955 */         for (b = 0; b < i; b++) {
/* 1956 */           if (dvc_cc_usage[b] < paramInt) {
/* 1957 */             dvc_cc_usage[b] = dvc_cc_usage[b] + 1;
/*      */           }
/*      */         } 
/* 1960 */         dvc_cc_usage[b1] = 0;
/* 1961 */         dvc_cc_block[b1] = 1;
/* 1962 */         return j;
/*      */       } 
/*      */     } 
/* 1965 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void cache_prune() {
/* 1974 */     int i = dvc_cc_active;
/*      */ 
/*      */     
/* 1977 */     for (byte b = 0; b < i; ) {
/* 1978 */       int j = dvc_cc_block[b];
/* 1979 */       if (j == 0) {
/*      */         
/* 1981 */         i--;
/* 1982 */         dvc_cc_block[b] = dvc_cc_block[i];
/* 1983 */         dvc_cc_color[b] = dvc_cc_color[i];
/* 1984 */         dvc_cc_usage[b] = dvc_cc_usage[i];
/*      */         continue;
/*      */       } 
/* 1987 */       dvc_cc_block[b] = dvc_cc_block[b] - 1;
/* 1988 */       b++;
/*      */     } 
/*      */     
/* 1991 */     dvc_cc_active = i;
/* 1992 */     if (dvc_cc_active < 2) {
/*      */       
/* 1994 */       dvc_pixcode = 38;
/*      */     }
/* 1996 */     else if (dvc_cc_active == 2) {
/* 1997 */       dvc_pixcode = 4;
/* 1998 */     } else if (dvc_cc_active == 3) {
/* 1999 */       dvc_pixcode = 5;
/* 2000 */     } else if (dvc_cc_active < 6) {
/* 2001 */       dvc_pixcode = 6;
/* 2002 */     } else if (dvc_cc_active < 10) {
/* 2003 */       dvc_pixcode = 7;
/*      */     } else {
/* 2005 */       dvc_pixcode = 32;
/* 2006 */     }  next_1[31] = dvc_pixcode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void next_block(int paramInt) {
/* 2015 */     boolean bool = true;
/* 2016 */     if (!video_detected) {
/* 2017 */       bool = false;
/*      */     }
/*      */     
/* 2020 */     if (dvc_pixel_count != 0)
/*      */     {
/* 2022 */       if (dvc_y_clipped > 0 && dvc_lasty == dvc_size_y) {
/*      */ 
/*      */         
/* 2025 */         int m = this.color_remap_table[0];
/* 2026 */         for (int k = dvc_y_clipped; k < 256; k++) {
/* 2027 */           block[k] = m;
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/* 2032 */     dvc_pixel_count = 0;
/* 2033 */     dvc_next_state = 1;
/*      */     
/* 2035 */     int i = dvc_lastx * this.blockWidth;
/* 2036 */     int j = dvc_lasty * this.blockHeight;
/* 2037 */     while (paramInt != 0) {
/* 2038 */       if (bool) {
/* 2039 */         this.screen.paste_array(block, i, j, 16, this.blockHeight);
/*      */       }
/*      */       
/* 2042 */       dvc_lastx++;
/* 2043 */       i += 16;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2060 */       if (dvc_lastx >= dvc_size_x)
/*      */         break; 
/* 2062 */       paramInt--;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void init_reversal() {
/* 2073 */     for (byte b = 0; b < 'Ā'; b++) {
/* 2074 */       byte b2 = 8;
/* 2075 */       int k = 8;
/* 2076 */       int i = b;
/* 2077 */       int j = 0;
/* 2078 */       for (byte b1 = 0; b1 < 8; b1++) {
/* 2079 */         j <<= 1;
/* 2080 */         if ((i & 0x1) == 1) {
/* 2081 */           if (b2 > b1)
/* 2082 */             b2 = b1; 
/* 2083 */           j |= 0x1;
/* 2084 */           k = 7 - b1;
/*      */         } 
/* 2086 */         i >>= 1;
/*      */       } 
/* 2088 */       dvc_reversal[b] = j;
/* 2089 */       dvc_right[b] = b2;
/* 2090 */       dvc_left[b] = k;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int add_bits(char paramChar) {
/* 2098 */     dvc_zero_count += dvc_right[paramChar];
/*      */ 
/*      */     
/* 2101 */     char c = paramChar;
/* 2102 */     dvc_ib_acc |= c << dvc_ib_bcnt;
/*      */     
/* 2104 */     dvc_ib_bcnt += 8;
/*      */     
/* 2106 */     if (dvc_zero_count > 30) {
/*      */ 
/*      */       
/* 2109 */       if (!debug_msgs || 
/* 2110 */         dvc_decoder_state != 38 || fatal_count >= 40 || fatal_count > 0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2118 */       dvc_next_state = 43;
/* 2119 */       dvc_decoder_state = 43;
/* 2120 */       return 4;
/*      */     } 
/*      */     
/* 2123 */     if (paramChar != '\000') {
/* 2124 */       dvc_zero_count = dvc_left[paramChar];
/*      */     }
/* 2126 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final int get_bits(int paramInt) {
/* 2135 */     if (paramInt == 1) {
/* 2136 */       dvc_code = dvc_ib_acc & 0x1;
/* 2137 */       dvc_ib_acc >>= 1;
/* 2138 */       dvc_ib_bcnt--;
/* 2139 */       return 0;
/*      */     } 
/*      */ 
/*      */     
/* 2143 */     if (paramInt == 0) {
/* 2144 */       return 0;
/*      */     }
/*      */     
/* 2147 */     int i = dvc_ib_acc & dvc_getmask[paramInt];
/*      */ 
/*      */     
/* 2150 */     dvc_ib_bcnt -= paramInt;
/*      */ 
/*      */     
/* 2153 */     dvc_ib_acc >>= paramInt;
/*      */ 
/*      */     
/* 2156 */     i = dvc_reversal[i];
/*      */ 
/*      */     
/* 2159 */     i >>= 8 - paramInt;
/*      */     
/* 2161 */     dvc_code = i;
/*      */ 
/*      */     
/* 2164 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   int process_bits(char paramChar) {
/* 2178 */     boolean bool = true;
/*      */     
/* 2180 */     byte b = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2185 */     add_bits(paramChar);
/* 2186 */     dvc_new_bits = paramChar;
/* 2187 */     count_bytes++;
/* 2188 */     int i = 0;
/*      */ 
/*      */     
/* 2191 */     while (!b) {
/* 2192 */       byte b1; i = bits_to_read[dvc_decoder_state];
/*      */       
/* 2194 */       if (i > dvc_ib_bcnt) {
/*      */         
/* 2196 */         b = 0;
/*      */ 
/*      */         
/*      */         break;
/*      */       } 
/*      */ 
/*      */       
/* 2203 */       int j = get_bits(i);
/* 2204 */       dvc_counter_bits += i;
/*      */ 
/*      */       
/* 2207 */       if (dvc_code == 0) {
/* 2208 */         dvc_next_state = next_0[dvc_decoder_state];
/*      */       } else {
/* 2210 */         dvc_next_state = next_1[dvc_decoder_state];
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 2215 */       switch (dvc_decoder_state) {
/*      */ 
/*      */         
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 32:
/* 2224 */           if (dvc_cc_active == 1) {
/* 2225 */             dvc_code = dvc_cc_usage[0];
/*      */           }
/* 2227 */           else if (dvc_decoder_state == 4) {
/* 2228 */             dvc_code = 0;
/* 2229 */           } else if (dvc_decoder_state == 3) {
/* 2230 */             dvc_code = 1;
/* 2231 */           } else if (dvc_code != 0) {
/* 2232 */             dvc_code++;
/*      */           } 
/* 2234 */           dvc_color = cache_find(dvc_code);
/* 2235 */           if (dvc_color == -1) {
/*      */ 
/*      */ 
/*      */             
/* 2239 */             dvc_next_state = 38;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2244 */           dvc_last_color = this.color_remap_table[dvc_color];
/*      */           
/* 2246 */           if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
/* 2247 */             block[dvc_pixel_count] = dvc_last_color;
/*      */           }
/*      */           else {
/*      */             
/* 2251 */             dvc_next_state = 38;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2256 */           dvc_pixel_count++;
/*      */           break;
/*      */         
/*      */         case 12:
/* 2260 */           if (dvc_code == 7) {
/* 2261 */             dvc_next_state = 14; break;
/* 2262 */           }  if (dvc_code == 6) {
/* 2263 */             dvc_next_state = 13;
/*      */             break;
/*      */           } 
/* 2266 */           dvc_code += 2;
/* 2267 */           for (b1 = 0; b1 < dvc_code; b1++) {
/*      */             
/* 2269 */             if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
/* 2270 */               block[dvc_pixel_count] = dvc_last_color;
/*      */             }
/*      */             else {
/*      */               
/* 2274 */               dvc_next_state = 38;
/*      */ 
/*      */               
/*      */               break;
/*      */             } 
/*      */             
/* 2280 */             dvc_pixel_count++;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 13:
/* 2285 */           dvc_code += 8;
/*      */         
/*      */         case 14:
/* 2288 */           if (!debug_msgs || 
/* 2289 */             dvc_decoder_state != 14 || dvc_code < 16);
/*      */ 
/*      */ 
/*      */           
/* 2293 */           for (b1 = 0; b1 < dvc_code; b1++) {
/*      */             
/* 2295 */             if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
/* 2296 */               block[dvc_pixel_count] = dvc_last_color;
/*      */             }
/*      */             else {
/*      */               
/* 2300 */               dvc_next_state = 38;
/*      */ 
/*      */               
/*      */               break;
/*      */             } 
/*      */             
/* 2306 */             dvc_pixel_count++;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 33:
/* 2311 */           if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
/* 2312 */             block[dvc_pixel_count] = dvc_last_color;
/*      */           }
/*      */           else {
/*      */             
/* 2316 */             dvc_next_state = 38;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2321 */           dvc_pixel_count++;
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 35:
/* 2335 */           dvc_next_state = dvc_pixcode;
/*      */           break;
/*      */ 
/*      */ 
/*      */         
/*      */         case 9:
/* 2341 */           dvc_red = dvc_code << this.bitsPerColor * 2;
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 41:
/* 2351 */           dvc_green = dvc_code << this.bitsPerColor;
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 8:
/* 2360 */           dvc_red = dvc_code << this.bitsPerColor * 2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2368 */           dvc_green = dvc_code << this.bitsPerColor;
/*      */ 
/*      */ 
/*      */         
/*      */         case 42:
/* 2373 */           dvc_blue = dvc_code;
/* 2374 */           dvc_color = dvc_red | dvc_green | dvc_blue;
/* 2375 */           j = cache_lru(dvc_color);
/* 2376 */           if (j != 0) {
/* 2377 */             if (!debug_msgs || 
/* 2378 */               count_bytes > 6L);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2387 */             dvc_next_state = 38;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2392 */           dvc_last_color = this.color_remap_table[dvc_color];
/*      */           
/* 2394 */           if (dvc_pixel_count < this.blockHeight * this.blockWidth) {
/* 2395 */             block[dvc_pixel_count] = dvc_last_color;
/*      */           }
/*      */           else {
/*      */             
/* 2399 */             dvc_next_state = 38;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2404 */           dvc_pixel_count++;
/*      */           break;
/*      */         case 17:
/*      */         case 26:
/* 2408 */           dvc_newx = dvc_code;
/* 2409 */           if (dvc_decoder_state == 17 && dvc_newx > dvc_size_x) {
/* 2410 */             if (debug_msgs);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2415 */             dvc_newx = 0;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 39:
/* 2420 */           dvc_newy = dvc_code;
/* 2421 */           if (this.blockHeight == 16) {
/* 2422 */             dvc_newy &= 0x7F;
/*      */           }
/*      */           
/* 2425 */           dvc_lastx = dvc_newx;
/* 2426 */           dvc_lasty = dvc_newy;
/*      */           
/* 2428 */           if (dvc_lasty <= dvc_size_y || 
/* 2429 */             debug_msgs);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2441 */           this.screen.repaint_it(1);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 20:
/* 2446 */           dvc_code = dvc_lastx + dvc_code + 1;
/* 2447 */           if (dvc_code <= dvc_size_x || 
/* 2448 */             debug_msgs);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 21:
/* 2456 */           dvc_lastx = dvc_code;
/* 2457 */           if (this.blockHeight == 16) {
/* 2458 */             dvc_lastx &= 0x7F;
/*      */           }
/* 2460 */           if (dvc_lastx <= dvc_size_x || 
/* 2461 */             debug_msgs);
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 27:
/* 2475 */           if (timeout_count == count_bytes - 1L)
/*      */           {
/*      */             
/* 2478 */             dvc_next_state = 38;
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2488 */           if ((dvc_ib_bcnt & 0x7) != 0)
/* 2489 */             get_bits(dvc_ib_bcnt & 0x7); 
/* 2490 */           timeout_count = count_bytes;
/*      */           
/* 2492 */           this.screen.repaint_it(1);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 24:
/* 2497 */           if (cmd_p_count != 0)
/* 2498 */             cmd_p_buff[cmd_p_count - 1] = cmd_last; 
/* 2499 */           cmd_p_count++;
/*      */           
/* 2501 */           cmd_last = dvc_code;
/*      */           break;
/*      */         
/*      */         case 46:
/* 2505 */           if (dvc_code == 0) {
/*      */ 
/*      */             
/* 2508 */             switch (cmd_last) {
/*      */               case 1:
/* 2510 */                 dvc_next_state = 37;
/*      */                 break;
/*      */ 
/*      */               
/*      */               case 2:
/* 2515 */                 dvc_next_state = 44;
/*      */                 break;
/*      */ 
/*      */               
/*      */               case 3:
/* 2520 */                 if (cmd_p_count != 0) {
/* 2521 */                   set_framerate(cmd_p_buff[0]); break;
/*      */                 } 
/* 2523 */                 set_framerate(0);
/*      */                 break;
/*      */               case 4:
/* 2526 */                 this.remconsObj.setPwrStatusPower(1);
/*      */                 break;
/*      */               
/*      */               case 5:
/* 2530 */                 this.remconsObj.setPwrStatusPower(0);
/*      */                 
/* 2532 */                 this.screen.clearScreen();
/* 2533 */                 dvc_newx = 50;
/* 2534 */                 dvc_code = 38;
/*      */                 break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               case 6:
/* 2544 */                 this.screen.clearScreen();
/*      */ 
/*      */ 
/*      */                 
/* 2548 */                 if (!video_detected) {
/* 2549 */                   this.screen.clearScreen();
/*      */                 }
/* 2551 */                 set_status(2, getLocalString(12290));
/* 2552 */                 set_status(1, " ");
/*      */                 
/* 2554 */                 set_status(3, " ");
/* 2555 */                 set_status(4, " ");
/* 2556 */                 this.post_complete = false;
/*      */                 break;
/*      */               case 7:
/* 2559 */                 this.ts_type = cmd_p_buff[0];
/*      */                 break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               case 9:
/* 2585 */                 System.out.println("received keychg and cleared bits\n");
/* 2586 */                 if ((dvc_ib_bcnt & 0x7) != 0) {
/* 2587 */                   get_bits(dvc_ib_bcnt & 0x7);
/*      */                 }
/*      */                 break;
/*      */               
/*      */               case 10:
/* 2592 */                 seize();
/*      */                 break;
/*      */ 
/*      */ 
/*      */               
/*      */               case 11:
/* 2598 */                 System.out.println("Setting bpc to  " + cmd_p_buff[0]);
/* 2599 */                 setBitsPerColor(cmd_p_buff[0]);
/*      */                 break;
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*      */               case 12:
/* 2606 */                 System.out.println("Setting encryption to  " + cmd_p_buff[0]);
/* 2607 */                 setVideoDecryption(cmd_p_buff[0]);
/*      */                 break;
/*      */ 
/*      */ 
/*      */               
/*      */               case 13:
/* 2613 */                 System.out.println("Header received ");
/* 2614 */                 setBitsPerColor(cmd_p_buff[0]);
/*      */ 
/*      */                 
/* 2617 */                 setVideoDecryption(cmd_p_buff[1]);
/*      */                 
/* 2619 */                 this.remconsObj.SetLicensed(cmd_p_buff[2]);
/* 2620 */                 this.remconsObj.SetFlags(cmd_p_buff[3]);
/*      */                 break;
/*      */               case 16:
/* 2623 */                 sendAck();
/*      */                 break;
/*      */               
/*      */               case 128:
/* 2627 */                 this.screen.invalidate();
/* 2628 */                 this.screen.repaint();
/*      */                 break;
/*      */             } 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2635 */             cmd_p_count = 0;
/*      */           } 
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 44:
/* 2645 */           printchan = dvc_code;
/* 2646 */           printstring = "";
/*      */           break;
/*      */ 
/*      */         
/*      */         case 45:
/* 2651 */           if (dvc_code != 0) {
/* 2652 */             printstring += (char)dvc_code;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2657 */           switch (printchan) {
/*      */             case 1:
/*      */             case 2:
/* 2660 */               set_status(2 + printchan, printstring);
/*      */               break;
/*      */             case 3:
/* 2663 */               System.out.println(printstring);
/*      */               break;
/*      */             
/*      */             case 4:
/* 2667 */               this.screen.show_text(printstring);
/*      */               break;
/*      */           } 
/*      */           
/* 2671 */           dvc_next_state = 1;
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 0:
/* 2687 */           cache_reset();
/* 2688 */           dvc_pixel_count = 0;
/* 2689 */           dvc_lastx = 0;
/* 2690 */           dvc_lasty = 0;
/* 2691 */           dvc_red = 0;
/* 2692 */           dvc_green = 0;
/* 2693 */           dvc_blue = 0;
/* 2694 */           fatal_count = 0;
/* 2695 */           timeout_count = -1L;
/*      */           
/* 2697 */           cmd_p_count = 0;
/*      */           break;
/*      */         
/*      */         case 38:
/* 2701 */           if (fatal_count == 0) {
/*      */             
/* 2703 */             debug_lastx = dvc_lastx;
/* 2704 */             debug_lasty = dvc_lasty;
/* 2705 */             debug_show_block = 1;
/*      */           } 
/* 2707 */           if (fatal_count == 40)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2713 */             refresh_screen();
/*      */           }
/* 2715 */           if (fatal_count == 11680) {
/* 2716 */             refresh_screen();
/*      */           }
/* 2718 */           fatal_count++;
/* 2719 */           if (fatal_count == 120000)
/*      */           {
/* 2721 */             refresh_screen();
/*      */           }
/* 2723 */           if (fatal_count == 12000000) {
/*      */             
/* 2725 */             refresh_screen();
/* 2726 */             fatal_count = 41;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 34:
/* 2731 */           next_block(1);
/*      */           break;
/*      */         case 29:
/* 2734 */           dvc_code += 2;
/*      */         
/*      */         case 30:
/* 2737 */           next_block(dvc_code);
/*      */           break;
/*      */         
/*      */         case 40:
/* 2741 */           dvc_size_x = dvc_newx;
/* 2742 */           dvc_size_y = dvc_code;
/*      */           break;
/*      */ 
/*      */         
/*      */         case 47:
/* 2747 */           dvc_lastx = 0;
/* 2748 */           dvc_lasty = 0;
/* 2749 */           dvc_pixel_count = 0;
/* 2750 */           cache_reset();
/* 2751 */           this.scale_x = 1;
/* 2752 */           this.scale_y = 1;
/* 2753 */           this.screen_x = dvc_size_x * this.blockWidth;
/* 2754 */           this.screen_y = dvc_size_y * 16 + dvc_code;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2762 */           if (this.screen_x == 0 || this.screen_y == 0) {
/* 2763 */             video_detected = false;
/*      */           } else {
/* 2765 */             video_detected = true;
/*      */           } 
/*      */           
/* 2768 */           if (dvc_code > 0) {
/* 2769 */             dvc_y_clipped = 256 - 16 * dvc_code;
/*      */           } else {
/* 2771 */             dvc_y_clipped = 0;
/*      */           } 
/* 2773 */           if (!video_detected) {
/* 2774 */             this.screen.clearScreen();
/* 2775 */             set_status(2, getLocalString(12290));
/* 2776 */             set_status(1, " ");
/* 2777 */             set_status(3, " ");
/* 2778 */             set_status(4, " ");
/* 2779 */             System.out.println("No video. image_source = " + this.screen.image_source);
/* 2780 */             this.post_complete = false;
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/* 2785 */           this.screen.set_abs_dimensions(this.screen_x, this.screen_y);
/* 2786 */           SetHalfHeight();
/* 2787 */           this.mouse_sync.serverScreen(this.screen_x, this.screen_y);
/* 2788 */           set_status(2, getLocalString(12291) + this.screen_x + "x" + this.screen_y);
/* 2789 */           set_status(1, " ");
/*      */           break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case 43:
/* 2797 */           if (dvc_next_state != dvc_decoder_state) {
/* 2798 */             dvc_ib_bcnt = 0;
/* 2799 */             dvc_ib_acc = 0;
/* 2800 */             dvc_zero_count = 0;
/* 2801 */             count_bytes = 0L;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 37:
/* 2806 */           return 1;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2813 */       if (dvc_next_state == 2 && dvc_pixel_count == this.blockHeight * this.blockWidth) {
/* 2814 */         next_block(1);
/* 2815 */         cache_prune();
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2844 */       if (dvc_decoder_state == dvc_next_state && dvc_decoder_state != 45 && dvc_decoder_state != 38 && dvc_decoder_state != 43) {
/* 2845 */         System.out.println("Machine hung in state " + dvc_decoder_state);
/* 2846 */         b = 6;
/*      */         continue;
/*      */       } 
/* 2849 */       dvc_decoder_state = dvc_next_state;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2861 */     return b;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean process_dvc(char paramChar) {
/*      */     boolean bool1;
/*      */     boolean bool2;
/* 2871 */     if (dvc_reversal[255] == 0) {
/*      */       
/* 2873 */       System.out.println("dvc initializing");
/*      */ 
/*      */       
/* 2876 */       init_reversal();
/* 2877 */       cache_reset();
/* 2878 */       dvc_decoder_state = 0;
/* 2879 */       dvc_next_state = 0;
/* 2880 */       dvc_zero_count = 0;
/* 2881 */       dvc_ib_acc = 0;
/* 2882 */       dvc_ib_bcnt = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2896 */       buildPixelTable(this.bitsPerColor);
/* 2897 */       SetHalfHeight();
/*      */     } 
/*      */     
/* 2900 */     if (!dvc_process_inhibit) {
/* 2901 */       bool1 = process_bits(paramChar);
/*      */     } else {
/* 2903 */       bool1 = false;
/*      */     } 
/* 2905 */     if (!bool1) {
/* 2906 */       bool2 = true;
/*      */     } else {
/* 2908 */       System.out.println("Exit from DVC mode status =" + bool1);
/* 2909 */       System.out.println("Current block at " + dvc_lastx + " " + dvc_lasty);
/* 2910 */       System.out.println("Byte count " + count_bytes);
/* 2911 */       bool2 = true;
/*      */       
/* 2913 */       dvc_decoder_state = 38;
/* 2914 */       dvc_next_state = 38;
/*      */ 
/*      */ 
/*      */       
/* 2918 */       fatal_count = 0;
/* 2919 */       refresh_screen();
/*      */     } 
/* 2921 */     return bool2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void set_sig_colors(int[] paramArrayOfint) {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void change_key() {
/* 2934 */     this.RC4encrypter.update_key();
/* 2935 */     super.change_key();
/*      */   }
/*      */ 
/*      */   
/*      */   public void set_mouse_protocol(int paramInt) {
/* 2940 */     this.mouse_protocol = paramInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 2946 */   private static final byte[] cursor_none = new byte[] { 0 };
/*      */ 
/*      */   
/* 2949 */   private static final int[] cursor_outline = new int[] { -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, -8355712, -8355712, -8355712, -8355712, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, -8355712, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Cursor current_cursor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Cursor customCursor(Image paramImage, Point paramPoint, String paramString) {
/* 2975 */     Cursor cursor = null;
/*      */     try {
/* 2977 */       Class clazz = Toolkit.class;
/* 2978 */       Method method = clazz.getMethod("createCustomCursor", new Class[] { Image.class, Point.class, String.class });
/*      */       
/* 2980 */       Toolkit toolkit = Toolkit.getDefaultToolkit();
/* 2981 */       if (method != null) {
/* 2982 */         cursor = (Cursor)method.invoke(toolkit, new Object[] { paramImage, paramPoint, paramString });
/*      */       }
/*      */     } catch (Exception exception) {
/*      */       
/* 2986 */       System.out.println("This JVM cannot create custom cursors");
/*      */     } 
/* 2988 */     return cursor;
/*      */   }
/*      */   
/*      */   Cursor createCursor(int paramInt) {
/*      */     MemoryImageSource memoryImageSource;
/*      */     Image image;
/*      */     int[] arrayOfInt;
/*      */     byte b;
/* 2996 */     String str = System.getProperty("java.version", "0");
/*      */ 
/*      */     
/* 2999 */     Toolkit toolkit = Toolkit.getDefaultToolkit();
/*      */     
/* 3001 */     switch (paramInt) {
/*      */       case 0:
/* 3003 */         return Cursor.getDefaultCursor();
/*      */       case 1:
/* 3005 */         return Cursor.getPredefinedCursor(1);
/*      */       case 2:
/* 3007 */         image = toolkit.createImage(cursor_none);
/*      */         break;
/*      */       
/*      */       case 3:
/* 3011 */         arrayOfInt = new int[1024];
/* 3012 */         arrayOfInt[33] = -8355712; arrayOfInt[32] = -8355712; arrayOfInt[1] = -8355712; arrayOfInt[0] = -8355712;
/* 3013 */         memoryImageSource = new MemoryImageSource(32, 32, arrayOfInt, 0, 32);
/* 3014 */         image = createImage(memoryImageSource);
/*      */         break;
/*      */       
/*      */       case 4:
/* 3018 */         arrayOfInt = new int[1024];
/* 3019 */         for (b = 0; b < 21; b++) {
/* 3020 */           for (byte b1 = 0; b1 < 12; b1++) {
/* 3021 */             arrayOfInt[b1 + b * 32] = cursor_outline[b1 + b * 12];
/*      */           }
/*      */         } 
/* 3024 */         memoryImageSource = new MemoryImageSource(32, 32, arrayOfInt, 0, 32);
/* 3025 */         image = createImage(memoryImageSource);
/*      */         break;
/*      */       default:
/* 3028 */         System.out.println("createCursor: unknown cursor " + paramInt);
/* 3029 */         return Cursor.getDefaultCursor();
/*      */     } 
/*      */     
/* 3032 */     Cursor cursor = null;
/* 3033 */     if (str.compareTo("1.2") < 0) {
/* 3034 */       System.out.println("This JVM cannot create custom cursors");
/*      */     } else {
/*      */       
/* 3037 */       cursor = customCursor(image, new Point(), "rcCursor");
/*      */     } 
/*      */     
/* 3040 */     return (cursor != null) ? cursor : Cursor.getDefaultCursor();
/*      */   }
/*      */ 
/*      */   
/*      */   public void set_cursor(int paramInt) {
/* 3045 */     this.current_cursor = createCursor(paramInt);
/* 3046 */     setCursor(this.current_cursor);
/*      */   }
/*      */ 
/*      */   
/*      */   private void SetHalfHeight() {
/* 3051 */     if (this.screen_x > 1616) {
/* 3052 */       if (this.remconsObj.halfHeightCapable) {
/* 3053 */         if (8 != this.blockHeight) {
/* 3054 */           System.out.println("Setting halfheight mode on supported system");
/* 3055 */           this.blockHeight = 8;
/* 3056 */           bits_to_read[21] = 8;
/* 3057 */           bits_to_read[17] = 8;
/* 3058 */           bits_to_read[39] = 8;
/* 3059 */           bits_to_read[30] = 8;
/*      */         }
/*      */       
/*      */       }
/* 3063 */       else if (!this.unsupportedVideoModeWarned) {
/*      */         
/* 3065 */         new VErrorDialog(this.remconsObj.ParentApp.dispFrame, getLocalString(8225), getLocalString(8226), false);
/* 3066 */         this.unsupportedVideoModeWarned = true;
/*      */       
/*      */       }
/*      */     
/*      */     }
/* 3071 */     else if (16 != this.blockHeight) {
/* 3072 */       System.out.println("Setting non-halfheight mode");
/* 3073 */       this.blockHeight = 16;
/* 3074 */       bits_to_read[21] = 7;
/* 3075 */       bits_to_read[17] = 7;
/* 3076 */       bits_to_read[39] = 7;
/* 3077 */       bits_to_read[30] = 7;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void buildPixelTable(int paramInt) {
/*      */     byte b;
/* 3088 */     int i = 1 << paramInt * 3;
/*      */ 
/*      */     
/* 3091 */     switch (paramInt) {
/*      */       
/*      */       case 5:
/* 3094 */         for (b = 0; b < i; b++) {
/* 3095 */           this.color_remap_table[b] = (b & 0x1F) << 3;
/* 3096 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0x3E0) << 6;
/* 3097 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0x7C00) << 9;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 4:
/* 3102 */         for (b = 0; b < i; b++) {
/* 3103 */           this.color_remap_table[b] = (b & 0xF) << 4;
/* 3104 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF0) << 8;
/* 3105 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF00) << 12;
/*      */         } 
/*      */         break;
/*      */       case 3:
/* 3109 */         for (b = 0; b < i; b++) {
/* 3110 */           this.color_remap_table[b] = (b & 0xF) << 5;
/* 3111 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF0) << 11;
/* 3112 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF00) << 15;
/*      */         } 
/*      */         break;
/*      */       
/*      */       case 2:
/* 3117 */         for (b = 0; b < i; b++) {
/* 3118 */           this.color_remap_table[b] = (b & 0xF) << 6;
/* 3119 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF0) << 15;
/* 3120 */           this.color_remap_table[b] = this.color_remap_table[b] | (b & 0xF00) << 18;
/*      */         } 
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setBitsPerColor(int paramInt) {
/* 3130 */     this.bitsPerColor = 5 - (paramInt & 0x3);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 3135 */     bits_to_read[8] = this.bitsPerColor;
/* 3136 */     bits_to_read[9] = this.bitsPerColor;
/* 3137 */     bits_to_read[41] = this.bitsPerColor;
/* 3138 */     bits_to_read[42] = this.bitsPerColor;
/*      */     
/* 3140 */     buildPixelTable(this.bitsPerColor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setVideoDecryption(int paramInt) {
/* 3147 */     switch (paramInt) {
/*      */       case 0:
/* 3149 */         this.dvc_encryption = false;
/* 3150 */         this.cipher = 0;
/* 3151 */         this.remconsObj.setPwrStatusEncLabel(getLocalString(12292));
/* 3152 */         this.remconsObj.setPwrStatusEnc(0);
/* 3153 */         System.out.println("Setting encryption -> None");
/*      */         return;
/*      */       case 1:
/* 3156 */         this.dvc_encryption = true;
/* 3157 */         this.remconsObj.setPwrStatusEncLabel(getLocalString(12293));
/* 3158 */         this.remconsObj.setPwrStatusEnc(1);
/* 3159 */         this.dvc_mode = true;
/* 3160 */         this.cipher = 1;
/* 3161 */         System.out.println("Setting encryption -> RC4 - 128 bit");
/*      */         return;
/*      */       case 2:
/* 3164 */         this.dvc_encryption = true;
/* 3165 */         this.remconsObj.setPwrStatusEncLabel(getLocalString(12294));
/* 3166 */         this.remconsObj.setPwrStatusEnc(1);
/* 3167 */         this.dvc_mode = true;
/* 3168 */         this.cipher = 2;
/* 3169 */         System.out.println("Setting encryption -> AES - 128 bit");
/*      */         return;
/*      */       case 3:
/* 3172 */         this.dvc_encryption = true;
/* 3173 */         this.remconsObj.setPwrStatusEncLabel(getLocalString(12295));
/* 3174 */         this.remconsObj.setPwrStatusEnc(1);
/* 3175 */         this.dvc_mode = true;
/* 3176 */         this.cipher = 3;
/* 3177 */         System.out.println("Setting encryption -> AES - 256 bit");
/*      */         return;
/*      */     } 
/* 3180 */     this.dvc_encryption = false;
/* 3181 */     this.remconsObj.setPwrStatusEncLabel(getLocalString(12292));
/* 3182 */     this.remconsObj.setPwrStatusEnc(0);
/* 3183 */     System.out.println("Unsupported encryption");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte mouseButtonState(int paramInt) {
/* 3189 */     byte b = 0;
/* 3190 */     switch (paramInt) {
/*      */       case 4:
/* 3192 */         b = (byte)(b | 0x1);
/*      */         break;
/*      */       case 2:
/* 3195 */         b = (byte)(b | 0x4);
/*      */         break;
/*      */       case 1:
/* 3198 */         b = (byte)(b | 0x2);
/*      */         break;
/*      */     } 
/* 3201 */     return b;
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getMouseButtonState(MouseEvent paramMouseEvent) {
/* 3206 */     byte b = 0;
/* 3207 */     if ((paramMouseEvent.getModifiersEx() & 0x1000) != 0)
/* 3208 */       b = (byte)(b | 0x2); 
/* 3209 */     if ((paramMouseEvent.getModifiersEx() & 0x800) != 0)
/* 3210 */       b = (byte)(b | 0x4); 
/* 3211 */     if ((paramMouseEvent.getModifiersEx() & 0x400) != 0) {
/* 3212 */       b = (byte)(b | 0x1);
/*      */     }
/*      */     
/* 3215 */     return b;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendMouse(MouseEvent paramMouseEvent) {
/* 3223 */     Point point1 = new Point(0, 0);
/* 3224 */     Point point2 = new Point(0, 0);
/*      */     
/* 3226 */     point1 = getAbsMouseCoordinates(paramMouseEvent);
/* 3227 */     char c1 = (char)point1.x;
/* 3228 */     char c2 = (char)point1.y;
/*      */     
/* 3230 */     if ((paramMouseEvent.getModifiersEx() & 0x80) > 0) {
/*      */ 
/*      */ 
/*      */       
/* 3234 */       this.mousePrevPosn.x = c1;
/* 3235 */       this.mousePrevPosn.y = c2;
/*      */     }
/* 3237 */     else if (c1 <= this.screen_x && c2 <= this.screen_y) {
/*      */       
/* 3239 */       point2.x = c1 - this.mousePrevPosn.x;
/* 3240 */       this.mousePrevPosn.y -= c2;
/*      */ 
/*      */       
/* 3243 */       this.mousePrevPosn.x = c1;
/* 3244 */       this.mousePrevPosn.y = c2;
/*      */       
/* 3246 */       int i = point2.x;
/* 3247 */       int j = point2.y;
/*      */       
/* 3249 */       if (i < -127) {
/* 3250 */         i = -127;
/*      */       }
/* 3252 */       else if (i > 127) {
/* 3253 */         i = 127;
/*      */       } 
/* 3255 */       if (j < -127) {
/* 3256 */         j = -127;
/*      */       }
/* 3258 */       else if (j > 127) {
/* 3259 */         j = 127;
/*      */       } 
/*      */       
/* 3262 */       this.UI_dirty = true;
/*      */       
/* 3264 */       if (this.screen_x > 0 && this.screen_y > 0) {
/* 3265 */         c1 = (char)(3000 * c1 / this.screen_x);
/* 3266 */         c2 = (char)(3000 * c2 / this.screen_y);
/*      */       } else {
/*      */         
/* 3269 */         c1 = (char)(3000 * c1 / 1);
/* 3270 */         c2 = (char)(3000 * c2 / 1);
/*      */       } 
/*      */ 
/*      */       
/* 3274 */       byte[] arrayOfByte = new byte[10];
/*      */       
/* 3276 */       arrayOfByte[0] = 2;
/* 3277 */       arrayOfByte[1] = 0;
/* 3278 */       arrayOfByte[2] = (byte)(c1 & 0xFF);
/* 3279 */       arrayOfByte[3] = (byte)(c1 >> 8);
/* 3280 */       arrayOfByte[4] = (byte)(c2 & 0xFF);
/* 3281 */       arrayOfByte[5] = (byte)(c2 >> 8);
/*      */       
/* 3283 */       if (i < 0) {
/* 3284 */         arrayOfByte[6] = (byte)(i & 0xFF);
/*      */       } else {
/*      */         
/* 3287 */         arrayOfByte[6] = (byte)(i & 0xFF);
/*      */       } 
/*      */       
/* 3290 */       if (j < 0) {
/* 3291 */         arrayOfByte[7] = (byte)(j & 0xFF);
/*      */       } else {
/*      */         
/* 3294 */         arrayOfByte[7] = (byte)(j & 0xFF);
/*      */       } 
/* 3296 */       arrayOfByte[8] = getMouseButtonState(paramMouseEvent);
/* 3297 */       arrayOfByte[9] = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 3302 */       transmitb(arrayOfByte, arrayOfByte.length);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Point getAbsMouseCoordinates(MouseEvent paramMouseEvent) {
/* 3313 */     Point point = new Point();
/*      */     
/* 3315 */     point.y = paramMouseEvent.getY();
/*      */     
/* 3317 */     point.x = paramMouseEvent.getX();
/* 3318 */     return point;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendMouseScroll(MouseWheelEvent paramMouseWheelEvent) {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sendAck() {
/* 3341 */     byte[] arrayOfByte = new byte[2];
/*      */     
/* 3343 */     arrayOfByte[0] = 12;
/* 3344 */     arrayOfByte[1] = 0;
/*      */     
/* 3346 */     String str = new String(arrayOfByte);
/* 3347 */     transmit(str);
/*      */   }
/*      */ 
/*      */   
/*      */   public void requestScreenFocus(MouseEvent paramMouseEvent) {
/* 3352 */     requestFocus();
/*      */   }
/*      */ 
/*      */   
/*      */   public void installKeyboardHook() {
/* 3357 */     this.remconsObj.remconsInstallKeyboardHook();
/*      */   }
/*      */ 
/*      */   
/*      */   public void unInstallKeyboardHook() {
/* 3362 */     this.remconsObj.remconsUnInstallKeyboardHook();
/*      */   }
/*      */ }


/* Location:              C:\Users\MARTIN\Downloads\intgapp3_231.jar!\com\hp\ilo2\remcons\cim.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */