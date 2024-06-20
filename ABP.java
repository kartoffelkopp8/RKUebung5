import fau.cs7.nwemu.*;

public class ABP {
    private static class ABPSender extends AbstractHost {
        int seqnum = 0;
        NWEmuMsg currentMsg = null;
        boolean isAwaitingAck = false;

        @Override
        public void input(NWEmuPkt packet) {
            if (!isAwaitingAck || packet.acknum != seqnum || packet.acknum != packet.checksum) return;
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

            // Calculate checksum
            pkt.checksum = pkt.seqnum + pkt.acknum + pkt.flags;
            for (int i = 0; i < pkt.payload.length; i++) {
                pkt.checksum += pkt.payload[i];
            }

            toLayer3(pkt);
        }
    }

    private static class ABPReciever extends AbstractHost {
        int seqnum = 0;

        @Override
        public void input(NWEmuPkt packet) {
            // Verify Checksum
            int expectedChecksum = packet.seqnum + packet.acknum + packet.flags;
            for (int i = 0; i < packet.payload.length; i++) {
                expectedChecksum += packet.payload[i];
            }
            if (packet.checksum != expectedChecksum) return;

            // Recieve data (Only new ones, not when it was resent because of a lost ack)
            if (packet.seqnum == seqnum) {
                NWEmuMsg msg = new NWEmuMsg();
                msg.data = packet.payload;
                toLayer5(msg);
            }

            // Send ack (Even for old packets, to resend ack if lost)
            NWEmuPkt ack = new NWEmuPkt();
            ack.acknum = packet.seqnum;
            ack.checksum = ack.acknum;
            toLayer3(ack);
            seqnum = 1-packet.seqnum;
        }
    }

    public static void main(String[] args) {
        NWEmu emu = new NWEmu(new ABPSender(), new ABPReciever());
        emu.emulate(10, 0.1, 0.3, 1000, 2);
    }
}
