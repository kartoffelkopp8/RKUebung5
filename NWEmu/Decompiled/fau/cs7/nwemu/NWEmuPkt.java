package fau.cs7.nwemu;

public class NWEmuPkt implements Cloneable {
   public int seqnum = 0;
   public int acknum = 0;
   public int flags = 0;
   public int checksum = 0;
   public byte[] payload = new byte[20];

   public NWEmuPkt copy() {
      NWEmuPkt var1 = new NWEmuPkt();
      var1.seqnum = this.seqnum;
      var1.acknum = this.acknum;
      var1.flags = this.flags;
      var1.checksum = this.checksum;

      for (int var2 = 0; var2 < 20; var2++) {
         var1.payload[var2] = this.payload[var2];
      }

      return var1;
   }

   @Override
   protected Object clone() {
      return this.copy();
   }

   @Override
   public String toString() {
      String var1;
      if (this.payload[0] == 0) {
         var1 = "";
      } else {
         var1 = new String(this.payload);
      }

      return "{S:" + this.seqnum + "; A:" + this.acknum + "; F:" + this.flags + "; C:" + this.checksum + "; P:\"" + var1 + "\";}";
   }
}
