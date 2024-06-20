import fau.cs7.nwemu.*;

public class ABP {
    private static class ABPSender extends AbstractHost {
        int seqnum = 0;
        NWEmuMsg currentMsg = null;
        boolean isAwaitingAck = false;

        @Override
        public void init() {
            // Only useful for re-init
            seqnum = 0;
            currentMsg = null;
            isAwaitingAck = false;
        }

        @Override
        public void input(NWEmuPkt packet) {
            if (!isAwaitingAck || packet.acknum != seqnum || packet.checksum != calculateChecksum(packet)) return;
            seqnum = 1-seqnum;
            isAwaitingAck = false;
            stopTimer();
        }

        @Override
        public Boolean output(NWEmuMsg message) {
            if (isAwaitingAck) return false;

            // Send packet + start waiting
            currentMsg = message;
            sendPacket();
            isAwaitingAck = true;

            // Restart timer
            stopTimer();
            startTimer(20);
            return true;
        }

        @Override
        public void timerInterrupt() {
            // Re-send packet
            sendPacket();
            stopTimer();
            startTimer(20);
        }

        private void sendPacket() {
            NWEmuPkt pkt = new NWEmuPkt();
            pkt.seqnum = seqnum;
            pkt.payload = currentMsg.data;
            pkt.checksum = calculateChecksum(pkt);

            toLayer3(pkt);
        }
    }

    private static class ABPReciever extends AbstractHost {
        int seqnum = 0;

        @Override
        public void init() {
            // Only useful for re-init
            seqnum = 0;
        }

        @Override
        public void input(NWEmuPkt packet) {
            // Verify Checksum
            if (packet.checksum != calculateChecksum(packet)) return;

            // Recieve data (Only new ones, not when it was resent because of a lost ack)
            if (packet.seqnum == seqnum) {
                NWEmuMsg msg = new NWEmuMsg();
                msg.data = packet.payload;
                toLayer5(msg);
            }

            // Send ack (Even for old packets, to resend ack if lost)
            NWEmuPkt ack = new NWEmuPkt();
            ack.acknum = packet.seqnum;
            ack.checksum = calculateChecksum(ack); // We don't actually need the full message to be intact, this would be enough: ack.checksum = ack.acknum;
            toLayer3(ack);

            seqnum = 1-packet.seqnum;
        }
    }

    public static int calculateChecksum(NWEmuPkt packet) {
        int checksum = packet.seqnum + packet.acknum + packet.flags;
        for (int i = 0; i < packet.payload.length; i++) {
            checksum += packet.payload[i];
        }
        return checksum;
    }

    public static void main(String[] args) {
        NWEmu emu = new NWEmu(new ABPSender(), new ABPReciever());
        emu.emulate(10, 0.1, 0.3, 1000, 2);
    }
}
