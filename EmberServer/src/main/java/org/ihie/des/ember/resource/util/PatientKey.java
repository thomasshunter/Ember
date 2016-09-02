package org.ihie.des.ember.resource.util;

import java.io.Serializable;
import java.util.Calendar;

import ca.uhn.fhir.model.primitive.IdDt;

public class PatientKey
{
    public final static String SP_LAST_UPDATED      = "_lastUpdated";
    public final static char DELIM_ID               = '+';
    public final static String DELIM_STRING_ID      = String.valueOf('+');
    private final static int[] CALENDAR_CONSTANTS   = { Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND };

    private final int instId;
    private final long patId;

    public PatientKey(final int instId, final long patId)
    {
        this.instId = instId;
        this.patId = patId;
    }

    public PatientKey(final IdDt patId)
    {
        this(getValue(patId));
    }
        
    public final static String getValue(final IdDt id)
    {
        if ((id == null) || id.isEmpty())
        {
            return null;
        }

        final String key = id.getValue();
        final int s = key.lastIndexOf('/');
        return (s >= 0) ? key.substring(s + 1) : key;
    }


    public PatientKey(final String key)
    {
        final int d             = key.indexOf(DELIM_ID);
        final String instKey    = key.substring(0, d), patKey = key.substring(d + 1);
        this.instId             = Columns.parseInstitutionId(instKey);
        this.patId              = Columns.parsePersonId(patKey);
    }

    public final int getInstitutionId()
    {
        return this.instId;
    }

    public final long getPatientId()
    {
        return this.patId;
    }

    public final Integer getInstitutionKey()
    {
        return Columns.wrapInstitutionId(this.instId);
    }

    public final Long getPatientKey()
    {
        return Columns.wrapPersonId(this.patId);
    }

}

