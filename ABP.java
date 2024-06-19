import fau.cs7.nwemu.*;

public class ABP {
    private static class ABPSender extends AbstractHost {
        private int seq;
        private boolean sending;
        private NWEmuPkt pkt;

        @Override
        public void init() {
            seq = 0;
            sending = false;
            pkt = new NWEmuPkt();
            super.init();
            sysLog(1, "Initialising host");
        }

        @Override
        public void input(NWEmuPkt arg0) {
            // TODO Auto-generated method stub
            super.input(arg0);
        }

        @Override
        public Boolean output(NWEmuMsg arg0) {
            if(sending){
                return false;
            }
            //initialise paket
            int checksum = 0;
            pkt.acknum = -1;
            pkt.seqnum = 0;
            for(int i = 0; i < NWEmu.PAYSIZE; i++){
                pkt.payload[i] = arg0.data[i];
                checksum += pkt.payload[i];
            }
            pkt.checksum += pkt.seqnum + pkt.acknum;
            startTimer(10.0);
            toLayer3(pkt);
            sending = true;
            sysLog(1, "sending...");
            return true;
        }

        @Override
        public void timerInterrupt() {
            startTimer(10.0);
            toLayer3(pkt);
        }
    }

    private static class ABPReciever extends AbstractHost {
        int ack;

        @Override
        public void init() {
            sysLog(1, "init() started");
            super.init();
        }

        @Override
        public void input(NWEmuPkt arg0) {
            // TODO Auto-generated method stub
            super.input(arg0);
        }
    }

    public static void main(String[] args) {
        NWEmu emu = new NWEmu(new ABPSender(), new ABPReciever());
        emu.emulate(1000, 0.1, 0.2, 0.01, 2);
    }
}
