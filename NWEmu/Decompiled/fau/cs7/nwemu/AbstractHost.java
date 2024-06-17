package fau.cs7.nwemu;

public class AbstractHost {
   static long hostcount = 0L;
   String descriptor;
   NWEmu EmulatorRef;

   public Boolean output(NWEmuMsg var1) {
      System.out.println(this.descriptor + ".output() not implemented, data = " + new String(var1.data));
      return false;
   }

   public void input(NWEmuPkt var1) {
      System.out.println(this.descriptor + ".input() not implemented, payload = " + new String(var1.payload));
   }

   public void init() {
      System.out.println(this.descriptor + ".init() not implemented");
   }

   public void timerInterrupt() {
      System.out.println(this.descriptor + ".timerInterrupt() not implemented");
   }

   public AbstractHost() {
      this.descriptor = this.getClass().getName() + hostcount++;
   }

   public AbstractHost(String var1) {
      this.descriptor = var1;
      hostcount++;
   }

   public final String getDescriptor() {
      return this.descriptor;
   }

   public final void registerEmu(NWEmu var1) {
      this.EmulatorRef = var1;
   }

   public final void registerEmu(NWEmu var1, String var2) {
      this.EmulatorRef = var1;
      this.descriptor = var2;
   }

   public final void startTimer(double var1) {
      this.EmulatorRef.startTimer(this.descriptor, var1);
   }

   public final boolean stopTimer() {
      return this.EmulatorRef.stopTimer(this.descriptor);
   }

   public final void toLayer3(NWEmuPkt var1) {
      this.EmulatorRef.toLayer3(this.descriptor, var1);
   }

   public final void toLayer5(NWEmuMsg var1) {
      this.EmulatorRef.toLayer5(this.descriptor, var1);
   }

   public final double currTime() {
      return this.EmulatorRef.Sim.time();
   }

   public final void sysLog(int var1, String var2) {
      this.EmulatorRef.sysLog(var1, this.descriptor + " - " + var2);
   }
}
