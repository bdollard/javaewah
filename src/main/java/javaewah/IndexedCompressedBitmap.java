package javaewah;
import java.util.*;
import java.io.*;

public class IndexedCompressedBitmap extends EWAHCompressedBitmap {
  int indexIndex = 0;
  int indexExponent = 0;
  int rlwCount = 0;
  int indexSize = 64;   // 17 to 32 items in index, I should experiment later with 
                        // different sizes
  int[] index = new int[indexSize];
  
  public IndexedCompressedBitmap(int offset) {
    
  }

  public IndexedCompressedBitmap(int offset, byte[] initialData) {
    
  }
  
  public void truncateToIndex(int truncateIndex) {
    if (this.sizeinbits <= i) //easy case, don't need to do anything.
      return;

    int nearest = 0;
    for (int i = 0; i <= this.indexIndex; i += 2) {
      if (this.index[nearest + 1] > truncateIndex)
        break;
      } else {
        nearest = i;
      }
    }
    int current_offset = index[nearest];
    int current_size_in_bits = index[nearest + 1];
    RunningLengthWord rlw = new RunningLengthWord(buffer, current_offset);
    int num_lits = rlw.getNumberOfLiteralWords();
    int num_running = rlw.getRunningLenghth();
    while ( current_offset + (num_lits * wordin_bits) < this.buffer.length ) {
      current_size_in_bits += wordinbits * (num_lits + num_running);
      current_offset += num_lits * word_in_bits;
      if (current_size_in_bits > trucateIndex)
        break;

      rlw.position = current_offset;
    }
    this.rlw.position = rlw.position;
    this.rlw.
  }
  
  public void reshuffle_index() {
    int i = 0;
    for (int i = 0; i < this.indexSize/2; i+=2) {
      this.index[i] = this.index[i * 2];
    }
    this.indexIndex = this.indexSize / 2;
    this.indexExponent += 1;
  }
  
  @Override
  public void new_rlw() {
  //this code is pretty non-reentrant. but then, so is the rest of this class
    rlwCount++;
    super.new_rlw();
    if ( rlwCount == indexIndex * (2 ** indexExponent) ) {
      if (indexIndex == indexSize - 2) { // -2 -- one for zero-base index, one for two ints per index record
        reshuffle_index();
      }
      index[indexIndex] = this.rlw.position;
      index[indexIndex + 1] = this.sizeinbits;
      indexIndex += 2;
    }
  }
}