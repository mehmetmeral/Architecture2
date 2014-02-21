package simulator;

public class Cache {
    
    private int[][] CACHE_DATA;
    
    
    public Cache() {
    
       // Cach ise 16 blocks long and each line contain 4 words
       // First 1 Bit Dirty Bit, 1  Valid Bit, 3 bits Index, 2 bits offset, 13 bit TAG, 4*20 word
       // 1+1+3+2+13+80=100 bits 
       //0: VALID BIT
       //1: DIRTY BIT
       //2-4 INDEX BITS
       //5-6 OFFSET BITS
       //7-19 TAG
       //20-39 WORD 1
       //40-59 WORD 2
       //60-79 WORD 3
       //80-99 WORD 4
       
       CACHE_DATA = new int[16][100];
   
    }
    
   public String Read(int Addr) {
        
        String WORD="";
        Integer OffSet, TAG;
        
        boolean OntheCache = false;
        
        OffSet = Addr%4;
        TAG = Addr - OffSet; // block's TAG: we have first block's physical address only
        String sOffSet = ""; //00,01,10,11
        sOffSet = (OffSet<=1) ? "0" + String.format("%s", OffSet) : Long.toBinaryString(OffSet);
        int block=0;
        
        // check if it is in the CACHE
        for(int i=0; i<=15; i++) {
            //check block by block
            if (GetBlockTag(i) == TAG){ 
                //Set offSet bits to select the word asked
                CACHE_DATA[i][5] = Integer.parseInt(sOffSet.substring(0,1)); // first bit of offset 01 -> 1
                CACHE_DATA[i][6] = Integer.parseInt(sOffSet.substring(1,2)); // second bit of offset 01 -> 0
                System.out.println("Read Hit!");
                OntheCache = true;
                block = i; // found in the ith block
                break;
            }
        }
        
        if (!OntheCache) {
            System.out.println("Read Miss!");
            // fetch words from physical memory
            // fetch starting from first block for example:
            // asked address is 2046: fetch from 2044, 2045, 2046, 2047; 2046-(2046mod4)=2044
            Fetch(TAG); 
            block = TAG%16; 
            //Select the Word asked by setting offset bits
            CACHE_DATA[block][5] = Integer.parseInt(sOffSet.substring(0,1)); // second bit of offset 01 -> 0
            CACHE_DATA[block][6] = Integer.parseInt(sOffSet.substring(1,2)); // first bit of offset 01 -> 1
                      
        }
        
        WORD = GetWordFromBlock(block);
        
        return WORD;
    }
   
    public void Fetch(int TAG) {
        
       String DATA="";          
       int block;
       String sTag = "";
          
        // Insert new block into the cache
        block = TAG%16; 
        //Since Direct Mapping is used, after read-miss new data will be replaced by Mod 16
        // new block is placed in modulo 16 of the asked address
        // 2044 mod 16 = 12 placed 12. cache block
        SetDataBitsIntheBlock("0000000", block, 0, 6); //valid-dirty-index-offset bits
        sTag = Long.toBinaryString(TAG); //TAG (physical address) of the first word in the cache
        sTag = String.format("%0" + (13- sTag.length()) + "d", 0) + sTag;
        SetDataBitsIntheBlock(sTag, block, 7, 19); //Tag bits

       int indexStart=20, indexEnd=39;
            
       for (int i=1;i<=4;i++) {
           DATA = ControlPanel.MEMORY.getDirect(Long.toBinaryString(TAG+i-1));
           SetDataBitsIntheBlock(DATA,block, i*indexStart, indexEnd+((i-1)*indexStart));
       }
             
    }
    
     
    private int GetBlockTag(int block) {
        
        String s="";
   
        s = TakeDataBitsFromtheBlock(block, 7, 19);
               
        return Integer.parseInt(s, 2);
   
    }
    
    private String GetWordFromBlock(int block) {
        String sData="";
        int indexStart=0, indexEnd=0;
        
        int OffSet = (1 * CACHE_DATA[block][6]) + (2 * CACHE_DATA[block][5]); //select data from the block
       //20-39 WORD 1
       //40-59 WORD 2
       //60-79 WORD 3
       //80-99 WORD 4
        switch(OffSet) {
            case 0:
                indexStart = 20;
                indexEnd = 39;
                break;
            case 1:
                indexStart = 40;
                indexEnd = 59;
                break;
            case 2:
                indexStart = 60;
                indexEnd = 79;
                break;
            case 3:
                indexStart = 80;
                indexEnd = 99;
                break;
         }
        
    
        return TakeDataBitsFromtheBlock(block, indexStart, indexEnd);
    
    }
    
    private void UpdateWordInBlock(int block, String DATA) {
       ;
        int indexStart=0, indexEnd=0;
        
        int OffSet = (1 * CACHE_DATA[block][6]) + (2 * CACHE_DATA[block][5]); //select data from the block
       //20-39 WORD 1
       //40-59 WORD 2
       //60-79 WORD 3
       //80-99 WORD 4
        switch(OffSet) {
            case 0:
                indexStart = 20;
                indexEnd = 39;
                break;
            case 1:
                indexStart = 40;
                indexEnd = 59;
                break;
            case 2:
                indexStart = 60;
                indexEnd = 79;
                break;
            case 3:
                indexStart = 80;
                indexEnd = 99;
                break;
         }
        
        SetDataBitsIntheBlock(DATA, block, indexStart, indexEnd);
        
    
    }
    
      
    private void SetDataBitsIntheBlock(String mData, int block, int indexStart, int indexEnd) {
        
        int j=0;
        for(int i=indexStart; i<=indexEnd; i++) {
             CACHE_DATA[block][i] = Integer.parseInt(mData.substring(j, j+1));
             j++;
        }
    }
    
    private String TakeDataBitsFromtheBlock(int block, int indexStart, int indexEnd) {
        
        String sData = "";
        
        for(int i=indexStart; i<=indexEnd; i++)
             sData += CACHE_DATA[block][i];
        
        return sData;
    }
    
    public void Write(String WORD, int Addr) {
        
        int TAG=0;
        String sAddr = "";
        Integer OffSet;
        boolean OntheCache = false;
        
        OffSet = Addr%4; //place the word will be put on the block 
        TAG = Addr - OffSet; // block's physical address
        String sOffSet = ""; //00,01,10,11
        sOffSet = (OffSet<=1) ? "0" + String.format("%s", OffSet) : Long.toBinaryString(OffSet);
        int block=0;
        
        // check if it is in the CACHE
        for(int i=0; i<=15; i++) {
            //check block by block
            if (GetBlockTag(i) == TAG){ 
                // we are using Write Back method
                // if data on the cache, we will not update the physical address right away
                // instead we set dirty bit and update it while we are swaping cache blocks from the memory
                CACHE_DATA[i][1] = 1; // set Dirty bit, block updated
                //Set offSet bits to select the word asked
                CACHE_DATA[i][5] = Integer.parseInt(sOffSet.substring(0,1)); // first bit of offset 01 -> 1
                CACHE_DATA[i][6] = Integer.parseInt(sOffSet.substring(1,2)); // second bit of offset 01 -> 0
                UpdateWordInBlock(i,WORD);
                System.out.println("Write Hit!");
                OntheCache = true;
                break;
            }
        }
        
        if (!OntheCache) {
            System.out.println("Write Miss!");
            sAddr = Long.toBinaryString(Addr);
            sAddr = String.format("%0" + (20-sAddr.length())+ "d", 0) + sAddr;
            //data is not on the cache so write to memory. We are using  write allocete method
            //data block is also being fetched
            ControlPanel.MEMORY.setDirect(WORD, sAddr);
            Fetch(TAG); 
           
        }
        
              
        
    }
    
    public void WriteBack() {
    }
    
    
    
    
    
}
