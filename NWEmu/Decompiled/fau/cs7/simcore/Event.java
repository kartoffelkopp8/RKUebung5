package fau.cs7.simcore;

public abstract class Event {
   double time;
   long id;
   int DEBUG = 0;
   String descriptor;

   public abstract void actions();

   public void init() {
      this.id = -1L;
      this.time = -1.0;
   }

   public Event() {
      this.init();
      this.descriptor = this.getClass().getName();
   }

   public Event(String var1) {
      this.init();
      this.descriptor = var1;
      if (this.DEBUG > 1) {
         System.out.println("New Event: " + var1);
      }
   }

   public String getDescriptor() {
      return this.descriptor;
   }

   public double getTime() {
      return this.time;
   }
}
