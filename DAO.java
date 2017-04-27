package com.zzk;
import java.sql.*;
import javax.swing.JOptionPane;
public class DAO {
    private static DAO dao = new DAO(); // ����DAO��ľ�̬ʵ��
    /**
     * ���췽�����������ݿ�����
     */
    public DAO() {
        try {
            Class.forName("com.mysql.jdbc.Driver"); // �������ݿ�����
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "���ݿ���������ʧ�ܡ�\n"
                    + e.getMessage());
        }
    }
    
    /**
     * ������ݿ����ӵķ���
     * 
     * @return Connection
     */
    public static Connection getConn() {
        try {
            Connection conn = null; // �������ݿ�����
            String url = "jdbc:mysql://localhost:3306/iCourse?characterEncoding=utf8&useSSL=false"; // ���ݿ�db_picture.mdb��URL
            String username = "root"; // ���ݿ���û���
            String password = ""; // ���ݿ�����
            conn = DriverManager.getConnection(url, username, password); // ��������
            return conn; // ��������
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "���ݿ�����ʧ�ܡ�\n" + e.getMessage());
            return null;
        }
    }
}