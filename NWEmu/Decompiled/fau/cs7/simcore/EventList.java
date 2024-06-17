package fau.cs7.simcore;

import java.util.LinkedList;

public class EventList {
   LinkedList<Event> eventList;
   long curr_id;
   int DEBUG = 0;

   public EventList() {
      this.eventList = new LinkedList<>();
      this.curr_id = 0L;
   }

   public boolean empty() {
      return this.eventList.size() == 0;
   }

   public void schedule(Event var1, double var2) {
      int var4 = 0;
      var1.time = var2;
      var1.id = ++this.curr_id;

      while (var4 < this.eventList.size() && var2 > this.eventList.get(var4).time) {
         var4++;
      }

      if (this.DEBUG > 1) {
         System.out.println("EventList size " + this.eventList.size() + " insert at " + var4);
      }

      this.eventList.add(var4, var1);
   }

   public Event getNextEvent() {
      return this.empty() ? null : this.eventList.removeFirst();
   }

   public Event viewFirstOfDesc(String var1) {
      for (int var3 = 0; var3 < this.eventList.size(); var3++) {
         Event var2;
         if ((var2 = this.eventList.get(var3)).descriptor.equals(var1)) {
            return var2;
         }
      }

      return null;
   }

   public Event viewLastOfDesc(String var1) {
      Event var2 = null;

      for (int var3 = 0; var3 < this.eventList.size(); var3++) {
         if (this.eventList.get(var3).descriptor.equals(var1)) {
            var2 = this.eventList.get(var3);
         }
      }

      return var2;
   }

   public boolean cancelFirstOfDesc(String var1) {
      boolean var3 = false;

      for (int var4 = 0; var4 < this.eventList.size(); var4++) {
         if (this.eventList.get(var4).descriptor.equals(var1)) {
            var3 = true;
            this.eventList.remove(var4);
            break;
         }
      }

      return var3;
   }

   public Event viewFirst() {
      return this.eventList.getFirst();
   }

   public boolean cancel(Event var1) {
      return this.eventList.remove(var1);
   }

   public Event viewFirstOfClass(String var1) {
      for (int var3 = 0; var3 < this.eventList.size(); var3++) {
         Event var2;
         if ((var2 = this.eventList.get(var3)).getClass().getName().equals(var1)) {
            return var2;
         }
      }

      return null;
   }
}
