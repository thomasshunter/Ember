package org.ihie.des.ember.services.patient.bean;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ihie.des.ember.util.Util;

@SuppressWarnings("serial")
public class NameResolver implements Serializable
{
    private static final Log LOG                    = LogFactory.getLog(NameResolver.class);
    public final static Pattern PAT_MULTI_SPACE     = Pattern.compile("\\s{2,}");
    public final static double GENDER_RATIO         = .9;
    public final static int GENDER_TOTAL            = 25;

    /*********
     * List of Fake names given to unidentified patients
     ****************/
    public static final String[] BABY_NAME          = { "BABY", "BOY", "GIRL", "INF", "INFANT", "BABYBOY", "BABYGIRL" };

    public static final String[] FAKE_NAME          = { 
                                                        "DOE", "DOG", "CAT", "ANIMAL", "TEST", "SAMPLE", "PATIENT", "PRACTICE", "REUSE", "EMPLOYEE", "SYNTHESIS", "CR", "RESEARCHID", "NONE", "UNKNOWN", "NONAME", "NOT", "AAA", "DUP", "DUPL",
                                                        "UNIDENTIFIED", "MHCAP", "EHWR", "NO", "EMP", "XLCD", "TRANSPLANTCNTR", "HOMECARE", "NAME", "MISSING", "DPW", "MARIONCO", "OFC", "ODC", "DFC", "DCFS", "CPS", "PRISNERS", "MARIONCOUNTY",
                                                        "SELECTHOSPITAL", "JAIL", "SHELBYCOUNTY", "RUSHCOUNTY", "TIPPECANOECTY", "HENRYCOUNTY", "LAKECOUNTY", "DELAWARECTY", "HUNTINGTONCTY", "WABASHCOUNTY", "DEKALBCOUNTY", "RIPLEYCOUNTY",
                                                        "SOCIALSERVICE", "SCOTTCOUNTY", "STATEOFIND", "LOUNDESCO", "PROCUREMENTORG", "UNSPECIFIED", "WMH", "ISCAT", "PID", "RIVERVIEW-EMPHEALTH", "RIVERVIEWEMPHEALTH", "RVH", "DOCUMENT",
                                                        "PHARMACY", "RO-HOSPITAL", "ROHOSPITAL", "REFERRING" 
    };

    /** List of name prefixes */
    public static final String[] PREFIX             = { "DR", "MR", "MRS", "MS" };

    /** List of name suffixes */
    public static final String[] SUFFIX             = { "JR", "SR", "II", "III", "IV", "MD", "PHD", "DDS", "MS", "ESQ", 
                                                        "DMD",      // Doctor of Medical Dentistry
                                                        "DPM",      // Doctor of Podiatric Medicine
                                                        "RPH",      // Registered Pharmacist
                                                        "DVM",      // Doctor of Veterinary Medicine
                                                        "VMD",      // Veterinary Medical Doctor
                                                        "MRCVS",    // Member of the Royal College of Veterinary Surgeons
                                                        "MPH",      // Master of Public Health
                                                        "DMSC",     // Doctor of Medical Science
                                                        "FAAP",     // Fellow of the American Academy of Pediatrics
                                                        "FACC",     // Fellow of the American College of Cardiology
                                                        "FACEP",    // Fellow of the American College of Emergency Physicians
                                                        "FACFAS",   // Fellowship of American College of Foot and Ankle Surgeons
                                                        "FACP",     // Fellow of the American College of Physicians
                                                        "FACS",     // Fellow, American College of Surgeons
                                                        "MBBS",     // Bachelor of Medicine and Bachelor of Surgery
                                                        "MHSA",     // Master of Health Services Administration
                                                        "MMSC",     // Master of Medical Science
                                                        "MRCP",     // Member of the Royal College of Physicians
                                                        "FRCS",     // Fellow of the Royal College of Surgeons
                                                        "FRCSI",    // Fellow of the Royal College of Surgeons of Ireland
                                                        "CNM",      // Certified Nurse-Midwife
                                                        "NP",       // Nurse Practitioner
                                                        "ANP",      // Adult Nurse Practitioner
                                                        "APN",      // Advanced Practice Nurse
                                                        "APRN",     // Advanced Practice Registered Nurse
                                                        "ARNP",     // Advanced Registered Nurse Practitioner
                                                        "CNP",      // Certified Nurse Practitioner
                                                        "FNP",      // Family Nurse Practitioner
                                                        "GNP",      // Gerontological Nurse Practitioner
                                                        "NNP",      // Neonatal Nurse Practitioner
                                                        "PNP",      // Pediatric Nurse Practitioner
                                                        "CRNA",     // Certified Registered Nurse Anesthetist
                                                        "MBA"       // Master of Business Administration
    };

    // These are used as suffixes in incoming data, but might also be foreign
    // names
    public static final String[] QUESTIONABLE_SUFFIX= { 
                                                        "PA",       // Physician's Assistant
                                                        "OD",       // Doctor of Optometry
                                                        "DO",       // Doctor Of Osteopathy
    };

    // These are prefixes for multiple-word last names
    // We don't need to add entries like "ST.", since the lookup will work with
    // or without the "."
    public static final String[] LAST_NAME_PREFIX   = { "MC", "VAN", "DE", "LA", "DELA", "ST" };
    public final static int RARE_MATCH              = 1;
    public final static int NO_MATCH                = -1;
    public final static int COMMON_MATCH            = 0;
    private final static int MIN_MATCH_SIZE         = 5; // Need this many letters for LCS
    private static Set<String> commonFirstNameCache = null;
    private static Set<String> commonLastNameCache  = null;
    private static boolean babyWildCardAllowed      = false;

    private String firstName                        = null;
    private String middleName                       = null;
    private String lastName                         = null; // Component parts of full name
    private String prefix                           = null; // Person's distinguishing prefix title, like "Dr."
    private String suffix                           = null; // Distinguishing name suffix, like DDS
    private boolean fake                            = false;
    private boolean baby                            = false;
    private String firstNYSIIS                      = null;
    private String middleNYSIIS                     = null;
    private String lastNYSIIS                       = null; // NYSIIS codes of name components
    private String firstAlpha                       = null;
    private String middleAlpha                      = null;
    private String lastAlpha                        = null; // Name components with non-letter characters removed

    
    
    
    private final static int format(final char[] nameChar, final String name, final int nameLen)
    {
        int j = 0;
        for (int i = 0; i < nameLen; i++)
        { // Make everything upper case, eliminate non-alphanumeric chars
            final char currChar = name.charAt(i);
            if ((('A' <= currChar) && (currChar <= 'Z')) || (('0' <= currChar) && (currChar <= '9')))
            {
                nameChar[j++] = currChar;
            }
            else if (('a' <= currChar) && (currChar <= 'z'))
            {
                nameChar[j++] = (char) (currChar - 32);
            }
        }
        return j; // Number of significant chars in name
    }

    /**
     * matches one person's full name with a second person's full name by
     * matching the longest possible sequence of characters from each name,
     * removing the matched sequence, and repeating the process. The returned
     * <code>int</code> value represents the match character "percentage".
     * 
     * @param name1
     *            first full person name to be matched
     * @param name2
     *            second full person name to be matched
     * @return the match character "percentage"
     **/
    public static int longestCommonNameMatch(final String name1, final String name2)
    {
        if ((name1 == null) || (name2 == null))
        {
            return 0;
        }
        final int repetitionThreshold = 3;

        int name1Len = name1.length(), name2Len = name2.length();
        if ((name1Len < 5) || (name2Len < 5))
        {
            return 0;
        }
        final char[] name1Char = new char[name1Len];
        name1Len = format(name1Char, name1, name1Len);
        final char[] name2Char = new char[name2Len];
        name2Len = format(name2Char, name2, name2Len);
        final int nameLenMin = Math.min(name1Len, name2Len);
        // Find smallest of the two names, that's all we can hope to match!
        if (nameLenMin == 0)
        {
            return 0;
        }
        int nameLenRemaining = nameLenMin; // Keep track of largest sequence we
                                           // can still hope to match
        final int[][] lcs = new int[name1Len][name2Len]; // Array to keep track
                                                         // of char sequences
                                                         // matched so far
        // If matching two short names, can match on even one letter; else
        // require sequence of 3 chars for a match
        int lenThreshold = nameLenRemaining < MIN_MATCH_SIZE ? 0 : 2, commonTmp1 = 0, commonTmp2 = 0, commonTotal = 0;
        for (int i = 0; i < repetitionThreshold; i++)
        { // Make sequence repetition loop
            if (nameLenRemaining < 1)
            {
                break;
            }
            else if (nameLenRemaining <= lenThreshold)
            {
                lenThreshold = nameLenRemaining - 1;
            }
            int commonMax = lenThreshold;
            for (int j = 0; j < name1Len; j++)
            { // Loop to try and match on char from name1
                final char currChar = name1Char[j];
                for (int k = 0; k < name2Len; k++)
                {
                    // Loop to find char match in name2
                    if (name2Char[k] == currChar)
                    {
                        final int matchSize = (((j == 0) || (k == 0)) ? 0 : lcs[j - 1][k - 1]) + 1;
                        lcs[j][k] = matchSize;
                        if (matchSize > commonMax)
                        {
                            commonMax = matchSize;
                            commonTmp1 = j;
                            commonTmp2 = k;
                        }
                    }
                }
            }
            if (commonMax == lenThreshold)
            { // IF no char sequences of sufficient len matched,
                break; // THEN quit searching for sequences
            }
            commonTotal += commonMax;
            // Incr total numb chars matched
            nameLenRemaining -= commonMax; // Subtract number remaining chars to
                                           // match
            for (int j = commonTmp1 - commonMax + 1; j <= commonTmp1; j++)
            {
                name1Char[j] = 0; // Replace name1 matched char with filler that
                                  // will never match
            }
            for (int j = commonTmp2 - commonMax + 1; j <= commonTmp2; j++)
            {
                name2Char[j] = 1; // Replace matched name2 char with char diff
                                  // from name1 filler
            }
            for (int j = 0; j < name1Len; j++)
            {
                for (int k = 0; k < name2Len; k++)
                {
                    lcs[j][k] = 0;
                }
            }
        }
        return (100 * commonTotal) / nameLenMin; // Calc percentage of matching
                                                 // chars
    }

    public NameResolver() 
    {
    }

    /**
     * Construct <code>NameResolver</code> by parsing single text name value
     * 
     * @param fullName text person name in format &lt;last name&gt;, &lt;<first name>&gt; &lt;middle
     *            initial&gt;
     **/
    public NameResolver( String fullName ) 
    {
        this();
        setFullName(fullName);
    }

    /**
     * Construct <code>NameResolver</code> from individual portions of the name
     * 
     * @param firstName given first name
     * @param lastName last/family name
     **/
    public NameResolver( String firstName, String lastName) 
    {
        this(firstName, null, lastName);
    }

    /**
     * Construct <code>NameResolver</code> from individual portions of the name
     * 
     * @param firstName given first name
     * @param middleName middle name or initial
     * @param lastName last/family name
     **/
    public NameResolver( String firstName, String middleName, String lastName) 
    {
        this(firstName, middleName, lastName, null, null);
    }

    /**
     * Construct <code>NameResolver</code> from individual portions of the name
     * 
     * @param firstName given first name
     * @param middleName middle name or initial
     * @param lastName last/family name
     * @param namePrefix prefix associated with formal name
     * @param nameSuffix suffix associated with formal name
     **/
    public NameResolver( String firstName, String middleName, String lastName, String namePrefix, String nameSuffix) 
    {
        this.firstName  = Util.trim(firstName);
        this.middleName = Util.trim(middleName);
        this.lastName   = Util.trim(lastName); //Save component parts of name
        this.prefix     = Util.trim(namePrefix);
        this.suffix     = Util.trim(nameSuffix);
        initAll();
    }

    /**
     * Construct <code>NameResolver</code> from individual portions of the name
     * 
     * @param firstName
     *            given first name
     * @param middleName
     *            middle name or initial
     * @param lastName
     *            last/family name
     * @param namePrefix
     *            prefix associated with formal name
     * @param nameSuffix
     *            suffix associated with formal name
     * @return the Name
     **/
    public static NameResolver create(String firstName, String middleName, String lastName, String namePrefix, String nameSuffix)
    {
        firstName   = Util.trim(firstName);
        middleName  = Util.trim(middleName);
        lastName    = Util.trim(lastName); // Save component parts of name
        namePrefix  = Util.trim(namePrefix);
        nameSuffix  = Util.trim(nameSuffix);

        if ((firstName == null) && (middleName == null) && (lastName == null) && (namePrefix == null) && (nameSuffix == null))
        {
            return null;
        }

        final NameResolver n = new NameResolver(firstName, middleName, lastName, namePrefix, nameSuffix);
        
        return n.allNull() ? null : n;
    }

    /**
     * Construct <code>NameResolver</code> by parsing single text name value
     * 
     * @param fullName
     *            text person name in format &lt;last name&gt;, &lt;<first
     *            name>&gt; &lt;middle initial&gt;
     * @return the Name
     **/
    public static NameResolver create(String fullName)
    {
        final NameResolver name = fullName == null ? null : new NameResolver(fullName);
        
        return (name == null) || name.allNull() ? null : name;
    }


    /**
     * gets given first name
     * 
     * @return given first name
     **/
    public String getFirstName()
    {
        return this.firstName;
    }

    /**
     * gets the full name
     * 
     * @param includeMiddleName
     *            <code>true</code> to include middle name, <code>false</code>
     *            if no middle name
     * @param includePrefix
     *            <code>true</code> to include prefix, <code>false</code> if no
     *            prefix
     * @param includeSuffix
     *            <code>true</code> to include suffix, <code>false</code> if no
     *            suffix
     * @return full name, according to include parameter rules
     **/
    public String getFullName(final boolean includeMiddleName, final boolean includePrefix, final boolean includeSuffix)
    {
        return getFullName(this.firstName, includeMiddleName ? this.middleName : null, this.lastName, includePrefix ? this.prefix : null, includeSuffix ? this.suffix : null);
    }

    /**
     * gets the full name
     * 
     * @param firstName
     *            given first name
     * @param middleName
     *            middle name or initial
     * @param lastName
     *            last/family name
     * @param prefix
     *            prefix associated with formal name
     * @param suffix
     *            suffix associated with formal name
     * @return full name, according to include parameter rules
     **/
    public static String getFullName(final String firstName, final String middleName, final String lastName, final String prefix, final String suffix)
    {
        String retValue = lastName; // Start with the last name
        String first;

        if (Util.length(suffix) != 0)
        {
            retValue = retValue == null ? suffix : retValue + ' ' + suffix;
        }
        
        if (Util.length(firstName) != 0)
        {
            if (Util.length(prefix) != 0)
            {
                first = firstName == null ? prefix : prefix + ' ' + firstName;
            }
            else
            {
                first = firstName;
            }
        
            retValue = retValue == null ? first : retValue + ", " + first;
            
            if (Util.length(middleName) != 0)
            {
                retValue += ' ' + middleName;
            }
        }
        
        return retValue;
    }

    /**
     * gets entire, formated person name
     * 
     * @return entire, formated person name
     **/
    public String getFullName()
    {
        return getFullName(true, true, true); // Return formated full name
    }

    /**
     * gets entire, formated person name
     * 
     * @return entire, formated person name
     **/
    @Override
    public String toString()
    {
        return getFullName();
    }

    /**
     * gets last/family name
     * 
     * @return last/family name
     **/
    public String getLastName()
    {
        return this.lastName;
    }

    /**
     * gets middle name or initial
     * 
     * @return middle name or initial
     **/
    public String getMiddleName()
    {
        return this.middleName;
    }

    /**
     * get name prefix, like "Dr."
     * 
     * @return name prefix, like "Dr."
     **/
    public String getPrefix()
    {
        return this.prefix;
    }

    /**
     * get name suffix, like "DDS"
     * 
     * @return name suffix, like "DDS"
     **/
    public String getSuffix()
    {
        return this.suffix;
    }

    /**
     * Initializes the fake name flag and the baby name flag
     **/
    private void initFake()
    {
        this.fake = checkFakeList();
        this.baby = Util.containsIgnoreCase(BABY_NAME, this.firstName);
    }

    /**
     * checks to see if this name looks like an "unidentified patient" name
     * 
     * @return <code>true</code> means patient's name is in list of common
     *         "unidentified patients" names
     **/
    private boolean checkFakeList()
    {
        final String last = getLastAlpha();
        final String first = getFirstAlpha();
        
        if ("EXPO".equalsIgnoreCase(last) && "EXPO".equalsIgnoreCase(first))
        {
            /*
             * https://tools.regenstrief.org/jira/browse/CORE-765 See a few of
             * this as last name that appear to be real. When fake, first name
             * is usually followed by numbers. getFirstAlpha() would remove
             * those, though, so we can use equalsIgnoreCase instead of
             * startsWith.
             */
            return true;
        }
        
        return checkFakeList(last) || checkFakeList(first);
    }

    /**
     * checks to see if the name component looks like an "unidentified patient"
     * name
     * 
     * @param name
     *            the name component
     * @return <code>true</code> means patient's name is in list of common
     *         "unidentified patients" names
     **/
    private boolean checkFakeList(final String name)
    {
        if (Util.isEmpty(name))
        { 
            // Names must contain at least one letter
            return true;
        }
        
        for (int i = 0; i < FAKE_NAME.length; i++)
        {
            if (name.equalsIgnoreCase(FAKE_NAME[i]))
            { 
                // Names cannot be in the fake list
                return true;
            }
        }
        
        return false;
    }

    /**
     * checks to see if this name looks like an "unidentified patient" name
     * 
     * @return <code>true</code> means patient's name is in list of common
     *         "unidentified patients" names
     **/
    public boolean isFakeName()
    {
        return this.fake;
    }

    /**
     * checks to see if this name looks like an infant that has not yet been
     * named
     * 
     * @return <code>true</code> means patient's name is in list of common baby
     *         names
     **/
    public boolean isBabyName()
    {
        return this.baby;
    }

    /**
     * matches this Name withe person's name in the matchName argument by
     * matching the longest possible sequence of characters from each name,
     * removing the matched sequence, and repeating the process. The returned
     * <code>int</code> value represents the match character "percentage".
     * 
     * @param m
     *            full person name to match to this Name
     * @return the match character "percentage"
     **/
    public int longestCommonNameMatch( NameResolver m)
    {
        String ln = getLastName();
        String fn = getFirstName();
        
        if ((ln == null) || (fn == null) || (m == null))
        {
            return 0;
        }
        
        String mln = m.getLastName();
        String mfn = m.getFirstName();
        
        if ((mln == null) || (mfn == null))
        {
            return 0;
        }
        
        int lnl     = ln.length();
        int fnl     = fn.length();
        int mlnl    = mln.length();
        int mfnl    = mfn.length();
        
        if (((lnl + fnl) < MIN_MATCH_SIZE) || ((mlnl + mfnl) < MIN_MATCH_SIZE))
        { 
            // Short names must match exactly
            if (ln.equalsIgnoreCase(mln) && fn.equalsIgnoreCase(mfn))
            {
                return 100;
            }
            else
            {
                return ln.equalsIgnoreCase(mfn) && fn.equalsIgnoreCase(mln) ? 100 : 0;
            }
        }
        
        int matchScore1 = longestCommonNameMatch(getFullName(), m.getFullName());
        int matchScore2 = 0;
        
        if (matchScore1 < 100)
        {
            String mn   = getMiddleName();
            String mmn  = m.getMiddleName();
            
            if (((mn == null) && (mmn != null)) || ((mn != null) && (mmn == null)))
            {
                matchScore2 = longestCommonNameMatch(getFullName(false, false, false), m.getFullName(false, false, false));
            }
        }
        
        return Math.max(matchScore1, matchScore2);
    }

    /**
     * sets given first name
     * 
     * @param firstName
     *            given first name
     **/
    public void setFirstName(final String firstName)
    {
        this.firstName = firstName;
        initFirst();
    }

    /**
     * sets full person name
     * 
     * @param fullName
     *            text person name in format &lt;last name&gt;, &lt;<first
     *            name>&gt; &lt;middle initial&gt;
     **/
    public void setFullName(final String fullName)
    {
        String txt = formatFullName(fullName); // Get rid of leading/trailing
                                               // spaces and bad characters,
                                               // merge consecutive spaces
        String firstAndMiddle, middleToken, lastToken;
        int i, j;
        boolean foundSuffix, foundPrefix;

        if (Util.isEmpty(txt))
        {
            this.lastName = null;
            this.firstName = null;
            this.middleName = null;
            initAll();
            return;
        }
        i = txt.indexOf(','); // Find comma between last and first name
        if (i > -1)
        {
            this.lastName = txt.substring(0, i); // last name could contain
                                                 // suffixes, but these will be
                                                 // parsed during initAll which
                                                 // is called before returning
            firstAndMiddle = txt.substring(i + 1);
        }
        else
        {
            i = txt.indexOf(' '); // *ELSE find first space
            if (i < 0)
            {
                this.lastName = txt;
                this.firstName = null;
                this.middleName = null;
                initAll();
                return;
            }

            lastToken = txt.substring(0, i);
            firstAndMiddle = txt.substring(i + 1);
            this.lastName = lastToken;
            if ((i < txt.length()) && isCommonLastNamePrefix(this.lastName))
            {
                setFullName(firstAndMiddle);
                if (this.firstName != null)
                {
                    setLastName(lastToken + ' ' + this.lastName);
                    return;
                }
                this.lastName = lastToken;
            }
        }
        do
        {
            txt = Util.trim(firstAndMiddle);
            i = txt == null ? -1 : txt.indexOf(' ');
            if (i == -1)
            {
                this.firstName = txt;
                this.middleName = null;
            }
            else
            {
                this.firstName = txt.substring(0, i);
                this.middleName = txt.substring(i + 1);
            }
            foundPrefix = false;
            foundSuffix = isCommonSuffix(this.firstName);
            if (foundSuffix)
            {
                this.suffix = this.suffix == null ? this.firstName : this.suffix + ' ' + this.firstName;
                firstAndMiddle = this.middleName;
            }
            else
            {
                foundPrefix = isCommonPrefix(this.firstName);
                if (foundPrefix)
                {
                    this.prefix = this.prefix == null ? this.firstName : this.prefix + ' ' + this.firstName;
                    firstAndMiddle = this.middleName;
                }
                else if (this.middleName != null)
                {
                    i = this.middleName.lastIndexOf(' ');
                    if (i >= 0)
                    {
                        middleToken = this.middleName.substring(i + 1);
                        foundSuffix = isCommonSuffix(middleToken);
                        if (foundSuffix)
                        {
                            this.middleName = this.middleName.substring(0, i);
                            this.suffix = this.suffix == null ? middleToken : middleToken + ' ' + this.suffix;
                            firstAndMiddle = firstAndMiddle.substring(0, firstAndMiddle.lastIndexOf(' '));
                        }
                        else if (isQuestionableSuffix(middleToken))
                        {
                            // Only allow a questionable suffix if it is
                            // preceded by a common suffix
                            j = this.middleName.lastIndexOf(' ', i - 1);
                            if (j < 0)
                            {
                                j = -1;
                            }
                            foundSuffix = isCommonSuffix(this.middleName.substring(j + 1, i));
                            if (foundSuffix)
                            {
                                middleToken = this.middleName.substring(j + 1);
                                this.middleName = j <= 0 ? null : this.middleName.substring(0, j);
                                this.suffix = this.suffix == null ? middleToken : middleToken + ' ' + this.suffix;
                                firstAndMiddle = firstAndMiddle.substring(0, firstAndMiddle.length() - middleToken.length() - 1);
                            }
                        }
                    }
                    else
                    {
                        foundSuffix = isCommonSuffix(this.middleName);
                        if (foundSuffix)
                        {
                            this.suffix = this.middleName;
                            this.middleName = null;
                            firstAndMiddle = this.firstName;
                        }
                    }
                }
            }
        }
        while (foundSuffix || foundPrefix);
        initAll();
    }

    /**
     * Retrieves whether the given String is in the given cache
     * 
     * @param cache
     *            the cache
     * @param s
     *            the String
     * @return whether the String is in the cache
     **/
    private static boolean inCache(final String[] cache, final String s)
    {
        return Util.containsIgnoreCase(cache, trimName(s));
    }

    /**
     * Retrieves whether the given String is a common prefix
     * 
     * @param s
     *            the String
     * @return whether the String is a common prefix
     **/
    public static boolean isCommonPrefix(final String s)
    {
        return inCache(PREFIX, s);
    }

    /**
     * Retrieves whether the given String is a common suffix
     * 
     * @param s
     *            the String
     * @return whether the String is a common suffix
     **/
    public static boolean isCommonSuffix(final String s)
    {
        return inCache(SUFFIX, Util.remove(s, '.'));
    }

    /**
     * Retrieves whether the given String is a questionable suffix
     * 
     * @param s
     *            the String
     * @return whether the String is a questionable suffix
     **/
    public static boolean isQuestionableSuffix(final String s)
    {
        return inCache(QUESTIONABLE_SUFFIX, s);
    }

    /**
     * Retrieves whether the given String is a common prefix to last names
     * 
     * @param s
     *            the String
     * @return whether the String is a common prefix to last names
     **/
    public static boolean isCommonLastNamePrefix(final String s)
    {
        return inCache(LAST_NAME_PREFIX, s);
    }

    /**
     * modifies last/family name
     * 
     * @param lastName
     *            last/family name
     **/
    public void setLastName(final String lastName)
    {
        this.lastName = lastName;
        initLast();
    }

    /**
     * modifies middle name or initial
     * 
     * @param middleName
     *            middle name or initial
     **/
    public void setMiddleName(final String middleName)
    {
        this.middleName = middleName;
        initMiddle();
    }

    /**
     * modifies name prefix, like "Dr."
     * 
     * @param prefix
     *            new name prefix, like "Dr."
     **/
    public void setPrefix(final String prefix)
    {
        this.prefix = prefix;
        clearPrefix();
    }

    /**
     * modifies name suffix, like "DDS"
     * 
     * @param suffix
     *            new name suffix, like "DDS"
     **/
    public void setSuffix(final String suffix)
    {
        this.suffix = suffix;
        clearSuffix();
    }

    /**
     * Returns whether or not two names have equal NYSIIS codes
     * 
     * @param matchName
     *            the name to compare to this
     * @return true if the names have equal NYSIIS codes, false otherwise
     **/
    public boolean equalNYSIIS(final NameResolver matchName)
    {
        // If any name is null, they can't be compared, so no match
        if ((getFirstName() == null) || (getLastName() == null) || (matchName == null) || (matchName.getFirstName() == null) || (matchName.getLastName() == null))
        {
            return false;
        }

        final String firstNameNYSIIS = getFirstNYSIIS(), lastNameNYSIIS = getLastNYSIIS();
        final String matchFirstNameNYSIIS = matchName.getFirstNYSIIS(), matchLastNameNYSIIS = matchName.getLastNYSIIS();
        final boolean lastNameMatch = lastNameNYSIIS.equals(matchLastNameNYSIIS);

        // There's a match if the first names match and the last names match
        // or if this first name matches matchName's last name and vice versa
        if ((firstNameNYSIIS.equals(matchFirstNameNYSIIS) && lastNameMatch))
        {
            return true;
        }
        else if ((firstNameNYSIIS.equals(matchLastNameNYSIIS) && lastNameNYSIIS.equals(matchFirstNameNYSIIS)))
        {
            return true;
        }
        else if (!lastNameMatch)
        {
            // If the last names didn't match, the names don't match
            return false;
        }
        else if ((getMiddleName() != null) && (getMiddleNYSIIS().equals(matchFirstNameNYSIIS)))
        {
            // If the last names matched & either first name matches the other's
            // middle name, the names match
            return true;
        }
        else if ((matchName.getMiddleName() != null) && (matchName.getMiddleNYSIIS().equals(firstNameNYSIIS)))
        {
            return true;
        }

        return false;
    }

    /**
     * Strips the non letter characters from a string
     * 
     * @param s
     *            the string to strip
     * @return the stripped string
     **/
    private static String removeNonLetterCharacters(String s)
    {
        if (s == null)
        {
            return s;
        }

        for (int i = 0; i < s.length(); i++)
        {
            if (!Character.isLetter(s.charAt(i)))
            {
                s = removeCharAt(s, i);
                i--;
            }
        }

        return s.toUpperCase();
    }

    /**
     * Returns the NYSIIS code for a given name as described at:
     * http://www.dropby.com/NYSIIS.html
     * 
     * @param nameString
     *            the name to generate a NYSIIS code for
     * @return the NYSIIS code
     **/
    public static String getNYSIIS(final String nameString)
    {
        return getNYSIISOfAlphaName(removeNonLetterCharacters(nameString));
    }

    /**
     * Returns the NYSIIS code for a given name with non-letter characters
     * removed as described at: http://www.dropby.com/NYSIIS.html
     * 
     * @param nameString
     *            the name to generate a NYSIIS code for
     * @return the NYSIIS code
     **/
    private static String getNYSIISOfAlphaName(final String nameString)
    {
        int i = 0;
        String code = nameString;
        String tempString = null;
        String tempString2 = null;

        if (Util.isEmpty(code))
        {
            return code;
        }
        final char firstChar = code.charAt(0);

        // 1. remove all 'S' and 'Z' chars from the end of the surname
        for (i = code.length() - 1; i >= 1; i--)
        {
            if ((code.charAt(i) != 'S') && (code.charAt(i) != 'Z'))
            {
                break;
            }
        }
        code = code.substring(0, i + 1);

        // 2. transcode initial strings
        // MAC => MC
        // PF => F
        if (code.startsWith("MAC"))
        {
            code = "MC" + code.substring(3, code.length());
        }
        else if (code.startsWith("PF"))
        {
            code = "F" + code.substring(2, code.length());
        }

        // 3. Transcode trailing strings as follows,
        // IX => IC
        // EX => EC
        // YE,EE,IE => Y
        // NT,ND => N
        // RT,RD => D
        // DT => T
        if (code.length() > 1)
        {
            tempString = code.substring(0, code.length() - 2);
            if (code.endsWith("IX"))
            {
                code = tempString + "IC";
            }
            else if (code.endsWith("EX"))
            {
                code = tempString + "EC";
            }
            else if (code.endsWith("YE") || code.endsWith("EE") || code.endsWith("IE"))
            {
                code = tempString + "Y";
            }
            else if (code.endsWith("NT") || code.endsWith("ND"))
            {
                code = tempString + "N";
            }
            else if (code.endsWith("RT") || code.endsWith("RD"))
            {
                code = tempString + "D";
            }
            else if (code.endsWith("DT"))
            {
                code = tempString + "T";
            }
        }

        // 4. transcode 'EV' to 'EF' if not at start of name
        if (code.length() == 0)
        {
            return code;
        }
        tempString = code.substring(0, 1);
        code = tempString + Util.replaceAllExact(code.substring(1, code.length()), "EV", "EF");

        // 5. use first character of name as first character of key
        // no code required

        // 6. remove any 'W' that follows a vowel
        for (i = 1; i < code.length(); i++)
        {
            if ((code.charAt(i) == 'W') && Util.isVowel(code.charAt(i - 1)))
            {
                code = removeCharAt(code, i);
                i--;
            }
        }

        // 7. replace all vowels with 'A'
        code = code.replace('E', 'A');
        code = code.replace('I', 'A');
        code = code.replace('O', 'A');
        code = code.replace('U', 'A');

        // 8. transcode 'GHT' to 'T'
        code = Util.replaceAllExact(code, "GHT", "T");

        // 9. transcode 'DG' to 'G'
        code = Util.replaceAllExact(code, "DG", "G");

        // 10. transcode 'PH' to 'F'
        code = Util.replaceAllExact(code, "PH", "F");

        // 11. if not first character, eliminate all 'H'
        // preceded or followed by a consonant
        for (i = 1; i < code.length(); i++)
        {
            if (code.charAt(i) == 'H')
            {
                if (!Util.isVowel(code.charAt(i - 1)) || ((i < code.length() - 1) && !Util.isVowel(code.charAt(i + 1))))
                {
                    code = removeCharAt(code, i);
                    i--;
                }
            }
        }

        // 12. change 'KN' to 'N', else 'K' to 'C'
        code = Util.replaceAllExact(code, "KN", "N");
        code = code.replace('K', 'C');

        // 13. if not first character, change 'M' to 'N'
        if (code.length() == 0)
        {
            return code;
        }
        tempString = code.substring(0, 1);
        code = tempString + code.substring(1, code.length()).replace('M', 'N');

        // 14. if not first character, change 'Q' to 'G'
        if (code.length() == 0)
        {
            return code;
        }
        tempString = code.substring(0, 1);
        code = tempString + code.substring(1, code.length()).replace('Q', 'G');

        // 15. transcode 'SH' to 'S'
        code = Util.replaceAllExact(code, "SH", "S");

        // 16. transcode 'SCH' to 'S'
        code = Util.replaceAllExact(code, "SCH", "S");

        // 17. transcode 'YW' to 'Y'
        code = Util.replaceAllExact(code, "YW", "Y");

        // 18. if not first or last character, change 'Y' to 'A'
        if (code.length() == 0)
        {
            return code;
        }
        if (code.length() > 2)
        {
            tempString = code.substring(0, 1);
            tempString2 = code.substring(code.length() - 1, code.length());
            code = tempString + code.substring(1, code.length() - 1).replace('Y', 'A') + tempString2;
        }

        // 19. transcode 'WR' to 'R'
        code = Util.replaceAllExact(code, "WR", "R");

        // 20. if not first character, change 'Z' to 'S'
        tempString = code.substring(0, 1);
        code = tempString + code.substring(1, code.length()).replace('Z', 'S');

        // 21. transcode terminal 'AY' to 'Y'
        if (code.endsWith("AY"))
        {
            code = code.substring(0, code.length() - 2) + "Y";
        }

        // 22. remove trailing vowels
        if (code.length() == 0)
        {
            return code;
        }
        while ((code.length() > 1) && Util.isVowel(code.charAt(code.length() - 1)))
        {
            code = code.substring(0, code.length() - 1);
            if (code.length() == 0)
            {
                return code;
            }
        }

        // 23. collapse all strings of repeated characters
        for (i = 0; i < code.length() - 1; i++)
        {
            if (code.charAt(i) == code.charAt(i + 1))
            {
                code = removeCharAt(code, i);
                i--;
            }
        }

        // 24. if first char of original surname was a vowel,
        // append it to the code
        if (Util.isVowel(firstChar))
        {
            if (code.charAt(0) == 'A')
            {
                code = removeCharAt(code, 0);
            }
            code = firstChar + code;
        }

        return code;
    }

    /**
     * Removes the character at a given index from a string.
     * 
     * @param str
     *            the string from which the character should be removed
     * @param i
     *            the index of the character to remove
     * @return true string after the character has been removed
     **/
    public static String removeCharAt(final String str, final int i)
    {
        if (i == str.length() - 1)
        {
            return str.substring(0, i);
        }

        return str.substring(0, i) + str.substring(i + 1, str.length());
    }

    /**
     * Merges nam with this Name; any null values in this are replaced by values
     * from nam
     * 
     * @param nam
     *            the Name to merge with this
     **/
    public void merge(final NameResolver nam)
    {
        if (nam == null)
        {
            return;
        }

        if (this.firstName == null)
        {
            this.firstName = nam.getFirstName();
        }
        if (this.middleName == null)
        {
            this.middleName = nam.getMiddleName();
        }
        if (this.lastName == null)
        {
            this.lastName = nam.getLastName();
        }
        if (this.prefix == null)
        {
            this.prefix = nam.getPrefix();
        }
        if (this.suffix == null)
        {
            this.suffix = nam.getSuffix();
        }
        initAll();
    }

    /**
     * Constructs a new NameResolver as a copy of nam
     * 
     * @param nam the Name to copy
     **/
    public NameResolver( NameResolver nam) 
    {
        if (nam == null) 
        {
            return;
        }
        
        this.firstName = nam.getFirstName();
        this.middleName = nam.getMiddleName();
        this.lastName = nam.getLastName();
        this.prefix = nam.getPrefix();
        this.suffix = nam.getSuffix();
        initAll();
    }

    /**
     * Determines if o equals this
     * 
     * @param o
     *            Object
     * @return whether or not o equals this
     **/
    @Override
    public boolean equals(final Object o)
    {
        if (!(o instanceof NameResolver))
        {
            return false;
        }
        final NameResolver nam = (NameResolver) o;
        if (!equals(this.firstName, nam.getFirstName()))
        {
            return false;
        }
        else if (!equals(this.middleName, nam.getMiddleName()))
        {
            return false;
        }
        else if (!equals(this.lastName, nam.getLastName()))
        {
            return false;
        }
        else if (!equals(this.prefix, nam.getPrefix()))
        {
            return false;
        }
        else if (!equals(this.suffix, nam.getSuffix()))
        {
            return false;
        }

        return true;
    }

    private boolean equals(final String s1, final String s2)
    {
        return Util.isEmpty(s1) ? Util.isEmpty(s2) : Util.equalsIgnoreCase(s1, s2);
    }

    /**
     * Retrieves whether the names match (requires exact String matching, but
     * allows swapping of fields and partial middle names)
     * 
     * @param nam
     *            the Name to compare to this
     * @return whether the names match
     **/
    public boolean matches(final NameResolver nam)
    {
        final String last = getLastAlpha(), first = getFirstAlpha(), middle = getMiddleAlpha();
        final String candLast = nam.getLastAlpha(), candFirst = nam.getFirstAlpha(), candMiddle = nam.getMiddleAlpha();
        final boolean lastMatch = Util.equalsIgnoreCase(last, candLast);
        final boolean middleMatch = presentDataMatches(middle, candMiddle);

        if (middleMatch)
        {
            if (Util.equalsIgnoreCase(first, candFirst) && lastMatch)
            {
                return true; // Same first names, same last names,
                             // non-conflicting middle names
            }
            else if (Util.equalsIgnoreCase(first, candLast) && Util.equalsIgnoreCase(last, candFirst))
            {
                return true; // First and last names same but swapped,
                             // non-conflicting middle names
            }
        }

        if (lastMatch)
        {
            if (babyWildCardAllowed && middleMatch && (this.baby || nam.isBabyName()) && (!this.fake && !nam.isFakeName()))
            {
                return true; // Same non-fake last names with an infant first
                             // name
            }
            final boolean firstMiddleMatch = Util.equalsIgnoreCase(first, candMiddle);
            final boolean middleFirstMatch = Util.equalsIgnoreCase(middle, candFirst);
            if (firstMiddleMatch && middleFirstMatch)
            {
                return true; // Same last names, first and middle names same but
                             // swapped
            }
            else if (firstMiddleMatch && presentInitialMatches(middle, candFirst))
            {
                return true; // Same last names, first and middle names
                             // non-conflicting but swapped
            }
            else if (middleFirstMatch && presentInitialMatches(first, candMiddle))
            {
                return true; // Same last names, first and middle names
                             // non-conflicting but swapped
            }
        }

        return false;
    }

    public final static boolean isBabyWildCardAllowed()
    {
        return babyWildCardAllowed;
    }

    public final static void setBabyWildCardAllowed(final boolean babyWildCardAllowed)
    {
        NameResolver.babyWildCardAllowed = babyWildCardAllowed;
    }

    /**
     * Retrieves whether the amount of data (none, initial, or whole name)
     * present for two names matches
     * 
     * @param name1
     *            a name String
     * @param name2
     *            another name String
     * @return whether the names match
     **/
    private final static boolean presentDataMatches(final String name1, final String name2)
    {
        return presentInitialMatches(name1, name2) ? true : name1.equalsIgnoreCase(name2);
    }

    /**
     * Retrieves whether one name is null or an initial that matches another
     * name
     * 
     * @param name1
     *            a name String
     * @param name2
     *            another name String
     * @return whether one name is null or an initial that matches another name
     **/
    private final static boolean presentInitialMatches(final String name1, final String name2)
    {
        final int len1 = Util.length(name1), len2 = Util.length(name2);

        if ((len1 == 0) || (len2 == 0))
        {
            return true; // Can match if one name is null
        }
        else if (((len1 == 1) || (len2 == 1)) && (name1.charAt(0) == name2.charAt(0)))
        {
            return true; // Can match if one name is just a matching initial
        }

        return false;
    }

    /**
     * Returns the hash code for the name
     * 
     * @return the hash code
     **/
    @Override
    public int hashCode()
    {
        return getFullName().hashCode();
    }

    /**
     * Determines if all fields of the name are null
     * 
     * @return true if all fields are null, false otherwise
     **/
    public boolean allNull()
    {
        return ((this.firstName == null) && (this.middleName == null) && (this.lastName == null) && (this.prefix == null) && (this.suffix == null));
    }

    /**
     * Returns a new name with the sames fields as this name, but upper case
     * 
     * @return the upper case Name
     **/
    public NameResolver toUpperCase()
    {
        final NameResolver upperCaseName = copy();

        if (upperCaseName.lastName != null)
        {
            upperCaseName.lastName = upperCaseName.lastName.toUpperCase();
        }
        if (upperCaseName.firstName != null)
        {
            upperCaseName.firstName = upperCaseName.firstName.toUpperCase();
        }
        if (upperCaseName.middleName != null)
        {
            upperCaseName.middleName = upperCaseName.middleName.toUpperCase();
        }
        if (upperCaseName.prefix != null)
        {
            upperCaseName.prefix = upperCaseName.prefix.toUpperCase();
        }
        if (upperCaseName.suffix != null)
        {
            upperCaseName.suffix = upperCaseName.suffix.toUpperCase();
        }
        upperCaseName.initAll();
        return upperCaseName;
    }

    protected NameResolver copy()
    {
        return new NameResolver(this);
    }

    /**
     * Retrieves the name's initials
     * 
     * @return the name's initials
     **/
    public String getInitials()
    {
        String initials = "";

        if (this.firstName != null)
        {
            initials += this.firstName.charAt(0);
        }
        if (this.middleName != null)
        {
            initials += this.middleName.charAt(0);
        }
        if (this.lastName != null)
        {
            initials += this.lastName.charAt(0);
        }

        return initials.length() == 0 ? null : initials.toUpperCase();
    }

    /**
     * This (or initAll) should be called whenever the first name changes
     **/
    private void initFirst()
    {
        initFake();
        clearFirst();
    }

    /**
     * Clears values that will need to be recomputed when the first name changes
     **/
    private void clearFirst()
    {
        this.firstName = Util.trim(this.firstName);
        if ((this.prefix == null) && (this.firstName != null))
        {
            final int i = this.firstName.indexOf(' ');
            if (i > 0)
            {
                final String txt = this.firstName.substring(0, i);
                if (isCommonPrefix(txt))
                {
                    this.prefix = txt;
                    this.firstName = this.firstName.substring(i + 1).trim();
                    clearPrefix();
                }
            }
        }

        this.firstName = parseSuffixes(this.firstName);
        this.firstNYSIIS = null;
        this.firstAlpha = null;
    }

    /**
     * This (or initAll) should be called whenever the middle name changes
     **/
    private void initMiddle()
    {
        clearMiddle();
    }

    /**
     * Clears values that will need to be recomputed when the middle name
     * changes
     **/
    private void clearMiddle()
    {
        this.middleName = parseSuffixes(this.middleName);
        this.middleNYSIIS = null;
        this.middleAlpha = null;
    }

    private final static boolean valid(final char c)
    {
        // CORE-1970, don't trim digits
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) || (c == '-') || ((c >= '0') && (c <= '9'));
    }

    /**
     * Retrieves whether the character should be left by the trimName method
     * 
     * @param c
     *            the character
     * @return whether the character should be left by the trimName method
     **/
    private final static boolean untrimmable(final char c)
    {
        return valid(c) || (c == '_');
    }

    /**
     * Trims unwanted characters from the beginning and end of the name
     * 
     * @param s
     *            the name
     * @return the trimmed name
     **/
    public final static String trimName(final String s)
    {
        int start, stop;

        if ((s == null) || isGibberish(s))
        {
            return s;
        }
        final int last = s.length() - 1;
        for (start = 0; start <= last; start++)
        {
            if (untrimmable(s.charAt(start)))
            {
                break;
            }
        }
        if (start > last)
        {
            return null;
        }
        for (stop = last; true; stop--)
        {
            if (untrimmable(s.charAt(stop)))
            {
                break;
            }
        }

        return (start == 0) && (stop == last) ? s : s.substring(start, stop + 1);
    }

    /**
     * Retrieves whether the character is a letter, space, or '-' (or ',' or '.'
     * when the character is from a full name)
     * 
     * @param c
     *            the character
     * @param fullName
     *            whether the character is from a full name
     * @return whether the character is good for use in a name
     **/
    private final static boolean goodChar(final char c, final boolean fullName)
    {
        return valid(c) || (c == ' ') || (fullName && ((c == '.') || (c == ',')));
    }

    /**
     * Removes unwanted characters from the name
     * 
     * @param s
     *            the name
     * @return the formatted name
     **/
    protected final static String formatName(final String s)
    {
        return formatName(s, false);
    }

    /**
     * Removes unwanted characters from the name, allowing ',' and '.' which are
     * used by the full-name parser
     * 
     * @param s
     *            the name
     * @return the formatted name
     **/
    private final static String formatFullName(final String s)
    {
        return formatName(s, true);
    }

    private final static boolean isGibberish(final String s)
    {
        final int len = Util.length(s);
        boolean gib = false;
        for (int i = 0; i < len; i++)
        {
            final char c = s.charAt(i);
            if (Character.isLetter(c))
            {
                return false;
            }
            else if ((c != ' ') && (c != '.') && (c != ','))
            {
                gib = true; // Has some gibberish, but only return true if all
                            // gibberish
            }
        }
        return gib;
    }

    /**
     * Removes unwanted characters from the name
     * 
     * @param s
     *            the name
     * @param fullName
     *            whether the character is from a full name
     * @return the formatted name
     **/
    protected final static String formatName(String s, final boolean fullName)
    {
        final int len = Util.length(s);
        if (len == 0)
        {
            return null;
        }

        if (isGibberish(s))
        {
            // If there are no letters, must be some kind of fake/test name;
            // don't trim to nothing or just dashes
            return collapseName(s);
        }

        // Convert NAME chars
        StringBuilder sb = null;
        char[] chars = null;
        int start = 0;
        for (int i = 0; i < len; i++)
        {
            final char currChar = s.charAt(i);
            if (!goodChar(currChar, fullName))
            {
                if (chars == null)
                {
                    sb = new StringBuilder(len);
                    chars = s.toCharArray();
                }
                sb.append(chars, start, i - start);
                switch (currChar)
                {
                case '_': // "_" to "-"
                    sb.append('-');
                    break;
                case '.': // "." to " "
                case ',': // "," to " "
                    sb.append(' ');
                    break;
                default: // Strip out all NAME chars except for letters, spaces,
                         // and dashes
                    break;
                }
                start = i + 1;
            }
        }
        if (sb != null)
        {
            if (start < len)
            {
                sb.append(chars, start, len - start);
            }
            s = sb.toString();
        }
        return collapseName(s);
    }

    private final static String collapseName(String s)
    {
        // Strip off leading spaces, trailing spaces
        s = Util.trim(s);

        // Convert multiple spaces to a single space wherever they occur
        if (s != null)
        {
            s = PAT_MULTI_SPACE.matcher(s).replaceAll(" ");
        }

        return s;
    }

    /**
     * This (or initAll) should be called whenever the last name changes
     **/
    private void initLast()
    {
        initFake();
        clearLast();
    }

    /**
     * Clears values that will need to be recomputed when the last name changes
     **/
    protected String parseSuffixes(String name)
    {
        int i;

        name = trimName(name);
        if (name != null)
        {
            while ((i = Math.max(name.lastIndexOf(' '), name.lastIndexOf(','))) != -1)
            {
                final String txt = name.substring(i + 1);
                if (isCommonSuffix(txt))
                {
                    this.suffix = this.suffix == null ? txt : txt + ' ' + this.suffix;
                    name = trimName(name.substring(0, i));
                }
                else
                {
                    break;
                }
            }
            clearSuffix();
        }

        return formatName(name);
    }

    /**
     * Clears values that will need to be recomputed when the last name changes
     **/
    private void clearLast()
    {
        this.lastName = parseSuffixes(this.lastName);
        this.lastNYSIIS = null;
        this.lastAlpha = null;
    }

    /**
     * Should be called whenever the suffix is changed
     **/
    private void clearSuffix()
    {
        this.suffix = truncatePrefixOrSuffix(formatName(Util.remove(this.suffix, '.')));
    }

    /**
     * Should be called whenever the prefix is changed
     **/
    private void clearPrefix()
    {
        this.prefix = truncatePrefixOrSuffix(formatName(this.prefix));
    }

    /**
     * Removes tokens from the given String if it is too long
     * 
     * @param s
     *            the prefix or suffix String
     * @return the truncated prefix or suffix String
     **/
    public static String truncatePrefixOrSuffix(String s)
    {
        if (Util.length(s) > 10)
        {
            final int i = s.lastIndexOf(' ', 9);
            if (i > 0)
            {
                s = s.substring(0, i);
            }
            else
            {
                throw new RuntimeException("Prefix or suffix too long: " + s);
            }
        }

        return s;
    }

    /**
     * This should be called whenever the first, middle, and last name change
     **/
    private void initAll()
    {
        initFake();
        clearFirst();
        clearMiddle();
        clearLast();
        clearSuffix();
        clearPrefix();
    }

    /**
     * Retrieves the NYSIIS code of the first name, computing it if needed
     * 
     * @return the NYSIIS code of the first name
     **/
    public String getFirstNYSIIS()
    {
        if (this.firstNYSIIS == null)
        {
            this.firstNYSIIS = NameResolver.getNYSIISOfAlphaName(getFirstAlpha());
        }
        return this.firstNYSIIS;
    }

    /**
     * Retrieves the NYSIIS code of the middle name, computing it if needed
     * 
     * @return the NYSIIS code of the middle name
     **/
    public String getMiddleNYSIIS()
    {
        if (this.middleNYSIIS == null)
        {
            this.middleNYSIIS = NameResolver.getNYSIISOfAlphaName(getMiddleAlpha());
        }
        return this.middleNYSIIS;
    }

    /**
     * Retrieves the NYSIIS code of the last name, computing it if needed
     * 
     * @return the NYSIIS code of the last name
     **/
    public String getLastNYSIIS()
    {
        if (this.lastNYSIIS == null)
        {
            this.lastNYSIIS = NameResolver.getNYSIISOfAlphaName(getLastAlpha());
        }
        return this.lastNYSIIS;
    }

    /**
     * Retrieves the first name with non-letter characters removed, computing it
     * if needed
     * 
     * @return the first name with non-letter characters removed
     **/
    public String getFirstAlpha()
    {
        if (this.firstAlpha == null)
        {
            this.firstAlpha = NameResolver.removeNonLetterCharacters(this.firstName);
        }
        return this.firstAlpha;
    }

    /**
     * Retrieves the middle name with non-letter characters removed, computing
     * it if needed
     * 
     * @return the middle name with non-letter characters removed
     **/
    public String getMiddleAlpha()
    {
        if (this.middleAlpha == null)
        {
            this.middleAlpha = NameResolver.removeNonLetterCharacters(this.middleName);
        }
        return this.middleAlpha;
    }

    /**
     * Retrieves the last name with non-letter characters removed, computing it
     * if needed
     * 
     * @return the last name with non-letter characters removed
     **/
    public String getLastAlpha()
    {
        if (this.lastAlpha == null)
        {
            this.lastAlpha = NameResolver.removeNonLetterCharacters(this.lastName);
        }
        return this.lastAlpha;
    }

    /**
     * Retrieves the name as a String formatted "lastName,firstName middleName",
     * with non-letter characters removed from the name
     * 
     * @return the name as a String
     **/
    public String getFullNameRemoveNonLetterCharacters()
    {
        final String last = getLastAlpha(), first = getFirstAlpha(), middle = getMiddleAlpha();
        final StringBuilder name = new StringBuilder(2 + Util.length(last) + Util.length(first) + Util.length(middle));

        if (Util.isValued(last))
        {
            name.append(last);
        }
        name.append(',');

        if (Util.isValued(first))
        {
            name.append(first);
        }
        name.append(' ');

        if (Util.isValued(middle))
        {
            name.append(middle);
        }

        return name.toString();
    }



    /**
     * Creates the common first name cache
     * 
     * @param c
     *            the Collection of common first name Strings
     **/
    public static void setCommonFirstNameCache(final Collection<String> c)
    {
        commonFirstNameCache = new HashSet<String>(c);
    }

    /**
     * Creates the common last name cache
     * 
     * @param c
     *            the Collection of common last name Strings
     **/
    public static void setCommonLastNameCache(final Collection<String> c)
    {
        commonLastNameCache = new HashSet<String>(c);
    }

    /**
     * Retrieves the common first name cache
     * 
     * @return the common first name cache Set
     **/
    public static Set<String> getCommonFirstNameCache()
    {
        return commonFirstNameCache;
    }

    /**
     * Retrieves the common last name cache
     * 
     * @return the common last name cache Set
     **/
    public static Set<String> getCommonLastNameCache()
    {
        return commonLastNameCache;
    }

    /**
     * Retrieves whether the name is common
     * 
     * @return whether the name is common
     **/
    public boolean isCommon()
    {
        final Set<String> fc = commonFirstNameCache, lc = commonLastNameCache;
        if ((fc == null) && (lc == null))
        {
            return true;
        }
        final String f = getFirstAlpha(), l = getLastAlpha();
        // If a Person's first and last names are accidentally switched, that
        // should not affect commonness of the name
        // return (fc.contains(f) && lc.contains(l)) || (fc.contains(l) &&
        // lc.contains(f));
        // CORE-1253, rules are more conservative for common names, so safest
        // thing is to call Name common if any part is
        return fc.contains(f) || lc.contains(l) || fc.contains(l) || lc.contains(f);
    }

    /**
     * Retrieves whether a name is fake
     * 
     * @param name
     *            the name
     * @return whether the name is fake
     **/
    public static boolean isFakeName(final NameResolver name)
    {
        return (name == null) || name.isFakeName();
    }

    /**
     * Clears the name
     **/
    public void clear()
    {
        setFirstName(null);
        setMiddleName(null);
        setLastName(null);
        setPrefix(null);
        setSuffix(null);
    }

    /**
     * Truncates the NameResolver to the size allowed by the database
     * 
     * @return the NameResolver
     **/
    public NameResolver truncate()
    {
        this.firstName = Util.truncate(this.firstName, 40);
        this.middleName = Util.truncate(this.middleName, 40);
        this.lastName = Util.truncate(this.lastName, 40);

        return this;
    }

    /**
     * Truncates the Name to the size allowed by the database
     * 
     * @param name
     *            the Name
     * @return the Name
     **/
    public static NameResolver truncate( NameResolver name)
    {
        return name == null ? null : name.truncate();
    }

    /**
     * Replaces null fields with empty Strings
     **/
    public void unNull()
    {
        this.firstName = Util.unNull(this.firstName);
        this.lastName = Util.unNull(this.lastName);
    }

    public static NameResolver toUpperCase( NameResolver name)
    {
        return name == null ? null : name.toUpperCase();
    }

    public final static String getFirstName(final NameResolver name)
    {
        return name == null ? null : name.getFirstName();
    }

    public final static String getLastName(final NameResolver name)
    {
        return name == null ? null : name.getLastName();
    }
}
