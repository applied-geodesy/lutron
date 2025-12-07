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

package org.applied_geodesy.instrument.meteorology.lutron;

import java.io.IOException;
import java.util.Date;

import org.applied_geodesy.instrument.meteorology.MeteorologyParameterType;
import org.applied_geodesy.instrument.meteorology.MeteorologyParameters;
import org.applied_geodesy.io.rxtx.ReceiveDataType;
import org.applied_geodesy.io.rxtx.ReceiverExchangeable;
import org.applied_geodesy.io.rxtx.RxTx;
import org.applied_geodesy.io.rxtx.RxTxReturnable;

public class LutronGC2028Sensor implements RxTxReturnable, ReceiverExchangeable {
	private RxTx connRxTx;
	private StringBuffer responseMessage = new StringBuffer();
	private final static String RESPONSE_PREFIX = new String(new byte[] {02 , 52}), // , 52
							    RESPONSE_SUFIX  = new String(new byte[] {13});
	
	private MeteorologyParameters meteorologyParameters = new MeteorologyParameters();
	private MeteorologyParameters currentMeteorologyParameters = new MeteorologyParameters();
	
	public LutronGC2028Sensor(RxTx connRxTx) {
		this.connRxTx = connRxTx;
		this.connRxTx.setReceiveDataType(ReceiveDataType.BYTE_ARRAY);
	}
	
	@Override
	public void receive(byte[] bytesRX) throws IOException {
		this.responseMessage.append(new String(bytesRX));
		//System.out.println(this.responseMessage);
		int startPos = this.responseMessage.lastIndexOf( LutronGC2028Sensor.RESPONSE_PREFIX );
		int endPos   = this.responseMessage.lastIndexOf( LutronGC2028Sensor.RESPONSE_SUFIX );

        if (endPos >= 0 && startPos >= 0 && startPos < endPos && (endPos - startPos) == 15) {
        	String message = this.responseMessage.substring(startPos, endPos);

        	int displayData = Integer.parseInt(message.substring(2, 3)); // 1 == upper (CO2); 2 == lower (Temp)
        	int unit = Integer.parseInt(message.substring(3, 5));  // 01 == °C; 02 == °F; 19 == PPM
        	double sign = message.substring(5, 6).equalsIgnoreCase("0") ? 1.0 : -1.0;  // 0 == Positiv; 1 == Negativ
        	int decimalPoint = Integer.parseInt(message.substring(6, 7));  // 0 == No DP, 1 == 1 DP, 2 == 2 DP, 3 == 3 DP
        	double value = sign * Double.parseDouble(message.substring(7, 15)) * Math.pow(10, -decimalPoint);  // recorded value

        	if (displayData == 1 && unit == 19) {
        		this.currentMeteorologyParameters.clear();
        		this.currentMeteorologyParameters.set(MeteorologyParameterType.CARBON_DIOXIDE, value);
        	}
        	else if (displayData == 2) {
        		// °F --> °C  == ((32 °F - 32) * 5/9)
        		if (unit == 2) {
        			value = (32.0 * value - 32.0) * 5.0 / 9.0;
        		}
        		this.currentMeteorologyParameters.set(MeteorologyParameterType.DRY_BULB_TEMPERATURE, value, new Date((this.currentMeteorologyParameters.getTimestamp().getTime() + System.currentTimeMillis())/2));
        		this.meteorologyParameters.set(this.currentMeteorologyParameters);
        		this.currentMeteorologyParameters.clear();
        	}
        	this.responseMessage.setLength(0); // clearing
        }
	}

	@Override
	public void receive(int intRX) throws IOException {
		throw new IOException("Error, unsupported method call. Use receive(byte[] bytesRX) for data transfer.");
	}

	public MeteorologyParameters getMeteorologyParameters() {
		return this.meteorologyParameters;
	}

	@Override
	public RxTx getRxTx() {
		return this.connRxTx;
	}
}
