package com.my.database;

import org.apache.commons.dbcp.BasicDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.my.vo.EquipmentData;
import com.my.vo.OrderBufferVo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DataSource {
	private static BasicDataSource bds;

	private static String driverClass;

	private static String connectUrl;

	private static String userName;

	private static String password;

	private static String initSize;

	private static String maxActive;

	private static String maxIdel;

	private static String minIdle;

	private static String minEvictableIdleTimeMillis;

	private static String timeBetweenEvictionRunsMillis;

	protected final static Log log = LogFactory.getLog(DataSource.class);

	static {
		init();
	}

	public static void init() {
		Properties p = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("pool.properties");
		try {
			p.load(in);
			driverClass = p.getProperty("driverClass");
			connectUrl = p.getProperty("connectUrl");
			userName = p.getProperty("userName");
			password = p.getProperty("password");
			initSize = p.getProperty("initSize");
			maxActive = p.getProperty("maxActive");
			maxIdel = p.getProperty("maxIdel");
			minIdle = p.getProperty("minIdle");
			minEvictableIdleTimeMillis = p.getProperty("minEvictableIdleTimeMillis");
			timeBetweenEvictionRunsMillis = p.getProperty("timeBetweenEvictionRunsMillis");

			bds = new BasicDataSource();
			bds.setDriverClassName(driverClass);
			bds.setUrl(connectUrl);
			bds.setUsername(userName);
			bds.setPassword(password);
			bds.setInitialSize(Integer.parseInt(initSize));
			bds.setMaxIdle(Integer.parseInt(maxIdel));
			bds.setMaxActive(Integer.parseInt(maxActive));
			bds.setMinIdle(Integer.parseInt(minIdle));
			bds.setMinEvictableIdleTimeMillis(Integer.parseInt(minEvictableIdleTimeMillis));
			bds.setTimeBetweenEvictionRunsMillis(Integer.parseInt(timeBetweenEvictionRunsMillis));

		} catch (IOException e) {
			log.info(e.getMessage(), e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				log.info(e.getMessage(), e);
			}
		}
	}

	/**
	 * �õ�����
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		if (bds == null)
			return null;
		return bds.getConnection();
	}
	/**
	 * �����豸��ţ���ѯ�豸�Ĵ��������ݣ�����ж����������������ѯ�����һ��
	 * @param v_equipment_name
	 * @return
	 */
	public static OrderBufferVo selectSendBufferTable(String v_equipment_name) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		OrderBufferVo content = null;
		try {
			conn = bds.getConnection();
			pstmt = conn.prepareStatement(
					"SELECT * FROM t_order_buffer WHERE v_equipment_name= ? AND i_send_flag<4 AND dtm_create>DATE_FORMAT(NOW(),'%Y-%m-%d') ORDER BY dtm_create ASC LIMIT 0,1");
			pstmt.setString(1, v_equipment_name);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				content = new OrderBufferVo();
				content.setI_id(Integer.valueOf(rs.getInt("i_id")));
				content.setI_send_flag(Integer.valueOf(rs.getInt("i_send_flag")));
				content.setV_equipment_name(rs.getString("v_equipment_name"));
				content.setV_order_content(rs.getString("v_order_content"));
			}
		} catch (SQLException e) {
			log.info(e.getMessage(), e);
		} finally {
			colseResource(conn,pstmt,null);
		}
		return content;
	}
	/**
	 * �رմ򿪵����ݿ���Դ
	 * @param conn
	 * @param pstmt
	 * @param rs
	 */
	public static void colseResource(Connection conn,PreparedStatement pstmt,ResultSet rs){
		
			try {
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				conn = null;
			}
			
			try {
				if(pstmt!=null){
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				pstmt = null;
			}
			
			try {
				if(rs!=null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				rs = null;
			}
	}
	
	/**
	 * ���·������ݵķ���״̬Ϊ�ѷ���
	 * @param id
	 * @return
	 */
	public static int updateSendBufferById(int id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int count = 0;
		try {
			conn = bds.getConnection();
			pstmt = conn.prepareStatement("UPDATE t_order_buffer SET i_send_flag=i_send_flag+1 WHERE i_id=?");
			pstmt.setInt(1, id);
			count = pstmt.executeUpdate();
		} catch (SQLException e) {
			log.info(e.getMessage(), e);
		} finally {
			colseResource(conn,pstmt,null);
		}
		return count;
	}
	/**
	 * ��ɾ�������data2���е����ݣ���������Ϊһ������
	 */
	public static void deleteAndInsertData2(EquipmentData ed){
		Connection conn = null;
		PreparedStatement pst = null;
		PreparedStatement pst2 = null;
		try {
			conn = bds.getConnection();
			conn.setAutoCommit(false);
			//ɾ������
			pst2 = conn.prepareStatement("delete from t_equipment_data2 where v_equipment_name= ?");
			pst2.setString(1, ed.getV_equipment_name());
			pst2.execute();
			//�������
			pst = conn.prepareStatement(
					"insert into t_equipment_data2(v_equipment_name,p001,p002,p003,p004,p005,p006,p007,p008,p009,p010,p011,p012,p013,p014,p015,p016,p017,p018,p019,p020,p021,p022,p023,p024,p025,p026,p027,p028,p029,p030) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, ed.getV_equipment_name());
			pst.setDouble(2, ed.getP001());
			pst.setDouble(3, ed.getP002());
			pst.setDouble(4, ed.getP003());
			pst.setDouble(5, ed.getP004());
			pst.setDouble(6, ed.getP005());
			pst.setDouble(7, ed.getP006());
			pst.setDouble(8, ed.getP007());
			pst.setDouble(9, ed.getP008());
			pst.setDouble(10, ed.getP009());
			pst.setDouble(11, ed.getP010());
			pst.setDouble(12, ed.getP011());// �缶������04���ټ���
			pst.setDouble(13, ed.getP012());
			pst.setDouble(14, ed.getP013());
			pst.setDouble(15, ed.getP014());
			pst.setDouble(16, ed.getP015());
			
			pst.setDouble(17, ed.getP016());
			pst.setDouble(18, ed.getP017());
			pst.setDouble(19, ed.getP018());
			pst.setDouble(20, ed.getP019());
			pst.setDouble(21, ed.getP020());
			pst.setDouble(22, ed.getP021());
			pst.setDouble(23, ed.getP022());
			pst.setDouble(24, ed.getP023());
			pst.setDouble(25, ed.getP024());
			pst.setDouble(26, ed.getP025());
			pst.setDouble(27, ed.getP026());
			pst.setDouble(28, ed.getP027());
			pst.setDouble(29, ed.getP028());
			pst.setDouble(30, ed.getP029());
			pst.setDouble(31, ed.getP030());
			//ִ�в���
			pst.execute();
			//�ύ
			conn.commit();
		} catch (SQLException e) {
			log.info(e.getMessage(), e);
			try {
				conn.rollback();
				conn.commit();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			colseResource(conn,pst,null);
			colseResource(null,pst2,null);
		}
	}

	/**
	 * �����豸������data��
	 * 
	 * @param ed
	 */
	public static void insertEquipmentData(EquipmentData ed) {
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = bds.getConnection();
			pst = conn.prepareStatement(
					"insert into t_equipment_data(v_equipment_name,p001,p002,p003,p004,p005,p006,p007,p008,p009,p010,p011,p012,p013,p014,p015,p016,p017,p018,p019,p020,p021,p022,p023,p024,p025,p026,p027,p028,p029,p030) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, ed.getV_equipment_name());
			pst.setDouble(2, ed.getP001());
			pst.setDouble(3, ed.getP002());
			pst.setDouble(4, ed.getP003());
			pst.setDouble(5, ed.getP004());
			pst.setDouble(6, ed.getP005());
			pst.setDouble(7, ed.getP006());
			pst.setDouble(8, ed.getP007());
			pst.setDouble(9, ed.getP008());
			pst.setDouble(10, ed.getP009());
			pst.setDouble(11, ed.getP010());
			pst.setDouble(12, ed.getP011());// �缶������04���ټ���
			pst.setDouble(13, ed.getP012());
			pst.setDouble(14, ed.getP013());
			pst.setDouble(15, ed.getP014());
			pst.setDouble(16, ed.getP015());

			pst.setDouble(17, ed.getP016());
			pst.setDouble(18, ed.getP017());
			pst.setDouble(19, ed.getP018());
			pst.setDouble(20, ed.getP019());
			pst.setDouble(21, ed.getP020());
			pst.setDouble(22, ed.getP021());
			pst.setDouble(23, ed.getP022());
			pst.setDouble(24, ed.getP023());
			pst.setDouble(25, ed.getP024());
			pst.setDouble(26, ed.getP025());
			pst.setDouble(27, ed.getP026());
			pst.setDouble(28, ed.getP027());
			pst.setDouble(29, ed.getP028());
			pst.setDouble(30, ed.getP029());
			pst.setDouble(31, ed.getP030());

			pst.execute();
		} catch (SQLException e) {
			log.info(e.getMessage(), e);
		} finally {
			colseResource(conn,pst,null);
		}
	}

	/**
	 * �ر����ӳ�
	 * 
	 * @throws SQLException
	 */
	public static void close() throws SQLException {
		bds.close();
	}

	// test
	public static void main(String a[]) {
		System.out.println(
				"SEND,DATAS,00000088,0,65,60,0.8,1,32.1,50.6,60.7,0,0,0,0,0,31.725957,112.641365,END".length());

	}
}
