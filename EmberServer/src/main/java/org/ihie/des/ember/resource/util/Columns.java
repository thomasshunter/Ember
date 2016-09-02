package org.ihie.des.ember.resource.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class Columns
{
    public final static int NULL_ID = -1;

    private final static long formatLong(final ResultSet rs, final long val) throws SQLException
    {
        return rs.wasNull() ? NULL_ID : val; // CORE-1502
    }

    private final static long getLong(final ResultSet rs, final String col) throws SQLException
    {
        return formatLong(rs, rs.getLong(col));
    }

    private final static long getLong(final ResultSet rs, final int col) throws SQLException
    {
        return formatLong(rs, rs.getLong(col));
    }

    /*
     * ResultSet.get methods more important here than PreparedStatement.set
     * methods. Changes from int to long will break set calls at compile time,
     * so they're easy to find and fix. Calls to get won't break, so ideally we
     * would only need to change them here instead of a bunch of different
     * places.
     */
    public final static int getInstitutionId(final ResultSet rs, final String col) throws SQLException
    {
        // This method doesn't do much, but it links the Java data type to the
        // Oracle column type in a single place
        return rs.getInt(col);
    }

    public final static long getPersonId(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getSequenceNumber(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getTeamId(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    /*
     * These methods are just for mapping data types. They're not dependent on
     * names. So we don't need a patientId method in addition to the above
     * personId method here. We just need patient-specific methods when dealing
     * with PatientRelated objects. Similarly, we wouldn't need
     * getUserId/ProviderId/GlobalPersonId methods here.
     */
    /*
     * public final static long getPatientId(final ResultSet rs, final String
     * col) throws SQLException { return getLong(rs, col); }
     */

    public final static long getEncounterId(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getOrderId(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static int getRefillNumber(final ResultSet rs, final String col) throws SQLException
    {
        return rs.getInt(col);
    }

    public final static long getEligibilityId(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getLocationId(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getProducerId(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static int getSysId(final ResultSet rs, final String col) throws SQLException
    {
        return rs.getInt(col);
    }

    public final static long getPrivilegeId(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getNodeId(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getPropertyId(final ResultSet rs, final String col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static int getInstitutionId(final ResultSet rs, final int col) throws SQLException
    {
        // This method doesn't do much, but it links the Java data type to the
        // Oracle column type in a single place
        return rs.getInt(col);
    }

    public final static long getPersonId(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getSequenceNumber(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    // See comments for above getPatientId method
    /*
     * public final static long getPatientId(final ResultSet rs, final int col)
     * throws SQLException { return getLong(rs, col); }
     */

    public final static long getTeamId(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getEncounterId(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getOrderId(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static int getRefillNumber(final ResultSet rs, final int col) throws SQLException
    {
        return rs.getInt(col);
    }

    public final static long getEligibilityId(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getLocationId(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getProducerId(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static int getSysId(final ResultSet rs, final int col) throws SQLException
    {
        return rs.getInt(col);
    }

    public final static long getPrivilegeId(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getNodeId(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static long getPropertyId(final ResultSet rs, final int col) throws SQLException
    {
        return getLong(rs, col);
    }

    public final static void setInstitutionId(final PreparedStatement stmt, final int col, final int val) throws SQLException
    {
        stmt.setInt(col, val);
    }

    public final static void setPersonId(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setSequenceNumber(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setTeamId(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setEncounterId(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setOrderId(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setRefillNumber(final PreparedStatement stmt, final int col, final int val) throws SQLException
    {
        stmt.setInt(col, val);
    }

    public final static void setEligibilityId(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setLocationId(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setProducerId(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setSysId(final PreparedStatement stmt, final int col, final int val) throws SQLException
    {
        stmt.setInt(col, val);
    }

    public final static void setPrivilegeId(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setNodeId(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setPropertyId(final PreparedStatement stmt, final int col, final long val) throws SQLException
    {
        stmt.setLong(col, val);
    }

    public final static void setPerson(final PreparedStatement stmt, final int iCol, final int pCol, final int instId, final long pId) throws SQLException
    {
        setInstitutionId(stmt, iCol, instId);
        setPersonId(stmt, pCol, pId);
    }

    public final static void setConcept(final PreparedStatement stmt, final int sCol, final int cCol, final int sysId, final String code) throws SQLException
    {
        if (code == null)
        {
            stmt.setNull(sCol, Types.NUMERIC);
            stmt.setString(cCol, null);
        }
        else
        {
            setSysId(stmt, sCol, sysId);
            stmt.setString(cCol, code);
        }
    }

    public final static Integer wrapInstitutionId(final int institutionId)
    {
        return Integer.valueOf(institutionId);
    }

    public final static Long wrapPersonId(final long personId)
    {
        return Long.valueOf(personId);
    }

    public final static Long wrapSequenceNumber(final long sequenceNumber)
    {
        return Long.valueOf(sequenceNumber);
    }

    public final static Long wrapTeamId(final long teamId)
    {
        return Long.valueOf(teamId);
    }

    public final static Long wrapEncounterId(final long encounterId)
    {
        return Long.valueOf(encounterId);
    }

    public final static Long wrapOrderId(final long orderId)
    {
        return Long.valueOf(orderId);
    }

    public final static Long wrapLocationId(final long locationId)
    {
        return Long.valueOf(locationId);
    }

    public final static Long wrapProducerId(final long locationId)
    {
        return Long.valueOf(locationId);
    }

    public final static Integer wrapSysId(final int sysId)
    {
        return Integer.valueOf(sysId);
    }

    public final static Long wrapPrivilegeId(final long privilegeId)
    {
        return Long.valueOf(privilegeId);
    }

    public final static Long wrapNodeId(final long nodeId)
    {
        /*
         * At one point just had wrap(int) and wrap(long). Had a Map<Long, ?>.
         * Called Map.get(wrap(0)). So it wrapped it as an Integer and didn't
         * find it. Since we might sometimes wrap literals instead of typed
         * variables, best to have a wrap method for each database type. That
         * would avoid silently compiling a mistake like above.
         */
        return Long.valueOf(nodeId);
    }

    public final static int unwrapInstitutionId(final Number n)
    {
        return n.intValue();
    }

    public final static long unwrapPersonId(final Number n)
    {
        return n.longValue();
    }

    public final static long unwrapSequenceNumber(final Number n)
    {
        return n.longValue();
    }

    public final static long unwrapTeamId(final Number n)
    {
        return n.longValue();
    }

    public final static long unwrapLocationId(final Number n)
    {
        return n.longValue();
    }

    public final static long unwrapProducerId(final Number n)
    {
        return n.longValue();
    }

    public final static int unwrapSysId(final Number n)
    {
        return n.intValue();
    }

    public final static long unwrapPrivilegeId(final Number n)
    {
        return n.longValue();
    }

    public final static long unwrapNodeId(final Number n)
    {
        return n.longValue();
    }

    public final static int parseInstitutionId(final String s)
    {
        return Integer.parseInt(s);
    }

    public final static long parsePersonId(final String s)
    {
        return Long.parseLong(s);
    }

    public final static long parseEncounterId(final String s)
    {
        return Long.parseLong(s);
    }

    public final static long parseOrderId(final String s)
    {
        return Long.parseLong(s);
    }

    public final static long parseLocationId(final String s)
    {
        return Long.parseLong(s);
    }

    public final static long parseProducerId(final String s)
    {
        return Long.parseLong(s);
    }

    public final static int parseSysId(final String s)
    {
        return Integer.parseInt(s);
    }

    public final static long parsePrivilegeId(final String s)
    {
        return Long.parseLong(s);
    }
}