/**
 * This class simulates the ALU component
 * It has 3 Register class instances.
 * OP1 and OP2 represent the operant registers inside the ALU  
 * When these operant registers are set and and an operator function is call
 * the result is saved in RES register
*/
package simulator;


public class ALU {
    
    public Register OP1, OP2, RES, RES1, RESlong;

    public ALU() {

        OP1 = new Register("OP1", 20, false);
        OP2 = new Register("OP2", 20, false);
        RES = new Register("OP2", 20, false);
        RES1 = new Register("RES1", 20, false);
        RESlong = new Register("LongResult", 40, false);

    }
    
    /**
     * This function simulates ADDER circuit inside the ALU
     * Values are read from operand registers
     * Result is loaded to RES register
     * 
     */
    public void add() {
        
        RES.set( Long.toBinaryString(Integer.parseInt(OP1.get(), 2) + Integer.parseInt(OP2.get(), 2)));
    
    }
    
     /**
     * This function simulates SUBTRACTOR circuit inside the ALU
     * Values are read from operand registers
     * Result is loaded to RES register
     */
    public void sub() {
        
        RES.set( Long.toBinaryString(Integer.parseInt(OP1.get(), 2) - Integer.parseInt(OP2.get(), 2)));
    
    }
    
     /**
     * This method multiply op1 by op2. And result was stored in RESlong which
     * is a 40 bits register.
     */
    public void multiply() {
        RESlong.set(Long.toBinaryString(Integer.parseInt(OP1.get(), 2) + Integer.parseInt(OP2.get(), 2)));
    }

    public void divide() {
        RES.set(Long.toBinaryString(Integer.parseInt(OP1.get(), 2) / Integer.parseInt(OP2.get(), 2)));
        RES1.set(Long.toBinaryString(Integer.parseInt(OP1.get(), 2) % Integer.parseInt(OP2.get(), 2)));
    }

    public void and() {
        String result = "";
        for (int i = 0; i < 20; i++) {
            if (OP1.get().substring(i, i + 1).equals("1") && OP2.get().substring(i, i + 1).equals("1")) {
                result = result + "1";
            } else {
                result = result + "0";
            }
        }
        RES.set(result);
    }

    public void orr() {
        String result = "";
        for (int i = 0; i < 20; i++) {
            if (OP1.get().substring(i, i + 1).equals("0") && OP2.get().substring(i, i + 1).equals("0")) {
                result = result + "0";
            } else {
                result = result + "1";
            }
        }
        RES.set(result);
    }

    public void not() {
        String result = "";
        for (int i = 0; i < 20; i++) {
            if (OP1.get().substring(i, i + 1).equals("0")) {
                result = result + "1";
            } else {
                result = result + "0";

            }
        }
    }
    
}