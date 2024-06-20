import java.util.LinkedList;
import java.util.List;

import fau.cs7.nwemu.AbstractHost;
import fau.cs7.nwemu.NWEmu;
import fau.cs7.nwemu.NWEmuMsg;
import fau.cs7.nwemu.NWEmuPkt;

public class GoBackN {
    private static class GoBackNSender extends AbstractHost {
        private static final int WINDOW_SIZE = 8;

        private int baseSeqNum = 0;
        private List<NWEmuMsg> waitingWindow = new LinkedList<>();

        private boolean isAwaited(int seqnum) {
            return seqnum >= baseSeqNum && seqnum < baseSeqNum + waitingWindow.size();
        }

        @Override
        public void init() {
            // Only useful for re-init
            baseSeqNum = 0;
            waitingWindow = new LinkedList<>();
        }

        @Override
        public void input(NWEmuPkt packet) {
            if (packet.checksum != calculateChecksum(packet) || !isAwaited(packet.acknum)) return;

            // Advance baseSeqNum and remove acknowleged packets from waitingWindow
            int prevBaseSeqNum = baseSeqNum;
            baseSeqNum = packet.acknum + 1;
            for (int i = 0; i < baseSeqNum - prevBaseSeqNum; i++) {
                waitingWindow.removeFirst();
            }

            // Stop timer if everything was acknowleged
            if (waitingWindow.isEmpty()) {
                stopTimer();
            } else {
                stopTimer();
                startTimer(20);
            }
        }

        @Override
        public Boolean output(NWEmuMsg message) {
            if (waitingWindow.size() >= WINDOW_SIZE) return false;

            // Send packet + start listening for its ack
            sendNewPacket(message);
            waitingWindow.add(message);

            // Restart timer
            stopTimer();
            startTimer(20);
            return true;
        }

        @Override
        public void timerInterrupt() {
            // Re-send packet
            resendPackets();
            stopTimer();
            startTimer(20);
        }

        private void sendNewPacket(NWEmuMsg message) {
            NWEmuPkt pkt = new NWEmuPkt();
            pkt.seqnum = baseSeqNum + waitingWindow.size();
            pkt.payload = message.data;
            pkt.checksum = calculateChecksum(pkt);

            toLayer3(pkt);
        }

        private void resendPackets() {
            for (int i = 0; i < waitingWindow.size(); i++) {
                NWEmuPkt pkt = new NWEmuPkt();
                pkt.seqnum = baseSeqNum + i;
                pkt.payload = waitingWindow.get(i).data;
                pkt.checksum = calculateChecksum(pkt);
                toLayer3(pkt);
            }
        }
    }

    private static class GoBackNReciever extends AbstractHost {
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
                seqnum = packet.seqnum + 1;
            }

            // Send ack (Even for old packets, to resend ack if lost)
            if (packet.seqnum <= seqnum) {
                NWEmuPkt ack = new NWEmuPkt();
                ack.acknum = packet.seqnum;
                ack.checksum = calculateChecksum(ack); // We don't actually need the full message to be intact, this would be enough: ack.checksum = ack.acknum;
                toLayer3(ack);
            }
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
        NWEmu emu = new NWEmu(new GoBackNSender(), new GoBackNReciever());
        emu.emulate(20, 0.2, 0.2, 10, 2);
    }
}
