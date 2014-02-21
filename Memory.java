/** * This class simulates the Memory * Memory is assumed 2-D array containing 20 banks of 8192 words * First index of the Memory array delegates the address value * Second index of the Memory array holds 20-bit content * It has 2 methods.*/package simulator;public class Memory {        public int[][] MEMORY;    private Cache L1Cache;           public Memory() {            MEMORY = new int[2048][20];        L1Cache = new Cache();                }        /**     * This method returns the content of Memory at the asked address     * if available from the Cache, otherwise Read-Miss happens     * Address value is taken as string of 0s and 1s but in order to fetch      * from memory it is read bit by bit     * @param strAddr The address of Memory whose content is needed      * @return the content as string. Since content is stored as 20 bits in the Memory     * it is merged and returned as a whole      */    public String get(String strAddr) {                  String s = "";                  s = L1Cache.Read(Integer.parseInt(strAddr, 2));                  return s;     }         /**     * This method returns the content of Memory at the asked address     * directly from the memory     * It is used by cache if Read-Miss happens      * Address value is taken as string of 0s and 1s but in order to fetch      * from memory it is read bit by bit     * @param strAddr The address of Memory whose content is needed      * @return the content as string. Since content is stored as 20 bits in the Memory     * it is merged and returned as a whole      */    public String getDirect(String strAddr) {                  String s = "";                        for(int i=0;i<20;i++) {           s += MEMORY[Integer.parseInt(strAddr, 2)][i];         }                 return s;     }        /**     * This method saves a content in the Memory address      * @param strValue The content which will be saved in the Memory.     * This value is taken as string of 0s and 1s but it is saved bit by bit     * @param strAddr The content is saved in this address in the Memory array     */    public void set(String strValue, String strAddr) {                 strValue = (strValue.length()!=20) ? String.format("%0"+ (20-strValue.length())+ "d", 0)+strValue : strValue;                     for(int i=0;i<20;i++) {             MEMORY[Integer.parseInt(strAddr, 2)][i] = Integer.parseInt(Character.toString(strValue.charAt(i)));         }          }                }