/**
 * This class is the main form of the CPU simulator project
 * It serves as the User Interface that simulates the console of the CS6461 Computer
 * It hold IPL button to start computer, switches as radio buttons for providing input
 * As the output of the computer, it shows basic registers and their current situations.
 * 
 */
package simulator;

import java.awt.Color;
import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;


public class ControlPanel extends javax.swing.JFrame {
    
    boolean running;
    ImageIcon iconON = new ImageIcon("Images/ON.jpg");
    ImageIcon iconOFF = new ImageIcon("Images/OFF.jpg");
    String strSwitches;
    //this line is only for test purpose;
    //HI, Mehmet;
    
    
    // These Registers variables are instantited from Register Class
    // and used as Instruction Register, OPCODE register and ADDR register
    // other Registers (General Purpose Registers, Index Registers) are created on the GUI
    Register IR, OPCODE, ADDR, PC, CC;
    
    // This variables are instantited from File class
    // and used to store Register File and Index Register File
    File XF, RF, I, T;
    
    Memory MEMORY;
    ALU ALU;
    
     private Timer timer;
     private int counter = 3; // the duration
     private int delay = 1000; // every 1 second
    
   
              
    public ControlPanel() {
        
        initComponents();
        
        
        lblONOFF.setIcon(iconOFF);
        OPCODE = new Register("OPCODE", 6, false);
        ADDR = new Register("ADDR", 8, false);
        IR = new Register("IR", 20, false);
        PC = new Register("PC", 13, false);
        CC = new Register("CC", 4, false);
        MEMORY = new Memory();
        ALU = new ALU();
        XF =  new File();
        RF = new File();
        I = new File();
        T = new File();
    
        
          
    }
    
    ActionListener action = new ActionListener()
        {   
            @Override
            //test
            public void actionPerformed(ActionEvent event)
            {
                if(counter == 0)
                {
                    timer.stop();
                    label.setText("The time is up!");
                    
                    
                }
                else
                {
                    label.setText("Wait for " + counter + " sec");
                    counter--;
                }
            }
        };

  
     /**
     * This action when btnDeposit button clicked is used to store values directly into MEMORY
     * This makes easier to test
     * TextBoxes take address and value as decimal
     * Function converts these values to binary string and pads with 0s to until to word size (20 bit)
     * Then, these values is set in the MEMORY.
     */
    private void btnDepositActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDepositActionPerformed

        String sVal, sAddr;       
        sVal = Long.toBinaryString(Integer.parseInt(txtMemoryValue.getText()));
        sVal = String.format("%0" + (20-sVal.length())+ "d", 0) + sVal;
        
        sAddr = Long.toBinaryString(Integer.parseInt(txtMemoryAddr.getText()));
        sAddr = String.format("%0" + (20-sAddr.length())+ "d", 0) + sAddr;
       
        MEMORY.set(sVal,sAddr );
        txtMemoryAddr.setText("");
        txtMemoryValue.setText("");
     //  Background task;
        
//          Background    task = new Background();
//            task.execute();
        
      
    }//GEN-LAST:event_btnDepositActionPerformed

     /**
     * This action when btnIPL button is clicked starts the Simulator
     * The rest of the function is left for the later phases.
     */
    private void btnIPLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIPLActionPerformed
          
        if (this.running) {
          this.running = false;
          lblONOFF.setIcon(iconOFF);
            
        }
        else {
           this.running = true;
           lblONOFF.setIcon(iconON); 
        }
    }//GEN-LAST:event_btnIPLActionPerformed

    /**
     * This action when btnSingleStep button is clicked runs the instruction which 
     * is set by switches (radio button
     * After switch values are read, Instruction Register is set with this instruction to be executed
     * Then, instruction is decoded, and related function is called.
     */    
    private void btnSingleStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSingleStepActionPerformed
       
       strSwitches = GetSwitchValues();
       IR.set(strSwitches);
       String opcodeCondition = strSwitches.substring(0, 6);
        if (opcodeCondition.equals("010100") || opcodeCondition.equals("010101")
                || opcodeCondition.equals("010110") || opcodeCondition.equals("010111")
                || opcodeCondition.equals("011000") || opcodeCondition.equals("011001")) {
            DecodeArithRtoR();
        } else {
            Decode();
        }
       lblOpcode.setText(OPCODE.get());
       //ComputeEffectiveAddress(); 
                      
       switch (Integer.parseInt(OPCODE.get(),2)) {
            case 1:
               LDR();
               break;
            case 2 :
              STR();
              break;
            case 3:
               LDA();
               break;
            case 4:
               AMR();
               break;    
            case 5:
               SMR();
               break;  
            case 6:
                AIR() ;
               break;  
            case 7:
                 SIR();
                break; 
            case 10:
                 JZ();
                break; 
            case 11:
                 //JNE();
                break; 
            case 12:
                 //JCC();
                break; 
            case 13:
                  //JMP
                  break;
            case 14:
                  //JSR
                  break;
            case 41:
                 LDX();
                 break;
            case 42: 
                 STX();
            default:
                 break;
        }  
    }//GEN-LAST:event_btnSingleStepActionPerformed
     
    /**
     * This function decodes instruction to be executed and assigns the associated Register
     * according the data provided.
     */ 
    private void Decode(){
   
        OPCODE.set(strSwitches.substring(0,6));;
        XF.set(strSwitches.substring(6,8));
        RF.set(strSwitches.substring(8,10));
        I.set(strSwitches.substring(10,11));
        T.set(strSwitches.substring(11,12));
        ADDR.set(strSwitches.substring(12,20));
          
    }
    
    private void DecodeArithRtoR() {
        OPCODE.set(strSwitches.substring(0, 6));;
        XF.set(strSwitches.substring(6, 8));
        RF.set(strSwitches.substring(8, 10));
    }
      
    /**
     * This function gets the the input by reading radio button values
     * @return all values in a binary string
     */
    public String GetSwitchValues() {
        
        strSwitches = "";
        
        strSwitches += jRadioButton1.isSelected() ? "1" : "0";
        strSwitches += jRadioButton2.isSelected() ? "1" : "0";
        strSwitches += jRadioButton3.isSelected() ? "1" : "0";
        strSwitches += jRadioButton4.isSelected() ? "1" : "0";
        strSwitches += jRadioButton5.isSelected() ? "1" : "0";
        strSwitches += jRadioButton6.isSelected() ? "1" : "0";
        strSwitches += jRadioButton7.isSelected() ? "1" : "0";
        strSwitches += jRadioButton8.isSelected() ? "1" : "0";
        strSwitches += jRadioButton9.isSelected() ? "1" : "0";
        strSwitches += jRadioButton10.isSelected() ? "1" : "0";
        strSwitches += jRadioButton11.isSelected() ? "1" : "0";
        strSwitches += jRadioButton12.isSelected() ? "1" : "0";
        strSwitches += jRadioButton13.isSelected() ? "1" : "0";
        strSwitches += jRadioButton14.isSelected() ? "1" : "0";
        strSwitches += jRadioButton15.isSelected() ? "1" : "0";
        strSwitches += jRadioButton16.isSelected() ? "1" : "0";
        strSwitches += jRadioButton17.isSelected() ? "1" : "0";
        strSwitches += jRadioButton18.isSelected() ? "1" : "0";
        strSwitches += jRadioButton19.isSelected() ? "1" : "0";
        strSwitches += jRadioButton20.isSelected() ? "1" : "0";
        
        return strSwitches;
    }
    
    /**
     * This function calculates the effective address
     * It examines the values of Index Register File and Indirecting bit to set 
     * Memory Address Register with the effective address
     */

    
    public void ComputeEffectiveAddress() {
         
      String memoryAddr;
      
        
        if (XF.get().equals("00") & I.get().equals("0")) {
            
            MAR.set(ADDR.get());
            timer = new Timer (1000, action);
            timer.start ();
            MBR.set(MEMORY.get(MAR.get()));
           
           
        }
       
        else if ((XF.get().equals("01")||XF.get().equals("10")||XF.get().equals("11")) & I.get().equals("0") )  {  
            
            ALU.OP1.set(ADDR.get());
            ALU.OP2.set(SelectIndexRegister().get());
            ALU.add(); 
            MAR.set(ALU.RES.get());
            MBR.set(MEMORY.get(MAR.get()));

        }
        
        else if (XF.get().equals("00") & I.get().equals("1")) {
            MAR.set(ADDR.get());
            memoryAddr = MAR.get();
            MBR.set(MEMORY.get(memoryAddr));
            MAR.set(MBR.get());
            MBR.set(MEMORY.get(MAR.get()));
     
        }
        
        else {
            ALU.OP1.set(ADDR.get());
            ALU.OP2.set(SelectIndexRegister().get());
            ALU.add();
            MAR.set(MEMORY.get(ALU.RES.get()));
            MBR.set(MEMORY.get(MAR.get()));

        }
   
   }
    
    /**
     * This function calculates the effective address for the instructions which do not
     * use the index register 
     */
    public void computeEffectiveAddressForNonIndex(){
       
        if (I.get().equals("0")) {
            MAR.set(ADDR.get());
            MBR.set(MEMORY.get(MAR.get()));}
        else
            MAR.set(MEMORY.get(ADDR.get()));
            MBR.set(MEMORY.get(MAR.get()));
    }
      
   /**
    * Load Register From Memory
    */    
    public void LDR() {
        
        ComputeEffectiveAddress(); 
        SelectRegister().set(MBR.get());
    
    }
    
    /**
     * Store register to memory
     */
    public void STR() {
            ComputeEffectiveAddress(); 
            MEMORY.set(SelectRegister().get(), MAR.get());
    
    }
    
    /**
     * Load register with address
     */
     public void LDA() {
         ComputeEffectiveAddress();  
         SelectRegister().set(MAR.get());
         
    }
    
     /**
      * Subtract memory from register
     */
    public void SMR(){
        ComputeEffectiveAddress(); 
        ALU.OP2.set(MBR.get());
        ALU.OP1.set(SelectRegister().get());
        ALU.sub();
        SelectRegister().set(ALU.RES.get());
        
    }     
    
    /**
     * Add memory to register
     */
    public void AMR(){
        ComputeEffectiveAddress(); 
        ALU.OP2.set(MBR.get());
        ALU.OP1.set(SelectRegister().get());
        ALU.add();
        SelectRegister().set(ALU.RES.get());
         
  }     
    
    /**
     * Add immediate to register
     */
     public void AIR() {
          ComputeEffectiveAddress(); 
          ALU.OP2.set(ADDR.get());
    	  ALU.OP1.set(SelectRegister().get());
    	  ALU.add();
    	  SelectRegister().set (ALU.RES.get());
    
    }
    
   /**
    * Subtract immediate from register
    */
    public void SIR(){
          ComputeEffectiveAddress();  
    	  ALU.OP2.set(ADDR.get());
    	  ALU.OP1.set(SelectRegister().get());
    	  ALU.sub();
    	  SelectRegister().set (ALU.RES.get());
    }
    
    /**
     * Load index register from memory
     */
    public void LDX(){
        computeEffectiveAddressForNonIndex();
        SelectIndexRegister().set(MAR.get());
    
    }
    
    /**
     * Store index register to memory
     */
    public void STX(){
        computeEffectiveAddressForNonIndex();
        MEMORY.set(SelectIndexRegister().get(), MAR.get());
    
    }
    
    /**
     * Jump if zero
     */
    public void JZ(){
        
        ComputeEffectiveAddress();  
       
        if  (SelectRegister().get().equals("0")) {
                        
            if (I.get().equals("0")) 
                PC.set(MAR.get());
            else
                PC.set(MEMORY.get(MAR.get()));
        }
        else
            // PC = PC + 1:
            ALU.OP2.set(PC.get());
    	    ALU.OP1.set("1");
    	    ALU.add();
            PC.set (ALU.RES.get());
            
      }
   
    
    /**
     * Jump if not equal
     */
     public void JNE(){
         
        ComputeEffectiveAddress();  
       
        if  (!SelectRegister().get().equals("0")) {
                        
            if (I.get().equals("0")) 
                PC.set(MAR.get());
            else
                PC.set(MEMORY.get(MAR.get()));
        }
        else
            // PC = PC + 1:
            ALU.OP2.set(PC.get());
    	    ALU.OP1.set("1");
    	    ALU.add();
            PC.set (ALU.RES.get());
   }
     
     /**
     * Jump if condition true
     */
     public void JCC(){
         
        ComputeEffectiveAddress();
        
        CC.set(RF.get());
       
        if  (CC.get().charAt(0) == '1') { // if cc bit = 1
                        
           if (I.get().equals("0")) 
                PC.set(MAR.get());
            else
                PC.set(MEMORY.get(MAR.get()));
        }
        else
            // PC = PC + 1:
            ALU.OP2.set(PC.get());
    	    ALU.OP1.set("1");
    	    ALU.add();
            PC.set (ALU.RES.get());
      }
     
     /**
     * Unconditional Jump to Address
     */
     public void JMP(){
         
        ComputeEffectiveAddress();
                   
        if (I.get().equals("0")) 
            PC.set(MAR.get());
        else
            PC.set(MEMORY.get(MAR.get()));
    }
     
     /**
     * Jump and Save Return Address
     */
     public void JSR(){
        
        ComputeEffectiveAddress();
        // PC = PC + 1:
        ALU.OP2.set(PC.get());
        ALU.OP1.set("1");
        ALU.add();
        RF.set("11"); // select Register 3 by seting Register File
        SelectRegister().set(ALU.RES.get()); // R3 <- PC+1
        if (I.get().equals("1")) 
            PC.set(MEMORY.get(MAR.get())); // PC <- c(EA)
       // R0 should contain pointer to args. Arguments list should end with -17777 ???????
       
    }
     
     /**
     * Return From Subroutine w/ return code as Immed portion (optional)
     * stored in R0's address field
     */
     public void RFS(){
        RF.set("00"); //select Register 0 by seting Register File
        SelectRegister().set(ADDR.get()); // R0 <- Immed
        RF.set("11"); //select Register 3 by seting Register File
        PC.set(SelectRegister().get()); // PC <- c(R3)
               
    }
     
     /**
     * Subtract One And Branch
     */
     public void SOB(){
        
        ALU.OP2.set(SelectRegister().get());
        ALU.OP1.set("1");
        ALU.sub();
        SelectRegister().set(ALU.RES.get());
        
                
       
    }
     
     /**
     * Multiple register by register. Rx contains the high order bits. Rx+1
     * contains the low order bits of the result.
     */
    public void MLT() {
        if ((XF.equals("00") || XF.equals("10")) && (RF.equals("00") || RF.equals("10"))) {
            ALU.OP1.set(SelectRegister().get());
            String RFcopy = RF.get();
            RF.set(XF.get());
            ALU.OP2.set(SelectRegister().get());
            ALU.multiply();
            RF.set(RFcopy);
            SelectRegister().set(ALU.RESlong.get().substring(0, 20));
            SelectNextRegister().set(ALU.RESlong.get().substring(20, 40));
        }
    }

    /**
     * Divide register by register. Rx contains the quotient;Rx+1 contains the
     * remainder.
     */
    public void DVD() {
        if ((XF.equals("00") || XF.equals("10")) && (RF.equals("00") || RF.equals("10"))) {
            ALU.OP1.set(SelectRegister().get());
            String RFcopy = RF.get();
            RF.set(XF.get());
            ALU.OP2.set(SelectRegister().get());
            ALU.divide();
            RF.set(RFcopy);
            SelectRegister().set(ALU.RES.get());
            SelectNextRegister().set(ALU.RES1.get());
        }

    }

    /**
     * This method is to test the equality of register and register.
     */
    public void TRR() {
        ALU.OP1.set(SelectRegister().get());
        RF.set(XF.get());
        ALU.OP2.set(SelectRegister().get());
        if (ALU.OP1.get().equals(ALU.OP2.get())) {
            //set cc(4) to 1
        } else {
            // set cc(4) to 0
        }
    }

    /**
     * Logical and of register and register
     */
    public void AND() {
        ALU.OP1.set(SelectRegister().get());
        String RFcopy = RF.get();

        RF.set(XF.get());
        ALU.OP2.set(SelectRegister().get());
        ALU.and();
        RF.set(RFcopy);
        SelectRegister().set(ALU.RES.get());

    }

    public void ORR() {
        ALU.OP1.set(SelectRegister().get());
        String RFcopy = RF.get();
        RF.set(XF.get());
        ALU.OP2.set(SelectRegister().get());
        ALU.orr();
        RF.set(RFcopy);
        SelectRegister().set(ALU.RES.get());
    }

    public void not() {
        ALU.OP1.set(SelectRegister().get());
        ALU.not();
        SelectRegister().set(ALU.RES.get());
    }
  
  /**
   * This function select General Purpose Register 
   * according to chosen Register File
   * @return selected register
   */
    private Register SelectRegister() {
        
        Register Rselected;
		
         switch (RF.Index) {
            case 0:
                Rselected = GPR0;
                break;
            case 1:
                Rselected = GPR1;
                break;
            case 2:
                Rselected = GPR2;
                break;   
             case 3:
                Rselected = GPR3;
                break;
             default:
                 Rselected = null;
                 break;
         }
         
         return Rselected;
         
 }
    
    private Register SelectNextRegister() {

        Register Rselected;

        switch (RF.Index) {
            case 0:
                Rselected = GPR1;
                break;

            case 2:
                Rselected = GPR3;
                break;

            default:
                Rselected = null;
                break;
        }

        return Rselected;

    }
    /**
   * This function select Index Register 
   * according to chosen Index Register File
   * @return selected index register
   */
     private Register SelectIndexRegister() {
        
        Register Rselected;
		
         switch (XF.Index) {
            case 1:
                Rselected = IX1;
                break;
            case 2:
                Rselected = IX2;
                break;
            case 3:
                Rselected = IX3;
                break;   
            default:
                 Rselected = null;
                 break;
         }
         
         return Rselected;
         
 }
     
    
//      public class Background extends SwingWorker <String, Object>{
//       //@Override
//       public String doInBackground() throws InterruptedException {
//        
//        
//           return null;
//   
//        }
//        protected void done() {
//           try {
//              
//          //  for (int i=1; i<5; i++) {
//               // txtMemoryAddr.setText(txtMemoryAddr.getText() + "A");
//                Thread.sleep(3000);
//                txtMemoryAddr.setText("a");
//            //}
//         
//           } catch (InterruptedException ex) {
//               Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
//           }
//        }
//    }
//    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ControlPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ControlPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ControlPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ControlPanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
       /* Create and display the form */
        SwingUtilities.invokeLater(new Runnable() {
       // java.awt.EventQueue.invokeLater(new Runnable() {
             @Override
            public void run() {
                new ControlPanel().setVisible(true);
            }
        });
    }
    
 @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jFrame2 = new javax.swing.JFrame();
        jFrame3 = new javax.swing.JFrame();
        jFrame4 = new javax.swing.JFrame();
        jFrame5 = new javax.swing.JFrame();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        btnIPL = new javax.swing.JToggleButton();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        lblOpcode = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        GPR0 = new simulator.Register("R0", 20, true);
        GPR1 = new simulator.Register("R1", 20, true);
        GPR2 = new simulator.Register("R2", 20, true);
        GPR3 = new simulator.Register("R3", 20, true);
        IX1 = new simulator.Register("IX1", 13, true);
        IX2 = new simulator.Register("IX2", 13, true);
        IX3 = new simulator.Register("IX3", 13, true);
        MBR = new simulator.Register("MBR", 20, true);
        MAR = new simulator.Register("MAR", 20, true);
        jPanel1 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jRadioButton11 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        jRadioButton13 = new javax.swing.JRadioButton();
        jRadioButton14 = new javax.swing.JRadioButton();
        jRadioButton15 = new javax.swing.JRadioButton();
        jRadioButton16 = new javax.swing.JRadioButton();
        jRadioButton17 = new javax.swing.JRadioButton();
        jRadioButton18 = new javax.swing.JRadioButton();
        jRadioButton19 = new javax.swing.JRadioButton();
        jRadioButton20 = new javax.swing.JRadioButton();
        btnSingleStep = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnDeposit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtMemoryAddr = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtMemoryValue = new javax.swing.JTextField();
        lblONOFF = new javax.swing.JLabel();
        label = new javax.swing.JLabel();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame3Layout = new javax.swing.GroupLayout(jFrame3.getContentPane());
        jFrame3.getContentPane().setLayout(jFrame3Layout);
        jFrame3Layout.setHorizontalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame3Layout.setVerticalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame4Layout = new javax.swing.GroupLayout(jFrame4.getContentPane());
        jFrame4.getContentPane().setLayout(jFrame4Layout);
        jFrame4Layout.setHorizontalGroup(
            jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame4Layout.setVerticalGroup(
            jFrame4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jFrame5Layout = new javax.swing.GroupLayout(jFrame5.getContentPane());
        jFrame5.getContentPane().setLayout(jFrame5Layout);
        jFrame5Layout.setHorizontalGroup(
            jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame5Layout.setVerticalGroup(
            jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnIPL.setText("IPL");
        btnIPL.setToolTipText("");
        btnIPL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIPLActionPerformed(evt);
            }
        });

        lblOpcode.setText("000000");

        jLabel13.setText("OPCODE:");

        GPR0.setBitLength(20);
        GPR0.setOpaque(false);
        GPR0.setRegName("R0");

        GPR1.setRegName("R1");

        GPR2.setRegName("R2");

        GPR3.setRegName("R3");

        IX1.setRegName("IX1");

        IX2.setRegName("IX2");

        IX3.setRegName("IX3");

        MBR.setRegName("MBR");

        MAR.setRegName("MAR");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setToolTipText("");
        jPanel1.setName("aa"); // NOI18N

        jRadioButton1.setText("1");
        jRadioButton1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jRadioButton1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton2.setText("2");
        jRadioButton2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton3.setText("3");
        jRadioButton3.setToolTipText("");
        jRadioButton3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton4.setText("4");
        jRadioButton4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton5.setText("5");
        jRadioButton5.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton6.setText("6");
        jRadioButton6.setToolTipText("");
        jRadioButton6.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton7.setText("7");
        jRadioButton7.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton8.setText("8");
        jRadioButton8.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton9.setText("9");
        jRadioButton9.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton10.setText("10");
        jRadioButton10.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton11.setText("11");
        jRadioButton11.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton12.setText("12");
        jRadioButton12.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton13.setText("13");
        jRadioButton13.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton14.setText("14");
        jRadioButton14.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton15.setText("15");
        jRadioButton15.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton16.setText("16");
        jRadioButton16.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton17.setText("17");
        jRadioButton17.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton18.setText("18");
        jRadioButton18.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton19.setText("19");
        jRadioButton19.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jRadioButton20.setText("20");
        jRadioButton20.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        btnSingleStep.setText("Run Single Step");
        btnSingleStep.setToolTipText("");
        btnSingleStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSingleStepActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jRadioButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSingleStep, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButton14)
                        .addGap(14, 14, 14)
                        .addComponent(jRadioButton15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton20)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton9)
                    .addComponent(jRadioButton10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jRadioButton13)
                        .addComponent(jRadioButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jRadioButton16)
                        .addComponent(jRadioButton17)
                        .addComponent(jRadioButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jRadioButton19)
                        .addComponent(jRadioButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jRadioButton12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jRadioButton11, javax.swing.GroupLayout.Alignment.LEADING)))
                .addGap(5, 5, 5)
                .addComponent(btnSingleStep))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnDeposit.setText("Deposit on Memory");
        btnDeposit.setToolTipText("");
        btnDeposit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDepositActionPerformed(evt);
            }
        });

        jLabel1.setText(" Address");

        jLabel2.setText("Value");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDeposit, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txtMemoryAddr, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtMemoryValue, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMemoryAddr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMemoryValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDeposit)
                .addContainerGap())
        );

        label.setText("label");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnIPL, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(GPR0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(GPR1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(GPR2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(GPR3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(IX1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(IX2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(IX3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(MBR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(MAR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addComponent(jDesktopPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblONOFF, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(74, 74, 74)
                                        .addComponent(label))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblOpcode)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnIPL, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblONOFF, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(196, 196, 196)
                        .addComponent(jDesktopPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblOpcode)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GPR0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GPR1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(GPR2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GPR3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(IX1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(IX2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(IX3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(MBR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(8, 8, 8)
                .addComponent(MAR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

  
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private simulator.Register GPR0;
    private simulator.Register GPR1;
    private simulator.Register GPR2;
    private simulator.Register GPR3;
    private simulator.Register IX1;
    private simulator.Register IX2;
    private simulator.Register IX3;
    private simulator.Register MAR;
    private simulator.Register MBR;
    private javax.swing.JButton btnDeposit;
    private javax.swing.JToggleButton btnIPL;
    private javax.swing.JButton btnSingleStep;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JFrame jFrame3;
    private javax.swing.JFrame jFrame4;
    private javax.swing.JFrame jFrame5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton12;
    private javax.swing.JRadioButton jRadioButton13;
    private javax.swing.JRadioButton jRadioButton14;
    private javax.swing.JRadioButton jRadioButton15;
    private javax.swing.JRadioButton jRadioButton16;
    private javax.swing.JRadioButton jRadioButton17;
    private javax.swing.JRadioButton jRadioButton18;
    private javax.swing.JRadioButton jRadioButton19;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton20;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JLabel label;
    private javax.swing.JLabel lblONOFF;
    private javax.swing.JLabel lblOpcode;
    private javax.swing.JTextField txtMemoryAddr;
    private javax.swing.JTextField txtMemoryValue;
    // End of variables declaration//GEN-END:variables
}
