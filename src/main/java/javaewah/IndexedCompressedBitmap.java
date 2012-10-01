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

  public IndexedCompressedBitmap(int offset, byte[] initialData) {
    super();
  }

  public IndexedCompressedBitmap(int offset) {
    super();
  }

  public IndexedCompressedBitmap(){
    super();
  }

  public void truncateToIndex(int truncateIndex) {
    if (this.sizeinbits <= truncateIndex) //easy case, don't need to do anything.
      return;

    int nearest = 0;
    for (int i = 0; i <= this.indexIndex; i += 2) {
      if (this.index[nearest + 1] > truncateIndex) {
        break;
      } else {
        nearest = i;
      }
    }
    System.out.println( "nearest:" + nearest );
    for (int i = nearest + 2; i < indexSize; i++) {
      index[i] = 0;
    }
    int current_offset = index[nearest];
    int current_size_in_bits = index[nearest + 1];
    RunningLengthWord rlw = new RunningLengthWord(buffer, current_offset);
    int num_lits = rlw.getNumberOfLiteralWords();
    long num_running = rlw.getRunningLength();
    while ( current_offset + (num_lits * wordinbits) < this.buffer.length ) {
      if (current_size_in_bits + wordinbits * (num_lits + num_running)
            > truncateIndex) {
        System.out.println("got here");
        break;
      }
      current_size_in_bits += wordinbits * (num_lits + num_running);
      num_lits = rlw.getNumberOfLiteralWords();
      num_running = rlw.getRunningLength();
      current_offset += num_lits + 1;
      num_lits = rlw.getNumberOfLiteralWords();
      num_running = rlw.getRunningLength();
      rlw.position = current_offset;
    }
    System.out.println("rlw position: " + rlw.position);
    this.rlw.position = rlw.position;
    this.sizeinbits = truncateIndex;
    int total_remaining = truncateIndex - current_size_in_bits;
    if (total_remaining <= rlw.getRunningLength() * wordinbits) {
      rlw.setRunningLength(total_remaining / wordinbits);


      if (rlw.getRunningBit()) {
        buffer[current_offset + 1] = 0l;
        rlw.setNumberOfLiteralWords(0);
      } else {
        System.out.println("dead space");
        System.out.println("tot: " + total_remaining);
        int mod = total_remaining % wordinbits;
        System.out.println("mod: " + mod);
        if (mod != 0) {
          buffer[current_offset + 1] = ~0l >>> (wordinbits - mod);
          rlw.setNumberOfLiteralWords(1);
        } else {
          rlw.setNumberOfLiteralWords(0);
        }
      }
      this.actualsizeinwords = 1 + rlw.position + rlw.getNumberOfLiteralWords();
      return;
    }

    total_remaining -= rlw.getRunningLength() * wordinbits;
    System.out.println("total remaining: " + total_remaining);
    int mod = total_remaining % wordinbits;
    int numWholeWords =  total_remaining / wordinbits;
    if (mod == 0) {
      rlw.setNumberOfLiteralWords(numWholeWords);
    } else {
      System.out.println("offset" + (current_offset + numWholeWords));
      long mask = ~0l >>> (wordinbits - mod);
      System.out.println("mask " + mask);
      buffer[current_offset + numWholeWords + 1] &= mask;
      rlw.setNumberOfLiteralWords(numWholeWords + 1);
    }
    this.actualsizeinwords = 1 + rlw.position + rlw.getNumberOfLiteralWords();

  }

  public void reshuffle_index() {
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
    if (rlwCount == indexIndex * Math.pow(2, indexExponent)) {
      if (indexIndex == indexSize - 2) { // -2 -- one for zero-base index, one for two ints per index record
        reshuffle_index();
      }
      index[indexIndex] = this.rlw.position;
      index[indexIndex + 1] = this.sizeinbits;
      indexIndex += 2;
    }
  }
}