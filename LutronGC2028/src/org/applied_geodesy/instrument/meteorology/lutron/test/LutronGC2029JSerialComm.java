/***********************************************************************
* Copyright by Michael Loesler, https://software.applied-geodesy.org   *
*                                                                      *
* This program is free software; you can redistribute it and/or modify *
* it under the terms of the GNU General Public License as published by *
* the Free Software Foundation; either version 3 of the License, or    *
* at your option any later version.                                    *
*                                                                      *
* This program is distributed in the hope that it will be useful,      *
* but WITHOUT ANY WARRANTY; without even the implied warranty of       *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the        *
* GNU General Public License for more details.                         *
*                                                                      *
* You should have received a copy of the GNU General Public License    *
* along with this program; if not, see <http://www.gnu.org/licenses/>  *
* or write to the                                                      *
* Free Software Foundation, Inc.,                                      *
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.            *
*                                                                      *
***********************************************************************/

package org.applied_geodesy.instrument.meteorology.lutron.test;

import org.applied_geodesy.instrument.meteorology.MeteorologyParameterType;
import org.applied_geodesy.instrument.meteorology.lutron.LutronGC2028Sensor;
import org.applied_geodesy.io.rxtx.serialcomm.JSerialCommunicator;

import com.fazecast.jSerialComm.SerialPort;

public class LutronGC2029JSerialComm {
	
	
	public static void main(String[] args) {
		System.out.println("Starte Messung via LutronGC2029JSerialComm");
		JSerialCommunicator comm = new JSerialCommunicator();
		comm.setSerialPort(SerialPort.getCommPort("COM5"));
		comm.setBaudRate(9600);
		comm.setDataBits(8);
		comm.setStopBits(SerialPort.ONE_STOP_BIT);
		comm.setParity(SerialPort.NO_PARITY);
	
		LutronGC2028Sensor gc2028Sensor = new LutronGC2028Sensor(comm);
		
		comm.addReceiver(gc2028Sensor);

		if (comm.open()) {
			try {
				int i=1;
				while (i++ < 10) {
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