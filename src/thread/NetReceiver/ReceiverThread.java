package thread.NetReceiver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import createFrame.CreateErrorFrame;

public class ReceiverThread extends Thread {

	private static String SPLIT = "|";
	protected Socket socket = null;
	protected DataInputStream inByte = null;
	protected DataOutputStream outByte = null;
	protected List<String> cmdList = null;

	public ReceiverThread(Socket socket) {

		this.setDaemon(true);

		try {

			inByte = new DataInputStream(socket.getInputStream());

			outByte = new DataOutputStream(socket.getOutputStream());

			this.socket = socket;

		} catch (IOException e) {

			CreateErrorFrame thread = new CreateErrorFrame();
			thread.setMessage("Lose host connection");
			thread.start();

			e.printStackTrace();
		}
	}

	/*
	 *���ַ�����"|"��Ϊ�ָ���ȡcmdList�б?
	 */
	public static List<String> splitToken(String fs) {

		StringTokenizer pt = new StringTokenizer(fs, SPLIT);

		List<String> l = new ArrayList<String>();

		while (pt.hasMoreTokens()) {

			l.add(pt.nextToken());
		}

		return l;
	}

	/*
	 * ��cmdList�и��Index��ֵ�ж��Ƿ���param��ȡ���ȷ���True������ȷ���False��
	 */
	public boolean equParam(int index, String param) {

		if (cmdList.get(index).equals(param)) return true;

		else return false;
	}

	/*
	 * ����cmdList��Index���ֵ��
	 */
	public String getParam(int index) {

		return cmdList.get(index);
	}

	/*
	 * ����������д��UTF�ַ�
	 */
	public void writeUTF(String Stream) throws IOException {

		outByte.writeUTF(Stream);

		System.out.println(Stream + "                    - " + this.getClass().getName());
	}

	/*
	 * ���������ж���UTF�ַ�
	 */
	public String readUTF() throws IOException {

		String recString = inByte.readUTF();

		System.out.println(recString + "                    - " + this.getClass().getName());

		return (recString);
	}

	protected void free() {

		socket = null;
		inByte = null;
		outByte = null;
		cmdList = null;
	}
}
