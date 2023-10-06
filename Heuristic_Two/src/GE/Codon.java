package GE;

import java.util.Random;

public class Codon {
    private StringBuilder codon;
    private Random random;

    public Codon(Random random){
        this.random = random;
        codon = new StringBuilder();

        //create a random 8 bits string
        for(int i = 0; i < 8; i++)
            codon.append(random.nextInt(2));
    }

    public Codon(Codon codon){
        this.random = codon.random;
        this.codon = new StringBuilder(codon.toString());
    }

    /**
     * Performs mutation by randomly flipping one of the bits
     */
    public void mutate(){
        int index = random.nextInt(8);
        char currentBit = codon.charAt(index);
        char newBit = (currentBit == '0') ? '1' : '0';
        codon.setCharAt(index, newBit);
    }

    /**
     * Copy method
     * @return deep copy of the current codon
     */
    public Codon getCopy(){
        return new Codon(this);
    }

    @Override
    public String toString(){
        return this.codon.toString();
    }

    /**
     * Convert the binary string to a denary value
     * @return denary value of binary string
     */
    public int getDenaryValue(){
        return Integer.parseInt(codon.toString(),2);
    }
    
}
