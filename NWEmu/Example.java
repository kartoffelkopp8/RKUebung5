import fau.cs7.nwemu.*;

public class Beispiel {

	public static void main(String[] args) {

		// declare stuff needed by sender AND receiver in this class
		class CommonHost extends AbstractHost {
			int seqnum;
			int acknum;

		}

		// class representing the sender
		class SendingHost extends CommonHost {
			public void init() {
				sysLog(0, "Sending Host: init()");
				seqnum = 0;
				acknum = -1;
			}

			public Boolean output(NWEmuMsg message) {
				NWEmuPkt sndpkt = new NWEmuPkt();
				for (int i = 0; i < NWEmu.PAYSIZE; i++) {
					sndpkt.payload[i] = message.data[i];
				}
				sndpkt.seqnum = seqnum++;
				sndpkt.acknum = acknum;
				sndpkt.checksum = 0;
				sysLog(0, "Sending Host: output(" + message + ") -> toLayer3(" + sndpkt + ")");
				toLayer3(sndpkt);

				return true;
			}

			public void input(NWEmuPkt pkt) {
				sysLog(0, "Sending Host: input(" + pkt + ")");
			}

			public void timerInterrupt() {
				sysLog(0, "Sending Host: timerInterrupt()");
			}
		}

		// class representing the receiver
		class ReceivingHost extends CommonHost {
			public void init() {
				sysLog(0, "Receiving Host: init()");
				seqnum = -1;
				acknum = 0;
			}

			public void input(NWEmuPkt pkt) {
				NWEmuMsg message = new NWEmuMsg();
				for (int i = 0; i < NWEmu.PAYSIZE; i++) {
					message.data[i] = pkt.payload[i];
				}
				sysLog(0, "Receiving Host: input(" + pkt + ") -> toLayer5(" + message + ")");
				toLayer5(message);
			}
		}

		// instantiate sender and receiver
		SendingHost HostA = new SendingHost();
		ReceivingHost HostB = new ReceivingHost();

		// perform emulation
		NWEmu TestEmu = new NWEmu(HostA, HostB);
		TestEmu.randTimer();
		TestEmu.emulate(10, 0.0, 0.0, 10.0, 2);
		// send 10 messages, no loss, no corruption, lambda 10, log level 2
	}
}
