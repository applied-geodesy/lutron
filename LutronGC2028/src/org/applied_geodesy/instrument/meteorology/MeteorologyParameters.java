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

package org.applied_geodesy.instrument.meteorology;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MeteorologyParameters {
	private Map<MeteorologyParameterType, Double> parameters = new HashMap<MeteorologyParameterType, Double>(10);
	private Date timestamp = null;
		
	public void clear() {
		this.timestamp = null;
		this.parameters.clear();
	}
	
	public boolean contains(MeteorologyParameterType parameterType) {
		return this.parameters.containsKey(parameterType);
	}
	
	public void set(MeteorologyParameters meteorologyParameters) {
		this.clear();
		
		this.setTimestamp(meteorologyParameters.getTimestamp());
		Map<MeteorologyParameterType, Double> parameters = meteorologyParameters.parameters;
		for (Map.Entry<MeteorologyParameterType, Double> entry : parameters.entrySet())
			this.set(entry.getKey(), entry.getValue());
	}
	
	public void set(MeteorologyParameterType parameterType, double value) {
		this.set(parameterType, value, new Date());
	}
	
	public void set(MeteorologyParameterType parameterType, double value, Date timestamp) {
		this.setTimestamp(timestamp);
		this.parameters.put(parameterType, value);
	}
	
	public double get(MeteorologyParameterType parameterType) {
		return this.parameters.get(parameterType);
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public Date getTimestamp() {
		return this.timestamp;
	}

	@Override
	public String toString() {
		return "MeteorologyParameters [timestamp=" + timestamp + ", parameters=" + parameters + "]";
	}
}
