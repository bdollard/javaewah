package javaewah;
import org.junit.Test;
import java.util.*;
import java.io.*;
import junit.framework.Assert;

public class IndexedCompressedBitmapTest {


  /**
   * Test bitmap truncation functionality
   */
  @Test
  public void truncateTest() {
    System.out.println("Testing truncate functionality");
    final int totalNumBits = 32768;
    final double odds = 0.04;
    Random rand = new Random(323232323);
    int[] truncatePoints = {0, 7, 1232, 12323, 32766, 32767};
    int[] bitsSetAtTruncatePoints = new int[truncatePoints.length];
    int numBitsSet = 0;
    int pointsIndex = 0;
    IndexedCompressedBitmap cBitMap = new IndexedCompressedBitmap();
    for (int i = 0; i < totalNumBits; i++) {
      if (rand.nextDouble() < odds) {
        cBitMap.set(i);
        numBitsSet++;
      }
      if (i == truncatePoints[pointsIndex]) {
        bitsSetAtTruncatePoints[pointsIndex] = numBitsSet;
        pointsIndex++;
      }
    }
    Assert.assertEquals(cBitMap.cardinality(),numBitsSet);
    for (int i = 0; i < truncatePoints.length; i++) {
      try { 
      System.out.println("index is: " + i);
      IndexedCompressedBitmap bm = (IndexedCompressedBitmap) cBitMap.clone(); 
      bm.truncateToIndex(truncatePoints[i]);
      System.out.println(bm.toDebugString());
      Assert.assertEquals(bm.cardinality(), bitsSetAtTruncatePoints[i]);
      Assert.assertTrue(bm.set(truncatePoints[i] + 1));
      Assert.assertEquals(bm.cardinality(), (bitsSetAtTruncatePoints[i] + 1));
      } catch (Exception e) {}

    }
    
    
  }

}