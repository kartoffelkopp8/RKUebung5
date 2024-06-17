package fau.cs7.nwemu;

public class NWEmuMsg implements Cloneable {
   public byte[] data = new byte[20];

   public NWEmuMsg copy() {
      NWEmuMsg var1 = new NWEmuMsg();

      for (int var2 = 0; var2 < 20; var2++) {
         var1.data[var2] = this.data[var2];
      }

      return var1;
   }

   protected Object Clone() {
      return this.copy();
   }

   @Override
   public String toString() {
      return new String(this.data);
   }
}
