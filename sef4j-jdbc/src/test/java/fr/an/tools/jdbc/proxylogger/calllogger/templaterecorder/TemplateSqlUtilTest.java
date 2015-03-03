package fr.an.tools.jdbc.proxylogger.calllogger.templaterecorder;

import fr.an.tools.jdbclogger.calllogger.templaterecorder.TemplateSqlUtil;

import java.util.regex.Pattern;

import junit.framework.TestCase;

public class TemplateSqlUtilTest extends TestCase {

	public TemplateSqlUtilTest(String name) {
		super(name);
	}

	public void test1() {
		String sql = " SELECT COUNT(1)  FROM VMAT_TITRE WHERE TIT_ID = 218968354 AND TIT_STARTDATE  < to_date('12-05-2010','dd-mm-yyyy') AND (TIT_STOPDATE  > to_date('12-05-2010','dd-mm-yyyy') OR TIT_STOPDATE  IS NULL)";
		String sqlTempl = TemplateSqlUtil.templatizeSqlText(sql);
		assertFalse(sql.equals(sqlTempl));
	}
	
	public void testComp() {
		Pattern re = Pattern.compile(TemplateSqlUtil.comparatorRE); 
		assertMatches("=", re);
	}

	private void assertMatches(String text, Pattern re) {
		boolean matches = re.matcher(text).matches();
		assertTrue("regexp does not match text: text:'" + text + "',  regexp:'" + re + "'", matches);
	}
	
	public void testNumberNotIn() {
		String sql = " insert into MUT_TCS_LINKS select SEQ_HIGH_VALUES.nextval, 0, 81907887, 81907888,  sysdate from dual " 
				+ "where 81907888 not in (select CSL_NEXT_ID from MUT_TCS_LINKS where CSL_PRIOR_ID=81907887) ";
		String sqlTempl = TemplateSqlUtil.templatizeSqlText(sql);
		assertFalse(sql.equals(sqlTempl));

		String sql2 = " insert into MUT_TCS_LINKS select SEQ_HIGH_VALUES.nextval, 0, 81907887, 81907888,  sysdate from dual " 
			+ "where 11111 not in (select CSL_NEXT_ID from MUT_TCS_LINKS where CSL_PRIOR_ID=22222) ";
		String sqlTempl2 = TemplateSqlUtil.templatizeSqlText(sql2);
		assertEquals(sqlTempl2, sqlTempl);
	}

	public void testInsertInto() {
		String sql = " INSERT INTO MUT_CALCULATION_STEP (CAS_ID, CAS_CLASSNAME, CAS_MUTEX_ID, CAS_LEVEL, CAS_POST_DATE, CAS_OBJECT_ID, CAS_SUBOBJECT1_ID, CAS_SUBOBJECT2_ID, CAS_INT_PARAM01, CAS_INT_PARAM02, CAS_DBL_PARAM01, CAS_DBL_PARAM02, CAS_STARTDATE, CAS_COMMAND_ID, CAS_USER_ID, CAS_PRIORITY, CAS_PROPAGATION, CAS_IC_MUTEX_ID, CAS_INT_PARAM03)" 
			+ " VALUES (81907908, 'TCSInvestmentLinkMaster', -1161201843, 1, sysdate, 1192660, 1163742, 0, 0, 0, 0.0, 0.0, to_date(?,'') , 20, 200611528, 51, 1, 0, 0)";
		String sqlTempl = TemplateSqlUtil.templatizeSqlText(sql);
		assertFalse(sql.equals(sqlTempl));

		String sql2 = " INSERT INTO MUT_CALCULATION_STEP (CAS_ID, CAS_CLASSNAME, CAS_MUTEX_ID, CAS_LEVEL, CAS_POST_DATE, CAS_OBJECT_ID, CAS_SUBOBJECT1_ID, CAS_SUBOBJECT2_ID, CAS_INT_PARAM01, CAS_INT_PARAM02, CAS_DBL_PARAM01, CAS_DBL_PARAM02, CAS_STARTDATE, CAS_COMMAND_ID, CAS_USER_ID, CAS_PRIORITY, CAS_PROPAGATION, CAS_IC_MUTEX_ID, CAS_INT_PARAM03) " 
			+ "VALUES (11111, 'TCSInvestmentLinkMaster', -2222222, 333, sysdate, 44444, 5555, 666, 0, 0, 0.0, 0.0, to_date(?,'') , 20, 200611528, 51, 1, 0, 0)";
		String sqlTempl2 = TemplateSqlUtil.templatizeSqlText(sql2);
		assertEquals(sqlTempl2, sqlTempl);
	}

	public void testInsertIntoSelect() {
		String sql = "INSERT INTO lns_compte_values (ctv_compte_id,lyx_day,ctv_nav,ctv_compta,ctv_rp,ctv_accruals,ctv_virtual,ctv_locked,ctv_spot,ctv_virtualprovision) " 
			+ "SELECT 7086943, to_date(?,'') , 0, 0, 0, 0, 0, 0, 1.0, 0 FROM DUAL";
		String sqlTempl = TemplateSqlUtil.templatizeSqlText(sql);
		assertFalse(sql.equals(sqlTempl));

		String sql2 = "INSERT INTO lns_compte_values (ctv_compte_id,lyx_day,ctv_nav,ctv_compta,ctv_rp,ctv_accruals,ctv_virtual,ctv_locked,ctv_spot,ctv_virtualprovision) " 
			+ "SELECT 2222, to_date(?,'') , 0, 0, 0, 0, 0, 0, 1.0, 0 FROM DUAL";
		String sqlTempl2 = TemplateSqlUtil.templatizeSqlText(sql2);
		assertEquals(sqlTempl2, sqlTempl);
	}
	
	public void testSelectNull() {
		String sql = "SELECT  NULL FROM  (SELECT MUT_ID, MUT_ROOT_ID, CEA_AMOUNT,  ABS(CEA_AMOUNT)   -  (SELECT NVL (SUM (ABS(CEA_AMOUNT)), 0)    " 
				+ "FROM MUT_STATUS_PROVISION_REVERSAL,        MUT_TRESOEVENT_STATUS,         MUTATION REV,         MUT_EVENEMENT REV_ETA    " 
				+ "WHERE CEA_STATUS_ID = SPR_STATUS_ID     AND SPR_PROVISION_ID = PRO.MUT_ROOT_ID     AND SPR_STATUS_ID = REV.MUT_ID       " 
				+ " AND REV.MUT_LASTFLAG = 1             AND REV.MUT_TYPESTATUS_ID = 2        " 
				+ "AND CEA_TRADEDATE <=  TO_DATE('16-11-2010', 'dd-mm-yyyy')   " 
				+ "AND REV_ETA.EVT_ID = REV.MUT_ID      AND REV_ETA.EVT_CANCELLEDEVENT_ID IS NULL       AND REV_ETA.EVT_CANCELLINGEVENT_ID IS NULL) REST_AMOUNT,    PVS_NAME, CEA_TRADEDATE, PRV_MATURITYDATE, DEV_SHORTNAME,   ROO_ID, ROO_NAME    FROM MUT_STATUS_PROVISION,   MUT_TRESOEVENT_STATUS,    VMAT_DOM_COMPTE,          MUTATION PRO,             MUT_EVENEMENT,            PROVISION_TYPE            " 
				+ " WHERE CEA_STATUS_ID = PRV_STATUS_ID   AND (DML_ID = CEA_SENDER_ID OR DML_ID = CEA_RECEIVER_ID)  AND PRV_STATUS_ID = MUT_ID   AND MUT_LASTFLAG = 1       AND MUT_TYPESTATUS_ID = 2   AND PVS_ID = 5  AND EVT_ID = MUT_ID   AND EVT_CANCELLEDEVENT_ID IS NULL  AND EVT_CANCELLINGEVENT_ID IS NULL   AND PVS_ID = PRV_PROV_TYPE   AND CEA_TRADEDATE <=  TO_DATE('16-11-2010', 'dd-mm-yyyy') AND ROO_ID  = 9314812 " 
				+ ") PRO  WHERE ABS(REST_AMOUNT) > 0  	templatized key=SELECT  NULL FROM  (SELECT MUT_ID, MUT_ROOT_ID, CEA_AMOUNT,  ABS(CEA_AMOUNT)   -  (SELECT NVL (SUM (ABS(CEA_AMOUNT)), 0)    FROM MUT_STATUS_PROVISION_REVERSAL,        MUT_TRESOEVENT_STATUS,         MUTATION REV,         MUT_EVENEMENT REV_ETA    WHERE CEA_STATUS_ID = SPR_STATUS_ID     AND SPR_PROVISION_ID = PRO.MUT_ROOT_ID     AND SPR_STATUS_ID = REV.MUT_ID       AND REV.MUT_LASTFLAG = ?             AND REV.MUT_TYPESTATUS_ID = ?        AND CEA_TRADEDATE <= to_date(?,'')    AND REV_ETA.EVT_ID = REV.MUT_ID      " 
				+ " AND REV_ETA.EVT_CANCELLEDEVENT_ID IS NULL       AND REV_ETA.EVT_CANCELLINGEVENT_ID IS NULL) REST_AMOUNT,    PVS_NAME, CEA_TRADEDATE, PRV_MATURITYDATE, DEV_SHORTNAME,   ROO_ID, ROO_NAME    FROM MUT_STATUS_PROVISION,   MUT_TRESOEVENT_STATUS,    VMAT_DOM_COMPTE,          MUTATION PRO,             MUT_EVENEMENT,            PROVISION_TYPE            WHERE CEA_STATUS_ID = PRV_STATUS_ID   AND (DML_ID = CEA_SENDER_ID OR DML_ID = CEA_RECEIVER_ID)  AND PRV_STATUS_ID = MUT_ID   AND MUT_LASTFLAG = ?       AND MUT_TYPESTATUS_ID = ?   AND PVS_ID = ?  AND EVT_ID = MUT_ID   AND EVT_CANCELLEDEVENT_ID IS NULL  AND EVT_CANCELLINGEVENT_ID IS NULL   AND PVS_ID = PRV_PROV_TYPE   AND CEA_TRADEDATE <= to_date(?,'')  AND ROO_ID  = ? ) PRO  " 
				+ " WHERE ABS(REST_AMOUNT) > ?";
		String sqlTempl = TemplateSqlUtil.templatizeSqlText(sql);
		assertFalse(sql.equals(sqlTempl));
	}
	
	public void testProc() {
		String sql = "Begin callProc(1234); End";
		String sqlTempl = TemplateSqlUtil.templatizeSqlText(sql);
		assertFalse(sql.equals(sqlTempl));
	}

	public void testProc2() {
		String sql = "Begin callProc('abc'); End";
		String sqlTempl = TemplateSqlUtil.templatizeSqlText(sql);
		assertFalse(sql.equals(sqlTempl));
	}

	public void testProc3() {
		String sql = "Begin callProc('abc', 123); End";
		String sqlTempl = TemplateSqlUtil.templatizeSqlText(sql);
		assertFalse(sql.equals(sqlTempl));
	}

	
}
