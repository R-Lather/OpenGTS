// ----------------------------------------------------------------------------
// Copyright 2007-2014, GeoTelematic Solutions, Inc.
// All rights reserved
// ----------------------------------------------------------------------------
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ----------------------------------------------------------------------------
// Change History:
//  2007/01/10  Martin D. Flynn
//     -Initial release
// ----------------------------------------------------------------------------
package org.opengts.war.report.dmtp;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.opengts.util.*;
import org.opengts.dbtools.*;
import org.opengts.db.*;
import org.opengts.db.tables.*;

import org.opengts.war.tools.*;
import org.opengts.war.report.*;
import org.opengts.war.report.field.*;

import org.opengts.db.dmtp.Property;

public class PropertyReport
    extends ReportData
{

    // ------------------------------------------------------------------------

    private java.util.List<FieldData> rowData = null;

    // ------------------------------------------------------------------------

    /**
    *** Property Values Report Constructor
    *** @param rptEntry The ReportFEntry that generated this report
    *** @param reqState The session RequestProperties instance
    *** @param devList  The list of devices
    **/
    public PropertyReport(ReportEntry rptEntry, RequestProperties reqState, ReportDeviceList devList)
        throws ReportException
    {
        super(rptEntry, reqState, devList);
        if (this.getAccount() == null) {
            throw new ReportException("Account-ID not specified");
        } else
        if (this.getDeviceCount() != 1) {
            throw new ReportException("1 and only 1 Device must be specified");
        }
    }

    // ------------------------------------------------------------------------

    /**
    *** Post report initialization
    **/
    public void postInitialize()
    {
        //ReportConstraints rc = this.getReportConstraints();
        //Print.logInfo("LimitType=" + rc.getSelectionLimitType() + ", Limit=" + rc.getSelectionLimit());
    }

    // ------------------------------------------------------------------------

    /**
    *** Gets the bound ReportLayout singleton instance for this report
    *** @return The bound ReportLayout
    **/
    public static ReportLayout GetReportLayout()
    {
        // bind the report format to this data
        return FieldLayout.getReportLayout();
    }

    /**
    *** Gets the bound ReportLayout singleton instance for this report
    *** @return The bound ReportLayout
    **/
    public ReportLayout getReportLayout()
    {
        // bind the report format to this data
        return GetReportLayout();
    }

    // ------------------------------------------------------------------------

    /**
    *** Creates and returns an iterator for the row data displayed in the body of this report.
    *** @return The body row data iterator
    **/
    public DBDataIterator getBodyDataIterator()
    {
        
        /* init */
        this.rowData = new Vector<FieldData>();
        
        /* loop through properties */
        String accountID = this.getAccountID();
        String deviceID  = this.getFirstDeviceID();
        try {
            Device device    = this.getDevice(deviceID);
            if (device != null) {
                Property properties[] = Property.getProperties(accountID, deviceID);
                if (properties != null) {
                    for (int i = 0; i < properties.length; i++) {
                        FieldData fd = new FieldData();
                        fd.setDevice(device);
                        fd.setString(FieldLayout.DATA_DEVICE_ID     , deviceID);
                        fd.setLong(  FieldLayout.DATA_TIMESTAMP     , properties[i].getTimestamp());
                        fd.setLong(  FieldLayout.DATA_PROPERTY_KEY  , (long)properties[i].getPropKey());
                        fd.setString(FieldLayout.DATA_PROPERTY_DESC , properties[i].getDescription());
                        fd.setString(FieldLayout.DATA_PROPERTY_VALUE, properties[i].getValueAsString());
                        this.rowData.add(fd);
                    }
                }
            } else {
                // should never occur
                Print.logError("Returned DeviceList 'Device' is null: " + deviceID);
            }
        } catch (DBException dbe) {
            Print.logError("Error retrieving Device: " + deviceID);
        }

        /* return data iterator */
        return new ListDataIterator(this.rowData);
        
    }

    /**
    *** Creates and returns an iterator for the row data displayed in the total rows of this report.
    *** @return The total row data iterator
    **/
    public DBDataIterator getTotalsDataIterator()
    {
        return null;
    }

    // ------------------------------------------------------------------------

}
