/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ 
/*     */ 
/*     */ public class OkCancelDialog
/*     */   extends JDialog
/*     */   implements ActionListener, WindowListener
/*     */ {
/*     */   JPanel mainPanel;
/*     */   JLabel txt;
/*     */   JButton ok;
/*     */   JButton cancel;
/*     */   boolean rc;
/*     */   
/*     */   public OkCancelDialog(JFrame paramJFrame, String paramString) {
/*  29 */     super(paramJFrame, "Notice!", true);
/*  30 */     ui_init(paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   public OkCancelDialog(String paramString, boolean paramBoolean) {
/*  35 */     super(new JFrame(), "Notice!", paramBoolean);
/*  36 */     ui_init(paramString);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void ui_init(String paramString) {
/*  41 */     this.txt = new JLabel(paramString);
/*     */ 
/*     */     
/*  44 */     this.mainPanel = new JPanel();
/*  45 */     this.mainPanel.setBorder(BorderFactory.createEtchedBorder(0));
/*  46 */     this.mainPanel.add(this.txt);
/*  47 */     this.mainPanel.setPreferredSize(this.mainPanel.getPreferredSize());
/*     */     
/*  49 */     this.ok = new JButton("    Ok    ");
/*  50 */     this.ok.addActionListener(this);
/*     */     
/*  52 */     this.cancel = new JButton("Cancel");
/*  53 */     this.cancel.addActionListener(this);
/*     */     
/*  55 */     GridBagLayout gridBagLayout = new GridBagLayout();
/*  56 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/*     */     
/*  58 */     setLayout(gridBagLayout);
/*     */     
/*  60 */     gridBagConstraints.fill = 2;
/*  61 */     gridBagConstraints.anchor = 17;
/*  62 */     gridBagConstraints.gridx = 0;
/*  63 */     gridBagConstraints.gridy = 0;
/*  64 */     add(this.mainPanel, gridBagConstraints);
/*     */     
/*  66 */     JPanel jPanel = new JPanel();
/*  67 */     jPanel.setLayout(new FlowLayout(2));
/*  68 */     jPanel.add(this.ok);
/*  69 */     jPanel.add(this.cancel);
/*     */     
/*  71 */     gridBagConstraints.fill = 0;
/*  72 */     gridBagConstraints.anchor = 13;
/*  73 */     gridBagConstraints.gridx = 0;
/*  74 */     gridBagConstraints.gridy = 1;
/*  75 */     gridBagConstraints.gridwidth = 1;
/*     */     
/*  77 */     add(jPanel, gridBagConstraints);
/*  78 */     addWindowListener(this);
/*     */     
/*  80 */     setSize((this.mainPanel.getPreferredSize()).width + 40, (this.txt.getPreferredSize()).height + 100);
/*  81 */     setResizable(false);
/*  82 */     setLocationRelativeTo(null);
/*  83 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/*  88 */     if (paramActionEvent.getSource() == this.ok) {
/*  89 */       dispose();
/*  90 */       this.rc = true;
/*     */     }
/*  92 */     else if (paramActionEvent.getSource() == this.cancel) {
/*  93 */       dispose();
/*  94 */       this.rc = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean result() {
/* 100 */     return this.rc;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(String paramString) {
/* 106 */     this.txt.repaint();
/*     */   }
/*     */ 
/*     */   
/*     */   public void windowClosing(WindowEvent paramWindowEvent) {
/* 111 */     dispose();
/* 112 */     this.rc = false;
/*     */   }
/*     */   
/*     */   public void windowOpened(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowDeiconified(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowIconified(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowActivated(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowClosed(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowDeactivated(WindowEvent paramWindowEvent) {}
/*     */ }


/* Location:              C:\Users\MARTIN\Downloads\intgapp3_231.jar!\com\hp\ilo2\remcons\OkCancelDialog.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */