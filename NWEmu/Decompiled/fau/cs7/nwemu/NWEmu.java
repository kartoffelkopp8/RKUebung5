package fau.cs7.nwemu;

import fau.cs7.simcore.DESim;
import fau.cs7.simcore.Event;
import fau.cs7.simcore.RandMarZam87;

public class NWEmu {
   public static final int PAYSIZE = 20;
   DESim Sim = new DESim();
   int TRACE = 0;
   int nsim = 0;
   int nsimmax = 0;
   double lossprob;
   double corruptprob;
   double lambda;
   int ntolayer3;
   int ntolayer5;
   int nlost;
   int ncorrupt;
   int norder;
   RandMarZam87 genArr = new RandMarZam87();
   RandMarZam87 genLos = new RandMarZam87();
   RandMarZam87 genCor = new RandMarZam87();
   RandMarZam87 genDel = new RandMarZam87();
   AbstractHost HostA;
   AbstractHost HostB;

   void sysLog(int var1, String var2) {
      if (this.TRACE >= var1) {
         System.out.print("  [" + this.Sim.time() + "] - " + var1 + ": ");
         System.out.println(var2);
      }
   }

   public NWEmu(AbstractHost var1, AbstractHost var2) {
      this.HostA = var1;
      this.HostB = var2;
      this.HostA.registerEmu(this, "HostA");
      this.HostB.registerEmu(this, "HostB");
   }

   public void emulate(int var1, double var2, double var4, double var6, int var8) {
      this.init(var1, var2, var4, var6, var8);
      this.Sim.run();
      System.out.println("-----");
      System.out.println("Emulator terminated.");
      this.sysLog(1, "Packets generated  : " + this.nsim);
      this.sysLog(2, "Packets to layer 5 : " + this.ntolayer5);
      this.sysLog(2, "Packets to layer 3 : " + this.ntolayer3);
      this.sysLog(2, "Packets lost       : " + this.nlost);
      this.sysLog(2, "Packets corrupted  : " + this.ncorrupt);
      this.sysLog(2, "Correct ordered    : " + (this.norder == 0));
   }

   public AbstractHost dispatch(String var1) {
      if (var1.equals(this.HostA.getDescriptor())) {
         return this.HostA;
      } else {
         return var1.equals(this.HostB.getDescriptor()) ? this.HostB : null;
      }
   }

   public AbstractHost dispatchOther(String var1) {
      if (var1.equals(this.HostA.getDescriptor())) {
         return this.HostB;
      } else {
         return var1.equals(this.HostB.getDescriptor()) ? this.HostA : null;
      }
   }

   public void init(int var1, double var2, double var4, double var6, int var8) {
      System.out.println("----- Network Emulator - Java Version 4.07 - ksjh -----");
      System.out.println();
      this.nsimmax = var1;
      this.lossprob = var2;
      this.corruptprob = var4;
      this.lambda = var6;
      this.TRACE = var8;
      this.ntolayer3 = 0;
      this.ntolayer5 = 0;
      this.nlost = 0;
      this.ncorrupt = 0;
      this.norder = 0;
      this.HostA.init();
      this.HostB.init();
      this.generate_next_arrival();
   }

   public void randTimer() {
      this.genArr.randomizeTimer();
      this.genLos.randomizeTimer();
      this.genCor.randomizeTimer();
      this.genDel.randomizeTimer();
      this.genArr.randomizeTimer();
   }

   void generate_next_arrival() {
      double var1 = this.genArr.randU01() * this.lambda * 2.0;
      if (this.nsim < this.nsimmax) {
         this.Sim.schedule(new NWEmu.E_FromLayer5(this.HostA.getDescriptor()), var1);
         this.sysLog(3, "Scheduling next FromLayer5 from Host " + this.HostA.getDescriptor() + " with delay " + var1);
      }
   }

   public boolean stopTimer(String var1) {
      String var2 = new String("TimerInterrupt-" + var1);
      boolean var3 = this.Sim.getEventList().cancelFirstOfDesc(var2);
      this.sysLog(3, "StopTimer " + var2 + ", success: " + var3);
      return var3;
   }

   public boolean startTimer(String var1, double var2) {
      this.sysLog(3, "StartTimer Host: " + var1 + " with delay " + var2);
      if (this.Sim.getEventList().viewFirstOfDesc("TimerInterrupt-" + var1) != null) {
         this.sysLog(1, "WARNING: Starting a timer that is already started!");
      }

      return this.Sim.schedule(new NWEmu.E_TimerInterrupt(var1), var2);
   }

   public void toLayer3(String var1, NWEmuPkt var2) {
      double var5 = this.Sim.time();
      this.ntolayer3++;
      if (this.genLos.randU01() < this.lossprob) {
         this.nlost++;
         this.sysLog(1, var1 + ".toLayer3(" + var2 + "): packet lost");
      } else {
         NWEmuPkt var7 = var2.copy();
         double var3;
         if ((var3 = this.genCor.randU01()) < this.corruptprob) {
            this.ncorrupt++;
            if (var3 < 0.75) {
               var7.payload[0] = 122;
            } else if (var3 < 0.875) {
               var7.seqnum = 999999;
            } else {
               var7.acknum = 999999;
            }

            this.sysLog(1, var1 + ".toLayer3(" + var2 + "): packet corrupted (" + var7 + ")");
         } else if (var2 != null) {
            this.sysLog(1, var1 + ".toLayer3(" + var2 + ")");
         }

         NWEmu.E_FromLayer3 var8 = (NWEmu.E_FromLayer3)this.Sim.getEventList().viewLastOfDesc("FromLayer3-" + this.dispatchOther(var1).getDescriptor());
         if (var8 != null) {
            var5 = var8.getTime();
         }

         NWEmu.E_FromLayer3 var9 = new NWEmu.E_FromLayer3(this.dispatchOther(var1).getDescriptor());
         var9.pktptr = var7;
         this.Sim.getEventList().schedule(var9, var5 + 1.0 + 9.0 * this.genDel.randU01());
      }
   }

   public void toLayer5(String var1, NWEmuMsg var2) {
      int var3 = var2.data[0] - 97;
      this.sysLog(1, var1 + ".toLayer5(" + var2 + ")");
      if (var3 != this.ntolayer5 % 26) {
         this.sysLog(1, "Message at layer5 did not arrive in correct order!");
         this.norder++;
      }

      this.ntolayer5++;
   }

   class E_FromLayer3 extends Event {
      String hostdesc;
      NWEmuPkt pktptr;

      public E_FromLayer3() {
         super("FromLayer3");
         NWEmu.this.sysLog(5, "New " + this.getDescriptor());
      }

      public E_FromLayer3(String var2) {
         super("FromLayer3-" + var2);
         NWEmu.this.sysLog(5, "New " + this.getDescriptor());
         this.hostdesc = var2;
      }

      @Override
      public void actions() {
         NWEmu.this.sysLog(5, "Event: " + this.getDescriptor());
         NWEmu.this.sysLog(2, this.hostdesc + ".input(), packet = " + this.pktptr.toString());
         NWEmu.this.dispatch(this.hostdesc).input(this.pktptr);
      }
   }

   class E_FromLayer5 extends Event {
      String hostdesc;

      public E_FromLayer5() {
         super("FromLayer5");
         NWEmu.this.sysLog(5, "New " + this.getDescriptor());
      }

      public E_FromLayer5(String var2) {
         super("FromLayer5-" + var2);
         this.hostdesc = var2;
         NWEmu.this.sysLog(5, "New " + this.getDescriptor());
      }

      @Override
      public void actions() {
         NWEmu.this.sysLog(5, "Event: " + this.getDescriptor());
         NWEmuMsg var1 = new NWEmuMsg();
         int var2 = NWEmu.this.nsim % 26;

         for (int var3 = 0; var3 < 20; var3++) {
            var1.data[var3] = (byte)(97 + var2);
         }

         NWEmu.this.sysLog(2, this.hostdesc + ".output(), message = " + var1.toString());
         if (NWEmu.this.dispatch(this.hostdesc).output(var1)) {
            NWEmu.this.nsim++;
         } else {
            NWEmu.this.sysLog(2, this.hostdesc + ".output(), message = " + var1.toString() + " failed, will be retried!");
         }

         NWEmu.this.generate_next_arrival();
      }
   }

   class E_TimerInterrupt extends Event {
      String hostdesc;

      public E_TimerInterrupt() {
         super("TimerInterrupt");
         this.hostdesc = "";
         NWEmu.this.sysLog(5, "New " + this.getDescriptor());
      }

      public E_TimerInterrupt(String var2) {
         super("TimerInterrupt-" + var2);
         this.hostdesc = var2;
         NWEmu.this.sysLog(5, "New " + this.getDescriptor());
      }

      @Override
      public void actions() {
         NWEmu.this.sysLog(5, "Event: " + this.getDescriptor());
         NWEmu.this.sysLog(2, this.hostdesc + ".timerInterrupt()");
         NWEmu.this.dispatch(this.hostdesc).timerInterrupt();
      }
   }
}
