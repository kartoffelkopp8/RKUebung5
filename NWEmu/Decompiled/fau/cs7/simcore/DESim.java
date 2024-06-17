package fau.cs7.simcore;

public class DESim {
   double simTime;
   EventList eventList;
   boolean stopped;
   int DEBUG = 0;

   public DESim() {
      this.init();
   }

   public void init() {
      this.simTime = 0.0;
      this.stopped = false;
      this.eventList = new EventList();
   }

   public void run() {
      while (!this.stopped) {
         if (this.eventList.empty()) {
            if (this.DEBUG > 1) {
               System.out.println("Empty event list [" + this.simTime + "]");
            }

            this.stopped = true;
            break;
         }

         Event var1 = this.eventList.getNextEvent();
         this.simTime = var1.time;
         if (this.DEBUG > 1) {
            System.out.println("Processing event [" + this.simTime + "]");
         }

         var1.actions();
         if (this.DEBUG > 2) {
            System.out.println("Processed event [" + this.simTime + "]");
         }
      }
   }

   public boolean schedule(Event var1, double var2) {
      if (this.DEBUG > 1) {
         System.out.println("Scheduling event for " + (this.simTime + var2) + " [" + this.simTime + "]");
      }

      if (var2 >= 0.0) {
         this.eventList.schedule(var1, this.simTime + var2);
         return true;
      } else {
         return false;
      }
   }

   public boolean scheduleAt(Event var1, double var2) {
      if (this.DEBUG > 1) {
         System.out.println("Scheduling event for " + var2 + " [" + this.simTime + "]");
      }

      if (var2 >= this.simTime) {
         this.eventList.schedule(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public void stop() {
      this.stopped = true;
      if (this.DEBUG > 1) {
         System.out.println("stop()");
      }
   }

   public double time() {
      if (this.DEBUG > 1) {
         System.out.println("time()" + this.simTime);
      }

      return this.simTime;
   }

   public EventList getEventList() {
      if (this.DEBUG > 1) {
         System.out.println("eventList()");
      }

      return this.eventList;
   }
}
