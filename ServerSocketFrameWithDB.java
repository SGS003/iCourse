package Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Client.Course;

public class ServerSocketFrameWithDB extends JFrame {
	private JTextField tf_send;
	private JTextArea ta_info;
	private PrintWriter writer; // ����PrintWriter�����
	private ServerSocket server; // ����ServerSocket����
	private Socket socket; // ����Socket����socket
	private Vector<Socket> vector = new Vector<Socket>();// ���ڴ洢���ӵ��������Ŀͻ����׽��ֶ���
	private int counts = 0;// ���ڼ�¼���ӵĿͻ�����

	public void getServer() {
		try {
			server = new ServerSocket(1978); // ʵ����Socket����
			ta_info.append("Connction Success\n"); // �����Ϣ
			while (true) {
				socket = server.accept(); // ʵ����Socket����
				counts++;
				ta_info.append("Client" + counts + "connected successed" + "\n"); // �����Ϣ
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(String.valueOf(counts - 1));// ��ͻ��˷����׽�������
				vector.add(socket);// �洢�ͻ����׽��ֶ���
				new ServerThread(socket).start();// �����������߳���
			}
		} catch (Exception e) {
			e.printStackTrace(); // ����쳣��Ϣ
		}
	}

	class ServerThread extends Thread {
		Socket socket = null; // ����Socket����
		BufferedReader reader; // ����BufferedReader����

		public ServerThread(Socket socket) { // ���췽��
			this.socket = socket;
		}

		public void run() {
			try {
				if (socket != null) {
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // ʵ����BufferedReader����

					try {
						while (true) { // ����׽���������״̬
							String line = reader.readLine();// ��ȡ�ͻ�����Ϣ
							if (line != null) {

								if (line.equals("Signin")) {
									Signin();
								} else if (line.equals("Login")) {
									Login();
								} else if (line.equals("Edit")) {
									Edit();
								}
							}
						}
					} finally {
						try {
							if (reader != null) {
								reader.close();// �ر���
							}
							if (socket != null) {
								socket.close(); // �ر��׽���
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Connection conn = DAO.getConn();// ������ݿ�����
		// String sql = "select * from course where uID = ? ";
		// PreparedStatement ps = conn.prepareStatement(sql);
		// ps.setString(1, value[0]);// Ϊ��1��������ֵ
		// ResultSet rs = ps.executeQuery();

		private void Edit() {
			// TODO Auto-generated method stub
			String[] value = new String[3];// get Course name, get room, get
											// teacher's name
			try {
				value[0] = reader.readLine();
				value[1] = reader.readLine();
				value[2] = reader.readLine();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			int[] values = new int[3];// get userID , courseOrder , day
			try {
				values[0] = reader.read();
				values[1] = reader.read();
				values[2] = reader.read();
			} catch (IOException e) {
				e.printStackTrace();
			}

			ta_info.append("Course name is " + value[0] + "\n");// ��ÿͻ�����Ϣ
			ta_info.append("room is " + value[1] + "\n");
			ta_info.append("teacher is " + value[2] + "\n");
			ta_info.append("userID is " + values[0] + "\n");
			ta_info.append("course is " + values[1] + "\n");
			ta_info.append("day is " + values[2] + "\n");

			try {
				Connection conn = DAO.getConn();// ������ݿ�����
				System.out.println(value[0]);

				String sql3 = "select cName from course where uID = ? and courseOrder = ? and courseDay = ?";
				PreparedStatement ps = conn.prepareStatement(sql3);
				ps.setInt(1, values[0]);// Ϊ��1��������ֵ
				ps.setInt(2, values[1]);// Ϊ��2��������ֵ
				ps.setInt(3, values[2]);// Ϊ��3��������ֵ
				ResultSet rs = ps.executeQuery();
				int flag = 0; // ִ��SQL��䣬��ø��¼�¼��
				while (rs.next()) {
					if (rs.getString("cName") != null) {
						String sql = "update course set cName = ? where courseOrder = ? and courseDay = ?";// ����SQL
						// PreparedStatement ps = conn.prepareStatement(sql);
						// ����PreparedStatement���󣬲�����SQL���
						ps = conn.prepareStatement(sql);
						ps.setString(1, value[0]);// Ϊ��1��������ֵ
						ps.setInt(2, values[1]);// Ϊ��2��������ֵ
						ps.setInt(3, values[2]);// Ϊ��3��������ֵ

						flag = ps.executeUpdate(); // ִ��SQL��䣬��ø��¼�¼��

						String sql1 = "update course set room = ? where courseOrder = ? and courseDay = ?";
						ps = conn.prepareStatement(sql1);
						ps.setString(1, value[1]);// Ϊ��1��������ֵ
						ps.setInt(2, values[1]);// Ϊ��2��������ֵ
						ps.setInt(3, values[2]);// Ϊ��3��������ֵ

						flag = ps.executeUpdate(); // ִ��SQL��䣬��ø��¼�¼��

						String sql2 = "update course set teacher = ? where courseOrder = ? and courseDay = ?";
						ps = conn.prepareStatement(sql2);
						ps.setString(1, value[2]);// Ϊ��1��������ֵ
						ps.setInt(2, values[1]);// Ϊ��2��������ֵ
						ps.setInt(3, values[2]);// Ϊ��3��������ֵ

						flag = ps.executeUpdate(); // ִ��SQL��䣬��ø��¼�¼��
					} else {
						String sql = "INSERT INTO course VALUES ��?,?,?) WHERE courseOrder = ? and courseDay = ? ";
						ps = conn.prepareStatement(sql);
						ps.setString(1, value[0]);// Ϊ��1��������ֵ
						ps.setString(2, value[1]);// Ϊ��2��������ֵ
						ps.setString(3, value[2]);// Ϊ��3��������ֵ
						ps.setInt(4, values[1]);// Ϊ��2��������ֵ
						ps.setInt(5, values[2]);// Ϊ��3��������ֵ

						flag = ps.executeUpdate(); // ִ��SQL��䣬��ø��¼�¼��
					}
				}

				ps.close();// �ر�PreparedStatement����
				conn.close();// �ر�����
				if (flag > 0) {
					ta_info.append("and save into database sucessfully��\n");
					writer.println("save sucessfully��");// ��ͻ����������ɹ�����Ϣ
				} else {
					writer.println("fail to save��\n");// ��ͻ����������ɹ�����Ϣ
				}
			} catch (SQLException ee) {
				writer.println("fail to save��\n" + ee.getMessage());// ��ͻ����������ɹ�����Ϣ
			}
		}

		private void Login() {
			// TODO Auto-generated method stub
			String[] value = new String[2];// ���������Դ洢�ͻ��˽��յ���Ϣ
			try {
				value[0] = reader.readLine();
				value[1] = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ta_info.append("user name is " + value[0] + "\n");// ��ÿͻ�����Ϣ
			ta_info.append("password is " + value[1] + "\n");
			try {
				int uID = 0;
				Connection conn = DAO.getConn();// ������ݿ�����
				String sql = "select uID from user where uName = ? and pin = ?";// ����SQL
				PreparedStatement ps = conn.prepareStatement(sql);// ����PreparedStatement���󣬲�����SQL���
				ps.setString(1, value[0]);// Ϊ��1��������ֵ
				ps.setString(2, value[1]);// Ϊ��2��������ֵ
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					if (rs.getInt(uID) != 0) {
						uID = rs.getInt(uID);
					}
				}
				if (uID != 0)
					writer.println("success");
				else
					writer.println("fail");
				rs.close();
				ps.close();// �ر�PreparedStatement����
				conn.close();// �ر�����
			} catch (SQLException ee) {
				writer.println("fail to save��\n" + ee.getMessage());// ��ͻ����������ɹ�����Ϣ
			}
		}

		private void Signin() {
			String[] value = new String[3];// ���������Դ洢�ͻ��˽��յ���Ϣ
			try {
				value[0] = reader.readLine();
				value[1] = reader.readLine();
				value[2] = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ta_info.append("user name is " + value[0] + "\n");// ��ÿͻ�����Ϣ
			ta_info.append("password is " + value[1] + "\n");
			ta_info.append("email is " + value[2] + "\n");
			try {

				Connection conn = DAO.getConn();// ������ݿ�����
				System.out.println(value[0]);
				String sql = "insert into user (uName,pin,email) values(?,?,?)";// ����SQL
																				// PreparedStatement
																				// ps
																				// =
																				// conn.prepareStatement(sql);//
																				// ����PreparedStatement���󣬲�����SQL���
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, value[0]);// Ϊ��1��������ֵ
				ps.setString(2, value[1]);// Ϊ��2��������ֵ
				ps.setString(3, value[2]);// Ϊ��2��������ֵ

				int flag = ps.executeUpdate(); // ִ��SQL��䣬��ø��¼�¼��
				ps.close();// �ر�PreparedStatement����
				conn.close();// �ر�����
				if (flag > 0) {
					ta_info.append("and save into database sucessfully��\n");
					writer.println("save sucessfully��");// ��ͻ����������ɹ�����Ϣ
				} else {
					writer.println("fail to save��\n");// ��ͻ����������ɹ�����Ϣ
				}
			} catch (SQLException ee) {
				writer.println("fail to save��\n" + ee.getMessage());// ��ͻ����������ɹ�����Ϣ
			}
		}

	}

	private void writeInfo(PrintWriter writer, String text) {
		writer.println(text);
	}

	public static void main(String[] args) {
		ServerSocketFrameWithDB frame = new ServerSocketFrameWithDB();
		frame.setVisible(true);
		frame.getServer();
	}

	public ServerSocketFrameWithDB() {
		super();
		setTitle("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 379, 260);

		final JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		ta_info = new JTextArea();
		scrollPane.setViewportView(ta_info);

		final JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		final JLabel label = new JLabel();
		label.setText("Server��");
		panel.add(label);

		tf_send = new JTextField();
		tf_send.setPreferredSize(new Dimension(150, 25));
		panel.add(tf_send);

		final JButton button = new JButton();
		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				for (int i = 0; i < vector.size(); i++) {
					Socket socket = vector.get(i);// ������ӳɹ����׽��ֶ���
					PrintWriter writer;
					try {
						if (socket != null && !socket.isClosed()) {
							writer = new PrintWriter(socket.getOutputStream(), true);// �������������
							writeInfo(writer, tf_send.getText()); // ���ı�������Ϣд����
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				ta_info.append("Server��" + tf_send.getText() + "\n"); // ���ı�������Ϣ��ʾ���ı�����
				tf_send.setText(""); // ���ı������
			}
		});
		button.setText("Send");
		panel.add(button);

		final JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.NORTH);

		final JLabel label_1 = new JLabel();
		label_1.setForeground(Color.BLACK);
		label_1.setFont(new Font("Chalkboard", Font.BOLD, 22));
		label_1.setText("Server Side");
		panel_1.add(label_1);
	}
}
