package fau.cs7.simcore;

public class RandMarZam87 implements RandomStream {
   double[] u = new double[97];
   double c;
   double cd;
   double cm;
   int i97;
   int j97;
   boolean test = false;
   private static final long maxij = 30081L;
   private static final long maxkl = 31328L;
   private static final long maxijkl = 942438977L;
   private static long ijkl = 281960388L;

   public void init() {
      ijkl %= 942438977L;
      if (ijkl < 0L) {
         ijkl += 942438978L;
      }

      int var12 = (int)(ijkl % 30082L);
      if (var12 < 0) {
         var12 += 30082;
      }

      int var13 = (int)(ijkl / 30082L % 31329L);
      if (var13 < 0) {
         var13 += 31329;
      }

      int var6 = var12 / 177 % 177 + 2;
      int var7 = var12 % 177 + 2;
      int var8 = var13 / 169 % 178 + 1;
      int var9 = var13 % 169;

      for (int var5 = 0; var5 < 97; var5++) {
         double var1 = 0.0;
         double var3 = 0.5;

         for (int var10 = 0; var10 < 24; var10++) {
            int var11 = var6 * var7 % 179 * var8 % 179;
            var6 = var7;
            var7 = var8;
            var9 = (53 * var9 + 1) % 169;
            if (var9 * var11 % 64 >= 32) {
               var1 += var3;
            }

            var3 *= 0.5;
         }

         this.u[var5] = var1;
      }

      this.c = 0.021602869F;
      this.cd = 0.45623308F;
      this.cm = 0.9999998F;
      this.i97 = 97;
      this.j97 = 33;
      this.test = true;
   }

   public void init(long var1) {
      ijkl = var1;
      this.init();
   }

   public RandMarZam87() {
      this.init();
      ijkl += 30082L;
   }

   public RandMarZam87(int var1, int var2) {
      ijkl = ijkl = (long)var2 * 30082L + (long)var1;
      this.init();
   }

   public RandMarZam87(int var1) {
      this.init((long)var1);
   }

   public RandMarZam87(long var1) {
      this.init(var1);
   }

   public void resetStartStream() {
      ijkl = 281960388L;
      this.init();
   }

   public void resetStartSubstream() {
      this.init();
   }

   public void resetNextSubstream() {
      ijkl += 30082L;
      this.init();
   }

   public void randomizeTimer() {
      long var1 = System.currentTimeMillis();
      this.init(ijkl + var1);
   }

   @Override
   public double randU01() {
      if (!this.test) {
         this.resetStartStream();
      }

      double var1 = this.u[this.i97 - 1] - this.u[this.j97 - 1];
      if (var1 <= 0.0) {
         var1++;
      }

      this.u[this.i97 - 1] = var1;
      this.i97--;
      if (this.i97 == 0) {
         this.i97 = 97;
      }

      this.j97--;
      if (this.j97 == 0) {
         this.j97 = 97;
      }

      this.c = this.c - this.cd;
      if (this.c < 0.0) {
         this.c = this.c + this.cm;
      }

      var1 -= this.c;
      if (var1 < 0.0) {
         var1++;
      }

      return var1;
   }
}
