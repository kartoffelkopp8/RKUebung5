import fau.cs7.nwemu.AbstractHost;
import fau.cs7.nwemu.NWEmu;
import fau.cs7.nwemu.NWEmuMsg;
import fau.cs7.nwemu.NWEmuPkt;

public class GoBackN {
    private static class GoBackNSender extends AbstractHost {
        @Override
        public void init() {
            // TODO Auto-generated method stub
            super.init();
        }

        @Override
        public void input(NWEmuPkt arg0) {
            // TODO Auto-generated method stub
            super.input(arg0);
        }

        @Override
        public Boolean output(NWEmuMsg arg0) {
            // TODO Auto-generated method stub
            return super.output(arg0);
        }

        @Override
        public void timerInterrupt() {
            // TODO Auto-generated method stub
            super.timerInterrupt();
        }
    }

    private static class GoBackNReciever extends AbstractHost {
        @Override
        public void init() {
            // TODO Auto-generated method stub
            super.init();
        }

        @Override
        public void input(NWEmuPkt arg0) {
            // TODO Auto-generated method stub
            super.input(arg0);
        }
    }

    public static void main(String[] args) {
        NWEmu emu = new NWEmu(new GoBackNSender(), new GoBackNReciever());
        emu.emulate(1000, 0.1, 0.2, 0.01, 2);
    }
}
