package table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.zzk.DAO;

public class ServerSocketFrame extends JFrame {
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
			ta_info.append("�������׽����Ѿ������ɹ�\n"); // �����Ϣ
			while (true) { // ����׽���������״̬
				socket = server.accept(); // ʵ����Socket����
				counts++;
				ta_info.append("��" + counts + "���ͻ����ӳɹ�\n"); // �����Ϣ
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
					int index = -1;// �洢�˳��Ŀͻ�������ֵ
					try {
						while (true) { // ����׽���������״̬
							String line = reader.readLine();// ��ȡ�ͻ�����Ϣ

							try {
								index = Integer.parseInt(line);// ����˳��Ŀͻ�������ֵ
							} catch (Exception ex) {
								index = -1;
							}
							if (line != null) {
								String[] value = new String[3];// ��������

								if (line.startsWith("N")) {
									System.out.println("get name");
									value[0] = line.substring(1);// �������
									ta_info.append("user name is " + line.substring(1) + "\n");// ��ÿͻ�����Ϣ
								}
								if (line.startsWith("P")) {
									System.out.println("get pin");
									value[1] = line.substring(1);// �������
									ta_info.append("password is " + line.substring(1) + "\n");
								}
								if (line.startsWith("E")) {
									System.out.println("get email");
									value[2] = line.substring(1);// ���email
									ta_info.append("email is " + line.substring(1) + "\n");
								}
								try {
									
									Connection conn = DAO.getConn();// ������ݿ�����
									
									String sql = "insert into user (uName,pin,email) values(��,��,��)";// ����SQL���
									
									PreparedStatement ps = conn.prepareStatement(sql);// ����PreparedStatement���󣬲�����SQL���
									
									ps.setString(1, value[0]);// Ϊ��1��������ֵ
									ps.setString(2, value[1]);// Ϊ��2��������ֵ
									ps.setString(3, value[2]);// Ϊ��2��������ֵ
									
									int flag = ps.executeUpdate(); // ִ��SQL��䣬��ø��¼�¼��
									ps.close();// �ر�PreparedStatement����
									conn.close();// �ر�����
									if (flag > 0) {
										ta_info.append("���ɹ��ر��浽���ݿ��С�\n");
										writer.println("����ɹ���");// ��ͻ����������ɹ�����Ϣ
									} else {
										writer.println("����ʧ�ܡ�\n");// ��ͻ����������ɹ�����Ϣ
									}
								} catch (SQLException ee) {
									writer.println("����ʧ�ܡ�\n" + ee.getMessage());// ��ͻ����������ɹ�����Ϣ
								}
							}
						}
					} catch (Exception e) {
						if (index != -1) {
							vector.set(index, null);// ���˳��Ŀͻ����׽�������Ϊnull
							ta_info.append("��" + (index + 1) + "���ͻ����Ѿ��˳���\n"); // ����쳣��Ϣ
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

	}

	private void writeInfo(PrintWriter writer, String text) {
		writer.println(text);
	}

	public static void main(String[] args) { // ������
		ServerSocketFrame frame = new ServerSocketFrame(); // �����������
		frame.setVisible(true);// ��ʾ����
		frame.getServer(); // ���÷���
	}

	public ServerSocketFrame() {
		super();
		setTitle("�������˳���");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 379, 260);

		final JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		ta_info = new JTextArea();
		scrollPane.setViewportView(ta_info);

		final JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);

		final JLabel label = new JLabel();
		label.setText("���������͵���Ϣ��");
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
				ta_info.append("���������͵���Ϣ�ǣ�" + tf_send.getText() + "\n"); // ���ı�������Ϣ��ʾ���ı�����
				tf_send.setText(""); // ���ı������
			}
		});
		button.setText("��  ��");
		panel.add(button);

		final JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.NORTH);

		final JLabel label_1 = new JLabel();
		label_1.setForeground(new Color(0, 0, 255));
		label_1.setFont(new Font("", Font.BOLD, 22));
		label_1.setText("һ�Զ�ͨ�š����������˳���");
		panel_1.add(label_1);
	}
}
