package org.applied_geodesy.instrument.meteorology.lutron.test;

import org.applied_geodesy.instrument.meteorology.MeteorologyParameterType;
import org.applied_geodesy.instrument.meteorology.lutron.LutronGC2028Sensor;
import org.applied_geodesy.io.rxtx.rxtxcomm.RxTxCommunicator;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class LutronGC2029RxTxComm {
	public static void main(String[] args) throws Exception {
		System.out.println("Starte Messung via LutronGC2029RxTxComm");
		RxTxCommunicator comm = new RxTxCommunicator();
		comm.setCommPortIdentifier(CommPortIdentifier.getPortIdentifier("COM5"));
		comm.setBaudRate(9600); 
		comm.setDataBits(SerialPort.DATABITS_8);
		comm.setStopBits(SerialPort.STOPBITS_1);
		comm.setParity(SerialPort.PARITY_NONE);
		
		LutronGC2028Sensor gc2028Sensor = new LutronGC2028Sensor(comm);
		
		comm.addReceiver(gc2028Sensor);

		if (comm.open()) {
			try {
				int i=1;
				while (i++ < 20) {
					if (gc2028Sensor.getMeteorologyParameters().contains(MeteorologyParameterType.CARBON_DIOXIDE))
						System.out.println(gc2028Sensor.getMeteorologyParameters());
					Thread.sleep(2000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				comm.close();
				comm.removeReceiver(gc2028Sensor);
			}
		}
	}
}
